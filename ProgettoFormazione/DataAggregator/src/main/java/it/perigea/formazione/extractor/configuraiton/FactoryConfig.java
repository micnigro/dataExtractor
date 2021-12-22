package it.perigea.formazione.extractor.configuraiton;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import it.perigea.formazione.extractor.service.ClinicalStatusImpl;
import it.perigea.formazione.extractor.service.SomministrationImpl;


@Configuration
public class FactoryConfig {

	@Bean (name="clinicalBean")
	public ClinicalStatusImpl clinicalStatusImpl() {
		return new ClinicalStatusImpl();
	}

	@Bean (name="sommBean")
	public SomministrationImpl somministrationsImpl() {
		return new SomministrationImpl();
	}

}
