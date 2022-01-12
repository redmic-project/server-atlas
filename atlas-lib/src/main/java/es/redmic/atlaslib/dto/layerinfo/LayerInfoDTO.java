package es.redmic.atlaslib.dto.layerinfo;

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

import javax.validation.constraints.NotNull;
import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaUrl;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaUrlUuid;

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.dto.layer.DownloadDTO;
import es.redmic.atlaslib.dto.layer.LatLonBoundingBoxDTO;
import es.redmic.atlaslib.dto.layer.LayerActivityDTO;

import es.redmic.atlaslib.dto.layer.LayerCompactDTO;
import es.redmic.atlaslib.dto.layer.ProtocolDTO;
import es.redmic.atlaslib.dto.layer.TimeDefinitionDTO;
import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.brokerlib.deserializer.CustomRelationDeserializer;

/**
 * DTO de entrada para datos enviados por el cliente y que complementan a los
 * obtenidos vía getCapability.
 *
 * Necesario validación DTO
 *
 */
public class LayerInfoDTO extends LayerCompactDTO {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"LayerDTO\",\"namespace\":\"es.redmic.atlaslib.dto.layerinfo\",\"fields\":["
			+ "{\"name\":\"parent\",\"type\":" + CategoryDTO.SCHEMA$ + "},"
			+ "{\"name\":\"legend\",\"type\":[\"null\", \"string\"]},"
			+ "{\"name\":\"relatedActivities\",\"type\": [\"null\", {\"type\": \"array\",\"items\": "+ LayerActivityDTO.SCHEMA$ +"}]},"
			+ "{\"name\":\"themeInspire\",\"type\":[\"null\", " + ThemeInspireDTO.SCHEMA$ + "]},"
			+ "{\"name\":\"latLonBoundsImage\",\"type\":[\"null\", " + LatLonBoundingBoxDTO.SCHEMA$ + "]},"
			+ "{\"name\":\"protocols\",\"type\": [{\"type\": \"array\",\"items\":" + ProtocolDTO.SCHEMA$ + "}]},"
			+ "{\"name\":\"downloads\",\"type\": [\"null\", {\"type\": \"array\",\"items\":" + DownloadDTO.SCHEMA$ + "}]},"
			+ "{\"name\":\"timeDefinition\",\"type\":[\"null\", " + TimeDefinitionDTO.SCHEMA$ + "]},"
			+ "{\"name\":\"description\",\"type\":[\"null\", \"string\"]},"
			+ "{\"name\":\"alias\",\"type\":[\"null\", \"string\"]},"
			+ "{\"name\":\"atlas\",\"type\":\"boolean\", \"default\": \"false\"},"
			+ "{\"name\":\"refresh\",\"type\":\"int\", \"default\": \"0\"},"
			+ "{\"name\":\"urlSource\",\"type\":\"string\"},"
			+ "{\"name\":\"styles\",\"type\": [\"null\", \"string\"]},"
			+ "{\"name\":\"name\",\"type\":\"string\"},"
			+ "{\"name\":\"id\",\"type\":\"string\"}]}");
	// @formatter:on

	public LayerInfoDTO() {
		super();
	}

	@NotNull
	@JsonDeserialize(using = CustomRelationDeserializer.class)
	@JsonSchemaUrlUuid(value = "controller.mapping.CATEGORY")
	CategoryDTO parent;

	private String legend;

	public CategoryDTO getParent() {
		return parent;
	}

	public void setParent(CategoryDTO parent) {
		this.parent = parent;
	}

	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	@JsonIgnore
	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}

	@JsonIgnore
	@Override
	public Object get(int field) {
		switch (field) {
		case 0:
			return getParent();
		case 1:
			return getRelatedActivities();
		case 2:
			return getThemeInspire();
		case 3:
			return getLatLonBoundsImage();
		case 4:
			return getProtocols();
		case 5:
			return getDownloads();
		case 6:
			return getTimeDefinition();
		case 7:
			return getDescription();
		case 8:
			return getAlias();
		case 9:
			return getAtlas();
		case 10:
			return getRefresh();
		case 11:
			return getUrlSource();
		case 12:
			return getStyles();
		case 13:
			return getName();
		case 14:
			return getId();
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@JsonIgnore
	@Override
	public void put(int field, Object value) {
		switch (field) {
		case 0:
			setParent((CategoryDTO) value);
			break;
		case 1:
			setRelatedActivities(value != null ? (java.util.List) value : null);
			break;
		case 2:
			setThemeInspire(value != null ? (ThemeInspireDTO) value : null);
			break;
		case 3:
			setLatLonBoundsImage(value != null ? (LatLonBoundingBoxDTO) value : null);
			break;
		case 4:
			setProtocols(value != null ? (java.util.List) value : null);
			break;
		case 5:
			setDownloads(value != null ? (java.util.List) value : null);
			break;
		case 6:
			setTimeDefinition(value != null ? (TimeDefinitionDTO) value : null);
			break;
		case 7:
			setDescription(value != null ? value.toString() : null);
			break;
		case 8:
			setAlias(value != null ? value.toString() : null);
			break;
		case 9:
			setAtlas((Boolean) value);
			break;
		case 10:
			setRefresh((int) value);
			break;
		case 11:
			setUrlSource(value != null ? value.toString() : null);
			break;
		case 12:
			setStyles(value != null ? value.toString() : null);
			break;
		case 13:
			setName(value.toString());
			break;
		case 14:
			setId(value.toString());
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayerInfoDTO other = (LayerInfoDTO) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (legend == null) {
			if (other.legend != null)
				return false;
		} else if (!legend.equals(other.legend))
			return false;
		return true;
	}
}
