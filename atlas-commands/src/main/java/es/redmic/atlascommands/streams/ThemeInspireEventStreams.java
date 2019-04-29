package es.redmic.atlascommands.streams;

import java.util.HashMap;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.atlas.partialupdate.themeinspire.AggregationThemeInspireInAtlasPostUpdateEvent;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventFactory;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireEvent;
import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.common.EventTypes;
import es.redmic.brokerlib.avro.serde.hashmap.HashMapSerde;
import es.redmic.commandslib.exceptions.ExceptionType;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.streams.EventSourcingStreams;

public class ThemeInspireEventStreams extends EventSourcingStreams {

	private String atlasAggByThemeInspireTopic;

	private HashMapSerde<String, AggregationThemeInspireInAtlasPostUpdateEvent> hashMapSerde;

	private KTable<String, HashMap<String, AggregationThemeInspireInAtlasPostUpdateEvent>> aggByThemeInspire;

	public ThemeInspireEventStreams(StreamConfig config, String atlasAggByThemeInspireTopic,
			AlertService alertService) {
		super(config, alertService);
		this.atlasAggByThemeInspireTopic = atlasAggByThemeInspireTopic;
		this.hashMapSerde = new HashMapSerde<>(schemaRegistry);

		init();
	}

	/**
	 * Crea KTable de atlas agregados por themeinspire
	 * 
	 * @see es.redmic.commandslib.streaming.streams.EventSourcingStreams#
	 *      createExtraStreams()
	 */

	@Override
	protected void createExtraStreams() {
		aggByThemeInspire = builder.table(atlasAggByThemeInspireTopic, Consumed.with(Serdes.String(), hashMapSerde));
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
	 * Comprueba si themeInspire está referenciado en atlas para cancelar el borrado
	 */

	@Override
	protected void processDeleteStream(KStream<String, Event> events) {

		// Stream filtrado por eventos de borrado
		KStream<String, Event> deleteEvents = events
				.filter((id, event) -> (EventTypes.CHECK_DELETE.equals(event.getType())));

		deleteEvents.leftJoin(aggByThemeInspire,
				(deleteEvent, atlasAggByThemeInspire) -> getCheckDeleteResultEvent(deleteEvent, atlasAggByThemeInspire))
				.to(topic);
	}

	@SuppressWarnings("serial")
	private Event getCheckDeleteResultEvent(Event deleteEvent,
			HashMap<String, AggregationThemeInspireInAtlasPostUpdateEvent> atlasAggByThemeInspire) {

		if (atlasAggByThemeInspire == null || atlasAggByThemeInspire.isEmpty()) { // elemento no referenciado

			return ThemeInspireEventFactory.getEvent(deleteEvent, ThemeInspireEventTypes.DELETE_CHECKED);
		} else { // elemento referenciado

			return ThemeInspireEventFactory.getEvent(deleteEvent, ThemeInspireEventTypes.DELETE_CHECK_FAILED,
					ExceptionType.ITEM_REFERENCED.toString(), new HashMap<String, String>() {
						{
							put("id", deleteEvent.getAggregateId());
						}
					});
		}
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

	/**
	 * Función para procesar modificaciones de referencias
	 */

	@Override
	protected void processPostUpdateStream(KStream<String, Event> events) {
		// En este caso no hay modificación de relaciones
	}

	@Override
	protected void processExtraStreams(KStream<String, Event> events, KStream<String, Event> snapshotEvents) {
	}
}
