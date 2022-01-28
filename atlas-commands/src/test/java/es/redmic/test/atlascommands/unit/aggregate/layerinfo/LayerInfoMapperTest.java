package es.redmic.test.atlascommands.unit.aggregate.layerinfo;

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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;
import org.mapstruct.factory.Mappers;

import es.redmic.atlascommands.mapper.LayerInfoDTOMapper;
import es.redmic.atlascommands.utils.Capabilities;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layerinfo.LayerInfoDTO;
import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;
import es.redmic.testutils.utils.JsonToBeanTestUtil;

public class LayerInfoMapperTest {

	final String URL_CAPABILITIES = new File("src/test/resources/data/capabilities/wms.xml").toURI().toString();

	private HashMap<String, LayerWMSDTO> layers;

	private LayerDTO expectedLayer;

	private LayerInfoDTO layerInfo;

	public LayerInfoMapperTest() throws IOException {

		layers = Capabilities.getCapabilities(URL_CAPABILITIES);

		expectedLayer = (LayerDTO) JsonToBeanTestUtil.getBean("/data/layers/layerDTO.json", LayerDTO.class);

		layerInfo = (LayerInfoDTO) JsonToBeanTestUtil.getBean("/data/layers/layerInfoDTO.json", LayerInfoDTO.class);

		// Establece urlSource dinámicamente (depende de donde se ejecute)
		expectedLayer.setUrlSource(URL_CAPABILITIES);
		expectedLayer.setLegend("http://externallegend.com");
		layerInfo.setUrlSource(URL_CAPABILITIES);
	}

	@Test
	public void layerInfoMapper_ReturnLayerDTO_IfLayerInfoAndCapabilitiesDataMergeCorrectly() {

		LayerDTO layer = Mappers.getMapper(LayerInfoDTOMapper.class).map(layerInfo,
				(LayerWMSDTO) layers.values().toArray()[0]);

		assertEquals(expectedLayer, layer);
	}
}
