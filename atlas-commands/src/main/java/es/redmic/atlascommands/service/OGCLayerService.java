package es.redmic.atlascommands.service;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import es.redmic.atlascommands.utils.Capabilities;
import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;
import es.redmic.exception.atlas.LayerNotFoundException;

@Service
public class OGCLayerService {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LayerWMSDTO> discoverWMSLayers(String urlSource) {

		HashMap<String, LayerWMSDTO> result = Capabilities.getCapabilities(urlSource);

		if (result.isEmpty())
			return new ArrayList<>();

		return new ArrayList(result.values());
	}

	public LayerWMSDTO getLayerFromWMSService(String urlSource, String name) {

		HashMap<String, LayerWMSDTO> result = Capabilities.getCapabilities(urlSource);

		if (!result.containsKey(name))
			throw new LayerNotFoundException(name, urlSource);

		return result.get(name);
	}
}
