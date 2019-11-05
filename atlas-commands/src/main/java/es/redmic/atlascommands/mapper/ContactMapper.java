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
import org.opengis.metadata.citation.Address;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Telephone;

import es.redmic.atlaslib.dto.layer.ContactDTO;

@Mapper
public interface ContactMapper {

	default ContactDTO map(ResponsibleParty contact) {
		ContactDTO contactDTO = new ContactDTO();

		contactDTO.setName(contact.getIndividualName());

		if (contact.getOrganisationName() != null)
			contactDTO.setOrganization(contact.getOrganisationName().toString());

		if (contact.getContactInfo() != null) {
			Telephone phone = contact.getContactInfo().getPhone();

			if (phone != null && !phone.getVoices().isEmpty())
				contactDTO.setPhone(phone.getVoices().toArray()[0].toString());

			if (phone != null && !phone.getFacsimiles().isEmpty())
				contactDTO.setFax(phone.getFacsimiles().toArray()[0].toString());

			contactDTO.setEmail(getEmail(contact.getContactInfo().getAddress()));
			contactDTO.setAddress(getAddress(contact.getContactInfo().getAddress()));
		}

		if (contact.getPositionName() != null)
			contactDTO.setContactPosition(contact.getPositionName().toString());

		return contactDTO;
	}

	default String getEmail(Address address) {

		if (address == null || address.getElectronicMailAddresses().isEmpty())
			return null;
		return address.getElectronicMailAddresses().toArray()[0].toString();
	}

	default String getAddress(Address address) {

		if (address == null)
			return null;

		String addressAux = null;
		if (!address.getDeliveryPoints().isEmpty())
			addressAux = address.getDeliveryPoints().toArray()[0].toString() + " ";

		if (address.getCity() != null)
			addressAux += address.getCity() + " ";

		if (address.getPostalCode() != null)
			addressAux += address.getPostalCode() + " ";

		if (address.getCountry() != null)
			addressAux += address.getCountry() + " ";

		return addressAux;
	}
}
