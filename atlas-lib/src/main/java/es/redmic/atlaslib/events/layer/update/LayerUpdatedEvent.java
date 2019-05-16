package es.redmic.atlaslib.events.layer.update;

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

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.common.LayerEvent;

public class LayerUpdatedEvent extends LayerEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"LayerUpdatedEvent\","
				+ "\"namespace\":\"es.redmic.atlaslib.events.layer.update\",\"fields\":["
			+ getLayerEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = LayerEventTypes.UPDATED;

	public LayerUpdatedEvent() {
		super(type);
	}

	public LayerUpdatedEvent(LayerDTO themeInspire) {
		super(type);
		this.setLayer(themeInspire);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
