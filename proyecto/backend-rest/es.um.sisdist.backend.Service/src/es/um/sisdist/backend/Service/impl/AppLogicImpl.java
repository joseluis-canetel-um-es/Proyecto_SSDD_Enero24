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

import es.um.sisdist.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.backend.grpc.PingRequest;
import es.um.sisdist.backend.dao.DAOFactoryImpl;
import es.um.sisdist.backend.dao.IDAOFactory;
import es.um.sisdist.backend.dao.database.IDatabaseDAO;
import es.um.sisdist.backend.dao.models.Database;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.backend.dao.models.utils.UserUtils;
import es.um.sisdist.backend.dao.user.IUserDAO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * @author dsevilla
 *
 */
public class AppLogicImpl
{
    IDAOFactory daoFactory;
    IUserDAO UserDao;
    IDatabaseDAO DatabaseDao;
    private static final Logger logger = Logger.getLogger(AppLogicImpl.class.getName());

    private final ManagedChannel channel;
    private final GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub;
    //private final GrpcServiceGrpc.GrpcServiceStub asyncStub;

    static AppLogicImpl instance = new AppLogicImpl();

    private AppLogicImpl()
    {
        daoFactory = new DAOFactoryImpl();
        Optional<String> backend = Optional.ofNullable(System.getenv("DB_BACKEND"));
        
        if (backend.isPresent() && backend.get().equals("mongo")) {
        	UserDao = daoFactory.createMongoUserDAO();
        	DatabaseDao = daoFactory.createMongoDatabaseDAO();
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
        //asyncStub = GrpcServiceGrpc.newStub(channel);
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
        	UserDao.addVisits(email); // cuando se accede al endpoint, se debe incrementar el numero de visitas
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
  
}