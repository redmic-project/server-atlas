package es.redmic.test.atlasview.integration.controller;

/*-
 * #%L
 * Atlas-query-endpoint
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

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import es.redmic.atlasview.AtlasViewApplication;
import es.redmic.atlasview.model.category.Category;
import es.redmic.atlasview.repository.category.CategoryESRepository;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.testutils.documentation.DocumentationViewBaseTest;
import es.redmic.testutils.utils.JsonToBeanTestUtil;

@SpringBootTest(classes = { AtlasViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "schema.registry.port=19092" })
@DirtiesContext
public class CategoryControllerTest extends DocumentationViewBaseTest {

	@Value("${documentation.ATLAS_HOST}")
	private String HOST;

	@Value("${controller.mapping.CATEGORY}")
	private String CATEGORY_PATH;

	@Autowired
	CategoryESRepository repository;

	Category category = new Category();

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1);

	@PostConstruct
	public void CreateCategoryFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@Override
	@Before
	public void setUp() {

		String code = UUID.randomUUID().toString();

		category.setId("category-" + code);
		category.setName("Sistema de cuadrículas geográficas");

		repository.save(category);

		// @formatter:off
		
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.apply(documentationConfiguration(this.restDocumentation)
						.uris().withScheme(SCHEME).withHost(HOST).withPort(PORT))
				.alwaysDo(this.document).build();

		// @formatter:on
	}

	@After
	public void clean() {
		repository.delete(category.getId());
	}

	@Test
	public void getCategory_Return200_WhenItemExist() throws Exception {

		// @formatter:off
		
		this.mockMvc.perform(get(CATEGORY_PATH + "/" + category.getId()).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body.id", is(category.getId())));
		
		// @formatter:on
	}

	@Test
	public void searchCategorysPost_Return200_WhenSearchIsCorrect() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);

		// @formatter:off
		
		this.mockMvc
				.perform(post(CATEGORY_PATH + "/_search").content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getSimpleQueryFieldsDescriptor());
		
		// @formatter:on
	}

	@Test
	public void searchCategorysQueryString_Return200_WhenSearchIsCorrect() throws Exception {

		// @formatter:off
		
		this.mockMvc
			.perform(get(CATEGORY_PATH)
					.param("fields", "{name}")
					.param("text", category.getName())
					.param("from", "0")
					.param("size", "1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getSearchSimpleParametersDescription());
		
		// @formatter:off
	}

	@Test
	public void mgetCategorys_Return200_WhenCategorysExists() throws Exception {

		MgetDTO mgetQuery = new MgetDTO();
		mgetQuery.setIds(Arrays.asList(category.getId()));
		mgetQuery.setFields(Arrays.asList("id"));

		// @formatter:off
		
		this.mockMvc
			.perform(post(CATEGORY_PATH + "/_mget").content(mapper.writeValueAsString(mgetQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data[0].id", is(category.getId())))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getMgetRequestDescription());
		
		// @formatter:on
	}

	@Test
	public void suggestCategorysQueryString_Return200_WhenSuggestIsCorrect() throws Exception {

		// @formatter:off
		
		this.mockMvc
			.perform(get(CATEGORY_PATH + "/_suggest")
					.param("fields", new String[] { "name" })
					.param("text", category.getName())
					.param("size", "1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.length()", is(1)))
				.andExpect(jsonPath("$.body[0]", startsWith("<b>")))
				.andExpect(jsonPath("$.body[0]", endsWith("</b>")))
					.andDo(getSuggestParametersDescription());
		
		// @formatter:on
	}

	@Test
	public void suggestCategorysPost_Return200_WhenSuggestIsCorrect() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);
		dataQuery.createSimpleQueryDTOFromSuggestQueryParams(new String[] { "name" }, category.getName(), 1);

		// @formatter:off
		
		this.mockMvc
			.perform(post(CATEGORY_PATH + "/_suggest").content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.length()", is(1)))
				.andExpect(jsonPath("$.body[0]", startsWith("<b>")))
				.andExpect(jsonPath("$.body[0]", endsWith("</b>")))
					.andDo(getSimpleQueryFieldsDescriptor());;
				
		
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getFilterSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		Map<String, Object> schemaExpected = (Map<String, Object>) JsonToBeanTestUtil
				.getBean("/data/schemas/simplequerydtoschema.json", Map.class);

		// @formatter:off
		
		this.mockMvc.perform(get(CATEGORY_PATH + filterSchemaPath)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body", is(schemaExpected)));
		// @formatter:on
	}
}
