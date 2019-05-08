package es.redmic.atlaslib.dto.atlas;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaDefault;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaUniqueItemsByRequiredProperties;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaUrl;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.brokerlib.deserializer.CustomRelationDeserializer;

public abstract class LayerCompactDTO extends LayerBaseDTO {

	public LayerCompactDTO() {
		super();
		this.protocol = new ArrayList<ProtocolDTO>();
	}

	@JsonDeserialize(using = CustomRelationDeserializer.class)
	@JsonSchemaUrl(value = "controller.mapping.THEME_INSPIRE")
	private ThemeInspireDTO themeInspire;

	@Valid
	private LatLonBoundingBoxDTO latLonBoundsImage;

	@JsonSchemaUniqueItemsByRequiredProperties
	@Valid
	@NotNull
	@Size(min = 1)
	private List<ProtocolDTO> protocol;

	@Size(min = 0, max = 1500)
	private String description;

	@Size(min = 0, max = 250)
	private String alias;

	@NotNull
	@JsonSchemaDefault(value = "false")
	private Boolean atlas = false;

	@NotNull
	@JsonSchemaDefault(value = "0")
	private Integer refresh = 0;

	public ThemeInspireDTO getThemeInspire() {
		return themeInspire;
	}

	public void setThemeInspire(ThemeInspireDTO themeInspire) {
		this.themeInspire = themeInspire;
	}

	public LatLonBoundingBoxDTO getLatLonBoundsImage() {
		return latLonBoundsImage;
	}

	public void setLatLonBoundsImage(LatLonBoundingBoxDTO latLonBoundsImage) {
		this.latLonBoundsImage = latLonBoundsImage;
	}

	public List<ProtocolDTO> getProtocol() {
		return protocol;
	}

	public void setProtocol(List<ProtocolDTO> protocol) {
		this.protocol = protocol;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Boolean getAtlas() {
		return atlas;
	}

	public void setAtlas(Boolean atlas) {
		this.atlas = atlas;
	}

	public Integer getRefresh() {
		return refresh;
	}

	public void setRefresh(Integer refresh) {
		this.refresh = refresh;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + ((atlas == null) ? 0 : atlas.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((latLonBoundsImage == null) ? 0 : latLonBoundsImage.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((refresh == null) ? 0 : refresh.hashCode());
		result = prime * result + ((themeInspire == null) ? 0 : themeInspire.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayerCompactDTO other = (LayerCompactDTO) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (atlas == null) {
			if (other.atlas != null)
				return false;
		} else if (!atlas.equals(other.atlas))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (latLonBoundsImage == null) {
			if (other.latLonBoundsImage != null)
				return false;
		} else if (!latLonBoundsImage.equals(other.latLonBoundsImage))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (refresh == null) {
			if (other.refresh != null)
				return false;
		} else if (!refresh.equals(other.refresh))
			return false;
		if (themeInspire == null) {
			if (other.themeInspire != null)
				return false;
		} else if (!themeInspire.equals(other.themeInspire))
			return false;
		return true;
	}
}
