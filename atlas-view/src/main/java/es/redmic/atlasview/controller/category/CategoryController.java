package es.redmic.atlasview.controller.category;

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

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.CategoryEventFactory;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.create.CreateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.atlasview.mapper.category.CategoryESMapper;
import es.redmic.atlasview.model.category.Category;
import es.redmic.atlasview.service.category.CategoryESService;
import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.data.controller.DataController;

@Controller
@RequestMapping(value = "${controller.mapping.CATEGORY}")
@KafkaListener(topics = "${broker.topic.category}")
public class CategoryController extends DataController<Category, CategoryDTO, SimpleQueryDTO> {

	@Value("${broker.topic.category}")
	private String category_topic;

	CategoryESService service;

	public CategoryController(CategoryESService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(CreateCategoryEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.save(Mappers.getMapper(CategoryESMapper.class).map(event.getCategory()));
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(CategoryEventFactory.getEvent(event, CategoryEventTypes.CREATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), category_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new CreateCategoryConfirmedEvent().buildFrom(event), category_topic);
		} else {
			publishFailedEvent(CategoryEventFactory.getEvent(event, CategoryEventTypes.CREATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), category_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateCategoryEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.update(Mappers.getMapper(CategoryESMapper.class).map(event.getCategory()));
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(CategoryEventFactory.getEvent(event, CategoryEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), category_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new UpdateCategoryConfirmedEvent().buildFrom(event), category_topic);
		} else {
			publishFailedEvent(CategoryEventFactory.getEvent(event, CategoryEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), category_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteCategoryEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId());
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(CategoryEventFactory.getEvent(event, CategoryEventTypes.DELETE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), category_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new DeleteCategoryConfirmedEvent().buildFrom(event), category_topic);
		} else {
			publishFailedEvent(CategoryEventFactory.getEvent(event, CategoryEventTypes.DELETE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), category_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}
