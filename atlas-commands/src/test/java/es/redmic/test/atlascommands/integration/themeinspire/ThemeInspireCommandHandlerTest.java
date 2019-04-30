package es.redmic.test.atlascommands.integration.themeinspire;

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
import es.redmic.atlascommands.handler.ThemeInspireCommandHandler;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.create.ThemeInspireCreatedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.CheckDeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireFailedEvent;
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
@KafkaListener(topics = "${broker.topic.theme-inspire}", groupId = "ThemeInspireCommandHandlerTest")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=ThemeInspireCommandHandler",
		"schema.registry.port=18097" })
public class ThemeInspireCommandHandlerTest extends KafkaBaseIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private static final String code = "gg";

	@Value("${broker.topic.theme-inspire}")
	private String theme_inspire_topic;

	@Value("${broker.topic.atlas}")
	private String atlas_topic;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	ThemeInspireCommandHandler themeInspireCommandHandler;

	@PostConstruct
	public void ThemeInspireCommandHandlerTestPostConstruct() throws Exception {

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
	public void createThemeInspireConfirmedEvent_SendThemeInspireCreatedEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createThemeInspireConfirmedEvent");

		// Envía create para meterlo en el stream
		CreateThemeInspireEvent createThemeInspireEvent = ThemeInspireDataUtil.getCreateEvent(code + "1");
		kafkaTemplate.send(theme_inspire_topic, createThemeInspireEvent.getAggregateId(), createThemeInspireEvent);

		// Envía confirmed y espera un evento de created con el atlas original dentro
		CreateThemeInspireConfirmedEvent event = ThemeInspireDataUtil.getCreateThemeInspireConfirmedEvent(code + "1");

		kafkaTemplate.send(theme_inspire_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(120, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.CREATED, confirm.getType());

		assertEquals(createThemeInspireEvent.getThemeInspire(), ((ThemeInspireCreatedEvent) confirm).getThemeInspire());
	}

	// Envía un evento de confirmación de modificación y debe provocar un evento
	// Updated con el item dentro
	@Test
	public void updateThemeInspireConfirmedEvent_SendThemeInspireUpdatedEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> updateThemeInspireConfirmedEvent");

		// Envía update para meterlo en el stream
		UpdateThemeInspireEvent updateThemeInspireEvent = ThemeInspireDataUtil.getUpdateEvent(code + "2");
		kafkaTemplate.send(theme_inspire_topic, updateThemeInspireEvent.getAggregateId(), updateThemeInspireEvent);

		// Envía confirmed y espera un evento de updated con el atlas original dentro
		UpdateThemeInspireConfirmedEvent event = ThemeInspireDataUtil.getUpdateThemeInspireConfirmedEvent(code + "2");
		kafkaTemplate.send(theme_inspire_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.UPDATED, confirm.getType());
		assertEquals(updateThemeInspireEvent.getThemeInspire(), ((ThemeInspireUpdatedEvent) confirm).getThemeInspire());
	}

	// Envía un evento de comprobación de que el elemento puede ser borrado y debe
	// provocar un evento DeleteThemeInspireCheckedEvent ya que no está referenciado
	@Test
	public void checkDeleteThemeInspireEvent_SendDeleteThemeInspireCheckedEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> CheckDeleteThemeInspireEvent");

		CheckDeleteThemeInspireEvent event = ThemeInspireDataUtil.getCheckDeleteThemeInspireEvent(code + "3a");

		kafkaTemplate.send(theme_inspire_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.DELETE_CHECKED, confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}

	// Envía un evento de confirmación de borrado y debe provocar un evento Deleted
	@Test
	public void deleteThemeInspireConfirmedEvent_SendThemeInspireDeletedEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> deleteThemeInspireConfirmedEvent");

		DeleteThemeInspireConfirmedEvent event = ThemeInspireDataUtil.getDeleteThemeInspireConfirmedEvent(code + "3");

		kafkaTemplate.send(theme_inspire_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.DELETED, confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}

	// Fail cases

	// Envía un evento de error de creación y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = ItemAlreadyExistException.class)
	public void createThemeInspireFailedEvent_SendThemeInspireCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createThemeInspireFailedEvent");

		CreateThemeInspireFailedEvent event = ThemeInspireDataUtil.getCreateThemeInspireFailedEvent(code + "4");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<ThemeInspireDTO> completableFuture = Whitebox.invokeMethod(themeInspireCommandHandler,
				"getCompletableFeature", event.getSessionId(), ThemeInspireDataUtil.getThemeInspire(code + "4"));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(theme_inspire_topic,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(themeInspireCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.CREATE_CANCELLED, confirm.getType());
	}

	// Envía un evento de error de modificación y debe provocar un evento Cancelled
	// con el item dentro
	@Test(expected = ItemNotFoundException.class)
	public void updateThemeInspireFailedEvent_SendThemeInspireCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> updateThemeInspireFailedEvent");

		// Envía created para meterlo en el stream y lo saca de la cola
		ThemeInspireCreatedEvent themeInspireCreatedEvent = ThemeInspireDataUtil
				.getThemeInspireCreatedEvent(code + "5");
		themeInspireCreatedEvent.getThemeInspire().setName("Nombre erroneo al crearlo");
		themeInspireCreatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(theme_inspire_topic, themeInspireCreatedEvent.getAggregateId(), themeInspireCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		ThemeInspireUpdatedEvent themeInspireUpdateEvent = ThemeInspireDataUtil.getThemeInspireUpdatedEvent(code + "5");
		themeInspireUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(theme_inspire_topic, themeInspireUpdateEvent.getAggregateId(), themeInspireUpdateEvent);
		blockingQueue.poll(20, TimeUnit.SECONDS);

		Thread.sleep(8000);

		// Envía failed y espera un evento de cancelled con el atlas original dentro
		UpdateThemeInspireFailedEvent event = ThemeInspireDataUtil.getUpdateThemeInspireFailedEvent(code + "5");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<ThemeInspireDTO> completableFuture = Whitebox.invokeMethod(themeInspireCommandHandler,
				"getCompletableFeature", event.getSessionId(), ThemeInspireDataUtil.getThemeInspire(code + "5"));

		kafkaTemplate.send(theme_inspire_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(themeInspireCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.UPDATE_CANCELLED, confirm.getType());
		assertEquals(themeInspireUpdateEvent.getThemeInspire(),
				((UpdateThemeInspireCancelledEvent) confirm).getThemeInspire());
	}

	// Envía un evento de comprobación de que el elemento puede ser borrado y debe
	// provocar un evento DeleteThemeInspireCheckFailedEvent ya que está
	// referenciado
	/*-@Test
	public void checkDeleteThemeInspireEvent_SendDeleteThemeInspireCheckFailedEvent_IfReceivesSuccess()
			throws InterruptedException {
	
		logger.debug("----> DeleteThemeInspireCheckFailedEvent");
	
		CheckDeleteThemeInspireEvent event = ThemeInspireDataUtil.getCheckDeleteThemeInspireEvent(code + "5a");
	
		AtlasCreatedEvent atlasWithThemeInspireEvent = AtlasDataUtil.getAtlasCreatedEvent(1);
		atlasWithThemeInspireEvent.getAtlas().setType(ThemeInspireDataUtil.getThemeInspire(code + "5a"));
	
		kafkaTemplate.send(atlas_topic, atlasWithThemeInspireEvent.getAggregateId(), atlasWithThemeInspireEvent);
	
		Thread.sleep(4000);
	
		kafkaTemplate.send(theme_inspire_topic, event.getAggregateId(), event);
	
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
	
		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.DELETE_CHECK_FAILED, confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}-*/

	// Envía un evento de error de borrado y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = DeleteItemException.class)
	public void deleteThemeInspireFailedEvent_SendThemeInspireCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> deleteThemeInspireFailedEvent");

		// Envía created para meterlo en el stream y lo saca de la cola
		ThemeInspireCreatedEvent themeInspireCreatedEvent = ThemeInspireDataUtil
				.getThemeInspireCreatedEvent(code + "6");
		themeInspireCreatedEvent.getThemeInspire().setName("Nombre erroneo al crearlo");
		themeInspireCreatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(theme_inspire_topic, themeInspireCreatedEvent.getAggregateId(), themeInspireCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		ThemeInspireUpdatedEvent themeInspireUpdateEvent = ThemeInspireDataUtil.getThemeInspireUpdatedEvent(code + "6");
		themeInspireUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(theme_inspire_topic, themeInspireUpdateEvent.getAggregateId(), themeInspireUpdateEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		Thread.sleep(8000);

		// Envía failed y espera un evento de cancelled con el atlas original dentro
		DeleteThemeInspireFailedEvent event = ThemeInspireDataUtil.getDeleteThemeInspireFailedEvent(code + "6");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<ThemeInspireDTO> completableFuture = Whitebox.invokeMethod(themeInspireCommandHandler,
				"getCompletableFeature", event.getSessionId(), themeInspireUpdateEvent.getThemeInspire());

		kafkaTemplate.send(theme_inspire_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(themeInspireCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.DELETE_CANCELLED, confirm.getType());
		assertEquals(themeInspireUpdateEvent.getThemeInspire(),
				((DeleteThemeInspireCancelledEvent) confirm).getThemeInspire());
	}

	@KafkaHandler
	public void themeInspireCreatedEvent(ThemeInspireCreatedEvent themeInspireCreatedEvent) {

		blockingQueue.offer(themeInspireCreatedEvent);
	}

	@KafkaHandler
	public void createThemeInspireCancelledEvent(CreateThemeInspireCancelledEvent createThemeInspireCancelledEvent) {

		blockingQueue.offer(createThemeInspireCancelledEvent);
	}

	@KafkaHandler
	public void themeInspireUpdatedEvent(ThemeInspireUpdatedEvent themeInspireUpdatedEvent) {

		blockingQueue.offer(themeInspireUpdatedEvent);
	}

	@KafkaHandler
	public void updateThemeInspireCancelledEvent(UpdateThemeInspireCancelledEvent updateThemeInspireCancelledEvent) {

		blockingQueue.offer(updateThemeInspireCancelledEvent);
	}

	@KafkaHandler
	public void themeInspireDeletedEvent(ThemeInspireDeletedEvent themeInspireDeletedEvent) {

		blockingQueue.offer(themeInspireDeletedEvent);
	}

	@KafkaHandler
	public void deleteThemeInspireCancelledEvent(DeleteThemeInspireCancelledEvent deleteThemeInspireCancelledEvent) {

		blockingQueue.offer(deleteThemeInspireCancelledEvent);
	}

	@KafkaHandler
	public void deleteThemeInspireCheckedEvent(DeleteThemeInspireCheckedEvent deleteThemeInspireCheckedEvent) {

		blockingQueue.offer(deleteThemeInspireCheckedEvent);
	}

	@KafkaHandler
	public void deleteThemeInspireCheckFailedEvent(
			DeleteThemeInspireCheckFailedEvent deleteThemeInspireCheckFailedEvent) {

		blockingQueue.offer(deleteThemeInspireCheckFailedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

}
