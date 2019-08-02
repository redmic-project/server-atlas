package es.redmic.test.atlascommands.unit.aggregate.category;

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

import es.redmic.atlascommands.aggregate.CategoryAggregate;
import es.redmic.atlascommands.statestore.CategoryStateStore;
import es.redmic.atlaslib.events.category.common.CategoryCancelledEvent;
import es.redmic.atlaslib.events.category.common.CategoryEvent;
import es.redmic.atlaslib.events.category.create.CategoryCreatedEvent;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.delete.CategoryDeletedEvent;
import es.redmic.atlaslib.events.category.delete.DeleteCategoryEvent;
import es.redmic.atlaslib.events.category.update.CategoryUpdatedEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.restlib.config.UserService;
import es.redmic.test.atlascommands.integration.category.CategoryDataUtil;

@RunWith(MockitoJUnitRunner.class)
public class ApplyEventTest {

	private final String code = UUID.randomUUID().toString();

	CategoryStateStore categoryStateStore;

	UserService userService;

	CategoryAggregate agg;

	@Before
	public void setUp() {

		categoryStateStore = Mockito.mock(CategoryStateStore.class);

		userService = Mockito.mock(UserService.class);

		agg = new CategoryAggregate(categoryStateStore, userService);
	}

	@Test
	public void applyCategoryCreatedEvent_ChangeAggrefateState_IfProcessIsOk() {

		CategoryCreatedEvent evt = CategoryDataUtil.getCategoryCreatedEvent(code);

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyCategoryUpdatedEvent_ChangeAggregateState_IfProcessIsOk() {

		CategoryUpdatedEvent evt = CategoryDataUtil.getCategoryUpdatedEvent(code);

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyCategoryDeletedEvent_ChangeAggregateState_IfProcessIsOk() {

		CategoryDeletedEvent evt = CategoryDataUtil.getCategoryDeletedEvent(code);

		agg.apply(evt);

		checkDeletedState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreated_IfEventIsCreated() {

		CategoryCreatedEvent evt = CategoryDataUtil.getCategoryCreatedEvent(code);

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsCreate() {

		CreateCategoryEvent evt = CategoryDataUtil.getCreateEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdated_IfEventIsUpdated() {

		CategoryUpdatedEvent evt = CategoryDataUtil.getCategoryUpdatedEvent(code);

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsUpdate() {

		UpdateCategoryEvent evt = CategoryDataUtil.getUpdateEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleted_IfLastEventIsDeleted() {

		List<Event> history = new ArrayList<>();

		history.add(CategoryDataUtil.getCategoryCreatedEvent(code));
		history.add(CategoryDataUtil.getCategoryUpdatedEvent(code));

		history.add(CategoryDataUtil.getCategoryDeletedEvent(code));

		agg.loadFromHistory(history);

		checkDeletedState((CategoryDeletedEvent) history.get(2));
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsDelete() {

		DeleteCategoryEvent evt = CategoryDataUtil.getDeleteEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdateCancelled_IfLastEventIsUpdateCancelled() {

		List<Event> history = new ArrayList<>();

		history.add(CategoryDataUtil.getCategoryCreatedEvent(code));
		history.add(CategoryDataUtil.getCategoryUpdatedEvent(code));

		history.add(CategoryDataUtil.getUpdateCategoryCancelledEvent(code));

		agg.loadFromHistory(history);

		checkCancelledState((CategoryCancelledEvent) history.get(2));
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleteCancelled_IfLastEventIsDeleteCancelled() {

		List<Event> history = new ArrayList<>();

		history.add(CategoryDataUtil.getCategoryCreatedEvent(code));
		history.add(CategoryDataUtil.getCategoryUpdatedEvent(code));

		history.add(CategoryDataUtil.getDeleteCategoryCancelledEvent(code));

		agg.loadFromHistory(history);

		checkCancelledState((CategoryCancelledEvent) history.get(2));
	}

	private void checkCancelledState(CategoryCancelledEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getCategory(), evt.getCategory());
		assertFalse(agg.isDeleted());
	}

	private void checkCreatedOrUpdatedState(CategoryEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getCategory(), evt.getCategory());
		assertFalse(agg.isDeleted());
	}

	private void checkDeletedState(CategoryDeletedEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
