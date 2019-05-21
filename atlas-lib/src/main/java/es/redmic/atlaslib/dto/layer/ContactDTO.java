package es.redmic.atlaslib.dto.layer;

/*-
 * #%L
 * Atlas-lib
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

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ContactDTO extends org.apache.avro.specific.SpecificRecordBase
		implements org.apache.avro.specific.SpecificRecord {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"ContactDTO\",\"namespace\":\"es.redmic.atlaslib.dto.layer\",\"fields\":["
				+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"email\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"phone\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"fax\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"address\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"organization\",\"type\":[\"string\", \"null\"]},"
				+ "{\"name\":\"contactPosition\",\"type\":[\"string\", \"null\"]}]}");
	// @formatter:on

	private String name;
	private String email;
	private String phone;
	private String fax;
	private String address;
	private String organization;
	private String contactPosition;

	public ContactDTO() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getContactPosition() {
		return contactPosition;
	}

	public void setContactPosition(String contactPosition) {
		this.contactPosition = contactPosition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((contactPosition == null) ? 0 : contactPosition.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fax == null) ? 0 : fax.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactDTO other = (ContactDTO) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (contactPosition == null) {
			if (other.contactPosition != null)
				return false;
		} else if (!contactPosition.equals(other.contactPosition))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fax == null) {
			if (other.fax != null)
				return false;
		} else if (!fax.equals(other.fax))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "Name: " + getName() + "\nEmail: " + getEmail() + "\nOrganization: " + getOrganization() + "\nPhone: "
				+ getPhone() + "\nFax: " + getFax() + "\nAddress: " + getAddress() + "\nContact position: "
				+ getContactPosition();
	}

	@JsonIgnore
	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}

	@JsonIgnore
	@Override
	public java.lang.Object get(int field$) {
		switch (field$) {
		case 0:
			return name;
		case 1:
			return email;
		case 2:
			return phone;
		case 3:
			return fax;
		case 4:
			return address;
		case 5:
			return organization;
		case 6:
			return contactPosition;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
		case 0:
			name = value$ != null ? value$.toString() : null;
			break;
		case 1:
			email = value$ != null ? value$.toString() : null;
			break;
		case 2:
			phone = value$ != null ? value$.toString() : null;
			break;
		case 3:
			fax = value$ != null ? value$.toString() : null;
			break;
		case 4:
			address = value$ != null ? value$.toString() : null;
			break;
		case 5:
			organization = value$ != null ? value$.toString() : null;
			break;
		case 6:
			contactPosition = value$ != null ? value$.toString() : null;
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
