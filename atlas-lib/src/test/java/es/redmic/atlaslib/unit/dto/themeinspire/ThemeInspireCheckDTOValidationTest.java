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

import org.junit.Before;
import org.junit.Test;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.testutils.dto.DTOBaseTest;

public class ThemeInspireCheckDTOValidationTest extends DTOBaseTest<ThemeInspireDTO> {

	private static ThemeInspireDTO dto;

	@Before
	public void reset() {

		dto = new ThemeInspireDTO();
		dto.setId("1");
		dto.setCode("1234");
		dto.setName("type");
		dto.setName_en("type");
	}

	@Test
	public void validationDTO_NoReturnError_IfDTOIsCorrect() {

		checkDTOHasNoError(dto);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfNameIsNull() {

		dto.setName(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnSizeError_IfNameExceedsSize() {

		dto.setName(generateString(151));

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfName_enIsNull() {

		dto.setName_en(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnSizeError_IfName_enExceedsSize() {

		dto.setName_en(generateString(151));

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnSizeError_IfCodeExceedsSize() {

		dto.setCode(generateString(51));

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}
}
