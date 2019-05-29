package es.redmic.atlasview.model.layer;

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

import java.util.List;

import org.locationtech.jts.geom.Polygon;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.redmic.elasticsearchlib.common.model.JoinIndex;
import es.redmic.models.es.administrative.model.ActivityCompact;

public class Layer extends LayerModelCompact {

	@JsonIgnore
	public static final String JOIN_INDEX_NAME = "layer";

	private JoinIndex joinIndex;

	private String title;
	private String abstractLayer;
	private List<String> keywords;
	private List<String> srs;
	private List<StyleLayer> stylesLayer;
	private Contact contact;
	private List<ActivityCompact> activities;
	private String urlSource;
	private Boolean queryable;
	private List<String> formats;
	private String image;
	private LatLonBoundingBox latLonBoundsImage;
	private List<Protocol> protocols;
	private Polygon geometry;
	private String legend;
	private Attribution attribution;
	private Dimension timeDimension;
	private Dimension elevationDimension;

	public Layer() {

		joinIndex = new JoinIndex();
		joinIndex.setName(JOIN_INDEX_NAME);
	}

	public JoinIndex getJoinIndex() {
		return joinIndex;
	}

	public void setJoinIndex(JoinIndex joinIndex) {
		this.joinIndex = joinIndex;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<String> getSrs() {
		return srs;
	}

	public void setSrs(List<String> srs) {
		this.srs = srs;
	}

	public String getAbstractLayer() {
		return abstractLayer;
	}

	public void setAbstractLayer(String abstractLayer) {
		this.abstractLayer = abstractLayer;
	}

	public List<StyleLayer> getStylesLayer() {
		return stylesLayer;
	}

	public void setStylesLayer(List<StyleLayer> stylesLayer) {
		this.stylesLayer = stylesLayer;
	}

	public Polygon getGeometry() {
		return geometry;
	}

	public void setGeometry(Polygon geometry) {
		this.geometry = geometry;
	}

	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public Attribution getAttribution() {
		return attribution;
	}

	public void setAttribution(Attribution attribution) {
		this.attribution = attribution;
	}

	public Dimension getTimeDimension() {
		return timeDimension;
	}

	public void setTimeDimension(Dimension timeDimension) {
		this.timeDimension = timeDimension;
	}

	public Dimension getElevationDimension() {
		return elevationDimension;
	}

	public void setElevationDimension(Dimension elevationDimension) {
		this.elevationDimension = elevationDimension;
	}

	public List<ActivityCompact> getActivities() {
		return activities;
	}

	public void setActivities(List<ActivityCompact> activities) {
		this.activities = activities;
	}

	public String getUrlSource() {
		return urlSource;
	}

	public void setUrlSource(String urlSource) {
		this.urlSource = urlSource;
	}

	public Boolean getQueryable() {
		return queryable;
	}

	public void setQueryable(Boolean queryable) {
		this.queryable = queryable;
	}

	public List<String> getFormats() {
		return formats;
	}

	public void setFormats(List<String> formats) {
		this.formats = formats;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public LatLonBoundingBox getLatLonBoundsImage() {
		return latLonBoundsImage;
	}

	public void setLatLonBoundsImage(LatLonBoundingBox latLonBoundsImage) {
		this.latLonBoundsImage = latLonBoundsImage;
	}

	public List<Protocol> getProtocols() {
		return protocols;
	}

	public void setProtocols(List<Protocol> protocols) {
		this.protocols = protocols;
	}
}
