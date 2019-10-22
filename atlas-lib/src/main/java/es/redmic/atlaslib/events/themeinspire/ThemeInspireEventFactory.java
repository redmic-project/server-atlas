package es.redmic.atlaslib.events.themeinspire;

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

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.create.ThemeInspireCreatedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.fail.ThemeInspireRollbackEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireFailedEvent;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.fail.PrepareRollbackEvent;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;

public class ThemeInspireEventFactory {

	private static Logger logger = LogManager.getLogger();

	public static Event getEvent(Event source, String type) {

		if (type.equals(ThemeInspireEventTypes.DELETE)) {

			logger.debug("Creando evento DeleteThemeInspireEvent para: " + source.getAggregateId());
			return new DeleteThemeInspireEvent().buildFrom(source);
		}

		if (type.equals(ThemeInspireEventTypes.DELETE_CHECKED)) {

			logger.debug("Creando evento DeleteThemeInspireCheckedEvent para: " + source.getAggregateId());
			return new DeleteThemeInspireCheckedEvent().buildFrom(source);
		}

		if (type.equals(ThemeInspireEventTypes.CREATE_CONFIRMED)) {

			logger.debug("Creando evento CreateThemeInspireConfirmedEvent para: " + source.getAggregateId());

			return new CreateThemeInspireConfirmedEvent().buildFrom(source);
		}

		if (type.equals(ThemeInspireEventTypes.UPDATE_CONFIRMED)) {

			logger.debug("Creando evento UpdateThemeInspireConfirmedEvent para: " + source.getAggregateId());

			return new UpdateThemeInspireConfirmedEvent().buildFrom(source);
		}

		if (type.equals(ThemeInspireEventTypes.DELETE_CONFIRMED)) {

			logger.debug("Creando evento DeleteThemeInspireConfirmedEvent para: " + source.getAggregateId());

			return new DeleteThemeInspireConfirmedEvent().buildFrom(source);
		}

		if (type.equals(ThemeInspireEventTypes.DELETED)) {

			logger.debug("Creando evento ThemeInspireDeletedEvent para: " + source.getAggregateId());
			return new ThemeInspireDeletedEvent().buildFrom(source);
		}

		logger.error("Tipo de evento no soportado");
		throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
	}

	public static Event getEvent(Event source, String type, ThemeInspireDTO themeInspire) {

		if (type.equals(ThemeInspireEventTypes.ROLLBACK)) {
			logger.debug("Creando evento ThemeInspireRollbackEvent para: " + source.getAggregateId());
			ThemeInspireRollbackEvent rollbackEvent = new ThemeInspireRollbackEvent().buildFrom(source);
			rollbackEvent.setLastSnapshotItem(themeInspire);
			rollbackEvent.setFailEventType(((PrepareRollbackEvent) source).getFailEventType());
			return rollbackEvent;
		}

		ThemeInspireEvent successfulEvent = null;

		if (type.equals(ThemeInspireEventTypes.CREATED)) {
			logger.debug("Creando evento ThemeInspireCreatedEvent para: " + source.getAggregateId());
			successfulEvent = new ThemeInspireCreatedEvent().buildFrom(source);
		}

		if (type.equals(ThemeInspireEventTypes.UPDATED)) {
			logger.debug("Creando evento ThemeInspireUpdatedEvent para: " + source.getAggregateId());
			successfulEvent = new ThemeInspireUpdatedEvent().buildFrom(source);
		}

		if (successfulEvent != null) {
			successfulEvent.setThemeInspire(themeInspire);
			return successfulEvent;
		} else {
			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}

	public static Event getEvent(Event source, String type, String exceptionType,
			Map<String, String> exceptionArguments) {

		EventError failedEvent = null;

		if (type.equals(ThemeInspireEventTypes.CREATE_FAILED)) {

			logger.debug("No se pudo crear Atlas type en la vista");
			failedEvent = new CreateThemeInspireFailedEvent().buildFrom(source);
		}
		if (type.equals(ThemeInspireEventTypes.UPDATE_FAILED)) {

			logger.debug("No se pudo modificar Atlas type en la vista");
			failedEvent = new UpdateThemeInspireFailedEvent().buildFrom(source);
		}
		if (type.equals(ThemeInspireEventTypes.DELETE_FAILED)) {

			logger.debug("No se pudo eliminar Atlas type de la vista");
			failedEvent = new DeleteThemeInspireFailedEvent().buildFrom(source);
		}

		if (type.equals(ThemeInspireEventTypes.DELETE_CHECK_FAILED)) {

			logger.debug("Checkeo de eliminación fallido, el item está referenciado");
			failedEvent = new DeleteThemeInspireCheckFailedEvent().buildFrom(source);
		}

		if (type.equals(ThemeInspireEventTypes.CREATE_CANCELLED)) {

			logger.debug("Enviando evento CreateThemeInspireCancelledEvent para: " + source.getAggregateId());
			failedEvent = new CreateThemeInspireCancelledEvent().buildFrom(source);
		}

		if (failedEvent != null) {

			failedEvent.setExceptionType(exceptionType);
			failedEvent.setArguments(exceptionArguments);
			return failedEvent;

		} else {
			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}

	public static Event getEvent(Event source, String type, ThemeInspireDTO themeInspire, String exceptionType,
			Map<String, String> exceptionArguments) {

		ThemeInspireCancelledEvent cancelledEvent = null;

		if (type.equals(ThemeInspireEventTypes.UPDATE_CANCELLED)) {

			logger.debug("Creando evento UpdateThemeInspireCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new UpdateThemeInspireCancelledEvent().buildFrom(source);
		}

		if (type.equals(ThemeInspireEventTypes.DELETE_CANCELLED)) {

			logger.debug("Creando evento DeleteThemeInspireCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new DeleteThemeInspireCancelledEvent().buildFrom(source);
		}

		if (cancelledEvent != null) {

			cancelledEvent.setThemeInspire(themeInspire);
			cancelledEvent.setExceptionType(exceptionType);
			cancelledEvent.setArguments(exceptionArguments);
			return cancelledEvent;

		} else {

			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}
}
