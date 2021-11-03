package es.redmic.atlascommands.mapper;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.data.ows.CRSEnvelope;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.StyleImpl;
import org.geotools.data.wms.xml.Attribution;
import org.geotools.data.wms.xml.Dimension;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.redmic.atlaslib.dto.layer.AttributionDTO;
import es.redmic.atlaslib.dto.layer.DimensionDTO;
import es.redmic.atlaslib.dto.layer.LogoURLDTO;
import es.redmic.atlaslib.dto.layer.StyleLayerDTO;
import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;

@Mapper
public interface LayerWMSMapper {

	final int SRID = 4326;

	// @formatter:off

	// TODO: Pasar por contexto

	final String SRS = "EPSG:4326",
			refRegex = "ref#((\\d*,?)*)#",
			endLineRegex = "\r?\n|\r",
			refInBracketsRegex = ".*(" + refRegex + ").*",
			formatRegex = ".*&format=(\\w*)%2F(\\w*)&.*",
			timeDimensionProperty = "time",
			elevationDimensionProperty = "elevation",
			legendGraphicUrlParameters = "?request=GetLegendGraphic&version=1.0.0&format=image/png&layer=topp:states";

	// @formatter:on
	@Mapping(source = "layer", target = "legend", qualifiedByName = "legend")
	@Mapping(source = "layer", target = "timeDimension", qualifiedByName = "timeDimension")
	@Mapping(source = "layer", target = "elevationDimension", qualifiedByName = "elevationDimension")
	@Mapping(source = "layer", target = "stylesLayer", qualifiedByName = "stylesLayer")
	@Mapping(source = "layer", target = "abstractLayer", qualifiedByName = "abstractLayer")
	@Mapping(source = "layer", target = "geometry", qualifiedByName = "geometry")
	@Mapping(source = "layer", target = "keywords", qualifiedByName = "keywords")
	@Mapping(source = "layer", target = "attribution", qualifiedByName = "attribution")
	LayerWMSDTO map(Layer layer, @Context String urlSource);

	@Named("legend")
	default String getLegend(Layer layer, @Context String urlSource) {

		return urlSource + legendGraphicUrlParameters;
	}

	@Named("timeDimension")
	default DimensionDTO getElevationDimension(Layer layer, @Context String urlSource) {

		return getDimension(layer, timeDimensionProperty);
	}

	@Named("elevationDimension")
	default DimensionDTO getTimeDimension(Layer layer, @Context String urlSource) {

		return getDimension(layer, elevationDimensionProperty);
	}

	@Named("stylesLayer")
	default List<StyleLayerDTO> getStyleLayer(Layer layer, @Context String urlSource) {

		List<StyleLayerDTO> stylesLayer = new ArrayList<>();

		for (StyleImpl style : layer.getStyles()) {
			StyleLayerDTO styleLayerDTO = new StyleLayerDTO();
			styleLayerDTO.setName(style.getName());

			if (style.getAbstract() != null)
				styleLayerDTO.setAbstractStyle(style.getAbstract().toString());

			if (style.getTitle() != null)
				styleLayerDTO.setTitle(style.getTitle().toString());

			if (style.getLegendURLs() != null && style.getLegendURLs().size() > 0) {

				String url = style.getLegendURLs().get(0).toString();

				styleLayerDTO.setUrl(url);
				styleLayerDTO.setFormat(styleLayerDTO.getUrl().replaceAll(formatRegex, "$1/$2"));
			}

			stylesLayer.add(styleLayerDTO);
		}
		return stylesLayer.isEmpty() ? null : stylesLayer;
	}

	@Named("abstractLayer")
	default String getAbstractLayer(Layer layer, @Context String urlSource) {

		if (layer.get_abstract() == null)
			return null;

		String abstractLayer = layer.get_abstract().replaceAll(endLineRegex, " ");

		if (abstractLayer.matches(refInBracketsRegex)) {
			String ref = abstractLayer.replaceAll(refInBracketsRegex, "$1");
			if (ref.matches(refRegex)) {
				abstractLayer = abstractLayer.replace(ref, "");
				abstractLayer = abstractLayer.replace("  ", " ");
			}
		}
		return abstractLayer;
	}

	@Named("geometry")
	default Polygon getGeometry(Layer layer, @Context String urlSource) {

		if (layer.getBoundingBoxes().get(SRS) == null)
			return null;

		CRSEnvelope srsBbox = layer.getBoundingBoxes().get(SRS);

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(srsBbox.getMinY(), srsBbox.getMinX()),
				new Coordinate(srsBbox.getMinY(), srsBbox.getMaxX()),
				new Coordinate(srsBbox.getMaxY(), srsBbox.getMaxX()),
				new Coordinate(srsBbox.getMaxY(), srsBbox.getMinX()),
				new Coordinate(srsBbox.getMinY(), srsBbox.getMinX()) };

		Polygon polygon = JTSFactoryFinder.getGeometryFactory().createPolygon(coordinates);
		polygon.setSRID(SRID);
		return polygon;
	}

	@Named("keywords")
	default List<String> getKeywords(Layer layer, @Context String urlSource) {

		if (layer.getKeywords() == null)
			return null;

		return Arrays.asList(layer.getKeywords());
	}

	@Named("attribution")
	default AttributionDTO getAttribution(Layer layer, @Context String urlSource) {

		if (layer.getAttribution() == null)
			return null;

		AttributionDTO attribution = new AttributionDTO();

		Attribution source = layer.getAttribution();

		attribution.setTitle(source.getTitle());

		if (source.getOnlineResource() != null)
			attribution.setOnlineResource(source.getOnlineResource().toString());

		if (source.getLogoURL() != null) {

			LogoURLDTO logoURL = new LogoURLDTO();
			logoURL.setFormat(source.getLogoURL().getFormat());
			logoURL.setOnlineResource(source.getLogoURL().getOnlineResource().toString());
			attribution.setLogoURL(logoURL);
		}

		return attribution;
	}

	default DimensionDTO getDimension(Layer layer, String property) {

		if (layer.getDimensions() == null || layer.getDimensions().size() == 0)
			return null;

		DimensionDTO dimension = new DimensionDTO();

		Dimension source = layer.getDimension(property);

		if (source == null)
			return null;

		dimension.setName(source.getName());
		dimension.setUnits(source.getUnits());
		dimension.setUnitSymbol(source.getUnitSymbol());
		dimension.setDefaultValue(source.getExtent().getDefaultValue());

		return dimension;
	}
}
