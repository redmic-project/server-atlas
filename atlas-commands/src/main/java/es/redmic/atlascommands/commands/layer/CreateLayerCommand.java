package es.redmic.atlascommands.commands.layer;

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

import java.util.UUID;

import org.joda.time.DateTime;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.utils.LayerUtil;
import es.redmic.commandslib.commands.Command;

public class CreateLayerCommand extends Command {

	private LayerDTO layer;

	public CreateLayerCommand() {
	}

	public CreateLayerCommand(LayerDTO layer) {

		if (layer.getId() == null) {
			// Se crea un id único para Layer
			layer.setId(LayerUtil.generateId(UUID.randomUUID().toString()));
		}
		
		layer.setInserted(DateTime.now());
		layer.setUpdated(DateTime.now());
		
		this.setLayer(layer);
	}

	public LayerDTO getLayer() {
		return layer;
	}

	public void setLayer(LayerDTO layer) {
		this.layer = layer;
	}
}
