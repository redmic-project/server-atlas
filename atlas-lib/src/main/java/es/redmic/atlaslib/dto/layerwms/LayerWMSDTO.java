package es.redmic.atlaslib.dto.layerwms;

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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.avro.Schema;
import org.locationtech.jts.geom.Polygon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaDefault;

import es.redmic.atlaslib.dto.layer.AttributionDTO;
import es.redmic.atlaslib.dto.layer.ContactDTO;
import es.redmic.atlaslib.dto.layer.DimensionDTO;
import es.redmic.atlaslib.dto.layer.LayerBaseDTO;
import es.redmic.atlaslib.dto.layer.StyleLayerDTO;
import es.redmic.jts4jackson.module.JTSModule;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LayerWMSDTO extends LayerBaseDTO {

	@JsonIgnore
	protected ObjectMapper mapper = new ObjectMapper().registerModule(new JTSModule());

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"LayerWMSDTO\",\"namespace\":\"es.redmic.atlaslib.dto.layerwms\",\"fields\":["
			+ "{\"name\":\"title\",\"type\":\"string\"},"
			+ "{\"name\":\"abstractLayer\",\"type\":[\"string\", \"null\"]},"
			+ "{\"name\":\"keywords\",\"type\":[{\"type\":\"array\",\"items\":\"string\"},\"null\"]},"
			+ "{\"name\":\"srs\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},"
			+ "{\"name\":\"stylesLayer\",\"type\":[{\"type\": \"array\",\"items\":" + StyleLayerDTO.SCHEMA$ + "},\"null\"]},"
			+ "{\"name\":\"contact\",\"type\":[" + ContactDTO.SCHEMA$ + ",\"null\"]},"
			+ "{\"name\":\"queryable\",\"type\":\"boolean\", \"default\": \"true\"},"
			+ "{\"name\":\"formats\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},"
			+ "{\"name\":\"image\",\"type\":[\"string\", \"null\"]},"
			+ "{\"name\":\"geometry\",\"type\":\"string\"},"
			+ "{\"name\":\"legend\",\"type\":[\"string\", \"null\"]},"
			+ "{\"name\":\"attibution\",\"type\":[" + AttributionDTO.SCHEMA$ + ",\"null\"]},"
			+ "{\"name\":\"timeDimension\",\"type\":[" + DimensionDTO.SCHEMA$ + ",\"null\"]},"
			+ "{\"name\":\"elevationDimension\"," + "\"type\":"
					+ "[\"es.redmic.atlaslib.dto.layer.DimensionDTO\",\"null\"]},"
			+ "{\"name\":\"name\",\"type\":\"string\"},"
			+ "{\"name\":\"id\",\"type\":\"string\"}]}");
	// @formatter:on

	public LayerWMSDTO() {
		super();
	}

	@NotNull
	@Size(min = 3)
	private String title;

	private String abstractLayer;

	private List<String> keywords;

	@NotNull
	@Size(min = 1)
	private List<String> srs;

	@Valid
	private List<StyleLayerDTO> stylesLayer;

	@Valid
	private ContactDTO contact;

	@JsonSchemaDefault(value = "true")
	@NotNull
	private Boolean queryable = true;

	@NotNull
	@Size(min = 1)
	private List<String> formats;

	private String image;

	@NotNull
	private Polygon geometry;

	private String legend;

	private AttributionDTO attribution;

	private DimensionDTO timeDimension;

	private DimensionDTO elevationDimension;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public List<StyleLayerDTO> getStylesLayer() {
		return stylesLayer;
	}

	public void setStylesLayer(List<StyleLayerDTO> stylesLayer) {
		this.stylesLayer = stylesLayer;
	}

	public ContactDTO getContact() {
		return contact;
	}

	public void setContact(ContactDTO contact) {
		this.contact = contact;
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

	public AttributionDTO getAttribution() {
		return attribution;
	}

	public void setAttribution(AttributionDTO attribution) {
		this.attribution = attribution;
	}

	public DimensionDTO getTimeDimension() {
		return timeDimension;
	}

	public void setTimeDimension(DimensionDTO timeDimension) {
		this.timeDimension = timeDimension;
	}

	public DimensionDTO getElevationDimension() {
		return elevationDimension;
	}

	public void setElevationDimension(DimensionDTO elevationDimension) {
		this.elevationDimension = elevationDimension;
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
			return title;
		case 1:
			return abstractLayer;
		case 2:
			return keywords;
		case 3:
			return srs;
		case 4:
			return stylesLayer;
		case 5:
			return contact;
		case 6:
			return queryable;
		case 7:
			return formats;
		case 8:
			return image;
		case 9:
			try {
				return mapper.writeValueAsString(getGeometry());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
		case 10:
			return legend;
		case 11:
			return attribution;
		case 12:
			return timeDimension;
		case 13:
			return elevationDimension;
		case 14:
			return getName();
		case 15:
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
			title = value != null ? value.toString() : null;
			break;
		case 1:
			abstractLayer = value != null ? value.toString() : null;
			break;
		case 2:
			keywords = value != null ? getStringList((java.util.List) value) : null;
			break;
		case 3:
			srs = value != null ? getStringList((java.util.List) value) : null;
			break;
		case 4:
			stylesLayer = value != null ? (java.util.List) value : null;
			break;
		case 5:
			contact = value != null ? (ContactDTO) value : null;
			break;
		case 6:
			queryable = value != null ? (Boolean) value : null;
			break;
		case 7:
			formats = value != null ? getStringList((java.util.List) value) : null;
			break;
		case 8:
			image = value != null ? value.toString() : null;
			break;
		case 9:
			try {
				if (value != null) {
					setGeometry(mapper.readValue(value.toString(), Polygon.class));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 10:
			legend = value != null ? value.toString() : null;
			break;
		case 11:
			attribution = value != null ? (AttributionDTO) value : null;
			break;
		case 12:
			timeDimension = value != null ? (DimensionDTO) value : null;
			break;
		case 13:
			elevationDimension = value != null ? (DimensionDTO) value : null;
			break;
		case 14:
			setName(value.toString());
			break;
		case 15:
			setId(value.toString());
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	private List<String> getStringList(List<?> value) {

		return value.stream().map(s -> s.toString()).collect(Collectors.toList());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((abstractLayer == null) ? 0 : abstractLayer.hashCode());
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result + ((formats == null) ? 0 : formats.hashCode());
		result = prime * result + ((geometry == null) ? 0 : geometry.hashCode());
		result = prime * result + ((legend == null) ? 0 : legend.hashCode());
		result = prime * result + ((attribution == null) ? 0 : attribution.hashCode());
		result = prime * result + ((timeDimension == null) ? 0 : timeDimension.hashCode());
		result = prime * result + ((elevationDimension == null) ? 0 : elevationDimension.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
		result = prime * result + ((queryable == null) ? 0 : queryable.hashCode());
		result = prime * result + ((srs == null) ? 0 : srs.hashCode());
		result = prime * result + ((stylesLayer == null) ? 0 : stylesLayer.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		LayerWMSDTO other = (LayerWMSDTO) obj;
		if (abstractLayer == null) {
			if (other.abstractLayer != null)
				return false;
		} else if (!abstractLayer.equals(other.abstractLayer))
			return false;
		if (contact == null) {
			if (other.contact != null)
				return false;
		} else if (!contact.equals(other.contact))
			return false;
		if (formats == null) {
			if (other.formats != null)
				return false;
		} else if (!formats.equals(other.formats))
			return false;
		if (geometry == null) {
			if (other.geometry != null)
				return false;
		} else if (!geometry.equals(other.geometry))
			return false;
		if (legend == null) {
			if (other.legend != null)
				return false;
		} else if (!legend.equals(other.legend))
			return false;
		if (attribution == null) {
			if (other.attribution != null)
				return false;
		} else if (!attribution.equals(other.attribution))
			return false;
		if (timeDimension == null) {
			if (other.timeDimension != null)
				return false;
		} else if (!timeDimension.equals(other.timeDimension))
			return false;
		if (elevationDimension == null) {
			if (other.elevationDimension != null)
				return false;
		} else if (!elevationDimension.equals(other.elevationDimension))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (keywords == null) {
			if (other.keywords != null)
				return false;
		} else if (!keywords.equals(other.keywords))
			return false;
		if (queryable == null) {
			if (other.queryable != null)
				return false;
		} else if (!queryable.equals(other.queryable))
			return false;
		if (srs == null) {
			if (other.srs != null)
				return false;
		} else if (!srs.equals(other.srs))
			return false;
		if (stylesLayer == null) {
			if (other.stylesLayer != null)
				return false;
		} else if (!stylesLayer.equals(other.stylesLayer))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
