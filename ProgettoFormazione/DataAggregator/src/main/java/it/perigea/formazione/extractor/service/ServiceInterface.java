package it.perigea.formazione.extractor.service;

import java.util.List;


public interface ServiceInterface<T>{

	public String dataDownload() throws Exception;

	public List<T> fromJsonToJava() throws Exception;

	public List<T> modifiedData() throws Exception;

	public List<T> runProcess() throws Exception;

}
