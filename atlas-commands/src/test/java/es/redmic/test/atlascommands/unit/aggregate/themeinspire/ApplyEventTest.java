package es.redmic.test.atlascommands.unit.aggregate.themeinspire;

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import es.redmic.atlascommands.aggregate.ThemeInspireAggregate;
import es.redmic.atlascommands.statestore.ThemeInspireStateStore;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireCancelledEvent;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.ThemeInspireCreatedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.atlaslib.unit.utils.ThemeInspireDataUtil;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.restlib.config.UserService;

@RunWith(MockitoJUnitRunner.class)
public class ApplyEventTest {

	private final String code = "gg";

	ThemeInspireStateStore themeInspireStateStore;

	UserService userService;

	ThemeInspireAggregate agg;

	@Before
	public void setUp() {

		themeInspireStateStore = Mockito.mock(ThemeInspireStateStore.class);

		userService = Mockito.mock(UserService.class);

		agg = new ThemeInspireAggregate(themeInspireStateStore, userService);
	}

	@Test
	public void applyThemeInspireCreatedEvent_ChangeAggrefateState_IfProcessIsOk() {

		ThemeInspireCreatedEvent evt = ThemeInspireDataUtil.getThemeInspireCreatedEvent(code);

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyThemeInspireUpdatedEvent_ChangeAggregateState_IfProcessIsOk() {

		ThemeInspireUpdatedEvent evt = ThemeInspireDataUtil.getThemeInspireUpdatedEvent(code);

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyThemeInspireDeletedEvent_ChangeAggregateState_IfProcessIsOk() {

		ThemeInspireDeletedEvent evt = ThemeInspireDataUtil.getThemeInspireDeletedEvent(code);

		agg.apply(evt);

		checkDeletedState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreated_IfEventIsCreated() {

		ThemeInspireCreatedEvent evt = ThemeInspireDataUtil.getThemeInspireCreatedEvent(code);

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsCreate() {

		CreateThemeInspireEvent evt = ThemeInspireDataUtil.getCreateEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdated_IfEventIsUpdated() {

		ThemeInspireUpdatedEvent evt = ThemeInspireDataUtil.getThemeInspireUpdatedEvent(code);

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsUpdate() {

		UpdateThemeInspireEvent evt = ThemeInspireDataUtil.getUpdateEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleted_IfLastEventIsDeleted() {

		List<Event> history = new ArrayList<>();

		history.add(ThemeInspireDataUtil.getThemeInspireCreatedEvent(code));
		history.add(ThemeInspireDataUtil.getThemeInspireUpdatedEvent(code));

		history.add(ThemeInspireDataUtil.getThemeInspireDeletedEvent(code));

		agg.loadFromHistory(history);

		checkDeletedState((ThemeInspireDeletedEvent) history.get(2));
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsDelete() {

		DeleteThemeInspireEvent evt = ThemeInspireDataUtil.getDeleteEvent(code);

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdateCancelled_IfLastEventIsUpdateCancelled() {

		List<Event> history = new ArrayList<>();

		history.add(ThemeInspireDataUtil.getThemeInspireCreatedEvent(code));
		history.add(ThemeInspireDataUtil.getThemeInspireUpdatedEvent(code));

		history.add(ThemeInspireDataUtil.getUpdateThemeInspireCancelledEvent(code));

		agg.loadFromHistory(history);

		checkCancelledState((ThemeInspireCancelledEvent) history.get(2));
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleteCancelled_IfLastEventIsDeleteCancelled() {

		List<Event> history = new ArrayList<>();

		history.add(ThemeInspireDataUtil.getThemeInspireCreatedEvent(code));
		history.add(ThemeInspireDataUtil.getThemeInspireUpdatedEvent(code));

		history.add(ThemeInspireDataUtil.getDeleteThemeInspireCancelledEvent(code));

		agg.loadFromHistory(history);

		checkCancelledState((ThemeInspireCancelledEvent) history.get(2));
	}

	private void checkCancelledState(ThemeInspireCancelledEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getThemeInspire(), evt.getThemeInspire());
		assertFalse(agg.isDeleted());
	}

	private void checkCreatedOrUpdatedState(ThemeInspireEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getThemeInspire(), evt.getThemeInspire());
		assertFalse(agg.isDeleted());
	}

	private void checkDeletedState(ThemeInspireDeletedEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
