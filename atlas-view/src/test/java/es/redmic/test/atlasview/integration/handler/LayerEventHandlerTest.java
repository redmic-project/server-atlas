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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerFailedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerFailedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;
import es.redmic.atlasview.AtlasViewApplication;
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
import es.redmic.viewlib.config.MapperScanBeanItfc;

@SpringBootTest(classes = { AtlasViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@KafkaListener(topics = "${broker.topic.layer}", groupId = "test")
@TestPropertySource(properties = { "schema.registry.port=19184" })
@DirtiesContext
@ActiveProfiles("test")
public class LayerEventHandlerTest extends DocumentationViewBaseTest {

	private static final String PARENT_ID = "category-333";

	private static final String USER_ID = "1";

	@Autowired
	MapperScanBeanItfc mapper;

	@Autowired
	LayerESRepository repository;

	@Autowired
	CategoryESRepository categoryRepository;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Value("${broker.topic.layer}")
	private String LAYER_TOPIC;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1);

	@PostConstruct
	public void CreateLayerFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@Override
	@Before
	public void setUp() {

		Category category = new Category();
		category.setId(PARENT_ID);
		category.setName("Sistema de cuadrículas geográficas");

		categoryRepository.save(category);
	}

	@After
	public void clean() {
		repository.delete(PARENT_ID);
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void sendLayerCreatedEvent_SaveItem_IfEventIsOk() throws Exception {

		CreateLayerEvent event = getCreateLayerEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId(), event.getLayer().getParent().getId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getLayer().getId(), event.getLayer().getParent().getId());

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.CREATE_CONFIRMED, confirm.getType());

		Layer layer = (Layer) item.get_source();
		assertEquals(layer.getId(), event.getAggregateId());
		assertEquals(layer.getName(), event.getLayer().getName());
	}

	@Test
	public void sendLayerUpdatedEvent_callUpdate_IfEventIsOk() throws Exception {

		UpdateLayerEvent event = getUpdateLayerEvent();

		repository.save(mapper.getMapperFacade().map(event.getLayer(), Layer.class),
				event.getLayer().getParent().getId());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId(), event.getLayer().getParent().getId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getLayer().getId(), event.getLayer().getParent().getId());

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.UPDATE_CONFIRMED.toString(), confirm.getType());

		Layer layer = (Layer) item.get_source();
		assertEquals(layer.getId(), event.getAggregateId());
		assertEquals(layer.getName(), event.getLayer().getName());
	}

	@Test(expected = ItemNotFoundException.class)
	public void sendLayerDeleteEvent_callDelete_IfEventIsOk() throws Exception {

		DeleteLayerEvent event = getDeleteLayerEvent();

		LayerDTO original = getUpdateLayerEvent().getLayer();

		repository.save(mapper.getMapperFacade().map(original, Layer.class), original.getParent().getId());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);
		assertNotNull(confirm);
		assertEquals(LayerEventTypes.DELETE_CONFIRMED.toString(), confirm.getType());

		repository.findById(event.getAggregateId());
	}

	@Test
	public void sendLayerCreatedEvent_PublishCreateLayerFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		CreateLayerEvent event = getCreateLayerEvent();

		repository.save(mapper.getMapperFacade().map(event.getLayer(), Layer.class),
				event.getLayer().getParent().getId());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(event.getLayer().getId(), event.getLayer().getParent().getId());

		assertNotNull(fail);
		assertEquals(LayerEventTypes.CREATE_FAILED.toString(), fail.getType());

		CreateLayerFailedEvent createLayerFailedEvent = (CreateLayerFailedEvent) fail;

		Map<String, String> arguments = createLayerFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(3, arguments.size());

		assertNotNull(arguments.get("id"));
		assertNotNull(arguments.get("name"));
		assertNotNull(arguments.get("urlSource"));
	}

	@Test
	public void sendLayerUpdateEvent_PublishUpdateLayerFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		UpdateLayerEvent event = getUpdateLayerEvent();

		// @formatter:off
			LayerDTO conflict = getLayer(),
					original = event.getLayer();
			// @formatter:on
		conflict.setId(original.getId() + "cpy");
		conflict.setName(original.getName() + "cpy");

		// Guarda el que se va a modificar
		repository.save(mapper.getMapperFacade().map(original, Layer.class), original.getParent().getId());

		// Guarda el que va a entrar en conflicto
		repository.save(mapper.getMapperFacade().map(conflict, Layer.class), conflict.getParent().getId());

		// Edita el nombre del que se va a modificar para entrar en conflicto (mismo
		// nombre y urlsource con distinto id)
		original.setName(conflict.getName());
		event.setLayer(original);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(original.getId(), original.getParent().getId());
		repository.delete(conflict.getId(), conflict.getParent().getId());

		assertNotNull(fail);
		assertEquals(LayerEventTypes.UPDATE_FAILED.toString(), fail.getType());

		UpdateLayerFailedEvent createLayerFailedEvent = (UpdateLayerFailedEvent) fail;

		Map<String, String> arguments = createLayerFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(arguments.size(), 3);

		assertNotNull(arguments.get("id"));
		assertNotNull(arguments.get("name"));
		assertNotNull(arguments.get("urlSource"));
	}

	@Test
	public void sendLayerDeleteEvent_PublishDeleteLayerFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		// TODO: Implementar cuando se metan las referencias en la vista.
		assertTrue(true);
	}

	@KafkaHandler
	public void createTypeLayerConfirmed(CreateLayerConfirmedEvent createLayerConfirmedEvent) {

		blockingQueue.offer(createLayerConfirmedEvent);
	}

	@KafkaHandler
	public void createLayerFailed(CreateLayerFailedEvent createLayerFailedEvent) {

		blockingQueue.offer(createLayerFailedEvent);
	}

	@KafkaHandler
	public void updateLayerConfirmed(UpdateLayerConfirmedEvent updateLayerConfirmedEvent) {

		blockingQueue.offer(updateLayerConfirmedEvent);
	}

	@KafkaHandler
	public void updateLayerFailed(UpdateLayerFailedEvent updateLayerFailedEvent) {

		blockingQueue.offer(updateLayerFailedEvent);
	}

	@KafkaHandler
	public void deleteLayerConfirmed(DeleteLayerConfirmedEvent deleteLayerConfirmedEvent) {

		blockingQueue.offer(deleteLayerConfirmedEvent);
	}

	@KafkaHandler
	public void deleteLayerFailed(DeleteLayerFailedEvent deleteLayerFailedEvent) {

		blockingQueue.offer(deleteLayerFailedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

	protected LayerDTO getLayer() throws IOException {

		return (LayerDTO) JsonToBeanTestUtil.getBean("/data/dto/layer/layer.json", LayerDTO.class);
	}

	protected CreateLayerEvent getCreateLayerEvent() throws IOException {

		CreateLayerEvent createdEvent = new CreateLayerEvent();
		createdEvent.setId(UUID.randomUUID().toString());
		createdEvent.setDate(DateTime.now());
		createdEvent.setType(LayerEventTypes.CREATE);
		createdEvent.setLayer(getLayer());
		createdEvent.setAggregateId(createdEvent.getLayer().getId());
		createdEvent.setVersion(1);
		createdEvent.setSessionId(UUID.randomUUID().toString());
		createdEvent.setUserId(USER_ID);
		return createdEvent;
	}

	protected UpdateLayerEvent getUpdateLayerEvent() throws IOException {

		UpdateLayerEvent updatedEvent = new UpdateLayerEvent();
		updatedEvent.setId(UUID.randomUUID().toString());
		updatedEvent.setDate(DateTime.now());
		updatedEvent.setType(LayerEventTypes.UPDATE);
		LayerDTO layer = getLayer();
		layer.setName(layer.getName() + "2");
		updatedEvent.setLayer(layer);
		updatedEvent.setAggregateId(updatedEvent.getLayer().getId());
		updatedEvent.setVersion(2);
		updatedEvent.setSessionId(UUID.randomUUID().toString());
		updatedEvent.setUserId(USER_ID);
		return updatedEvent;
	}

	protected DeleteLayerEvent getDeleteLayerEvent() throws IOException {

		DeleteLayerEvent deletedEvent = new DeleteLayerEvent();
		deletedEvent.setId(UUID.randomUUID().toString());
		deletedEvent.setDate(DateTime.now());
		deletedEvent.setType(LayerEventTypes.DELETE);
		deletedEvent.setAggregateId(getLayer().getId());
		deletedEvent.setVersion(3);
		deletedEvent.setSessionId(UUID.randomUUID().toString());
		deletedEvent.setUserId(USER_ID);
		deletedEvent.setParentId(PARENT_ID);
		return deletedEvent;
	}
}