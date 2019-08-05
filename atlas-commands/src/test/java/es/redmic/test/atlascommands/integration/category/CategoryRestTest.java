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
import es.redmic.atlascommands.handler.CategoryCommandHandler;
import es.redmic.atlascommands.statestore.CategoryStateStore;
import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.create.CreateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.atlaslib.unit.utils.CategoryDataUtil;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.test.atlascommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.documentation.DocumentationCommandBaseTest;
import es.redmic.testutils.utils.JsonToBeanTestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AtlasCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CategoryRest", "schema.registry.port=19097" })
@KafkaListener(topics = "${broker.topic.category}", groupId = "CategoryRestTest")
public class CategoryRestTest extends DocumentationCommandBaseTest {
	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private final String CODE = UUID.randomUUID().toString();

	@Value("${documentation.MICROSERVICE_HOST}")
	private String HOST;

	@Value("${controller.mapping.CATEGORY}")
	private String CATEGORY_PATH;

	@Autowired
	CategoryCommandHandler categoryCommandHandler;

	CategoryStateStore categoryStateStore;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Value("${broker.topic.category}")
	private String category_topic;

	@PostConstruct
	public void CreateCategoryFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Before
	public void before() {

		categoryStateStore = Mockito.mock(CategoryStateStore.class);

		Whitebox.setInternalState(categoryCommandHandler, "categoryStateStore", categoryStateStore);

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
	public void createCategory_SendCreateCategoryEvent_IfCommandWasSuccess() throws Exception {

		CategoryDTO categoryDTO = CategoryDataUtil.getCategory(CODE);

		// @formatter:off
		
		String id = CategoryDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(post(CATEGORY_PATH)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(categoryDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)))
				.andExpect(jsonPath("$.body.name", is(categoryDTO.getName())));
		
		// @formatter:on

		CreateCategoryEvent event = (CreateCategoryEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		CreateCategoryEvent expectedEvent = CategoryDataUtil.getCreateEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getCategory().getName(), expectedEvent.getCategory().getName());
	}

	@Test
	public void updateCategory_SendUpdateCategoryEvent_IfCommandWasSuccess() throws Exception {

		when(categoryStateStore.get(anyString())).thenReturn(CategoryDataUtil.getCategoryCreatedEvent(CODE));

		CategoryDTO categoryDTO = CategoryDataUtil.getCategory(CODE);

		// @formatter:off
		
		String id = CategoryDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(put(CATEGORY_PATH + "/" + id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(categoryDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)));
		
		// @formatter:on

		UpdateCategoryEvent event = (UpdateCategoryEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		UpdateCategoryEvent expectedEvent = CategoryDataUtil.getUpdateEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getCategory().getName(), expectedEvent.getCategory().getName());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	@Test
	public void deleteCategory_SendDeleteCategoryEvent_IfCommandWasSuccess() throws Exception {

		when(categoryStateStore.get(anyString())).thenReturn(CategoryDataUtil.getCategoryUpdatedEvent(CODE));

		// @formatter:off
		
		String id = CategoryDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(delete(CATEGORY_PATH + "/" + id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)));
		
		// @formatter:on

		DeleteCategoryEvent event = (DeleteCategoryEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		DeleteCategoryEvent expectedEvent = CategoryDataUtil.getDeleteEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getEditSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		Map<String, Object> schemaExpected = (Map<String, Object>) JsonToBeanTestUtil
				.getBean("/data/schemas/categoryschema.json", Map.class);

		// @formatter:off
		
		this.mockMvc.perform(get(CATEGORY_PATH + editSchemaPath)
				.header("Authorization", "Bearer " + getTokenOAGUser())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", is(schemaExpected)));
		// @formatter:on
	}

	@KafkaHandler
	public void createCategory(CreateCategoryEvent createCategoryEvent) {

		CreateCategoryConfirmedEvent createCategoryConfirmEvent = new CreateCategoryConfirmedEvent()
				.buildFrom(createCategoryEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(category_topic,
				createCategoryEvent.getAggregateId(), createCategoryConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(createCategoryEvent);
	}

	@KafkaHandler
	public void updateCategory(UpdateCategoryEvent updateCategoryEvent) {

		UpdateCategoryConfirmedEvent updateConfirmEvent = new UpdateCategoryConfirmedEvent()
				.buildFrom(updateCategoryEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(category_topic,
				updateCategoryEvent.getAggregateId(), updateConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(updateCategoryEvent);
	}

	@KafkaHandler
	public void deleteCategory(DeleteCategoryEvent deleteCategoryEvent) {

		DeleteCategoryConfirmedEvent deleteConfirmEvent = new DeleteCategoryConfirmedEvent()
				.buildFrom(deleteCategoryEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(category_topic,
				deleteCategoryEvent.getAggregateId(), deleteConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(deleteCategoryEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
