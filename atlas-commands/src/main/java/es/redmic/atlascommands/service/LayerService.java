package es.redmic.atlascommands.service;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.atlascommands.commands.layer.CreateLayerCommand;
import es.redmic.atlascommands.commands.layer.DeleteLayerCommand;
import es.redmic.atlascommands.commands.layer.UpdateLayerCommand;
import es.redmic.atlascommands.handler.LayerCommandHandler;
import es.redmic.atlascommands.mapper.LayerInfoDTOMapper;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layerinfo.LayerInfoDTO;
import es.redmic.commandslib.service.CommandServiceItfc;

@Service
public class LayerService implements CommandServiceItfc<LayerInfoDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final LayerCommandHandler commandHandler;

	private OGCLayerService ogcService;

	@Autowired
	public LayerService(LayerCommandHandler commandHandler, OGCLayerService ogcService) {
		this.commandHandler = commandHandler;
		this.ogcService = ogcService;
	}

	@Override
	public LayerDTO create(LayerInfoDTO layerInfo) {

		logger.debug("Create Layer");

		LayerDTO layerDTO = ogcService.getLayerFromWMSService(layerInfo.getUrlSource(), layerInfo.getName());

		return commandHandler
				.save(new CreateLayerCommand(Mappers.getMapper(LayerInfoDTOMapper.class).map(layerInfo, layerDTO)));
	}

	@Override
	public LayerDTO update(String id, LayerInfoDTO layerInfo) {

		logger.debug("Update Layer");

		LayerDTO layerDTO = ogcService.getLayerFromWMSService(layerInfo.getUrlSource(), layerInfo.getName());

		return commandHandler.update(id,
				new UpdateLayerCommand(Mappers.getMapper(LayerInfoDTOMapper.class).map(layerInfo, layerDTO)));
	}

	@Override
	public LayerDTO delete(String id) {

		logger.debug("Delete Layer");

		return commandHandler.update(id, new DeleteLayerCommand(id));
	}
}
