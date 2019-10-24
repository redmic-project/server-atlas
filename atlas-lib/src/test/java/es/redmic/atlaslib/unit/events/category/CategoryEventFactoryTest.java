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
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import es.redmic.atlaslib.events.category.CategoryEventFactory;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.create.CategoryCreatedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryFailedEvent;
import es.redmic.atlaslib.events.category.delete.CategoryDeletedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckFailedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryCheckedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryFailedEvent;
import es.redmic.atlaslib.events.category.fail.CategoryRollbackEvent;
import es.redmic.atlaslib.events.category.update.CategoryUpdatedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryCancelledEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryConfirmedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryFailedEvent;
import es.redmic.atlaslib.unit.utils.CategoryDataUtil;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.fail.PrepareRollbackEvent;
import es.redmic.testutils.utils.AvroBaseTest;

public class CategoryEventFactoryTest extends AvroBaseTest {

	@Test
	public void GetEvent_ReturnDeleteCategoryEvent_IfTypeIsDelete() {

		Event source = CategoryDataUtil.getDeleteCategoryCheckedEvent();
		DeleteCategoryEvent event = (DeleteCategoryEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.DELETE);

		assertEquals(CategoryEventTypes.DELETE, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteCategoryCheckedEvent_IfTypeIsDelete_Checked() {

		Event source = CategoryDataUtil.getCheckDeleteCategoryEvent();
		DeleteCategoryCheckedEvent event = (DeleteCategoryCheckedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.DELETE_CHECKED);

		assertEquals(CategoryEventTypes.DELETE_CHECKED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnCreateCategoryConfirmedEvent_IfTypeIsCreateConfirmed() {

		Event source = CategoryDataUtil.getCreateCategoryConfirmedEvent();
		CreateCategoryConfirmedEvent event = (CreateCategoryConfirmedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.CREATE_CONFIRMED);

		assertEquals(CategoryEventTypes.CREATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnUpdateCategoryConfirmedEvent_IfTypeIsUpdateConfirmed() {

		Event source = CategoryDataUtil.getUpdateCategoryConfirmedEvent();
		UpdateCategoryConfirmedEvent event = (UpdateCategoryConfirmedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.UPDATE_CONFIRMED);

		assertEquals(CategoryEventTypes.UPDATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteCategoryConfirmedEvent_IfTypeIsDeleteConfirmed() {

		Event source = CategoryDataUtil.getDeleteCategoryConfirmedEvent();
		DeleteCategoryConfirmedEvent event = (DeleteCategoryConfirmedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.DELETE_CONFIRMED);

		assertEquals(CategoryEventTypes.DELETE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteCategoryCheckedEvent_IfTypeIsDeleted() {

		Event source = CategoryDataUtil.getDeleteCategoryConfirmedEvent();
		CategoryDeletedEvent event = (CategoryDeletedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.DELETED);

		assertEquals(CategoryEventTypes.DELETED, event.getType());

		checkMetadataFields(source, event);
	}

	/////////////////////////

	@Test
	public void GetEvent_ReturnCategoryCreatedEvent_IfTypeIsCreated() {

		Event source = CategoryDataUtil.getCreateCategoryConfirmedEvent();
		CategoryCreatedEvent event = (CategoryCreatedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.CREATED, CategoryDataUtil.getCategory());

		assertEquals(CategoryEventTypes.CREATED, event.getType());
		assertNotNull(event.getCategory());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnCategoryCreatedEvent_IfTypeIsUpdated() {

		Event source = CategoryDataUtil.getUpdateCategoryConfirmedEvent();
		CategoryUpdatedEvent event = (CategoryUpdatedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.UPDATED, CategoryDataUtil.getCategory());

		assertEquals(CategoryEventTypes.UPDATED, event.getType());
		assertNotNull(event.getCategory());

		checkMetadataFields(source, event);
	}

	///////////////////

	@Test
	public void GetEvent_ReturnCreateCategoryFailedEvent_IfTypeIsCreateFailed() {

		CreateCategoryFailedEvent exception = CategoryDataUtil.getCreateCategoryFailedEvent();

		Event source = CategoryDataUtil.getCreateEvent();

		CreateCategoryFailedEvent event = (CreateCategoryFailedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.CREATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(CategoryEventTypes.CREATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnUpdateCategoryFailedEvent_IfTypeIsUpdateFailed() {

		UpdateCategoryFailedEvent exception = CategoryDataUtil.getUpdateCategoryFailedEvent();

		Event source = CategoryDataUtil.getUpdateEvent();

		UpdateCategoryFailedEvent event = (UpdateCategoryFailedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.UPDATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(CategoryEventTypes.UPDATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteCategoryFailedEventt_IfTypeIsDeleteFailed() {

		DeleteCategoryFailedEvent exception = CategoryDataUtil.getDeleteCategoryFailedEvent();

		Event source = CategoryDataUtil.getDeleteEvent();

		DeleteCategoryFailedEvent event = (DeleteCategoryFailedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.DELETE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(CategoryEventTypes.DELETE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteCategoryCheckFailedEvent_IfTypeIsDeleteCheckFailed() {

		DeleteCategoryCheckFailedEvent exception = CategoryDataUtil.getDeleteCategoryCheckFailedEvent();

		Event source = CategoryDataUtil.getCheckDeleteCategoryEvent();

		DeleteCategoryCheckFailedEvent event = (DeleteCategoryCheckFailedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.DELETE_CHECK_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(CategoryEventTypes.DELETE_CHECK_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnCreateCategoryCancelledEvent_IfTypeIsCreateCancelled() {

		CreateCategoryCancelledEvent exception = CategoryDataUtil.getCreateCategoryCancelledEvent();

		Event source = CategoryDataUtil.getCreateEvent();

		CreateCategoryCancelledEvent event = (CreateCategoryCancelledEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.CREATE_CANCELLED, exception.getExceptionType(), exception.getArguments());

		assertEquals(CategoryEventTypes.CREATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	////////////////////

	@Test
	public void GetEvent_ReturnUpdateCategoryCancelledEvent_IfTypeIsUpdateCancelled() {

		UpdateCategoryCancelledEvent exception = CategoryDataUtil.getUpdateCategoryCancelledEvent();

		Event source = CategoryDataUtil.getUpdateEvent();

		UpdateCategoryCancelledEvent event = (UpdateCategoryCancelledEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.UPDATE_CANCELLED, CategoryDataUtil.getCategory(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(CategoryEventTypes.UPDATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getCategory());
	}

	@Test
	public void GetEvent_ReturnDeleteCategoryCancelledEvent_IfTypeIsDeleteCancelled() {

		DeleteCategoryCancelledEvent exception = CategoryDataUtil.getDeleteCategoryCancelledEvent();

		Event source = CategoryDataUtil.getDeleteEvent();

		DeleteCategoryCancelledEvent event = (DeleteCategoryCancelledEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.DELETE_CANCELLED, CategoryDataUtil.getCategory(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(CategoryEventTypes.DELETE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getCategory());
	}

	////////////////////

	@Test
	public void GetEvent_ReturnCategoryRollbackEvent_IfTypeIsRollback() {

		PrepareRollbackEvent source = CategoryDataUtil.getPrepareRollbackEvent();

		CategoryRollbackEvent event = (CategoryRollbackEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.ROLLBACK, CategoryDataUtil.getCategory());

		assertEquals(CategoryEventTypes.ROLLBACK, event.getType());

		checkMetadataFields(source, event);
		assertEquals(source.getFailEventType(), event.getFailEventType());
		assertNotNull(event.getLastSnapshotItem());
	}

	// ROLLBACK

	@Test
	public void GetEvent_ReturnCreateCategoryFailed_IfRollbackFailEventTypeIsCreate() {

		CategoryRollbackEvent source = CategoryDataUtil.getCategoryRollbackEvent(UUID.randomUUID().toString(),
				CategoryEventTypes.CREATE);

		CreateCategoryFailedEvent event = (CreateCategoryFailedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.getEventFailedTypeByActionType(source.getFailEventType()));

		assertEquals(CategoryEventTypes.CREATE_FAILED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnUpdateCategoryFailedEvent_IfRollbackFailEventTypeIsUpdate() {

		CategoryRollbackEvent source = CategoryDataUtil.getCategoryRollbackEvent(UUID.randomUUID().toString(),
				CategoryEventTypes.UPDATE);

		UpdateCategoryFailedEvent event = (UpdateCategoryFailedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.getEventFailedTypeByActionType(source.getFailEventType()));

		assertEquals(CategoryEventTypes.UPDATE_FAILED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteCategoryFailedEvent_IfRollbackFailEventTypeIsDelete() {

		CategoryRollbackEvent source = CategoryDataUtil.getCategoryRollbackEvent(UUID.randomUUID().toString(),
				CategoryEventTypes.DELETE);

		DeleteCategoryFailedEvent event = (DeleteCategoryFailedEvent) CategoryEventFactory.getEvent(source,
				CategoryEventTypes.getEventFailedTypeByActionType(source.getFailEventType()));

		assertEquals(CategoryEventTypes.DELETE_FAILED, event.getType());

		checkMetadataFields(source, event);
	}

	private void checkMetadataFields(Event source, Event evt) {

		assertEquals(source.getAggregateId(), evt.getAggregateId());
		assertEquals(source.getVersion(), evt.getVersion());
		assertEquals(source.getSessionId(), evt.getSessionId());
		assertEquals(source.getUserId(), evt.getUserId());

		serializerAndDeserializer(evt);
	}

	private void checkErrorFields(EventError source, EventError evt) {

		assertEquals(source.getExceptionType(), evt.getExceptionType());
		assertEquals(source.getArguments(), evt.getArguments());

		serializerAndDeserializer(evt);
	}

}
