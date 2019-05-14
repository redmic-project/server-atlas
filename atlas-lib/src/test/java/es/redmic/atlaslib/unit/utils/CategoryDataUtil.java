package es.redmic.atlaslib.unit.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*-
 * #%L
 * Atlas-lib
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
import es.redmic.atlaslib.events.category.create.CategoryCreatedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryFailedEvent;
import es.redmic.atlaslib.events.category.delete.CategoryDeletedEvent;
import es.redmic.atlaslib.events.category.delete.CheckDeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckFailedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryFailedEvent;
import es.redmic.atlaslib.events.category.update.CategoryUpdatedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryFailedEvent;

public abstract class CategoryDataUtil {

	// @formatter:off
		public final static String PREFIX = "category-",
				CODE = UUID.randomUUID().toString(),
				USER = "1";
		// @formatter:on

	// Create

	public static CreateCategoryEvent getCreateEvent() {

		CreateCategoryEvent event = new CreateCategoryEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(CategoryEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setCategory(getCategory());

		return event;
	}

	public static CreateCategoryConfirmedEvent getCreateCategoryConfirmedEvent() {

		CreateCategoryConfirmedEvent event = new CreateCategoryConfirmedEvent().buildFrom(getCreateEvent());
		event.setType(CategoryEventTypes.CREATE_CONFIRMED);
		return event;
	}

	public static CategoryCreatedEvent getCategoryCreatedEvent() {

		CategoryCreatedEvent event = new CategoryCreatedEvent().buildFrom(getCreateEvent());
		event.setType(CategoryEventTypes.CREATED);
		event.setCategory(getCategory());
		return event;
	}

	public static CreateCategoryFailedEvent getCreateCategoryFailedEvent() {

		CreateCategoryFailedEvent event = new CreateCategoryFailedEvent().buildFrom(getCreateEvent());
		event.setType(CategoryEventTypes.CREATE_FAILED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	public static CreateCategoryCancelledEvent getCreateCategoryCancelledEvent() {

		CreateCategoryCancelledEvent event = new CreateCategoryCancelledEvent().buildFrom(getCreateEvent());
		event.setType(CategoryEventTypes.CREATE_CANCELLED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	// Update

	public static UpdateCategoryEvent getUpdateEvent() {

		UpdateCategoryEvent event = new UpdateCategoryEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(CategoryEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setCategory(getCategory());
		return event;
	}

	public static UpdateCategoryConfirmedEvent getUpdateCategoryConfirmedEvent() {

		UpdateCategoryConfirmedEvent event = new UpdateCategoryConfirmedEvent().buildFrom(getUpdateEvent());
		event.setType(CategoryEventTypes.UPDATE_CONFIRMED);
		return event;
	}

	public static CategoryUpdatedEvent getCategoryUpdatedEvent() {

		CategoryUpdatedEvent event = new CategoryUpdatedEvent().buildFrom(getUpdateEvent());
		event.setType(CategoryEventTypes.UPDATED);
		event.setCategory(getCategory());
		return event;
	}

	public static UpdateCategoryFailedEvent getUpdateCategoryFailedEvent() {

		UpdateCategoryFailedEvent event = new UpdateCategoryFailedEvent().buildFrom(getUpdateEvent());
		event.setType(CategoryEventTypes.UPDATE_FAILED);
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static UpdateCategoryCancelledEvent getUpdateCategoryCancelledEvent() {

		UpdateCategoryCancelledEvent event = new UpdateCategoryCancelledEvent().buildFrom(getUpdateEvent());
		event.setType(CategoryEventTypes.UPDATE_FAILED);
		event.setCategory(getCategory());
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	// Delete

	public static DeleteCategoryEvent getDeleteEvent() {

		DeleteCategoryEvent event = new DeleteCategoryEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(CategoryEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		return event;
	}

	public static CheckDeleteCategoryEvent getCheckDeleteCategoryEvent() {

		return new CheckDeleteCategoryEvent().buildFrom(getDeleteEvent());
	}

	public static DeleteCategoryCheckedEvent getDeleteCategoryCheckedEvent() {

		return new DeleteCategoryCheckedEvent().buildFrom(getDeleteEvent());
	}

	public static DeleteCategoryCheckFailedEvent getDeleteCategoryCheckFailedEvent() {

		DeleteCategoryCheckFailedEvent event = new DeleteCategoryCheckFailedEvent().buildFrom(getDeleteEvent());
		event.setExceptionType("ItemIsReferenced");
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static DeleteCategoryConfirmedEvent getDeleteCategoryConfirmedEvent() {

		DeleteCategoryConfirmedEvent event = new DeleteCategoryConfirmedEvent().buildFrom(getDeleteEvent());
		event.setAggregateId(PREFIX + CODE);
		event.setType(CategoryEventTypes.DELETE_CONFIRMED);
		event.setVersion(3);

		return event;
	}

	public static CategoryDeletedEvent getCategoryDeletedEvent() {

		CategoryDeletedEvent event = new CategoryDeletedEvent().buildFrom(getDeleteEvent());
		event.setType(CategoryEventTypes.DELETED);
		return event;
	}

	public static DeleteCategoryFailedEvent getDeleteCategoryFailedEvent() {

		DeleteCategoryFailedEvent event = new DeleteCategoryFailedEvent().buildFrom(getDeleteEvent());
		event.setType(CategoryEventTypes.DELETE_FAILED);
		event.setExceptionType("ItemNotFound");
		return event;
	}

	public static DeleteCategoryCancelledEvent getDeleteCategoryCancelledEvent() {

		DeleteCategoryCancelledEvent event = new DeleteCategoryCancelledEvent().buildFrom(getDeleteEvent());
		event.setType(CategoryEventTypes.DELETE_CONFIRMED);
		event.setCategory(getCategory());
		event.setExceptionType("ItemNotFound");
		return event;
	}

	public static CategoryDTO getCategory() {

		CategoryDTO category = new CategoryDTO();
		category.setId("1");
		category.setName("AIS");

		return category;
	}
}
