package es.redmic.atlaslib.events.layer;

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

import es.redmic.brokerlib.avro.common.EventTypes;

public abstract class LayerEventTypes extends EventTypes {

	public static String
	// @formatter:off
		//REFRESH
		REFRESH = "REFRESH",
		REFRESH_CONFIRMED = "REFRESH_CONFIRMED",
		REFRESHED = "REFRESHED",
		REFRESH_FAILED = "REFRESH_FAILED",
		REFRESH_CANCELLED = "REFRESH_CANCELLED";
	//@formatter:on

	public static boolean isLocked(String eventType) {

		return !(eventType.equals(LayerEventTypes.REFRESHED.toString())
				|| eventType.equals(LayerEventTypes.REFRESH_CANCELLED.toString())) && EventTypes.isLocked(eventType);
	}

	public static boolean isSnapshot(String eventType) {

		return eventType.equals(LayerEventTypes.REFRESHED.toString()) || EventTypes.isSnapshot(eventType);
	}

	public static boolean isUpdatable(String eventType) {

		return (isSnapshot(eventType) && !eventType.equals(EventTypes.DELETED.toString()));
	}
}
