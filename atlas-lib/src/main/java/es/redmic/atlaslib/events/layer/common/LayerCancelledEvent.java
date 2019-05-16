package es.redmic.atlaslib.events.layer.common;

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

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.brokerlib.avro.common.EventError;

public abstract class LayerCancelledEvent extends EventError {

	private LayerDTO layer;

	public LayerCancelledEvent(String type) {
		super(type);
		this.setLayer(layer);
	}

	public LayerDTO getLayer() {
		return layer;
	}

	public void setLayer(LayerDTO layer) {
		this.layer = layer;
	}

	@Override
	public Object get(int field$) {
		switch (field$) {
		case 0:
			return getLayer();
		case 1:
			return getExceptionType();
		case 2:
			return getArguments();
		case 3:
			return getAggregateId();
		case 4:
			return getVersion();
		case 5:
			return getType();
		case 6:
			return getDate().getMillis();
		case 7:
			return getSessionId();
		case 8:
			return getUserId();
		case 9:
			return getId();
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void put(int field$, Object value$) {
		switch (field$) {
		case 0:
			layer = (LayerDTO) value$;
			break;
		case 1:
			setExceptionType(value$.toString());
			break;
		case 2:
			setArguments((Map) value$);
			break;
		case 3:
			setAggregateId(value$.toString());
			break;
		case 4:
			setVersion((java.lang.Integer) value$);
			break;
		case 5:
			setType(value$.toString());
			break;
		case 6:
			setDate(new DateTime(value$, DateTimeZone.UTC));
			break;
		case 7:
			setSessionId(value$.toString());
			break;
		case 8:
			setUserId(value$.toString());
			break;
		case 9:
			setId(value$.toString());
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	public static String getLayerEventSchema() {

		return "{\"name\":\"layer\", \"type\": " + LayerDTO.SCHEMA$.toString() + "}";
	}
}
