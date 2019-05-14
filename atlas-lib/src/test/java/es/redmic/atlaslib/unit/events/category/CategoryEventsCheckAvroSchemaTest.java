package es.redmic.atlaslib.unit.events.category;

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
import es.redmic.atlaslib.events.category.delete.DeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryFailedEvent;
import es.redmic.atlaslib.events.category.update.CategoryUpdatedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryFailedEvent;
import es.redmic.atlaslib.unit.utils.AtlasAvroBaseTest;
import es.redmic.atlaslib.unit.utils.CategoryDataUtil;

public class CategoryEventsCheckAvroSchemaTest extends AtlasAvroBaseTest {

	// Create

	@Test
	public void CreateCategoryEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateCategoryEvent event = CategoryDataUtil.getCreateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateCategoryEvent",
				CreateCategoryEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateCategoryConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateCategoryConfirmedEvent event = CategoryDataUtil.getCreateCategoryConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateCategoryConfirmedEvent",
				CreateCategoryConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CategoryCreatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CategoryCreatedEvent event = CategoryDataUtil.getCategoryCreatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CategoryCreatedEvent",
				CategoryCreatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateCategoryFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateCategoryFailedEvent event = CategoryDataUtil.getCreateCategoryFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateCategoryFailedEvent",
				CreateCategoryFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateCategoryCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateCategoryCancelledEvent event = CategoryDataUtil.getCreateCategoryCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateCategoryCancelledEvent",
				CreateCategoryCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Update

	@Test
	public void UpdateCategoryEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateCategoryEvent event = CategoryDataUtil.getUpdateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateCategoryEvent",
				UpdateCategoryEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateCategoryConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateCategoryConfirmedEvent event = CategoryDataUtil.getUpdateCategoryConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateCategoryConfirmedEvent",
				UpdateCategoryConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CategoryUpdatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CategoryUpdatedEvent event = CategoryDataUtil.getCategoryUpdatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CategoryUpdatedEvent",
				CategoryUpdatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateCategoryFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateCategoryFailedEvent event = CategoryDataUtil.getUpdateCategoryFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateCategoryFailedEvent",
				UpdateCategoryFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateCategoryCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateCategoryCancelledEvent event = CategoryDataUtil.getUpdateCategoryCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateCategoryCancelledEvent",
				UpdateCategoryCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Delete

	@Test
	public void DeleteCategoryEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteCategoryEvent event = CategoryDataUtil.getDeleteEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteCategoryEvent",
				DeleteCategoryEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CheckDeleteCategoryEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CheckDeleteCategoryEvent event = CategoryDataUtil.getCheckDeleteCategoryEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CheckDeleteCategoryEvent",
				CheckDeleteCategoryEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteCategoryCheckedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteCategoryCheckedEvent event = CategoryDataUtil.getDeleteCategoryCheckedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteCategoryCheckedEvent",
				DeleteCategoryCheckedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteCategoryCheckFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteCategoryCheckFailedEvent event = CategoryDataUtil.getDeleteCategoryCheckFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteCategoryCheckFailedEvent",
				DeleteCategoryCheckFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteCategoryConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteCategoryConfirmedEvent event = CategoryDataUtil.getDeleteCategoryConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteCategoryConfirmedEvent",
				DeleteCategoryConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CategoryDeletedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CategoryDeletedEvent event = CategoryDataUtil.getCategoryDeletedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CategoryDeletedEvent",
				CategoryDeletedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteCategoryFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteCategoryFailedEvent event = CategoryDataUtil.getDeleteCategoryFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteCategoryFailedEvent",
				DeleteCategoryFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteCategoryCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteCategoryCancelledEvent event = CategoryDataUtil.getDeleteCategoryCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteCategoryCancelledEvent",
				DeleteCategoryCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}
}
