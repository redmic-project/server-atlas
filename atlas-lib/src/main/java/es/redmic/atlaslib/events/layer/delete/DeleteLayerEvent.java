package es.redmic.atlaslib.events.layer.delete;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.brokerlib.avro.common.SimpleEvent;

public class DeleteLayerEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"DeleteLayerEvent\","
				+ "\"namespace\":\"es.redmic.atlaslib.events.layer.delete\",\"fields\":["
			+ "{\"name\":\"parentId\",\"type\":[\"string\", \"null\"]},"
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = LayerEventTypes.DELETE;

	private String parentId;

	public DeleteLayerEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}

	@Override
	public Object get(int field$) {
		switch (field$) {
		case 0:
			return getParentId();
		case 1:
			return getAggregateId();
		case 2:
			return getVersion();
		case 3:
			return getType();
		case 4:
			return getDate().getMillis();
		case 5:
			return getSessionId();
		case 6:
			return getUserId();
		case 7:
			return getId();
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@Override
	public void put(int field$, Object value$) {
		switch (field$) {
		case 0:
			setParentId(value$ != null ? value$.toString() : null);
			break;
		case 1:
			setAggregateId(value$.toString());
			break;
		case 2:
			setVersion((java.lang.Integer) value$);
			break;
		case 3:
			setType(value$.toString());
			break;
		case 4:
			setDate(new DateTime(value$, DateTimeZone.UTC));
			break;
		case 5:
			setSessionId(value$.toString());
			break;
		case 6:
			setUserId(value$.toString());
			break;
		case 7:
			setId(value$.toString());
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
