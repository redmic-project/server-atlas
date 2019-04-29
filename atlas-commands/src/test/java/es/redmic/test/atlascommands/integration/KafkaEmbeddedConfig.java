package es.redmic.test.atlascommands.integration;

public class KafkaEmbeddedConfig {

	// number of brokers.
	public final static Integer NUM_BROKERS = 3;
	// partitions per topic.
	public final static Integer PARTITIONS_PER_TOPIC = 3;

	// @formatter:off
 
	public final static String[] TOPICS_NAME = new String[] { 
		"theme-inspire",
		"atlas",
		"theme-inspire-updated",
		"atlas-updated",
		"theme-inspire-snapshot",
		"atlas-snapshot",
		"atlas-agg-by-theme-inspire"
	};
	
	// @formatter:on
}
