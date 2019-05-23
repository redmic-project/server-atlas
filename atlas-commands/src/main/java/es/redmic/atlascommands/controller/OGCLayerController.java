package es.redmic.atlascommands.controller;

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

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.redmic.atlascommands.service.OGCLayerService;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.exception.data.ItemAlreadyExistException;
import es.redmic.exception.databinding.DTONotValidException;
import es.redmic.models.es.common.dto.BodyItemDTO;
import es.redmic.models.es.common.dto.SuperDTO;
import es.redmic.models.es.common.dto.UrlDTO;

@Controller
@RequestMapping(value = "${controller.mapping.DISCOVER_LAYERS}")
public class OGCLayerController {

	private OGCLayerService service;

	@Autowired
	public OGCLayerController(OGCLayerService service) {
		this.service = service;
	}

	@PostMapping(value = "/wms", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public SuperDTO discoverWMSLayers(@Valid @RequestBody UrlDTO workSpace, BindingResult errorDto) {

		if (errorDto.hasErrors())
			throw new DTONotValidException(errorDto);

		List<LayerDTO> result = service.discoverWMSLayers(workSpace.getUrl());

		if (result == null)
			throw new ItemAlreadyExistException();

		return new BodyItemDTO<List<LayerDTO>>(result);
	}
}
