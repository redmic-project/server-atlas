package es.redmic.atlaslib.dto.atlas;

import org.opengis.metadata.citation.Address;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Telephone;

import com.fasterxml.jackson.annotation.JsonSetter;

public class ContactDTO {

	private String name;
	private String email;
	private String phone;
	private String fax;
	private String address;
	private String organization;
	private String contactPosition;

	public ContactDTO() {
	}

	public ContactDTO(ResponsibleParty contactInfo) {

		setName(contactInfo.getIndividualName());

		if (contactInfo.getOrganisationName() != null)
			setOrganization(contactInfo.getOrganisationName().toString());

		if (contactInfo.getContactInfo() != null) {
			setPhone(contactInfo.getContactInfo().getPhone());
			setFax(contactInfo.getContactInfo().getPhone());
			setEmail(contactInfo.getContactInfo().getAddress());
			setAddress(contactInfo.getContactInfo().getAddress());
		}

		if (contactInfo.getPositionName() != null)
			setContactPosition(contactInfo.getPositionName().toString());
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

	@JsonSetter("email")
	public void setEmail(String email) {
		this.email = email;
	}

	public void setEmail(Address address) {
		if (address != null && !address.getElectronicMailAddresses().isEmpty())
			this.email = address.getElectronicMailAddresses().toArray()[0].toString();
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

	@JsonSetter("address")
	public void setAddress(String address) {
		this.address = address;
	}

	public void setAddress(Address address) {
		if (address != null) {
			String addressAux = null;
			if (!address.getDeliveryPoints().isEmpty())
				addressAux = address.getDeliveryPoints().toArray()[0].toString() + " ";

			if (address.getCity() != null)
				addressAux += address.getCity() + " ";

			if (address.getPostalCode() != null)
				addressAux += address.getPostalCode() + " ";

			if (address.getCountry() != null)
				addressAux += address.getCountry() + " ";

			if (addressAux != null)
				this.address = addressAux;
		}
	}

	public String getPhone() {
		return phone;
	}

	@JsonSetter("phone")
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPhone(Telephone phone) {
		if (phone != null && !phone.getVoices().isEmpty())
			this.phone = phone.getVoices().toArray()[0].toString();
	}

	public String getFax() {
		return fax;
	}

	@JsonSetter("fax")
	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setFax(Telephone fax) {
		if (phone != null && !fax.getFacsimiles().isEmpty())
			this.fax = fax.getFacsimiles().toArray()[0].toString();
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
}
