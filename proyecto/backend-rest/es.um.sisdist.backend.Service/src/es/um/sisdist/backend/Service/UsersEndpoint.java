package es.um.sisdist.backend.Service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.DataBase;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.backend.dao.models.utils.UserUtils;
import es.um.sisdist.models.DatabaseDTO;
import es.um.sisdist.models.DatabaseDTOUtils;
import es.um.sisdist.models.UserDTO;
import es.um.sisdist.models.UserDTOUtils;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/u")
public class UsersEndpoint
{
    private static final Logger logger = Logger.getLogger(UsersEndpoint.class.getName());

    private AppLogicImpl impl = AppLogicImpl.getInstance();
    /** punto de entrada para una solicitud GET a la ruta "/u/{username}",
     * donde "{username}" es un parámetro de ruta que representa el nombre de usuario 
     del usuario del que se desea obtener información.
    */
    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserInfo(@PathParam("username") String username)
    {
        return UserDTOUtils.toDTO(impl.getUserByEmail(username).orElse(null));
    }
    
    
    // obtener las bases de datos de un usuario dado su ID
    @GET
    @Path("/{id}/dbinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDatabasesUser(@PathParam ("id") String userID) {
 		Optional<LinkedList<DataBase>> databases = impl.getDatabasesByUserId(userID);
 		if (databases.isPresent()) {
 			LinkedList<DatabaseDTO> databasesDTO = new LinkedList<DatabaseDTO>();
 			for( DataBase db : databases.get()) {
 				databasesDTO.add( DatabaseDTOUtils.toDTO(db) );
 			}		
 			return Response.ok(databasesDTO).build();
 		}else {
 			return Response.status(Status.BAD_REQUEST).build();
 		}
    }
    
    // metodo consulta de bases de datos
 	@GET
 	@Path("/{id}/db/{name}")
 	public DatabaseDTO getDatabase(@PathParam("id") String userId, @PathParam("name") String databaseName) {
 		return DatabaseDTOUtils.toDTO(impl.getDatabase(userId, databaseName));
 	}
   
    
 	// metodo para que el usuario pueda crear bases de datos
 	@POST
    @Path("/{id}/db")
 	@Consumes(MediaType.APPLICATION_JSON)
 	public Response createDatabase(@PathParam("id") String idUser, JsonObject jsonObject) { 		
        String databaseName = jsonObject.getString("name"); // NOMBRE BASE DE DATOS
        String key = jsonObject.getString("key");
        String value = jsonObject.getString("value");
 		LinkedList<String> pares = new LinkedList<String>();
 		pares.add(key+":"+value);
 		String databaseUrl = "/u/" + idUser + "/db/" + databaseName; 		
 		boolean created = impl.createDatabase(idUser, databaseName, databaseUrl, pares);
 		if(created) {
 			return Response.status(Response.Status.CREATED).header("Location", databaseUrl).build();
 		}else {
 			return Response.status(Response.Status.BAD_REQUEST).build();
 		}
 	}
 	
 // Añadir un par clave/valor de la base de datos
   	@POST
   	@Path("/{id}/db/{dbid}/a")
   	public Response addKeyValue(@PathParam("id") String userId, @PathParam("dbid") String databaseId, JsonObject jsonObject) {
   		String key = jsonObject.getString("key");
        String value = jsonObject.getString("value");
  		boolean added = impl.insertKeyValue(userId, databaseId, key, value);

   		if (added) {
  			return Response.ok().build(); // Respuesta HTTP 200 OK si se eliminó correctamente
  		} else {
  			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(); // Respuesta HTTP 500 Internal Server
  		}
   	    
   	}
   	
 	// Eliminar un par clave/valor de la base de datos
 	@DELETE
 	@Path("/{id}/db/{dbid}/d/{key}")
 	public Response deleteKeyValue(@PathParam("id") String userId, @PathParam("dbid") String databaseId,@PathParam("key") String clave) {

		boolean deleted = impl.deleteKeyValue(userId, databaseId, clave);

 		if (deleted) {
			return Response.ok().build(); // Respuesta HTTP 200 OK si se eliminó correctamente
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(); // Respuesta HTTP 500 Internal Server
		}
 	    
 	}
 	
}