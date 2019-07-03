package es.redmic.test.atlascommands.integration.themeinspire;

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

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.create.ThemeInspireCreatedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.CheckDeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireFailedEvent;
import es.redmic.exception.common.ExceptionType;

public abstract class ThemeInspireDataUtil {

	// @formatter:off

	public final static String PREFIX = "themeinspired-code-",
			USER = "REDMIC_PROCESS";

	// @formatter:on

	// Create

	public static CreateThemeInspireEvent getCreateEvent(String code) {

		CreateThemeInspireEvent event = new CreateThemeInspireEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(ThemeInspireEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setSessionId("sessionIdA");
		event.setThemeInspire(getThemeInspire(code));

		return event;
	}

	public static CreateThemeInspireConfirmedEvent getCreateThemeInspireConfirmedEvent(String code) {

		return new CreateThemeInspireConfirmedEvent().buildFrom(getCreateEvent(code));
	}

	public static ThemeInspireCreatedEvent getThemeInspireCreatedEvent(String code) {

		ThemeInspireCreatedEvent event = new ThemeInspireCreatedEvent().buildFrom(getCreateEvent(code));

		event.setThemeInspire(getThemeInspire(code));

		return event;
	}

	public static CreateThemeInspireFailedEvent getCreateThemeInspireFailedEvent(String code) {

		CreateThemeInspireFailedEvent event = new CreateThemeInspireFailedEvent().buildFrom(getCreateEvent(code));

		event.setExceptionType(ExceptionType.ITEM_ALREADY_EXIST_EXCEPTION.name());

		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		event.setArguments(arguments);

		return event;
	}

	// Update

	public static UpdateThemeInspireEvent getUpdateEvent(String code) {

		UpdateThemeInspireEvent event = new UpdateThemeInspireEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(ThemeInspireEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setSessionId("sessionIdB");
		event.setThemeInspire(getThemeInspire(code));
		return event;
	}

	public static UpdateThemeInspireConfirmedEvent getUpdateThemeInspireConfirmedEvent(String code) {

		return new UpdateThemeInspireConfirmedEvent().buildFrom(getUpdateEvent(code));
	}

	public static ThemeInspireUpdatedEvent getThemeInspireUpdatedEvent(String code) {

		ThemeInspireUpdatedEvent event = new ThemeInspireUpdatedEvent().buildFrom(getUpdateEvent(code));

		event.setThemeInspire(getThemeInspire(code));

		return event;
	}

	public static UpdateThemeInspireCancelledEvent getUpdateThemeInspireCancelledEvent(String code) {

		UpdateThemeInspireCancelledEvent event = new UpdateThemeInspireCancelledEvent().buildFrom(getUpdateEvent(code));

		event.setThemeInspire(getThemeInspire(code));

		event.setExceptionType(ExceptionType.ES_UPDATE_DOCUMENT.name());

		Map<String, String> arguments = new HashMap<>();
		// arguments.put("A", "B");
		event.setArguments(arguments);
		return event;
	}

	public static UpdateThemeInspireFailedEvent getUpdateThemeInspireFailedEvent(String code) {

		UpdateThemeInspireFailedEvent event = new UpdateThemeInspireFailedEvent().buildFrom(getUpdateEvent(code));

		event.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());

		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);

		return event;
	}

	// Delete

	public static DeleteThemeInspireEvent getDeleteEvent(String code) {

		DeleteThemeInspireEvent event = new DeleteThemeInspireEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(ThemeInspireEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		event.setSessionId("sessionIdC");
		return event;
	}

	public static CheckDeleteThemeInspireEvent getCheckDeleteThemeInspireEvent(String code) {

		return new CheckDeleteThemeInspireEvent().buildFrom(getDeleteEvent(code));
	}

	public static DeleteThemeInspireConfirmedEvent getDeleteThemeInspireConfirmedEvent(String code) {

		return new DeleteThemeInspireConfirmedEvent().buildFrom(getDeleteEvent(code));
	}

	public static ThemeInspireDeletedEvent getThemeInspireDeletedEvent(String code) {

		return new ThemeInspireDeletedEvent().buildFrom(getDeleteEvent(code));
	}

	public static DeleteThemeInspireCancelledEvent getDeleteThemeInspireCancelledEvent(String code) {

		DeleteThemeInspireCancelledEvent event = new DeleteThemeInspireCancelledEvent().buildFrom(getDeleteEvent(code));

		event.setThemeInspire(getThemeInspire(code));

		event.setExceptionType(ExceptionType.DELETE_ITEM_EXCEPTION.name());

		Map<String, String> arguments = new HashMap<>();
		// arguments.put("A", "B");
		event.setArguments(arguments);

		return event;
	}

	public static DeleteThemeInspireFailedEvent getDeleteThemeInspireFailedEvent(String code) {

		DeleteThemeInspireFailedEvent event = new DeleteThemeInspireFailedEvent().buildFrom(getDeleteEvent(code));

		event.setExceptionType(ExceptionType.DELETE_ITEM_EXCEPTION.name());

		Map<String, String> arguments = new HashMap<>();
		// arguments.put("A", "B");
		event.setArguments(arguments);

		return event;
	}

	public static ThemeInspireDTO getThemeInspire(String code) {

		ThemeInspireDTO themeInspire = new ThemeInspireDTO();
		themeInspire.setCode(code);
		themeInspire.setId(PREFIX + code);
		themeInspire.setName("Sistema de cuadrículas geográficas");
		themeInspire.setName_en("Geographical grid systems");

		return themeInspire;
	}
}
