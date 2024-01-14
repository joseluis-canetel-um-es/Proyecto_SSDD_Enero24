package es.um.sisdist.backend.dao.databaseMapReduce;

import java.util.Optional;

import es.um.sisdist.backend.dao.models.DatabaseMapReduce;

public interface IDatabaseMapReduce {
	public String createDatabase(String idUser, String databaseName);
	
	public Optional<DatabaseMapReduce> getDatabase(String idDatabase);
	
	public void completeStatus(String id);
	
    public String getDataStatus(String id);


}
