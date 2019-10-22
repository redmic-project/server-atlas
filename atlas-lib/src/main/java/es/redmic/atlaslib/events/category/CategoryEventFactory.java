package es.redmic.atlaslib.events.category;

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

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.common.CategoryCancelledEvent;
import es.redmic.atlaslib.events.category.common.CategoryEvent;
import es.redmic.atlaslib.events.category.create.CategoryCreatedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryFailedEvent;
import es.redmic.atlaslib.events.category.delete.CategoryDeletedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckFailedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryFailedEvent;
import es.redmic.atlaslib.events.category.fail.CategoryRollbackEvent;
import es.redmic.atlaslib.events.category.update.CategoryUpdatedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryFailedEvent;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.fail.PrepareRollbackEvent;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;

public class CategoryEventFactory {

	private static Logger logger = LogManager.getLogger();

	public static Event getEvent(Event source, String type) {

		if (type.equals(CategoryEventTypes.DELETE)) {

			logger.debug("Creando evento DeleteCategoryEvent para: " + source.getAggregateId());
			return new DeleteCategoryEvent().buildFrom(source);
		}

		if (type.equals(CategoryEventTypes.DELETE_CHECKED)) {

			logger.debug("Creando evento DeleteCategoryCheckedEvent para: " + source.getAggregateId());
			return new DeleteCategoryCheckedEvent().buildFrom(source);
		}

		if (type.equals(CategoryEventTypes.CREATE_CONFIRMED)) {

			logger.debug("Creando evento CreateCategoryConfirmedEvent para: " + source.getAggregateId());

			return new CreateCategoryConfirmedEvent().buildFrom(source);
		}

		if (type.equals(CategoryEventTypes.UPDATE_CONFIRMED)) {

			logger.debug("Creando evento UpdateCategoryConfirmedEvent para: " + source.getAggregateId());

			return new UpdateCategoryConfirmedEvent().buildFrom(source);
		}

		if (type.equals(CategoryEventTypes.DELETE_CONFIRMED)) {

			logger.debug("Creando evento DeleteCategoryConfirmedEvent para: " + source.getAggregateId());

			return new DeleteCategoryConfirmedEvent().buildFrom(source);
		}

		if (type.equals(CategoryEventTypes.DELETED)) {

			logger.debug("Creando evento CategoryDeletedEvent para: " + source.getAggregateId());
			return new CategoryDeletedEvent().buildFrom(source);
		}

		logger.error("Tipo de evento no soportado");
		throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
	}

	public static Event getEvent(Event source, String type, CategoryDTO category) {

		if (type.equals(CategoryEventTypes.ROLLBACK)) {
			logger.debug("Creando evento CategoryRollbackEvent para: " + source.getAggregateId());
			CategoryRollbackEvent rollbackEvent = new CategoryRollbackEvent().buildFrom(source);
			rollbackEvent.setLastSnapshotItem(category);
			rollbackEvent.setFailEventType(((PrepareRollbackEvent) source).getFailEventType());
			return rollbackEvent;
		}

		CategoryEvent successfulEvent = null;

		if (type.equals(CategoryEventTypes.CREATED)) {
			logger.debug("Creando evento CategoryCreatedEvent para: " + source.getAggregateId());
			successfulEvent = new CategoryCreatedEvent().buildFrom(source);
		}

		if (type.equals(CategoryEventTypes.UPDATED)) {
			logger.debug("Creando evento CategoryUpdatedEvent para: " + source.getAggregateId());
			successfulEvent = new CategoryUpdatedEvent().buildFrom(source);
		}

		if (successfulEvent != null) {
			successfulEvent.setCategory(category);
			return successfulEvent;
		} else {
			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}

	public static Event getEvent(Event source, String type, String exceptionType,
			Map<String, String> exceptionArguments) {

		EventError failedEvent = null;

		if (type.equals(CategoryEventTypes.CREATE_FAILED)) {

			logger.debug("No se pudo crear Atlas type en la vista");
			failedEvent = new CreateCategoryFailedEvent().buildFrom(source);
		}
		if (type.equals(CategoryEventTypes.UPDATE_FAILED)) {

			logger.debug("No se pudo modificar Atlas type en la vista");
			failedEvent = new UpdateCategoryFailedEvent().buildFrom(source);
		}
		if (type.equals(CategoryEventTypes.DELETE_FAILED)) {

			logger.debug("No se pudo eliminar Atlas type de la vista");
			failedEvent = new DeleteCategoryFailedEvent().buildFrom(source);
		}

		if (type.equals(CategoryEventTypes.DELETE_CHECK_FAILED)) {

			logger.debug("Checkeo de eliminación fallido, el item está referenciado");
			failedEvent = new DeleteCategoryCheckFailedEvent().buildFrom(source);
		}

		if (type.equals(CategoryEventTypes.CREATE_CANCELLED)) {

			logger.debug("Enviando evento CreateCategoryCancelledEvent para: " + source.getAggregateId());
			failedEvent = new CreateCategoryCancelledEvent().buildFrom(source);
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

	public static Event getEvent(Event source, String type, CategoryDTO category, String exceptionType,
			Map<String, String> exceptionArguments) {

		CategoryCancelledEvent cancelledEvent = null;

		if (type.equals(CategoryEventTypes.UPDATE_CANCELLED)) {

			logger.debug("Creando evento UpdateCategoryCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new UpdateCategoryCancelledEvent().buildFrom(source);
		}

		if (type.equals(CategoryEventTypes.DELETE_CANCELLED)) {

			logger.debug("Creando evento DeleteCategoryCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new DeleteCategoryCancelledEvent().buildFrom(source);
		}

		if (cancelledEvent != null) {

			cancelledEvent.setCategory(category);
			cancelledEvent.setExceptionType(exceptionType);
			cancelledEvent.setArguments(exceptionArguments);
			return cancelledEvent;

		} else {

			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}
}
