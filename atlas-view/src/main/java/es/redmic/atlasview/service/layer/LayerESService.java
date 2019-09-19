package es.redmic.atlasview.service.layer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.mapstruct.factory.Mappers;

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
import es.redmic.atlasview.mapper.layer.LayerESMapper;
import es.redmic.atlasview.model.layer.Layer;
import es.redmic.atlasview.model.layer.LayerWMS;
import es.redmic.atlasview.model.themeinspire.ThemeInspire;
import es.redmic.atlasview.repository.category.CategoryESRepository;
import es.redmic.atlasview.repository.layer.LayerESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.viewlib.data.dto.MetaDTO;
import es.redmic.viewlib.data.service.RDataService;

@Service
public class LayerESService extends RDataService<Layer, LayerDTO, GeoDataQueryDTO> {

	protected static Logger logger = LogManager.getLogger();

	LayerESRepository repository;

	CategoryESRepository categoryRepository;

	@Autowired
	public LayerESService(LayerESRepository repository, CategoryESRepository categoryRepository) {
		super(repository);
		this.repository = repository;
		this.categoryRepository = categoryRepository;
	}

	public EventApplicationResult save(Layer model, String parentId) {

		try {
			categoryRepository.findById(parentId);
		} catch (Exception e) {
			logger.error("Categoría con id ",
					parentId + " no encontrada. Imposible guardar una capa asociada a una categoría que no existe");
			return new EventApplicationResult(ExceptionType.ES_PARENT_NOT_EXIST_ERROR.toString(), "parentId", parentId);
		}

		return repository.save(model, parentId);
	}

	public EventApplicationResult update(Layer model, String parentId) {

		try {
			categoryRepository.findById(parentId);
		} catch (Exception e) {
			logger.error("Categoría con id ",
					parentId + " no encontrada. Imposible modificar una capa asociada a una categoría que no existe");
			return new EventApplicationResult(ExceptionType.ES_PARENT_NOT_EXIST_ERROR.toString(), "parentId", parentId);
		}

		return repository.update(model, parentId);
	}

	public EventApplicationResult refresh(LayerWMS model) {
		return repository.refresh(model);
	}

	public EventApplicationResult delete(String id) {
		return repository.delete(id);
	}

	public EventApplicationResult updateThemeInspireInLayer(String id, ThemeInspire themeInspire, DateTime updated) {
		return repository.updateThemeInspireInLayer(id, themeInspire, updated);
	}

	/**
	 * Sobrescribe findById para realizar una query en lugar de un get. En caso
	 * contrario sería necesario pasarle el id del padre
	 */
	@Override
	public MetaDTO<?> findById(String id) {

		return viewResultToDTO(repository.queryById(id));
	}

	/**
	 * Sobrescribe mget para realizar una query en lugar de un mget. En caso
	 * contrario sería necesario pasarle el id del padre
	 */
	@Override
	public JSONCollectionDTO mget(MgetDTO dto) {

		return viewResultToDTO(repository.searchByIds(dto.getIds().stream().toArray(String[]::new)).getHits());
	}

	@Override
	protected MetaDTO<?> viewResultToDTO(DataHitWrapper<?> viewResult) {
		return Mappers.getMapper(LayerESMapper.class).map(viewResult);
	}

	@Override
	protected JSONCollectionDTO viewResultToDTO(DataSearchWrapper<?> viewResult) {
		return Mappers.getMapper(LayerESMapper.class).map(viewResult);
	}

	@Override
	protected JSONCollectionDTO viewResultToDTO(DataHitsWrapper<?> viewResult) {
		return Mappers.getMapper(LayerESMapper.class).map(viewResult);
	}
}