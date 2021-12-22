package it.perigea.formazione.extractor.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.perigea.formazione.comune.ClinicalStatusDto;
import it.perigea.formazione.extractor.controller.SomministrationController;
import it.perigea.formazione.extractor.entity.ExecutionEntity;
import it.perigea.formazione.extractor.entity.ProcessEntity;
import it.perigea.formazione.extractor.model.ExecutionDto;
import it.perigea.formazione.extractor.model.ProcessDto;
import it.perigea.formazione.extractor.repository.ExecutionRepository;
import it.perigea.formazione.extractor.repository.ProcessRepository;

public class ClinicalStatusManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SomministrationController.class);

	@Autowired
	public ProcessRepository processRepository;

	@Autowired
	public ExecutionRepository esecutionRepository;

	//	metodo per la manipolazione del json relativo agli stati clinici
	public List<ClinicalStatusDto> getModifiedClinicalStatusList(String jsonString)
			throws Exception {
		List<ClinicalStatusDto> listClinicalDto = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(jsonString);
		ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));
		String dataString = (zonedDateTimeNow.getDayOfMonth()+"/"+zonedDateTimeNow.getMonthValue()+"/"+zonedDateTimeNow.getYear());
		jsonNode.forEach(dataRow -> {
			String extractedData;
			if(dataRow.findValue("data_inizio_sintomi")==null) {
				extractedData="/";
			}else {
				extractedData=dataRow.get("data_inizio_sintomi").asText();}
			Integer extractedTotEvent = dataRow.get("casi_totali").asInt();
			Integer extractedNoClincalSt;
			if(dataRow.findValue("nessuno_stato_clinico")==null) {
				extractedNoClincalSt = 0;
			}else {
				extractedNoClincalSt = dataRow.get("nessuno_stato_clinico").asInt();}
			Integer extractedHealed;
			if(dataRow.findValue("solo_st_guarito")==null) {
				extractedHealed = 0;
			}else {
				extractedHealed = dataRow.get("solo_st_guarito").asInt();}
			Integer extractedDeaths;
			if(dataRow.findValue("solo_st_deceduto")==null) {
				extractedDeaths=0;
			}else {
				extractedDeaths = dataRow.get("solo_st_deceduto").asInt();}
			Integer extractedNoSymp;
			if(dataRow.findValue("solo_st_asintomatico")==null) {
				extractedNoSymp =0;
			}else {
				extractedNoSymp = dataRow.get("solo_st_asintomatico").asInt();}
			Integer extractedSymp;
			if(dataRow.findValue("solo_st_lieve_pau_severo")==null) {
				extractedSymp =0;
			}else {
				extractedSymp = dataRow.get("solo_st_lieve_pau_severo").asInt();}
			Integer extractedSympHealed;
			if(dataRow.findValue("st_lieve_pau_severo_grave_g")==null) {
				extractedSympHealed =0;
			}else {
				extractedSympHealed = dataRow.get("st_lieve_pau_severo_grave_g").asInt();}
			Integer extractedSympDeaths;
			if(dataRow.findValue("st_lieve_pau_severo_grave_d")==null) {
				extractedSympDeaths =0;
			}else {
				extractedSympDeaths = dataRow.get("st_lieve_pau_severo_grave_d").asInt();}
			Integer extractedNoSympHealed;
			if(dataRow.findValue("st_asintomatico_g")==null) {
				extractedNoSympHealed =0;
			}else {
				extractedNoSympHealed = dataRow.get("st_asintomatico_g").asInt();}
			Integer extractedNoSympDeaths;
			if(dataRow.findValue("st_asintomatico_d")==null) {
				extractedNoSympDeaths =0;
			}else {
				extractedNoSympDeaths = dataRow.get("st_asintomatico_d").asInt();}
			Date dateExtractedDate = null;
			try {
				dateExtractedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(extractedData + "T00:00:00.000");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			ClinicalStatusDto clinicalDto = new ClinicalStatusDto();
			clinicalDto.setDataInizioSintomi(dateExtractedDate);
			clinicalDto.setCasiTotali(extractedTotEvent);
			clinicalDto.setNessunoStatoClinico(extractedNoClincalSt);
			clinicalDto.setGuariti(extractedHealed);
			clinicalDto.setDeceduti(extractedDeaths);
			clinicalDto.setAsintomatici(extractedNoSymp);
			clinicalDto.setConSintomi(extractedSymp);
			clinicalDto.setConSintomiGuariti(extractedSympHealed);
			clinicalDto.setConSintomiDecessi(extractedSympDeaths);
			clinicalDto.setAsintomaticiGuariti(extractedNoSympHealed);
			clinicalDto.setAsintomaticiDeceduti(extractedNoSympDeaths);
			clinicalDto.setData(zonedDateTimeNow);
			clinicalDto.setDate(dataString);
			listClinicalDto.add(clinicalDto);
		});
		LOGGER.info("List return correctly");
		return listClinicalDto;
	}

	//	metodo per la creazione di esecuzioni relative agli stati clinici
	public ExecutionEntity getExecutionClinicalEntity(ExecutionDto exeClinicalDto) {
		ExecutionEntity exeClinicalEntity = new ExecutionEntity();
		exeClinicalEntity.setTime(exeClinicalDto.getTime());
		exeClinicalEntity.setResult(exeClinicalDto.getResult());
		exeClinicalEntity.setTipe(exeClinicalDto.getType());
		return exeClinicalEntity;
	}


	//	metodo per l'inserimento a DB dei processi relativi agli stati clinici
	public List<ProcessDto> getProcessClinicalDtoListFromDB(List<ProcessEntity> list) {
		return list.stream().map(entity -> {
			ProcessDto dto = new ProcessDto();
			dto.setUuid(entity.getUuid());
			dto.setDateTime(entity.getDateTime());
			dto.setStatus(entity.getStatus());
			dto.setType(entity.getTipe());
			return dto;
		}).collect(Collectors.toList());
	}

	//	metodo per l'inserimento a DB delle esecuzioni relative agli stati clinici
	public List<ExecutionDto> getExecutionClinicalDtoListFromDB(List<ExecutionEntity> list) {
		return list.stream().map(entity -> {
			ExecutionDto dto = new ExecutionDto();
			dto.setId(entity.getId());
			dto.setTime(entity.getTime());
			dto.setResult(entity.getResult());
			dto.setType(entity.getTipe());
			return dto;
		}).collect(Collectors.toList());
	}

	//	metodo per la creazione di processi relativi agli stati clinici
	public ProcessEntity getProcessClinicalEntity(ProcessDto procClinicalDto) {
		ProcessEntity procClinicalEntity = new ProcessEntity();
		procClinicalEntity.setDateTime(procClinicalDto.getDateTime());
		procClinicalEntity.setStatus(procClinicalDto.getStatus());
		procClinicalEntity.setTipe(procClinicalDto.getType());
		return procClinicalEntity;
	}
}
