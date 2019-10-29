package es.redmic.atlascommands.streams;

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

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventFactory;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireEvent;
import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.streams.EventSourcingStreams;

public class ThemeInspireEventStreams extends EventSourcingStreams {

	public ThemeInspireEventStreams(StreamConfig config, AlertService alertService) {
		super(config, alertService);
		init();
	}

	/**
	 * 
	 * @see es.redmic.commandslib.streaming.streams.EventSourcingStreams#
	 *      createExtraStreams()
	 */

	@Override
	protected void createExtraStreams() {
	}

	/**
	 * Reenvía eventos finales a topic de snapshot
	 */
	@Override
	protected void forwardSnapshotEvents(KStream<String, Event> events) {

		events.filter((id, event) -> (ThemeInspireEventTypes.isSnapshot(event.getType()))).to(snapshotTopic);
	}

	/**
	 * Función que apartir del evento de confirmación de la vista y del evento
	 * create (petición de creación), si todo es correcto, genera evento created
	 */

	@Override
	protected Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(ThemeInspireEventTypes.CREATE);

		assert confirmedEvent.getType().equals(ThemeInspireEventTypes.CREATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		ThemeInspireDTO themeInspire = ((ThemeInspireEvent) requestEvent).getThemeInspire();

		return ThemeInspireEventFactory.getEvent(confirmedEvent, ThemeInspireEventTypes.CREATED, themeInspire);
	}

	/**
	 * Función que apartir del evento de confirmación de la vista y del evento
	 * update (petición de modificación), si todo es correcto, genera evento updated
	 */

	@Override
	protected Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(ThemeInspireEventTypes.UPDATE);

		assert confirmedEvent.getType().equals(ThemeInspireEventTypes.UPDATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		ThemeInspireDTO themeInspire = ((ThemeInspireEvent) requestEvent).getThemeInspire();

		return ThemeInspireEventFactory.getEvent(requestEvent, ThemeInspireEventTypes.UPDATED, themeInspire);
	}

	/**
	 * En este caso el borrado se compueba en el handler de layer
	 */

	@Override
	protected void processDeleteStream(KStream<String, Event> events) {
	}

	/**
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento UpdateCancelled
	 */

	@Override
	protected Event getUpdateCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert failedEvent.getType().equals(ThemeInspireEventTypes.UPDATE_FAILED);

		assert ThemeInspireEventTypes.isSnapshot(lastSuccessEvent.getType());

		ThemeInspireDTO themeInspire = ((ThemeInspireEvent) lastSuccessEvent).getThemeInspire();

		EventError eventError = (EventError) failedEvent;

		return ThemeInspireEventFactory.getEvent(failedEvent, ThemeInspireEventTypes.UPDATE_CANCELLED, themeInspire,
				eventError.getExceptionType(), eventError.getArguments());
	}

	/**
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento DeleteFailed
	 */

	@Override
	protected Event getDeleteCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert failedEvent.getType().equals(ThemeInspireEventTypes.DELETE_FAILED);

		assert ThemeInspireEventTypes.isSnapshot(lastSuccessEvent.getType());

		ThemeInspireDTO themeInspire = ((ThemeInspireEvent) lastSuccessEvent).getThemeInspire();

		EventError eventError = (EventError) failedEvent;

		return ThemeInspireEventFactory.getEvent(failedEvent, ThemeInspireEventTypes.DELETE_CANCELLED, themeInspire,
				eventError.getExceptionType(), eventError.getArguments());
	}

	@Override
	protected void processEnrichCreateSteam(KStream<String, Event> events) {
		// En este caso no hay enriquecimiento
	}

	@Override
	protected void processEnrichUpdateSteam(KStream<String, Event> events) {
		// En este caso no hay enriquecimiento
	}

	@Override
	protected void processPartialUpdatedStream(KStream<String, Event> themeInspireEvents,
			KStream<String, Event> updateConfirmedEvents) {
		// En este caso no hay modificaciones parciales
	}

	@Override
	protected void processExtraStreams(KStream<String, Event> events, KTable<String, Event> successEventsTable) {
	}

	@Override
	protected Event getRollbackEvent(Event prepareRollbackEvent, Event lastSuccessEvent) {

		return ThemeInspireEventFactory.getEvent(prepareRollbackEvent, ThemeInspireEventTypes.ROLLBACK,
				lastSuccessEvent != null ? ((ThemeInspireEvent) lastSuccessEvent).getThemeInspire() : null);
	}
}
