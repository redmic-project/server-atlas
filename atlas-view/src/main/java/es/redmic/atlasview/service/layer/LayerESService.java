package es.redmic.atlasview.service.layer;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlasview.model.layer.Layer;
import es.redmic.atlasview.repository.layer.LayerESRepository;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.data.service.RDataService;

@Service
public class LayerESService extends RDataService<Layer, LayerDTO, SimpleQueryDTO> {

	LayerESRepository repository;

	@Autowired
	public LayerESService(LayerESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	public EventApplicationResult save(Layer model, String parentId) {
		return repository.save(model, parentId);
	}

	public EventApplicationResult update(Layer model, String parentId) {
		return repository.update(model, parentId);
	}

	public EventApplicationResult delete(String id, String parentId) {
		return repository.delete(id, parentId);
	}
}
