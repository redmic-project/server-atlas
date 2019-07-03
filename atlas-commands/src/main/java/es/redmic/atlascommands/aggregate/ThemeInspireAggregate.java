package es.redmic.atlascommands.aggregate;

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

import es.redmic.atlascommands.commands.themeinspire.CreateThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.DeleteThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.UpdateThemeInspireCommand;
import es.redmic.atlascommands.statestore.ThemeInspireStateStore;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireCancelledEvent;
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

		return themeInspireStateStore.get(id);
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
			apply((ThemeInspireCancelledEvent) event);
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

	public void apply(ThemeInspireCancelledEvent event) {
		this.themeInspire = event.getThemeInspire();
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
