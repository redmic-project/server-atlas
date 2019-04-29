package es.redmic.test.atlascommands.unit.aggregate.themeinspire;

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
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.create.ThemeInspireCreatedEvent;
import es.redmic.atlaslib.events.themeinspire.delete.DeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.ThemeInspireDeletedEvent;
import es.redmic.atlaslib.events.themeinspire.update.ThemeInspireUpdatedEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.test.atlascommands.integration.themeinspire.ThemeInspireDataUtil;

@RunWith(MockitoJUnitRunner.class)
public class ApplyEventTest {
	
	private final String code = "gg";
	
	ThemeInspireStateStore themeInspireStateStore;
	
	ThemeInspireAggregate agg;
	
	@Before
	public void setUp() {

		themeInspireStateStore = Mockito.mock(ThemeInspireStateStore.class);

		agg = new ThemeInspireAggregate(themeInspireStateStore);
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
