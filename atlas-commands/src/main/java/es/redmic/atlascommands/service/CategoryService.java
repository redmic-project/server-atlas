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

import es.redmic.atlascommands.commands.category.CreateCategoryCommand;
import es.redmic.atlascommands.commands.category.DeleteCategoryCommand;
import es.redmic.atlascommands.commands.category.UpdateCategoryCommand;
import es.redmic.atlascommands.handler.CategoryCommandHandler;
import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.commandslib.service.CommandServiceItfc;

@Service
public class CategoryService implements CommandServiceItfc<CategoryDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final CategoryCommandHandler commandHandler;

	@Autowired
	public CategoryService(CategoryCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public CategoryDTO create(CategoryDTO category) {

		logger.debug("Create Category");

		return commandHandler.save(new CreateCategoryCommand(category));
	}

	@Override
	public CategoryDTO update(String id, CategoryDTO category) {

		logger.debug("Update Category");

		return commandHandler.update(id, new UpdateCategoryCommand(category));
	}

	@Override
	public CategoryDTO delete(String id) {

		logger.debug("Delete Category");

		return commandHandler.update(id, new DeleteCategoryCommand(id));
	}
}
