package es.redmic.atlasview.model.atlas;

import es.redmic.models.es.common.model.BaseAbstractStringES;

public class Protocols extends BaseAbstractStringES {

	private String type;

	private String url;

	public Protocols() {
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
}
