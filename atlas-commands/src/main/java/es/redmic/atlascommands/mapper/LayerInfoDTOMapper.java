package es.redmic.atlascommands.mapper;

/*-
 * #%L
 * Atlas-management
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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layerinfo.LayerInfoDTO;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LayerInfoDTOMapper {

	// @formatter:off

	@Mapping(source = "layerInfoDTO.id", target = "id")
	@Mapping(source = "layerDTO.name", target = "name")
	@Mapping(source = "layerInfoDTO.themeInspire", target = "themeInspire")
	@Mapping(source = "layerInfoDTO.latLonBoundsImage", target = "latLonBoundsImage")
	@Mapping(source = "layerInfoDTO.protocols", target = "protocols")
	@Mapping(source = "layerInfoDTO.description", target = "description")
	@Mapping(target = "alias",
		expression = "java(layerInfoDTO.getAlias() != null ? "
				+ "layerInfoDTO.getAlias() : (layerDTO.getTitle() != null ? layerDTO.getTitle() : layerDTO.getName()))")
	@Mapping(source = "layerInfoDTO.atlas", target = "atlas", defaultValue = "false")
	@Mapping(source = "layerInfoDTO.refresh", target = "refresh", defaultValue = "0")
	@Mapping(source = "layerDTO.urlSource", target = "urlSource")
	@Mapping(source = "layerInfoDTO.parent", target = "parent")
	LayerDTO map(LayerInfoDTO layerInfoDTO, LayerDTO layerDTO);
	
	// @formatter:on
}
