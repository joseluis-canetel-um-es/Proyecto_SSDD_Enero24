package es.um.sisdist.models;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.LinkedList;

@XmlRootElement
public class DatabaseDTO {
	private String id;
	private String name;
	private String url;
	private LinkedList<String> pares;


	public DatabaseDTO(String name) {
		super();
		this.name = name;
		this.pares = new LinkedList<String>();
	}

	public DatabaseDTO()
    {
    }
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<String> getPares() {
		return pares;
	}

	public void setPares(LinkedList<String> pares) {
		this.pares = pares;
	}

}