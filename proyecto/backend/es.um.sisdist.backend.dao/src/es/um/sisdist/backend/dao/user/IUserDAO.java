package es.um.sisdist.backend.dao.user;


import java.util.LinkedList;
import java.util.Optional;

import es.um.sisdist.backend.dao.models.DataBase;
import es.um.sisdist.backend.dao.models.User;

public interface IUserDAO
{
    public Optional<User> getUserById(String idUser);

    public Optional<User> getUserByEmail(String emailUser);
    
    public void addVisits(String emailUser);
    public boolean insertUser(String emailUser, String nameUser, String passwordUser);
    public boolean deleteUser(String emailUser);
    public boolean updateUser(User user);
    public boolean insertDatabase(String idUser, String databaseName, String url, LinkedList<String> pares);
	public boolean deleteDatabase(String idUser, String databaseName);
	public DataBase getDatabase(String idUser, String databaseName);
	public boolean insertClaveValor(String idUser, String databaseId, String clave, String value);
	public boolean deleteClaveValor(String idUser, String databaseId, String clave);
	public Optional<LinkedList<DataBase>> getDatabases(String idUser);
 
}