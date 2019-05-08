package es.redmic.atlaslib.dto.atlas;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.jts4jackson.module.JTSModule;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LayerDTO extends LayerCompactDTO {

	@JsonIgnore
	protected ObjectMapper mapper = new ObjectMapper().registerModule(new JTSModule());

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"LayerDTO\",\"namespace\":\"es.redmic.atlaslib.dto.atlas\",\"fields\":["
				+ "{\"name\":\"title\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"abstractLayer\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"keyword\",\"type\":[{\"type\":\"array\",\"items\":\"string\"},\"null\"]},"
				+ "{\"name\":\"srs\",\"type\":[{\"type\":\"array\",\"items\":\"string\"},\"null\"]},"
				+ "{\"name\":\"styleLayer\",\"type\":[{ \"name\":\"StyleLayerDTO\", \"type\":\"record\","
					+ "\"namespace\":\"es.redmic.atlaslib.dto.atlas\",\"fields\":["
						+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"title\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"abstractStyle\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"format\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"url\",\"type\":[\"string\", \"null\"]}]},\"null\"]},"
				+ "{\"name\":\"contact\",\"type\":[{ \"name\":\"ContactDTO\", \"type\":\"record\","
					+ "\"namespace\":\"es.redmic.atlaslib.dto.atlas\",\"fields\":["
						+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"email\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"phone\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"fax\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"address\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"organization\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"contactPosition\",\"type\":[\"string\", \"null\"]}]},\"null\"]},"
				+ "{\"name\": \"activities\",\"type\": [{\"type\": \"array\",\"items\": {\"type\":\"record\","
					+ "\"namespace\":\"es.redmic.atlaslib.dto.atlas\",\"name\":\"ActivityDTO\",\"fields\": ["
						+ "{\"name\": \"id\",\"type\": \"string\"},"
						+ "{\"name\": \"name\",\"type\": \"string\"},"
						+ "{\"name\": \"path\",\"type\": \"string\"}]}},\"null\"]},"
				+ "{\"name\":\"urlSource\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"queryable\",\"type\":[\"boolean\", \"null\"]},"
				+ "{\"name\":\"formats\",\"type\":[{\"type\":\"array\",\"items\":\"string\"},\"null\"]},"
				+ "{\"name\":\"image\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"geometry\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"themeInspire\",\"type\":[{\"type\":\"record\","
					+ "\"name\":\"ThemeInspireDTO\",\"namespace\":\"es.redmic.atlaslib.dto.themeinspire\",\"fields\":["
						+ "{\"name\":\"code\",\"type\":\"string\"},"
						+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"name_en\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"id\",\"type\":\"string\"}]}, \"null\"]},"
				+ "{\"name\":\"latLonBoundsImage\",\"type\":[{ \"name\":\"LatLonBoundingBoxDTO\", \"type\":\"record\","
					+ "\"namespace\":\"es.redmic.atlaslib.dto.atlas\",\"fields\":["
						+ "{\"name\":\"minX\",\"type\":\"double\"},"
						+ "{\"name\":\"minY\",\"type\":\"double\"},"
						+ "{\"name\":\"maxX\",\"type\":\"double\"},"
						+ "{\"name\":\"maxY\",\"type\":\"double\"}]}, \"null\"]},"
				+ "{\"name\": \"protocol\",\"type\": [{\"type\": \"array\",\"items\": {\"type\":\"record\","
					+ "\"namespace\":\"es.redmic.atlaslib.dto.atlas\",\"name\":\"ProtocolDTO\",\"fields\": ["
						+ "{\"name\": \"type\",\"type\": \"string\"},"
						+ "{\"name\": \"url\",\"type\": \"string\"}]}},\"null\"]},"
				+ "{\"name\":\"description\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"alias\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"atlas\",\"type\":\"boolean\", \"default\": \"false\"},"
				+ "{\"name\":\"refresh\",\"type\":\"int\", \"default\": \"0\"},"
				+ "{\"name\":\"name\",\"type\":\"string\"},"
				+ "{\"name\":\"id\",\"type\":\"string\"}]}");
	// @formatter:on

	public LayerDTO() {
		super();
	}

	private String title;

	private String abstractLayer;

	private List<String> keyword;

	private List<String> srs;

	private StyleLayerDTO styleLayer;

	private ContactDTO contact;

	private List<ActivityDTO> activities;

	private String urlSource;

	private Boolean queryable;

	private List<String> formats;

	private String image;

	private Geometry geometry;

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

	public StyleLayerDTO getStyleLayer() {
		return styleLayer;
	}

	public void setStyleLayer(StyleLayerDTO styleLayer) {
		this.styleLayer = styleLayer;
	}

	public ContactDTO getContact() {
		return contact;
	}

	public void setContact(ContactDTO contact) {
		this.contact = contact;
	}

	public List<ActivityDTO> getActivities() {
		return activities;
	}

	public void setActivities(List<ActivityDTO> activities) {
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

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
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
			return keyword;
		case 3:
			return srs;
		case 4:
			return styleLayer;
		case 5:
			return contact;
		case 6:
			return activities;
		case 7:
			return urlSource;
		case 8:
			return queryable;
		case 9:
			return formats;
		case 10:
			return image;
		case 11:
			try {
				return mapper.writeValueAsString(getGeometry());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
		case 12:
			return getThemeInspire();
		case 13:
			return getLatLonBoundsImage();
		case 14:
			return getProtocol();
		case 15:
			return getDescription();
		case 16:
			return getAlias();
		case 17:
			return getAtlas();
		case 18:
			return getRefresh();
		case 19:
			return getName();
		case 20:
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
			keyword = value != null ? (java.util.List) value : null;
			break;
		case 3:
			srs = value != null ? (java.util.List) value : null;
			break;
		case 4:
			styleLayer = value != null ? (StyleLayerDTO) value : null;
			break;
		case 5:
			contact = value != null ? (ContactDTO) value : null;
			break;
		case 6:
			activities = value != null ? (java.util.List) value : null;
			break;
		case 7:
			urlSource = value != null ? value.toString() : null;
			break;
		case 8:
			queryable = value != null ? (Boolean) value : null;
			break;
		case 9:
			formats = value != null ? (java.util.List) value : null;
			break;
		case 10:
			image = value != null ? value.toString() : null;
			break;
		case 11:
			try {
				if (value != null) {
					setGeometry(mapper.readValue((value.toString()), Point.class));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 12:
			setThemeInspire(value != null ? (ThemeInspireDTO) value : null);
			break;
		case 13:
			setLatLonBoundsImage(value != null ? (LatLonBoundingBoxDTO) value : null);
			break;
		case 14:
			setProtocol(value != null ? (java.util.List) value : null);
			break;
		case 15:
			setDescription(value != null ? value.toString() : null);
			break;
		case 16:
			setAlias(value != null ? value.toString() : null);
			break;
		case 17:
			setAtlas((Boolean) value);
			break;
		case 18:
			setRefresh((int) value);
			break;
		case 19:
			setName(value.toString());
			break;
		case 20:
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
		result = prime * result + ((abstractLayer == null) ? 0 : abstractLayer.hashCode());
		result = prime * result + ((activities == null) ? 0 : activities.hashCode());
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result + ((formats == null) ? 0 : formats.hashCode());
		result = prime * result + ((geometry == null) ? 0 : geometry.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + ((queryable == null) ? 0 : queryable.hashCode());
		result = prime * result + ((srs == null) ? 0 : srs.hashCode());
		result = prime * result + ((styleLayer == null) ? 0 : styleLayer.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((urlSource == null) ? 0 : urlSource.hashCode());
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
		LayerDTO other = (LayerDTO) obj;
		if (abstractLayer == null) {
			if (other.abstractLayer != null)
				return false;
		} else if (!abstractLayer.equals(other.abstractLayer))
			return false;
		if (activities == null) {
			if (other.activities != null)
				return false;
		} else if (!activities.equals(other.activities))
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
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
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
		if (styleLayer == null) {
			if (other.styleLayer != null)
				return false;
		} else if (!styleLayer.equals(other.styleLayer))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (urlSource == null) {
			if (other.urlSource != null)
				return false;
		} else if (!urlSource.equals(other.urlSource))
			return false;
		return true;
	}
}
