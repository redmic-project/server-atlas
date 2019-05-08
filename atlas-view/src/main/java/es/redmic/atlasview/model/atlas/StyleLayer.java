package es.redmic.atlasview.model.atlas;

public class StyleLayer {

	private String name;
	private String title;
	private String abstractStyle;
	private String format;
	private String url;

	public StyleLayer() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getabstractStyle() {
		return abstractStyle;
	}

	public void setabstractStyle(String abstractStyle) {
		this.abstractStyle = abstractStyle;
	}

	@Override
	public String toString() {
		return "Name: " + getName() + ", Title: " + getTitle() + ", Abstract: " + getabstractStyle() + ", URL: "
				+ getUrl();
	}
}
