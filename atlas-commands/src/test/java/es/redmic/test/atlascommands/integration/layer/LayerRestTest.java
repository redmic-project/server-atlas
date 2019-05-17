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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.atlascommands.AtlasCommandsApplication;
import es.redmic.atlascommands.handler.LayerCommandHandler;
import es.redmic.atlascommands.statestore.LayerStateStore;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.test.atlascommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.documentation.DocumentationCommandBaseTest;
import es.redmic.testutils.utils.JsonToBeanTestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AtlasCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=LayerRest", "schema.registry.port=19197" })
@KafkaListener(topics = "${broker.topic.layer}", groupId = "LayerRestTest")
public class LayerRestTest extends DocumentationCommandBaseTest {
	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private final String CODE = UUID.randomUUID().toString();

	// @formatter:off
	
	private final String HOST = "redmic.es/api/atlas/commands",
			CATEGORY_PATH = "/layer";
	
	// @formatter:on

	@Autowired
	LayerCommandHandler layerCommandHandler;

	LayerStateStore layerStateStore;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Value("${broker.topic.layer}")
	private String layer_topic;

	@PostConstruct
	public void CreateLayerFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Before
	public void before() {

		layerStateStore = Mockito.mock(LayerStateStore.class);

		Whitebox.setInternalState(layerCommandHandler, "layerStateStore", layerStateStore);

		// @formatter:off
		
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.apply(documentationConfiguration(this.restDocumentation)
						.uris().withScheme(SCHEME).withHost(HOST).withPort(PORT))
				.alwaysDo(this.document).build();

		// @formatter:on
	}

	@Test
	public void createLayer_SendCreateLayerEvent_IfCommandWasSuccess() throws Exception {

		LayerDTO layerDTO = LayerDataUtil.getLayer(CODE);

		// @formatter:off
		
		String id = LayerDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(post(CATEGORY_PATH)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(layerDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)))
				.andExpect(jsonPath("$.body.name", is(layerDTO.getName())));
		
		// @formatter:on

		CreateLayerEvent event = (CreateLayerEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		CreateLayerEvent expectedEvent = LayerDataUtil.getCreateEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getLayer().getName(), expectedEvent.getLayer().getName());
	}

	@Test
	public void updateLayer_SendUpdateLayerEvent_IfCommandWasSuccess() throws Exception {

		when(layerStateStore.getLayer(anyString())).thenReturn(LayerDataUtil.getLayerCreatedEvent(CODE));

		LayerDTO layerDTO = LayerDataUtil.getLayer(CODE);

		// @formatter:off
		
		String id = LayerDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(put(CATEGORY_PATH + "/" + id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(layerDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)));
		
		// @formatter:on

		UpdateLayerEvent event = (UpdateLayerEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		UpdateLayerEvent expectedEvent = LayerDataUtil.getUpdateEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getLayer().getName(), expectedEvent.getLayer().getName());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	@Test
	public void deleteLayer_SendDeleteLayerEvent_IfCommandWasSuccess() throws Exception {

		when(layerStateStore.getLayer(anyString())).thenReturn(LayerDataUtil.getLayerUpdatedEvent(CODE));

		// @formatter:off
		
		String id = LayerDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(delete(CATEGORY_PATH + "/" + id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)));
		
		// @formatter:on

		DeleteLayerEvent event = (DeleteLayerEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		DeleteLayerEvent expectedEvent = LayerDataUtil.getDeleteEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getEditSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		Map<String, Object> schemaExpected = (Map<String, Object>) JsonToBeanTestUtil
				.getBean("/data/schemas/layerschema.json", Map.class);

		// @formatter:off
		
		this.mockMvc.perform(get(CATEGORY_PATH + editSchemaPath)
				.header("Authorization", "Bearer " + getTokenOAGUser())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", is(schemaExpected)));
		// @formatter:on
	}

	@KafkaHandler
	public void createLayer(CreateLayerEvent createLayerEvent) {

		CreateLayerConfirmedEvent createLayerConfirmEvent = new CreateLayerConfirmedEvent().buildFrom(createLayerEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(layer_topic,
				createLayerEvent.getAggregateId(), createLayerConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(createLayerEvent);
	}

	@KafkaHandler
	public void updateLayer(UpdateLayerEvent updateLayerEvent) {

		UpdateLayerConfirmedEvent updateConfirmEvent = new UpdateLayerConfirmedEvent().buildFrom(updateLayerEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(layer_topic,
				updateLayerEvent.getAggregateId(), updateConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(updateLayerEvent);
	}

	@KafkaHandler
	public void deleteLayer(DeleteLayerEvent deleteLayerEvent) {

		DeleteLayerConfirmedEvent deleteConfirmEvent = new DeleteLayerConfirmedEvent().buildFrom(deleteLayerEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(layer_topic,
				deleteLayerEvent.getAggregateId(), deleteConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(deleteLayerEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}