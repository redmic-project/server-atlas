package es.redmic.test.atlascommands.integration.category;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
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

import es.redmic.atlascommands.AtlasCommandsApplication;
import es.redmic.atlascommands.handler.CategoryCommandHandler;
import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.create.CategoryCreatedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryFailedEvent;
import es.redmic.atlaslib.events.category.delete.CategoryDeletedEvent;
import es.redmic.atlaslib.events.category.delete.CheckDeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckFailedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryFailedEvent;
import es.redmic.atlaslib.events.category.fail.CategoryRollbackEvent;
import es.redmic.atlaslib.events.category.update.CategoryUpdatedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryFailedEvent;
import es.redmic.atlaslib.unit.utils.CategoryDataUtil;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.fail.PrepareRollbackEvent;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.exception.data.DeleteItemException;
import es.redmic.exception.data.ItemAlreadyExistException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.test.atlascommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AtlasCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.category}", groupId = "CategoryCommandHandlerTest")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CategoryCommandHandler",
		"schema.registry.port=19099", "rest.eventsource.timeout.ms=20000" })
public class CategoryCommandHandlerTest extends KafkaBaseIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private static final String code = UUID.randomUUID().toString();

	@Value("${broker.topic.category}")
	private String category_topic;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	CategoryCommandHandler categoryCommandHandler;

	@PostConstruct
	public void CategoryCommandHandlerTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@Before
	public void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	// Success cases

	// Envía un evento de confirmación de creación y debe provocar un evento Created
	// con el item dentro
	@Test
	public void createCategoryConfirmedEvent_SendCategoryCreatedEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createCategoryConfirmedEvent");

		// Envía create para meterlo en el stream
		CreateCategoryEvent createCategoryEvent = CategoryDataUtil.getCreateEvent(code + "1");
		kafkaTemplate.send(category_topic, createCategoryEvent.getAggregateId(), createCategoryEvent);

		// Envía confirmed y espera un evento de created con el category original dentro
		CreateCategoryConfirmedEvent event = CategoryDataUtil.getCreateCategoryConfirmedEvent(code + "1");

		kafkaTemplate.send(category_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(120, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.CREATED, confirm.getType());

		assertEquals(createCategoryEvent.getCategory(), ((CategoryCreatedEvent) confirm).getCategory());
	}

	// Envía un evento de confirmación de modificación y debe provocar un evento
	// Updated con el item dentro
	@Test
	public void updateCategoryConfirmedEvent_SendCategoryUpdatedEvent_IfReceivesSuccess() throws InterruptedException {

		logger.debug("----> updateCategoryConfirmedEvent");

		// Envía update para meterlo en el stream
		UpdateCategoryEvent updateCategoryEvent = CategoryDataUtil.getUpdateEvent(code + "2");
		kafkaTemplate.send(category_topic, updateCategoryEvent.getAggregateId(), updateCategoryEvent);

		// Envía confirmed y espera un evento de updated con el category original dentro
		UpdateCategoryConfirmedEvent event = CategoryDataUtil.getUpdateCategoryConfirmedEvent(code + "2");
		kafkaTemplate.send(category_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.UPDATED, confirm.getType());
		assertEquals(updateCategoryEvent.getCategory(), ((CategoryUpdatedEvent) confirm).getCategory());
	}

	// Envía un evento de comprobación de que el elemento puede ser borrado y debe
	// provocar un evento DeleteCategoryCheckedEvent ya que no está referenciado
	@Test
	public void checkDeleteCategoryEvent_SendDeleteCategoryCheckedEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> CheckDeleteCategoryEvent");

		CheckDeleteCategoryEvent event = CategoryDataUtil.getCheckDeleteCategoryEvent(code + "3a");

		kafkaTemplate.send(category_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.DELETE_CHECKED, confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}

	// Envía un evento de confirmación de borrado y debe provocar un evento Deleted
	@Test
	public void deleteCategoryConfirmedEvent_SendCategoryDeletedEvent_IfReceivesSuccess() throws InterruptedException {

		logger.debug("----> deleteCategoryConfirmedEvent");

		DeleteCategoryConfirmedEvent event = CategoryDataUtil.getDeleteCategoryConfirmedEvent(code + "3");

		kafkaTemplate.send(category_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.DELETED, confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}

	// Fail cases

	// Envía un evento de error de creación y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = ItemAlreadyExistException.class)
	public void createCategoryFailedEvent_SendCategoryCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createCategoryFailedEvent");

		CreateCategoryFailedEvent event = CategoryDataUtil.getCreateCategoryFailedEvent(code + "4");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<CategoryDTO> completableFuture = Whitebox.invokeMethod(categoryCommandHandler,
				"getCompletableFeature", event.getSessionId());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(category_topic, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(categoryCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.CREATE_CANCELLED, confirm.getType());
	}

	// Envía un evento de error de modificación y debe provocar un evento Cancelled
	// con el item dentro
	@Test(expected = ItemNotFoundException.class)
	public void updateCategoryFailedEvent_SendCategoryCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> updateCategoryFailedEvent");

		// Envía created para meterlo en el stream y lo saca de la cola
		CategoryCreatedEvent categoryCreatedEvent = CategoryDataUtil.getCategoryCreatedEvent(code + "5");
		categoryCreatedEvent.getCategory().setName("Nombre erroneo al crearlo");
		categoryCreatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(category_topic, categoryCreatedEvent.getAggregateId(), categoryCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		CategoryUpdatedEvent categoryUpdateEvent = CategoryDataUtil.getCategoryUpdatedEvent(code + "5");
		categoryUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(category_topic, categoryUpdateEvent.getAggregateId(), categoryUpdateEvent);
		blockingQueue.poll(20, TimeUnit.SECONDS);

		Thread.sleep(8000);

		// Envía failed y espera un evento de cancelled con el category original dentro
		UpdateCategoryFailedEvent event = CategoryDataUtil.getUpdateCategoryFailedEvent(code + "5");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<CategoryDTO> completableFuture = Whitebox.invokeMethod(categoryCommandHandler,
				"getCompletableFeature", event.getSessionId());

		kafkaTemplate.send(category_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(categoryCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.UPDATE_CANCELLED, confirm.getType());
		assertEquals(categoryUpdateEvent.getCategory(), ((UpdateCategoryCancelledEvent) confirm).getCategory());
	}

	// Envía un evento de error de borrado y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = DeleteItemException.class)
	public void deleteCategoryFailedEvent_SendCategoryCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> deleteCategoryFailedEvent");

		// Envía created para meterlo en el stream y lo saca de la cola
		CategoryCreatedEvent categoryCreatedEvent = CategoryDataUtil.getCategoryCreatedEvent(code + "6");
		categoryCreatedEvent.getCategory().setName("Nombre erroneo al crearlo");
		categoryCreatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(category_topic, categoryCreatedEvent.getAggregateId(), categoryCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		CategoryUpdatedEvent categoryUpdateEvent = CategoryDataUtil.getCategoryUpdatedEvent(code + "6");
		categoryUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(category_topic, categoryUpdateEvent.getAggregateId(), categoryUpdateEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		Thread.sleep(8000);

		// Envía failed y espera un evento de cancelled con el category original dentro
		DeleteCategoryFailedEvent event = CategoryDataUtil.getDeleteCategoryFailedEvent(code + "6");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<CategoryDTO> completableFuture = Whitebox.invokeMethod(categoryCommandHandler,
				"getCompletableFeature", event.getSessionId());

		kafkaTemplate.send(category_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(categoryCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(CategoryEventTypes.DELETE_CANCELLED, confirm.getType());
		assertEquals(categoryUpdateEvent.getCategory(), ((DeleteCategoryCancelledEvent) confirm).getCategory());
	}

	// Envía un evento de error de prepare rollback y debe provocar un evento
	// CategoryRollback con el item dentro
	@Test
	public void prepareRollbackEvent_SendCategoryRollbackEvent_IfReceivesSuccess() throws Exception {

		// Envía created para meterlo en el stream y lo saca de la cola
		CategoryCreatedEvent categoryCreatedEvent = CategoryDataUtil.getCategoryCreatedEvent(code + "7");
		kafkaTemplate.send(category_topic, categoryCreatedEvent.getAggregateId(), categoryCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		PrepareRollbackEvent event = CategoryDataUtil.getPrepareRollbackEvent(code + "7");

		kafkaTemplate.send(category_topic, event.getAggregateId(), event);

		Event rollback = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(CategoryEventTypes.ROLLBACK, rollback.getType());
		assertEquals(event.getFailEventType(), ((CategoryRollbackEvent) rollback).getFailEventType());
		assertEquals(categoryCreatedEvent.getCategory(), ((CategoryRollbackEvent) rollback).getLastSnapshotItem());
	}

	@KafkaHandler
	public void categoryCreatedEvent(CategoryCreatedEvent categoryCreatedEvent) {

		blockingQueue.offer(categoryCreatedEvent);
	}

	@KafkaHandler
	public void createCategoryCancelledEvent(CreateCategoryCancelledEvent createCategoryCancelledEvent) {

		blockingQueue.offer(createCategoryCancelledEvent);
	}

	@KafkaHandler
	public void categoryUpdatedEvent(CategoryUpdatedEvent categoryUpdatedEvent) {

		blockingQueue.offer(categoryUpdatedEvent);
	}

	@KafkaHandler
	public void updateCategoryCancelledEvent(UpdateCategoryCancelledEvent updateCategoryCancelledEvent) {

		blockingQueue.offer(updateCategoryCancelledEvent);
	}

	@KafkaHandler
	public void categoryDeletedEvent(CategoryDeletedEvent categoryDeletedEvent) {

		blockingQueue.offer(categoryDeletedEvent);
	}

	@KafkaHandler
	public void deleteCategoryCancelledEvent(DeleteCategoryCancelledEvent deleteCategoryCancelledEvent) {

		blockingQueue.offer(deleteCategoryCancelledEvent);
	}

	@KafkaHandler
	public void deleteCategoryCheckedEvent(DeleteCategoryCheckedEvent deleteCategoryCheckedEvent) {

		blockingQueue.offer(deleteCategoryCheckedEvent);
	}

	@KafkaHandler
	public void deleteCategoryCheckFailedEvent(DeleteCategoryCheckFailedEvent deleteCategoryCheckFailedEvent) {

		blockingQueue.offer(deleteCategoryCheckFailedEvent);
	}

	@KafkaHandler
	public void categoryRollbackEvent(CategoryRollbackEvent categoryRollbackEvent) {

		blockingQueue.offer(categoryRollbackEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

}
