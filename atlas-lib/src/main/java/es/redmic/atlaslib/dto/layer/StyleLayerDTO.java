package es.redmic.atlaslib.dto.layer;

import org.apache.avro.Schema;

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

import org.geotools.data.ows.StyleImpl;
import org.opengis.util.InternationalString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class StyleLayerDTO extends org.apache.avro.specific.SpecificRecordBase
		implements org.apache.avro.specific.SpecificRecord {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"StyleLayerDTO\",\"namespace\":\"es.redmic.atlaslib.dto.layer\",\"fields\":["
				+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"title\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"abstractStyle\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"format\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"url\",\"type\":[\"string\", \"null\"]}]}");
	// @formatter:on

	private String name;
	private String title;
	private String abstractStyle;
	private String format;
	private String url;

	public StyleLayerDTO() {
	}

	public StyleLayerDTO(StyleImpl style) {
		setName(style.getName());
		setTitle(style.getAbstract());
		setabstractStyle(style.getAbstract());
		if (style.getLegendURLs() != null && style.getLegendURLs().size() > 0)
			setUrl(style.getLegendURLs().get(0).toString());

		if (getUrl() != null)
			setFormat(getUrl().replaceAll(".*&format=(\\w*)%2F(\\w*)&.*", "$1/$2"));
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

	@JsonSetter("title")
	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(InternationalString title) {
		if (title != null)
			this.title = title.toString();
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getabstractStyle() {
		return abstractStyle;
	}

	@JsonSetter("abstractStyle")
	public void setabstractStyle(String abstractStyle) {
		this.abstractStyle = abstractStyle;
	}

	public void setabstractStyle(InternationalString abstractStyle) {
		if (abstractStyle != null)
			this.abstractStyle = abstractStyle.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abstractStyle == null) ? 0 : abstractStyle.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StyleLayerDTO other = (StyleLayerDTO) obj;
		if (abstractStyle == null) {
			if (other.abstractStyle != null)
				return false;
		} else if (!abstractStyle.equals(other.abstractStyle))
			return false;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Name: " + getName() + ", Title: " + getTitle() + ", Abstract: " + getabstractStyle() + ", URL: "
				+ getUrl();
	}

	@JsonIgnore
	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}

	@JsonIgnore
	@Override
	public java.lang.Object get(int field$) {
		switch (field$) {
		case 0:
			return name;
		case 1:
			return title;
		case 2:
			return abstractStyle;
		case 3:
			return format;
		case 4:
			return url;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
		case 0:
			name = value$ != null ? value$.toString() : null;
			break;
		case 1:
			title = value$ != null ? value$.toString() : null;
			break;
		case 2:
			abstractStyle = value$ != null ? value$.toString() : null;
			break;
		case 3:
			format = value$ != null ? value$.toString() : null;
			break;
		case 4:
			url = value$ != null ? value$.toString() : null;
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
