package es.redmic.test.atlasview.integration.handler;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.create.CreateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryFailedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryFailedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryFailedEvent;
import es.redmic.atlasview.AtlasViewApplication;
import es.redmic.atlasview.mapper.category.CategoryESMapper;
import es.redmic.atlasview.model.category.Category;
import es.redmic.atlasview.model.layer.Layer;
import es.redmic.atlasview.repository.category.CategoryESRepository;
import es.redmic.atlasview.repository.layer.LayerESRepository;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.testutils.documentation.DocumentationViewBaseTest;
import es.redmic.testutils.utils.JsonToBeanTestUtil;

@SpringBootTest(classes = { AtlasViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@KafkaListener(topics = "${broker.topic.category}", groupId = "test")
@TestPropertySource(properties = { "schema.registry.port=19084" })
@DirtiesContext
@ActiveProfiles("test")
public class CategoryEventHandlerTest extends DocumentationViewBaseTest {

	private final String USER_ID = "1";

	@Autowired
	CategoryESRepository repository;

	@Autowired
	LayerESRepository layerRepository;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Value("${broker.topic.category}")
	private String CATEGORY_TOPIC;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1);

	@PostConstruct
	public void CreateCategoryFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void sendCategoryCreatedEvent_SaveItem_IfEventIsOk() throws Exception {

		CreateCategoryEvent event = getCreateCategoryEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(CATEGORY_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getCategory().getId());

		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.CREATE_CONFIRMED, confirm.getType());

		Category category = (Category) item.get_source();
		assertEquals(category.getId(), event.getAggregateId());
		assertEquals(category.getName(), event.getCategory().getName());
	}

	@Test
	public void sendCategoryUpdatedEvent_callUpdate_IfEventIsOk() throws Exception {

		UpdateCategoryEvent event = getUpdateCategoryEvent();

		repository.save(Mappers.getMapper(CategoryESMapper.class).map(event.getCategory()));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(CATEGORY_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getCategory().getId());

		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.UPDATE_CONFIRMED.toString(), confirm.getType());

		Category category = (Category) item.get_source();
		assertEquals(category.getId(), event.getAggregateId());
		assertEquals(category.getName(), event.getCategory().getName());
	}

	@Test(expected = ItemNotFoundException.class)
	public void sendCategoryDeleteEvent_callDelete_IfEventIsOk() throws Exception {

		DeleteCategoryEvent event = getDeleteCategoryEvent();

		repository.save(Mappers.getMapper(CategoryESMapper.class).map(getUpdateCategoryEvent().getCategory()));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(CATEGORY_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);
		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.DELETE_CONFIRMED.toString(), confirm.getType());

		repository.findById(event.getAggregateId());
	}

	@Test
	public void sendCategoryCreatedEvent_PublishCreateCategoryFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		CreateCategoryEvent event = getCreateCategoryEvent();

		repository.save(Mappers.getMapper(CategoryESMapper.class).map(event.getCategory()));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(CATEGORY_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(event.getCategory().getId());

		assertNotNull(fail);
		assertEquals(CategoryEventTypes.CREATE_FAILED.toString(), fail.getType());

		CreateCategoryFailedEvent createCategoryFailedEvent = (CreateCategoryFailedEvent) fail;

		Map<String, String> arguments = createCategoryFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(2, arguments.size());

		assertNotNull(arguments.get("id"));
		assertNotNull(arguments.get("name"));
	}

	@Test
	public void sendCategoryUpdateEvent_PublishUpdateCategoryFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		UpdateCategoryEvent event = getUpdateCategoryEvent();

		// @formatter:off
			CategoryDTO conflict = getCategory(),
					original = event.getCategory();
			// @formatter:on
		conflict.setId(original.getId() + "cpy");
		conflict.setName(original.getName() + "cpy");

		// Guarda el que se va a modificar
		repository.save(Mappers.getMapper(CategoryESMapper.class).map(original));

		// Guarda el que va a entrar en conflicto
		repository.save(Mappers.getMapper(CategoryESMapper.class).map(conflict));

		// Edita el nombre del que se va a modificar para entrar en conflicto
		original.setName(conflict.getName());
		event.setCategory(original);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(CATEGORY_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(original.getId());
		repository.delete(conflict.getId());

		assertNotNull(fail);
		assertEquals(CategoryEventTypes.UPDATE_FAILED.toString(), fail.getType());

		UpdateCategoryFailedEvent createCategoryFailedEvent = (UpdateCategoryFailedEvent) fail;

		Map<String, String> arguments = createCategoryFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(arguments.size(), 1);

		assertNotNull(arguments.get("name"));
	}

	@Test
	public void sendCategoryDeleteEvent_PublishDeleteCategoryFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		CategoryDTO category = getCategory();

		// Guarda el que se va a borrar
		repository.save(Mappers.getMapper(CategoryESMapper.class).map(category));

		Layer layer = (Layer) JsonToBeanTestUtil.getBean("/data/model/layer/layer.json", Layer.class);
		layer.getJoinIndex().setParent(category.getId());

		// guarda un hijo
		layerRepository.save(layer, category.getId());

		DeleteCategoryEvent event = getDeleteCategoryEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(CATEGORY_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event failed = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);
		assertNotNull(failed);
		assertEquals(CategoryEventTypes.DELETE_FAILED.toString(), failed.getType());

		layerRepository.delete(layer.getId(), category.getId());
		repository.delete(category.getId());
	}

	@KafkaHandler
	public void createTypeCategoryConfirmed(CreateCategoryConfirmedEvent createCategoryConfirmedEvent) {

		blockingQueue.offer(createCategoryConfirmedEvent);
	}

	@KafkaHandler
	public void createCategoryFailed(CreateCategoryFailedEvent createCategoryFailedEvent) {

		blockingQueue.offer(createCategoryFailedEvent);
	}

	@KafkaHandler
	public void updateCategoryConfirmed(UpdateCategoryConfirmedEvent updateCategoryConfirmedEvent) {

		blockingQueue.offer(updateCategoryConfirmedEvent);
	}

	@KafkaHandler
	public void updateCategoryFailed(UpdateCategoryFailedEvent updateCategoryFailedEvent) {

		blockingQueue.offer(updateCategoryFailedEvent);
	}

	@KafkaHandler
	public void deleteCategoryConfirmed(DeleteCategoryConfirmedEvent deleteCategoryConfirmedEvent) {

		blockingQueue.offer(deleteCategoryConfirmedEvent);
	}

	@KafkaHandler
	public void deleteCategoryFailed(DeleteCategoryFailedEvent deleteCategoryFailedEvent) {

		blockingQueue.offer(deleteCategoryFailedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

	protected CategoryDTO getCategory() {

		CategoryDTO category = new CategoryDTO();
		category.setId("category-a31bd54e-836e-4942-b10b-8d7bdbd6f196");
		category.setName("AIS");
		return category;
	}

	protected CreateCategoryEvent getCreateCategoryEvent() {

		CreateCategoryEvent createdEvent = new CreateCategoryEvent();
		createdEvent.setId(UUID.randomUUID().toString());
		createdEvent.setDate(DateTime.now());
		createdEvent.setType(CategoryEventTypes.CREATE);
		createdEvent.setCategory(getCategory());
		createdEvent.setAggregateId(createdEvent.getCategory().getId());
		createdEvent.setVersion(1);
		createdEvent.setSessionId(UUID.randomUUID().toString());
		createdEvent.setUserId(USER_ID);
		return createdEvent;
	}

	protected UpdateCategoryEvent getUpdateCategoryEvent() {

		UpdateCategoryEvent updatedEvent = new UpdateCategoryEvent();
		updatedEvent.setId(UUID.randomUUID().toString());
		updatedEvent.setDate(DateTime.now());
		updatedEvent.setType(CategoryEventTypes.UPDATE);
		CategoryDTO category = getCategory();
		category.setName(category.getName() + "2");
		updatedEvent.setCategory(category);
		updatedEvent.setAggregateId(updatedEvent.getCategory().getId());
		updatedEvent.setVersion(2);
		updatedEvent.setSessionId(UUID.randomUUID().toString());
		updatedEvent.setUserId(USER_ID);
		return updatedEvent;
	}

	protected DeleteCategoryEvent getDeleteCategoryEvent() {

		DeleteCategoryEvent deletedEvent = new DeleteCategoryEvent();
		deletedEvent.setId(UUID.randomUUID().toString());
		deletedEvent.setDate(DateTime.now());
		deletedEvent.setType(CategoryEventTypes.DELETE);
		deletedEvent.setAggregateId(getCategory().getId());
		deletedEvent.setVersion(3);
		deletedEvent.setSessionId(UUID.randomUUID().toString());
		deletedEvent.setUserId(USER_ID);
		return deletedEvent;
	}
}
