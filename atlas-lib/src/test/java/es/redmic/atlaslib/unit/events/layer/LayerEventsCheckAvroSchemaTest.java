package es.redmic.atlaslib.unit.events.layer;

/*-
 * #%L
 * Atlas-lib
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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerFailedEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;
import es.redmic.atlaslib.unit.utils.AtlasAvroBaseTest;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;

public class LayerEventsCheckAvroSchemaTest extends AtlasAvroBaseTest {

	// Create

	@Test
	public void CreateLayerEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateLayerEvent event = LayerDataUtil.getCreateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateLayerEvent",
				CreateLayerEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateLayerConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateLayerConfirmedEvent event = LayerDataUtil.getCreateLayerConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateLayerConfirmedEvent",
				CreateLayerConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void LayerCreatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		LayerCreatedEvent event = LayerDataUtil.getLayerCreatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de LayerCreatedEvent",
				LayerCreatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateLayerFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateLayerFailedEvent event = LayerDataUtil.getCreateLayerFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateLayerFailedEvent",
				CreateLayerFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateLayerCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateLayerCancelledEvent event = LayerDataUtil.getCreateLayerCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateLayerCancelledEvent",
				CreateLayerCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Update

	@Test
	public void UpdateLayerEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateLayerEvent event = LayerDataUtil.getUpdateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateLayerEvent",
				UpdateLayerEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateLayerConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateLayerConfirmedEvent event = LayerDataUtil.getUpdateLayerConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateLayerConfirmedEvent",
				UpdateLayerConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void LayerUpdatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		LayerUpdatedEvent event = LayerDataUtil.getLayerUpdatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de LayerUpdatedEvent",
				LayerUpdatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateLayerFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateLayerFailedEvent event = LayerDataUtil.getUpdateLayerFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateLayerFailedEvent",
				UpdateLayerFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateLayerCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateLayerCancelledEvent event = LayerDataUtil.getUpdateLayerCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateLayerCancelledEvent",
				UpdateLayerCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Delete

	@Test
	public void DeleteLayerEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteLayerEvent event = LayerDataUtil.getDeleteEvent();

		event.setParentId("category-1234");

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteLayerEvent",
				DeleteLayerEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CheckDeleteLayerEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CheckDeleteLayerEvent event = LayerDataUtil.getCheckDeleteLayerEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CheckDeleteLayerEvent",
				CheckDeleteLayerEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteLayerCheckedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteLayerCheckedEvent event = LayerDataUtil.getDeleteLayerCheckedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteLayerCheckedEvent",
				DeleteLayerCheckedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteLayerCheckFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteLayerCheckFailedEvent event = LayerDataUtil.getDeleteLayerCheckFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteLayerCheckFailedEvent",
				DeleteLayerCheckFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteLayerConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteLayerConfirmedEvent event = LayerDataUtil.getDeleteLayerConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteLayerConfirmedEvent",
				DeleteLayerConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void LayerDeletedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		LayerDeletedEvent event = LayerDataUtil.getLayerDeletedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de LayerDeletedEvent",
				LayerDeletedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteLayerFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteLayerFailedEvent event = LayerDataUtil.getDeleteLayerFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteLayerFailedEvent",
				DeleteLayerFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteLayerCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteLayerCancelledEvent event = LayerDataUtil.getDeleteLayerCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteLayerCancelledEvent",
				DeleteLayerCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}
}
