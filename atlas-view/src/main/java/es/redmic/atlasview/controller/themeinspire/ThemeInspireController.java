package es.redmic.atlasview.controller.themeinspire;

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

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventFactory;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.fail.ThemeInspireRollbackEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.atlasview.mapper.themeinspire.ThemeInspireESMapper;
import es.redmic.atlasview.model.themeinspire.ThemeInspire;
import es.redmic.atlasview.service.themeinspire.ThemeInspireESService;
import es.redmic.brokerlib.avro.fail.RollbackFailedEvent;
import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.data.controller.DataController;

@Controller
@RequestMapping(value = "${controller.mapping.THEME_INSPIRE}")
@KafkaListener(topics = "${broker.topic.theme-inspire}")
public class ThemeInspireController extends DataController<ThemeInspire, ThemeInspireDTO, SimpleQueryDTO> {

	@Value("${broker.topic.theme-inspire}")
	private String theme_inspire_topic;

	ThemeInspireESService service;

	public ThemeInspireController(ThemeInspireESService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(CreateThemeInspireEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.save(Mappers.getMapper(ThemeInspireESMapper.class).map(event.getThemeInspire()));
		} catch (Exception e) {
			publishFailedEvent(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.CREATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), theme_inspire_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new CreateThemeInspireConfirmedEvent().buildFrom(event), theme_inspire_topic);
		} else {
			publishFailedEvent(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.CREATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), theme_inspire_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateThemeInspireEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.update(Mappers.getMapper(ThemeInspireESMapper.class).map(event.getThemeInspire()));
		} catch (Exception e) {
			publishFailedEvent(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), theme_inspire_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new UpdateThemeInspireConfirmedEvent().buildFrom(event), theme_inspire_topic);
		} else {
			publishFailedEvent(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), theme_inspire_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteThemeInspireEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId());
		} catch (Exception e) {
			publishFailedEvent(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.DELETE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), theme_inspire_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new DeleteThemeInspireConfirmedEvent().buildFrom(event), theme_inspire_topic);
		} else {
			publishFailedEvent(ThemeInspireEventFactory.getEvent(event, ThemeInspireEventTypes.DELETE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), theme_inspire_topic);
		}
	}

	@KafkaHandler
	public void listen(ThemeInspireRollbackEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.rollback(Mappers.getMapper(ThemeInspireESMapper.class).map(event.getLastSnapshotItem()),
					event.getAggregateId());
		} catch (Exception e) {
			publishFailedEvent(new RollbackFailedEvent(event.getFailEventType()).buildFrom(event), theme_inspire_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(
					ThemeInspireEventFactory.getEvent(event,
							ThemeInspireEventTypes.getEventFailedTypeByActionType(event.getFailEventType())),
					theme_inspire_topic);
		} else {
			publishFailedEvent(new RollbackFailedEvent(event.getFailEventType()).buildFrom(event), theme_inspire_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}
