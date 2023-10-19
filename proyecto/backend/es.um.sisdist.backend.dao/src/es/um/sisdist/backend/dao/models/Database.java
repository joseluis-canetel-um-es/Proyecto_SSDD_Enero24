package es.um.sisdist.backend.dao.models;

import java.util.LinkedList;

import es.um.sisdist.backend.dao.models.utils.UserUtils;

/**
 * 
 * Clase usada para representar las propiedades de una base de datos
 */
public class Database {
	private String idUser; // relacionar el id de usuario con la db
    private String id;
	private String name;
	private String url;
	private LinkedList<String> pares;

	public Database(String idUser, String name, String url) {
		this.idUser = idUser;
		this.id = UserUtils.md5pass(idUser+name); // Generar un ID único para la base de datos
		this.name = name;
		this.url = url;
		this.pares = new LinkedList<String>();
	}
	
	public Database(String idUser, String id, String name, String url, LinkedList<String> pares) {
		this.idUser = idUser;
		this.id = id;
		this.name = name;
		this.url = url;
		this.pares = pares;
	}
	
	public Database() {
	}
	
	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(final String idUser) {
		this.idUser = idUser;
	}

    public String getId()
    {
        return id;
    }

    public void setId(final String uid)
    {
        this.id = uid;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
		return "Database [id=" + id + ", name=" + name + ", url=" + url + ", pares=" + pares + ", idUser=" + idUser
				+ "]";
	}
}