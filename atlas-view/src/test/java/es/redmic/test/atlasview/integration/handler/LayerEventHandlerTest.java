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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerFailedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerFailedEvent;
import es.redmic.atlaslib.events.layer.fail.LayerRollbackEvent;
import es.redmic.atlaslib.events.layer.partialupdate.themeinspire.UpdateThemeInspireInLayerEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerFailedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;
import es.redmic.atlasview.AtlasViewApplication;
import es.redmic.atlasview.mapper.layer.LayerESMapper;
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
@KafkaListener(topics = "${broker.topic.layer}", groupId = "test")
@TestPropertySource(properties = { "schema.registry.port=19184" })
@DirtiesContext
@ActiveProfiles("test")
public class LayerEventHandlerTest extends DocumentationViewBaseTest {

	private static final String PARENT_ID = "category-333";

	private static final String USER_ID = "1";

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
	public void sendCreateLayerEvent_SaveItem_IfEventIsOk() throws Exception {

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
	public void sendUpdateLayerEvent_callUpdate_IfEventIsOk() throws Exception {

		UpdateLayerEvent event = getUpdateLayerEvent();

		repository.save(Mappers.getMapper(LayerESMapper.class).map(event.getLayer()),
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

	@Test
	public void sendRefreshLayerEvent_callRefresh_IfEventIsOk() throws Exception {

		RefreshLayerEvent event = getRefreshLayerEvent();

		event.getLayer().setQueryable(false);

		LayerDTO layer = getLayer();

		repository.save(Mappers.getMapper(LayerESMapper.class).map(layer), layer.getParent().getId());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		RefreshLayerConfirmedEvent confirm = (RefreshLayerConfirmedEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(event.getLayer().getId(), layer.getParent().getId());

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.REFRESH_CONFIRMED.toString(), confirm.getType());

		LayerDTO result = confirm.getLayer();
		assertEquals(result.getId(), event.getAggregateId());
		assertEquals(result.getName(), event.getLayer().getName());
		// Se modifica bien el campo al refrescar
		assertFalse(layer.getQueryable().equals(result.getQueryable()));
	}

	@Test(expected = ItemNotFoundException.class)
	public void sendDeleteLayerEvent_callDelete_IfEventIsOk() throws Exception {

		DeleteLayerEvent event = getDeleteLayerEvent();

		LayerDTO original = getUpdateLayerEvent().getLayer();

		String parentId = original.getParent().getId();

		repository.save(Mappers.getMapper(LayerESMapper.class).map(original), parentId);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);
		assertNotNull(confirm);
		assertEquals(LayerEventTypes.DELETE_CONFIRMED.toString(), confirm.getType());

		repository.findById(event.getAggregateId(), parentId);
	}

	@Test
	public void sendCreateLayerEvent_PublishCreateLayerFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		CreateLayerEvent event = getCreateLayerEvent();

		repository.save(Mappers.getMapper(LayerESMapper.class).map(event.getLayer()),
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
	public void sendCreateLayerEvent_PublishCreateLayerFailedEvent_IfParentNotExist() throws Exception {

		CreateLayerEvent event = getCreateLayerEvent();

		String newParentId = "dddd";

		event.getLayer().getParent().setId(newParentId);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(fail);
		assertEquals(LayerEventTypes.CREATE_FAILED.toString(), fail.getType());

		CreateLayerFailedEvent createLayerFailedEvent = (CreateLayerFailedEvent) fail;

		Map<String, String> arguments = createLayerFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(1, arguments.size());

		assertNotNull(arguments.get("parentId"));
		assertEquals(newParentId, arguments.get("parentId"));
	}

	@Test
	public void sendUpdateLayerEvent_PublishUpdateLayerFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		UpdateLayerEvent event = getUpdateLayerEvent();

		// @formatter:off
			LayerDTO conflict = getLayer(),
					original = event.getLayer();
			// @formatter:on
		conflict.setId(original.getId() + "cpy");
		conflict.setName(original.getName() + "cpy");

		// Guarda el que se va a modificar
		repository.save(Mappers.getMapper(LayerESMapper.class).map(original), original.getParent().getId());

		// Guarda el que va a entrar en conflicto
		repository.save(Mappers.getMapper(LayerESMapper.class).map(conflict), conflict.getParent().getId());

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
	public void sendUpdateThemeInspireInLayerEvent_UpdateItem_IfEventIsOk() throws Exception {

		UpdateLayerEvent event = getUpdateLayerEvent();

		repository.save(Mappers.getMapper(LayerESMapper.class).map(event.getLayer()),
				event.getLayer().getParent().getId());

		UpdateThemeInspireInLayerEvent updateEvent = getUpdateThemeInspireInLayerEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC,
				updateEvent.getAggregateId(), updateEvent);
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
		assertEquals(layer.getThemeInspire().getName(), updateEvent.getThemeInspire().getName());
	}

	@Test
	public void sendUpdateLayerEvent_PublishCreateLayerFailedEvent_IfParentNotExist() throws Exception {

		UpdateLayerEvent event = getUpdateLayerEvent();

		String newParentId = "dddd";

		event.getLayer().getParent().setId(newParentId);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(fail);
		assertEquals(LayerEventTypes.UPDATE_FAILED.toString(), fail.getType());

		UpdateLayerFailedEvent updateLayerFailedEvent = (UpdateLayerFailedEvent) fail;

		Map<String, String> arguments = updateLayerFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(1, arguments.size());

		assertNotNull(arguments.get("parentId"));
		assertEquals(newParentId, arguments.get("parentId"));
	}

	@Test
	public void sendDeleteLayerEvent_PublishDeleteLayerFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		assertTrue(true);
	}

	// Rollback

	@Test(expected = ItemNotFoundException.class)
	public void sendLayerRollbackEvent_PublishCreateLayerFailedEvent_IfFailEventTypeIsCreate() throws Exception {

		LayerRollbackEvent event = getLayerRollbackEvent(LayerEventTypes.CREATE);

		Layer lastSnapshotLayer = Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem());

		event.setLastSnapshotItem(null);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.CREATE_FAILED, confirm.getType());

		repository.findById(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent()).get_source();
	}

	@Test(expected = ItemNotFoundException.class)
	public void sendLayerRollbackEvent_PublishCreateLayerFailedEventAndDoRollback_IfFailEventTypeIsCreateAndLastSnapshotItemIsDifferent()
			throws Exception {

		LayerRollbackEvent event = getLayerRollbackEvent(LayerEventTypes.CREATE);

		Layer lastSnapshotLayer = Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem());

		event.setLastSnapshotItem(null);

		repository.save(lastSnapshotLayer, lastSnapshotLayer.getJoinIndex().getParent());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.CREATE_FAILED, confirm.getType());

		repository.findById(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent()).get_source();
	}

	@Test
	public void sendLayerRollbackEvent_PublishUpdateLayerFailedEvent_IfFailEventTypeIsUpdate() throws Exception {

		LayerRollbackEvent event = getLayerRollbackEvent(LayerEventTypes.UPDATE);

		Layer lastSnapshotLayer = Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem());

		repository.save(lastSnapshotLayer, lastSnapshotLayer.getJoinIndex().getParent());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.UPDATE_FAILED, confirm.getType());

		Layer newLayer = (Layer) repository
				.findById(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent()).get_source();
		assertEquals(lastSnapshotLayer, newLayer);

		repository.delete(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent());
	}

	@Test
	public void sendLayerRollbackEvent_PublishUpdateLayerFailedEventAndDoRollback_IfFailEventTypeIsUpdateAndLastSnapshotItemIsDifferent()
			throws Exception {

		LayerRollbackEvent event = getLayerRollbackEvent(LayerEventTypes.UPDATE);

		Layer lastSnapshotLayer = Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem());

		lastSnapshotLayer.setUpdated(lastSnapshotLayer.getUpdated().plusDays(1));
		lastSnapshotLayer.setName("other");

		assertNotEquals(event.getLastSnapshotItem(), lastSnapshotLayer);

		repository.save(lastSnapshotLayer, lastSnapshotLayer.getJoinIndex().getParent());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.UPDATE_FAILED, confirm.getType());

		Layer newLayer = (Layer) repository
				.findById(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent()).get_source();
		assertEquals(Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem()), newLayer);

		repository.delete(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent());
	}

	@Test
	public void sendLayerRollbackEvent_PublishRefreshLayerFailedEvent_IfFailEventTypeIsRefresh() throws Exception {

		LayerRollbackEvent event = getLayerRollbackEvent(LayerEventTypes.REFRESH);

		Layer lastSnapshotLayer = Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem());

		repository.save(lastSnapshotLayer, lastSnapshotLayer.getJoinIndex().getParent());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.REFRESH_FAILED, confirm.getType());

		Layer newLayer = (Layer) repository
				.findById(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent()).get_source();
		assertEquals(lastSnapshotLayer, newLayer);

		repository.delete(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent());
	}

	@Test
	public void sendLayerRollbackEvent_PublishRefreshLayerFailedEventAndDoRollback_IfFailEventTypeIsRefreshAndLastSnapshotItemIsDifferent()
			throws Exception {

		LayerRollbackEvent event = getLayerRollbackEvent(LayerEventTypes.REFRESH);

		Layer lastSnapshotLayer = Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem());

		lastSnapshotLayer.setUpdated(lastSnapshotLayer.getUpdated().plusDays(1));
		lastSnapshotLayer.setName("other");

		assertNotEquals(event.getLastSnapshotItem(), lastSnapshotLayer);

		repository.save(lastSnapshotLayer, lastSnapshotLayer.getJoinIndex().getParent());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.REFRESH_FAILED, confirm.getType());

		Layer newLayer = (Layer) repository
				.findById(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent()).get_source();
		assertEquals(Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem()), newLayer);

		repository.delete(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent());
	}

	@Test
	public void sendLayerRollbackEvent_PublishDeleteLayerFailedEvent_IfFailEventTypeIsDelete() throws Exception {

		LayerRollbackEvent event = getLayerRollbackEvent(LayerEventTypes.DELETE);

		Layer lastSnapshotLayer = Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem());

		repository.save(lastSnapshotLayer, lastSnapshotLayer.getJoinIndex().getParent());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.DELETE_FAILED, confirm.getType());

		Layer newLayer = (Layer) repository
				.findById(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent()).get_source();
		assertEquals(lastSnapshotLayer, newLayer);

		repository.delete(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent());
	}

	@Test
	public void sendLayerRollbackEvent_PublishDeleteLayerFailedEventAndDoRollback_IfFailEventTypeIsDeleteAndLastSnapshotItemIsDifferent()
			throws Exception {

		LayerRollbackEvent event = getLayerRollbackEvent(LayerEventTypes.DELETE);

		Layer lastSnapshotLayer = Mappers.getMapper(LayerESMapper.class).map(event.getLastSnapshotItem());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(LAYER_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.DELETE_FAILED, confirm.getType());

		Layer newLayer = (Layer) repository
				.findById(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent()).get_source();
		assertEquals(lastSnapshotLayer, newLayer);

		repository.delete(lastSnapshotLayer.getId(), lastSnapshotLayer.getJoinIndex().getParent());
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
	public void refreshLayerFailed(RefreshLayerFailedEvent refreshLayerFailedEvent) {

		blockingQueue.offer(refreshLayerFailedEvent);
	}

	@KafkaHandler
	public void deleteLayerConfirmed(DeleteLayerConfirmedEvent deleteLayerConfirmedEvent) {

		blockingQueue.offer(deleteLayerConfirmedEvent);
	}

	@KafkaHandler
	public void deleteLayerFailed(DeleteLayerFailedEvent deleteLayerFailedEvent) {

		blockingQueue.offer(deleteLayerFailedEvent);
	}

	@KafkaHandler
	public void refreshLayerConfirmed(RefreshLayerConfirmedEvent refreshLayerConfirmedEvent) {

		blockingQueue.offer(refreshLayerConfirmedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

	protected LayerDTO getLayer() {

		try {
			return (LayerDTO) JsonToBeanTestUtil.getBean("/data/dto/layer/layer.json", LayerDTO.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected CreateLayerEvent getCreateLayerEvent() {

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

	protected UpdateLayerEvent getUpdateLayerEvent() {

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

	protected UpdateThemeInspireInLayerEvent getUpdateThemeInspireInLayerEvent() {

		UpdateThemeInspireInLayerEvent event = new UpdateThemeInspireInLayerEvent().buildFrom(getUpdateLayerEvent());

		LayerDTO layer = getLayer();
		layer.getThemeInspire().setName("Nombre cambiado");
		event.setThemeInspire(layer.getThemeInspire());
		event.setAggregateId(layer.getId());
		event.setVersion(3);
		event.setSessionId(UUID.randomUUID().toString());
		event.setUserId(USER_ID);

		return event;
	}

	protected RefreshLayerEvent getRefreshLayerEvent() {

		RefreshLayerEvent refreshEvent = new RefreshLayerEvent();

		refreshEvent.setId(UUID.randomUUID().toString());
		refreshEvent.setDate(DateTime.now());
		refreshEvent.setType(LayerEventTypes.UPDATE);
		LayerWMSDTO layer;
		try {
			layer = (LayerWMSDTO) JsonToBeanTestUtil.getBean("/data/dto/layer/layerWMS.json", LayerWMSDTO.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		layer.setName(layer.getName() + "2");
		refreshEvent.setLayer(layer);
		refreshEvent.setAggregateId(refreshEvent.getLayer().getId());
		refreshEvent.setVersion(2);
		refreshEvent.setSessionId(UUID.randomUUID().toString());
		refreshEvent.setUserId(USER_ID);

		return refreshEvent;
	}

	protected DeleteLayerEvent getDeleteLayerEvent() {

		DeleteLayerEvent deletedEvent = new DeleteLayerEvent();
		deletedEvent.setId(UUID.randomUUID().toString());
		deletedEvent.setDate(DateTime.now());
		deletedEvent.setType(LayerEventTypes.DELETE);
		deletedEvent.setAggregateId(getLayer().getId());
		deletedEvent.setVersion(3);
		deletedEvent.setSessionId(UUID.randomUUID().toString());
		deletedEvent.setUserId(USER_ID);
		return deletedEvent;
	}

	public LayerRollbackEvent getLayerRollbackEvent(String failEventType) {

		LayerRollbackEvent event = new LayerRollbackEvent().buildFrom(getCreateLayerEvent());

		event.setFailEventType(failEventType);
		event.setLastSnapshotItem(getLayer());

		return event;
	}
}
