package es.redmic.atlascommands.commands.layer;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.commandslib.commands.Command;

/*-
 * #%L
 * atlas commands
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

public class UpdateLayerCommand extends Command {

	private LayerDTO layer;

	public UpdateLayerCommand() {
	}

	public UpdateLayerCommand(LayerDTO layer) {
		this.setLayer(layer);
	}

	public LayerDTO getLayer() {
		return layer;
	}

	public void setLayer(LayerDTO layer) {
		this.layer = layer;
	}
}
