package es.redmic.atlasview.controller.layer;

/*-
 * #%L
 * Atlas-query-endpoint
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.LayerEventFactory;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.atlasview.config.MapperScanBean;
import es.redmic.atlasview.model.layer.Layer;
import es.redmic.atlasview.service.layer.LayerESService;
import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.data.controller.DataController;

@Controller
@RequestMapping(value = "${controller.mapping.LAYER}")
@KafkaListener(topics = "${broker.topic.layer}")
public class LayerController extends DataController<Layer, LayerDTO, SimpleQueryDTO> {

	@Value("${broker.topic.layer}")
	private String layer_topic;

	@Autowired
	protected MapperScanBean mapper;

	LayerESService service;

	public LayerController(LayerESService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(CreateLayerEvent event) {

		EventApplicationResult result = null;

		String parentId = event.getLayer().getParent() != null ? event.getLayer().getParent().getId() : null;

		try {
			result = service.save(mapper.getMapperFacade().map(event.getLayer(), Layer.class), parentId);
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(LayerEventFactory.getEvent(event, LayerEventTypes.CREATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), layer_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new CreateLayerConfirmedEvent().buildFrom(event), layer_topic);
		} else {
			publishFailedEvent(LayerEventFactory.getEvent(event, LayerEventTypes.CREATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), layer_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateLayerEvent event) {

		EventApplicationResult result = null;

		String parentId = event.getLayer().getParent() != null ? event.getLayer().getParent().getId() : null;

		try {
			result = service.update(mapper.getMapperFacade().map(event.getLayer(), Layer.class), parentId);
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(LayerEventFactory.getEvent(event, LayerEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), layer_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new UpdateLayerConfirmedEvent().buildFrom(event), layer_topic);
		} else {
			publishFailedEvent(LayerEventFactory.getEvent(event, LayerEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), layer_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteLayerEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId(), event.getParentId());
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(LayerEventFactory.getEvent(event, LayerEventTypes.DELETE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), layer_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new DeleteLayerConfirmedEvent().buildFrom(event), layer_topic);
		} else {
			publishFailedEvent(LayerEventFactory.getEvent(event, LayerEventTypes.DELETE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), layer_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}