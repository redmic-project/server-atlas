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
import es.redmic.atlasview.model.layer.LayerWMS;
import es.redmic.atlasview.repository.layer.LayerESRepository;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.data.dto.MetaDTO;
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

	public EventApplicationResult refresh(LayerWMS model) {
		return repository.refresh(model);
	}

	public EventApplicationResult delete(String id) {
		return repository.delete(id);
	}

	/**
	 * Sobrescribe findById para realizar una query en lugar de un get. En caso
	 * contrario sería necesario pasarle el id del padre
	 */
	@Override
	public MetaDTO<?> findById(String id) {

		return mapper.getMapperFacade().map(repository.queryById(id), MetaDTO.class, getMappingContext());
	}

	/**
	 * Sobrescribe mget para realizar una query en lugar de un mget. En caso
	 * contrario sería necesario pasarle el id del padre
	 */
	@Override
	public JSONCollectionDTO mget(MgetDTO dto) {

		return mapper.getMapperFacade().map(
				repository.searchByIds(dto.getIds().stream().toArray(String[]::new)).getHits(),
				JSONCollectionDTO.class);
	}
}
