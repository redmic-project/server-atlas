package es.redmic.atlaslib.events.layer;

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

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.common.LayerCancelledEvent;
import es.redmic.atlaslib.events.layer.common.LayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerFailedEvent;
import es.redmic.atlaslib.events.layer.create.LayerCreatedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckFailedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerFailedEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;

public class LayerEventFactory {

	private static Logger logger = LogManager.getLogger();

	public static Event getEvent(Event source, String type) {

		if (type.equals(LayerEventTypes.DELETE)) {

			logger.debug("Creando evento DeleteLayerEvent para: " + source.getAggregateId());
			return new DeleteLayerEvent().buildFrom(source);
		}

		if (type.equals(LayerEventTypes.DELETE_CHECKED)) {

			logger.debug("Creando evento DeleteLayerCheckedEvent para: " + source.getAggregateId());
			return new DeleteLayerCheckedEvent().buildFrom(source);
		}

		if (type.equals(LayerEventTypes.CREATE_CONFIRMED)) {

			logger.debug("Creando evento CreateLayerConfirmedEvent para: " + source.getAggregateId());

			return new CreateLayerConfirmedEvent().buildFrom(source);
		}

		if (type.equals(LayerEventTypes.UPDATE_CONFIRMED)) {

			logger.debug("Creando evento UpdateLayerConfirmedEvent para: " + source.getAggregateId());

			return new UpdateLayerConfirmedEvent().buildFrom(source);
		}

		if (type.equals(LayerEventTypes.DELETE_CONFIRMED)) {

			logger.debug("Creando evento DeleteLayerConfirmedEvent para: " + source.getAggregateId());

			return new DeleteLayerConfirmedEvent().buildFrom(source);
		}

		if (type.equals(LayerEventTypes.DELETED)) {

			logger.debug("Creando evento LayerDeletedEvent para: " + source.getAggregateId());
			return new LayerDeletedEvent().buildFrom(source);
		}

		logger.error("Tipo de evento no soportado");
		throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
	}

	public static Event getEvent(Event source, String type, LayerDTO layer) {

		LayerEvent successfulEvent = null;

		if (type.equals(LayerEventTypes.CREATED)) {
			logger.debug("Creando evento LayerCreatedEvent para: " + source.getAggregateId());
			successfulEvent = new LayerCreatedEvent().buildFrom(source);
		}

		if (type.equals(LayerEventTypes.UPDATED)) {
			logger.debug("Creando evento LayerUpdatedEvent para: " + source.getAggregateId());
			successfulEvent = new LayerUpdatedEvent().buildFrom(source);
		}

		if (successfulEvent != null) {
			successfulEvent.setLayer(layer);
			return successfulEvent;
		} else {
			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}

	public static Event getEvent(Event source, String type, String exceptionType,
			Map<String, String> exceptionArguments) {

		EventError failedEvent = null;

		if (type.equals(LayerEventTypes.CREATE_FAILED)) {

			logger.debug("No se pudo crear Atlas type en la vista");
			failedEvent = new CreateLayerFailedEvent().buildFrom(source);
		}
		if (type.equals(LayerEventTypes.UPDATE_FAILED)) {

			logger.debug("No se pudo modificar Atlas type en la vista");
			failedEvent = new UpdateLayerFailedEvent().buildFrom(source);
		}
		if (type.equals(LayerEventTypes.DELETE_FAILED)) {

			logger.debug("No se pudo eliminar Atlas type de la vista");
			failedEvent = new DeleteLayerFailedEvent().buildFrom(source);
		}

		if (type.equals(LayerEventTypes.DELETE_CHECK_FAILED)) {

			logger.debug("Checkeo de eliminación fallido, el item está referenciado");
			failedEvent = new DeleteLayerCheckFailedEvent().buildFrom(source);
		}

		if (type.equals(LayerEventTypes.CREATE_CANCELLED)) {

			logger.debug("Enviando evento CreateLayerCancelledEvent para: " + source.getAggregateId());
			failedEvent = new CreateLayerCancelledEvent().buildFrom(source);
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

	public static Event getEvent(Event source, String type, LayerDTO layer, String exceptionType,
			Map<String, String> exceptionArguments) {

		LayerCancelledEvent cancelledEvent = null;

		if (type.equals(LayerEventTypes.UPDATE_CANCELLED)) {

			logger.debug("Creando evento UpdateLayerCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new UpdateLayerCancelledEvent().buildFrom(source);
		}

		if (type.equals(LayerEventTypes.DELETE_CANCELLED)) {

			logger.debug("Creando evento DeleteLayerCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new DeleteLayerCancelledEvent().buildFrom(source);
		}

		if (cancelledEvent != null) {

			cancelledEvent.setLayer(layer);
			cancelledEvent.setExceptionType(exceptionType);
			cancelledEvent.setArguments(exceptionArguments);
			return cancelledEvent;

		} else {

			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}
}
