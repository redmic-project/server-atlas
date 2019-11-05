package es.redmic.atlaslib.dto.layer;

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

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AttributionDTO extends org.apache.avro.specific.SpecificRecordBase
		implements org.apache.avro.specific.SpecificRecord {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"AttributionDTO\",\"namespace\":\"es.redmic.atlaslib.dto.layer\",\"fields\":["
				+ "{\"name\":\"title\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"onlineResource\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"logoURL\",\"type\":[" + LogoURLDTO.SCHEMA$ + ",\"null\"]}]}");
	// @formatter:on

	public AttributionDTO() {
		super();
	}

	private String title;

	private String onlineResource;

	private LogoURLDTO logoURL;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOnlineResource() {
		return onlineResource;
	}

	public void setOnlineResource(String onlineResource) {
		this.onlineResource = onlineResource;
	}

	public LogoURLDTO getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(LogoURLDTO logoURL) {
		this.logoURL = logoURL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((logoURL == null) ? 0 : logoURL.hashCode());
		result = prime * result + ((onlineResource == null) ? 0 : onlineResource.hashCode());
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
		AttributionDTO other = (AttributionDTO) obj;
		if (logoURL == null) {
			if (other.logoURL != null)
				return false;
		} else if (!logoURL.equals(other.logoURL))
			return false;
		if (onlineResource == null) {
			if (other.onlineResource != null)
				return false;
		} else if (!onlineResource.equals(other.onlineResource))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
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
			return title;
		case 1:
			return onlineResource;
		case 2:
			return logoURL;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
		case 0:
			title = value$ != null ? value$.toString() : null;
			break;
		case 1:
			onlineResource = value$ != null ? value$.toString() : null;
			break;
		case 2:
			logoURL = value$ != null ? (LogoURLDTO) value$ : null;
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
