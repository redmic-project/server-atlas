package es.redmic.atlasview.service.themeinspire;

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

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlasview.mapper.themeinspire.ThemeInspireESMapper;
import es.redmic.atlasview.model.themeinspire.ThemeInspire;
import es.redmic.atlasview.repository.themeinspire.ThemeInspireESRepository;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.viewlib.data.dto.MetaDTO;
import es.redmic.viewlib.data.service.RWDataService;

@Service
public class ThemeInspireESService extends RWDataService<ThemeInspire, ThemeInspireDTO, SimpleQueryDTO> {

	@Autowired
	public ThemeInspireESService(ThemeInspireESRepository repository) {
		super(repository);
	}

	@Override
	protected MetaDTO<?> viewResultToDTO(DataHitWrapper<?> viewResult) {
		return Mappers.getMapper(ThemeInspireESMapper.class).map(viewResult);
	}

	@Override
	protected JSONCollectionDTO viewResultToDTO(DataSearchWrapper<?> viewResult) {
		return Mappers.getMapper(ThemeInspireESMapper.class).map(viewResult);
	}

	@Override
	protected JSONCollectionDTO viewResultToDTO(DataHitsWrapper<?> viewResult) {
		return Mappers.getMapper(ThemeInspireESMapper.class).map(viewResult);
	}
}