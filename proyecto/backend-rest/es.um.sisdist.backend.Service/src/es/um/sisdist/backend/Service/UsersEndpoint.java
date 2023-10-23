package es.um.sisdist.backend.Service;

import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.Database;
import es.um.sisdist.models.DatabaseDTO;
import es.um.sisdist.models.DatabaseDTOUtils;
import es.um.sisdist.models.UserDTO;
import es.um.sisdist.models.UserDTOUtils;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/u")
public class UsersEndpoint {
	private static final Logger logger = Logger.getLogger(UsersEndpoint.class.getName());

	private AppLogicImpl impl = AppLogicImpl.getInstance();

	/**
	 * punto de entrada para una solicitud GET a la ruta "/u/{id}", donde "{id}" es
	 * un parámetro de ruta que representa el id de usuario del usuario del que se
	 * desea obtener información.
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserDTO getUserInfo(@PathParam("id") String idUser) {
		return UserDTOUtils.toDTO(impl.getUserById(idUser).orElse(null));
	}

	// metodo para que el usuario pueda crear bases de datos
	@POST
	@Path("/{id}/db")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDatabase(@PathParam("id") String idUser, JsonObject jsonObject) {
		String databaseName = jsonObject.getString("name");
		String key = jsonObject.getString("key");
		String value = jsonObject.getString("value");
		LinkedList<String> pares = new LinkedList<String>();
		pares.add(key + ":" + value);
		String databaseUrl = "/u/" + idUser + "/db/" + databaseName;
		boolean created = impl.createDatabase(idUser, databaseName, databaseUrl, pares);
		if (created) {
			return Response.status(Response.Status.CREATED).header("Location", databaseUrl).build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	// Añadir un par clave/valor de la base de datos
	@PUT
	@Path("/{id}/db/{dbid}/d/{key}")
	public Response addKeyValue(@PathParam("id") String idUser, @PathParam("dbid") String idDatabase,
			@PathParam("key") String key, @QueryParam("v") String value) {
		boolean added = impl.insertKeyValue(idUser, idDatabase, key, value);
		if (added) {
			return Response.ok().build(); // Respuesta HTTP 200 OK si se agregó correctamente
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(); // Respuesta HTTP 500 Internal Server
																					// Error si hubo un problema
		}
	}

	// Eliminar un par clave/valor de la base de datos
	@DELETE
	@Path("/{id}/db/{dbid}/d/{key}")
	public Response deleteKeyValue(@PathParam("id") String idUser, @PathParam("dbid") String idDatabase,
			@PathParam("key") String key) {
		boolean deleted = impl.deleteKeyValue(idUser, idDatabase, key);
		if (deleted) {
			return Response.ok().build(); // Respuesta HTTP 200 OK si se eliminó correctamente
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(); // Respuesta HTTP 500 Internal Server
		}

	}

	/**
	 * 	
	 * MAP REDUCE
	 */
	// Método para realizar el procesamiento MapReduce
    @GET
	@Path("/{id}/db/{dbid}/mr/{funcion}")
    public Response performMapReduce(@PathParam("id") String idUser, @PathParam("dbid") String idDatabase, @PathParam("funcion") String funcion) {
        // Llamar a gRPC para realizar el procesamiento MapReduce con la función indicada
        // lógica de procesamiento MapReduce con la función

    	 
    	// resultado que devuelve el procesamiento
		/*
		 * String resultado = impl.performMapReduceLogic(idUser, idDatabase, funcion);
		 * 
		 * if (resultado != null) { return Response.ok(resultado).build(); // Respuesta
		 * HTTP 200 OK con el resultado } else { return
		 * Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(); // Respuesta
		 * HTTP 500 Internal Server Error si hubo un problema }
		 */
    	return null;
    }

    // Aquí deberías implementar tu lógica de procesamiento MapReduce con la función proporcionada
    //private String performMapReduceLogic(String idUser, String idDatabase, String funcion) {
        // Lógica para procesar MapReduce con la función dada.
        // Realiza las operaciones necesarias y devuelve el resultado como una cadena.

        // Ejemplo:
      //  String resultado = MapReduceProcessor.process(idUser, idDatabase, funcion);
    //    return resultado;
    //}
	
	
	
	// obtener las bases de datos de un usuario dado su ID
	@GET
	@Path("/{id}/dbinfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDatabasesUser(@PathParam("id") String idUser) {
		Optional<LinkedList<Database>> databases = impl.getDatabasesByUserId(idUser);
		if (databases.isPresent()) {
			LinkedList<DatabaseDTO> databasesDTO = new LinkedList<DatabaseDTO>();
			for (Database db : databases.get()) {
				databasesDTO.add(DatabaseDTOUtils.toDTO(db));
			}
			return Response.ok(databasesDTO).build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	// metodo consulta de bases de datos
	@GET
	@Path("/{id}/db/{dbid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDatabase(@PathParam("id") String idUser, @PathParam("dbid") String idDatabase) {
		Optional<Database> database = impl.getDatabase(idUser, idDatabase);
		if (database.isPresent()) {
			return Response.ok(DatabaseDTOUtils.toDTO(database.get())).build();
		} else {
			logger.info("No te envio nada");
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}