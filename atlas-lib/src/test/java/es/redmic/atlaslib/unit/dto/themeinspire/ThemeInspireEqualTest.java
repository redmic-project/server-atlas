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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.unit.utils.ThemeInspireDataUtil;

public class ThemeInspireEqualTest {

	@Test
	public void equal_returnTrue_IfThemeInspireTypeIsEqual() {

		ThemeInspireDTO dto = ThemeInspireDataUtil.getThemeInspire();

		assertTrue(dto.equals(dto));
	}

	@Test
	public void equal_returnFalse_IfIdIsDifferent() {

		ThemeInspireDTO dto1 = ThemeInspireDataUtil.getThemeInspire();

		ThemeInspireDTO dto2 = ThemeInspireDataUtil.getThemeInspire();

		dto1.setId("111111");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfIdIsNull() {

		ThemeInspireDTO dto1 = ThemeInspireDataUtil.getThemeInspire();

		ThemeInspireDTO dto2 = ThemeInspireDataUtil.getThemeInspire();

		dto1.setId(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfThemeInspireCodeIsDifferent() {

		ThemeInspireDTO dto1 = ThemeInspireDataUtil.getThemeInspire();

		ThemeInspireDTO dto2 = ThemeInspireDataUtil.getThemeInspire();

		dto1.setCode("112222344");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfThemeInspireTypeCodeIsNull() {

		ThemeInspireDTO dto1 = ThemeInspireDataUtil.getThemeInspire();

		ThemeInspireDTO dto2 = ThemeInspireDataUtil.getThemeInspire();

		dto1.setCode(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfThemeInspireTypeNameIsDifferent() {

		ThemeInspireDTO dto1 = ThemeInspireDataUtil.getThemeInspire();

		ThemeInspireDTO dto2 = ThemeInspireDataUtil.getThemeInspire();

		dto1.setName("cddd");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfThemeInspireTypeNameIsNull() {

		ThemeInspireDTO dto1 = ThemeInspireDataUtil.getThemeInspire();

		ThemeInspireDTO dto2 = ThemeInspireDataUtil.getThemeInspire();

		dto1.setName(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfThemeInspireTypeName_enIsDifferent() {

		ThemeInspireDTO dto1 = ThemeInspireDataUtil.getThemeInspire();

		ThemeInspireDTO dto2 = ThemeInspireDataUtil.getThemeInspire();

		dto1.setName_en("cvvcvc");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfThemeInspireTypeName_enIsNull() {

		ThemeInspireDTO dto1 = ThemeInspireDataUtil.getThemeInspire();

		ThemeInspireDTO dto2 = ThemeInspireDataUtil.getThemeInspire();

		dto1.setName_en(null);
		assertFalse(dto1.equals(dto2));
	}
}
