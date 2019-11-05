package es.redmic.atlaslib.unit.dto.category;

/*-
 * #%L
 * Atlas-lib
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.unit.utils.CategoryDataUtil;

public class CategoryEqualTest {

	@Test
	public void equal_returnTrue_IfCategoryTypeIsEqual() {

		CategoryDTO dto = CategoryDataUtil.getCategory();

		assertTrue(dto.equals(dto));
	}

	@Test
	public void equal_returnFalse_IfIdIsDifferent() {

		CategoryDTO dto1 = CategoryDataUtil.getCategory();

		CategoryDTO dto2 = CategoryDataUtil.getCategory();

		dto1.setId("111111");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfIdIsNull() {

		CategoryDTO dto1 = CategoryDataUtil.getCategory();

		CategoryDTO dto2 = CategoryDataUtil.getCategory();

		dto1.setId(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfCategoryTypeNameIsDifferent() {

		CategoryDTO dto1 = CategoryDataUtil.getCategory();

		CategoryDTO dto2 = CategoryDataUtil.getCategory();

		dto1.setName("cddd");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfCategoryTypeNameIsNull() {

		CategoryDTO dto1 = CategoryDataUtil.getCategory();

		CategoryDTO dto2 = CategoryDataUtil.getCategory();

		dto1.setName(null);
		assertFalse(dto1.equals(dto2));
	}
}
