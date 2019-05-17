package es.redmic.test.atlasview.unit.mapper;

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

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlasview.config.MapperScanBean;
import es.redmic.atlasview.mapper.layer.LayerESMapper;
import es.redmic.atlasview.model.layer.Layer;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.testutils.utils.JsonToBeanTestUtil;
import es.redmic.viewlib.common.mapper.es2dto.DataCollectionESMapper;
import es.redmic.viewlib.common.mapper.es2dto.DataItemESMapper;

@RunWith(MockitoJUnitRunner.class)
public class LayerMapperTest {

	@InjectMocks
	LayerESMapper mapper;

	@InjectMocks
	DataCollectionESMapper dataCollectionMapper;

	@InjectMocks
	DataItemESMapper dataItemMapper;

	protected MapperScanBean factory = new MapperScanBean().build();

	// @formatter:off

	String modelPath = "/data/model/layer/layer.json",
			dtoToSavePath = "/data/dto/layer/layer.json",
			searchWrapperPath = "/data/model/layer/searchWrapperLayerESModel.json",
			searchDTOPath = "/data/dto/layer/searchWrapperLayerDTO.json";

	// @formatter:on

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(mapper);
		factory.addMapper(dataCollectionMapper);
		factory.addMapper(dataItemMapper);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		LayerDTO dtoIn = (LayerDTO) JsonToBeanTestUtil.getBean(dtoToSavePath, LayerDTO.class);

		Layer modelOut = factory.getMapperFacade().map(dtoIn, Layer.class);

		String modelStringExpected = JsonToBeanTestUtil.getJsonString(modelPath);
		String modelString = JsonToBeanTestUtil.writeValueAsString(modelOut);

		JSONAssert.assertEquals(modelStringExpected, modelString, false);
	}

	@Test
	public void mapperSearchWrapperToDto() throws JsonParseException, JsonMappingException, IOException, JSONException {

		JavaType type = JsonToBeanTestUtil.getParametizedType(DataSearchWrapper.class, Layer.class);

		DataSearchWrapper<?> searchWrapperModel = (DataSearchWrapper<?>) JsonToBeanTestUtil.getBean(searchWrapperPath,
				type);
		String expected = JsonToBeanTestUtil.getJsonString(searchDTOPath);

		JSONCollectionDTO searchDTO = factory.getMapperFacade().map(searchWrapperModel.getHits(),
				JSONCollectionDTO.class);

		String searchDTOString = JsonToBeanTestUtil.writeValueAsString(searchDTO);

		JSONAssert.assertEquals(expected, searchDTOString, false);
	}
}
