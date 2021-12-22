package it.perigea.formazione.extractor.configuraiton;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	@Value("${topicName}")
	private String topicName;

	@Value("${ClinicalTopicName}")
	private String ClinicalTopicName;

	@Bean
	public KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic myTopic() {
		return TopicBuilder.name(topicName)
				.partitions(1)
				.replicas(1)
				.config(TopicConfig.RETENTION_MS_CONFIG, "43200000")
				.build();
	}

	@Bean
	public NewTopic mySecondTopic() {
		return TopicBuilder.name(ClinicalTopicName)
				.partitions(1)
				.replicas(1)
				.config(TopicConfig.RETENTION_MS_CONFIG, "43200000")
				.build();
	}
}
