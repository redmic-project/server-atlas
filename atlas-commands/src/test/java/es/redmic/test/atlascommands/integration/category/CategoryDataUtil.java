package es.redmic.test.atlascommands.integration.category;

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.create.CategoryCreatedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryFailedEvent;
import es.redmic.atlaslib.events.category.delete.CategoryDeletedEvent;
import es.redmic.atlaslib.events.category.delete.CheckDeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryFailedEvent;
import es.redmic.atlaslib.events.category.update.CategoryUpdatedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryFailedEvent;
import es.redmic.exception.common.ExceptionType;

public abstract class CategoryDataUtil {

	// @formatter:off

	public final static String PREFIX = "category-",
			USER = "REDMIC_PROCESS";

	// @formatter:on

	// Create

	public static CreateCategoryEvent getCreateEvent(String code) {

		CreateCategoryEvent event = new CreateCategoryEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(CategoryEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setSessionId("sessionIdA");
		event.setCategory(getCategory(code));

		return event;
	}

	public static CreateCategoryConfirmedEvent getCreateCategoryConfirmedEvent(String code) {

		return new CreateCategoryConfirmedEvent().buildFrom(getCreateEvent(code));
	}

	public static CategoryCreatedEvent getCategoryCreatedEvent(String code) {

		CategoryCreatedEvent event = new CategoryCreatedEvent().buildFrom(getCreateEvent(code));

		event.setCategory(getCategory(code));

		return event;
	}

	public static CreateCategoryFailedEvent getCreateCategoryFailedEvent(String code) {

		CreateCategoryFailedEvent event = new CreateCategoryFailedEvent().buildFrom(getCreateEvent(code));

		event.setExceptionType(ExceptionType.ITEM_ALREADY_EXIST_EXCEPTION.name());

		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		event.setArguments(arguments);

		return event;
	}

	// Update

	public static UpdateCategoryEvent getUpdateEvent(String code) {

		UpdateCategoryEvent event = new UpdateCategoryEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(CategoryEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setSessionId("sessionIdB");
		event.setCategory(getCategory(code));
		return event;
	}

	public static UpdateCategoryConfirmedEvent getUpdateCategoryConfirmedEvent(String code) {

		return new UpdateCategoryConfirmedEvent().buildFrom(getUpdateEvent(code));
	}

	public static CategoryUpdatedEvent getCategoryUpdatedEvent(String code) {

		CategoryUpdatedEvent event = new CategoryUpdatedEvent().buildFrom(getUpdateEvent(code));

		event.setCategory(getCategory(code));

		return event;
	}

	public static UpdateCategoryFailedEvent getUpdateCategoryFailedEvent(String code) {

		UpdateCategoryFailedEvent event = new UpdateCategoryFailedEvent().buildFrom(getUpdateEvent(code));

		event.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());

		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);

		return event;
	}

	// Delete

	public static DeleteCategoryEvent getDeleteEvent(String code) {

		DeleteCategoryEvent event = new DeleteCategoryEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(CategoryEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		event.setSessionId("sessionIdC");
		return event;
	}

	public static CheckDeleteCategoryEvent getCheckDeleteCategoryEvent(String code) {

		return new CheckDeleteCategoryEvent().buildFrom(getDeleteEvent(code));
	}

	public static DeleteCategoryConfirmedEvent getDeleteCategoryConfirmedEvent(String code) {

		return new DeleteCategoryConfirmedEvent().buildFrom(getDeleteEvent(code));
	}

	public static CategoryDeletedEvent getCategoryDeletedEvent(String code) {

		return new CategoryDeletedEvent().buildFrom(getDeleteEvent(code));
	}

	public static DeleteCategoryFailedEvent getDeleteCategoryFailedEvent(String code) {

		DeleteCategoryFailedEvent event = new DeleteCategoryFailedEvent().buildFrom(getDeleteEvent(code));

		event.setExceptionType(ExceptionType.DELETE_ITEM_EXCEPTION.name());

		Map<String, String> arguments = new HashMap<>();
		// arguments.put("A", "B");
		event.setArguments(arguments);

		return event;
	}

	public static CategoryDTO getCategory(String code) {

		CategoryDTO category = new CategoryDTO();
		category.setId(PREFIX + code);
		category.setName("AIS");

		return category;
	}
}
