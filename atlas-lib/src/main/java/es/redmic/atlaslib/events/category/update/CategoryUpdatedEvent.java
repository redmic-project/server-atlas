package es.redmic.atlaslib.events.category.update;

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

import es.redmic.atlaslib.dto.category.CategoryDTO;
import es.redmic.atlaslib.events.category.CategoryEventTypes;
import es.redmic.atlaslib.events.category.common.CategoryEvent;

public class CategoryUpdatedEvent extends CategoryEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CategoryUpdatedEvent\","
				+ "\"namespace\":\"es.redmic.atlaslib.events.category.update\",\"fields\":["
			+ getCategoryEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = CategoryEventTypes.UPDATED;

	public CategoryUpdatedEvent() {
		super(type);
	}

	public CategoryUpdatedEvent(CategoryDTO themeInspire) {
		super(type);
		this.setCategory(themeInspire);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
