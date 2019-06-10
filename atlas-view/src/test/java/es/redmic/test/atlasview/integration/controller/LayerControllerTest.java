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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import es.redmic.atlasview.AtlasViewApplication;
import es.redmic.atlasview.model.category.Category;
import es.redmic.atlasview.model.layer.Layer;
import es.redmic.atlasview.repository.category.CategoryESRepository;
import es.redmic.atlasview.repository.layer.LayerESRepository;
import es.redmic.models.es.common.query.dto.AggsPropertiesDTO;
import es.redmic.models.es.common.query.dto.BboxQueryDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.testutils.documentation.DocumentationViewBaseTest;
import es.redmic.testutils.utils.JsonToBeanTestUtil;

@SpringBootTest(classes = { AtlasViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "schema.registry.port=19392" })
@DirtiesContext
public class LayerControllerTest extends DocumentationViewBaseTest {

	private static final String PARENT_ID = "category-333";

	@Value("${documentation.ATLAS_HOST}")
	private String HOST;

	@Value("${controller.mapping.LAYER}")
	private String LAYER_PATH;

	@Autowired
	LayerESRepository repository;

	@Autowired
	CategoryESRepository categoryRepository;

	Layer layer = new Layer();

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1);

	@PostConstruct
	public void CreateLayerFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@Override
	@Before
	public void setUp() {

		Category category = new Category();
		category.setId(PARENT_ID);
		category.setName("Sistema de cuadrículas geográficas");

		categoryRepository.save(category);

		try {
			layer = (Layer) JsonToBeanTestUtil.getBean("/data/model/layer/layer.json", Layer.class);
			layer.setAtlas(false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		layer.getJoinIndex().setParent(PARENT_ID);

		repository.save(layer, PARENT_ID);

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
		repository.delete(layer.getId(), PARENT_ID);
		categoryRepository.delete(PARENT_ID);
	}

	@Test
	public void getLayer_Return200_WhenItemExist() throws Exception {

		// @formatter:off
		
		this.mockMvc.perform(get(LAYER_PATH + "/" + layer.getId()).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body.id", is(layer.getId())));
		
		// @formatter:on
	}

	@Test
	public void searchLayersPost_Return200_WhenSearchIsCorrect() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);

		// @formatter:off
		
		this.mockMvc
				.perform(post(LAYER_PATH + "/_search").content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getSimpleQueryFieldsDescriptor());
		
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void searchLayersPostWithFacets_Return200_WhenSearchIsCorrect() throws Exception {

		GeoDataQueryDTO geoDataQuery = new GeoDataQueryDTO();

		List<AggsPropertiesDTO> aggs = new ArrayList<>();

		aggs.add(getAggProperties("keywords", "keywords"));

		AggsPropertiesDTO aggP = getAggProperties("protocols", "protocols.type");
		aggP.setNested("protocols");
		aggs.add(aggP);
		aggs.add(getAggProperties("themeInspire", "themeInspire.name"));
		geoDataQuery.setAggs(aggs);

		geoDataQuery.setSize(1);

		// Se elimina accessibilityIds ya que no está permitido para usuarios
		// básicos
		HashMap<String, Object> query = mapper.convertValue(geoDataQuery, HashMap.class);
		query.remove("accessibilityIds");

		// @formatter:off
		
		MvcResult x = this.mockMvc
				.perform(post(LAYER_PATH + "/_search").content(mapper.writeValueAsString(query))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
				.andExpect(jsonPath("$.body._aggs", notNullValue()))
				.andExpect(jsonPath("$.body._aggs.protocols", notNullValue()))
				.andExpect(jsonPath("$.body._aggs.themeInspire", notNullValue()))
				.andExpect(jsonPath("$.body._aggs.keywords", notNullValue()))
				.andExpect(jsonPath("$.body._aggs.protocols.sterms#protocols", notNullValue()))
				.andExpect(jsonPath("$.body._aggs.protocols.sterms#protocols.buckets", notNullValue()))
				.andExpect(jsonPath("$.body._aggs.themeInspire.buckets", notNullValue()))
				.andExpect(jsonPath("$.body._aggs.keywords.buckets", notNullValue()))
				.andExpect(jsonPath("$.body._aggs.protocols.sterms#protocols.buckets.length()", is(1)))
				.andExpect(jsonPath("$.body._aggs.themeInspire.buckets.length()", is(1)))
				.andExpect(jsonPath("$.body._aggs.keywords.buckets.length()", is(1))).andReturn();
		
		System.out.println(x.getResponse().getContentAsString());
		
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void searchLayersByBbox_Return200_WhenSearchIsCorrect() throws Exception {

		GeoDataQueryDTO geoDataQuery = new GeoDataQueryDTO();

		BboxQueryDTO bbox = new BboxQueryDTO();
		bbox.setBottomRightLat(26.91650390625);
		bbox.setBottomRightLon(-13.29345703125);
		bbox.setTopLeftLat(29.55322265625);
		bbox.setTopLeftLon(-18.78662109375);

		geoDataQuery.setBbox(bbox);
		geoDataQuery.setSize(1);

		// Se elimina accessibilityIds ya que no está permitido para usuarios
		// básicos
		HashMap<String, Object> query = mapper.convertValue(geoDataQuery, HashMap.class);
		query.remove("accessibilityIds");

		// @formatter:off
		
		this.mockMvc
				.perform(post(LAYER_PATH + "/_search").content(mapper.writeValueAsString(query))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)));
		
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void searchLayersByBbox_NoReturnRecords_WhenDataNotSatisfyQuery() throws Exception {

		GeoDataQueryDTO geoDataQuery = new GeoDataQueryDTO();

		BboxQueryDTO bbox = new BboxQueryDTO();
		bbox.setBottomRightLat(27.7294921875);
		bbox.setBottomRightLon(-30.56640625);
		bbox.setTopLeftLat(30.3662109375);
		bbox.setTopLeftLon(-36.0595703125);

		geoDataQuery.setBbox(bbox);
		geoDataQuery.setSize(1);

		// Se elimina accessibilityIds ya que no está permitido para usuarios
		// básicos
		HashMap<String, Object> query = mapper.convertValue(geoDataQuery, HashMap.class);
		query.remove("accessibilityIds");

		// @formatter:off
		
		this.mockMvc
				.perform(post(LAYER_PATH + "/_search").content(mapper.writeValueAsString(query))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(0)));
		
		// @formatter:on
	}

	@Test
	public void searchLayersPost_NoReturnResult_WhenSearchLayersWithAtlasEqualToTrue() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);

		dataQuery.addTerm("atlas", true);

		// @formatter:off
		
		this.mockMvc
				.perform(post(LAYER_PATH + "/_search").content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(0)));
		
		// @formatter:on
	}

	@Test
	public void searchLayersPost_ReturnResult_WhenSearchLayersWithAtlasEqualToTrue() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);

		dataQuery.addTerm("atlas", true);

		Layer layerAtlas = (Layer) JsonToBeanTestUtil.getBean("/data/model/layer/layer.json", Layer.class);
		layerAtlas.setId("layer-1234");
		layerAtlas.setName("atlas");
		layerAtlas.getJoinIndex().setParent(PARENT_ID);
		repository.save(layerAtlas, PARENT_ID);

		// @formatter:off
		
		this.mockMvc
				.perform(post(LAYER_PATH + "/_search").content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)));
		
		// @formatter:on

		repository.delete(layerAtlas.getId(), PARENT_ID);
	}

	@Test
	public void searchLayersQueryString_Return200_WhenSearchIsCorrect() throws Exception {

		// @formatter:off
		
		this.mockMvc
			.perform(get(LAYER_PATH)
					.param("fields", "{name}")
					.param("text", layer.getName())
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
	public void mgetLayers_Return200_WhenLayersExists() throws Exception {

		MgetDTO mgetQuery = new MgetDTO();
		mgetQuery.setIds(Arrays.asList(layer.getId()));
		mgetQuery.setFields(Arrays.asList("id"));

		// @formatter:off
		
		this.mockMvc
			.perform(post(LAYER_PATH + "/_mget").content(mapper.writeValueAsString(mgetQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data[0].id", is(layer.getId())))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getMgetRequestDescription());
		// @formatter:on
	}

	@Test
	public void suggestLayersQueryString_Return200_WhenSuggestIsCorrect() throws Exception {

		// @formatter:off
		
		this.mockMvc
			.perform(get(LAYER_PATH + "/_suggest")
					.param("fields", new String[] { "name" })
					.param("text", layer.getName())
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
	public void suggestLayersPost_Return200_WhenSuggestIsCorrect() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);
		dataQuery.createSimpleQueryDTOFromSuggestQueryParams(new String[] { "name" }, layer.getName(), 1);

		// @formatter:off
		
		this.mockMvc
			.perform(post(LAYER_PATH + "/_suggest").content(mapper.writeValueAsString(dataQuery))
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
				.getBean("/data/schemas/geodataquerydtoschema.json", Map.class);

		// @formatter:off
		
		this.mockMvc.perform(get(LAYER_PATH + filterSchemaPath)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body", is(schemaExpected)));
		// @formatter:on
	}

	private AggsPropertiesDTO getAggProperties(String term, String field) {

		AggsPropertiesDTO aggProperties = new AggsPropertiesDTO();
		aggProperties.setField(field);
		aggProperties.setTerm(term);
		aggProperties.setSize(10);
		aggProperties.setMinCount(1);
		return aggProperties;
	}
}
