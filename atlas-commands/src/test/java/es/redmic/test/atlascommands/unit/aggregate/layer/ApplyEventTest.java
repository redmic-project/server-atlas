package es.redmic.test.atlascommands.unit.aggregate.layer;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import es.redmic.atlascommands.aggregate.LayerAggregate;
import es.redmic.atlascommands.statestore.LayerStateStore;
import es.redmic.atlaslib.events.layer.common.LayerCancelledEvent;
import es.redmic.atlaslib.events.layer.common.LayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.create.LayerCreatedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.refresh.LayerRefreshedEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.restlib.config.UserService;

@RunWith(MockitoJUnitRunner.class)
public class ApplyEventTest {

	private final String code = UUID.randomUUID().toString();

	LayerStateStore layerStateStore;

	UserService userService;

	LayerAggregate agg;

	@Before
	public void setUp() {

		layerStateStore = Mockito.mock(LayerStateStore.class);

		userService = Mockito.mock(UserService.class);

		agg = new LayerAggregate(layerStateStore, userService);
	}

	@Test
	public void applyLayerCreatedEvent_ChangeAggregateState_IfProcessIsOk() {

		LayerCreatedEvent evt = LayerDataUtil.getLayerCreatedEvent(code);

		agg.apply(evt);

		checkCreatedUpdatedOrRefreshedState(evt);
	}

	@Test
	public void applyLayerUpdatedEvent_ChangeAggregateState_IfProcessIsOk() {

		LayerUpdatedEvent evt = LayerDataUtil.getLayerUpdatedEvent(code);

		agg.apply(evt);

		checkCreatedUpdatedOrRefreshedState(evt);
	}

	@Test
	public void applyLayerRefreshedEvent_ChangeAggregateState_IfProcessIsOk() {

		LayerRefreshedEvent evt = LayerDataUtil.getLayerRefreshedEvent(code);

		agg.apply(evt);

		checkCreatedUpdatedOrRefreshedState(evt);
	}

	@Test
	public void applyLayerDeletedEvent_ChangeAggregateState_IfProcessIsOk() {

		LayerDeletedEvent evt = LayerDataUtil.getLayerDeletedEvent(code);

		agg.apply(evt);

		checkDeletedState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreated_IfEventIsCreated() {

		LayerCreatedEvent evt = LayerDataUtil.getLayerCreatedEvent(code);

		agg.loadFromHistory(evt);

		checkCreatedUpdatedOrRefreshedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsCreate() {

		CreateLayerEvent evt = LayerDataUtil.getCreateEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdated_IfEventIsUpdated() {

		LayerUpdatedEvent evt = LayerDataUtil.getLayerUpdatedEvent(code);

		agg.loadFromHistory(evt);

		checkCreatedUpdatedOrRefreshedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsUpdate() {

		UpdateLayerEvent evt = LayerDataUtil.getUpdateEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToRefreshed_IfEventIsUpdated() {

		LayerRefreshedEvent evt = LayerDataUtil.getLayerRefreshedEvent(code);

		agg.loadFromHistory(evt);

		checkCreatedUpdatedOrRefreshedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsRefresh() {

		RefreshLayerEvent evt = LayerDataUtil.getRefreshEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleted_IfLastEventIsDeleted() {

		List<Event> history = new ArrayList<>();

		history.add(LayerDataUtil.getLayerCreatedEvent(code));
		history.add(LayerDataUtil.getLayerUpdatedEvent(code));

		history.add(LayerDataUtil.getLayerDeletedEvent(code));

		agg.loadFromHistory(history);

		checkDeletedState((LayerDeletedEvent) history.get(2));
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsDelete() {

		DeleteLayerEvent evt = LayerDataUtil.getDeleteEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdateCancelled_IfLastEventIsUpdateCancelled() {

		List<Event> history = new ArrayList<>();

		history.add(LayerDataUtil.getLayerCreatedEvent(code));
		history.add(LayerDataUtil.getLayerUpdatedEvent(code));

		history.add(LayerDataUtil.getUpdateLayerCancelledEvent(code));

		agg.loadFromHistory(history);

		checkCancelledState((LayerCancelledEvent) history.get(2));
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleteCancelled_IfLastEventIsDeleteCancelled() {

		List<Event> history = new ArrayList<>();

		history.add(LayerDataUtil.getLayerCreatedEvent(code));
		history.add(LayerDataUtil.getLayerUpdatedEvent(code));

		history.add(LayerDataUtil.getDeleteLayerCancelledEvent(code));

		agg.loadFromHistory(history);

		checkCancelledState((LayerCancelledEvent) history.get(2));
	}

	private void checkCancelledState(LayerCancelledEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getLayer(), evt.getLayer());
		assertFalse(agg.isDeleted());
	}

	private void checkCreatedUpdatedOrRefreshedState(LayerEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getLayer(), evt.getLayer());
		assertFalse(agg.isDeleted());
	}

	private void checkDeletedState(LayerDeletedEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
