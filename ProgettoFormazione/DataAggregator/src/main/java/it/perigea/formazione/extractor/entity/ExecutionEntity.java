package it.perigea.formazione.extractor.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="esecuzione")
public class ExecutionEntity {

	@Id
	@Column(name="id_esecuzione",nullable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name="tempo")
	private long time;

	@Column(name="esito")
	private String result;

	@Column(name="tipo")
	private String type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTipe() {
		return type;
	}

	public void setTipe(String tipe) {
		this.type = tipe;
	}
}
