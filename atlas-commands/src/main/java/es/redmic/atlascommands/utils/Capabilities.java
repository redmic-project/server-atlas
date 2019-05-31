package es.redmic.atlascommands.utils;

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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WebMapServer;
import org.geotools.ows.ServiceException;
import org.mapstruct.factory.Mappers;
import org.opengis.metadata.citation.ResponsibleParty;

import es.redmic.atlascommands.mapper.ContactMapper;
import es.redmic.atlascommands.mapper.LayerWMSMapper;
import es.redmic.atlaslib.dto.layer.ContactDTO;
import es.redmic.atlaslib.dto.layerwms.LayerWMSDTO;
import es.redmic.exception.custom.ResourceNotFoundException;

public abstract class Capabilities {

	public static HashMap<String, LayerWMSDTO> getCapabilities(String url) {

		URL serverURL;

		try {
			serverURL = new URL(url.split("\\?")[0]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			// TODO: excepción propia
			throw new ResourceNotFoundException();
		}

		WebMapServer wms;

		try {
			wms = new WebMapServer(serverURL);
		} catch (ServiceException | IOException e) {
			e.printStackTrace();
			// TODO: excepción propia
			throw new ResourceNotFoundException();
		}

		return getLayers(url, wms.getCapabilities());
	}

	private static HashMap<String, LayerWMSDTO> getLayers(String url, WMSCapabilities capabilities) {

		HashMap<String, LayerWMSDTO> layers = new HashMap<String, LayerWMSDTO>();

		List<Layer> layerList = capabilities.getLayerList();

		for (int i = 0; i < layerList.size(); i++) {

			if (layerList.get(i).getName() != null) {

				LayerWMSDTO layerAux = Mappers.getMapper(LayerWMSMapper.class).map(layerList.get(i), url);
				layerAux.setContact(getContact(capabilities.getService().getContactInformation()));
				layerAux.setFormats(capabilities.getRequest().getGetMap().getFormats());
				layers.put(layerAux.getName(), layerAux);
			}
		}

		return layers;
	}

	private static ContactDTO getContact(ResponsibleParty contact) {

		if (contact == null)
			return null;

		return Mappers.getMapper(ContactMapper.class).map(contact);
	}
}
