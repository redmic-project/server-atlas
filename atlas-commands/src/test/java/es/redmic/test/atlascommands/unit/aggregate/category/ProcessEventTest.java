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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import es.redmic.atlascommands.aggregate.CategoryAggregate;
import es.redmic.atlascommands.commands.category.CreateCategoryCommand;
import es.redmic.atlascommands.commands.category.DeleteCategoryCommand;
import es.redmic.atlascommands.commands.category.UpdateCategoryCommand;
import es.redmic.atlascommands.statestore.CategoryStateStore;
import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.create.CreateCategoryEvent;
import es.redmic.atlaslib.events.category.delete.CheckDeleteCategoryEvent;
import es.redmic.atlaslib.events.category.update.UpdateCategoryEvent;
import es.redmic.atlaslib.unit.utils.CategoryDataUtil;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.restlib.config.UserService;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEventTest {

	private final String code = UUID.randomUUID().toString();

	CategoryStateStore categoryStateStore;

	UserService userService;

	CategoryAggregate agg;

	@Before
	public void setUp() {

		categoryStateStore = Mockito.mock(CategoryStateStore.class);

		userService = Mockito.mock(UserService.class);

		agg = new CategoryAggregate(categoryStateStore, userService);

		when(userService.getUserId()).thenReturn("13");
	}

	@Test
	public void processCreateCategoryCommand_ReturnCategoryCreatedEvent_IfProcessIsOk() {

		when(categoryStateStore.get(any())).thenReturn(null);

		CategoryDTO category = CategoryDataUtil.getCategory(code);

		CreateCategoryCommand command = new CreateCategoryCommand(category);

		CreateCategoryEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getCategory());
		assertEquals(evt.getCategory(), category);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), category.getId());
		assertEquals(evt.getType(), CategoryEventTypes.CREATE);
		assertTrue(evt.getVersion().equals(1));
	}

	@Test
	public void processUpdateCategoryCommand_ReturnCategoryUpdatedEvent_IfProcessIsOk() {

		when(categoryStateStore.get(any())).thenReturn(CategoryDataUtil.getCategoryCreatedEvent(code));

		CategoryDTO category = CategoryDataUtil.getCategory(code);

		UpdateCategoryCommand command = new UpdateCategoryCommand(category);

		UpdateCategoryEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getCategory());
		assertEquals(evt.getCategory(), category);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), category.getId());
		assertEquals(evt.getType(), CategoryEventTypes.UPDATE);
		assertTrue(evt.getVersion().equals(2));
	}

	// Editar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processUpdateCategoryCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		when(categoryStateStore.get(any())).thenReturn(CategoryDataUtil.getCategoryDeletedEvent(code));

		CategoryDTO category = CategoryDataUtil.getCategory(code);

		agg.process(new UpdateCategoryCommand(category));
	}

	// Editar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processUpdateCategoryCommand_ThrowItemLockedException_IfItemIsLocked() {

		when(categoryStateStore.get(any())).thenReturn(CategoryDataUtil.getUpdateEvent(code));

		CategoryDTO category = CategoryDataUtil.getCategory(code);

		agg.process(new UpdateCategoryCommand(category));
	}

	@Test
	public void processDeleteCategoryCommand_ReturnCheckDeleteCategoryEvent_IfProcessIsOk() {

		when(categoryStateStore.get(any())).thenReturn(CategoryDataUtil.getCategoryUpdatedEvent(code));

		CategoryDTO category = CategoryDataUtil.getCategory(code);

		DeleteCategoryCommand command = new DeleteCategoryCommand(category.getId());

		CheckDeleteCategoryEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), category.getId());
		assertEquals(evt.getType(), CategoryEventTypes.CHECK_DELETE);
		assertTrue(evt.getVersion().equals(3));
	}

	// Borrar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processDeleteCategoryCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		when(categoryStateStore.get(any())).thenReturn(CategoryDataUtil.getCategoryDeletedEvent(code));

		CategoryDTO category = CategoryDataUtil.getCategory(code);

		agg.process(new DeleteCategoryCommand(category.getId()));
	}

	// Borrar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processDeleteCategoryCommand_ThrowItemLockedException_IfItemIsLocked() {

		when(categoryStateStore.get(any())).thenReturn(CategoryDataUtil.getUpdateEvent(code));

		CategoryDTO category = CategoryDataUtil.getCategory(code);

		agg.process(new DeleteCategoryCommand(category.getId()));
	}
}
