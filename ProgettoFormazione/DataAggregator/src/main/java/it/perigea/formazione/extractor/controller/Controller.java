package it.perigea.formazione.extractor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.perigea.formazione.extractor.factory.ServiceFactory;
//import it.perigea.formazione.extractor.service.ServiceInterface;

@RestController
@RequestMapping("/Generale")
public class Controller {

	@Autowired
	private ServiceFactory factory;


	@GetMapping("/dataDownload")
	public ResponseEntity<String> dataDownload(@RequestParam String sorgente) {
		try {
			List<?> resultRequest = factory.createService(sorgente).runProcess();
			return ResponseEntity.ok().body("The process ended succesfully");
		} catch (Exception exc) {
			exc.printStackTrace();
			return ResponseEntity.ok().body("Status failure.");
		}
	}
}
