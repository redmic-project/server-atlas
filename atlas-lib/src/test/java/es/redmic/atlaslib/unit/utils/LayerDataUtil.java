package es.redmic.atlaslib.unit.utils;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import es.redmic.atlaslib.dto.layer.ActivityDTO;
import es.redmic.atlaslib.dto.layer.ContactDTO;
import es.redmic.atlaslib.dto.layer.LatLonBoundingBoxDTO;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layer.ProtocolDTO;
import es.redmic.atlaslib.dto.layer.StyleLayerDTO;
import es.redmic.atlaslib.dto.layerinfo.LayerInfoDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.create.CreateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerFailedEvent;
import es.redmic.atlaslib.events.layer.create.LayerCreatedEvent;
import es.redmic.atlaslib.events.layer.delete.CheckDeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckFailedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerCheckedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerFailedEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerCancelledEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;

public abstract class LayerDataUtil {

	// @formatter:off
	public final static String PREFIX = "layer-",
			CODE = UUID.randomUUID().toString(),
			USER = "1";
	// @formatter:on

	// Create

	public static CreateLayerEvent getCreateEvent() {

		CreateLayerEvent event = new CreateLayerEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(LayerEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setLayer(getLayer());

		return event;
	}

	public static CreateLayerConfirmedEvent getCreateLayerConfirmedEvent() {

		CreateLayerConfirmedEvent event = new CreateLayerConfirmedEvent().buildFrom(getCreateEvent());
		event.setType(LayerEventTypes.CREATE_CONFIRMED);
		return event;
	}

	public static LayerCreatedEvent getLayerCreatedEvent() {

		LayerCreatedEvent event = new LayerCreatedEvent().buildFrom(getCreateEvent());
		event.setType(LayerEventTypes.CREATED);
		event.setLayer(getLayer());
		return event;
	}

	public static CreateLayerFailedEvent getCreateLayerFailedEvent() {

		CreateLayerFailedEvent event = new CreateLayerFailedEvent().buildFrom(getCreateEvent());
		event.setType(LayerEventTypes.CREATE_FAILED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	public static CreateLayerCancelledEvent getCreateLayerCancelledEvent() {

		CreateLayerCancelledEvent event = new CreateLayerCancelledEvent().buildFrom(getCreateEvent());
		event.setType(LayerEventTypes.CREATE_CANCELLED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	// Update

	public static UpdateLayerEvent getUpdateEvent() {

		UpdateLayerEvent event = new UpdateLayerEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(LayerEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setLayer(getLayer());
		return event;
	}

	public static UpdateLayerConfirmedEvent getUpdateLayerConfirmedEvent() {

		UpdateLayerConfirmedEvent event = new UpdateLayerConfirmedEvent().buildFrom(getUpdateEvent());
		event.setType(LayerEventTypes.UPDATE_CONFIRMED);
		return event;
	}

	public static LayerUpdatedEvent getLayerUpdatedEvent() {

		LayerUpdatedEvent event = new LayerUpdatedEvent().buildFrom(getUpdateEvent());
		event.setType(LayerEventTypes.UPDATED);
		event.setLayer(getLayer());
		return event;
	}

	public static UpdateLayerFailedEvent getUpdateLayerFailedEvent() {

		UpdateLayerFailedEvent event = new UpdateLayerFailedEvent().buildFrom(getUpdateEvent());
		event.setType(LayerEventTypes.UPDATE_FAILED);
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static UpdateLayerCancelledEvent getUpdateLayerCancelledEvent() {

		UpdateLayerCancelledEvent event = new UpdateLayerCancelledEvent().buildFrom(getUpdateEvent());
		event.setType(LayerEventTypes.UPDATE_FAILED);
		event.setLayer(getLayer());
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	// Delete

	public static DeleteLayerEvent getDeleteEvent() {

		DeleteLayerEvent event = new DeleteLayerEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(LayerEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		return event;
	}

	public static CheckDeleteLayerEvent getCheckDeleteLayerEvent() {

		return new CheckDeleteLayerEvent().buildFrom(getDeleteEvent());
	}

	public static DeleteLayerCheckedEvent getDeleteLayerCheckedEvent() {

		return new DeleteLayerCheckedEvent().buildFrom(getDeleteEvent());
	}

	public static DeleteLayerCheckFailedEvent getDeleteLayerCheckFailedEvent() {

		DeleteLayerCheckFailedEvent event = new DeleteLayerCheckFailedEvent().buildFrom(getDeleteEvent());
		event.setExceptionType("ItemIsReferenced");
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static DeleteLayerConfirmedEvent getDeleteLayerConfirmedEvent() {

		DeleteLayerConfirmedEvent event = new DeleteLayerConfirmedEvent().buildFrom(getDeleteEvent());
		event.setAggregateId(PREFIX + CODE);
		event.setType(LayerEventTypes.DELETE_CONFIRMED);
		event.setVersion(3);

		return event;
	}

	public static LayerDeletedEvent getLayerDeletedEvent() {

		LayerDeletedEvent event = new LayerDeletedEvent().buildFrom(getDeleteEvent());
		event.setType(LayerEventTypes.DELETED);
		return event;
	}

	public static DeleteLayerFailedEvent getDeleteLayerFailedEvent() {

		DeleteLayerFailedEvent event = new DeleteLayerFailedEvent().buildFrom(getDeleteEvent());
		event.setType(LayerEventTypes.DELETE_FAILED);
		event.setExceptionType("ItemNotFound");
		return event;
	}

	public static DeleteLayerCancelledEvent getDeleteLayerCancelledEvent() {

		DeleteLayerCancelledEvent event = new DeleteLayerCancelledEvent().buildFrom(getDeleteEvent());
		event.setType(LayerEventTypes.DELETE_CONFIRMED);
		event.setLayer(getLayer());
		event.setExceptionType("ItemNotFound");
		return event;
	}

	@SuppressWarnings("serial")
	public static LayerDTO getLayer() {

		LayerDTO layer = new LayerDTO();

		layer.setId(PREFIX + CODE);
		layer.setName("Prueba");
		layer.setTitle("title");
		layer.setAlias("Prueba");
		layer.setDescription("Prueba");

		layer.setAbstractLayer("Prueba");
		layer.setImage("Prueba");

		List<String> srs = new ArrayList<>();
		srs.add("srs");
		layer.setSrs(srs);

		List<String> keyword = new ArrayList<>();
		keyword.add("keyword");
		layer.setKeyword(keyword);

		layer.setUrlSource("http://redmic.es");

		List<String> formats = new ArrayList<>();
		formats.add("WMS");
		layer.setFormats(formats);

		GeometryFactory geometryFactory = new GeometryFactory();

		Point geometry = geometryFactory.createPoint(new Coordinate(44.56433, 37.94388));
		layer.setGeometry(geometry);

		ActivityDTO activity = new ActivityDTO();
		activity.setId("3");
		activity.setName("AIS");
		activity.setPath("r.1.2.3");

		layer.setActivities(new ArrayList<ActivityDTO>() {
			{
				add(activity);
			}
		});

		ContactDTO contact = new ContactDTO();
		contact.setName("Pepe");
		layer.setContact(contact);

		layer.setThemeInspire(ThemeInspireDataUtil.getThemeInspire());

		layer.setProtocols(getProtocols());

		layer.setLatLonBoundsImage(getLatLonBoundingBoxDTO());

		StyleLayerDTO styleLayer = new StyleLayerDTO();
		styleLayer.setName("styleLayer");
		layer.setStyleLayer(styleLayer);

		return layer;
	}

	public static LayerInfoDTO getLayerInfo() {

		LayerInfoDTO layerInfo = new LayerInfoDTO();

		layerInfo.setId(PREFIX + CODE);
		layerInfo.setName("Prueba");
		layerInfo.setAlias("Prueba");
		layerInfo.setDescription("Prueba");

		layerInfo.setParent(CategoryDataUtil.getCategory());

		layerInfo.setThemeInspire(ThemeInspireDataUtil.getThemeInspire());

		layerInfo.setProtocols(getProtocols());

		layerInfo.setLatLonBoundsImage(getLatLonBoundingBoxDTO());

		return layerInfo;
	}

	@SuppressWarnings("serial")
	public static List<ProtocolDTO> getProtocols() {

		ProtocolDTO protocol = new ProtocolDTO();
		protocol.setType("WMS");
		protocol.setUrl("https://atlas.redmic.es/geoserver/tn/wms");

		return new ArrayList<ProtocolDTO>() {
			{
				add(protocol);
			}
		};
	}

	public static LatLonBoundingBoxDTO getLatLonBoundingBoxDTO() {

		LatLonBoundingBoxDTO latLonBoundingBoxDTO = new LatLonBoundingBoxDTO();

		latLonBoundingBoxDTO.setMaxX(2.0);
		latLonBoundingBoxDTO.setMaxY(4.0);
		latLonBoundingBoxDTO.setMinX(1.0);
		latLonBoundingBoxDTO.setMinY(1.0);

		return latLonBoundingBoxDTO;
	}
}
