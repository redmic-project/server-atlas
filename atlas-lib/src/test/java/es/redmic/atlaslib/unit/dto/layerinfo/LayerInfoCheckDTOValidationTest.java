package es.redmic.atlaslib.unit.dto.layerinfo;

import java.util.ArrayList;

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

import es.redmic.atlaslib.dto.layer.LatLonBoundingBoxDTO;
import es.redmic.atlaslib.dto.layer.ProtocolDTO;
import es.redmic.atlaslib.dto.layerinfo.LayerInfoDTO;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;
import es.redmic.testutils.dto.DTOBaseTest;

public class LayerInfoCheckDTOValidationTest extends DTOBaseTest<LayerInfoDTO> {

	private static LayerInfoDTO dto;

	@Before
	public void reset() {

		dto = LayerDataUtil.getLayerInfo();
	}

	@Test
	public void validationDTO_NoReturnError_IfDTOIsCorrect() {

		checkDTOHasNoError(dto);
	}

	@Test
	public void validationDTO_ReturnError_IfNameIsNull() {

		dto.setName(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfProtocolIsNull() {

		dto.setProtocols(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnSizeError_IfProtocolIsEmpty() {

		dto.setProtocols(new ArrayList<ProtocolDTO>());

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfAtlasIsNull() {

		dto.setAtlas(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfRefreshIsNull() {

		dto.setRefresh(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnSizeError_IfDescriptionExceedsSize() {

		dto.setDescription(generateString(1501));

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnSizeError_IfAliasExceedsSize() {

		dto.setAlias(generateString(251));

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfLatLonBoundsImageFieldIsNull() {

		LatLonBoundingBoxDTO latLonBoundingBoxDTO = LayerDataUtil.getLatLonBoundingBoxDTO();
		latLonBoundingBoxDTO.setMinY(null);
		dto.setLatLonBoundsImage(latLonBoundingBoxDTO);
		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfProtocolFieldIsNull() {

		dto.getProtocols().get(0).setType(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfParentFieldIsNull() {

		dto.setParent(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}
}
