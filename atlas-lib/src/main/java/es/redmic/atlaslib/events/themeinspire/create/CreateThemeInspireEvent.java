package es.redmic.atlaslib.events.themeinspire.create;

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

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.themeinspire.ThemeInspireEventTypes;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireEvent;

public class CreateThemeInspireEvent extends ThemeInspireEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateThemeInspireEvent\","
				+ "\"namespace\":\"es.redmic.atlaslib.events.themeinspire.create\",\"fields\":["
			+ getThemeInspireEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = ThemeInspireEventTypes.CREATE;

	public CreateThemeInspireEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public CreateThemeInspireEvent(ThemeInspireDTO themeInspire) {
		super(type);
		this.setThemeInspire(themeInspire);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
