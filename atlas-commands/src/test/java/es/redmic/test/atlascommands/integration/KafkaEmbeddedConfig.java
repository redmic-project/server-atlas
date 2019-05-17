package es.redmic.test.atlascommands.integration;

/*-
 * #%L
 * Atlas-management
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

public class KafkaEmbeddedConfig {

	// number of brokers.
	public final static Integer NUM_BROKERS = 3;
	// partitions per topic.
	public final static Integer PARTITIONS_PER_TOPIC = 3;

	// @formatter:off
 
	public final static String[] TOPICS_NAME = new String[] { 
		"theme-inspire",
		"layer",
		"theme-inspire-updated",
		"layer-updated",
		"theme-inspire-snapshot",
		"layer-snapshot",
		"layer-agg-by-theme-inspire",
		"category",
		"category-snapshot"
	};
	
	// @formatter:on
}
