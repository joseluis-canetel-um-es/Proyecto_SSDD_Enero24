/**
 *
 */
package es.um.sisdist.backend.dao.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Logger;

import es.um.sisdist.backend.dao.models.DataBase;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.backend.dao.models.utils.UserUtils;
import es.um.sisdist.backend.dao.utils.Lazy;

/**
 * @author dsevilla
 *
 */
public class SQLUserDAO implements IUserDAO
{
    Supplier<Connection> conn;
    private static final Logger logger = Logger.getLogger(SQLUserDAO.class.getName());

    public SQLUserDAO()
    {
    	conn = Lazy.lazily(() -> 
    	{
    		try
    		{
    			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();

    			// Si el nombre del host se pasa por environment, se usa aquí.
    			// Si no, se usa localhost. Esto permite configurarlo de forma
    			// sencilla para cuando se ejecute en el contenedor, y a la vez
    			// se pueden hacer pruebas locales
    			String sqlServerName = Optional.ofNullable(System.getenv("SQL_SERVER")).orElse("localhost");
    			String dbName = Optional.ofNullable(System.getenv("DB_NAME")).orElse("ssdd");
    			return DriverManager.getConnection(
                    "jdbc:mysql://" + sqlServerName + "/" + dbName + "?user=root&password=root");
    		} catch (Exception e)
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
            
    			return null;
    		}
    	});
    }

    @Override
    public Optional<User> getUserById(String id)
    {
    	  PreparedStatement stm;
          try {
              stm = conn.get().prepareStatement("SELECT * FROM users WHERE id = ?");
              stm.setString(1, id);
              ResultSet result = stm.executeQuery();
              if (result.next()) {
                  return createUser(result);
              }
          } catch (SQLException e) {
          }
          return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email)
    {
        PreparedStatement stm;
        try
        {
            stm = conn.get().prepareStatement("SELECT * from users WHERE email = ?");
            stm.setString(1, email);
            ResultSet result = stm.executeQuery();
            if (result.next()) {
                return createUser(result);

            }
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return Optional.empty();
    }

    private Optional<User> createUser(ResultSet result)
    {
        try
        {
            return Optional.of(new User(result.getString(1), // id
                    result.getString(2), // email
                    result.getString(3), // pwhash
                    result.getString(4), // name
                    result.getString(5), // token
                    result.getInt(6))); // visits
        } catch (SQLException e)
        {
            return Optional.empty();
        }
    }
    
 // modificada por kholoud
 
    
    /** modificado por kholoud*/
    // recibe un objeto User que contiene la información actualizada 
    // del usuario que se desea modificar en la base de datos
    public boolean updateUser(User user) {
        PreparedStatement stm;
        try {
            stm = conn.get().prepareStatement("UPDATE users SET id = ?, email = ?, password_hash = ?, name = ?, token = ?, visits = ? WHERE id = ?");
            stm.setString(1, user.getId());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getPassword_hash());
            stm.setString(4, user.getName());
            stm.setString(5, user.getToken());
            stm.setInt(6, user.getVisits());
            //Se ejecuta la consulta de actualización
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Se verifica si se modificó al menos una fila en la base de datos 
        } catch (SQLException e) {
        }
        return false;
    }
    
    /** modificada por kholoud*/
    // inserta el user en la base de datos 
    // password ID Y TOKEN ????????????????????
    public boolean insertUser(String email, String name, String password) {
        PreparedStatement stm;
        try {
            stm = conn.get().prepareStatement("INSERT INTO users (id, email, password_hash, name, token, visits) VALUES (?, ?, ?, ?, ?, ?)");
            stm.setString(1, UUID.randomUUID().toString()); 
            stm.setString(2, email);
            stm.setString(3, UserUtils.md5pass(password)); // modificado
            stm.setString(4, name);
            stm.setString(5, UUID.randomUUID().toString());
            stm.setInt(6, 0);
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    /** modificada por kholoud*/
    // elimina el user en la base de datos 
    // recibe el ID DEL USUARIO
    public boolean deleteUser(String email) {
        PreparedStatement stm;
        try {
            stm = conn.get().prepareStatement("DELETE FROM users WHERE email = ?");
            stm.setString(1, email);
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
        }
        return false;
    }
    
    /**modificado por kholoud*/
    
	@Override
	public void addVisits(String email) {
		// TODO Auto-generated method stub
		// obtener usuario y modificar su numero de visitas en la base de datos
		 try {
		        PreparedStatement stm = conn.get().prepareStatement("UPDATE users SET visits = visits + 1 WHERE email = ?");
		        stm.setString(1, email);
		        stm.executeUpdate();
		        //return rowsAffected > 0;
		    } catch (SQLException e) {
		       // return false;
		    }
		

	}

	@Override
	public boolean insertDatabase(String idUser, String databaseName, String url, LinkedList<String> pares) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDatabase(String idUser, String databaseName) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public boolean deleteClaveValor(String idUser, String databaseName, String clave) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DataBase getDatabase(String idUser, String databaseName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<LinkedList<DataBase>> getDatabases(String idUser) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public boolean insertClaveValor(String idUser, String databaseName, String clave, String value) {
		// TODO Auto-generated method stub
		return false;
	}

}