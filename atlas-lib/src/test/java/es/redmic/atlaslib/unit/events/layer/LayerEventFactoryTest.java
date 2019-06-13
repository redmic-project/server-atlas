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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import es.redmic.atlaslib.events.layer.LayerEventFactory;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.create.CreateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEnrichedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerFailedEvent;
import es.redmic.atlaslib.events.layer.create.EnrichCreateLayerEvent;
import es.redmic.atlaslib.events.layer.create.LayerCreatedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckFailedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerFailedEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.refresh.LayerRefreshedEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerFailedEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;

public class LayerEventFactoryTest {

	@Test
	public void GetEvent_ReturnDeleteLayerEvent_IfTypeIsDelete() {

		Event source = LayerDataUtil.getDeleteLayerCheckedEvent();
		DeleteLayerEvent event = (DeleteLayerEvent) LayerEventFactory.getEvent(source, LayerEventTypes.DELETE);

		assertEquals(LayerEventTypes.DELETE, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteLayerCheckedEvent_IfTypeIsDelete_Checked() {

		Event source = LayerDataUtil.getCheckDeleteLayerEvent();
		DeleteLayerCheckedEvent event = (DeleteLayerCheckedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.DELETE_CHECKED);

		assertEquals(LayerEventTypes.DELETE_CHECKED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnCreateLayerConfirmedEvent_IfTypeIsCreateConfirmed() {

		Event source = LayerDataUtil.getCreateLayerConfirmedEvent();
		CreateLayerConfirmedEvent event = (CreateLayerConfirmedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.CREATE_CONFIRMED);

		assertEquals(LayerEventTypes.CREATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnUpdateLayerConfirmedEvent_IfTypeIsUpdateConfirmed() {

		Event source = LayerDataUtil.getUpdateLayerConfirmedEvent();
		UpdateLayerConfirmedEvent event = (UpdateLayerConfirmedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.UPDATE_CONFIRMED);

		assertEquals(LayerEventTypes.UPDATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteLayerConfirmedEvent_IfTypeIsDeleteConfirmed() {

		Event source = LayerDataUtil.getDeleteLayerConfirmedEvent();
		DeleteLayerConfirmedEvent event = (DeleteLayerConfirmedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.DELETE_CONFIRMED);

		assertEquals(LayerEventTypes.DELETE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteLayerCheckedEvent_IfTypeIsDeleted() {

		Event source = LayerDataUtil.getDeleteLayerConfirmedEvent();
		LayerDeletedEvent event = (LayerDeletedEvent) LayerEventFactory.getEvent(source, LayerEventTypes.DELETED);

		assertEquals(LayerEventTypes.DELETED, event.getType());

		checkMetadataFields(source, event);
	}

	/////////////////////////

	@Test
	public void GetEvent_ReturnEnrichCreateLayerEvent_IfTypeIsEnrichCreate() {

		Event source = LayerDataUtil.getEnrichCreateLayerEvent();
		EnrichCreateLayerEvent event = (EnrichCreateLayerEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.ENRICH_CREATE, LayerDataUtil.getLayer());

		assertEquals(LayerEventTypes.ENRICH_CREATE, event.getType());
		assertNotNull(event.getLayer());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnCreateEnrichedLayerEvent_IfTypeIsEnrichCreate() {

		Event source = LayerDataUtil.getCreateLayerEnrichedEvent();
		CreateLayerEnrichedEvent event = (CreateLayerEnrichedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.CREATE_ENRICHED, LayerDataUtil.getLayer());

		assertEquals(LayerEventTypes.CREATE_ENRICHED, event.getType());
		assertNotNull(event.getLayer());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnLayerCreatedEvent_IfTypeIsCreated() {

		Event source = LayerDataUtil.getCreateLayerConfirmedEvent();
		LayerCreatedEvent event = (LayerCreatedEvent) LayerEventFactory.getEvent(source, LayerEventTypes.CREATED,
				LayerDataUtil.getLayer());

		assertEquals(LayerEventTypes.CREATED, event.getType());
		assertNotNull(event.getLayer());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnLayerCreatedEvent_IfTypeIsUpdated() {

		Event source = LayerDataUtil.getUpdateLayerConfirmedEvent();
		LayerUpdatedEvent event = (LayerUpdatedEvent) LayerEventFactory.getEvent(source, LayerEventTypes.UPDATED,
				LayerDataUtil.getLayer());

		assertEquals(LayerEventTypes.UPDATED, event.getType());
		assertNotNull(event.getLayer());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnLayerRefreshedEvent_IfTypeIsRefreshed() {

		Event source = LayerDataUtil.getLayerRefreshedEvent();
		LayerRefreshedEvent event = (LayerRefreshedEvent) LayerEventFactory.getEvent(source, LayerEventTypes.REFRESHED,
				LayerDataUtil.getLayer());

		assertEquals(LayerEventTypes.REFRESHED, event.getType());
		assertNotNull(event.getLayer());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnRefreshLayerConfirmedEvent_IfTypeIsRefreshConfirmed() {

		Event source = LayerDataUtil.getRefreshLayerConfirmedEvent();
		RefreshLayerConfirmedEvent event = (RefreshLayerConfirmedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.REFRESH_CONFIRMED, LayerDataUtil.getLayer());

		assertEquals(LayerEventTypes.REFRESH_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	///////////////////

	@Test
	public void GetEvent_ReturnCreateLayerFailedEvent_IfTypeIsCreateFailed() {

		CreateLayerFailedEvent exception = LayerDataUtil.getCreateLayerFailedEvent();

		Event source = LayerDataUtil.getCreateEvent();

		CreateLayerFailedEvent event = (CreateLayerFailedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.CREATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(LayerEventTypes.CREATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnUpdateLayerFailedEvent_IfTypeIsUpdateFailed() {

		UpdateLayerFailedEvent exception = LayerDataUtil.getUpdateLayerFailedEvent();

		Event source = LayerDataUtil.getUpdateEvent();

		UpdateLayerFailedEvent event = (UpdateLayerFailedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.UPDATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(LayerEventTypes.UPDATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteLayerFailedEvent_IfTypeIsRefreshFailed() {

		RefreshLayerFailedEvent exception = LayerDataUtil.getRefreshLayerFailedEvent();

		Event source = LayerDataUtil.getRefreshEvent();

		RefreshLayerFailedEvent event = (RefreshLayerFailedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.REFRESH_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(LayerEventTypes.REFRESH_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnRefreshLayerFailedEvent_IfTypeIsDeleteFailed() {

		DeleteLayerFailedEvent exception = LayerDataUtil.getDeleteLayerFailedEvent();

		Event source = LayerDataUtil.getDeleteEvent();

		DeleteLayerFailedEvent event = (DeleteLayerFailedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.DELETE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(LayerEventTypes.DELETE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteLayerCheckFailedEvent_IfTypeIsDeleteCheckFailed() {

		DeleteLayerCheckFailedEvent exception = LayerDataUtil.getDeleteLayerCheckFailedEvent();

		Event source = LayerDataUtil.getCheckDeleteLayerEvent();

		DeleteLayerCheckFailedEvent event = (DeleteLayerCheckFailedEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.DELETE_CHECK_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(LayerEventTypes.DELETE_CHECK_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnCreateLayerCancelledEvent_IfTypeIsCreateCancelled() {

		CreateLayerCancelledEvent exception = LayerDataUtil.getCreateLayerCancelledEvent();

		Event source = LayerDataUtil.getCreateEvent();

		CreateLayerCancelledEvent event = (CreateLayerCancelledEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.CREATE_CANCELLED, exception.getExceptionType(), exception.getArguments());

		assertEquals(LayerEventTypes.CREATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	////////////////////

	@Test
	public void GetEvent_ReturnUpdateLayerCancelledEvent_IfTypeIsUpdateCancelled() {

		UpdateLayerCancelledEvent exception = LayerDataUtil.getUpdateLayerCancelledEvent();

		Event source = LayerDataUtil.getUpdateEvent();

		UpdateLayerCancelledEvent event = (UpdateLayerCancelledEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.UPDATE_CANCELLED, LayerDataUtil.getLayer(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(LayerEventTypes.UPDATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getLayer());
	}

	@Test
	public void GetEvent_ReturnDeleteLayerCancelledEvent_IfTypeIsDeleteCancelled() {

		DeleteLayerCancelledEvent exception = LayerDataUtil.getDeleteLayerCancelledEvent();

		Event source = LayerDataUtil.getDeleteEvent();

		DeleteLayerCancelledEvent event = (DeleteLayerCancelledEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.DELETE_CANCELLED, LayerDataUtil.getLayer(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(LayerEventTypes.DELETE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getLayer());
	}

	@Test
	public void GetEvent_ReturnRefreshLayerCancelledEvent_IfTypeIsRefreshCancelled() {

		RefreshLayerCancelledEvent exception = LayerDataUtil.getRefreshLayerCancelledEvent();

		Event source = LayerDataUtil.getRefreshEvent();

		RefreshLayerCancelledEvent event = (RefreshLayerCancelledEvent) LayerEventFactory.getEvent(source,
				LayerEventTypes.REFRESH_CANCELLED, LayerDataUtil.getLayer(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(LayerEventTypes.REFRESH_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getLayer());
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
