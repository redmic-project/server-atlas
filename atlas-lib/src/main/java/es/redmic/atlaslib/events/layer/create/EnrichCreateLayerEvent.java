package es.redmic.atlaslib.events.layer.create;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.common.LayerEvent;

public class EnrichCreateLayerEvent extends LayerEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"EnrichCreateLayerEvent\","
				+ "\"namespace\":\"es.redmic.atlaslib.events.layer.create\",\"fields\":["
			+ getLayerEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = LayerEventTypes.ENRICH_CREATE;

	public EnrichCreateLayerEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public EnrichCreateLayerEvent(LayerDTO layer) {
		super(type);
		setSessionId(UUID.randomUUID().toString());
		setLayer(layer);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
