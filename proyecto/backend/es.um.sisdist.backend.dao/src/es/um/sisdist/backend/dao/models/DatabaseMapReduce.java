package es.um.sisdist.backend.dao.models;

import java.util.LinkedList;
import java.util.UUID;

import es.um.sisdist.backend.dao.models.utils.UserUtils;

// clase para almacenar la database una vez realizadas las operaciones de map reduce
public class DatabaseMapReduce {

	private String idUser; // relacionar el id de usuario con la db
    private String id;
	private String name;
	//private String url;
	private String mrId; // id usado para retornar en user endpoint
	private String status; // comprobar si procesamiento ha terminado o no
	//private LinkedList<String> pares;

	public DatabaseMapReduce(String idUser, String name) {
		this.idUser = idUser;
		this.id = UserUtils.md5pass(idUser+name); // Generar un ID único para la base de datos
		this.name = name;
		//this.url = url;
		this.mrId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
		this.status = "";
		//this.pares = new LinkedList<String>();
	}
	
	public DatabaseMapReduce(String idUser, String id, String name, String url, LinkedList<String> pares) {
		this.idUser = idUser;
		this.id = id;
		this.name = name;
		//this.url = url;
		this.mrId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
		this.status = "";
		//this.pares = pares;
	}
	
	public DatabaseMapReduce() {
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
	
	/**
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
*/
	public String getMrId() {
		return mrId;
	}

	public void setMrId(String mrId) {
		this.mrId = mrId;
	}
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "DatabaseMapReduce [idUser=" + idUser + ", id=" + id + ", name=" + name + ", mrId=" + mrId + "]";
	}

}
