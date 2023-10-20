/**
 *
 */
package es.um.sisdist.backend.dao.user;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static java.util.Arrays.*;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

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
            User user = new User(email, UserUtils.md5pass(password), name, UUID.randomUUID().toString(), 0); // Crear objeto User con los datos proporcionados
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
            );
            com.mongodb.client.result.UpdateResult result = collection.get().updateOne(filter, update);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

	@Override
	public boolean addVisits(String email) {
		try {
			Optional<User> userOptional = getUserByEmail(email);
		    if (userOptional.isPresent()) {
		        User user = userOptional.get();
		        user.addVisits();
		        com.mongodb.client.result.UpdateResult result = collection.get().replaceOne(eq("email", email), user);
	            return result.getModifiedCount() > 0;
		    }else {
		    	return false;
		    }
	    } catch (Exception e) {
	    	 return false;
	    }
	}
	
}