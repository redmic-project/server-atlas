package es.redmic.atlascommands.streams;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.kstream.GlobalKTable;

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

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.layer.LayerEventFactory;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.common.LayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEnrichedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEnrichedEvent;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireEvent;
import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.common.EventTypes;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.streams.EventSourcingStreams;
import es.redmic.exception.common.ExceptionType;

public class LayerEventStreams extends EventSourcingStreams {

	private String themeInspireTopic;

	private GlobalKTable<String, Event> themeInspire;

	public LayerEventStreams(StreamConfig config, String themeInspireTopic, AlertService alertService) {
		super(config, alertService);
		this.themeInspireTopic = themeInspireTopic + snapshotTopicSuffix;
		init();
	}

	/**
	 * @see es.redmic.commandslib.streaming.streams.EventSourcingStreams#
	 *      createExtraStreams()
	 */

	@Override
	protected void createExtraStreams() {

		themeInspire = builder.globalTable(themeInspireTopic);
	}

	/**
	 * Reenvía eventos finales a topic de snapshot
	 */
	@Override
	protected void forwardSnapshotEvents(KStream<String, Event> events) {

		events.filter((id, event) -> (LayerEventTypes.isSnapshot(event.getType()))).to(snapshotTopic);
	}

	/**
	 * Función que a partir de los eventos de tipo CreateEnrich y globalKTable de
	 * las relaciones, enriquece el item antes de mandarlo a crear
	 * 
	 */

	@Override
	protected void processEnrichCreateSteam(KStream<String, Event> events) {

		KStream<String, Event> enrichCreateEvents = events
				.filter((id,
						event) -> (EventTypes.ENRICH_CREATE.equals(event.getType())
								&& getThemeInspireIdFromLayer(event) != null))
				.selectKey((k, v) -> getThemeInspireIdFromLayer(v));

		enrichCreateEvents.leftJoin(themeInspire, (k, v) -> k,
				(enrichCreateEvent, vesselTypeEvent) -> getEnrichCreateResultEvent(enrichCreateEvent, vesselTypeEvent))
				.selectKey((k, v) -> v.getAggregateId()).to(topic);
	}

	private Event getEnrichCreateResultEvent(Event enrichCreateEvents, Event themeInspireEvent) {

		if (themeInspireEvent != null && !themeInspireEvent.getType().equals(ThemeInspireEventTypes.DELETED)) {

			CreateLayerEnrichedEvent event = (CreateLayerEnrichedEvent) LayerEventFactory.getEvent(enrichCreateEvents,
					LayerEventTypes.CREATE_ENRICHED, ((LayerEvent) enrichCreateEvents).getLayer());

			((LayerEvent) event).getLayer().setThemeInspire(((ThemeInspireEvent) themeInspireEvent).getThemeInspire());

			return event;
		} else {

			String themeInspireId = getThemeInspireIdFromLayer(enrichCreateEvents);

			logger.warn("Intentando enriquecer " + enrichCreateEvents.getAggregateId()
					+ " con un elemento que no existe: themeInspire.id = " + themeInspireId);

			Map<String, String> arguments = new HashMap<>();
			arguments.put("themeInspire.id", themeInspireId);

			return LayerEventFactory.getEvent(enrichCreateEvents, LayerEventTypes.CREATE_FAILED,
					ExceptionType.ITEM_NOT_FOUND.toString(), arguments);
		}
	}

	/**
	 * Función que apartir del evento de confirmación de la vista y del evento
	 * create (petición de creación), si todo es correcto, genera evento created
	 */

	@Override
	protected Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(LayerEventTypes.CREATE);

		assert confirmedEvent.getType().equals(LayerEventTypes.CREATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		LayerDTO layer = ((LayerEvent) requestEvent).getLayer();

		return LayerEventFactory.getEvent(confirmedEvent, LayerEventTypes.CREATED, layer);
	}

	/*
	 * Función que a partir de los eventos de tipo UpdateEnrich y globalKTable de
	 * las relaciones, enriquece el item antes de mandarlo a modificar
	 * 
	 */

	@Override
	protected void processEnrichUpdateSteam(KStream<String, Event> events) {

		KStream<String, Event> enrichUpdateEvents = events
				.filter((id, event) -> (EventTypes.ENRICH_UPDATE.equals(event.getType())))
				.selectKey((k, v) -> getThemeInspireIdFromLayer(v));

		enrichUpdateEvents
				.leftJoin(themeInspire, (k, v) -> k, (enrichUpdateEvent,
						themeInspireEvent) -> getEnrichUpdateResultEvent(enrichUpdateEvent, themeInspireEvent))
				.selectKey((k, v) -> v.getAggregateId()).to(topic);
	}

	private Event getEnrichUpdateResultEvent(Event enrichUpdateEvents, Event themeInspireEvent) {

		UpdateLayerEnrichedEvent event = (UpdateLayerEnrichedEvent) LayerEventFactory.getEvent(enrichUpdateEvents,
				LayerEventTypes.UPDATE_ENRICHED, ((LayerEvent) enrichUpdateEvents).getLayer());

		if (themeInspireEvent != null && !themeInspireEvent.getType().equals(ThemeInspireEventTypes.DELETED)) {
			((LayerEvent) event).getLayer().setThemeInspire(((ThemeInspireEvent) themeInspireEvent).getThemeInspire());
		} else {

			String themeInspireId = getThemeInspireIdFromLayer(enrichUpdateEvents);

			logger.warn(
					"Intentando enriquecer " + enrichUpdateEvents.getAggregateId() + " con un elemento que no existe");

			Map<String, String> arguments = new HashMap<>();
			arguments.put("themeInspire.id", themeInspireId);

			return LayerEventFactory.getEvent(enrichUpdateEvents, LayerEventTypes.UPDATE_FAILED,
					ExceptionType.ITEM_NOT_FOUND.toString(), arguments);
		}

		return event;
	}

	/**
	 * Función que apartir del evento de confirmación de la vista y del evento
	 * update (petición de modificación), si todo es correcto, genera evento updated
	 */

	@Override
	protected Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(LayerEventTypes.UPDATE);

		assert confirmedEvent.getType().equals(LayerEventTypes.UPDATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		LayerDTO layer = ((LayerEvent) requestEvent).getLayer();

		return LayerEventFactory.getEvent(requestEvent, LayerEventTypes.UPDATED, layer);
	}

	/**
	 * Como en este caso no existen referencias, directamente se transforma el
	 * evento CHECK en CHECKED
	 */

	@Override
	protected void processDeleteStream(KStream<String, Event> events) {

		// Stream filtrado por eventos de borrado
		KStream<String, Event> deleteEvents = events
				.filter((id, event) -> (LayerEventTypes.CHECK_DELETE.equals(event.getType())));

		deleteEvents
				.flatMapValues(
						event -> Arrays.asList(LayerEventFactory.getEvent(event, LayerEventTypes.DELETE_CHECKED)))
				.to(topic);
	}

	/**
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento UpdateCancelled
	 */

	@Override
	protected Event getUpdateCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert failedEvent.getType().equals(LayerEventTypes.UPDATE_FAILED);

		assert LayerEventTypes.isSnapshot(lastSuccessEvent.getType());

		LayerDTO layer = ((LayerEvent) lastSuccessEvent).getLayer();

		EventError eventError = (EventError) failedEvent;

		return LayerEventFactory.getEvent(failedEvent, LayerEventTypes.UPDATE_CANCELLED, layer,
				eventError.getExceptionType(), eventError.getArguments());
	}

	/**
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento DeleteFailed
	 */

	@Override
	protected Event getDeleteCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert failedEvent.getType().equals(LayerEventTypes.DELETE_FAILED);

		assert LayerEventTypes.isSnapshot(lastSuccessEvent.getType());

		LayerDTO layer = ((LayerEvent) lastSuccessEvent).getLayer();

		EventError eventError = (EventError) failedEvent;

		return LayerEventFactory.getEvent(failedEvent, LayerEventTypes.DELETE_CANCELLED, layer,
				eventError.getExceptionType(), eventError.getArguments());
	}

	@Override
	protected void processPartialUpdatedStream(KStream<String, Event> layerEvents,
			KStream<String, Event> updateConfirmedEvents) {
		// En este caso no hay modificaciones parciales
	}

	@Override
	protected void processExtraStreams(KStream<String, Event> events, KStream<String, Event> snapshotEvents) {

		processRefreshSuccessStream(events);

		processRefreshFailedStream(events, snapshotEvents);
	}

	protected void processRefreshSuccessStream(KStream<String, Event> events) {

		// Stream filtrado por eventos de confirmación al refrescar
		KStream<String, Event> refreshConfirmedEvents = events
				.filter((id, event) -> (LayerEventTypes.REFRESH_CONFIRMED.equals(event.getType())));

		// Table filtrado por eventos de petición de refrescar (Siempre el último
		// evento)
		KTable<String, Event> refreshRequestEvents = events
				.filter((id, event) -> (LayerEventTypes.REFRESH.equals(event.getType()))).groupByKey()
				.reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de éxito
		refreshConfirmedEvents
				.join(refreshRequestEvents,
						(confirmedEvent, requestEvent) -> geRefreshedEvent(confirmedEvent, requestEvent))
				.filter((k, v) -> (v != null)).to(topic);
	}

	/**
	 * Función que apartir del evento de confirmación de la vista y del evento
	 * refresh (petición de modificación), si todo es correcto, genera evento
	 * updated
	 */

	protected Event geRefreshedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(LayerEventTypes.REFRESH);

		assert confirmedEvent.getType().equals(LayerEventTypes.REFRESH_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		// Se obtiene la capa de la vista que es la que contiene todos los datos
		LayerDTO layer = ((LayerEvent) confirmedEvent).getLayer();

		return LayerEventFactory.getEvent(requestEvent, LayerEventTypes.REFRESHED, layer);
	}

	/**
	 * Función que a partir del último evento correcto y el evento fallido al
	 * refrescar, envía evento de cancelación
	 */

	protected void processRefreshFailedStream(KStream<String, Event> events, KStream<String, Event> successEvents) {

		// Stream filtrado por eventos de fallo al modificar
		KStream<String, Event> failedEvents = events
				.filter((id, event) -> (LayerEventTypes.REFRESH_FAILED.equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		failedEvents
				.join(successEventsTable,
						(failedEvent, lastSuccessEvent) -> getRefreshCancelledEvent(failedEvent, lastSuccessEvent))
				.to(topic);
	}

	/**
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento RefreshCancelled
	 */

	protected Event getRefreshCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert failedEvent.getType().equals(LayerEventTypes.REFRESH_FAILED);

		assert LayerEventTypes.isSnapshot(lastSuccessEvent.getType());

		LayerDTO layer = ((LayerEvent) lastSuccessEvent).getLayer();

		EventError eventError = (EventError) failedEvent;

		return LayerEventFactory.getEvent(failedEvent, LayerEventTypes.REFRESH_CANCELLED, layer,
				eventError.getExceptionType(), eventError.getArguments());
	}

	private String getThemeInspireIdFromLayer(Event evt) {

		ThemeInspireDTO themeInspireDTO = ((LayerEvent) evt).getLayer().getThemeInspire();

		return themeInspireDTO != null ? themeInspireDTO.getId() : null;
	}
}
