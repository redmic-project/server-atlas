package es.redmic.atlaslib.unit.dto.layerinfo;

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

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layerinfo.LayerInfoDTO;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;

public class LayerInfoEqualTest {

	@Test
	public void equal_returnTrue_IfLayerInfoTypeIsEqual() {

		LayerInfoDTO dto = LayerDataUtil.getLayerInfo();

		assertTrue(dto.equals(dto));
	}

	@Test
	public void equal_returnFalse_IfIdIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setId("111111");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfIdIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setId(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoNameIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setName("cddd");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoNameIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setName(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoUrlSourceIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setUrlSource("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoUrlSourceIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setUrlSource(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoRefreshIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setRefresh(1);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoRefreshIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setRefresh(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoAtlasIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setAtlas(true);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoAtlasIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setAtlas(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoAliasIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setAlias("aaa");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoAliasIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setAlias(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoDescriptionIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setDescription("aaa");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoDescriptionIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setDescription(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoProtocolsIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.getProtocols().get(0).setType("aaa");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoProtocolsIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setProtocols(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoLatLonBoundsImageIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.getLatLonBoundsImage().setMaxX(2222.0);

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoLatLonBoundsImageIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setLatLonBoundsImage(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoThemeInspireIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.getThemeInspire().setName("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoThemeInspireIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setThemeInspire(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoParentIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.getParent().setName("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoParentIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setParent(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoLegendIsDifferent() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setLegend("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInfoLegendIsNull() {

		LayerInfoDTO dto1 = LayerDataUtil.getLayerInfo();

		LayerInfoDTO dto2 = LayerDataUtil.getLayerInfo();

		dto1.setLegend(null);
		assertFalse(dto1.equals(dto2));
	}

}
