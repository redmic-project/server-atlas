package es.redmic.atlascommands.commands.layer;

import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;
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

public class RefreshLayerCommand extends Command {

	private String layerId;

	private LayerWMSDTO layer;

	public RefreshLayerCommand() {
	}

	public RefreshLayerCommand(String id, LayerWMSDTO layer) {
		this.setLayerId(id);
		layer.setId(id);
		this.setLayer(layer);
	}

	public String getLayerId() {
		return layerId;
	}

	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}

	public LayerWMSDTO getLayer() {
		return layer;
	}

	public void setLayer(LayerWMSDTO layer) {
		this.layer = layer;
	}
}
