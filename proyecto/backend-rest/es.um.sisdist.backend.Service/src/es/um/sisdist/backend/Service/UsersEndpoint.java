package es.um.sisdist.backend.Service;

import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.Database;
import es.um.sisdist.backend.dao.models.DatabaseMapReduce;
import es.um.sisdist.models.DatabaseDTO;
import es.um.sisdist.models.DatabaseDTOUtils;
import es.um.sisdist.models.UserDTO;
import es.um.sisdist.models.UserDTOUtils;
import jakarta.json.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
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

	/**
	 * 	
	 * MAP REDUCE
	 * 
	 * modificado path --> quitada funcion
	 * 
	 *  " map " : " ( define ( ssdd - map p ) ... ) " ,
		" reduce " : " ( define ( ssdd - reduce k values ) ... ) " ,
		" out - db " : " output - database " 
	 * @throws JSONException 
	 */
	// Método para realizar el procesamiento MapReduce
	// Petición de realización de procesamientos map-reduce y comprobación del estado de los mismos
    // modificada para recibir como parametro el nombre de la base de datos
	@POST
	@Path("/{id}/db/{dbname}/mr")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response performMapReduce(@PathParam("id") String idUser, @PathParam("dbname") String nameDb, JsonObject jsonObject) throws JSONException {
    	// resultado que devuelve el procesamiento sobre la base de datos
		 //String map = jsonObject.getString("map");
		 //String reduce = jsonObject.getString("reduce");
		 String resultado = impl.performMapReduceLogic(idUser, nameDb/**, map, reduce*/);
		
		 JSONObject json = new JSONObject(resultado);
	     String id = json.getString("Id");
		  if (resultado != null) { 
			  // retorna 202 accepted y cabecera location (obligatoria)
			  logger.info("Se realiza el procesamiento MR correctamente");
			  logger.info("este sería el ID: "+id);
			  String Location =  "/u/"+idUser+"/db/"+nameDb+"/mr/"+id;
			  return Response.accepted(json.toString()).header("Location", Location).build(); 
		  } else { 
			  logger.info("No se realiza el procesamiento MR");

			  return  Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(); // Respuesta HTTP 500 Internal Server Error si hubo un problema }
		  }
    	//return null;
    }
	
    
 // metodo consulta de bases de datos Map reduce
 	@GET
 	@Path("/{id}/db/{dbname}/mr/{mrid}")
 	@Consumes(MediaType.APPLICATION_JSON)
 	public String getDatabaseMR(@PathParam("id") String idUser, @PathParam("dbname") String nameDb, @PathParam("mrid") String idDbMr) {
 		//Optional<DatabaseMapReduce> database = impl.getDatabaseMr(idUser, idDbMr);
 		
 		/**if (database.isPresent()) {
 			return impl.getMapReduceData(idDbMr);
 		} else {
 			logger.info("No hay datos en Map reduce");
 			return null;
 		}*/
 		String result = impl.getMapReduceData(idDbMr);
		logger.info("Map reduce:"+result);

		return result;

 	}


}