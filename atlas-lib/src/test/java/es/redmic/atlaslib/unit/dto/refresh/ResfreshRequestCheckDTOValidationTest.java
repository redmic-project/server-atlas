package es.redmic.atlaslib.unit.dto.refresh;

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

import es.redmic.atlaslib.dto.refresh.RefreshRequestDTO;
import es.redmic.testutils.dto.DTOBaseTest;

public class ResfreshRequestCheckDTOValidationTest extends DTOBaseTest<RefreshRequestDTO> {

	private static RefreshRequestDTO dto;

	@Before
	public void reset() {

		dto = new RefreshRequestDTO();
		dto.setName("Batimetrías");
		dto.setUrlSource("https://redmic.es/geoserver/wms");
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
	public void validationDTO_ReturnSizeError_IfNameSizeIsLessThan3() {

		dto.setName("aa");

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfUrlSourceIsNull() {

		dto.setUrlSource(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotValidURL_IfUrlSourceHasNotURLFormat() {

		dto.setUrlSource("noisa.url");

		checkDTOHasError(dto, URL_MESSAGE_TEMPLATE);
	}
}
