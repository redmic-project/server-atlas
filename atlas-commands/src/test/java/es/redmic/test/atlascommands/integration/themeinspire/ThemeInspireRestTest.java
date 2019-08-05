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
import es.redmic.atlascommands.handler.ThemeInspireCommandHandler;
import es.redmic.atlascommands.statestore.ThemeInspireStateStore;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.atlaslib.unit.utils.ThemeInspireDataUtil;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.test.atlascommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.documentation.DocumentationCommandBaseTest;
import es.redmic.testutils.utils.JsonToBeanTestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AtlasCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=ThemeInspireRest", "schema.registry.port=18097" })
@KafkaListener(topics = "${broker.topic.theme-inspire}", groupId = "ThemeInspireRestTest")
public class ThemeInspireRestTest extends DocumentationCommandBaseTest {
	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private final String CODE = "gg";

	@Value("${documentation.MICROSERVICE_HOST}")
	private String HOST;

	@Value("${controller.mapping.THEME_INSPIRE}")
	private String THEMEINSPIRE_PATH;

	@Autowired
	ThemeInspireCommandHandler themeInspireCommandHandler;

	ThemeInspireStateStore themeInspireStateStore;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Value("${broker.topic.theme-inspire}")
	private String theme_inspire_topic;

	@PostConstruct
	public void CreateThemeInspireFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Before
	public void before() {

		themeInspireStateStore = Mockito.mock(ThemeInspireStateStore.class);

		Whitebox.setInternalState(themeInspireCommandHandler, "themeInspireStateStore", themeInspireStateStore);

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
	public void createThemeInspire_SendCreateThemeInspireEvent_IfCommandWasSuccess() throws Exception {

		ThemeInspireDTO themeInspireDTO = ThemeInspireDataUtil.getThemeInspire(CODE);

		// @formatter:off
		
		String id = ThemeInspireDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(post(THEMEINSPIRE_PATH)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(themeInspireDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)))
				.andExpect(jsonPath("$.body.code", is(themeInspireDTO.getCode())))
				.andExpect(jsonPath("$.body.name", is(themeInspireDTO.getName())))
				.andExpect(jsonPath("$.body.name_en", is(themeInspireDTO.getName_en())));
		
		// @formatter:on

		CreateThemeInspireEvent event = (CreateThemeInspireEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		CreateThemeInspireEvent expectedEvent = ThemeInspireDataUtil.getCreateEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getThemeInspire().getName(), expectedEvent.getThemeInspire().getName());
		assertEquals(event.getThemeInspire().getName_en(), expectedEvent.getThemeInspire().getName_en());
		assertEquals(event.getThemeInspire().getCode(), expectedEvent.getThemeInspire().getCode());
	}

	@Test
	public void updateThemeInspire_SendUpdateThemeInspireEvent_IfCommandWasSuccess() throws Exception {

		when(themeInspireStateStore.get(anyString()))
				.thenReturn(ThemeInspireDataUtil.getThemeInspireCreatedEvent(CODE));

		ThemeInspireDTO themeInspireDTO = ThemeInspireDataUtil.getThemeInspire(CODE);

		// @formatter:off
		
		String id = ThemeInspireDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(put(THEMEINSPIRE_PATH + "/" + id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(themeInspireDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)));
		
		// @formatter:on

		UpdateThemeInspireEvent event = (UpdateThemeInspireEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		UpdateThemeInspireEvent expectedEvent = ThemeInspireDataUtil.getUpdateEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getThemeInspire().getName(), expectedEvent.getThemeInspire().getName());
		assertEquals(event.getThemeInspire().getName_en(), expectedEvent.getThemeInspire().getName_en());
		assertEquals(event.getThemeInspire().getCode(), expectedEvent.getThemeInspire().getCode());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	// @Test
	public void deleteThemeInspire_SendDeleteThemeInspireEvent_IfCommandWasSuccess() throws Exception {

		when(themeInspireStateStore.get(anyString()))
				.thenReturn(ThemeInspireDataUtil.getThemeInspireUpdatedEvent(CODE));

		// @formatter:off
		
		String id = ThemeInspireDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(delete(THEMEINSPIRE_PATH + "/" + id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)));
		
		// @formatter:on

		DeleteThemeInspireEvent event = (DeleteThemeInspireEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		DeleteThemeInspireEvent expectedEvent = ThemeInspireDataUtil.getDeleteEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getEditSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		Map<String, Object> schemaExpected = (Map<String, Object>) JsonToBeanTestUtil
				.getBean("/data/schemas/themeinspireschema.json", Map.class);

		// @formatter:off
		
		this.mockMvc.perform(get(THEMEINSPIRE_PATH + editSchemaPath)
				.header("Authorization", "Bearer " + getTokenOAGUser())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", is(schemaExpected)));
		// @formatter:on
	}

	@KafkaHandler
	public void createThemeInspire(CreateThemeInspireEvent createThemeInspireEvent) {

		CreateThemeInspireConfirmedEvent createThemeInspireConfirmEvent = new CreateThemeInspireConfirmedEvent()
				.buildFrom(createThemeInspireEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(theme_inspire_topic,
				createThemeInspireEvent.getAggregateId(), createThemeInspireConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(createThemeInspireEvent);
	}

	@KafkaHandler
	public void updateThemeInspire(UpdateThemeInspireEvent updateThemeInspireEvent) {

		UpdateThemeInspireConfirmedEvent updateConfirmEvent = new UpdateThemeInspireConfirmedEvent()
				.buildFrom(updateThemeInspireEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(theme_inspire_topic,
				updateThemeInspireEvent.getAggregateId(), updateConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(updateThemeInspireEvent);
	}

	@KafkaHandler
	public void deleteThemeInspire(DeleteThemeInspireEvent deleteThemeInspireEvent) {

		DeleteThemeInspireConfirmedEvent deleteConfirmEvent = new DeleteThemeInspireConfirmedEvent()
				.buildFrom(deleteThemeInspireEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(theme_inspire_topic,
				deleteThemeInspireEvent.getAggregateId(), deleteConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(deleteThemeInspireEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
