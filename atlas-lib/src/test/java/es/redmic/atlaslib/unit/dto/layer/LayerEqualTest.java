package es.redmic.atlaslib.unit.dto.layer;

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
import org.joda.time.DateTime;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;

public class LayerEqualTest {

	@Test
	public void equal_returnTrue_IfLayerIsEqual() {

		LayerDTO dto = LayerDataUtil.getLayer();

		assertTrue(dto.equals(dto));
	}

	@Test
	public void equal_returnFalse_IfIdIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setId("111111");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfIdIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setId(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerNameIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setName("cddd");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerNameIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setName(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerRefreshIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setRefresh(1);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerRefreshIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setRefresh(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerAtlasIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setAtlas(true);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerAtlasIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setAtlas(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerAliasIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setAlias("aaa");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerAliasIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setAlias(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerDescriptionIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setDescription("aaa");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerDescriptionIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setDescription(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerProtocolsIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getProtocols().get(0).setType("aaa");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerProtocolsIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setProtocols(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerLatLonBoundsImageIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getLatLonBoundsImage().setMaxX(2222.0);

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerLatLonBoundsImageIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setLatLonBoundsImage(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerThemeInspireIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getThemeInspire().setName("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerThemeInspireIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setThemeInspire(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerParentIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getParent().setName("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerParentIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setParent(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInsertedIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setInserted(DateTime.now().plusDays(1));

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerInsertedIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setInserted(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerUpdatedIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setUpdated(DateTime.now().plusDays(1));

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerUpdatedIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setUpdated(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerGeometryIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(-18.1745567321777, 27.6111183166504),
				new Coordinate(-18.1745567321777, 29.4221172332764),
				new Coordinate(-13.5011913299561, 29.4221172332764),
				new Coordinate(-13.3011913299561, 27.6111183166504),
				new Coordinate(-18.1745567321777, 27.6111183166504) };

		dto1.setGeometry(JTSFactoryFinder.getGeometryFactory().createPolygon(coordinates));

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerGeometryIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setGeometry(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerLegendIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setLegend("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerLegendIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setLegend(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerOpaqueIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setOpaque(true);

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerOpaqueIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setOpaque(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerTimeDimensionIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getTimeDimension().setName("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerTimeDimensionIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setTimeDimension(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerElevationDimensionIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getElevationDimension().setName("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerElevationDimensionIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setElevationDimension(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerImageIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setImage("aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerImageIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setImage(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerFormatsIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getFormats().set(0, "aaaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerFormatsIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setFormats(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerQueryableIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setQueryable(false);

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerQueryableIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setQueryable(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerUrlSourceIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setUrlSource("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerUrlSourceIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setUrlSource(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerActivitiesIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getActivities().get(0).setName("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerActivitiesIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setActivities(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerContactIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getContact().setName("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerContactIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setContact(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerStyleLayerIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getStyleLayer().setName("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerStyleLayerIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setStyleLayer(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerSrsIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getSrs().add(0, "aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerSrsIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setSrs(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerKeywordIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.getKeyword().add(0, "aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerKeywordIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setKeyword(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerAbstractLayerIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setAbstractLayer("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerAbstractLayerIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setAbstractLayer(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerTitleIsDifferent() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setTitle("aaa");

		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfLayerTitleIsNull() {

		LayerDTO dto1 = LayerDataUtil.getLayer();

		LayerDTO dto2 = LayerDataUtil.getLayer();

		dto1.setTitle(null);
		assertFalse(dto1.equals(dto2));
	}
}
