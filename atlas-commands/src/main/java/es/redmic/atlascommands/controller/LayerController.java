package es.redmic.atlascommands.controller;

import javax.validation.Valid;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.redmic.atlascommands.service.LayerService;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layerinfo.LayerInfoDTO;
import es.redmic.atlaslib.dto.refresh.RefreshRequestDTO;
import es.redmic.commandslib.controller.CommandController;
import es.redmic.models.es.common.dto.BodyItemDTO;
import es.redmic.models.es.common.dto.SuperDTO;

@Controller
@RequestMapping(value = "${controller.mapping.LAYER}")
public class LayerController extends CommandController<LayerInfoDTO> {

	LayerService service;

	@Autowired
	public LayerController(LayerService service) {
		super(service);
		this.service = service;
	}

	@PutMapping(value = "/refresh/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public SuperDTO refresh(@Valid @RequestBody RefreshRequestDTO dto, BindingResult errorDto,
			@PathVariable("id") String id) {

		return new BodyItemDTO<LayerDTO>(service.refresh(id, dto));
	}
}
