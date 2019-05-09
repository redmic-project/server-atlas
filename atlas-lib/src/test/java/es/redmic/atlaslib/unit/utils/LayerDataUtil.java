package es.redmic.atlaslib.unit.utils;

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

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import es.redmic.atlaslib.dto.layer.ActivityDTO;
import es.redmic.atlaslib.dto.layer.ContactDTO;
import es.redmic.atlaslib.dto.layer.LatLonBoundingBoxDTO;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layer.ProtocolDTO;
import es.redmic.atlaslib.dto.layer.StyleLayerDTO;
import es.redmic.atlaslib.dto.layerinfo.LayerInfoDTO;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;

public abstract class LayerDataUtil {

	@SuppressWarnings("serial")
	public static LayerDTO getLayer() {

		LayerDTO layer = new LayerDTO();

		layer.setName("Prueba");
		layer.setId("1");
		layer.setTitle("title");

		List<String> srs = new ArrayList<>();
		srs.add("srs");
		layer.setSrs(srs);

		layer.setUrlSource("http://redmic.es");

		List<String> formats = new ArrayList<>();
		formats.add("WMS");
		layer.setFormats(formats);

		GeometryFactory geometryFactory = new GeometryFactory();

		Point geometry = geometryFactory.createPoint(new Coordinate(44.56433, 37.94388));
		layer.setGeometry(geometry);

		ActivityDTO activity = new ActivityDTO();
		activity.setId("3");
		activity.setName("AIS");
		activity.setPath("r.1.2.3");

		layer.setActivities(new ArrayList<ActivityDTO>() {
			{
				add(activity);
			}
		});

		ContactDTO contact = new ContactDTO();
		contact.setName("Pepe");
		layer.setContact(contact);

		layer.setThemeInspire(ThemeInspireDataUtil.getThemeInspire());

		layer.setProtocols(getProtocols());

		layer.setLatLonBoundsImage(getLatLonBoundingBoxDTO());

		StyleLayerDTO styleLayer = new StyleLayerDTO();
		styleLayer.setName("styleLayer");
		layer.setStyleLayer(styleLayer);

		return layer;
	}

	public static LayerInfoDTO getLayerInfo() {

		LayerInfoDTO layerInfo = new LayerInfoDTO();

		ThemeInspireDTO themeInspire = ThemeInspireDataUtil.getThemeInspire();

		layerInfo.setThemeInspire(themeInspire);

		layerInfo.setProtocols(getProtocols());

		layerInfo.setLatLonBoundsImage(getLatLonBoundingBoxDTO());

		return layerInfo;
	}

	@SuppressWarnings("serial")
	public static List<ProtocolDTO> getProtocols() {

		ProtocolDTO protocol = new ProtocolDTO();
		protocol.setType("WMS");
		protocol.setUrl("https://atlas.redmic.es/geoserver/tn/wms");

		return new ArrayList<ProtocolDTO>() {
			{
				add(protocol);
			}
		};
	}

	public static LatLonBoundingBoxDTO getLatLonBoundingBoxDTO() {

		LatLonBoundingBoxDTO latLonBoundingBoxDTO = new LatLonBoundingBoxDTO();

		latLonBoundingBoxDTO.setMaxX(2.0);
		latLonBoundingBoxDTO.setMaxY(4.0);
		latLonBoundingBoxDTO.setMinX(1.0);
		latLonBoundingBoxDTO.setMinY(1.0);

		return latLonBoundingBoxDTO;
	}
}
