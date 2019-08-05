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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import es.redmic.atlascommands.aggregate.ThemeInspireAggregate;
import es.redmic.atlascommands.commands.themeinspire.CreateThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.DeleteThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.UpdateThemeInspireCommand;
import es.redmic.atlascommands.statestore.ThemeInspireStateStore;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.create.CreateThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.delete.CheckDeleteThemeInspireEvent;
import es.redmic.atlaslib.events.themeinspire.update.UpdateThemeInspireEvent;
import es.redmic.atlaslib.unit.utils.ThemeInspireDataUtil;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.restlib.config.UserService;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEventTest {

	private final String code = "gg";

	ThemeInspireStateStore themeInspireStateStore;

	UserService userService;

	ThemeInspireAggregate agg;

	@Before
	public void setUp() {

		themeInspireStateStore = Mockito.mock(ThemeInspireStateStore.class);

		userService = Mockito.mock(UserService.class);

		agg = new ThemeInspireAggregate(themeInspireStateStore, userService);

		when(userService.getUserId()).thenReturn("13");
	}

	@Test
	public void processCreateThemeInspireCommand_ReturnThemeInspireCreatedEvent_IfProcessIsOk() {

		when(themeInspireStateStore.get(any())).thenReturn(null);

		ThemeInspireDTO themeInspire = ThemeInspireDataUtil.getThemeInspire(code);

		CreateThemeInspireCommand command = new CreateThemeInspireCommand(themeInspire);

		CreateThemeInspireEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getThemeInspire());
		assertEquals(evt.getThemeInspire(), themeInspire);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), themeInspire.getId());
		assertEquals(evt.getType(), ThemeInspireEventTypes.CREATE);
		assertTrue(evt.getVersion().equals(1));
	}

	@Test
	public void processUpdateThemeInspireCommand_ReturnThemeInspireUpdatedEvent_IfProcessIsOk() {

		when(themeInspireStateStore.get(any())).thenReturn(ThemeInspireDataUtil.getThemeInspireCreatedEvent(code));

		ThemeInspireDTO themeInspire = ThemeInspireDataUtil.getThemeInspire(code);

		UpdateThemeInspireCommand command = new UpdateThemeInspireCommand(themeInspire);

		UpdateThemeInspireEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getThemeInspire());
		assertEquals(evt.getThemeInspire(), themeInspire);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), themeInspire.getId());
		assertEquals(evt.getType(), ThemeInspireEventTypes.UPDATE);
		assertTrue(evt.getVersion().equals(2));
	}

	// Editar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processUpdateThemeInspireCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		when(themeInspireStateStore.get(any())).thenReturn(ThemeInspireDataUtil.getThemeInspireDeletedEvent(code));

		ThemeInspireDTO themeInspire = ThemeInspireDataUtil.getThemeInspire(code);

		agg.process(new UpdateThemeInspireCommand(themeInspire));
	}

	// Editar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processUpdateThemeInspireCommand_ThrowItemLockedException_IfItemIsLocked() {

		when(themeInspireStateStore.get(any())).thenReturn(ThemeInspireDataUtil.getUpdateEvent(code));

		ThemeInspireDTO themeInspire = ThemeInspireDataUtil.getThemeInspire(code);

		agg.process(new UpdateThemeInspireCommand(themeInspire));
	}

	@Test
	public void processDeleteThemeInspireCommand_ReturnCheckDeleteThemeInspireEvent_IfProcessIsOk() {

		when(themeInspireStateStore.get(any())).thenReturn(ThemeInspireDataUtil.getThemeInspireUpdatedEvent(code));

		ThemeInspireDTO themeInspire = ThemeInspireDataUtil.getThemeInspire(code);

		DeleteThemeInspireCommand command = new DeleteThemeInspireCommand(themeInspire.getId());

		CheckDeleteThemeInspireEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), themeInspire.getId());
		assertEquals(evt.getType(), ThemeInspireEventTypes.CHECK_DELETE);
		assertTrue(evt.getVersion().equals(3));
	}

	// Borrar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processDeleteThemeInspireCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		when(themeInspireStateStore.get(any())).thenReturn(ThemeInspireDataUtil.getThemeInspireDeletedEvent(code));

		ThemeInspireDTO themeInspire = ThemeInspireDataUtil.getThemeInspire(code);

		agg.process(new DeleteThemeInspireCommand(themeInspire.getId()));
	}

	// Borrar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processDeleteThemeInspireCommand_ThrowItemLockedException_IfItemIsLocked() {

		when(themeInspireStateStore.get(any())).thenReturn(ThemeInspireDataUtil.getUpdateEvent(code));

		ThemeInspireDTO themeInspire = ThemeInspireDataUtil.getThemeInspire(code);

		agg.process(new DeleteThemeInspireCommand(themeInspire.getId()));
	}
}
