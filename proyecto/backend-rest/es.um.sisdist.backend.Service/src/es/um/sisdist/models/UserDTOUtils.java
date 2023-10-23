/**
 *
 */
package es.um.sisdist.models;

import es.um.sisdist.backend.dao.models.User;

/**
 *
 */
public class UserDTOUtils
{
    public static User fromDTO(UserDTO udto)
    {
        return new User(udto.getId(), udto.getEmail(), udto.getPassword(), udto.getName(), udto.getToken(),
                udto.getVisits());
    }

    public static UserDTO toDTO(User u)
    {
        return new UserDTO(u.getId(), u.getEmail(), "", // Password never is returned back
                u.getName(), u.getToken(), u.getVisits());
    }
    
    // este metodo tambien incrementa el numero de visitas
    public static UserDTO toDTOLogin(User u)
    {
    	u.addVisits(); // primero incrementa visitas y despues retorna usuario actualizado
        return new UserDTO(u.getId(), u.getEmail(), "", // Password never is returned back
                u.getName(), u.getToken(), u.getVisits());
    }
}