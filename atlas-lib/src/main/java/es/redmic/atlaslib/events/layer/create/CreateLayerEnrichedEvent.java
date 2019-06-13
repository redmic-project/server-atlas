package es.redmic.atlaslib.events.layer.create;

import org.apache.avro.Schema;

import es.redmic.atlaslib.dto.layer.LayerDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.layer.common.LayerEvent;

public class CreateLayerEnrichedEvent extends LayerEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateLayerEnrichedEvent\","
				+ "\"namespace\":\"es.redmic.atlaslib.events.layer.create\",\"fields\":["
			+ getLayerEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = LayerEventTypes.CREATE_ENRICHED;

	public CreateLayerEnrichedEvent() {
		super(type);
	}

	public CreateLayerEnrichedEvent(LayerDTO layer) {
		super(type);
		setLayer(layer);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
