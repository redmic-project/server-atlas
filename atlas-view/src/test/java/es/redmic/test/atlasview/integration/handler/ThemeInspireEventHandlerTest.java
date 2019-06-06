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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
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

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireFailedEvent;
import es.redmic.atlasview.AtlasViewApplication;
import es.redmic.atlasview.mapper.themeinspire.ThemeInspireESMapper;
import es.redmic.atlasview.model.themeinspire.ThemeInspire;
import es.redmic.atlasview.repository.themeinspire.ThemeInspireESRepository;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.testutils.documentation.DocumentationViewBaseTest;

@SpringBootTest(classes = { AtlasViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@KafkaListener(topics = "${broker.topic.theme-inspire}", groupId = "test")
@TestPropertySource(properties = { "schema.registry.port=18084" })
@DirtiesContext
@ActiveProfiles("test")
public class ThemeInspireEventHandlerTest extends DocumentationViewBaseTest {

	private final String USER_ID = "1";

	@Autowired
	ThemeInspireESRepository repository;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Value("${broker.topic.theme-inspire}")
	private String THEME_INSPIRE_TOPIC;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1);

	@PostConstruct
	public void CreateThemeInspireFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void sendThemeInspireCreatedEvent_SaveItem_IfEventIsOk() throws Exception {

		CreateThemeInspireEvent event = getCreateThemeInspireEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(THEME_INSPIRE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getThemeInspire().getId());

		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.CREATE_CONFIRMED, confirm.getType());

		ThemeInspire themeInspire = (ThemeInspire) item.get_source();
		assertEquals(themeInspire.getId(), event.getAggregateId());
		assertEquals(themeInspire.getCode(), event.getThemeInspire().getCode());
		assertEquals(themeInspire.getName(), event.getThemeInspire().getName());
	}

	@Test
	public void sendThemeInspireUpdatedEvent_callUpdate_IfEventIsOk() throws Exception {

		UpdateThemeInspireEvent event = getUpdateThemeInspireEvent();

		repository.save(Mappers.getMapper(ThemeInspireESMapper.class).map(event.getThemeInspire())); // mapper.getMapperFacade().map(event.getThemeInspire(),
																										// ThemeInspire.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(THEME_INSPIRE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getThemeInspire().getId());

		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.UPDATE_CONFIRMED.toString(), confirm.getType());

		ThemeInspire themeInspire = (ThemeInspire) item.get_source();
		assertEquals(themeInspire.getId(), event.getAggregateId());
		assertEquals(themeInspire.getCode(), event.getThemeInspire().getCode());
		assertEquals(themeInspire.getName(), event.getThemeInspire().getName());
	}

	@Test(expected = ItemNotFoundException.class)
	public void sendThemeInspireDeleteEvent_callDelete_IfEventIsOk() throws Exception {

		DeleteThemeInspireEvent event = getDeleteThemeInspireEvent();

		repository.save(
				Mappers.getMapper(ThemeInspireESMapper.class).map(getUpdateThemeInspireEvent().getThemeInspire())); // mapper.getMapperFacade().map(getUpdateThemeInspireEvent().getThemeInspire(),
																													// ThemeInspire.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(THEME_INSPIRE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);
		assertNotNull(confirm);
		assertEquals(ThemeInspireEventTypes.DELETE_CONFIRMED.toString(), confirm.getType());

		repository.findById(event.getAggregateId());
	}

	@Test
	public void sendThemeInspireCreatedEvent_PublishCreateThemeInspireFailedEvent_IfNoConstraintsFulfilled()
			throws Exception {

		CreateThemeInspireEvent event = getCreateThemeInspireEvent();

		repository.save(Mappers.getMapper(ThemeInspireESMapper.class).map(event.getThemeInspire())); // mapper.getMapperFacade().map(event.getThemeInspire(),
																										// ThemeInspire.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(THEME_INSPIRE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(event.getThemeInspire().getId());

		assertNotNull(fail);
		assertEquals(ThemeInspireEventTypes.CREATE_FAILED.toString(), fail.getType());

		CreateThemeInspireFailedEvent createThemeInspireFailedEvent = (CreateThemeInspireFailedEvent) fail;

		Map<String, String> arguments = createThemeInspireFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(2, arguments.size());

		assertNotNull(arguments.get("id"));
		assertNotNull(arguments.get("code"));

	}

	@Test
	public void sendThemeInspireUpdateEvent_PublishUpdateThemeInspireFailedEvent_IfNoConstraintsFulfilled()
			throws Exception {

		UpdateThemeInspireEvent event = getUpdateThemeInspireEvent();

		// @formatter:off
			ThemeInspireDTO conflict = getThemeInspire(),
					original = event.getThemeInspire();
			// @formatter:on
		conflict.setId(original.getId() + "cpy");
		conflict.setCode("171");

		// Guarda el que se va a modificar
		repository.save(Mappers.getMapper(ThemeInspireESMapper.class).map(original)); // mapper.getMapperFacade().map(original,
																						// ThemeInspire.class));

		// Guarda el que va a entrar en conflicto
		repository.save(Mappers.getMapper(ThemeInspireESMapper.class).map(conflict)); // mapper.getMapperFacade().map(conflict,
																						// ThemeInspire.class));

		// Edita el code del que se va a modificar para entrar en conflicto
		original.setCode(conflict.getCode());
		event.setThemeInspire(original);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(THEME_INSPIRE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(original.getId());
		repository.delete(conflict.getId());

		assertNotNull(fail);
		assertEquals(ThemeInspireEventTypes.UPDATE_FAILED.toString(), fail.getType());

		UpdateThemeInspireFailedEvent createThemeInspireFailedEvent = (UpdateThemeInspireFailedEvent) fail;

		Map<String, String> arguments = createThemeInspireFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(arguments.size(), 1);

		assertNotNull(arguments.get("code"));
	}

	@Test
	public void sendThemeInspireDeleteEvent_PublishDeleteThemeInspireFailedEvent_IfNoConstraintsFulfilled()
			throws Exception {

		// TODO: Implementar cuando se metan las referencias en la vista.
		assertTrue(true);
	}

	@KafkaHandler
	public void createTypeThemeInspireConfirmed(CreateThemeInspireConfirmedEvent createThemeInspireConfirmedEvent) {

		blockingQueue.offer(createThemeInspireConfirmedEvent);
	}

	@KafkaHandler
	public void createThemeInspireFailed(CreateThemeInspireFailedEvent createThemeInspireFailedEvent) {

		blockingQueue.offer(createThemeInspireFailedEvent);
	}

	@KafkaHandler
	public void updateThemeInspireConfirmed(UpdateThemeInspireConfirmedEvent updateThemeInspireConfirmedEvent) {

		blockingQueue.offer(updateThemeInspireConfirmedEvent);
	}

	@KafkaHandler
	public void updateThemeInspireFailed(UpdateThemeInspireFailedEvent updateThemeInspireFailedEvent) {

		blockingQueue.offer(updateThemeInspireFailedEvent);
	}

	@KafkaHandler
	public void deleteThemeInspireConfirmed(DeleteThemeInspireConfirmedEvent deleteThemeInspireConfirmedEvent) {

		blockingQueue.offer(deleteThemeInspireConfirmedEvent);
	}

	@KafkaHandler
	public void deleteThemeInspireFailed(DeleteThemeInspireFailedEvent deleteThemeInspireFailedEvent) {

		blockingQueue.offer(deleteThemeInspireFailedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

	protected ThemeInspireDTO getThemeInspire() {

		ThemeInspireDTO themeInspire = new ThemeInspireDTO();
		themeInspire.setId("themeinspire-code-gg");
		themeInspire.setCode("gg");
		themeInspire.setName("Sistema de cuadrículas geográficas");
		themeInspire.setName_en("Geographical grid systems");
		return themeInspire;
	}

	protected CreateThemeInspireEvent getCreateThemeInspireEvent() {

		CreateThemeInspireEvent createdEvent = new CreateThemeInspireEvent();
		createdEvent.setId(UUID.randomUUID().toString());
		createdEvent.setDate(DateTime.now());
		createdEvent.setType(ThemeInspireEventTypes.CREATE);
		createdEvent.setThemeInspire(getThemeInspire());
		createdEvent.setAggregateId(createdEvent.getThemeInspire().getId());
		createdEvent.setVersion(1);
		createdEvent.setSessionId(UUID.randomUUID().toString());
		createdEvent.setUserId(USER_ID);
		return createdEvent;
	}

	protected UpdateThemeInspireEvent getUpdateThemeInspireEvent() {

		UpdateThemeInspireEvent updatedEvent = new UpdateThemeInspireEvent();
		updatedEvent.setId(UUID.randomUUID().toString());
		updatedEvent.setDate(DateTime.now());
		updatedEvent.setType(ThemeInspireEventTypes.UPDATE);
		ThemeInspireDTO themeInspire = getThemeInspire();
		themeInspire.setName(themeInspire.getName() + "2");
		updatedEvent.setThemeInspire(themeInspire);
		updatedEvent.setAggregateId(updatedEvent.getThemeInspire().getId());
		updatedEvent.setVersion(2);
		updatedEvent.setSessionId(UUID.randomUUID().toString());
		updatedEvent.setUserId(USER_ID);
		return updatedEvent;
	}

	protected DeleteThemeInspireEvent getDeleteThemeInspireEvent() {

		DeleteThemeInspireEvent deletedEvent = new DeleteThemeInspireEvent();
		deletedEvent.setId(UUID.randomUUID().toString());
		deletedEvent.setDate(DateTime.now());
		deletedEvent.setType(ThemeInspireEventTypes.DELETE);
		deletedEvent.setAggregateId(getThemeInspire().getId());
		deletedEvent.setVersion(3);
		deletedEvent.setSessionId(UUID.randomUUID().toString());
		deletedEvent.setUserId(USER_ID);
		return deletedEvent;
	}
}
