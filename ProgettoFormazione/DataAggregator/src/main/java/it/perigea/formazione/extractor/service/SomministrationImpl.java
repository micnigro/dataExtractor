package it.perigea.formazione.extractor.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.GenericType;
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
import it.perigea.formazione.comune.SomministrationsDto;
import it.perigea.formazione.extractor.controller.SomministrationController;
import it.perigea.formazione.extractor.entity.AbbreviationsEntity;
import it.perigea.formazione.extractor.entity.ExecutionEntity;
import it.perigea.formazione.extractor.entity.ProcessEntity;
import it.perigea.formazione.extractor.kafka.KafkaService;
import it.perigea.formazione.extractor.manager.SomministrationsManager;
import it.perigea.formazione.extractor.model.AbbreviationsDto;
import it.perigea.formazione.extractor.model.ExecutionDto;
import it.perigea.formazione.extractor.model.ProcessDto;
import it.perigea.formazione.extractor.repository.AbbreviationsRepository;
import it.perigea.formazione.extractor.repository.ExecutionRepository;
import it.perigea.formazione.extractor.repository.ProcessRepository;


@Service
@Primary
public class SomministrationImpl implements ServiceInterface<SomministrationsDto> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SomministrationController.class);

	@Value("${lombardiaJson}")
	private String lombardiaJson;

	@Value("${province}")
	private String province;

	@Value("${topicName}")
	private String topicName;

	@Autowired
	private KafkaService kafkaService;

	@Autowired
	public ProcessRepository processRepository;

	@Autowired
	public ExecutionRepository executionRepository;

	@Autowired
	public AbbreviationsRepository abbRepository;

	public SomministrationsManager manager = new SomministrationsManager();

	//metodo per scaricare i dati relativi alle somministrazioni in Lombardia
	@Override
	public String dataDownload() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(lombardiaJson);
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		String jsonString = response.readEntity(String.class);
		System.out.println(jsonString);
		return jsonString;
	}

	//metodo per controllare se il Json delle province è gia stato scaricato e se è gia presente nel database locale
	public List<AbbreviationsDto> checkProvince() throws Exception {
		List<AbbreviationsEntity> list = abbRepository.findAll();
		if (!(list.isEmpty())) {
			return manager.getAbbreviationsDtoListFromDB(list);
		} else {
			List<AbbreviationsDto> abbDTOList = provinceClient();
			return abbDTOList;
		}
	}

	//metodo per ottenere il dato Json manipolato
	@Override
	public List<SomministrationsDto> modifiedData() throws Exception {
		LOGGER.trace("Entering method checkSomministration");
		String jsonString = dataDownload();
		List<AbbreviationsDto> province = checkProvince();
		return manager.getModifiedDataList(jsonString, province);
	}

	//metodo per la conversione dei dati Json in java
	@Override
	public List<SomministrationsDto> fromJsonToJava() throws Exception {
		List<SomministrationsDto> somministrations = modifiedData();
		return somministrations;
	}

	//metodo per scaricare i dati Json sulle province 
	public List<AbbreviationsDto> provinceClient() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(province);
		List<AbbreviationsDto> list = target.request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<AbbreviationsDto>>() {
				});
		System.out.println(list);
		List<AbbreviationsEntity> abbEntityList = saveAbbreviationsListInDB(list);
		return list;
	}

	//metodo per ottenere tutte le esecuzioni presenti su DB 
	public List<ExecutionDto> getAllExecutions() {
		List<ExecutionEntity> listExecutionEntity = executionRepository.findAll();
		return manager.getExecutionDtoListFromDB(listExecutionEntity);
	}

	//metodo per ottenere tutte le esecuzioni presenti su DB in base al tipo passato come input
	public List<ExecutionDto> getExecutionsByType(String type) {
		type = "Somministrations";
		List<ExecutionEntity> list = executionRepository.findByType(type);
		return manager.getExecutionDtoListFromDB(list);
	}

	//metodo per ottenere tutti i processi sulla base di una data passata come input
	public List<ProcessDto> getProcessByDate(String data) throws ParseException {
		Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(data + " 00:00:00.000");
		Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(data + " 23:59:59.999");
		List<ProcessEntity> list = processRepository.findAllByDateTimeLessThanEqualAndDateTimeGreaterThanEqual(endDate, startDate);
		return manager.getProcessDtoListFromDB(list);
	}

	//metodo per ottenre tutti i processi presenti su DB 
	public List<ProcessDto> getAllProcess() {
		List<ProcessEntity> listProcessEntity = processRepository.findAll();
		return manager.getProcessDtoListFromDB(listProcessEntity);
	}

	//metodo per ottenre tutti i processi sulla base di un determinato tipo passato come input
	public List<ProcessDto> getProcessByType(String type) {
		type = "Somministrations";
		List<ProcessEntity> list = processRepository.findByType(type);
		return manager.getProcessDtoListFromDB(list);
	}

	//metodo che riempie processi ed esecuzioni con le informazioni necessarie
	@Override
	public List<SomministrationsDto> runProcess() throws Exception {
		ProcessDto processSommDto = new ProcessDto();
		ExecutionDto exeSommDto = new ExecutionDto();
		processSommDto.setType("Somministrations");
		Date date = new Date();
		processSommDto.setDateTime(date);
		long startTime = System.currentTimeMillis();
		List<SomministrationsDto> resultRequest = fromJsonToJava();
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		exeSommDto.setTime(elapsedTime);
		exeSommDto.setType("Somministrations");
		exeSommDto.setResult("Success");
		processSommDto.setStatus("Ended");
		saveProcessDataInDB(processSommDto);
		saveExecutionDataInDB(exeSommDto);
		System.out.println("Status ending");
		kafkaService.sendMessage(topicName, resultRequest);
		return resultRequest;
	}

	//metodo per salvare le esecuzioni su DB 
	public ExecutionEntity saveExecutionDataInDB(ExecutionDto exeDto) {
		ExecutionEntity exeEntity = manager.getExecutionEntity(exeDto);
		executionRepository.save(exeEntity);
		return exeEntity;
	}

	//metodo per salvare i processi su DB 
	public ProcessEntity saveProcessDataInDB(ProcessDto procDto) {
		ProcessEntity procEntity = manager.getProcessEntity(procDto);
		processRepository.save(procEntity);
		return procEntity;
	}

	//metodo per salvare le province su DB 
	public List<AbbreviationsEntity> saveAbbreviationsListInDB(List<AbbreviationsDto> list) {
		List<AbbreviationsEntity> abbEntityList = manager.getAbbreviationsListForDB(list);
		abbRepository.saveAll(abbEntityList);
		return abbEntityList;
	}
}
