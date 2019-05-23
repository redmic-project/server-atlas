package es.redmic.test.atlascommands.integration.layer;

import java.util.ArrayList;

/*-
 * #%L
 * Atlas-management
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.joda.time.DateTime;
import org.locationtech.jts.geom.Coordinate;

import es.redmic.atlaslib.dto.layer.ActivityDTO;
import es.redmic.atlaslib.dto.layer.ContactDTO;
import es.redmic.atlaslib.dto.layer.LatLonBoundingBoxDTO;
import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.dto.layer.ProtocolDTO;
import es.redmic.atlaslib.dto.layer.StyleLayerDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.create.CreateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerEvent;
import es.redmic.atlaslib.events.layer.create.CreateLayerFailedEvent;
import es.redmic.atlaslib.events.layer.create.LayerCreatedEvent;
import es.redmic.atlaslib.events.layer.delete.CheckDeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerEvent;
import es.redmic.atlaslib.events.layer.delete.DeleteLayerFailedEvent;
import es.redmic.atlaslib.events.layer.delete.LayerDeletedEvent;
import es.redmic.atlaslib.events.layer.update.LayerUpdatedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerConfirmedEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerEvent;
import es.redmic.atlaslib.events.layer.update.UpdateLayerFailedEvent;
import es.redmic.exception.common.ExceptionType;
import es.redmic.test.atlascommands.integration.themeinspire.ThemeInspireDataUtil;

public abstract class LayerDataUtil {

	// @formatter:off

	public final static String PREFIX = "layer-",
			USER = "REDMIC_PROCESS";

	// @formatter:on

	// Create

	public static CreateLayerEvent getCreateEvent(String code) {

		CreateLayerEvent event = new CreateLayerEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(LayerEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setSessionId("sessionIdA");
		event.setLayer(getLayer(code));

		return event;
	}

	public static CreateLayerConfirmedEvent getCreateLayerConfirmedEvent(String code) {

		return new CreateLayerConfirmedEvent().buildFrom(getCreateEvent(code));
	}

	public static LayerCreatedEvent getLayerCreatedEvent(String code) {

		LayerCreatedEvent event = new LayerCreatedEvent().buildFrom(getCreateEvent(code));

		event.setLayer(getLayer(code));

		return event;
	}

	public static CreateLayerFailedEvent getCreateLayerFailedEvent(String code) {

		CreateLayerFailedEvent event = new CreateLayerFailedEvent().buildFrom(getCreateEvent(code));

		event.setExceptionType(ExceptionType.ITEM_ALREADY_EXIST_EXCEPTION.name());

		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		event.setArguments(arguments);

		return event;
	}

	// Update

	public static UpdateLayerEvent getUpdateEvent(String code) {

		UpdateLayerEvent event = new UpdateLayerEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(LayerEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setSessionId("sessionIdB");
		event.setLayer(getLayer(code));
		return event;
	}

	public static UpdateLayerConfirmedEvent getUpdateLayerConfirmedEvent(String code) {

		return new UpdateLayerConfirmedEvent().buildFrom(getUpdateEvent(code));
	}

	public static LayerUpdatedEvent getLayerUpdatedEvent(String code) {

		LayerUpdatedEvent event = new LayerUpdatedEvent().buildFrom(getUpdateEvent(code));

		event.setLayer(getLayer(code));

		return event;
	}

	public static UpdateLayerFailedEvent getUpdateLayerFailedEvent(String code) {

		UpdateLayerFailedEvent event = new UpdateLayerFailedEvent().buildFrom(getUpdateEvent(code));

		event.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());

		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("a", "b");
		event.setArguments(arguments);

		return event;
	}

	// Delete

	public static DeleteLayerEvent getDeleteEvent(String code) {

		DeleteLayerEvent event = new DeleteLayerEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(LayerEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		event.setSessionId("sessionIdC");
		return event;
	}

	public static CheckDeleteLayerEvent getCheckDeleteLayerEvent(String code) {

		return new CheckDeleteLayerEvent().buildFrom(getDeleteEvent(code));
	}

	public static DeleteLayerConfirmedEvent getDeleteLayerConfirmedEvent(String code) {

		return new DeleteLayerConfirmedEvent().buildFrom(getDeleteEvent(code));
	}

	public static LayerDeletedEvent getLayerDeletedEvent(String code) {

		return new LayerDeletedEvent().buildFrom(getDeleteEvent(code));
	}

	public static DeleteLayerFailedEvent getDeleteLayerFailedEvent(String code) {

		DeleteLayerFailedEvent event = new DeleteLayerFailedEvent().buildFrom(getDeleteEvent(code));

		event.setExceptionType(ExceptionType.DELETE_ITEM_EXCEPTION.name());

		Map<String, String> arguments = new HashMap<>();
		// arguments.put("A", "B");
		event.setArguments(arguments);

		return event;
	}

	@SuppressWarnings("serial")
	public static LayerDTO getLayer(String code) {

		LayerDTO layer = new LayerDTO();

		layer.setId(PREFIX + code);
		layer.setName("Prueba");
		layer.setTitle("title");
		layer.setAlias("Prueba");
		layer.setDescription("Prueba");

		layer.setAbstractLayer("Prueba");
		layer.setImage("Prueba");

		List<String> srs = new ArrayList<>();
		srs.add("srs");
		layer.setSrs(srs);

		List<String> keywords = new ArrayList<>();
		keywords.add("keywords");
		layer.setKeywords(keywords);

		layer.setUrlSource("http://redmic.es");

		List<String> formats = new ArrayList<>();
		formats.add("WMS");
		layer.setFormats(formats);

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(-18.1745567321777, 27.6111183166504),
				new Coordinate(-18.1745567321777, 29.4221172332764),
				new Coordinate(-13.3011913299561, 29.4221172332764),
				new Coordinate(-13.3011913299561, 27.6111183166504),
				new Coordinate(-18.1745567321777, 27.6111183166504) };

		layer.setGeometry(JTSFactoryFinder.getGeometryFactory().createPolygon(coordinates));

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

		layer.setThemeInspire(ThemeInspireDataUtil.getThemeInspire("cc"));

		layer.setProtocols(getProtocols());

		layer.setLatLonBoundsImage(getLatLonBoundingBoxDTO());

		StyleLayerDTO styleLayer = new StyleLayerDTO();
		styleLayer.setName("styleLayer");
		layer.setStylesLayer(new ArrayList<StyleLayerDTO>() {
			{
				add(styleLayer);
			}
		});

		return layer;
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
