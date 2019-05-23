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

import javax.validation.Valid;

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaUrl;

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.dto.layer.LatLonBoundingBoxDTO;
import es.redmic.atlaslib.dto.layer.LayerCompactDTO;
import es.redmic.atlaslib.dto.layer.ProtocolDTO;
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
			+ "{\"name\":\"parent\",\"type\":[" + CategoryDTO.SCHEMA$ + ",\"null\"]},"
			+ "{\"name\":\"themeInspire\",\"type\":["+ ThemeInspireDTO.SCHEMA$ +", \"null\"]},"
			+ "{\"name\":\"latLonBoundsImage\",\"type\":[" + LatLonBoundingBoxDTO.SCHEMA$ + ", \"null\"]},"
			+ "{\"name\": \"protocols\",\"type\": [{\"type\": \"array\",\"items\":" + ProtocolDTO.SCHEMA$ + "},\"null\"]},"
			+ "{\"name\":\"description\",\"type\":[\"string\", \"null\"]},"
			+ "{\"name\":\"alias\",\"type\":[\"string\", \"null\"]},"
			+ "{\"name\":\"atlas\",\"type\":\"boolean\", \"default\": \"false\"},"
			+ "{\"name\":\"refresh\",\"type\":\"int\", \"default\": \"0\"},"
			+ "{\"name\":\"urlSource\",\"type\":\"string\"},"
			+ "{\"name\":\"name\",\"type\":\"string\"},"
			+ "{\"name\":\"id\",\"type\":\"string\"}]}");
	// @formatter:on

	public LayerInfoDTO() {
		super();
	}

	@JsonDeserialize(using = CustomRelationDeserializer.class)
	@JsonSchemaUrl(value = "controller.mapping.CATEGORY")
	@Valid
	CategoryDTO parent;

	public CategoryDTO getParent() {
		return parent;
	}

	public void setParent(CategoryDTO parent) {
		this.parent = parent;
	}

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
			return getThemeInspire();
		case 2:
			return getLatLonBoundsImage();
		case 3:
			return getProtocols();
		case 4:
			return getDescription();
		case 5:
			return getAlias();
		case 6:
			return getAtlas();
		case 7:
			return getRefresh();
		case 8:
			return getUrlSource();
		case 9:
			return getName();
		case 10:
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
			setParent(value != null ? (CategoryDTO) value : null);
			break;
		case 1:
			setThemeInspire(value != null ? (ThemeInspireDTO) value : null);
			break;
		case 2:
			setLatLonBoundsImage(value != null ? (LatLonBoundingBoxDTO) value : null);
			break;
		case 3:
			setProtocols(value != null ? (java.util.List) value : null);
			break;
		case 4:
			setDescription(value != null ? value.toString() : null);
			break;
		case 5:
			setAlias(value != null ? value.toString() : null);
			break;
		case 6:
			setAtlas((Boolean) value);
			break;
		case 7:
			setRefresh((int) value);
			break;
		case 8:
			setUrlSource(value != null ? value.toString() : null);
			break;
		case 9:
			setName(value.toString());
			break;
		case 10:
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
		return true;
	}
}
