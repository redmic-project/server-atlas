package es.redmic.atlasview.repository.layer;

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

import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.redmic.atlasview.model.layer.Layer;
import es.redmic.atlasview.model.layer.LayerWMS;
import es.redmic.elasticsearchlib.common.utils.ElasticPersistenceUtils;
import es.redmic.elasticsearchlib.data.repository.RWDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.exception.elasticsearch.ESQueryException;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.viewlib.data.repository.IDataRepository;

@Repository
public class LayerESRepository extends RWDataESRepository<Layer, SimpleQueryDTO>
		implements IDataRepository<Layer, SimpleQueryDTO> {

	private static String[] INDEX = { "layer" };
	private static String TYPE = "_doc";

	// @formatter:off
	
	private final String ID_PROPERTY = "id",
			NAME_PROPERTY = "name",
			URL_SOURCE_PROPERTY = "urlSource";
	// @formatter:on

	@Autowired
	ElasticPersistenceUtils elasticPersistenceUtils;

	public LayerESRepository() {
		super(INDEX, TYPE);
		setInternalQuery(getLayerQuery());
	}

	@Override
	protected EventApplicationResult checkInsertConstraintsFulfilled(Layer modelToIndex) {

		QueryBuilder idTerm = QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()),
				nameAndUrlSourceTerm = QueryBuilders.boolQuery()
						.must(QueryBuilders.termQuery(NAME_PROPERTY, modelToIndex.getName()))
						.must(QueryBuilders.termQuery(URL_SOURCE_PROPERTY, modelToIndex.getUrlSource()));

		MultiSearchRequest request = new MultiSearchRequest();

		SearchSourceBuilder requestBuilderId = new SearchSourceBuilder().query(idTerm).size(1),
				requestBuilderNameAndUrlSource = new SearchSourceBuilder().query(nameAndUrlSourceTerm).size(1);

		request.add(new SearchRequest().indices(getIndex()).source(requestBuilderId))
				.add(new SearchRequest().indices(getIndex()).source(requestBuilderNameAndUrlSource));

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
			arguments.put(URL_SOURCE_PROPERTY, modelToIndex.getUrlSource());
		}

		if (arguments.size() > 0) {
			LOGGER.error(
					"No se puede modificar ya que incumple las restricciones de los datos " + arguments.toString());
			return new EventApplicationResult(ExceptionType.ES_INSERT_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkUpdateConstraintsFulfilled(Layer modelToIndex) {
		// @formatter:off
		
		BoolQueryBuilder nameAndUrlSourceTerm = QueryBuilders.boolQuery()
				.must(QueryBuilders.boolQuery()
						.must(QueryBuilders.termQuery(NAME_PROPERTY, modelToIndex.getName()))
						.must(QueryBuilders.termQuery(URL_SOURCE_PROPERTY, modelToIndex.getUrlSource())))
				.mustNot(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()));
		
		MultiSearchRequest request = new MultiSearchRequest();
		
		SearchSourceBuilder requestBuilder = new SearchSourceBuilder().query(nameAndUrlSourceTerm).size(1);
		
		request.add(new SearchRequest().indices(getIndex()).source(requestBuilder));
		
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
			arguments.put(ID_PROPERTY, modelToIndex.getId().toString());
			arguments.put(NAME_PROPERTY, modelToIndex.getName());
			arguments.put(URL_SOURCE_PROPERTY, modelToIndex.getUrlSource());
		}

		if (arguments.size() > 0) {
			LOGGER.error(
					"No se puede modificar ya que incumple las restricciones de los datos " + arguments.toString());
			return new EventApplicationResult(ExceptionType.ES_UPDATE_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkDeleteConstraintsFulfilled(String modelToIndexId) {

		return new EventApplicationResult(true);
	}

	public EventApplicationResult refresh(LayerWMS layer) {

		Layer source = (Layer) queryById(layer.getId()).get_source();

		return elasticPersistenceUtils.update(getIndex(source), getType(), layer, source.getId().toString(),
				source.getJoinIndex().getParent());
	}

	@Override
	public EventApplicationResult delete(String id) {

		EventApplicationResult checkDelete = checkDeleteConstraintsFulfilled(id);

		if (!checkDelete.isSuccess()) {
			return checkDelete;
		}

		Layer result = (Layer) queryById(id).get_source();

		return elasticPersistenceUtils.delete(getIndex()[0], getType(), id, result.getJoinIndex().getParent());
	}

	public DataHitWrapper<?> queryById(String id) {
		DataSearchWrapper<?> result = findBy(QueryBuilders.termQuery(ID_PROPERTY, id));

		if (result.getHits() == null || result.getHits().getTotal() == 0)
			throw new ItemNotFoundException("id", id + " en el servicio " + getIndex()[0] + " - " + getType());

		return result.getHits().getHits().get(0);
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
			query.must(QueryBuilders.termQuery("atlas", Boolean.valueOf(terms.get("atlas").toString())));
		}
		return super.getTermQuery(terms, query);
	}

	private QueryBuilder getLayerQuery() {
		return QueryBuilders.existsQuery(URL_SOURCE_PROPERTY);
	}
}
