package es.redmic.test.atlascommands.integration.layer;

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
import org.json.JSONException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.skyscreamer.jsonassert.JSONAssert;
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
import es.redmic.atlascommands.handler.LayerCommandHandler;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.create.CreateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerFailedEvent;
import es.redmic.atlaslib.events.layer.create.LayerCreatedEvent;
import es.redmic.atlaslib.events.layer.delete.CheckDeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckFailedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerFailedEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;
import es.redmic.brokerlib.avro.common.Event;
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
@KafkaListener(topics = "${broker.topic.layer}", groupId = "LayerCommandHandlerTest")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=LayerCommandHandler", "schema.registry.port=19999" })
public class LayerCommandHandlerTest extends KafkaBaseIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private static final String code = UUID.randomUUID().toString();

	@Value("${broker.topic.layer}")
	private String layer_topic;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	LayerCommandHandler layerCommandHandler;

	@PostConstruct
	public void LayerCommandHandlerTestPostConstruct() throws Exception {

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
	public void createLayerConfirmedEvent_SendLayerCreatedEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createLayerConfirmedEvent");

		// Envía create para meterlo en el stream
		CreateLayerEvent createLayerEvent = LayerDataUtil.getCreateEvent(code + "1");
		kafkaTemplate.send(layer_topic, createLayerEvent.getAggregateId(), createLayerEvent);

		// Envía confirmed y espera un evento de created con el layer original dentro
		CreateLayerConfirmedEvent event = LayerDataUtil.getCreateLayerConfirmedEvent(code + "1");

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(120, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.CREATED, confirm.getType());

		JSONAssert.assertEquals(createLayerEvent.getLayer().toString(),
				((LayerCreatedEvent) confirm).getLayer().toString(), false);
	}

	// Envía un evento de confirmación de modificación y debe provocar un evento
	// Updated con el item dentro
	@Test
	public void updateLayerConfirmedEvent_SendLayerUpdatedEvent_IfReceivesSuccess()
			throws InterruptedException, JSONException {

		logger.debug("----> updateLayerConfirmedEvent");

		// Envía update para meterlo en el stream
		UpdateLayerEvent updateLayerEvent = LayerDataUtil.getUpdateEvent(code + "2");
		kafkaTemplate.send(layer_topic, updateLayerEvent.getAggregateId(), updateLayerEvent);

		// Envía confirmed y espera un evento de updated con el layer original dentro
		UpdateLayerConfirmedEvent event = LayerDataUtil.getUpdateLayerConfirmedEvent(code + "2");
		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.UPDATED, confirm.getType());
		JSONAssert.assertEquals(updateLayerEvent.getLayer().toString(),
				((LayerUpdatedEvent) confirm).getLayer().toString(), false);
	}

	// Envía un evento de comprobación de que el elemento puede ser borrado y debe
	// provocar un evento DeleteLayerCheckedEvent ya que no está referenciado
	@Test
	public void checkDeleteLayerEvent_SendDeleteLayerCheckedEvent_IfReceivesSuccess() throws InterruptedException {

		logger.debug("----> CheckDeleteLayerEvent");

		CheckDeleteLayerEvent event = LayerDataUtil.getCheckDeleteLayerEvent(code + "3a");

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.DELETE_CHECKED, confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}

	// Envía un evento de confirmación de borrado y debe provocar un evento Deleted
	@Test
	public void deleteLayerConfirmedEvent_SendLayerDeletedEvent_IfReceivesSuccess() throws InterruptedException {

		logger.debug("----> deleteLayerConfirmedEvent");

		DeleteLayerConfirmedEvent event = LayerDataUtil.getDeleteLayerConfirmedEvent(code + "3");

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.DELETED, confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}

	// Fail cases

	// Envía un evento de error de creación y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = ItemAlreadyExistException.class)
	public void createLayerFailedEvent_SendLayerCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createLayerFailedEvent");

		CreateLayerFailedEvent event = LayerDataUtil.getCreateLayerFailedEvent(code + "4");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<LayerDTO> completableFuture = Whitebox.invokeMethod(layerCommandHandler,
				"getCompletableFeature", event.getSessionId(), LayerDataUtil.getLayer(code + "4"));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(layer_topic, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(layerCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.CREATE_CANCELLED, confirm.getType());
	}

	// Envía un evento de error de modificación y debe provocar un evento Cancelled
	// con el item dentro
	@Test(expected = ItemNotFoundException.class)
	public void updateLayerFailedEvent_SendLayerCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> updateLayerFailedEvent");

		// Envía created para meterlo en el stream y lo saca de la cola
		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "5");
		layerCreatedEvent.getLayer().setName("Nombre erroneo al crearlo");
		layerCreatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		LayerUpdatedEvent layerUpdateEvent = LayerDataUtil.getLayerUpdatedEvent(code + "5");
		layerUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(layer_topic, layerUpdateEvent.getAggregateId(), layerUpdateEvent);
		blockingQueue.poll(20, TimeUnit.SECONDS);

		Thread.sleep(8000);

		// Envía failed y espera un evento de cancelled con el layer original dentro
		UpdateLayerFailedEvent event = LayerDataUtil.getUpdateLayerFailedEvent(code + "5");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<LayerDTO> completableFuture = Whitebox.invokeMethod(layerCommandHandler,
				"getCompletableFeature", event.getSessionId(), LayerDataUtil.getLayer(code + "5"));

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(layerCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.UPDATE_CANCELLED, confirm.getType());
		assertEquals(layerUpdateEvent.getLayer(), ((UpdateLayerCancelledEvent) confirm).getLayer());
	}

	// Envía un evento de error de borrado y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = DeleteItemException.class)
	public void deleteLayerFailedEvent_SendLayerCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> deleteLayerFailedEvent");

		// Envía created para meterlo en el stream y lo saca de la cola
		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "6");
		layerCreatedEvent.getLayer().setName("Nombre erroneo al crearlo");
		layerCreatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		LayerUpdatedEvent layerUpdateEvent = LayerDataUtil.getLayerUpdatedEvent(code + "6");
		layerUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(layer_topic, layerUpdateEvent.getAggregateId(), layerUpdateEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		Thread.sleep(8000);

		// Envía failed y espera un evento de cancelled con el layer original dentro
		DeleteLayerFailedEvent event = LayerDataUtil.getDeleteLayerFailedEvent(code + "6");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<LayerDTO> completableFuture = Whitebox.invokeMethod(layerCommandHandler,
				"getCompletableFeature", event.getSessionId(), layerUpdateEvent.getLayer());

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(layerCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.DELETE_CANCELLED, confirm.getType());
		assertEquals(layerUpdateEvent.getLayer(), ((DeleteLayerCancelledEvent) confirm).getLayer());
	}

	@KafkaHandler
	public void layerCreatedEvent(LayerCreatedEvent layerCreatedEvent) {

		blockingQueue.offer(layerCreatedEvent);
	}

	@KafkaHandler
	public void createLayerCancelledEvent(CreateLayerCancelledEvent createLayerCancelledEvent) {

		blockingQueue.offer(createLayerCancelledEvent);
	}

	@KafkaHandler
	public void layerUpdatedEvent(LayerUpdatedEvent layerUpdatedEvent) {

		blockingQueue.offer(layerUpdatedEvent);
	}

	@KafkaHandler
	public void updateLayerCancelledEvent(UpdateLayerCancelledEvent updateLayerCancelledEvent) {

		blockingQueue.offer(updateLayerCancelledEvent);
	}

	@KafkaHandler
	public void layerDeletedEvent(LayerDeletedEvent layerDeletedEvent) {

		blockingQueue.offer(layerDeletedEvent);
	}

	@KafkaHandler
	public void deleteLayerCancelledEvent(DeleteLayerCancelledEvent deleteLayerCancelledEvent) {

		blockingQueue.offer(deleteLayerCancelledEvent);
	}

	@KafkaHandler
	public void deleteLayerCheckedEvent(DeleteLayerCheckedEvent deleteLayerCheckedEvent) {

		blockingQueue.offer(deleteLayerCheckedEvent);
	}

	@KafkaHandler
	public void deleteLayerCheckFailedEvent(DeleteLayerCheckFailedEvent deleteLayerCheckFailedEvent) {

		blockingQueue.offer(deleteLayerCheckFailedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

}