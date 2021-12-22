package it.perigea.formazione.extractor.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import it.perigea.formazione.comune.ClinicalStatusDto;
import it.perigea.formazione.extractor.controller.SomministrationController;
import it.perigea.formazione.extractor.entity.ExecutionEntity;
import it.perigea.formazione.extractor.entity.ProcessEntity;
import it.perigea.formazione.extractor.kafka.KafkaService;
import it.perigea.formazione.extractor.manager.ClinicalStatusManager;
import it.perigea.formazione.extractor.model.ExecutionDto;
import it.perigea.formazione.extractor.model.ProcessDto;
import it.perigea.formazione.extractor.repository.AbbreviationsRepository;
import it.perigea.formazione.extractor.repository.ExecutionRepository;
import it.perigea.formazione.extractor.repository.ProcessRepository;

@Service
@Primary
public class ClinicalStatusImpl implements ServiceInterface<ClinicalStatusDto> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SomministrationController.class);

	@Value("${statiCliniciJson}")
	private String statiCliniciJson;

	@Value("${ClinicalTopicName}")
	private String ClinicalTopicName;

	@Autowired
	public ProcessRepository processRepository;

	@Autowired
	public ExecutionRepository executionRepository;

	@Autowired
	public AbbreviationsRepository abbRepository;

	@Autowired
	private KafkaService kafkaService;

	public ClinicalStatusManager manager = new ClinicalStatusManager();

	//metodo per scaricare i dati relativi agli stati clinici in Lombardia
	@Override
	public String dataDownload() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(statiCliniciJson);
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		String jsonString = response.readEntity(String.class);
		System.out.println(jsonString);
		return jsonString;
	}

	//metodo per ottenere il dato Json manipolato
	@Override
	public List<ClinicalStatusDto> modifiedData() throws Exception {
		LOGGER.trace("Entering method checkSomministration");
		String jsonString = dataDownload();
		return manager.getModifiedClinicalStatusList(jsonString);
	}

	//metodo per la conversione dei dati Json in java
	@Override
	public List<ClinicalStatusDto> fromJsonToJava() throws Exception {
		List<ClinicalStatusDto> clinicalStatus = modifiedData();
		return clinicalStatus;
	}

	//metodo per ottenere tutte le esecuzioni presenti su DB 
	public List<ExecutionDto> getAllExecutions() {
		List<ExecutionEntity> listEsecutionClinicalEntity = executionRepository.findAll();
		return manager.getExecutionClinicalDtoListFromDB(listEsecutionClinicalEntity);
	}

	//metodo per ottenere tutte le esecuzioni presenti su DB in base al tipo passato come input
	public List<ExecutionDto> getExecutionsByType(String type) {
		type = "ClinicalStatus";
		List<ExecutionEntity> list = executionRepository.findByType(type);
		return manager.getExecutionClinicalDtoListFromDB(list);
	}

	//metodo per ottenere tutti i processi sulla base di una data passata come input
	public List<ProcessDto> getProcessByDate(String data) throws ParseException {
		Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(data + " 00:00:00.000");
		Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(data + " 23:59:59.999");
		List<ProcessEntity> list = processRepository.findAllByDateTimeLessThanEqualAndDateTimeGreaterThanEqual(endDate, startDate);
		return manager.getProcessClinicalDtoListFromDB(list);
	}

	//metodo per ottenre tutti i processi presenti su DB 
	public List<ProcessDto> getAllProcess() {
		List<ProcessEntity> listProcessClinicalEntity = processRepository.findAll();
		return manager.getProcessClinicalDtoListFromDB(listProcessClinicalEntity);
	}

	//metodo per ottenere tutti i processi in base al tipo passato come parametro
	public List<ProcessDto> getProcessByType(String type) throws ParseException {
		type = "ClinicalStatus";
		List<ProcessEntity> list = processRepository.findByType(type);
		return manager.getProcessClinicalDtoListFromDB(list);
	}

	//metodo per riempire processi ed esecuzioni con le informazioni necessarie
	@Override
	public List<ClinicalStatusDto> runProcess() throws Exception {
		ProcessDto processClinicalDto = new ProcessDto();
		ExecutionDto exeClinicalDto = new ExecutionDto();
		processClinicalDto.setType("ClinicalStatus");
		Date date = new Date();
		processClinicalDto.setDateTime(date);
		long startTime = System.currentTimeMillis();
		List<ClinicalStatusDto> resultRequest = fromJsonToJava();
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		exeClinicalDto.setTime(elapsedTime);
		exeClinicalDto.setType("ClinicalStatus");
		exeClinicalDto.setResult("Success");
		processClinicalDto.setStatus("Ended");
		saveProcessClinicalInDB(processClinicalDto);
		saveExecutionClinicalDataInDB(exeClinicalDto);
		System.out.println("Status ending");
		kafkaService.sendClinicalMessage(ClinicalTopicName, resultRequest);
		return resultRequest;
	}

	//metodo per salvare le esecuzioni su DB locale
	public ExecutionEntity saveExecutionClinicalDataInDB(ExecutionDto exeClinicalDto) {
		ExecutionEntity exeClinicalEntity = manager.getExecutionClinicalEntity(exeClinicalDto);
		executionRepository.save(exeClinicalEntity);
		return exeClinicalEntity;
	}

	//metodo per salvare i processi su DB locale
	public ProcessEntity saveProcessClinicalInDB(ProcessDto procClinicalDto) {
		ProcessEntity procClinicalEntity = manager.getProcessClinicalEntity(procClinicalDto);
		processRepository.save(procClinicalEntity);
		return procClinicalEntity;
	}

}
