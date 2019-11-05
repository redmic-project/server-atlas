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

import javax.validation.constraints.NotNull;

import org.apache.avro.Schema;
import org.geotools.data.ows.CRSEnvelope;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LatLonBoundingBoxDTO extends org.apache.avro.specific.SpecificRecordBase
		implements org.apache.avro.specific.SpecificRecord {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"LatLonBoundingBoxDTO\",\"namespace\":\"es.redmic.atlaslib.dto.layer\",\"fields\":["
			+ "{\"name\":\"minX\",\"type\":\"double\"},"
			+ "{\"name\":\"minY\",\"type\":\"double\"},"
			+ "{\"name\":\"maxX\",\"type\":\"double\"},"
			+ "{\"name\":\"maxY\",\"type\":\"double\"}]}");
	// @formatter:on

	@NotNull
	private Double minX;
	@NotNull
	private Double minY;
	@NotNull
	private Double maxX;
	@NotNull
	private Double maxY;

	public LatLonBoundingBoxDTO() {
	}

	public LatLonBoundingBoxDTO(CRSEnvelope latLonBoundingBox) {

		setMinX(latLonBoundingBox.getMinX());
		setMinY(latLonBoundingBox.getMinY());
		setMaxX(latLonBoundingBox.getMaxX());
		setMaxY(latLonBoundingBox.getMaxY());
	}

	public Double getMinX() {
		return minX;
	}

	public void setMinX(Double minX) {
		this.minX = minX;
	}

	public Double getMinY() {
		return minY;
	}

	public void setMinY(Double minY) {
		this.minY = minY;
	}

	public Double getMaxX() {
		return maxX;
	}

	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}

	public Double getMaxY() {
		return maxY;
	}

	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((maxX == null) ? 0 : maxX.hashCode());
		result = prime * result + ((maxY == null) ? 0 : maxY.hashCode());
		result = prime * result + ((minX == null) ? 0 : minX.hashCode());
		result = prime * result + ((minY == null) ? 0 : minY.hashCode());
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
		LatLonBoundingBoxDTO other = (LatLonBoundingBoxDTO) obj;
		if (maxX == null) {
			if (other.maxX != null)
				return false;
		} else if (!maxX.equals(other.maxX))
			return false;
		if (maxY == null) {
			if (other.maxY != null)
				return false;
		} else if (!maxY.equals(other.maxY))
			return false;
		if (minX == null) {
			if (other.minX != null)
				return false;
		} else if (!minX.equals(other.minX))
			return false;
		if (minY == null) {
			if (other.minY != null)
				return false;
		} else if (!minY.equals(other.minY))
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
	public Object get(int field) {
		switch (field) {
		case 0:
			return minX;
		case 1:
			return minY;
		case 2:
			return maxX;
		case 3:
			return maxY;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field, Object value) {
		switch (field) {
		case 0:
			minX = (Double) value;
			break;
		case 1:
			minY = (Double) value;
			break;
		case 2:
			maxX = (Double) value;
			break;
		case 3:
			maxY = (Double) value;
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
