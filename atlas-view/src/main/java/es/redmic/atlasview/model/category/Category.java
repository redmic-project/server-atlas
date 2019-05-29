package es.redmic.atlasview.model.category;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.redmic.elasticsearchlib.common.model.JoinIndex;

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

import es.redmic.models.es.common.model.BaseAbstractStringES;

public class Category extends BaseAbstractStringES {

	@JsonIgnore
	public static final String JOIN_INDEX_NAME = "category";

	private String name;

	private JoinIndex joinIndex;

	public Category() {
		joinIndex = new JoinIndex();
		joinIndex.setName(JOIN_INDEX_NAME);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JoinIndex getJoinIndex() {
		return joinIndex;
	}

	public void setJoinIndex(JoinIndex joinIndex) {
		this.joinIndex = joinIndex;
	}
}
