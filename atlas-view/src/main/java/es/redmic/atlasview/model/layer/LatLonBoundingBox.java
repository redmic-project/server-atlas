package es.redmic.atlasview.model.layer;

/*-
 * #%L
 * Atlas-query-endpoint
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

import javax.validation.constraints.NotNull;

public class LatLonBoundingBox {

	@NotNull
	private Double minX;
	@NotNull
	private Double minY;
	@NotNull
	private Double maxX;
	@NotNull
	private Double maxY;

	public LatLonBoundingBox() {
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
	public boolean equals(Object obj) {

		if (!(obj instanceof LatLonBoundingBox))
			return false;

		LatLonBoundingBox b = getClass().cast(obj);
		return this.minX.equals(b.getMinX()) && this.minY.equals(b.getMinY()) && this.maxX.equals(b.getMaxX())
				&& this.maxY.equals(b.getMaxY());
	}
}
