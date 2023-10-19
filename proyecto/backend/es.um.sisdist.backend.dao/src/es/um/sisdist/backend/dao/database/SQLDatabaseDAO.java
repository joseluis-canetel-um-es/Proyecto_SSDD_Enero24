package es.um.sisdist.backend.dao.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.sql.ResultSet;

import es.um.sisdist.backend.dao.models.Database;
import es.um.sisdist.backend.dao.utils.Lazy;

public class SQLDatabaseDAO implements IDatabaseDAO {
	Supplier<Connection> conn;
	/*
	public SQLDatabaseDAO() {
		conn = Lazy.lazily(() -> {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();

				// Si el nombre del host se pasa por environment, se usa aquí.
				// Si no, se usa localhost.
				String sqlServerName = Optional.ofNullable(System.getenv("SQL_SERVER")).orElse("localhost");
				String dbName = Optional.ofNullable(System.getenv("DB_NAME")).orElse("ssdd");
				return DriverManager
						.getConnection("jdbc:mysql://" + sqlServerName + "/" + dbName + "?user=root&password=root");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	// elimina una base de datos 
	@Override
	public boolean deleteDatabase(String userId, String databaseName) {
	    PreparedStatement stm;
	    try {
	        stm = conn.get().prepareStatement("DELETE FROM databases WHERE name = ? AND idUser = ?");
	        stm.setString(1, databaseName);
	        stm.setString(2, userId);
	        int rowsAffected = stm.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        // Manejar la excepción SQL según sea necesario
	    }
	    return false;
	}

	@Override
	public boolean insertDatabase(String db, String idUser, HashMap<String, Object> datos, String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDatabase(String idUser, String databaseName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<Database> getDatabase(String userID, String databaseId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteClaveValor(String dbId, String clave) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Database> getDatabases(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean insertDatabase(String idUser, String databaseName, String url, LinkedList<String> pares) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDatabase(String idDatabase) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<Database> getDatabase(String idDatabase) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public boolean insertClaveValor(String idDatabase, String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	

/**
	@Override
	public DataBase getDatabase(String databaseId) {
		try {
			PreparedStatement statement = conn.get().prepareStatement("SELECT * FROM databases WHERE id = ?");
			statement.setString(1, databaseId);

			ResultSet result = statement.executeQuery();

			if (result.next()) {
				// Crear una instancia de la clase DataBase y asignar los valores
				// correspondientes
				DataBase database = new DataBase(result.getString("name"));
				database.setId(result.getString("id"));
				database.setIdUser(result.getString("idUser"));

				// Obtener el mapa de pares clave-valor de la base de datos
				String paresString = result.getString("pares");

				if (paresString != null && !paresString.isEmpty()) {
					// Convertir la cadena de pares a un mapa
					HashMap<String, String> pares = new HashMap<>();
					String[] paresArray = paresString.split(",");

					for (String par : paresArray) {
						String[] keyValue = par.split(":");
						if (keyValue.length == 2) {
							pares.put(keyValue[0], keyValue[1]);
						}
					}

					database.setPares(pares);
				}

				return database;
			}
		} catch (SQLException e) {
		}

		// Si no se encontró ninguna base de datos, retornar null
		return null;
	}
	
	*/
	/*
	@Override
	public DataBase getDatabase(String databaseId) {
	    try {
	        PreparedStatement statement = conn.get().prepareStatement("SELECT * FROM databases WHERE id = ?");
	        statement.setString(1, databaseId);

	        ResultSet result = statement.executeQuery();

	        if (result.next()) {
	            // Crear una instancia de la clase DataBase y asignar los valores correspondientes
	            DataBase database = new DataBase(result.getString("name"));
	            database.setId(result.getString("id"));
	            database.setIdUser(result.getString("idUser"));

	            // Obtener la cadena de pares clave-valor de la base de datos
	            String paresString = result.getString("pares");

	            if (paresString != null && !paresString.isEmpty()) {
	                // Convertir la cadena de pares a una lista de cadenas
	                List<String> paresList = Arrays.asList(paresString.split(","));

	                // Asignar la lista de pares clave-valor a la base de datos
	                database.setPares(paresList);
	            }

	            return database;
	        }
	    } catch (SQLException e) {
	        // Manejar la excepción
	    }

	    // Si no se encontró ninguna base de datos, retornar null
	    return null;
	}
	*/

	@Override
	public boolean createDatabase(String idUser, String databaseName, String url, LinkedList<String> pares) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDatabase(String idDatabase) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<Database> getDatabase(String idDatabase) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public boolean insertClaveValor(String idDatabase, String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteClaveValor(String idDatabase, String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<LinkedList<Database>> getDatabases(String idUser) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

/**
	@Override
	public void addClaveValor(String db, String clave, String valor) {
		// Añade un par Clave,Valor a la base de datos
		try {
			// Obtener la instancia de la base de datos
			DataBase database = getDatabase(db);
			if (database != null) {
				// Obtener el mapa de pares clave-valor de la base de datos
				HashMap<String, String> paresDb = database.getPares();
				paresDb.put(clave, valor);
				// Eliminar la clave especificada del mapa de pares

				// Convertir el mapa a una representación de cadena
				StringBuilder sb = new StringBuilder();

				for (Map.Entry<String, String> entry : paresDb.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();

					sb.append(key).append(":").append(value).append(",");
				}

				// Eliminar la última coma
				if (sb.length() > 0) {
					sb.deleteCharAt(sb.length() - 1);
				}

				// Preparar la declaración SQL
				PreparedStatement statement = conn.get()
						.prepareStatement("UPDATE databases SET pares = ? WHERE name = ?");
				statement.setString(1, sb.toString());
				statement.setString(2, db);

				// Ejecutar la consulta de update
				statement.executeUpdate();
			}
		} catch (SQLException e) {
		}

	}
	*/
	/*
	// eliminar un par clave valor
	@Override
	public boolean deleteClaveValor(String db, String clave) {
		// Elimina un par Clave,Valor de la base de datos
		try {
			// Obtener la instancia de la base de datos
			DataBase database = getDatabase(db);

			if (database != null) {
				// Obtener el mapa de pares clave-valor de la base de datos
				List<String> pares = database.getPares();

				// Eliminar la clave especificada del mapa de pares
				//pares.remove(clave);

				// Convertir el mapa a una representación de cadena
				StringBuilder sb = new StringBuilder();

				for (String keyValue : pares){
					/**
					String key = entry.getKey();
					String value = entry.getValue();
					sb.append(key).append(":").append(value).append(",");
					String[] parts = keyValue.split(":");

					// Obtener la clave y el valor y si coincide key, se elimina
					String key = parts[0];
					String value = parts[1];
					if(key.equals(clave)) {
						pares.remove(keyValue);
					}
				}
				
				for(String keyValue: pares) {
					String[] parts = keyValue.split(":");

					// Obtener la clave y el valor y si coincide key, se elimina
					String key = parts[0];
					String value = parts[1];
					sb.append(key).append(":").append(value).append(",");

				}

				// Eliminar la última coma
				if (sb.length() > 0) {
					sb.deleteCharAt(sb.length() - 1);
				}

				PreparedStatement statement = conn.get()
						.prepareStatement("UPDATE databases SET pares = ? WHERE id = ?");
				statement.setString(1, sb.toString());
				statement.setString(2, database.getId());

				// Ejecutar la consulta de update
				statement.executeUpdate();
				return true;
			}
		} catch (SQLException e) {
		}
		return false;

	}
	
	@Override
	public void getValues() {
		// Obtiene lista de todos los pares Clave,Valor de la base de datos

		// TODO Auto-generated method stub

	}
	*/
/**
	@Override
	public boolean insertDatabase(String db, String idUser) {
		PreparedStatement stm;
		try {
			stm = conn.get().prepareStatement("INSERT INTO databases (id, name, user_id, pares) VALUES (?, ?, ?, ?)");
			// Crear un mapa vacío para los pares clave-valor
			// HashMap<String, String> pares = new HashMap<>();
			// convertir el mapa de pares a string
			String paresString = "";

			stm.setString(1, UUID.randomUUID().toString());
			stm.setString(2, db);
			stm.setString(3, idUser);
			stm.setString(4, paresString);
			int rowsAffected = stm.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
		}
		return false;
	}

	@Override
	public boolean insertDatabase(String db, String idUser, HashMap<Object, Object> hashMap) {
	    PreparedStatement stm;
	    try {
	        stm = conn.get().prepareStatement("INSERT INTO databases (id, name, idUser, pares) VALUES (?, ?, ?, ?)");

	        String paresString = String.join(",", pairs); // Convertir la lista de pares a una cadena separada por comas

	        stm.setString(1, UUID.randomUUID().toString());
	        stm.setString(2, db);
	        stm.setString(3, idUser);
	        stm.setString(4, paresString);
	        int rowsAffected = stm.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException e) {
	    }
	    return false;
	}
	*/

/**
	@Override
	public ArrayList<DataBase> getDatabases(String userId) {
		  ArrayList<DataBase> databases = new ArrayList<DataBase>();

		    try {
		        PreparedStatement stm = conn.get().prepareStatement("SELECT * FROM databases WHERE idUser = ?");
		        stm.setString(1, userId);

		        ResultSet result = stm.executeQuery();

		        while (result.next()) {
		            DataBase database = new DataBase(result.getString("name"));
		            database.setId(result.getString("id"));
		            database.setIdUser(result.getString("idUser"));

		            String paresString = result.getString("pares");

		            if (paresString != null && !paresString.isEmpty()) {
		                // Convertir la cadena de pares a un mapa
		                HashMap<String, String> pares = new HashMap<String, String>();
		                String[] paresArray = paresString.split(",");

		                for (String par : paresArray) {
		                    String[] keyValue = par.split(":");
		                    if (keyValue.length == 2) {
		                        pares.put(keyValue[0], keyValue[1]);
		                    }
		                }

		                database.setPares(pares);
		            }

		            databases.add(database);
		        }

		    } catch (SQLException e) {
		    }

		    return databases;
	}*/
	/*
	@Override
	public ArrayList<DataBase> getDatabases(String userId) {
	    ArrayList<DataBase> databases = new ArrayList<>();

	    try {
	        PreparedStatement stm = conn.get().prepareStatement("SELECT * FROM databases WHERE idUser = ?");
	        stm.setString(1, userId);

	        ResultSet result = stm.executeQuery();

	        while (result.next()) {
	            DataBase database = new DataBase(result.getString("name"));
	            database.setId(result.getString("id"));
	            database.setIdUser(result.getString("idUser"));

	            String paresString = result.getString("pares");

	            if (paresString != null && !paresString.isEmpty()) {
	                // Convertir la cadena de pares a una lista de cadenas
	                List<String> paresList = Arrays.asList(paresString.split(","));

	                // Asignar la lista de pares clave-valor a la base de datos
	                database.setPares(paresList);
	            }

	            databases.add(database);
	        }

	    } catch (SQLException e) {
	        // Manejar la excepción
	    }

	    return databases;
	}
	 */

}
