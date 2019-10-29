package es.redmic.atlascommands.streams;

import java.util.Arrays;

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

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.CategoryEventFactory;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.common.CategoryEvent;
import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.streams.EventSourcingStreams;

public class CategoryEventStreams extends EventSourcingStreams {

	public CategoryEventStreams(StreamConfig config, AlertService alertService) {
		super(config, alertService);
		init();
	}

	/**
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

		events.filter((id, event) -> (CategoryEventTypes.isSnapshot(event.getType()))).to(snapshotTopic);
	}

	/**
	 * Función que apartir del evento de confirmación de la vista y del evento
	 * create (petición de creación), si todo es correcto, genera evento created
	 */

	@Override
	protected Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(CategoryEventTypes.CREATE);

		assert confirmedEvent.getType().equals(CategoryEventTypes.CREATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		CategoryDTO category = ((CategoryEvent) requestEvent).getCategory();

		return CategoryEventFactory.getEvent(confirmedEvent, CategoryEventTypes.CREATED, category);
	}

	/**
	 * Función que apartir del evento de confirmación de la vista y del evento
	 * update (petición de modificación), si todo es correcto, genera evento updated
	 */

	@Override
	protected Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(CategoryEventTypes.UPDATE);

		assert confirmedEvent.getType().equals(CategoryEventTypes.UPDATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		CategoryDTO category = ((CategoryEvent) requestEvent).getCategory();

		return CategoryEventFactory.getEvent(requestEvent, CategoryEventTypes.UPDATED, category);
	}

	/**
	 * Como en este caso no existen referencias, directamente se transforma el
	 * evento CHECK en CHECKED
	 */

	@Override
	protected void processDeleteStream(KStream<String, Event> events) {

		// Stream filtrado por eventos de borrado
		KStream<String, Event> deleteEvents = events
				.filter((id, event) -> (CategoryEventTypes.CHECK_DELETE.equals(event.getType())));

		deleteEvents
				.flatMapValues(
						event -> Arrays.asList(CategoryEventFactory.getEvent(event, CategoryEventTypes.DELETE_CHECKED)))
				.to(topic);
	}

	/**
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento UpdateCancelled
	 */

	@Override
	protected Event getUpdateCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert failedEvent.getType().equals(CategoryEventTypes.UPDATE_FAILED);

		assert CategoryEventTypes.isSnapshot(lastSuccessEvent.getType());

		CategoryDTO category = ((CategoryEvent) lastSuccessEvent).getCategory();

		EventError eventError = (EventError) failedEvent;

		return CategoryEventFactory.getEvent(failedEvent, CategoryEventTypes.UPDATE_CANCELLED, category,
				eventError.getExceptionType(), eventError.getArguments());
	}

	/**
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento DeleteFailed
	 */

	@Override
	protected Event getDeleteCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert failedEvent.getType().equals(CategoryEventTypes.DELETE_FAILED);

		assert CategoryEventTypes.isSnapshot(lastSuccessEvent.getType());

		CategoryDTO category = ((CategoryEvent) lastSuccessEvent).getCategory();

		EventError eventError = (EventError) failedEvent;

		return CategoryEventFactory.getEvent(failedEvent, CategoryEventTypes.DELETE_CANCELLED, category,
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
	protected void processPartialUpdatedStream(KStream<String, Event> categoryEvents,
			KStream<String, Event> updateConfirmedEvents) {
		// En este caso no hay modificaciones parciales
	}

	@Override
	protected void processExtraStreams(KStream<String, Event> events, KTable<String, Event> successEventsTable) {
	}

	@Override
	protected Event getRollbackEvent(Event prepareRollbackEvent, Event lastSuccessEvent) {

		return CategoryEventFactory.getEvent(prepareRollbackEvent, CategoryEventTypes.ROLLBACK,
				lastSuccessEvent != null ? ((CategoryEvent) lastSuccessEvent).getCategory() : null);
	}
}
