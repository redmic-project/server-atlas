package es.redmic.atlasview.model.atlas;

import es.redmic.atlasview.model.themeinspire.ThemeInspire;
import es.redmic.models.es.common.model.BaseAbstractStringES;

public class LayerModelCompact extends BaseAbstractStringES {

	private String name;

	private String description;

	private String alias;

	private Boolean atlas;

	private ThemeInspire themeInspire;

	private Integer refresh;

	public LayerModelCompact() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public ThemeInspire getThemeInspire() {
		return themeInspire;
	}

	public void setThemeInspire(ThemeInspire themeInspire) {
		this.themeInspire = themeInspire;
	}

	public Integer getRefresh() {
		return refresh;
	}

	public void setRefresh(Integer refresh) {
		this.refresh = refresh;
	}
}
