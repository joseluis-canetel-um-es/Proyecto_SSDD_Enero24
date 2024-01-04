/**
 *
 */
package es.um.sisdist.backend.Service.impl;

import java.util.ArrayList;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.bson.json.JsonObject;

import es.um.sisdist.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.backend.grpc.PingRequest;
import es.um.sisdist.backend.dao.DAOFactoryImpl;
import es.um.sisdist.backend.dao.IDAOFactory;
import es.um.sisdist.backend.dao.database.IDatabaseDAO;
import es.um.sisdist.backend.dao.databaseMapReduce.IDatabaseMapReduce;
import es.um.sisdist.backend.dao.models.Database;
import es.um.sisdist.backend.dao.models.DatabaseMapReduce;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.backend.dao.models.utils.UserUtils;
import es.um.sisdist.backend.dao.user.IUserDAO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * 
 */
public class AppLogicImpl
{
    IDAOFactory daoFactory;
    IUserDAO UserDao;
    IDatabaseDAO DatabaseDao;
    IDatabaseMapReduce DatabaseMapReduceDao;
    
    private static final Logger logger = Logger.getLogger(AppLogicImpl.class.getName());

    private final ManagedChannel channel;
    //private final GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub;
    private final GrpcServiceGrpc.GrpcServiceStub asyncStub; // server GRPC asincrono

    static AppLogicImpl instance = new AppLogicImpl();

    private AppLogicImpl()
    {
        daoFactory = new DAOFactoryImpl();
        Optional<String> backend = Optional.ofNullable(System.getenv("DB_BACKEND"));
        
        if (backend.isPresent() && backend.get().equals("mongo")) {
        	UserDao = daoFactory.createMongoUserDAO();
        	DatabaseDao = daoFactory.createMongoDatabaseDAO();
        	DatabaseMapReduceDao = daoFactory.createMongoDatabaseMrDAO();
        }
        else {
        	UserDao = daoFactory.createSQLUserDAO();
        	DatabaseDao = daoFactory.createSQLDatabaseDAO();
        }
        var grpcServerName = Optional.ofNullable(System.getenv("GRPC_SERVER"));
        var grpcServerPort = Optional.ofNullable(System.getenv("GRPC_SERVER_PORT"));

        channel = ManagedChannelBuilder
                .forAddress(grpcServerName.orElse("localhost"), Integer.parseInt(grpcServerPort.orElse("50051")))
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid needing certificates.
                .usePlaintext().build();
        blockingStub = GrpcServiceGrpc.newBlockingStub(channel);
        asyncStub = GrpcServiceGrpc.newStub(channel); // usar el asincrono
    }

    public static AppLogicImpl getInstance()
    {
        return instance;
    }

    public Optional<User> getUserByEmail(String email)
    {
        Optional<User> u = UserDao.getUserByEmail(email);
        return u;
    }

    public Optional<User> getUserById(String userId)
    {
        return UserDao.getUserById(userId);
    }

    public boolean ping(int v)
    {
    	logger.info("Issuing ping, value: " + v);
    	
        // Test de grpc, puede hacerse con la BD
    	var msg = PingRequest.newBuilder().setV(v).build();
        var response = blockingStub.ping(msg);
        
        return response.getV() == v;
    }

    // El frontend, a través del formulario de login,
    // envía el usuario y pass, que se convierte a un DTO. De ahí
    // obtenemos la consulta a la base de datos, que nos retornará,
    // si procede,
    public Optional<User> checkLogin(String email, String pass)
    {
        Optional<User> u = UserDao.getUserByEmail(email);
        if (u.isPresent())
        {
        	UserDao.addVisits(email); // cuando se accede al endpoint, se debe incrementar el numero de visitass
            String hashed_pass = UserUtils.md5pass(pass);
            if (0 == hashed_pass.compareTo(u.get().getPassword_hash())) {
                return u;
            }
        }

        return Optional.empty();
    }
    
    // REGISTRO - inserta en db el usuario
    public boolean signup(String email, String name, String password) {
    	return UserDao.insertUser(email, name, password);    	
    }
    
    
    // crea una base de datos relacionada al id de un usuario
    // se debe insertar un valor inicial en la lista
    public boolean createDatabase(String idUser, String databaseName, String url, LinkedList<String> pares) {
    	return DatabaseDao.createDatabase(idUser, databaseName, url, pares);
    }
    
    // devuelve db MR dado su Id
    public Optional<DatabaseMapReduce> getDatabaseMr(String idUser, String idDatabase) {    	
    	return DatabaseMapReduceDao.getDatabase(idDatabase);
    }
    
    // devuelve la database dado su id
    public Optional<Database> getDatabase(String idUser, String idDatabase) {    	
    	return DatabaseDao.getDatabase(idDatabase);
    }
    
    // dado un id de usuario retorna las bases de datos relacioandos
    public Optional<LinkedList<Database>> getDatabasesByUserId(String idUser) {
        try {
        	return DatabaseDao.getDatabases(idUser);

        } catch (Exception e) {
            // Manejar la excepción según sea necesario
        }
        return null;
    }
    
    // dado un id de db y una clave, elimina el par <k,v> de la db
    public boolean insertKeyValue(String idUser, String idDatabase, String key, String value) {
    	return DatabaseDao.insertClaveValor(idDatabase, key, value);
    }
    
    // dado un id de db y una clave, elimina el par <k,v> de la db
    public boolean deleteKeyValue(String idUser, String idDatabase, String key) {
    	return DatabaseDao.deleteClaveValor(idDatabase, key);
    }
    
    // metodo para lanzar procesamiento map reduce
    public String performMapReduceLogic(String idUser,String idDatabase, String map, String reduce) {
    	// * llamar a DAO para conseguir valores de database
    	 Optional<Database> db = this.getDatabase(idUser, idDatabase);
    	 LinkedList<String> lista = db.get().getPares();
    	 String pares = convertirListaAString(lista);
    	// * tomar esos valores y las funciones 
    	// * usar cliente grpc para realizar procesamiento
    	 
    	 // Crear la request 
    	 var msg = PerformMapReduceRequest.newBuilder()
 				.setMapreduce(pares).setMap(map)
 		        .setReduce(reduce).build(); // aqui se añade lista de pares y funciones map reduce
		String mrId = DatabaseMapReduceDao.createDatabase(idUser, idDatabase);
		String db_name = db.get().getName() + "MR"; // nombre de db nueva
		final String resultadoMapReduce = ""; // string donde se almacena la respuesta de map reduce

		try {
			// Llamar al servidor gRPC usando el cliente asincrono y obtener la respuesta
			//MapReduceResponse response = asyncStub.performMapReduce(msg).get();

			// la funcion recibe una request y un stream observer
			 asyncStub.performMapReduce(msg, new StreamObserver<PerformMapReduceResponse>() {

	                @Override
	                public void onNext(PerformMapReduceResponse response) {
	                    resultadoMapReduce = response.getMapreduce();
	                }

	                @Override
	                public void onError(Throwable t) {
	                    // Manejar errores según sea necesario
	                    t.printStackTrace();
	                }

	                @Override
	                public void onCompleted() {
	      				logger.info("AppLogicImpl: La comunicacion ha sido completada");
	      				DatabaseMapReduceDao.completeStatus(mrId); // marcar estado como completado
	                }

	                public String getResultadoMapReduce() {
	                    return resultadoMapReduce;
	                }
	            });

			 
			 // se debe responder con: mrid y db_out
			JSONObject respuesta = new JSONObject();
			respuesta.put("Id", mrId);
			respuesta.put("DbOut", db_name);
			return respuesta.toString(); 
			
		} catch (Exception e) {
			logger.warning("No se ha completado el procesamiento Map Reduce: "+e);
		}
    	
    	return null;
    }
    
    // funcion auxiliar usada para convertir una linkedlist a string
    public static String convertirListaAString(LinkedList<String> lista) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String elemento : lista) {
            if (stringBuilder.length() > 0) {
                // separador: coma y espacio
                stringBuilder.append(", ");
            }
            stringBuilder.append(elemento);
        }
        return stringBuilder.toString();
    }
  
}