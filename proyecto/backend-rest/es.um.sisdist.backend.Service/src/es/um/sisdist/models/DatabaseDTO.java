package es.um.sisdist.models;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

@XmlRootElement
public class DatabaseDTO {
	
	private String idUser; // relacionar el id de usuario con la db
    private String id;
	private String name;
	private String url;
	private LinkedList<String> pares;
	
	public String getIdUser() {
		return idUser;
	}
	
	public void setIdUser(String idUser) {
		this.idUser = idUser;
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
	
	public DatabaseDTO(String idUser, String id, String name, String url, LinkedList<String> pares)
    {
        super();
        this.idUser = idUser;
        this.id = id;
        this.name = name;
        this.url = url;
        this.pares = pares;
    }

    public DatabaseDTO()
    {
    }
	

}