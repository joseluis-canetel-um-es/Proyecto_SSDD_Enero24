package es.um.sisdist.models;

import es.um.sisdist.backend.dao.models.Database;

public class DatabaseDTOUtils {
	
	public static Database fromDTO(DatabaseDTO dto) {
		return new Database(dto.getIdUser(), dto.getId(), dto.getName(), dto.getUrl(), dto.getPares());
	}

	public static DatabaseDTO toDTO(Database database) {
		return new DatabaseDTO(database.getIdUser(), database.getId(), database.getName(), database.getUrl(), database.getPares());
	}
}