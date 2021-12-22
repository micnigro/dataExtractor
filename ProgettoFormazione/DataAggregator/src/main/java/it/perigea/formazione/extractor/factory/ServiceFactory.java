package it.perigea.formazione.extractor.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import it.perigea.formazione.extractor.configuraiton.FactoryConfig;
import it.perigea.formazione.extractor.service.ServiceInterface;

@Service
public class ServiceFactory {

	@Value("${lombardiaJson}")
	private String lombardiaJson;

	@Value("${statiCliniciJson}")
	private String statiCliniciJson;

	@Autowired
	public FactoryConfig factory;

	//	metodo che mi restituisce l'implementazione corretta in base al tipo di sorgente passata come input
	public ServiceInterface<?> createService(String sorgente) {
		if (sorgente.contentEquals(lombardiaJson)) {
			return factory.somministrationsImpl();
		} else if (sorgente.contentEquals(statiCliniciJson)) {
			return factory.clinicalStatusImpl();
		}
		return null;
	}
}
