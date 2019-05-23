package es.redmic.atlasview.mapper.layer;

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

import org.springframework.stereotype.Component;

import es.redmic.atlaslib.dto.layer.ActivityDTO;
import es.redmic.atlaslib.dto.layer.AttributionDTO;
import es.redmic.atlaslib.dto.layer.ContactDTO;
import es.redmic.atlaslib.dto.layer.DimensionDTO;
import es.redmic.atlaslib.dto.layer.LatLonBoundingBoxDTO;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layer.ProtocolDTO;
import es.redmic.atlaslib.dto.layer.StyleLayerDTO;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlasview.model.layer.Attribution;
import es.redmic.atlasview.model.layer.Contact;
import es.redmic.atlasview.model.layer.Dimension;
import es.redmic.atlasview.model.layer.LatLonBoundingBox;
import es.redmic.atlasview.model.layer.Layer;
import es.redmic.atlasview.model.layer.Protocol;
import es.redmic.atlasview.model.layer.StyleLayer;
import es.redmic.atlasview.model.themeinspire.ThemeInspire;
import es.redmic.models.es.administrative.model.ActivityCompact;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class LayerESMapper extends CustomMapper<Layer, LayerDTO> {

	@Override
	public void mapAtoB(Layer a, LayerDTO b, MappingContext context) {

		if (a.getStylesLayer() != null) {
			b.setStylesLayer(mapperFacade.mapAsList(a.getStylesLayer(), StyleLayerDTO.class));
		}

		if (a.getContact() != null) {
			b.setContact(mapperFacade.map(a.getContact(), ContactDTO.class));
		}

		if (a.getActivities() != null) {
			b.setActivities(mapperFacade.mapAsList(a.getActivities(), ActivityDTO.class));
		}

		if (a.getThemeInspire() != null) {
			b.setThemeInspire(mapperFacade.map(a.getThemeInspire(), ThemeInspireDTO.class));
		}

		if (a.getLatLonBoundsImage() != null) {
			b.setLatLonBoundsImage(mapperFacade.map(a.getLatLonBoundsImage(), LatLonBoundingBoxDTO.class));
		}

		if (a.getProtocols() != null) {
			b.setProtocols(mapperFacade.mapAsList(a.getProtocols(), ProtocolDTO.class));
		}

		if (a.getTimeDimension() != null) {
			b.setTimeDimension(mapperFacade.map(a.getTimeDimension(), DimensionDTO.class));
		}

		if (a.getElevationDimension() != null) {
			b.setElevationDimension(mapperFacade.map(a.getElevationDimension(), DimensionDTO.class));
		}

		if (a.getAttribution() != null) {
			b.setAttribution(mapperFacade.map(a.getAttribution(), AttributionDTO.class));
		}

		super.mapAtoB(a, b, context);
	}

	@Override
	public void mapBtoA(LayerDTO b, Layer a, MappingContext context) {

		if (b.getStylesLayer() != null) {
			a.setStylesLayer(mapperFacade.mapAsList(b.getStylesLayer(), StyleLayer.class));
		}

		if (b.getContact() != null) {
			a.setContact(mapperFacade.map(b.getContact(), Contact.class));
		}

		if (b.getActivities() != null) {
			a.setActivities(mapperFacade.mapAsList(b.getActivities(), ActivityCompact.class));
		}

		if (b.getThemeInspire() != null) {
			a.setThemeInspire(mapperFacade.map(b.getThemeInspire(), ThemeInspire.class));
		}

		if (b.getLatLonBoundsImage() != null) {
			a.setLatLonBoundsImage(mapperFacade.map(b.getLatLonBoundsImage(), LatLonBoundingBox.class));
		}

		if (b.getProtocols() != null) {
			a.setProtocols(mapperFacade.mapAsList(b.getProtocols(), Protocol.class));
		}

		if (b.getTimeDimension() != null) {
			a.setTimeDimension(mapperFacade.map(b.getTimeDimension(), Dimension.class));
		}

		if (b.getElevationDimension() != null) {
			a.setElevationDimension(mapperFacade.map(b.getElevationDimension(), Dimension.class));
		}

		if (b.getAttribution() != null) {
			a.setAttribution(mapperFacade.map(b.getAttribution(), Attribution.class));
		}

		super.mapBtoA(b, a, context);
	}
}
