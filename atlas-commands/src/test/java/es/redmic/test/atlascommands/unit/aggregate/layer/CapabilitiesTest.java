package es.redmic.test.atlascommands.unit.aggregate.layer;

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
import static org.junit.Assert.assertNull;

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
import es.redmic.atlascommands.mapper.LayerMapper;
import es.redmic.atlascommands.utils.Capabilities;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.exception.custom.ResourceNotFoundException;
import es.redmic.jts4jackson.module.JTSModule;
import es.redmic.testutils.utils.JsonToBeanTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class CapabilitiesTest {

	protected ObjectMapper mapper = new ObjectMapper().registerModule(new JTSModule());

	private LayerDTO expectedLayer;

	final String URL_CAPABILITIES = new File("src/test/resources/data/capabilities/wms.xml").toURI().toString();

	private HashMap<String, LayerDTO> layers;

	public CapabilitiesTest() throws IOException {
		// Obtiene las capas mediante la utilidad, ya transformada a dto
		layers = Capabilities.getCapabilities(URL_CAPABILITIES);

		expectedLayer = (LayerDTO) JsonToBeanTestUtil.getBean("/data/layers/layer.json", LayerDTO.class);

		// Establece urlSource dinámicamente (depende de donde se ejecute)
		expectedLayer.setUrlSource(URL_CAPABILITIES);
	}

	// TODO: cambiar excepción
	@Test(expected = ResourceNotFoundException.class)
	public void getCapabilities_ThrowException_IfUrlIsMalformed() throws IOException, ServiceException {

		Capabilities.getCapabilities("wms.xml");
	}

	// TODO: cambiar excepción
	@Test(expected = ResourceNotFoundException.class)
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

		LayerDTO layerDTO = (LayerDTO) layers.values().toArray()[0];

		assertEquals(expectedLayer.getAbstractLayer(), layerDTO.getAbstractLayer());
		assertEquals(expectedLayer.getActivities(), layerDTO.getActivities());

		assertEquals(expectedLayer.getElevationDimension(), layerDTO.getElevationDimension());
		assertEquals(expectedLayer.getFormats(), layerDTO.getFormats());
		assertEquals(expectedLayer.getGeometry(), layerDTO.getGeometry());

		assertEquals(expectedLayer.getKeywords(), layerDTO.getKeywords());
		assertEquals(expectedLayer.getLatLonBoundsImage(), layerDTO.getLatLonBoundsImage());
		assertEquals(expectedLayer.getLegend(), layerDTO.getLegend());
		assertEquals(expectedLayer.getAttribution(), layerDTO.getAttribution());
		assertEquals(expectedLayer.getProtocols(), layerDTO.getProtocols());
		assertEquals(expectedLayer.getQueryable(), layerDTO.getQueryable());

		assertEquals(expectedLayer.getStylesLayer(), layerDTO.getStylesLayer());

		assertEquals(expectedLayer.getTimeDimension(), layerDTO.getTimeDimension());
		assertEquals(expectedLayer.getTitle(), layerDTO.getTitle());
		assertEquals(expectedLayer.getUrlSource(), layerDTO.getUrlSource());
		assertEquals(expectedLayer.getName(), layerDTO.getName());
	}

	@Test
	public void allLayers_ContaintContact_IfContactCapabilitiesIsNotNull() throws IOException, ServiceException {

		for (LayerDTO item : layers.values()) {
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
		LayerDTO layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(), "Isolíneas batimétricas (Batimetría de las Islas Canarias) ");

		layer.set_abstract("Isolíneas batimétricas ref#817,201,54556# (Batimetría de las Islas Canarias)");
		layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(), "Isolíneas batimétricas (Batimetría de las Islas Canarias)");

		layer.set_abstract("Isolíneas batimétricas " + "\n(Batimetría de las Islas Canarias)\nref#155,# aaaaaaaaaa");
		layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(),
				"Isolíneas batimétricas (Batimetría de las Islas Canarias) aaaaaaaaaa");

		layer.set_abstract("ref#155,#\nIsolíneas batimétricas (Batimetría de las Islas Canarias)\n");
		layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(), " Isolíneas batimétricas (Batimetría de las Islas Canarias) ");

		layer.set_abstract("ref#155#");
		layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getAbstractLayer(), "");
	}

	@Test
	public void activities_ContainExpectedIds_IfMapperIsCorrect() throws IOException, ServiceException {

		Layer layer = new Layer();

		String urlSource = "";

		layer.set_abstract("Isolíneas batimétricas " + "\n(Batimetría de las Islas Canarias)\nref#817#");
		LayerDTO layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getActivities().size(), 1);
		assertEquals(layerDTO.getActivities().get(0).getId(), "817");

		layer.set_abstract("Isolíneas batimétricas ref#817,201,54556# (Batimetría de las Islas Canarias)");
		layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getActivities().size(), 3);
		assertEquals(layerDTO.getActivities().get(0).getId(), "817");
		assertEquals(layerDTO.getActivities().get(1).getId(), "201");
		assertEquals(layerDTO.getActivities().get(2).getId(), "54556");

		layer.set_abstract("Isolíneas batimétricas " + "\n(Batimetría de las Islas Canarias)\nref#155,# aaaaaaaaaa");
		layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getActivities().size(), 1);
		assertEquals(layerDTO.getActivities().get(0).getId(), "155");

		layer.set_abstract("ref#155,#\nIsolíneas batimétricas (Batimetría de las Islas Canarias)\n");
		layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getActivities().size(), 1);
		assertEquals(layerDTO.getActivities().get(0).getId(), "155");

		layer.set_abstract("ref#155#");
		layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertEquals(layerDTO.getActivities().size(), 1);
		assertEquals(layerDTO.getActivities().get(0).getId(), "155");

		layer.set_abstract("Isolíneas batimétricas " + "\n(Batimetría de las Islas Canarias)\n");
		layerDTO = Mappers.getMapper(LayerMapper.class).map(layer, urlSource);
		assertNull(layerDTO.getActivities());
	}
}
