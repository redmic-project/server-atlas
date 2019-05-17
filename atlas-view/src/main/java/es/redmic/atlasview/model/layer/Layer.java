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

import es.redmic.models.es.administrative.model.ActivityCompact;

public class Layer extends LayerModelCompact {

	private String title;
	private String abstractLayer;
	private List<String> keyword;
	private List<String> srs;
	private StyleLayer styleLayer;
	private Contact contact;
	private List<ActivityCompact> activities;
	private String urlSource;
	private Boolean queryable;
	private List<String> formats;
	private String image;
	private LatLonBoundingBox latLonBoundsImage;
	private List<Protocol> protocols;

	private Polygon geometry;

	public Layer() {
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

	public List<String> getKeyword() {
		return keyword;
	}

	public void setKeyword(List<String> keyword) {
		this.keyword = keyword;
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

	public StyleLayer getStyleLayer() {
		return styleLayer;
	}

	public void setStyleLayer(StyleLayer styleLayer) {
		this.styleLayer = styleLayer;
	}

	public Polygon getGeometry() {
		return geometry;
	}

	public void setGeometry(Polygon geometry) {
		this.geometry = geometry;
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
