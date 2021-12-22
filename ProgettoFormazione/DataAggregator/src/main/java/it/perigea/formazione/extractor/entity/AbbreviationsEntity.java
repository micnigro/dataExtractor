package it.perigea.formazione.extractor.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="checkprovince")
public class AbbreviationsEntity {

	@Id
	@Column(name="codice",nullable=false)
	private int codice;

	@Column(name="provincia")
	private String province;

	@Column(name="sigla")
	private String abbreviation;

	@Column(name="regione")
	private String region;

	public int getId() {
		return codice;
	}

	public void setId(int codice) {
		this.codice = codice;
	}

	public String getProvince() {
		return province;
	}

	public void setName(String provincia) {
		this.province = provincia;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

}
