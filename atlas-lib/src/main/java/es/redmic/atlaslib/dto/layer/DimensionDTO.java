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

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DimensionDTO extends org.apache.avro.specific.SpecificRecordBase {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"DimensionDTO\",\"namespace\":\"es.redmic.atlaslib.dto.layer\",\"fields\":["
				+ "{\"name\":\"name\",\"type\":\"string\"},"
				+ "{\"name\":\"value\",\"type\":[\"null\", \"string\"]},"
				+ "{\"name\":\"units\",\"type\":\"string\"},"
				+ "{\"name\":\"unitSymbol\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"defaultValue\",\"type\":[\"string\", \"null\"]}]}");
	// @formatter:on

	public DimensionDTO() {
	}

	@NotNull
	private String name;

	private String value;

	@NotNull
	private String units;

	private String unitSymbol;

	private String defaultValue;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getUnitSymbol() {
		return unitSymbol;
	}

	public void setUnitSymbol(String unitSymbol) {
		this.unitSymbol = unitSymbol;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((units == null) ? 0 : units.hashCode());
		result = prime * result + ((unitSymbol == null) ? 0 : unitSymbol.hashCode());
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
		DimensionDTO other = (DimensionDTO) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (units == null) {
			if (other.units != null)
				return false;
		} else if (!units.equals(other.units))
			return false;
		if (unitSymbol == null) {
			if (other.unitSymbol != null)
				return false;
		} else if (!unitSymbol.equals(other.unitSymbol))
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
			return name;
		case 1:
			return value;
		case 2:
			return units;
		case 3:
			return unitSymbol;
		case 4:
			return defaultValue;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
		case 0:
			name = value$.toString();
			break;
		case 1:
			value = value$.toString();
			break;
		case 2:
			units = value$.toString();
			break;
		case 3:
			unitSymbol = value$ != null ? value$.toString() : null;
			break;
		case 4:
			defaultValue = value$.toString();
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
