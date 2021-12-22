package it.perigea.formazione.extractor.kafka;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import it.perigea.formazione.comune.ClinicalStatusDto;
import it.perigea.formazione.comune.SomministrationsDto;

@Service
public class KafkaService{

	@Value("${topicName}")
	private String topicName;

	@Value("${ClinicalTopicName}")
	private String ClinicalTopicName;

	@Autowired
	private KafkaTemplate<String, SomministrationsDto> kafkaTemplate;

	@Autowired
	private KafkaTemplate<String, ClinicalStatusDto> kafkaClinicalTemplate;

	//	metodo per l'invio dei messaggi al topic delle somministrazioni
	public void sendMessage(String topic, List<SomministrationsDto> somministrations) {
		somministrations.forEach(e -> kafkaTemplate.send(topicName,e));	
	}

	//	metodo per l'invio dei messaggi al topic delgli stati clinici
	public void sendClinicalMessage(String topic, List<ClinicalStatusDto> clinicalStatus) {
		clinicalStatus.forEach(e -> kafkaClinicalTemplate.send(ClinicalTopicName, e));
	}

}