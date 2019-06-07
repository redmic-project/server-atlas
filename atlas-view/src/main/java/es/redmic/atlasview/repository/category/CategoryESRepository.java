package es.redmic.atlasview.repository.category;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;

import es.redmic.atlasview.model.category.Category;
import es.redmic.atlasview.model.layer.Layer;
import es.redmic.elasticsearchlib.data.repository.RWDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.elasticsearch.ESQueryException;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.data.repository.IDataRepository;

@Repository
public class CategoryESRepository extends RWDataESRepository<Category, SimpleQueryDTO>
		implements IDataRepository<Category, SimpleQueryDTO> {

	private static String[] INDEX = { "layer" };
	private static String TYPE = "_doc";

	// @formatter:off
	
	private final String ID_PROPERTY = "id",
			NAME_PROPERTY = "name",
			URL_SOURCE_PROPERTY = "urlSource";
	// @formatter:on

	public CategoryESRepository() {
		super(INDEX, TYPE);
		setInternalQuery(getCategoryQuery());
	}

	@Override
	protected EventApplicationResult checkInsertConstraintsFulfilled(Category modelToIndex) {

		QueryBuilder idTerm = QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()),
				nameTerm = QueryBuilders.termQuery(NAME_PROPERTY, modelToIndex.getName());

		MultiSearchRequest request = new MultiSearchRequest();

		request.add(new SearchRequest().indices(getIndex()).source(new SearchSourceBuilder().query(idTerm).size(1)));
		request.add(new SearchRequest().indices(getIndex()).source(new SearchSourceBuilder().query(nameTerm).size(1)));

		MultiSearchResponse sr;
		try {
			sr = ESProvider.getClient().msearch(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(ID_PROPERTY, modelToIndex.getId());
		}
		if (responses != null && responses[1].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(NAME_PROPERTY, modelToIndex.getName());
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_INSERT_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkUpdateConstraintsFulfilled(Category modelToIndex) {
		// @formatter:off
		
		BoolQueryBuilder idTerm = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery(NAME_PROPERTY, modelToIndex.getName()))
				.mustNot(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()));
		
		MultiSearchRequest request = new MultiSearchRequest();
		
		request.add(new SearchRequest().indices(getIndex()).source(new SearchSourceBuilder().query(idTerm).size(1)));
		
		// @formatter:on

		MultiSearchResponse sr;
		try {
			sr = ESProvider.getClient().msearch(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(NAME_PROPERTY, modelToIndex.getName());
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_UPDATE_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkDeleteConstraintsFulfilled(String modelToIndexId) {

		HasChildQueryBuilder hasChildTerm = JoinQueryBuilders.hasChildQuery(Layer.JOIN_INDEX_NAME,
				QueryBuilders.matchAllQuery(), ScoreMode.None);

		MultiSearchRequest request = new MultiSearchRequest();

		SearchSourceBuilder requestBuilderHasChild = new SearchSourceBuilder().query(hasChildTerm).size(1);

		request.add(new SearchRequest().indices(getIndex()).source(requestBuilderHasChild));

		// @formatter:on

		MultiSearchResponse sr;
		try {
			sr = ESProvider.getClient().msearch(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(ID_PROPERTY, modelToIndexId);
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_DELETE_ITEM_WITH_CHILDREN_ERROR.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	/**
	 * Sobrescribe a la función base por incompatibilidad de query. Función que dado
	 * un conjunto de términos, nos devuelve una query de elasticsearch. Debe estar
	 * implementado en cada repositorio para darle una funcionalidad específica y
	 * aquí estarán las funcionalidades que comparten todos los repositorios.
	 * 
	 * @param terms
	 *            Map de términos pasados por la query.
	 * @param query
	 *            QueryBuilder con la query de los términos acumulados en los
	 *            repositorios específicos.
	 * @return query de tipo terms de elasticsearch.
	 */
	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("atlas")) {
			query.must(JoinQueryBuilders.hasChildQuery(Layer.JOIN_INDEX_NAME,
					QueryBuilders.termQuery("atlas", Boolean.valueOf(terms.get("atlas").toString())), ScoreMode.None));
		}
		return super.getTermQuery(terms, query);
	}

	private QueryBuilder getCategoryQuery() {

		return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(URL_SOURCE_PROPERTY));
	}
}
