package es.redmic.atlaslib.dto.atlas;

import javax.validation.constraints.NotNull;

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaFormat;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaNotNull;

import es.redmic.models.es.common.constraintvalidate.url.ValidateUrl;

@JsonSchemaNotNull
public class ProtocolDTO {

	@NotNull
	private String type;

	@JsonSchemaFormat("url")
	@ValidateUrl()
	@NotNull
	private String url;

	public ProtocolDTO() {
	}

	public ProtocolDTO(String type, String urlSource) {

		createProtocols(type, urlSource);
	}

	public void createProtocols(String type, String urlSource) {

		if (type == null || !type.equals("WMS"))
			return;

		setType(type);

		if (urlSource == null)
			return;

		setUrl(urlSource);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		ProtocolDTO other = (ProtocolDTO) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
