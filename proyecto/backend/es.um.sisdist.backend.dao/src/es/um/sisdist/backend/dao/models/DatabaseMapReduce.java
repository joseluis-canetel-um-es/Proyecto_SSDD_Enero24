package es.um.sisdist.backend.dao.models;

import java.util.UUID;

import es.um.sisdist.backend.dao.models.utils.UserUtils;

// clase para almacenar la database una vez realizadas las operaciones de map reduce
public class DatabaseMapReduce {

	private String idUser; // relacionar el id de usuario con la db
    private String id;
	private String name;
	private String mrId; // id usado para retornar en user endpoint
	private String status; // comprobar si procesamiento ha terminado o no
	private String data;

	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public DatabaseMapReduce(String idUser, String name) {
		this.idUser = idUser;
		this.id = UserUtils.md5pass(idUser+name); // Generar un ID único para la base de datos
		this.name = name;
		this.mrId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
		this.status = "Started"; // estado inicio
		this.data = "{}"; // json vacio
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
