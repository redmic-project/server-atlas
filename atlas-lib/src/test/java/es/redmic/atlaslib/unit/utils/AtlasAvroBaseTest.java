package es.redmic.atlaslib.unit.utils;

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

import java.util.HashMap;
import java.util.Properties;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

public abstract class AtlasAvroBaseTest {

	private final SchemaRegistryClient schemaRegistry;

	protected KafkaAvroSerializer avroSerializer;

	protected KafkaAvroDeserializer avroDeserializer;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AtlasAvroBaseTest() {

		Properties defaultConfig = new Properties();
		defaultConfig.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
		defaultConfig.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

		schemaRegistry = new MockSchemaRegistryClient();

		avroSerializer = new KafkaAvroSerializer(schemaRegistry, new HashMap(defaultConfig));

		avroDeserializer = new KafkaAvroDeserializer(schemaRegistry, new HashMap(defaultConfig));
	}

	@SuppressWarnings("unchecked")
	protected <T> T serializerAndDeserializer(T dto) {

		byte[] dtoBytes = avroSerializer.serialize("msg.t", dto);

		T result = (T) avroDeserializer.deserialize("msg.t", dtoBytes);

		avroSerializer.close();
		avroDeserializer.close();

		return result;
	}
}
