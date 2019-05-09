package es.redmic.atlaslib.dto.category;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

import es.redmic.atlaslib.dto.layer.LayerBaseDTO;

/**
 * DTO de entrada para datos enviados por el cliente y que corresponde con una
 * categoría que englobará un conjunto de capas
 * 
 * Necesario validación DTO
 * 
 */
public class CategoryDTO extends LayerBaseDTO {

	// @formatter:off
	
	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"CategoryDTO\",\"namespace\":\"es.redmic.atlaslib.dto.category\",\"fields\":["
				+ "{\"name\":\"name\",\"type\":\"string\"},"
				+ "{\"name\":\"id\",\"type\":\"string\"}]}");
	// @formatter:on

	public CategoryDTO() {
		super();
	}

	@Override
	@NotNull
	@Size(min = 3)
	public String getName() {
		return super.getName();
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
			return getName();
		case 1:
			return getId();
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field, Object value) {
		switch (field) {
		case 0:
			setName(value.toString());
			break;
		case 1:
			setId(value.toString());
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
