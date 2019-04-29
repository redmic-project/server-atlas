package es.redmic.atlascommands.aggregate;

import es.redmic.atlascommands.commands.themeinspire.CreateThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.DeleteThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.UpdateThemeInspireCommand;
import es.redmic.atlascommands.statestore.ThemeInspireStateStore;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.CheckDeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.aggregate.Aggregate;

public class ThemeInspireAggregate extends Aggregate {

	private ThemeInspireDTO themeInspire;

	private ThemeInspireStateStore themeInspireStateStore;

	public ThemeInspireAggregate(ThemeInspireStateStore themeInspireStateStore) {

		this.themeInspireStateStore = themeInspireStateStore;
	}

	public CreateThemeInspireEvent process(CreateThemeInspireCommand cmd) {

		assert themeInspireStateStore != null;

		String id = cmd.getThemeInspire().getId();

		if (exist(id)) {
			logger.info("Descartando ThemeInspire " + id + ". Ya está registrado.");
			return null; // Se lanza excepción en el origen no aquí
		}

		this.setAggregateId(id);

		CreateThemeInspireEvent evt = new CreateThemeInspireEvent(cmd.getThemeInspire());
		evt.setAggregateId(id);
		evt.setVersion(1);
		return evt;
	}

	public UpdateThemeInspireEvent process(UpdateThemeInspireCommand cmd) {

		assert themeInspireStateStore != null;

		String id = cmd.getThemeInspire().getId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		UpdateThemeInspireEvent evt = new UpdateThemeInspireEvent(cmd.getThemeInspire());
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public CheckDeleteThemeInspireEvent process(DeleteThemeInspireCommand cmd) {

		assert themeInspireStateStore != null;

		String id = cmd.getThemeInspireId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		CheckDeleteThemeInspireEvent evt = new CheckDeleteThemeInspireEvent();
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);

		return evt;
	}

	public ThemeInspireDTO getThemeInspire() {
		return themeInspire;
	}

	@Override
	protected boolean isLocked(String eventType) {

		return ThemeInspireEventTypes.isLocked(eventType);
	}

	@Override
	protected Event getItemFromStateStore(String id) {

		return themeInspireStateStore.getThemeInspire(id);
	}

	@Override
	public void loadFromHistory(Event event) {

		logger.debug("Cargando último estado de ThemeInspire ", event.getAggregateId());

		check(event);

		String eventType = event.getType();

		switch (eventType) {
		case "CREATED":
			logger.debug("Item creado");
			apply((ThemeInspireEvent) event);
			break;
		case "UPDATED":
			logger.debug("Item modificado");
			apply((ThemeInspireEvent) event);
			break;
		case "DELETED":
			logger.debug("Item borrado");
			apply((ThemeInspireDeletedEvent) event);
			break;
		// CANCELLED
		case "CREATE_CANCELLED":
			logger.debug("Compensación por creación fallida");
			apply((CreateThemeInspireCancelledEvent) event);
			break;
		case "UPDATE_CANCELLED":
		case "DELETE_CANCELLED":
			logger.debug("Compensación por edición/borrado fallido");
			apply((ThemeInspireEvent) event);
			break;
		default:
			logger.debug("Evento no manejado ", event.getType());
		}
	}

	public void apply(CreateThemeInspireCancelledEvent event) {
		this.deleted = true;
		apply(event);
	}

	public void apply(ThemeInspireDeletedEvent event) {
		this.deleted = true;
		super.apply(event);
	}

	public void apply(ThemeInspireEvent event) {
		this.themeInspire = event.getThemeInspire();
		super.apply(event);
	}

	@Override
	protected void reset() {
		this.themeInspire = null;
		super.reset();
	}
}
