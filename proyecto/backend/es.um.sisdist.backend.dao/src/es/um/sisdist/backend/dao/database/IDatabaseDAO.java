package es.um.sisdist.backend.dao.database;

import java.util.LinkedList;
import java.util.Optional;

import es.um.sisdist.backend.dao.models.Database;

/**
* A partir de la URL de la base de datos, se pueden añadir y eliminar
pares clave/valor a la base de datos, así como obtener listados de
valores y lanzar procesamientos map-reduce *
*/
public interface IDatabaseDAO {
	
	public boolean createDatabase(String idUser, String databaseName, String url, LinkedList<String> pares);
	 
	public boolean deleteDatabase(String idDatabase);
	
	public Optional<Database> getDatabase(String idDatabase);
	
	public boolean insertClaveValor(String idDatabase, String key, String value);
	
	public boolean deleteClaveValor(String idDatabase, String key);
	
	public Optional<LinkedList<Database>> getDatabases(String idUser);
	 
}
