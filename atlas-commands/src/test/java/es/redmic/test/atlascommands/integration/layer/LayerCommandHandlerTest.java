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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import es.redmic.atlascommands.aggregate.LayerAggregate;
import es.redmic.atlascommands.commands.layer.CreateLayerCommand;
import es.redmic.atlascommands.commands.layer.DeleteLayerCommand;
import es.redmic.atlascommands.commands.layer.RefreshLayerCommand;
import es.redmic.atlascommands.commands.layer.UpdateLayerCommand;
import es.redmic.atlascommands.handler.LayerCommandHandler;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.create.CreateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerFailedEvent;
import es.redmic.atlaslib.events.layer.create.EnrichCreateLayerEvent;
import es.redmic.atlaslib.events.layer.create.LayerCreatedEvent;
import es.redmic.atlaslib.events.layer.delete.CheckDeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckFailedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerFailedEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.fail.LayerRollbackEvent;
import es.redmic.atlaslib.events.layer.refresh.LayerRefreshedEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerFailedEvent;
import es.redmic.atlaslib.events.layer.update.EnrichUpdateLayerEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;
import es.redmic.atlaslib.events.themeinspire.create.ThemeInspireCreatedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;
import es.redmic.atlaslib.unit.utils.ThemeInspireDataUtil;
import es.redmic.brokerlib.alert.AlertType;
import es.redmic.brokerlib.alert.Message;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventTypes;
import es.redmic.brokerlib.avro.fail.PrepareRollbackEvent;
import es.redmic.brokerlib.avro.fail.RollbackFailedEvent;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.commandslib.exceptions.ConfirmationTimeoutException;
import es.redmic.commandslib.exceptions.ItemLockedException;
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
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=LayerCommandHandler", "schema.registry.port=19999",
		"rest.eventsource.timeout.ms=100000" })
public class LayerCommandHandlerTest extends KafkaBaseIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private static final String code = UUID.randomUUID().toString();

	@Value("${broker.topic.layer}")
	private String layer_topic;

	@Value("${broker.topic.theme-inspire}")
	private String theme_inspire_topic;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	protected static BlockingQueue<Object> blockingQueueForAlerts;

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
		blockingQueueForAlerts = new LinkedBlockingDeque<>();
	}

	// Success cases

	// Envía un evento de enriquecimiento de creación y debe provocar un evento
	// Create con el item dentro
	@Test
	public void enrichCreateLayerEvent_SendCreateLayerEvent_IfReceivesSuccess() throws InterruptedException {

		logger.debug("----> createLayerEvent");

		String code = "cc";

		// Envía themeInspireCreatedEvent
		ThemeInspireCreatedEvent themeInspireCreatedEvent = ThemeInspireDataUtil.getThemeInspireCreatedEvent(code);
		kafkaTemplate.send(theme_inspire_topic, themeInspireCreatedEvent.getAggregateId(), themeInspireCreatedEvent);

		Thread.sleep(4000);

		// Envía enrichCreateLayer con id del themeInspire igual al enviado

		EnrichCreateLayerEvent enrichCreateLayerEvent = LayerDataUtil
				.getEnrichCreateLayerEvent("layer-" + UUID.randomUUID().toString());
		enrichCreateLayerEvent.setSessionId(UUID.randomUUID().toString());
		enrichCreateLayerEvent.getLayer()
				.setThemeInspire(ThemeInspireDataUtil.getThemeInspireCreatedEvent(code).getThemeInspire());
		enrichCreateLayerEvent.getLayer().getThemeInspire().setName(null);
		enrichCreateLayerEvent.getLayer().getThemeInspire().setName_en(null);
		enrichCreateLayerEvent.getLayer().getThemeInspire().setCode(null);
		kafkaTemplate.send(layer_topic, enrichCreateLayerEvent.getAggregateId(), enrichCreateLayerEvent);

		// Comprueba que recibe createLayerEvent con themeInspire enriquecido
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.CREATE, confirm.getType());

		assertEquals(themeInspireCreatedEvent.getThemeInspire(),
				((CreateLayerEvent) confirm).getLayer().getThemeInspire());
	}

	// Envía un evento de confirmación de creación y debe provocar un evento Created
	// con el item dentro
	@Test
	public void createLayerConfirmedEvent_SendLayerCreatedEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createLayerConfirmedEvent");

		// Envía create para meterlo en el stream
		CreateLayerEvent createLayerEvent = LayerDataUtil.getCreateEvent(code + "1");
		kafkaTemplate.send(layer_topic, createLayerEvent.getAggregateId(), createLayerEvent);
		Event request = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
		assertNotNull(request);

		// Envía confirmed y espera un evento de created con el layer original dentro
		CreateLayerConfirmedEvent event = LayerDataUtil.getCreateLayerConfirmedEvent(code + "1");
		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.CREATED, confirm.getType());

		JSONAssert.assertEquals(createLayerEvent.getLayer().toString(),
				((LayerCreatedEvent) confirm).getLayer().toString(), false);
	}

	// Envía un evento de enriquecimiento de edición y debe provocar un evento
	// Update con el item dentro
	@Test
	public void enrichUpdateLayerEvent_SendUpdateLayerEvent_IfReceivesSuccess() throws InterruptedException {

		logger.debug("----> updateLayerEvent");

		String code = "cc";

		// Envía themeInspireUpdatedEvent
		ThemeInspireUpdatedEvent themeInspireUpdatedEvent = ThemeInspireDataUtil.getThemeInspireUpdatedEvent(code);
		kafkaTemplate.send(theme_inspire_topic, themeInspireUpdatedEvent.getAggregateId(), themeInspireUpdatedEvent);

		Thread.sleep(4000);

		// Envía enrichUpdateLayer con id del themeInspire igual al enviado

		EnrichUpdateLayerEvent enrichUpdateLayerEvent = LayerDataUtil
				.getEnrichUpdateLayerEvent("layer-" + UUID.randomUUID().toString());
		enrichUpdateLayerEvent.setSessionId(UUID.randomUUID().toString());
		enrichUpdateLayerEvent.getLayer()
				.setThemeInspire(ThemeInspireDataUtil.getThemeInspireCreatedEvent(code).getThemeInspire());
		enrichUpdateLayerEvent.getLayer().getThemeInspire().setName(null);
		enrichUpdateLayerEvent.getLayer().getThemeInspire().setName_en(null);
		enrichUpdateLayerEvent.getLayer().getThemeInspire().setCode(null);
		kafkaTemplate.send(layer_topic, enrichUpdateLayerEvent.getAggregateId(), enrichUpdateLayerEvent);

		// Comprueba que recibe createLayerEvent con themeInspire enriquecido
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.UPDATE, confirm.getType());

		assertEquals(themeInspireUpdatedEvent.getThemeInspire(),
				((UpdateLayerEvent) confirm).getLayer().getThemeInspire());
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
		Event request = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
		assertNotNull(request);

		// Envía confirmed y espera un evento de updated con el layer original dentro
		UpdateLayerConfirmedEvent event = LayerDataUtil.getUpdateLayerConfirmedEvent(code + "2");
		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.UPDATED, confirm.getType());
		JSONAssert.assertEquals(updateLayerEvent.getLayer().toString(),
				((LayerUpdatedEvent) confirm).getLayer().toString(), false);
	}

	// Envía un evento de confirmación de refrescado y debe provocar un evento
	// Refreshed con el item dentro
	@Test
	public void refreshLayerConfirmedEvent_SendLayerRefreshedEvent_IfReceivesSuccess()
			throws InterruptedException, JSONException {

		logger.debug("----> refreshLayerConfirmedEvent");

		// Envía refresh para meterlo en el stream
		RefreshLayerEvent refreshLayerEvent = LayerDataUtil.getRefreshEvent(code + "2a");
		kafkaTemplate.send(layer_topic, refreshLayerEvent.getAggregateId(), refreshLayerEvent);

		// Envía confirmed y espera un evento de refreshed con el layer refrescado
		// dentro
		RefreshLayerConfirmedEvent event = LayerDataUtil.getRefreshLayerConfirmedEvent(code + "2a");
		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.REFRESHED, confirm.getType());
		JSONAssert.assertEquals(refreshLayerEvent.getLayer().toString(),
				((LayerRefreshedEvent) confirm).getLayer().toString(), false);
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
				"getCompletableFeature", event.getSessionId());

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
				"getCompletableFeature", event.getSessionId());

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(layerCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.UPDATE_CANCELLED, confirm.getType());
		assertEquals(layerUpdateEvent.getLayer(), ((UpdateLayerCancelledEvent) confirm).getLayer());
	}

	// Envía un evento de error de refrescado y debe provocar un evento Cancelled
	// con el item dentro
	@Test(expected = ItemNotFoundException.class)
	public void refreshLayerFailedEvent_SendLayerCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> refreshLayerFailedEvent");

		// Envía created para meterlo en el stream y lo saca de la cola
		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "5a");
		layerCreatedEvent.getLayer().setName("Nombre erroneo al crearlo");
		layerCreatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía refresh para meterlo en el stream y lo saca de la cola
		RefreshLayerEvent refreshLayerEvent = LayerDataUtil.getRefreshEvent(code + "5a");
		refreshLayerEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(layer_topic, refreshLayerEvent.getAggregateId(), refreshLayerEvent);
		blockingQueue.poll(20, TimeUnit.SECONDS);

		Thread.sleep(3000);

		// Envía failed y espera un evento de cancelled con el layer original dentro
		RefreshLayerFailedEvent event = LayerDataUtil.getRefreshLayerFailedEvent(code + "5a");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<LayerDTO> completableFuture = Whitebox.invokeMethod(layerCommandHandler,
				"getCompletableFeature", event.getSessionId());

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(layerCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.REFRESH_CANCELLED, confirm.getType());
		assertEquals(layerCreatedEvent.getLayer(), ((RefreshLayerCancelledEvent) confirm).getLayer());
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
				"getCompletableFeature", event.getSessionId());

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(layerCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.DELETE_CANCELLED, confirm.getType());
		assertEquals(layerUpdateEvent.getLayer(), ((DeleteLayerCancelledEvent) confirm).getLayer());
	}

	// Rollback

	// Create

	// ConfirmationTimeoutException
	@Test
	public void createLayer_ThrowConfirmationTimeoutExceptionAndSendRollbackEvent_IfConfirmationIsNotReceived()
			throws Exception {

		// Envía themeInspireCreatedEvent
		ThemeInspireCreatedEvent themeInspireCreatedEvent = ThemeInspireDataUtil.getThemeInspireCreatedEvent("cc");
		kafkaTemplate.send(theme_inspire_topic, themeInspireCreatedEvent.getAggregateId(), themeInspireCreatedEvent);

		Thread.sleep(4000);

		try {
			Whitebox.invokeMethod(layerCommandHandler, "save",
					new CreateLayerCommand(LayerDataUtil.getLayer(code + 7)));
		} catch (Exception e) {
			assertTrue(e instanceof ConfirmationTimeoutException);
		}

		Event create = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(create);
		assertEquals(EventTypes.CREATE, create.getType());

		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(EventTypes.ROLLBACK, rollback.getType());
		assertEquals(LayerEventTypes.ENRICH_CREATE, ((LayerRollbackEvent) rollback).getFailEventType());

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(40, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	// ItemLockedException
	@Test
	public void createLayer_ThrowItemLockedExceptionAndSendRollbackEvent_IfItemLocked() throws Exception {

		RollbackFailedEvent rollbackFailedEvent = new RollbackFailedEvent(LayerEventTypes.CREATE_FAILED)
				.buildFrom(LayerDataUtil.getLayerCreatedEvent(code + "8"));
		kafkaTemplate.send(layer_topic, rollbackFailedEvent.getAggregateId(), rollbackFailedEvent);

		Thread.sleep(8000);

		try {
			Whitebox.invokeMethod(layerCommandHandler, "save",
					new CreateLayerCommand(LayerDataUtil.getLayer(code + "8")));
		} catch (Exception e) {
			assertTrue(e instanceof ItemLockedException);
		}
		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(EventTypes.ROLLBACK, rollback.getType());
		assertEquals(rollbackFailedEvent.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(40, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	@Test
	public void prepareRollbackEvent_SendLayerRollbackEventWithFailEventTypeEqualToCreateLayer_IfItemIsLocked()
			throws Exception {

		CreateLayerEvent createLayerEvent = LayerDataUtil.getCreateEvent(code + "9");
		kafkaTemplate.send(layer_topic, createLayerEvent.getAggregateId(), createLayerEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		Thread.sleep(8000);

		PrepareRollbackEvent event = (PrepareRollbackEvent) new LayerAggregate(null, null)
				.getRollbackEvent(createLayerEvent);

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(LayerEventTypes.ROLLBACK, rollback.getType());
		assertEquals(createLayerEvent.getType(), event.getFailEventType());
		assertEquals(event.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());
		assertNull(((LayerRollbackEvent) rollback).getLastSnapshotItem());
	}

	@Test
	public void prepareRollbackEventAfterRollbackFail_SendLayerRollbackEventWithFailEventTypeEqualToCreateLayer_IfItemIsLocked()
			throws Exception {

		CreateLayerEvent createLayerEvent = LayerDataUtil.getCreateEvent(code + "10");
		kafkaTemplate.send(layer_topic, createLayerEvent.getAggregateId(), createLayerEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		RollbackFailedEvent rollbackFailedEvent = new RollbackFailedEvent(LayerEventTypes.CREATE_FAILED)
				.buildFrom(createLayerEvent);
		kafkaTemplate.send(layer_topic, rollbackFailedEvent.getAggregateId(), rollbackFailedEvent);

		Thread.sleep(8000);

		PrepareRollbackEvent event = (PrepareRollbackEvent) new LayerAggregate(null, null)
				.getRollbackEvent(rollbackFailedEvent);

		try {
			kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		} catch (Exception e) {
			assertTrue(e instanceof ItemLockedException);
		}
		Event rollback = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
		assertNotNull(rollback);
		assertEquals(LayerEventTypes.ROLLBACK, rollback.getType());
		assertEquals(event.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());
		assertNull(((LayerRollbackEvent) rollback).getLastSnapshotItem());
	}

	// Update
	// ConfirmationTimeoutException
	@Test
	public void updateLayer_ThrowConfirmationTimeoutExceptionAndSendRollbackEvent_IfConfirmationIsNotReceived()
			throws Exception {

		// Envía themeInspireCreatedEvent
		ThemeInspireCreatedEvent themeInspireCreatedEvent = ThemeInspireDataUtil.getThemeInspireCreatedEvent("cc");
		kafkaTemplate.send(theme_inspire_topic, themeInspireCreatedEvent.getAggregateId(), themeInspireCreatedEvent);

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "11");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		Thread.sleep(8000);

		try {
			Whitebox.invokeMethod(layerCommandHandler, "update", layerCreatedEvent.getAggregateId(),
					new UpdateLayerCommand(layerCreatedEvent.getLayer()));
		} catch (Exception e) {
			assertTrue(e instanceof ConfirmationTimeoutException);
		}

		Event update = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(update);
		assertEquals(EventTypes.UPDATE, update.getType());

		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(EventTypes.ROLLBACK, rollback.getType());
		assertEquals(LayerEventTypes.ENRICH_UPDATE, ((LayerRollbackEvent) rollback).getFailEventType());

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(40, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	// ItemLockedException
	@Test
	public void updateLayer_ThrowItemLockedExceptionAndSendRollbackEvent_IfItemLocked() throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "12");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		RollbackFailedEvent rollbackFailedEvent = new RollbackFailedEvent(LayerEventTypes.UPDATE_FAILED)
				.buildFrom(layerCreatedEvent);
		kafkaTemplate.send(layer_topic, rollbackFailedEvent.getAggregateId(), rollbackFailedEvent);

		Thread.sleep(8000);

		try {
			Whitebox.invokeMethod(layerCommandHandler, "update", layerCreatedEvent.getAggregateId(),
					new UpdateLayerCommand(layerCreatedEvent.getLayer()));
		} catch (Exception e) {
			assertTrue(e instanceof ItemLockedException);
		}
		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(EventTypes.ROLLBACK, rollback.getType());
		assertEquals(rollbackFailedEvent.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(50, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	@Test
	public void prepareRollbackEvent_SendLayerRollbackEventWithFailEventTypeEqualToUpdateLayer_IfItemIsLocked()
			throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "13");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		UpdateLayerEvent updateLayerEvent = LayerDataUtil.getUpdateEvent(code + "13");
		kafkaTemplate.send(layer_topic, updateLayerEvent.getAggregateId(), updateLayerEvent);

		Thread.sleep(8000);

		PrepareRollbackEvent event = (PrepareRollbackEvent) new LayerAggregate(null, null)
				.getRollbackEvent(updateLayerEvent);

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		Event update = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);
		assertNotNull(update);
		assertEquals(LayerEventTypes.UPDATE, update.getType());

		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(LayerEventTypes.ROLLBACK, rollback.getType());
		assertEquals(updateLayerEvent.getType(), ((LayerRollbackEvent) rollback).getFailEventType());
		assertEquals(event.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());
		JSONAssert.assertEquals(layerCreatedEvent.getLayer().toString(),
				((LayerRollbackEvent) rollback).getLastSnapshotItem().toString(), false);
	}

	@Test
	public void prepareRollbackEventAfterRollbackFail_SendLayerRollbackEventWithFailEventTypeEqualToUpdateLayer_IfItemIsLocked()
			throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "14");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		UpdateLayerEvent updateLayerEvent = LayerDataUtil.getUpdateEvent(code + "14");
		kafkaTemplate.send(layer_topic, updateLayerEvent.getAggregateId(), updateLayerEvent);

		RollbackFailedEvent rollbackFailedEvent = new RollbackFailedEvent(LayerEventTypes.UPDATE_FAILED)
				.buildFrom(updateLayerEvent);
		kafkaTemplate.send(layer_topic, rollbackFailedEvent.getAggregateId(), rollbackFailedEvent);

		Thread.sleep(8000);

		PrepareRollbackEvent event = (PrepareRollbackEvent) new LayerAggregate(null, null)
				.getRollbackEvent(rollbackFailedEvent);

		try {
			kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		} catch (Exception e) {
			assertTrue(e instanceof ItemLockedException);
		}

		Event update = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
		assertNotNull(update);
		assertEquals(LayerEventTypes.UPDATE, update.getType());

		Event rollback = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
		assertNotNull(rollback);
		assertEquals(LayerEventTypes.ROLLBACK, rollback.getType());
		assertEquals(event.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());
		JSONAssert.assertEquals(layerCreatedEvent.getLayer().toString(),
				((LayerRollbackEvent) rollback).getLastSnapshotItem().toString(), false);
	}

	// Refresh
	// ConfirmationTimeoutException
	@Test
	public void refreshLayer_ThrowConfirmationTimeoutExceptionAndSendRollbackEvent_IfConfirmationIsNotReceived()
			throws Exception {

		// Envía themeInspireCreatedEvent
		ThemeInspireCreatedEvent themeInspireCreatedEvent = ThemeInspireDataUtil.getThemeInspireCreatedEvent("cc");
		kafkaTemplate.send(theme_inspire_topic, themeInspireCreatedEvent.getAggregateId(), themeInspireCreatedEvent);

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "15");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		Thread.sleep(4000);

		try {
			Whitebox.invokeMethod(layerCommandHandler, "refresh", new RefreshLayerCommand(
					layerCreatedEvent.getAggregateId(), LayerDataUtil.getLayerWMS(code + "15")));
		} catch (Exception e) {
			assertTrue(e instanceof ConfirmationTimeoutException);
		}
		Event evt = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(evt);
		if (!evt.getType().equals(EventTypes.ROLLBACK)) {
			evt = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);
		}

		assertNotNull(evt);
		assertEquals(EventTypes.ROLLBACK, evt.getType());
		assertEquals(LayerEventTypes.REFRESH, ((LayerRollbackEvent) evt).getFailEventType());

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(40, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	// ItemLockedException
	@Test
	public void refreshLayer_ThrowItemLockedExceptionAndSendRollbackEvent_IfItemLocked() throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "16");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		RollbackFailedEvent rollbackFailedEvent = new RollbackFailedEvent(LayerEventTypes.UPDATE_FAILED)
				.buildFrom(layerCreatedEvent);
		kafkaTemplate.send(layer_topic, rollbackFailedEvent.getAggregateId(), rollbackFailedEvent);

		Thread.sleep(8000);

		try {
			Whitebox.invokeMethod(layerCommandHandler, "refresh", new RefreshLayerCommand(
					layerCreatedEvent.getAggregateId(), LayerDataUtil.getLayerWMS(code + "16")));
		} catch (Exception e) {
			assertTrue(e instanceof ItemLockedException);
		}
		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(EventTypes.ROLLBACK, rollback.getType());
		assertEquals(rollbackFailedEvent.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(50, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	@Test
	public void prepareRollbackEvent_SendLayerRollbackEventWithFailEventTypeEqualToRefreshLayer_IfItemIsLocked()
			throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "17");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		RefreshLayerEvent refreshLayerEvent = LayerDataUtil.getRefreshEvent(code + "17");
		kafkaTemplate.send(layer_topic, refreshLayerEvent.getAggregateId(), refreshLayerEvent);

		Thread.sleep(8000);

		PrepareRollbackEvent event = (PrepareRollbackEvent) new LayerAggregate(null, null)
				.getRollbackEvent(refreshLayerEvent);

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(LayerEventTypes.ROLLBACK, rollback.getType());
		assertEquals(refreshLayerEvent.getType(), ((LayerRollbackEvent) rollback).getFailEventType());
		assertEquals(event.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());
		JSONAssert.assertEquals(layerCreatedEvent.getLayer().toString(),
				((LayerRollbackEvent) rollback).getLastSnapshotItem().toString(), false);
	}

	@Test
	public void prepareRollbackEventAfterRollbackFail_SendLayerRollbackEventWithFailEventTypeEqualToRefreshLayer_IfItemIsLocked()
			throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "18");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		RefreshLayerEvent refreshLayerEvent = LayerDataUtil.getRefreshEvent(code + "18");
		kafkaTemplate.send(layer_topic, refreshLayerEvent.getAggregateId(), refreshLayerEvent);

		RollbackFailedEvent rollbackFailedEvent = new RollbackFailedEvent(LayerEventTypes.UPDATE_FAILED)
				.buildFrom(refreshLayerEvent);
		kafkaTemplate.send(layer_topic, rollbackFailedEvent.getAggregateId(), rollbackFailedEvent);

		Thread.sleep(8000);

		PrepareRollbackEvent event = (PrepareRollbackEvent) new LayerAggregate(null, null)
				.getRollbackEvent(rollbackFailedEvent);

		try {
			kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		} catch (Exception e) {
			assertTrue(e instanceof ItemLockedException);
		}
		Event rollback = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
		assertNotNull(rollback);
		assertEquals(LayerEventTypes.ROLLBACK, rollback.getType());
		assertEquals(event.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());
		JSONAssert.assertEquals(layerCreatedEvent.getLayer().toString(),
				((LayerRollbackEvent) rollback).getLastSnapshotItem().toString(), false);
	}

	// Delete
	// ConfirmationTimeoutException
	@Test
	public void deleteLayer_ThrowConfirmationTimeoutExceptionAndSendRollbackEvent_IfConfirmationIsNotReceived()
			throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "19");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		Thread.sleep(8000);

		try {
			Whitebox.invokeMethod(layerCommandHandler, "update", layerCreatedEvent.getAggregateId(),
					new DeleteLayerCommand(layerCreatedEvent.getLayer().getId()));
		} catch (Exception e) {
			assertTrue(e instanceof ConfirmationTimeoutException);
		}

		Event confirm = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(LayerEventTypes.DELETE_CHECKED, confirm.getType());

		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(EventTypes.ROLLBACK, rollback.getType());
		assertEquals(LayerEventTypes.CHECK_DELETE, ((LayerRollbackEvent) rollback).getFailEventType());

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(40, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	// ItemLockedException
	@Test
	public void deleteLayer_ThrowItemLockedExceptionAndSendRollbackEvent_IfItemLocked() throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "20");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		RollbackFailedEvent rollbackFailedEvent = new RollbackFailedEvent(LayerEventTypes.DELETE_FAILED)
				.buildFrom(layerCreatedEvent);
		kafkaTemplate.send(layer_topic, rollbackFailedEvent.getAggregateId(), rollbackFailedEvent);

		Thread.sleep(8000);

		try {
			Whitebox.invokeMethod(layerCommandHandler, "update", layerCreatedEvent.getAggregateId(),
					new DeleteLayerCommand(layerCreatedEvent.getLayer().getId()));
		} catch (Exception e) {
			assertTrue(e instanceof ItemLockedException);
		}
		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(EventTypes.ROLLBACK, rollback.getType());
		assertEquals(rollbackFailedEvent.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(50, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	@Test
	public void prepareRollbackEvent_SendLayerRollbackEventWithFailEventTypeEqualToDeleteLayer_IfItemIsLocked()
			throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "21");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		DeleteLayerEvent deleteLayerEvent = LayerDataUtil.getDeleteEvent(code + "21");
		kafkaTemplate.send(layer_topic, deleteLayerEvent.getAggregateId(), deleteLayerEvent);

		Thread.sleep(8000);

		PrepareRollbackEvent event = (PrepareRollbackEvent) new LayerAggregate(null, null)
				.getRollbackEvent(deleteLayerEvent);

		kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		Event rollback = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		assertNotNull(rollback);
		assertEquals(LayerEventTypes.ROLLBACK, rollback.getType());
		assertEquals(deleteLayerEvent.getType(), ((LayerRollbackEvent) rollback).getFailEventType());
		assertEquals(event.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());
		JSONAssert.assertEquals(layerCreatedEvent.getLayer().toString(),
				((LayerRollbackEvent) rollback).getLastSnapshotItem().toString(), false);
	}

	@Test
	public void prepareRollbackEventAfterRollbackFail_SendLayerRollbackEventWithFailEventTypeEqualToDeleteLayer_IfItemIsLocked()
			throws Exception {

		LayerCreatedEvent layerCreatedEvent = LayerDataUtil.getLayerCreatedEvent(code + "22");
		kafkaTemplate.send(layer_topic, layerCreatedEvent.getAggregateId(), layerCreatedEvent);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		DeleteLayerEvent deleteLayerEvent = LayerDataUtil.getDeleteEvent(code + "22");
		kafkaTemplate.send(layer_topic, deleteLayerEvent.getAggregateId(), deleteLayerEvent);

		RollbackFailedEvent rollbackFailedEvent = new RollbackFailedEvent(LayerEventTypes.DELETE_FAILED)
				.buildFrom(deleteLayerEvent);
		kafkaTemplate.send(layer_topic, rollbackFailedEvent.getAggregateId(), rollbackFailedEvent);

		Thread.sleep(8000);

		PrepareRollbackEvent event = (PrepareRollbackEvent) new LayerAggregate(null, null)
				.getRollbackEvent(rollbackFailedEvent);

		try {
			kafkaTemplate.send(layer_topic, event.getAggregateId(), event);

		} catch (Exception e) {
			assertTrue(e instanceof ItemLockedException);
		}
		Event rollback = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
		assertNotNull(rollback);
		assertEquals(LayerEventTypes.ROLLBACK, rollback.getType());
		assertEquals(event.getFailEventType(), ((LayerRollbackEvent) rollback).getFailEventType());
		JSONAssert.assertEquals(layerCreatedEvent.getLayer().toString(),
				((LayerRollbackEvent) rollback).getLastSnapshotItem().toString(), false);
	}

	@KafkaHandler
	public void createLayerEvent(CreateLayerEvent createLayerEvent) {

		blockingQueue.offer(createLayerEvent);
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
	public void updateLayerEvent(UpdateLayerEvent updateLayerEvent) {

		blockingQueue.offer(updateLayerEvent);
	}

	@KafkaHandler
	public void layerUpdatedEvent(LayerUpdatedEvent layerUpdatedEvent) {

		blockingQueue.offer(layerUpdatedEvent);
	}

	@KafkaHandler
	public void layerRefreshedEvent(LayerRefreshedEvent layerRefreshedEvent) {

		blockingQueue.offer(layerRefreshedEvent);
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

	@KafkaHandler
	public void layerRollbackEvent(LayerRollbackEvent layerRollbackEvent) {

		blockingQueue.offer(layerRollbackEvent);
	}

	@KafkaListener(topics = "${broker.topic.alert}", groupId = "test")
	public void errorAlert(Message message) {
		blockingQueueForAlerts.offer(message);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

}
