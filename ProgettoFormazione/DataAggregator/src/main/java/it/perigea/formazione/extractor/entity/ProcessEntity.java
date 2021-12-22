package it.perigea.formazione.extractor.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="processo")
public class ProcessEntity {

	@Id
	@Column(name="uuid_processo",nullable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int uuid;

	@Column(name="data_ora")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTime;

	@Column(name="status")
	private String status;

	@Column(name="tipo")
	private String type;

	public int getUuid() {
		return uuid;
	}

	public void setUuid(int uuid) {
		this.uuid = uuid;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTipe() {
		return type;
	}

	public void setTipe(String tipe) {
		this.type = tipe;
	}
}