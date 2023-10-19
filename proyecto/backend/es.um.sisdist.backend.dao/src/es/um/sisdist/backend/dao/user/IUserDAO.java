package es.um.sisdist.backend.dao.user;

import java.util.Optional;

import es.um.sisdist.backend.dao.models.User;

public interface IUserDAO
{
    public Optional<User> getUserById(String idUser);

    public Optional<User> getUserByEmail(String emailUser);
    
    public void addVisits(String emailUser);
    
    public boolean insertUser(String emailUser, String nameUser, String passwordUser);
    
    public boolean deleteUser(String emailUser);
    
    public boolean updateUser(User user); 
}