package es.redmic.atlasview.mapper.layer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlasview.model.layer.Layer;
import es.redmic.viewlib.common.mapper.es2dto.DataCollectionESMapper;

@Mapper
public abstract class LayerESMapper extends DataCollectionESMapper<LayerDTO, Layer> {

	@Mapping(source = "model.joinIndex.parent", target = "parent.id")
	@Mapping(target = "parent.name", ignore = true)
	public abstract LayerDTO map(Layer model);

	@Mapping(source = "dto.parent.id", target = "joinIndex.parent")
	@Mapping(target = "joinIndex.name", ignore = true)
	public abstract Layer map(LayerDTO dto);

	@Override
	protected LayerDTO mapSource(Layer model) {
		return map(model);
	}
}