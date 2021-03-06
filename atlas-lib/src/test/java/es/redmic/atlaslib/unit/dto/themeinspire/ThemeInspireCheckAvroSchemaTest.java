package es.redmic.atlaslib.unit.dto.themeinspire;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.unit.utils.ThemeInspireDataUtil;
import es.redmic.testutils.utils.AvroBaseTest;

public class ThemeInspireCheckAvroSchemaTest extends AvroBaseTest {

	@Test
	public void serializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		ThemeInspireDTO dto = ThemeInspireDataUtil.getThemeInspire();

		Object result = serializerAndDeserializer(dto);

		assertTrue("El objeto obtenido debe ser una instancia de ThemeInspireDTO",
				ThemeInspireDTO.class.isInstance(result));

		assertEquals(result, dto);
	}
}
