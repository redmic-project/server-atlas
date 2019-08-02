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

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import es.redmic.atlascommands.aggregate.LayerAggregate;
import es.redmic.atlascommands.commands.layer.CreateLayerCommand;
import es.redmic.atlascommands.commands.layer.DeleteLayerCommand;
import es.redmic.atlascommands.commands.layer.RefreshLayerCommand;
import es.redmic.atlascommands.commands.layer.UpdateLayerCommand;
import es.redmic.atlascommands.statestore.LayerStateStore;
import es.redmic.atlascommands.streams.LayerEventStreams;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.layer.LayerEventFactory;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.common.LayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEnrichedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerFailedEvent;
import es.redmic.atlaslib.events.layer.create.LayerCreatedEvent;
import es.redmic.atlaslib.events.layer.delete.CheckDeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckFailedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.refresh.LayerRefreshedEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEnrichedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.commands.CommandHandler;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.common.StreamConfig.Builder;
import es.redmic.exception.factory.ExceptionFactory;
import es.redmic.restlib.config.UserService;

@Component
@KafkaListener(topics = "${broker.topic.layer}")
public class LayerCommandHandler extends CommandHandler {

	@Value("${spring.kafka.properties.schema.registry.url}")
	protected String schemaRegistry;

	@Value("${spring.kafka.bootstrap-servers}")
	protected String bootstrapServers;

	@Value("${broker.topic.layer}")
	private String layerTopic;

	@Value("${broker.topic.theme-inspire}")
	private String themeInspireTopic;

	@Value("${broker.state.store.layer.dir}")
	private String stateStoreLayerDir;

	@Value("${broker.state.store.layer.id}")
	private String layerIdConfig;

	@Value("${broker.stream.events.layer.id}")
	private String layerEventsStreamId;

	@Value("${stream.windows.time.ms}")
	private Long streamWindowsTime;

	private LayerStateStore layerStateStore;

	@Autowired
	UserService userService;

	@Autowired
	AlertService alertService;

	public LayerCommandHandler() {

	}

	@PostConstruct
	private void setUp() {

		// @formatter:off
		
		Builder config = StreamConfig.Builder
				.bootstrapServers(bootstrapServers)
				.schemaRegistry(schemaRegistry)
				.stateStoreDir(stateStoreLayerDir)
				.topic(layerTopic);
		
		layerStateStore = new LayerStateStore(
				config
					.serviceId(layerIdConfig)
					.build(), alertService);

		new LayerEventStreams(
				config
					.serviceId(layerEventsStreamId)
					.windowsTime(streamWindowsTime)
					.build(), themeInspireTopic, alertService);
		
		// @formatter:on
	}

	public LayerDTO save(CreateLayerCommand cmd) {

		LayerAggregate agg = new LayerAggregate(layerStateStore, userService);

		LayerEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se debe aplicar
		if (event == null)
			return null;

		// Se aplica el evento
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<LayerDTO> completableFuture = getCompletableFeature(event.getSessionId());

		// Emite evento para enviar a kafka
		publishToKafka(event, layerTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public LayerDTO update(String id, UpdateLayerCommand cmd) {

		LayerAggregate agg = new LayerAggregate(layerStateStore, userService);

		// Se procesa el comando, obteniendo el evento generado
		LayerEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<LayerDTO> completableFuture = getCompletableFeature(event.getSessionId());

		// Emite evento para enviar a kafka
		publishToKafka(event, layerTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public LayerDTO update(String id, DeleteLayerCommand cmd) {

		LayerAggregate agg = new LayerAggregate(layerStateStore, userService);
		agg.setAggregateId(id);

		// Se procesa el comando, obteniendo el evento generado
		CheckDeleteLayerEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<LayerDTO> completableFuture = getCompletableFeature(event.getSessionId());

		// Emite evento para enviar a kafka
		publishToKafka(event, layerTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public LayerDTO refresh(RefreshLayerCommand cmd) {

		LayerAggregate agg = new LayerAggregate(layerStateStore, userService);

		// Se procesa el comando, obteniendo el evento generado
		RefreshLayerEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<LayerDTO> completableFuture = getCompletableFeature(event.getSessionId());

		// Emite evento para enviar a kafka
		publishToKafka(event, layerTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	@KafkaHandler
	private void listen(CreateLayerEnrichedEvent event) {

		publishToKafka(LayerEventFactory.getEvent(event, LayerEventTypes.CREATE, event.getLayer()), layerTopic);
	}

	@KafkaHandler
	private void listen(LayerCreatedEvent event) {

		// El evento Creado se envía desde el stream
		resolveCommand(event.getSessionId(), event.getLayer());
	}

	@KafkaHandler
	private void listen(UpdateLayerEnrichedEvent event) {

		publishToKafka(LayerEventFactory.getEvent(event, LayerEventTypes.UPDATE, event.getLayer()), layerTopic);
	}

	@KafkaHandler
	private void listen(LayerUpdatedEvent event) {

		// El evento Modificado se envía desde el stream
		resolveCommand(event.getSessionId(), event.getLayer());
	}

	@KafkaHandler
	private void listen(LayerRefreshedEvent event) {

		// El evento Refrescado se envía desde el stream
		resolveCommand(event.getSessionId(), event.getLayer());
	}

	@KafkaHandler
	private void listen(DeleteLayerCheckedEvent event) {

		publishToKafka(LayerEventFactory.getEvent(event, LayerEventTypes.DELETE), layerTopic);
	}

	@KafkaHandler
	private void listen(DeleteLayerConfirmedEvent event) {

		publishToKafka(LayerEventFactory.getEvent(event, LayerEventTypes.DELETED), layerTopic);
	}

	@KafkaHandler
	private void listen(LayerDeletedEvent event) {

		resolveCommand(event.getSessionId());
	}

	@KafkaHandler
	private void listen(CreateLayerFailedEvent event) {

		publishToKafka(LayerEventFactory.getEvent(event, LayerEventTypes.CREATE_CANCELLED, event.getExceptionType(),
				event.getArguments()), layerTopic);
	}

	@KafkaHandler
	private void listen(CreateLayerCancelledEvent event) {

		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaHandler
	private void listen(UpdateLayerCancelledEvent event) {

		// El evento Cancelled se envía desde el stream
		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaHandler
	private void listen(RefreshLayerCancelledEvent event) {

		// El evento Cancelled se envía desde el stream
		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaHandler
	private void listen(DeleteLayerCheckFailedEvent event) {

		publishToKafka(LayerEventFactory.getEvent(event, LayerEventTypes.DELETE_CANCELLED, event.getExceptionType(),
				event.getArguments()), layerTopic);
	}

	@KafkaHandler
	private void listen(DeleteLayerCancelledEvent event) {

		// El evento Cancelled se envía desde el stream
		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaListener(topics = "${broker.topic.theme.inspire.updated}")
	private void listen(ThemeInspireUpdatedEvent event) {

		KeyValueIterator<String, Event> iteratble = layerStateStore.getAll();
		while (iteratble.hasNext()) {
			final KeyValue<String, Event> next = iteratble.next();

			Event layerEvent = next.value;

			if (LayerEventTypes.isSnapshot(layerEvent.getType())) {

				ThemeInspireDTO themeInspire = ((LayerEvent) layerEvent).getLayer().getThemeInspire();

				if (themeInspire.getId().equals(event.getThemeInspire().getId())
						&& !themeInspire.equals(event.getThemeInspire())) {

					publishToKafka(LayerEventFactory.getEvent(layerEvent, event, LayerEventTypes.UPDATE_THEMEINSPIRE),
							layerTopic);
				}
			}
		}
		iteratble.close();
	}
}
