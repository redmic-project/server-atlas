package es.redmic.atlaslib.unit.dto.layerwms;

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

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;

public class LayerWMSEqualTest {

	@Test
	public void equal_returnTrue_IfLayerWMSIsEqual() {

		LayerWMSDTO dto = LayerDataUtil.getLayerWMS();

		assertTrue(dto.equals(dto));
	}

	@Test
	public void equal_returnFalse_IfIdIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setId("111111");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfIdIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setId(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSNameIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setName("cddd");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSNameIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setName(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSGeometryIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(-18.1745567321777, 27.6111183166504),
				new Coordinate(-18.1745567321777, 29.4221172332764),
				new Coordinate(-13.5011913299561, 29.4221172332764),
				new Coordinate(-13.3011913299561, 27.6111183166504),
				new Coordinate(-18.1745567321777, 27.6111183166504) };

		dto1.setGeometry(JTSFactoryFinder.getGeometryFactory().createPolygon(coordinates));

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSGeometryIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setGeometry(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSLegendIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setLegend("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSLegendIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setLegend(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSAttributionIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.getAttribution().setTitle("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSAttributionIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setAttribution(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSTimeDimensionIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.getTimeDimension().setName("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSTimeDimensionIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setTimeDimension(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSElevationDimensionIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.getElevationDimension().setName("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSElevationDimensionIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setElevationDimension(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSFormatsIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.getFormats().set(0, "aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSFormatsIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setFormats(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSQueryableIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setQueryable(false);

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSQueryableIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setQueryable(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSContactIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.getContact().setName("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSContactIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setContact(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSStyleLayerIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.getStylesLayer().get(0).setName("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSStyleLayerIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setStylesLayer(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSSrsIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.getSrs().add(0, "aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSSrsIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setSrs(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSKeywordIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.getKeywords().add(0, "aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSKeywordIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setKeywords(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSAbstractLayerIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setAbstractLayer("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSAbstractLayerIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setAbstractLayer(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSTitleIsDifferent() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setTitle("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerWMSTitleIsNull() {

		LayerWMSDTO dto1 = LayerDataUtil.getLayerWMS();

		LayerWMSDTO dto2 = LayerDataUtil.getLayerWMS();

		dto1.setTitle(null);
		assertFalse(dto1.equals(dto2));
	}
}
