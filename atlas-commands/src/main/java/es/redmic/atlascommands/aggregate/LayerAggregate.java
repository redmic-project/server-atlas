package es.redmic.atlascommands.aggregate;

import es.redmic.atlascommands.commands.layer.CreateLayerCommand;
import es.redmic.atlascommands.commands.layer.DeleteLayerCommand;
import es.redmic.atlascommands.commands.layer.UpdateLayerCommand;
import es.redmic.atlascommands.statestore.LayerStateStore;

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

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.common.LayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.delete.CheckDeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.aggregate.Aggregate;

public class LayerAggregate extends Aggregate {

	private LayerDTO layer;

	private LayerStateStore layerStateStore;

	public LayerAggregate(LayerStateStore layerStateStore) {

		this.layerStateStore = layerStateStore;
	}

	public CreateLayerEvent process(CreateLayerCommand cmd) {

		assert layerStateStore != null;

		String id = cmd.getLayer().getId();

		if (exist(id)) {
			logger.info("Descartando Layer " + id + ". Ya está registrado.");
			return null; // Se lanza excepción en el origen no aquí
		}

		this.setAggregateId(id);

		CreateLayerEvent evt = new CreateLayerEvent(cmd.getLayer());
		evt.setAggregateId(id);
		evt.setVersion(1);
		return evt;
	}

	public UpdateLayerEvent process(UpdateLayerCommand cmd) {

		assert layerStateStore != null;

		String id = cmd.getLayer().getId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		UpdateLayerEvent evt = new UpdateLayerEvent(cmd.getLayer());
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public CheckDeleteLayerEvent process(DeleteLayerCommand cmd) {

		assert layerStateStore != null;

		String id = cmd.getLayerId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		CheckDeleteLayerEvent evt = new CheckDeleteLayerEvent();
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);

		return evt;
	}

	public LayerDTO getLayer() {
		return layer;
	}

	@Override
	protected boolean isLocked(String eventType) {

		return LayerEventTypes.isLocked(eventType);
	}

	@Override
	protected Event getItemFromStateStore(String id) {

		return layerStateStore.getLayer(id);
	}

	@Override
	public void loadFromHistory(Event event) {

		logger.debug("Cargando último estado de Layer ", event.getAggregateId());

		check(event);

		String eventType = event.getType();

		switch (eventType) {
		case "CREATED":
			logger.debug("Item creado");
			apply((LayerEvent) event);
			break;
		case "UPDATED":
			logger.debug("Item modificado");
			apply((LayerEvent) event);
			break;
		case "DELETED":
			logger.debug("Item borrado");
			apply((LayerDeletedEvent) event);
			break;
		// CANCELLED
		case "CREATE_CANCELLED":
			logger.debug("Compensación por creación fallida");
			apply((CreateLayerCancelledEvent) event);
			break;
		case "UPDATE_CANCELLED":
		case "DELETE_CANCELLED":
			logger.debug("Compensación por edición/borrado fallido");
			apply((LayerEvent) event);
			break;
		default:
			logger.debug("Evento no manejado ", event.getType());
		}
	}

	public void apply(CreateLayerCancelledEvent event) {
		this.deleted = true;
		apply(event);
	}

	public void apply(LayerDeletedEvent event) {
		this.deleted = true;
		super.apply(event);
	}

	public void apply(LayerEvent event) {
		this.layer = event.getLayer();
		super.apply(event);
	}

	@Override
	protected void reset() {
		this.layer = null;
		super.reset();
	}
}