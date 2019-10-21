package es.redmic.atlaslib.events.themeinspire.fail;

import org.apache.avro.Schema;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.brokerlib.avro.common.EventTypes;
import es.redmic.brokerlib.avro.fail.BaseRollbackEvent;

public class ThemeInspireRollbackEvent extends BaseRollbackEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"ThemeInspireRollbackEvent\","
				+ "\"namespace\":\"es.redmic.atlaslib.events.themeinspire.fail\",\"fields\":["
			+ "{\"name\":\"lastSnapshotItem\",\"type\":" + ThemeInspireDTO.SCHEMA$.toString() + "},"
			+ "{\"name\":\"failEventType\",\"type\": \"string\"},"
			+ getEventBaseSchema() + "]}");
	
	// @formatter:on

	static String type = EventTypes.ROLLBACK;

	private ThemeInspireDTO lastSnapshotItem;

	public ThemeInspireRollbackEvent() {
		super(type);
	}

	public ThemeInspireDTO getLastSnapshotItem() {
		return lastSnapshotItem;
	}

	public void setLastSnapshotItem(ThemeInspireDTO lastSnapshotItem) {
		this.lastSnapshotItem = lastSnapshotItem;
	}

	@Override
	public Object get(int field$) {
		switch (field$) {
		case 0:
			return getLastSnapshotItem();
		case 1:
			return getFailEventType();
		case 2:
			return getAggregateId();
		case 3:
			return getVersion();
		case 4:
			return getType();
		case 5:
			return getDate().getMillis();
		case 6:
			return getSessionId();
		case 7:
			return getUserId();
		case 8:
			return getId();
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@Override
	public void put(int field$, Object value$) {
		switch (field$) {
		case 0:
			setLastSnapshotItem((ThemeInspireDTO) value$);
			break;
		case 1:
			setFailEventType(value$.toString());
			break;
		case 2:
			setAggregateId(value$.toString());
			break;
		case 3:
			setVersion((java.lang.Integer) value$);
			break;
		case 4:
			setType(value$.toString());
			break;
		case 5:
			setDate(new DateTime(value$, DateTimeZone.UTC));
			break;
		case 6:
			setSessionId(value$.toString());
			break;
		case 7:
			setUserId(value$.toString());
			break;
		case 8:
			setId(value$.toString());
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
