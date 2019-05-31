package es.redmic.test.atlascommands.integration.ogc;

import static org.assertj.core.api.Assertions.assertThat;

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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import es.redmic.atlascommands.AtlasCommandsApplication;
import es.redmic.atlascommands.utils.Capabilities;
import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;
import es.redmic.models.es.common.dto.UrlDTO;
import es.redmic.test.atlascommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.documentation.DocumentationCommandBaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AtlasCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
public class DiscoverWMSLayersTest extends DocumentationCommandBaseTest {

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	// @formatter:off
	
	private final String HOST = "redmic.es/api/atlas/commands",
			PATH = "/discover-layers/wms",
			URL_CAPABILITIES = new File("src/test/resources/data/capabilities/wms.xml").toURI().toString();
	
	// @formatter:on

	private UrlDTO url;

	@Before
	public void before() {

		// @formatter:off
		
		url = new UrlDTO();
		url.setUrl(URL_CAPABILITIES);
		
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.apply(documentationConfiguration(this.restDocumentation)
						.uris().withScheme(SCHEME).withHost(HOST).withPort(PORT))
				.alwaysDo(this.document).build();

		// @formatter:on
	}

	@Test
	public void createLayer_SendCreateLayerEvent_IfCommandWasSuccess() throws Exception {

		// @formatter:off
		
		LayerWMSDTO layer = (LayerWMSDTO) Capabilities.getCapabilities(url.getUrl()).values().toArray()[0];

		MvcResult mvcResult = this.mockMvc.perform(post(PATH)
					.content(mapper.writeValueAsString(url))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body[0]", notNullValue()))
				.andExpect(jsonPath("$.body.length()", is(1)))
				.andReturn();
		// @formatter:on

		assertThat("{\"success\":true,\"body\":[" + mapper.writeValueAsString(layer) + "]}")
				.isEqualToIgnoringWhitespace(mvcResult.getResponse().getContentAsString());
	}
}
