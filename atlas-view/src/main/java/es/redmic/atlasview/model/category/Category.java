package es.redmic.atlasview.model.category;

import es.redmic.models.es.common.model.BaseAbstractStringES;

public class Category extends BaseAbstractStringES {

	private String name;

	public Category() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
