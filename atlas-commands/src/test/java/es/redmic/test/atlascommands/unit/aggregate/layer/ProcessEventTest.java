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

import es.redmic.atlascommands.aggregate.LayerAggregate;
import es.redmic.atlascommands.commands.layer.CreateLayerCommand;
import es.redmic.atlascommands.commands.layer.DeleteLayerCommand;
import es.redmic.atlascommands.commands.layer.RefreshLayerCommand;
import es.redmic.atlascommands.commands.layer.UpdateLayerCommand;
import es.redmic.atlascommands.statestore.LayerStateStore;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.common.LayerEvent;
import es.redmic.atlaslib.events.layer.delete.CheckDeleteLayerEvent;
import es.redmic.atlaslib.events.layer.refresh.RefreshLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.test.atlascommands.integration.layer.LayerDataUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEventTest {

	private final String code = UUID.randomUUID().toString();

	LayerStateStore layerStateStore;

	LayerAggregate agg;

	@Before
	public void setUp() {

		layerStateStore = Mockito.mock(LayerStateStore.class);

		agg = new LayerAggregate(layerStateStore);
	}

	@Test
	public void processCreateLayerCommand_ReturnCreateLayerEvent_IfThemeInspireIsNull() {

		when(layerStateStore.getLayer(any())).thenReturn(null);

		LayerDTO layer = LayerDataUtil.getLayer(code);
		layer.setThemeInspire(null);

		CreateLayerCommand command = new CreateLayerCommand(layer);

		LayerEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getLayer());
		assertEquals(evt.getLayer(), layer);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), layer.getId());
		assertEquals(evt.getType(), LayerEventTypes.CREATE);
		assertTrue(evt.getVersion().equals(1));
	}

	@Test
	public void processCreateLayerCommand_ReturnEnrichCreateLayerEvent_IfThemeInspireIsNotNull() {

		when(layerStateStore.getLayer(any())).thenReturn(null);

		LayerDTO layer = LayerDataUtil.getLayer(code);

		CreateLayerCommand command = new CreateLayerCommand(layer);

		LayerEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getLayer());
		assertEquals(evt.getLayer(), layer);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), layer.getId());
		assertEquals(evt.getType(), LayerEventTypes.ENRICH_CREATE);
		assertTrue(evt.getVersion().equals(1));
	}

	@Test
	public void processUpdateLayerCommand_ReturnLayerUpdatedEvent_IfProcessIsOk() {

		when(layerStateStore.getLayer(any())).thenReturn(LayerDataUtil.getLayerCreatedEvent(code));

		LayerDTO layer = LayerDataUtil.getLayer(code);

		UpdateLayerCommand command = new UpdateLayerCommand(layer);

		UpdateLayerEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getLayer());
		assertEquals(evt.getLayer(), layer);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), layer.getId());
		assertEquals(evt.getType(), LayerEventTypes.UPDATE);
		assertTrue(evt.getVersion().equals(2));
	}

	// Editar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processUpdateLayerCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		when(layerStateStore.getLayer(any())).thenReturn(LayerDataUtil.getLayerDeletedEvent(code));

		LayerDTO layer = LayerDataUtil.getLayer(code);

		agg.process(new UpdateLayerCommand(layer));
	}

	// Editar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processUpdateLayerCommand_ThrowItemLockedException_IfItemIsLocked() {

		when(layerStateStore.getLayer(any())).thenReturn(LayerDataUtil.getUpdateEvent(code));

		LayerDTO layer = LayerDataUtil.getLayer(code);

		agg.process(new UpdateLayerCommand(layer));
	}

	@Test
	public void processDeleteLayerCommand_ReturnCheckDeleteLayerEvent_IfProcessIsOk() {

		when(layerStateStore.getLayer(any())).thenReturn(LayerDataUtil.getLayerUpdatedEvent(code));

		LayerDTO layer = LayerDataUtil.getLayer(code);

		DeleteLayerCommand command = new DeleteLayerCommand(layer.getId());

		CheckDeleteLayerEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), layer.getId());
		assertEquals(evt.getType(), LayerEventTypes.CHECK_DELETE);
		assertTrue(evt.getVersion().equals(3));
	}

	// Borrar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processDeleteLayerCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		when(layerStateStore.getLayer(any())).thenReturn(LayerDataUtil.getLayerDeletedEvent(code));

		LayerDTO layer = LayerDataUtil.getLayer(code);

		agg.process(new DeleteLayerCommand(layer.getId()));
	}

	// Borrar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processDeleteLayerCommand_ThrowItemLockedException_IfItemIsLocked() {

		when(layerStateStore.getLayer(any())).thenReturn(LayerDataUtil.getUpdateEvent(code));

		LayerDTO layer = LayerDataUtil.getLayer(code);

		agg.process(new DeleteLayerCommand(layer.getId()));
	}

	@Test
	public void processRefreshLayerCommand_ReturnLayerRefreshedEvent_IfProcessIsOk() {

		when(layerStateStore.getLayer(any())).thenReturn(LayerDataUtil.getLayerCreatedEvent(code));

		LayerWMSDTO layer = LayerDataUtil.getLayerWMS(code);

		RefreshLayerCommand command = new RefreshLayerCommand(layer.getId(), layer);

		RefreshLayerEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getLayer());
		assertEquals(evt.getLayer(), layer);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), layer.getId());
		assertEquals(evt.getType(), LayerEventTypes.REFRESH);
		assertTrue(evt.getVersion().equals(2));
	}

	// Refrescar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processRefreshLayerCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		when(layerStateStore.getLayer(any())).thenReturn(LayerDataUtil.getLayerDeletedEvent(code));

		LayerWMSDTO layer = LayerDataUtil.getLayerWMS(code);

		agg.process(new RefreshLayerCommand(layer.getId(), layer));
	}

	// Refrescar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processRefreshLayerCommand_ThrowItemLockedException_IfItemIsLocked() {

		when(layerStateStore.getLayer(any())).thenReturn(LayerDataUtil.getUpdateEvent(code));

		LayerWMSDTO layer = LayerDataUtil.getLayerWMS(code);

		agg.process(new RefreshLayerCommand(layer.getId(), layer));
	}
}
