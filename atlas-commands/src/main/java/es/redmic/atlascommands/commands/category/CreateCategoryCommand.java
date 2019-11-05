package es.redmic.atlascommands.commands.category;

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

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.utils.CategoryUtil;
import es.redmic.commandslib.commands.Command;

public class CreateCategoryCommand extends Command {

	private CategoryDTO category;

	public CreateCategoryCommand() {
	}

	public CreateCategoryCommand(CategoryDTO category) {

		if (category.getId() == null) {
			// Se crea un id único para Category
			category.setId(CategoryUtil.generateId(UUID.randomUUID().toString()));
		}
		this.setCategory(category);
	}

	public CategoryDTO getCategory() {
		return category;
	}

	public void setCategory(CategoryDTO category) {
		this.category = category;
	}
}
