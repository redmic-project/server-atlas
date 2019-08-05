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
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.redmic.atlascommands.AtlasCommandsApplication;
import es.redmic.atlascommands.handler.LayerCommandHandler;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.common.LayerCancelledEvent;
import es.redmic.atlaslib.events.layer.create.LayerCreatedEvent;
import es.redmic.atlaslib.events.layer.partialupdate.themeinspire.UpdateThemeInspireInLayerEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;
import es.redmic.atlaslib.unit.utils.ThemeInspireDataUtil;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.exception.common.ExceptionType;
import es.redmic.test.atlascommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AtlasCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.layer}", groupId = "LayerPostUpdateHandlerTest")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=LayerPostUpdateHandler",
		"schema.registry.port=29999" })
public class LayerPostUpdateHandlerTest extends KafkaBaseIntegrationTest {

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

	// Envía un evento de themeInspire modificado y debe provocar un evento de
	// modificar layer para cada uno de los layer que contiene el themeInspire
	// modificado. Si a su vez se envía un evento de confirmación de la vista, se
	// debe obtener un evento modificado para cada layer confirmado
	@Test
	public void themeInspireUpdatedEvent_TriggerLayerUpdateEvent_IfIsReferencedInSomeLayer()
			throws InterruptedException {

		// Referencia a modificar
		ThemeInspireUpdatedEvent themeInspireUpdatedEvent = ThemeInspireDataUtil.getThemeInspireUpdatedEvent("cc");
		themeInspireUpdatedEvent.getThemeInspire().setName("new_name");

		// Envía created para que genere un evento postUpdate al modificarse la
		// referencia
		LayerCreatedEvent layerCreatedEventA = LayerDataUtil.getLayerCreatedEvent(code + "0a");
		kafkaTemplate.send(layer_topic, layerCreatedEventA.getAggregateId(), layerCreatedEventA);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		// Envía created para que genere un evento postUpdate al modificarse la
		// referencia
		LayerCreatedEvent layerCreatedEventB = LayerDataUtil.getLayerCreatedEvent(code + "0b");
		kafkaTemplate.send(layer_topic, layerCreatedEventB.getAggregateId(), layerCreatedEventB);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		// Envía created con themeInspire actualizado para comprobar que no genere un
		// evento postUpdate al modificarse la referencia
		LayerCreatedEvent layerCreatedEventC = LayerDataUtil.getLayerCreatedEvent(code + "0c");
		layerCreatedEventC.getLayer().setThemeInspire(themeInspireUpdatedEvent.getThemeInspire());
		kafkaTemplate.send(layer_topic, layerCreatedEventC.getAggregateId(), layerCreatedEventC);
		blockingQueue.poll(30, TimeUnit.SECONDS);

		blockingQueue.clear();
		kafkaTemplate.send(theme_inspire_topic, themeInspireUpdatedEvent.getAggregateId(), themeInspireUpdatedEvent);

		// Se recibe evento postUpdate para layerA
		UpdateThemeInspireInLayerEvent updateA = (UpdateThemeInspireInLayerEvent) blockingQueue.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(updateA);
		assertEquals(LayerEventTypes.UPDATE_THEMEINSPIRE, updateA.getType());
		assertEquals(themeInspireUpdatedEvent.getThemeInspire(), updateA.getThemeInspire());

		// Se recibe evento postUpdate para layerB
		UpdateThemeInspireInLayerEvent updateB = (UpdateThemeInspireInLayerEvent) blockingQueue.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(updateB);
		assertEquals(LayerEventTypes.UPDATE_THEMEINSPIRE, updateB.getType());
		assertEquals(themeInspireUpdatedEvent.getThemeInspire(), updateB.getThemeInspire());

		// Como layerC tenía el themeInspire actualizado, no se recibe evento para este
		// layer
		Event updateC = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertTrue((updateC == null) || (updateC.getAggregateId() != layerCreatedEventC.getAggregateId()));

		// Envía confirmación para simular que view lo insertó
		kafkaTemplate.send(layer_topic, updateA.getAggregateId(), new UpdateLayerConfirmedEvent().buildFrom(updateA));

		// Envía fallo para simular que view no lo insertó
		UpdateLayerFailedEvent updateLayerFailedEvent = new UpdateLayerFailedEvent().buildFrom(updateB);
		updateLayerFailedEvent.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		updateLayerFailedEvent.setArguments(arguments);
		kafkaTemplate.send(layer_topic, updateB.getAggregateId(), updateLayerFailedEvent);

		// Se modificó bien la primera layer
		LayerUpdatedEvent updated = (LayerUpdatedEvent) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(updated);
		assertEquals(LayerEventTypes.UPDATED, updated.getType());
		assertEquals(themeInspireUpdatedEvent.getThemeInspire(), updated.getLayer().getThemeInspire());
		assertEquals(updateA.getAggregateId(), updated.getAggregateId());

		// No se modificó la segunda layer
		LayerCancelledEvent cancelled = (LayerCancelledEvent) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(cancelled);
		assertEquals(LayerEventTypes.UPDATE_CANCELLED, cancelled.getType());
		assertEquals(layerCreatedEventB.getLayer().getThemeInspire(), cancelled.getLayer().getThemeInspire());
		assertEquals(updateB.getAggregateId(), cancelled.getAggregateId());
	}

	@KafkaHandler
	public void layerCreatedEvent(LayerCreatedEvent layerCreatedEvent) {

		blockingQueue.offer(layerCreatedEvent);
	}

	@KafkaHandler
	public void layerUpdatedEvent(LayerUpdatedEvent layerUpdatedEvent) {

		blockingQueue.offer(layerUpdatedEvent);
	}

	@KafkaHandler
	public void updateThemeInspireInLayerEvent(UpdateThemeInspireInLayerEvent updateThemeInspireInLayerEvent) {

		blockingQueue.offer(updateThemeInspireInLayerEvent);
	}

	@KafkaHandler
	public void updateLayerCancelledEvent(UpdateLayerCancelledEvent updateLayerCancelledEvent) {

		blockingQueue.offer(updateLayerCancelledEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

}
