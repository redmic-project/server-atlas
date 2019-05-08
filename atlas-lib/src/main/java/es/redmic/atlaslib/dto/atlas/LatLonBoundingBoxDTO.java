package es.redmic.atlaslib.dto.atlas;

import javax.validation.constraints.NotNull;

import org.geotools.data.ows.CRSEnvelope;

public class LatLonBoundingBoxDTO {

	@NotNull
	private Double minX;
	@NotNull
	private Double minY;
	@NotNull
	private Double maxX;
	@NotNull
	private Double maxY;

	public LatLonBoundingBoxDTO() {
	}

	public LatLonBoundingBoxDTO(CRSEnvelope latLonBoundingBox) {

		setMinX(latLonBoundingBox.getMinX());
		setMinY(latLonBoundingBox.getMinY());
		setMaxX(latLonBoundingBox.getMaxX());
		setMaxY(latLonBoundingBox.getMaxY());
	}

	public Double getMinX() {
		return minX;
	}

	public void setMinX(Double minX) {
		this.minX = minX;
	}

	public Double getMinY() {
		return minY;
	}

	public void setMinY(Double minY) {
		this.minY = minY;
	}

	public Double getMaxX() {
		return maxX;
	}

	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}

	public Double getMaxY() {
		return maxY;
	}

	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((maxX == null) ? 0 : maxX.hashCode());
		result = prime * result + ((maxY == null) ? 0 : maxY.hashCode());
		result = prime * result + ((minX == null) ? 0 : minX.hashCode());
		result = prime * result + ((minY == null) ? 0 : minY.hashCode());
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
		LatLonBoundingBoxDTO other = (LatLonBoundingBoxDTO) obj;
		if (maxX == null) {
			if (other.maxX != null)
				return false;
		} else if (!maxX.equals(other.maxX))
			return false;
		if (maxY == null) {
			if (other.maxY != null)
				return false;
		} else if (!maxY.equals(other.maxY))
			return false;
		if (minX == null) {
			if (other.minX != null)
				return false;
		} else if (!minX.equals(other.minX))
			return false;
		if (minY == null) {
			if (other.minY != null)
				return false;
		} else if (!minY.equals(other.minY))
			return false;
		return true;
	}
}
