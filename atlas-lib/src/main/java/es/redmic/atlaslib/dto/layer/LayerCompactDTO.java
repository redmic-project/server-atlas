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
		this.protocols = new ArrayList<>();
		this.activities = new ArrayList<>();
	}

	@JsonSchemaUniqueItemsByRequiredProperties
	@Valid
	private List<LayerActivityDTO> activities;

	@JsonDeserialize(using = CustomRelationDeserializer.class)
	@JsonSchemaUrl(value = "controller.mapping.THEME_INSPIRE")
	private ThemeInspireDTO themeInspire;

	@Valid
	private LatLonBoundingBoxDTO latLonBoundsImage;

	@JsonSchemaUniqueItemsByRequiredProperties
	@Valid
	@NotNull
	@Size(min = 1)
	private List<ProtocolDTO> protocols;

	@JsonSchemaUniqueItemsByRequiredProperties
	@Valid
	private List<DownloadDTO> downloads;

	@Valid
	private TimeDefinitionDTO timeDefinition;

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

	@NotNull
	private String urlSource;

	private String styles;

	public List<LayerActivityDTO> getActivities() {
		return this.activities;
	}

	public void setActivities(List<LayerActivityDTO> activities) {
		this.activities = activities;
	}

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

	public List<ProtocolDTO> getProtocols() {
		return protocols;
	}

	public void setProtocols(List<ProtocolDTO> protocols) {
		this.protocols = protocols;
	}

	public List<DownloadDTO> getDownloads() {
		return this.downloads;
	}

	public void setDownloads(List<DownloadDTO> downloads) {
		this.downloads = downloads;
	}

	public TimeDefinitionDTO getTimeDefinition() {
		return this.timeDefinition;
	}

	public void setTimeDefinition(TimeDefinitionDTO timeDefinition) {
		this.timeDefinition = timeDefinition;
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

	public String getUrlSource() {
		return urlSource;
	}

	public void setUrlSource(String urlSource) {
		this.urlSource = urlSource;
	}

	public String getStyles() {
		return this.styles;
	}

	public void setStyles(String styles) {
		this.styles = styles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + ((atlas == null) ? 0 : atlas.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((latLonBoundsImage == null) ? 0 : latLonBoundsImage.hashCode());
		result = prime * result + ((protocols == null) ? 0 : protocols.hashCode());
		result = prime * result + ((downloads == null) ? 0 : downloads.hashCode());
		result = prime * result + ((timeDefinition == null) ? 0 : timeDefinition.hashCode());
		result = prime * result + ((refresh == null) ? 0 : refresh.hashCode());
		result = prime * result + ((urlSource == null) ? 0 : urlSource.hashCode());
		result = prime * result + ((styles == null) ? 0 : styles.hashCode());
		result = prime * result + ((themeInspire == null) ? 0 : themeInspire.hashCode());
		result = prime * result + ((activities == null) ? 0 : activities.hashCode());
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
		if (protocols == null) {
			if (other.protocols != null)
				return false;
		} else if (!protocols.equals(other.protocols))
			return false;
		if (downloads == null) {
			if (other.downloads != null)
				return false;
		} else if (!downloads.equals(other.downloads))
			return false;
		if (timeDefinition == null) {
			if (other.timeDefinition != null)
				return false;
		} else if (!timeDefinition.equals(other.timeDefinition))
			return false;
		if (refresh == null) {
			if (other.refresh != null)
				return false;
		} else if (!refresh.equals(other.refresh))
			return false;
		if (urlSource == null) {
			if (other.urlSource != null)
				return false;
		} else if (!urlSource.equals(other.urlSource))
			return false;
		if (styles == null) {
			if (other.styles != null)
				return false;
		} else if (!styles.equals(other.styles))
			return false;
		if (themeInspire == null) {
			if (other.themeInspire != null)
				return false;
		} else if (!themeInspire.equals(other.themeInspire))
			return false;
		if (activities == null) {
			if (other.activities != null)
				return false;
		} else if (!activities.equals(other.activities))
			return false;
		return true;
	}
}
