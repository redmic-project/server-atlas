package es.redmic.atlaslib.unit.events.themeinspire;

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
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.fail.ThemeInspireRollbackEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireFailedEvent;
import es.redmic.atlaslib.unit.utils.ThemeInspireDataUtil;
import es.redmic.testutils.utils.AvroBaseTest;

public class ThemeInspireEventsCheckAvroSchemaTest extends AvroBaseTest {

	// Create

	@Test
	public void CreateThemeInspireEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateThemeInspireEvent event = ThemeInspireDataUtil.getCreateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateThemeInspireEvent",
				CreateThemeInspireEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateThemeInspireConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateThemeInspireConfirmedEvent event = ThemeInspireDataUtil.getCreateThemeInspireConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateThemeInspireConfirmedEvent",
				CreateThemeInspireConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void ThemeInspireCreatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		ThemeInspireCreatedEvent event = ThemeInspireDataUtil.getThemeInspireCreatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de ThemeInspireCreatedEvent",
				ThemeInspireCreatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateThemeInspireFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateThemeInspireFailedEvent event = ThemeInspireDataUtil.getCreateThemeInspireFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateThemeInspireFailedEvent",
				CreateThemeInspireFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateThemeInspireCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateThemeInspireCancelledEvent event = ThemeInspireDataUtil.getCreateThemeInspireCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateThemeInspireCancelledEvent",
				CreateThemeInspireCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Update

	@Test
	public void UpdateThemeInspireEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateThemeInspireEvent event = ThemeInspireDataUtil.getUpdateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateThemeInspireEvent",
				UpdateThemeInspireEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateThemeInspireConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateThemeInspireConfirmedEvent event = ThemeInspireDataUtil.getUpdateThemeInspireConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateThemeInspireConfirmedEvent",
				UpdateThemeInspireConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void ThemeInspireUpdatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		ThemeInspireUpdatedEvent event = ThemeInspireDataUtil.getThemeInspireUpdatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de ThemeInspireUpdatedEvent",
				ThemeInspireUpdatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateThemeInspireFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateThemeInspireFailedEvent event = ThemeInspireDataUtil.getUpdateThemeInspireFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateThemeInspireFailedEvent",
				UpdateThemeInspireFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateThemeInspireCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateThemeInspireCancelledEvent event = ThemeInspireDataUtil.getUpdateThemeInspireCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateThemeInspireCancelledEvent",
				UpdateThemeInspireCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Delete

	@Test
	public void DeleteThemeInspireEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteThemeInspireEvent event = ThemeInspireDataUtil.getDeleteEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteThemeInspireEvent",
				DeleteThemeInspireEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CheckDeleteThemeInspireEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CheckDeleteThemeInspireEvent event = ThemeInspireDataUtil.getCheckDeleteThemeInspireEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CheckDeleteThemeInspireEvent",
				CheckDeleteThemeInspireEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteThemeInspireCheckedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteThemeInspireCheckedEvent event = ThemeInspireDataUtil.getDeleteThemeInspireCheckedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteThemeInspireCheckedEvent",
				DeleteThemeInspireCheckedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteThemeInspireCheckFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteThemeInspireCheckFailedEvent event = ThemeInspireDataUtil.getDeleteThemeInspireCheckFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteThemeInspireCheckFailedEvent",
				DeleteThemeInspireCheckFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteThemeInspireConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteThemeInspireConfirmedEvent event = ThemeInspireDataUtil.getDeleteThemeInspireConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteThemeInspireConfirmedEvent",
				DeleteThemeInspireConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void ThemeInspireDeletedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		ThemeInspireDeletedEvent event = ThemeInspireDataUtil.getThemeInspireDeletedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de ThemeInspireDeletedEvent",
				ThemeInspireDeletedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteThemeInspireFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteThemeInspireFailedEvent event = ThemeInspireDataUtil.getDeleteThemeInspireFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteThemeInspireFailedEvent",
				DeleteThemeInspireFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteThemeInspireCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteThemeInspireCancelledEvent event = ThemeInspireDataUtil.getDeleteThemeInspireCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteThemeInspireCancelledEvent",
				DeleteThemeInspireCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// FAIL

	@Test
	public void ThemeInspireRollbackEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		ThemeInspireRollbackEvent event = ThemeInspireDataUtil.getThemeInspireRollbackEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de ThemeInspireRollbackEvent",
				ThemeInspireRollbackEvent.class.isInstance(result));

		assertEquals(result, event);
	}
}
