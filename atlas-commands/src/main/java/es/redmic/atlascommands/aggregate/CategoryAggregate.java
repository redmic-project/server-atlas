package es.redmic.atlascommands.aggregate;

import es.redmic.atlascommands.commands.category.CreateCategoryCommand;
import es.redmic.atlascommands.commands.category.DeleteCategoryCommand;
import es.redmic.atlascommands.commands.category.UpdateCategoryCommand;
import es.redmic.atlascommands.statestore.CategoryStateStore;

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

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.common.CategoryCancelledEvent;
import es.redmic.atlaslib.events.category.common.CategoryEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.delete.CategoryDeletedEvent;
import es.redmic.atlaslib.events.category.delete.CheckDeleteCategoryEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.aggregate.Aggregate;
import es.redmic.commandslib.exceptions.ItemLockedException;

public class CategoryAggregate extends Aggregate {

	private CategoryDTO category;

	private CategoryStateStore categoryStateStore;

	public CategoryAggregate(CategoryStateStore categoryStateStore) {

		this.categoryStateStore = categoryStateStore;
	}

	public CreateCategoryEvent process(CreateCategoryCommand cmd) {

		assert categoryStateStore != null;

		String id = cmd.getCategory().getId();

		if (exist(id)) {
			logger.info("Descartando Category " + id + ". Ya está registrado.");
			return null; // Se lanza excepción en el origen no aquí
		}

		this.setAggregateId(id);

		CreateCategoryEvent evt = new CreateCategoryEvent(cmd.getCategory());
		evt.setAggregateId(id);
		evt.setVersion(1);
		return evt;
	}

	public UpdateCategoryEvent process(UpdateCategoryCommand cmd) {

		assert categoryStateStore != null;

		String id = cmd.getCategory().getId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		UpdateCategoryEvent evt = new UpdateCategoryEvent(cmd.getCategory());
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public CheckDeleteCategoryEvent process(DeleteCategoryCommand cmd) {

		assert categoryStateStore != null;

		String id = cmd.getCategoryId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		CheckDeleteCategoryEvent evt = new CheckDeleteCategoryEvent();
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);

		return evt;
	}

	public CategoryDTO getCategory() {
		return category;
	}

	@Override
	protected boolean isLocked(String eventType) {

		return CategoryEventTypes.isLocked(eventType);
	}

	@Override
	protected Event getItemFromStateStore(String id) {

		return categoryStateStore.get(id);
	}

	@Override
	public void loadFromHistory(Event event) {

		logger.debug("Cargando último estado de Category ", event.getAggregateId());

		check(event);

		String eventType = event.getType();

		switch (eventType) {
		case "CREATED":
			logger.debug("Item creado");
			apply((CategoryEvent) event);
			break;
		case "UPDATED":
			logger.debug("Item modificado");
			apply((CategoryEvent) event);
			break;
		case "DELETED":
			logger.debug("Item borrado");
			apply((CategoryDeletedEvent) event);
			break;
		// CANCELLED
		case "CREATE_CANCELLED":
			logger.debug("Compensación por creación fallida");
			apply((CreateCategoryCancelledEvent) event);
			break;
		case "UPDATE_CANCELLED":
		case "DELETE_CANCELLED":
			logger.debug("Compensación por edición/borrado fallido");
			apply((CategoryCancelledEvent) event);
			break;
		default:
			throw new ItemLockedException("id", event.getAggregateId());
		}
	}

	public void apply(CreateCategoryCancelledEvent event) {
		this.deleted = true;
		super.apply(event);
	}

	public void apply(CategoryDeletedEvent event) {
		this.deleted = true;
		super.apply(event);
	}

	public void apply(CategoryCancelledEvent event) {
		this.category = event.getCategory();
		super.apply(event);
	}

	public void apply(CategoryEvent event) {
		this.category = event.getCategory();
		super.apply(event);
	}

	@Override
	protected void reset() {
		this.category = null;
		super.reset();
	}
}
