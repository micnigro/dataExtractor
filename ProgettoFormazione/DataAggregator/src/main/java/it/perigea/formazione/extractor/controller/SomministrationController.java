package it.perigea.formazione.extractor.controller;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.perigea.formazione.comune.SomministrationsDto;
import it.perigea.formazione.extractor.kafka.KafkaService;
import it.perigea.formazione.extractor.model.AbbreviationsDto;
import it.perigea.formazione.extractor.model.ExecutionDto;
import it.perigea.formazione.extractor.model.ProcessDto;
import it.perigea.formazione.extractor.service.ServiceInterface;
import it.perigea.formazione.extractor.service.SomministrationImpl;


@RestController
@RequestMapping("/Vaccini")
public class SomministrationController {

	@Value("${spring.application.name}")
	private String appName;

	@Value("${topicName}")
	private String topicName;

	@Autowired
	private SomministrationImpl sommImp;

	//	chiamata REST per lo scarico delle province
	@GetMapping("/provinceExtractor")
	public ResponseEntity<List<AbbreviationsDto>> provinceClient() throws Exception {
		try {
			List<AbbreviationsDto> resultRequest = sommImp.provinceClient();
			return ResponseEntity.ok().body(resultRequest);
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}

	//chiamata REST che mi restituisce tutte le esecuzioni salvate a DB
	@GetMapping("/getListEsecutions")
	public ResponseEntity<List<ExecutionDto>> getAllExecutions() {
		List<ExecutionDto> response = sommImp.getAllExecutions();
		if (!response.isEmpty()) {
			return ResponseEntity.ok().body(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	//	chiamata REST che mi restituisce tutte le esecuzioni salvate a DB in base al tipo passato come input
	@GetMapping("/getExecutionByType")
	@ResponseBody
	public ResponseEntity<List<ExecutionDto>> getExecutionsByType(@RequestParam String type) {
		List<ExecutionDto> resultRequest = sommImp.getExecutionsByType(type);
		return ResponseEntity.ok().body(resultRequest);
	}

	//	chiamata REST che mi restituisce tutti i processi salvati a DB
	@GetMapping("/getListProcess")
	public ResponseEntity<List<ProcessDto>> getAllProcess() {
		List<ProcessDto> response = sommImp.getAllProcess();
		if (!response.isEmpty()) {
			return ResponseEntity.ok().body(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	//	chiamata REST che mi restituisce tutti i processi salvati a DB in base al tipo passato come input
	@GetMapping("/getProcessByType")
	@ResponseBody
	public ResponseEntity<List<ProcessDto>> getProcessByType(@RequestParam String type) {
		List<ProcessDto> resultRequest = sommImp.getProcessByType(type);
		return ResponseEntity.ok().body(resultRequest);
	}

	//	chiamata REST che mi restituisce tutti i processi salvati a DB in base alla data passata come input
	@GetMapping("/getProcessByDate")
	@ResponseBody
	public ResponseEntity<List<ProcessDto>> getProcessByDate(@RequestParam String date) throws ParseException {
		List<ProcessDto> resultRequest = sommImp.getProcessByDate(date);
		return ResponseEntity.ok().body(resultRequest);
	}
}
