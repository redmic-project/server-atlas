package es.redmic.atlaslib.unit.utils;

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

import java.util.HashMap;
import java.util.Map;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.create.ThemeInspireCreatedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.CheckDeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireFailedEvent;

public abstract class ThemeInspireDataUtil {

	// @formatter:off
	public final static String PREFIX = "themeinspire-code-",
			CODE = "gg",
			USER = "1";
	// @formatter:on

	// Create

	public static CreateThemeInspireEvent getCreateEvent() {

		CreateThemeInspireEvent event = new CreateThemeInspireEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(ThemeInspireEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setThemeInspire(getThemeInspire());

		return event;
	}

	public static CreateThemeInspireConfirmedEvent getCreateThemeInspireConfirmedEvent() {

		CreateThemeInspireConfirmedEvent event = new CreateThemeInspireConfirmedEvent().buildFrom(getCreateEvent());
		event.setType(ThemeInspireEventTypes.CREATE_CONFIRMED);
		return event;
	}

	public static ThemeInspireCreatedEvent getThemeInspireCreatedEvent() {

		ThemeInspireCreatedEvent event = new ThemeInspireCreatedEvent().buildFrom(getCreateEvent());
		event.setType(ThemeInspireEventTypes.CREATED);
		event.setThemeInspire(getThemeInspire());
		return event;
	}

	public static CreateThemeInspireFailedEvent getCreateThemeInspireFailedEvent() {

		CreateThemeInspireFailedEvent event = new CreateThemeInspireFailedEvent().buildFrom(getCreateEvent());
		event.setType(ThemeInspireEventTypes.CREATE_FAILED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	public static CreateThemeInspireCancelledEvent getCreateThemeInspireCancelledEvent() {

		CreateThemeInspireCancelledEvent event = new CreateThemeInspireCancelledEvent().buildFrom(getCreateEvent());
		event.setType(ThemeInspireEventTypes.CREATE_CANCELLED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	// Update

	public static UpdateThemeInspireEvent getUpdateEvent() {

		UpdateThemeInspireEvent event = new UpdateThemeInspireEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(ThemeInspireEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setThemeInspire(getThemeInspire());
		return event;
	}

	public static UpdateThemeInspireConfirmedEvent getUpdateThemeInspireConfirmedEvent() {

		UpdateThemeInspireConfirmedEvent event = new UpdateThemeInspireConfirmedEvent().buildFrom(getUpdateEvent());
		event.setType(ThemeInspireEventTypes.UPDATE_CONFIRMED);
		return event;
	}

	public static ThemeInspireUpdatedEvent getThemeInspireUpdatedEvent() {

		ThemeInspireUpdatedEvent event = new ThemeInspireUpdatedEvent().buildFrom(getUpdateEvent());
		event.setType(ThemeInspireEventTypes.UPDATED);
		event.setThemeInspire(getThemeInspire());
		return event;
	}

	public static UpdateThemeInspireFailedEvent getUpdateThemeInspireFailedEvent() {

		UpdateThemeInspireFailedEvent event = new UpdateThemeInspireFailedEvent().buildFrom(getUpdateEvent());
		event.setType(ThemeInspireEventTypes.UPDATE_FAILED);
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static UpdateThemeInspireCancelledEvent getUpdateThemeInspireCancelledEvent() {

		UpdateThemeInspireCancelledEvent event = new UpdateThemeInspireCancelledEvent().buildFrom(getUpdateEvent());
		event.setType(ThemeInspireEventTypes.UPDATE_FAILED);
		event.setThemeInspire(getThemeInspire());
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	// Delete

	public static DeleteThemeInspireEvent getDeleteEvent() {

		DeleteThemeInspireEvent event = new DeleteThemeInspireEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(ThemeInspireEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		return event;
	}

	public static CheckDeleteThemeInspireEvent getCheckDeleteThemeInspireEvent() {

		return new CheckDeleteThemeInspireEvent().buildFrom(getDeleteEvent());
	}

	public static DeleteThemeInspireCheckedEvent getDeleteThemeInspireCheckedEvent() {

		return new DeleteThemeInspireCheckedEvent().buildFrom(getDeleteEvent());
	}

	public static DeleteThemeInspireCheckFailedEvent getDeleteThemeInspireCheckFailedEvent() {

		DeleteThemeInspireCheckFailedEvent event = new DeleteThemeInspireCheckFailedEvent().buildFrom(getDeleteEvent());
		event.setExceptionType("ItemIsReferenced");
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static DeleteThemeInspireConfirmedEvent getDeleteThemeInspireConfirmedEvent() {

		DeleteThemeInspireConfirmedEvent event = new DeleteThemeInspireConfirmedEvent().buildFrom(getDeleteEvent());
		event.setAggregateId(PREFIX + CODE);
		event.setType(ThemeInspireEventTypes.DELETE_CONFIRMED);
		event.setVersion(3);

		return event;
	}

	public static ThemeInspireDeletedEvent getThemeInspireDeletedEvent() {

		ThemeInspireDeletedEvent event = new ThemeInspireDeletedEvent().buildFrom(getDeleteEvent());
		event.setType(ThemeInspireEventTypes.DELETED);
		return event;
	}

	public static DeleteThemeInspireFailedEvent getDeleteThemeInspireFailedEvent() {

		DeleteThemeInspireFailedEvent event = new DeleteThemeInspireFailedEvent().buildFrom(getDeleteEvent());
		event.setType(ThemeInspireEventTypes.DELETE_FAILED);
		event.setExceptionType("ItemNotFound");
		return event;
	}

	public static DeleteThemeInspireCancelledEvent getDeleteThemeInspireCancelledEvent() {

		DeleteThemeInspireCancelledEvent event = new DeleteThemeInspireCancelledEvent().buildFrom(getDeleteEvent());
		event.setType(ThemeInspireEventTypes.DELETE_CONFIRMED);
		event.setThemeInspire(getThemeInspire());
		event.setExceptionType("ItemNotFound");
		return event;
	}

	public static ThemeInspireDTO getThemeInspire() {

		ThemeInspireDTO themeInspire = new ThemeInspireDTO();
		themeInspire.setCode(CODE);
		themeInspire.setId(PREFIX + CODE);
		themeInspire.setName("Sistema de cuadrículas geográficas");
		themeInspire.setName_en("Geographical grid systems");

		return themeInspire;
	}
}
