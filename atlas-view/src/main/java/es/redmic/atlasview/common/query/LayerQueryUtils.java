package es.redmic.atlasview.common.query;

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
import java.util.HashSet;

import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.EnvelopeBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.locationtech.jts.geom.Coordinate;

import es.redmic.elasticsearchlib.common.query.DataQueryUtils;
import es.redmic.exception.elasticsearch.ESBBoxQueryException;
import es.redmic.models.es.common.query.dto.BboxQueryDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;

public class LayerQueryUtils extends DataQueryUtils {

	public static BoolQueryBuilder getQuery(GeoDataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getBaseQuery(queryDTO, internalQuery, partialQuery));

		addMustTermIfExist(query, getShapeQuery(queryDTO.getBbox()));

		return getResultQuery(query);
	}

	@SuppressWarnings("serial")
	public static HashSet<String> getFieldsExcludedOnQuery() {

		return new HashSet<String>() {
			{
				add("activityId");
				add("accessibilityIds");
			}
		};
	}

	public static GeoShapeQueryBuilder getShapeQuery(BboxQueryDTO bbox) {

		if (bbox != null && bbox.getBottomRightLat() != null && bbox.getBottomRightLon() != null
				&& bbox.getTopLeftLat() != null && bbox.getTopLeftLon() != null) {

			// @formatter:off

			EnvelopeBuilder shape = new EnvelopeBuilder(
					new Coordinate(bbox.getTopLeftLon(), bbox.getTopLeftLat()),
					new Coordinate(bbox.getBottomRightLon(), bbox.getBottomRightLat())
			);

			// @formatter:on

			GeoShapeQueryBuilder qb;

			try {
				qb = QueryBuilders.geoShapeQuery("geometry", shape);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ESBBoxQueryException(e);
			}

			qb.relation(ShapeRelation.INTERSECTS);

			return qb;
		}
		return null;
	}
}
