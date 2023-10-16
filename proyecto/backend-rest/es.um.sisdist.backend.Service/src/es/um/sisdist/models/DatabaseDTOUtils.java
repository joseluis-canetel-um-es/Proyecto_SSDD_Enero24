package es.um.sisdist.models;

import es.um.sisdist.backend.dao.models.DataBase;

public class DatabaseDTOUtils {
	
	public static DataBase fromDTO(DatabaseDTO dto) {
		DataBase database = new DataBase(dto.getName());
		database.setId(dto.getId());
		database.setUrl(dto.getUrl());
		database.setPares(dto.getPares());
		return database;
	}

	public static DatabaseDTO toDTO(DataBase database) {
		DatabaseDTO dto = new DatabaseDTO(database.getName());
		dto.setId(database.getId());
		dto.setUrl(database.getUrl());
		dto.setPares(database.getPares());
		return dto;
	}
}