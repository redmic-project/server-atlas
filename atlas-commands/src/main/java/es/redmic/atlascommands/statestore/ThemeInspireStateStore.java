package es.redmic.atlascommands.statestore;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.statestore.StateStore;

public class ThemeInspireStateStore extends StateStore {

	public ThemeInspireStateStore(StreamConfig config, AlertService alertService) {
		super(config, alertService);
		init();
	}

	public Event getThemeInspire(String id) {
		return this.store.get(id);
	}
}
