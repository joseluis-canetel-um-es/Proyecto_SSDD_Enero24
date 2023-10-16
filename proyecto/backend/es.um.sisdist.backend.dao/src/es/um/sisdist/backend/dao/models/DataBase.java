package es.um.sisdist.backend.dao.models;

import java.util.LinkedList;

/**
 * 
 * Clase usada para representar las propiedades de una base de datos
 */
public class DataBase {
    private String id;
	private String name;
	private String url;
	private LinkedList<String> pares;

	public DataBase(String name) {
		this.name = name;
		this.pares = new LinkedList<String>();
	}
	
	public DataBase() {
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
	
	public void addPar(String key, String value) {
		String par = key+":"+value;
		pares.add(par);
	}
	
	public void removePar(String key) {
		for(String par : pares) {
			if(par.contains(key+":")) {
				pares.remove(par);
			}
		}
	}
	
	@Override
	public String toString() {
		return "DataBase [id=" + id + ", name=" + name + ", url=" + url + ", pares=" + pares + "]";
	}
}