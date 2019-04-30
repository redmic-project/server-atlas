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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.atlascommands.commands.themeinspire.CreateThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.DeleteThemeInspireCommand;
import es.redmic.atlascommands.commands.themeinspire.UpdateThemeInspireCommand;
import es.redmic.atlascommands.handler.ThemeInspireCommandHandler;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.commandslib.service.CommandServiceItfc;

@Service
public class ThemeInspireService implements CommandServiceItfc<ThemeInspireDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final ThemeInspireCommandHandler commandHandler;

	@Autowired
	public ThemeInspireService(ThemeInspireCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public ThemeInspireDTO create(ThemeInspireDTO atlas) {

		logger.debug("Create ThemeInspire");

		return commandHandler.save(new CreateThemeInspireCommand(atlas));
	}

	@Override
	public ThemeInspireDTO update(String id, ThemeInspireDTO atlas) {

		logger.debug("Update ThemeInspire");

		return commandHandler.update(id, new UpdateThemeInspireCommand(atlas));
	}

	@Override
	public ThemeInspireDTO delete(String id) {

		logger.debug("Delete ThemeInspire");

		return commandHandler.update(id, new DeleteThemeInspireCommand(id));
	}
}
