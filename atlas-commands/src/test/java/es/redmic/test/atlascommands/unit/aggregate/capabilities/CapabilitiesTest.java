package es.redmic.test.atlascommands.unit.aggregate.capabilities;

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

import org.geotools.data.ows.Layer;
import org.geotools.metadata.iso.citation.ResponsiblePartyImpl;
import org.geotools.ows.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.atlascommands.mapper.ContactMapper;
import es.redmic.atlascommands.mapper.LayerWMSMapper;
import es.redmic.atlascommands.utils.Capabilities;
import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;
import es.redmic.exception.custom.URLException;
import es.redmic.exception.utils.ExternalResourceException;
import es.redmic.jts4jackson.module.JTSModule;
import es.redmic.testutils.utils.JsonToBeanTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class CapabilitiesTest {

	protected ObjectMapper mapper = new ObjectMapper().registerModule(new JTSModule());

	private LayerWMSDTO expectedLayer;

	final String URL_CAPABILITIES = new File("src/test/resources/data/capabilities/wms.xml").toURI().toString();

	private HashMap<String, LayerWMSDTO> layers;

	public CapabilitiesTest() throws IOException {
		// Obtiene las capas mediante la utilidad, ya transformada a dto
		layers = Capabilities.getCapabilities(URL_CAPABILITIES);

		expectedLayer = (LayerWMSDTO) JsonToBeanTestUtil.getBean("/data/layers/layer.json", LayerWMSDTO.class);
	}

	@Test(expected = URLException.class)
	public void getCapabilities_ThrowException_IfUrlIsMalformed() throws IOException, ServiceException {

		Capabilities.getCapabilities("wms.xml");
	}

	@Test(expected = ExternalResourceException.class)
	public void getCapabilities_ThrowException_IfServiceIsInaccessible() throws IOException, ServiceException {

		Capabilities.getCapabilities(new File("wms.xml").toURI().toString());
	}

	@Test
	public void getCapabilities_ReturnLayers_IfUrlIsCorrect() throws IOException, ServiceException {

		assertEquals(1, layers.size());
	}

	/**
	 * Solo comprueba los datos recibidos de getCapabilities, no todo el dto.
	 */
	@Test
	public void layer_ContaintAllFields_IfMapperIsCorrect() throws IOException, ServiceException {

		LayerWMSDTO layerDTO = (LayerWMSDTO) layers.values().toArray()[0];

		assertEquals(expectedLayer.getAbstractLayer(), layerDTO.getAbstractLayer());

		assertEquals(expectedLayer.getElevationDimension(), layerDTO.getElevationDimension());
		assertEquals(expectedLayer.getFormats(), layerDTO.getFormats());
		assertEquals(expectedLayer.getGeometry(), layerDTO.getGeometry());

		assertEquals(expectedLayer.getKeywords(), layerDTO.getKeywords());
		assertEquals(expectedLayer.getAttribution(), layerDTO.getAttribution());
		assertEquals(expectedLayer.getQueryable(), layerDTO.getQueryable());

		assertEquals(expectedLayer.getStylesLayer(), layerDTO.getStylesLayer());

		assertEquals(expectedLayer.getTimeDimension(), layerDTO.getTimeDimension());
		assertEquals(expectedLayer.getTitle(), layerDTO.getTitle());
		assertEquals(expectedLayer.getName(), layerDTO.getName());
	}

	@Test
	public void allLayers_ContaintContact_IfContactCapabilitiesIsNotNull() throws IOException, ServiceException {

		for (LayerWMSDTO item : layers.values()) {
			assertEquals(expectedLayer.getContact(), item.getContact());
		}
	}

	@Test
	public void contact_NoThrowException_IfContactIsNull() throws IOException, ServiceException {

		Mappers.getMapper(ContactMapper.class).map(new ResponsiblePartyImpl());
	}

	@Test
	public void abstractLayer_ContainExpectedString_IfMapperIsCorrect() throws IOException, ServiceException {

		Layer layer = new Layer();

		String urlSource = "";

		layer.set_abstract("Isolíneas batimétricas " + "\n(Batimetría de las Islas Canarias)\nref#817#");
		LayerWMSDTO layerDTO = Mappers.getMapper(LayerWMSMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(), "Isolíneas batimétricas (Batimetría de las Islas Canarias) ");

		layer.set_abstract("Isolíneas batimétricas ref#817,201,54556# (Batimetría de las Islas Canarias)");
		layerDTO = Mappers.getMapper(LayerWMSMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(), "Isolíneas batimétricas (Batimetría de las Islas Canarias)");

		layer.set_abstract("Isolíneas batimétricas " + "\n(Batimetría de las Islas Canarias)\nref#155,# aaaaaaaaaa");
		layerDTO = Mappers.getMapper(LayerWMSMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(),
				"Isolíneas batimétricas (Batimetría de las Islas Canarias) aaaaaaaaaa");

		layer.set_abstract("ref#155,#\nIsolíneas batimétricas (Batimetría de las Islas Canarias)\n");
		layerDTO = Mappers.getMapper(LayerWMSMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(), " Isolíneas batimétricas (Batimetría de las Islas Canarias) ");

		layer.set_abstract("ref#155#");
		layerDTO = Mappers.getMapper(LayerWMSMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(), "");
	}
}
