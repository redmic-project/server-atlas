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
import es.redmic.models.es.common.model.BaseAbstractStringES;

public class LayerWMS extends BaseAbstractStringES {

	@JsonIgnore
	public static final String JOIN_INDEX_NAME = "layer";

	private JoinIndex joinIndex;

	private String name;
	private String title;
	private Boolean queryable;
	private String abstractLayer;
	private List<String> keywords;
	private List<String> srs;
	private List<StyleLayer> stylesLayer;
	private Contact contact;

	private List<String> formats;
	private Polygon geometry;
	private Attribution attribution;
	private Dimension timeDimension;
	private Dimension elevationDimension;

	public LayerWMS() {
		joinIndex = new JoinIndex();
		joinIndex.setName(JOIN_INDEX_NAME);
	}

	public JoinIndex getJoinIndex() {
		return joinIndex;
	}

	public void setJoinIndex(JoinIndex joinIndex) {
		this.joinIndex = joinIndex;
		joinIndex.setName(JOIN_INDEX_NAME);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getQueryable() {
		return queryable;
	}

	public void setQueryable(Boolean queryable) {
		this.queryable = queryable;
	}

	public String getAbstractLayer() {
		return abstractLayer;
	}

	public void setAbstractLayer(String abstractLayer) {
		this.abstractLayer = abstractLayer;
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

	public List<StyleLayer> getStylesLayer() {
		return stylesLayer;
	}

	public void setStylesLayer(List<StyleLayer> stylesLayer) {
		this.stylesLayer = stylesLayer;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public List<String> getFormats() {
		return formats;
	}

	public void setFormats(List<String> formats) {
		this.formats = formats;
	}

	public Polygon getGeometry() {
		return geometry;
	}

	public void setGeometry(Polygon geometry) {
		this.geometry = geometry;
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
}
