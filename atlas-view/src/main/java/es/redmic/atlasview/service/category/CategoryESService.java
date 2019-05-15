package es.redmic.atlasview.service.category;

/*-
 * #%L
 * Atlas-query-endpoint
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlasview.model.category.Category;
import es.redmic.atlasview.repository.category.CategoryESRepository;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.data.service.RWDataService;

@Service
public class CategoryESService extends RWDataService<Category, CategoryDTO, SimpleQueryDTO> {

	@Autowired
	public CategoryESService(CategoryESRepository repository) {
		super(repository);
	}
}
