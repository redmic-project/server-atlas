package es.redmic.atlaslib.unit.dto.category;

import static org.junit.Assert.assertEquals;

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

import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.junit.Test;

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.unit.utils.AtlasAvroBaseTest;
import es.redmic.atlaslib.unit.utils.CategoryDataUtil;

public class CategoryCheckAvroSchemaTest extends AtlasAvroBaseTest {

	@Test
	public void serializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() throws JSONException {

		CategoryDTO dto = CategoryDataUtil.getCategory();

		Object result = serializerAndDeserializer(dto);

		assertTrue("El objeto obtenido debe ser una instancia de CategoryDTO", CategoryDTO.class.isInstance(result));

		assertEquals(result, dto);
	}
}
