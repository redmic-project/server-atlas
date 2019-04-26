package es.redmic.atlaslib.unit.events.themeinspire;

/*-
 * #%L
 * atlas-lib
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

import org.junit.Test;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventFactory;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.create.ThemeInspireCreatedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireCheckedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireConfirmedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireFailedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.unit.utils.ThemeInspireDataUtil;

public class ThemeInspireEventFactoryTest {

	@Test
	public void GetEvent_ReturnDeleteThemeInspireEvent_IfTypeIsDelete() {

		Event source = ThemeInspireDataUtil.getDeleteThemeInspireCheckedEvent();
		DeleteThemeInspireEvent event = (DeleteThemeInspireEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.DELETE);

		assertEquals(ThemeInspireEventTypes.DELETE, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteThemeInspireCheckedEvent_IfTypeIsDelete_Checked() {

		Event source = ThemeInspireDataUtil.getCheckDeleteThemeInspireEvent();
		DeleteThemeInspireCheckedEvent event = (DeleteThemeInspireCheckedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.DELETE_CHECKED);

		assertEquals(ThemeInspireEventTypes.DELETE_CHECKED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnCreateThemeInspireConfirmedEvent_IfTypeIsCreateConfirmed() {

		Event source = ThemeInspireDataUtil.getCreateThemeInspireConfirmedEvent();
		CreateThemeInspireConfirmedEvent event = (CreateThemeInspireConfirmedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.CREATE_CONFIRMED);

		assertEquals(ThemeInspireEventTypes.CREATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnUpdateThemeInspireConfirmedEvent_IfTypeIsUpdateConfirmed() {

		Event source = ThemeInspireDataUtil.getUpdateThemeInspireConfirmedEvent();
		UpdateThemeInspireConfirmedEvent event = (UpdateThemeInspireConfirmedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.UPDATE_CONFIRMED);

		assertEquals(ThemeInspireEventTypes.UPDATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteThemeInspireConfirmedEvent_IfTypeIsDeleteConfirmed() {

		Event source = ThemeInspireDataUtil.getDeleteThemeInspireConfirmedEvent();
		DeleteThemeInspireConfirmedEvent event = (DeleteThemeInspireConfirmedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.DELETE_CONFIRMED);

		assertEquals(ThemeInspireEventTypes.DELETE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteThemeInspireCheckedEvent_IfTypeIsDeleted() {

		Event source = ThemeInspireDataUtil.getDeleteThemeInspireConfirmedEvent();
		ThemeInspireDeletedEvent event = (ThemeInspireDeletedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.DELETED);

		assertEquals(ThemeInspireEventTypes.DELETED, event.getType());

		checkMetadataFields(source, event);
	}

	/////////////////////////

	@Test
	public void GetEvent_ReturnThemeInspireCreatedEvent_IfTypeIsCreated() {

		Event source = ThemeInspireDataUtil.getCreateThemeInspireConfirmedEvent();
		ThemeInspireCreatedEvent event = (ThemeInspireCreatedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.CREATED, ThemeInspireDataUtil.getThemeInspire());

		assertEquals(ThemeInspireEventTypes.CREATED, event.getType());
		assertNotNull(event.getThemeInspire());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnThemeInspireCreatedEvent_IfTypeIsUpdated() {

		Event source = ThemeInspireDataUtil.getUpdateThemeInspireConfirmedEvent();
		ThemeInspireUpdatedEvent event = (ThemeInspireUpdatedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.UPDATED, ThemeInspireDataUtil.getThemeInspire());

		assertEquals(ThemeInspireEventTypes.UPDATED, event.getType());
		assertNotNull(event.getThemeInspire());

		checkMetadataFields(source, event);
	}

	///////////////////

	@Test
	public void GetEvent_ReturnCreateThemeInspireFailedEvent_IfTypeIsCreateFailed() {

		CreateThemeInspireFailedEvent exception = ThemeInspireDataUtil.getCreateThemeInspireFailedEvent();

		Event source = ThemeInspireDataUtil.getCreateEvent();

		CreateThemeInspireFailedEvent event = (CreateThemeInspireFailedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.CREATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(ThemeInspireEventTypes.CREATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnUpdateThemeInspireFailedEvent_IfTypeIsUpdateFailed() {

		UpdateThemeInspireFailedEvent exception = ThemeInspireDataUtil.getUpdateThemeInspireFailedEvent();

		Event source = ThemeInspireDataUtil.getUpdateEvent();

		UpdateThemeInspireFailedEvent event = (UpdateThemeInspireFailedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.UPDATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(ThemeInspireEventTypes.UPDATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteThemeInspireFailedEventt_IfTypeIsDeleteFailed() {

		DeleteThemeInspireFailedEvent exception = ThemeInspireDataUtil.getDeleteThemeInspireFailedEvent();

		Event source = ThemeInspireDataUtil.getDeleteEvent();

		DeleteThemeInspireFailedEvent event = (DeleteThemeInspireFailedEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.DELETE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(ThemeInspireEventTypes.DELETE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteThemeInspireCheckFailedEvent_IfTypeIsDeleteCheckFailed() {

		DeleteThemeInspireCheckFailedEvent exception = ThemeInspireDataUtil.getDeleteThemeInspireCheckFailedEvent();

		Event source = ThemeInspireDataUtil.getCheckDeleteThemeInspireEvent();

		DeleteThemeInspireCheckFailedEvent event = (DeleteThemeInspireCheckFailedEvent) ThemeInspireEventFactory.getEvent(
				source, ThemeInspireEventTypes.DELETE_CHECK_FAILED, exception.getExceptionType(),
				exception.getArguments());

		assertEquals(ThemeInspireEventTypes.DELETE_CHECK_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnCreateThemeInspireCancelledEvent_IfTypeIsCreateCancelled() {

		CreateThemeInspireCancelledEvent exception = ThemeInspireDataUtil.getCreateThemeInspireCancelledEvent();

		Event source = ThemeInspireDataUtil.getCreateEvent();

		CreateThemeInspireCancelledEvent event = (CreateThemeInspireCancelledEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.CREATE_CANCELLED, exception.getExceptionType(), exception.getArguments());

		assertEquals(ThemeInspireEventTypes.CREATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	////////////////////

	@Test
	public void GetEvent_ReturnUpdateThemeInspireCancelledEvent_IfTypeIsUpdateCancelled() {

		UpdateThemeInspireCancelledEvent exception = ThemeInspireDataUtil.getUpdateThemeInspireCancelledEvent();

		Event source = ThemeInspireDataUtil.getUpdateEvent();

		UpdateThemeInspireCancelledEvent event = (UpdateThemeInspireCancelledEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.UPDATE_CANCELLED, ThemeInspireDataUtil.getThemeInspire(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(ThemeInspireEventTypes.UPDATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getThemeInspire());
	}

	@Test
	public void GetEvent_ReturnDeleteThemeInspireCancelledEvent_IfTypeIsDeleteCancelled() {

		DeleteThemeInspireCancelledEvent exception = ThemeInspireDataUtil.getDeleteThemeInspireCancelledEvent();

		Event source = ThemeInspireDataUtil.getDeleteEvent();

		DeleteThemeInspireCancelledEvent event = (DeleteThemeInspireCancelledEvent) ThemeInspireEventFactory.getEvent(source,
				ThemeInspireEventTypes.DELETE_CANCELLED, ThemeInspireDataUtil.getThemeInspire(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(ThemeInspireEventTypes.DELETE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getThemeInspire());
	}

	////////////////////

	private void checkMetadataFields(Event source, Event evt) {

		assertEquals(source.getAggregateId(), evt.getAggregateId());
		assertEquals(source.getVersion(), evt.getVersion());
		assertEquals(source.getSessionId(), evt.getSessionId());
		assertEquals(source.getUserId(), evt.getUserId());
	}

	private void checkErrorFields(EventError source, EventError evt) {

		assertEquals(source.getExceptionType(), evt.getExceptionType());
		assertEquals(source.getArguments(), evt.getArguments());
	}

}
