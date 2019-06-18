package es.redmic.atlaslib.events.layer.partialupdate.themeinspire;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.atlaslib.events.layer.LayerEventTypes;
import es.redmic.atlaslib.events.themeinspire.common.ThemeInspireEvent;

public class UpdateThemeInspireInLayerEvent extends ThemeInspireEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateThemeInspireInLayerEvent\","
				+ "\"namespace\":\"es.redmic.atlaslib.events.layer.partialupdate.themeinspire\",\"fields\":["
			+ getThemeInspireEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = LayerEventTypes.UPDATE_THEMEINSPIRE;

	public UpdateThemeInspireInLayerEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public UpdateThemeInspireInLayerEvent(ThemeInspireDTO themeInspire) {
		super(type);
		setSessionId(UUID.randomUUID().toString());
		this.setThemeInspire(themeInspire);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}

}
