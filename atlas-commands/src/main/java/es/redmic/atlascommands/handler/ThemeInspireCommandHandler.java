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

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import es.redmic.atlascommands.aggregate.ThemeInspireAggregate;
import es.redmic.atlascommands.commands.themeinspire.CreateThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.DeleteThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.UpdateThemeInspireCommand;
import es.redmic.atlascommands.statestore.ThemeInspireStateStore;
import es.redmic.atlascommands.streams.ThemeInspireEventStreams;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventFactory;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.create.ThemeInspireCreatedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.CheckDeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.brokerlib.alert.AlertService;
import es.redmic.commandslib.commands.CommandHandler;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.common.StreamConfig.Builder;
import es.redmic.exception.factory.ExceptionFactory;
import es.redmic.restlib.config.UserService;

@Component
@KafkaListener(topics = "${broker.topic.theme-inspire}")
public class ThemeInspireCommandHandler extends CommandHandler {

	@Value("${spring.kafka.properties.schema.registry.url}")
	protected String schemaRegistry;

	@Value("${spring.kafka.bootstrap-servers}")
	protected String bootstrapServers;

	@Value("${broker.topic.theme-inspire}")
	private String themeInspireTopic;

	@Value("${broker.topic.theme.inspire.updated}")
	private String themeInspireUpdatedTopic;

	@Value("${broker.state.store.themeinspire.dir}")
	private String stateStoreThemeInspireDir;

	@Value("${broker.state.store.themeinspire.id}")
	private String themeInspireIdConfig;

	@Value("${broker.stream.events.themeinspire.id}")
	private String themeInspireEventsStreamId;

	@Value("${stream.windows.time.ms}")
	private Long streamWindowsTime;

	private ThemeInspireStateStore themeInspireStateStore;

	@Autowired
	UserService userService;

	@Autowired
	AlertService alertService;

	public ThemeInspireCommandHandler() {

	}

	@PostConstruct
	private void setUp() {

		// @formatter:off
		
		Builder config = StreamConfig.Builder
				.bootstrapServers(bootstrapServers)
				.schemaRegistry(schemaRegistry)
				.stateStoreDir(stateStoreThemeInspireDir)
				.topic(themeInspireTopic);
		
		themeInspireStateStore = new ThemeInspireStateStore(
				config
					.serviceId(themeInspireIdConfig)
					.build(), alertService);

		new ThemeInspireEventStreams(
				config
					.serviceId(themeInspireEventsStreamId)
					.windowsTime(streamWindowsTime)
					.build(), alertService);
		
		// @formatter:on
	}

	public ThemeInspireDTO save(CreateThemeInspireCommand cmd) {

		ThemeInspireAggregate agg = new ThemeInspireAggregate(themeInspireStateStore);

		// Se procesa el comando, obteniendo el evento generado
		logger.debug("Procesando CreateThemeInspireCommand");

		CreateThemeInspireEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se debe aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Se aplica el evento
		agg.apply(event);

		logger.debug("Aplicado evento: " + event.getType());

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<ThemeInspireDTO> completableFuture = getCompletableFeature(event.getSessionId(),
				agg.getThemeInspire());

		// Emite evento para enviar a kafka
		publishToKafka(event, themeInspireTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public ThemeInspireDTO update(String id, UpdateThemeInspireCommand cmd) {

		ThemeInspireAggregate agg = new ThemeInspireAggregate(themeInspireStateStore);

		// Se procesa el comando, obteniendo el evento generado
		UpdateThemeInspireEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<ThemeInspireDTO> completableFuture = getCompletableFeature(event.getSessionId(),
				agg.getThemeInspire());

		// Emite evento para enviar a kafka
		publishToKafka(event, themeInspireTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public ThemeInspireDTO update(String id, DeleteThemeInspireCommand cmd) {

		ThemeInspireAggregate agg = new ThemeInspireAggregate(themeInspireStateStore);
		agg.setAggregateId(id);

		// Se procesa el comando, obteniendo el evento generado
		CheckDeleteThemeInspireEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<ThemeInspireDTO> completableFuture = getCompletableFeature(event.getSessionId(),
				agg.getThemeInspire());

		// Emite evento para enviar a kafka
		publishToKafka(event, themeInspireTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	@KafkaHandler
	private void listen(ThemeInspireCreatedEvent event) {

		logger.debug("ThemeInspire creado " + event.getAggregateId());

		// El evento Creado se envía desde el stream

		resolveCommand(event.getSessionId());
	}

	@KafkaHandler
	private void listen(ThemeInspireUpdatedEvent event) {

		logger.debug("ThemeInspire modificado " + event.getAggregateId());

		// Envía los editados satisfactoriamente para tenerlos en cuenta en el
		// postupdate
		publishToKafka(event, themeInspireUpdatedTopic);

		// El evento Modificado se envía desde el stream

		resolveCommand(event.getSessionId());
	}

	@KafkaHandler
	private void listen(DeleteThemeInspireCheckedEvent event) {

		publishToKafka(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.DELETE), themeInspireTopic);
	}

	@KafkaHandler
	private void listen(DeleteThemeInspireConfirmedEvent event) {

		publishToKafka(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.DELETED), themeInspireTopic);
	}

	@KafkaHandler
	private void listen(ThemeInspireDeletedEvent event) {

		logger.debug("ThemeInspire eliminado " + event.getAggregateId());

		resolveCommand(event.getSessionId());
	}

	@KafkaHandler
	private void listen(CreateThemeInspireFailedEvent event) {

		publishToKafka(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.CREATE_CANCELLED,
				event.getExceptionType(), event.getArguments()), themeInspireTopic);
	}

	@KafkaHandler
	private void listen(CreateThemeInspireCancelledEvent event) {

		logger.debug("Error creando ThemeInspire " + event.getAggregateId());

		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaHandler
	private void listen(UpdateThemeInspireCancelledEvent event) {

		logger.debug("Error modificando ThemeInspire " + event.getAggregateId());

		// El evento Cancelled se envía desde el stream

		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaHandler
	private void listen(DeleteThemeInspireCheckFailedEvent event) {

		publishToKafka(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.DELETE_CANCELLED,
				event.getExceptionType(), event.getArguments()), themeInspireTopic);
	}

	@KafkaHandler
	private void listen(DeleteThemeInspireCancelledEvent event) {

		logger.debug("Error eliminando ThemeInspire " + event.getAggregateId());

		// El evento Cancelled se envía desde el stream

		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}
}
