package es.redmic.atlascommands.handler;

/*-
 * #%L
 * Atlas-management
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import es.redmic.atlascommands.aggregate.CategoryAggregate;
import es.redmic.atlascommands.commands.category.CreateCategoryCommand;
import es.redmic.atlascommands.commands.category.DeleteCategoryCommand;
import es.redmic.atlascommands.commands.category.UpdateCategoryCommand;
import es.redmic.atlascommands.statestore.CategoryStateStore;
import es.redmic.atlascommands.streams.CategoryEventStreams;
import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.CategoryEventFactory;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.create.CategoryCreatedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryFailedEvent;
import es.redmic.atlaslib.events.category.delete.CategoryDeletedEvent;
import es.redmic.atlaslib.events.category.delete.CheckDeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckFailedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.CategoryUpdatedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.brokerlib.alert.AlertService;
import es.redmic.commandslib.commands.CommandHandler;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.common.StreamConfig.Builder;
import es.redmic.exception.factory.ExceptionFactory;
import es.redmic.restlib.config.UserService;

@Component
@KafkaListener(topics = "${broker.topic.category}")
public class CategoryCommandHandler extends CommandHandler {

	@Value("${spring.kafka.properties.schema.registry.url}")
	protected String schemaRegistry;

	@Value("${spring.kafka.bootstrap-servers}")
	protected String bootstrapServers;

	@Value("${broker.topic.category}")
	private String categoryTopic;

	@Value("${broker.state.store.category.dir}")
	private String stateStoreCategoryDir;

	@Value("${broker.state.store.category.id}")
	private String categoryIdConfig;

	@Value("${broker.stream.events.category.id}")
	private String categoryEventsStreamId;

	@Value("${stream.windows.time.ms}")
	private Long streamWindowsTime;

	private CategoryStateStore categoryStateStore;

	@Autowired
	UserService userService;

	@Autowired
	AlertService alertService;

	public CategoryCommandHandler() {

	}

	@PostConstruct
	private void setUp() {

		// @formatter:off
		
		Builder config = StreamConfig.Builder
				.bootstrapServers(bootstrapServers)
				.schemaRegistry(schemaRegistry)
				.stateStoreDir(stateStoreCategoryDir)
				.topic(categoryTopic);
		
		categoryStateStore = new CategoryStateStore(
				config
					.serviceId(categoryIdConfig)
					.build(), alertService);

		new CategoryEventStreams(
				config
					.serviceId(categoryEventsStreamId)
					.windowsTime(streamWindowsTime)
					.build(), alertService);
		
		// @formatter:on
	}

	public CategoryDTO save(CreateCategoryCommand cmd) {

		CategoryAggregate agg = new CategoryAggregate(categoryStateStore, userService);

		CreateCategoryEvent event;

		try {
			event = agg.process(cmd);
		} catch (ItemLockedException e) {

			unlockStatus(agg, cmd.getCategory().getId(), categoryTopic);
			throw e;
		}

		// Si no se genera evento significa que no se debe aplicar
		if (event == null)
			return null;

		// Se aplica el evento
		agg.apply(event);

		return sendEventAndWaitResult(agg, event, categoryTopic);
	}

	public CategoryDTO update(String id, UpdateCategoryCommand cmd) {

		CategoryAggregate agg = new CategoryAggregate(categoryStateStore, userService);

		// Se procesa el comando, obteniendo el evento generado
		UpdateCategoryEvent event;

		try {
			event = agg.process(cmd);
		} catch (ItemLockedException e) {

			unlockStatus(agg, cmd.getCategory().getId(), categoryTopic);
			throw e;
		}

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		return sendEventAndWaitResult(agg, event, categoryTopic);
	}

	public CategoryDTO update(String id, DeleteCategoryCommand cmd) {

		CategoryAggregate agg = new CategoryAggregate(categoryStateStore, userService);
		agg.setAggregateId(id);

		// Se procesa el comando, obteniendo el evento generado
		CheckDeleteCategoryEvent event;

		try {
			event = agg.process(cmd);
		} catch (ItemLockedException e) {

			unlockStatus(agg, cmd.getCategoryId(), categoryTopic);
			throw e;
		}

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		return sendEventAndWaitResult(agg, event, categoryTopic);
	}

	@KafkaHandler
	private void listen(CategoryCreatedEvent event) {

		// El evento Creado se envía desde el stream
		resolveCommand(event.getSessionId(), event.getCategory());
	}

	@KafkaHandler
	private void listen(CategoryUpdatedEvent event) {

		// El evento Modificado se envía desde el stream
		resolveCommand(event.getSessionId(), event.getCategory());
	}

	@KafkaHandler
	private void listen(DeleteCategoryCheckedEvent event) {

		publishToKafka(CategoryEventFactory.getEvent(event, CategoryEventTypes.DELETE), categoryTopic);
	}

	@KafkaHandler
	private void listen(DeleteCategoryConfirmedEvent event) {

		publishToKafka(CategoryEventFactory.getEvent(event, CategoryEventTypes.DELETED), categoryTopic);
	}

	@KafkaHandler
	private void listen(CategoryDeletedEvent event) {

		resolveCommand(event.getSessionId());
	}

	@KafkaHandler
	private void listen(CreateCategoryFailedEvent event) {

		publishToKafka(CategoryEventFactory.getEvent(event, CategoryEventTypes.CREATE_CANCELLED,
				event.getExceptionType(), event.getArguments()), categoryTopic);
	}

	@KafkaHandler
	private void listen(CreateCategoryCancelledEvent event) {

		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaHandler
	private void listen(UpdateCategoryCancelledEvent event) {

		// El evento Cancelled se envía desde el stream
		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaHandler
	private void listen(DeleteCategoryCheckFailedEvent event) {

		publishToKafka(CategoryEventFactory.getEvent(event, CategoryEventTypes.DELETE_CANCELLED,
				event.getExceptionType(), event.getArguments()), categoryTopic);
	}

	@KafkaHandler
	private void listen(DeleteCategoryCancelledEvent event) {

		// El evento Cancelled se envía desde el stream
		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}
}
