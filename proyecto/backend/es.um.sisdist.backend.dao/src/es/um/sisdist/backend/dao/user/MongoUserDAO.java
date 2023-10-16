/**
 *
 */
package es.um.sisdist.backend.dao.user;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static java.util.Arrays.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import es.um.sisdist.backend.dao.models.*;
import es.um.sisdist.backend.dao.models.utils.UserUtils;
import es.um.sisdist.backend.dao.utils.Lazy;



/**
 * @author dsevilla
 *
 */
public class MongoUserDAO implements IUserDAO
{
    private static final Logger logger = Logger.getLogger(MongoUserDAO.class.getName());
    private Supplier<MongoCollection<User>> collection;

    public MongoUserDAO()
    {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().conventions(asList(Conventions.ANNOTATION_CONVENTION)).automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "mongodb://root:root@" 
        		+ Optional.ofNullable(System.getenv("MONGO_SERVER")).orElse("localhost")
                + ":27017/ssdd?authSource=admin";

        collection = Lazy.lazily(() -> 
        {
        	MongoClient mongoClient = MongoClients.create(uri);
        	MongoDatabase database = mongoClient
        		.getDatabase(Optional.ofNullable(System.getenv("DB_NAME")).orElse("ssdd"))
        		.withCodecRegistry(pojoCodecRegistry);
        	return database.getCollection("users", User.class);
        });
    }

    @Override
    public Optional<User> getUserById(String id)
    {
        Optional<User> user = Optional.ofNullable(collection.get().find(eq("id", id)).first());
        return user;
    }

    @Override
    public Optional<User> getUserByEmail(String email)
    {
        Optional<User> user = Optional.ofNullable(collection.get().find(eq("email", email)).first());
        return user;
    }
        
    @Override
   public boolean insertUser(String email, String name, String password) {
        try {
            User user = new User(email, UserUtils.md5pass(password), name, UUID.randomUUID().toString(),0); // Crear objeto User con los datos proporcionados
            collection.get().insertOne(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteUser(String id) {
        try {
            collection.get().deleteOne(eq("id", id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        try {
            Document filter = new Document("id", user.getId());
            Document update = new Document("$set", new Document()
                .append("email", user.getEmail())
                .append("password_hash", user.getPassword_hash())
                .append("name", user.getName())
                .append("token", user.getToken())
                .append("visits", user.getVisits())
                .append("databases", user.getDatabases())
            );
            com.mongodb.client.result.UpdateResult result = collection.get().updateOne(filter, update);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

	@Override
	public void addVisits(String email) {
		try {
	        Document filter = new Document("email", email);
	        Document update = new Document("$inc", new Document("visits", 1));
	        collection.get().updateOne(filter, update);
	    } catch (Exception e) {
	    }
	}

	@Override
	public boolean insertDatabase(String idUser, String databaseName, String url, LinkedList<String> pares) {
		try {
			DataBase database = new DataBase(databaseName); // Crear objeto DataBase con el nombre db
	        database.setId(UUID.randomUUID().toString()); // Generar un ID único para la base de datos
	        database.setUrl(url); // Asignar la URL
	        database.setPares(pares); // Asignar la lista de pares clave-valor
	        LinkedList<DataBase> databases = collection.get().find(eq("id", idUser)).first().getDatabases();
	        databases.add(database);
	        Document filter = new Document("id", idUser);
            Document update = new Document("$set", new Document("databases", databases));
            com.mongodb.client.result.UpdateResult result = collection.get().updateOne(filter, update);  
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            return false;
        }
	}

	@Override
	public boolean deleteDatabase(String idUser, String databaseName) {
		try {
			LinkedList<DataBase> databases = collection.get().find(eq("id", idUser)).first().getDatabases();
			for (DataBase database : databases) {
			    if (database.getName().equals(databaseName)) {
			        return databases.remove(database);
			    }
			}
            Document filter = new Document("id", idUser);
            Document update = new Document("$set", new Document("databases", databases));
            com.mongodb.client.result.UpdateResult result = collection.get().updateOne(filter, update);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            return false;
        }
	}

	@Override
	public DataBase getDatabase(String idUser, String databaseName) {
		try {
			LinkedList<DataBase> databases = collection.get().find(eq("id", idUser)).first().getDatabases();
			for (DataBase database : databases) {
			    if (database.getName().equals(databaseName)) {
			        return database;
			    }
			}
			return null;
		} catch (Exception e) {
            return null;
        }
	}

	@Override
	public Optional<LinkedList<DataBase>> getDatabases(String idUser) {
		try {
			Optional<LinkedList<DataBase>> databases = Optional.ofNullable(collection.get().find(eq("id", idUser)).first().getDatabases());
			return databases;
		}catch (Exception e) {
            return null;
        }
	}

	@Override
	public boolean insertClaveValor(String idUser, String databaseId, String clave, String value) {
		try {
			LinkedList<DataBase> databases = collection.get().find(eq("id", idUser)).first().getDatabases();
			for (DataBase database : databases) {
			    if (database.getId().equals(databaseId)) {
			    	database.addPar(clave, value);
			    }
			}
            Document filter = new Document("id", idUser);
            Document update = new Document("$set", new Document("databases", databases));
            com.mongodb.client.result.UpdateResult result = collection.get().updateOne(filter, update);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            return false;
        }
	}
	
	@Override
	public boolean deleteClaveValor(String idUser, String databaseId, String clave) {
		try {
			LinkedList<DataBase> databases = collection.get().find(eq("id", idUser)).first().getDatabases();
			for (DataBase database : databases) {
				if (database.getId().equals(databaseId)) {
			    	database.removePar(clave);
			    }
			}
			Document filter = new Document("id", idUser);
	        Document update = new Document("$set", new Document("databases", databases));
	        com.mongodb.client.result.UpdateResult result = collection.get().updateOne(filter, update);
	        return result.getModifiedCount() > 0;
		}catch (Exception e) {
            return false;
        }
		
	}
}