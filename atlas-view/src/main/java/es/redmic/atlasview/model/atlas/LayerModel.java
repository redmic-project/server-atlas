package es.redmic.atlasview.model.atlas;

import java.util.List;

import org.locationtech.jts.geom.Geometry;

import es.redmic.models.es.administrative.model.ActivityReferences;
import es.redmic.models.es.atlas.Contact;
import es.redmic.models.es.atlas.LatLonBoundingBox;
import es.redmic.models.es.atlas.StyleLayer;

public class LayerModel extends LayerModelCompact {

	private String title;
	private String abstractLayer;
	private List<String> keyword;
	private List<String> srs;
	private StyleLayer styleLayer;
	private Contact contact;
	private List<ActivityReferences> activities;
	private String urlSource;
	private Boolean queryable;
	private List<String> formats;
	private String image;
	private LatLonBoundingBox latLonBoundsImage;
	private List<Protocols> protocols;

	private Geometry geometry;

	public LayerModel() {
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getKeyword() {
		return keyword;
	}

	public void setKeyword(List<String> keyword) {
		this.keyword = keyword;
	}

	public List<String> getSrs() {
		return srs;
	}

	public void setSrs(List<String> srs) {
		this.srs = srs;
	}

	public String getAbstractLayer() {
		return abstractLayer;
	}

	public void setAbstractLayer(String abstractLayer) {
		this.abstractLayer = abstractLayer;
	}

	public StyleLayer getStyleLayer() {
		return styleLayer;
	}

	public void setStyleLayer(StyleLayer styleLayer) {
		this.styleLayer = styleLayer;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public List<ActivityReferences> getActivities() {
		return activities;
	}

	public void setActivities(List<ActivityReferences> activities) {
		this.activities = activities;
	}

	public String getUrlSource() {
		return urlSource;
	}

	public void setUrlSource(String urlSource) {
		this.urlSource = urlSource;
	}

	public Boolean getQueryable() {
		return queryable;
	}

	public void setQueryable(Boolean queryable) {
		this.queryable = queryable;
	}

	public List<String> getFormats() {
		return formats;
	}

	public void setFormats(List<String> formats) {
		this.formats = formats;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public LatLonBoundingBox getLatLonBoundsImage() {
		return latLonBoundsImage;
	}

	public void setLatLonBoundsImage(LatLonBoundingBox latLonBoundsImage) {
		this.latLonBoundsImage = latLonBoundsImage;
	}

	public List<Protocols> getProtocols() {
		return protocols;
	}

	public void setProtocols(List<Protocols> protocols) {
		this.protocols = protocols;
	}
}
