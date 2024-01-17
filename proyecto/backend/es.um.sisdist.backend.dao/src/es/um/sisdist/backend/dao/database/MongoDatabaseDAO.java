package es.um.sisdist.backend.dao.database;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Supplier;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static java.util.Arrays.*;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.nin;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import es.um.sisdist.backend.dao.models.Database;
import es.um.sisdist.backend.dao.utils.Lazy;


public class MongoDatabaseDAO implements IDatabaseDAO {

	private Supplier<MongoCollection<Database>> collection;

	public MongoDatabaseDAO() {
		CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
				.conventions(asList(Conventions.ANNOTATION_CONVENTION)).automatic(true).build();
		CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

		String uri = "mongodb://root:root@" + Optional.ofNullable(System.getenv("MONGO_SERVER")).orElse("localhost")
				+ ":27017/ssdd?authSource=admin";

		collection = Lazy.lazily(() -> {
			MongoClient mongoClient = MongoClients.create(uri);
			MongoDatabase database = mongoClient
					.getDatabase(Optional.ofNullable(System.getenv("DB_NAME")).orElse("ssdd"))
					.withCodecRegistry(pojoCodecRegistry);
			return database.getCollection("databases", Database.class);
		});
	}

	@Override
	public boolean createDatabase(String idUser, String databaseName, String url, LinkedList<String> pares) {
		try {
			Database database = new Database(idUser, databaseName, url); // Crear objeto DataBase
	        database.setPares(pares); // Asignar la lista de pares clave-valor
	        
	        collection.get().insertOne(database);
	        return true; 
        } catch (Exception e) {
            return false;
        }
	}

	@Override
	public boolean deleteDatabase(String idDatabase) {
		try {
            collection.get().deleteOne(eq("id", idDatabase));
            return true;
        } catch (Exception e) {
            return false;
        }
	}

	@Override
	public boolean insertClaveValor(String idDatabase, String key, String value) {
		try {
			Optional<Database> databaseOptional = getDatabase(idDatabase);
		    if (databaseOptional.isPresent()) {
		        Database database = databaseOptional.get();
		        database.addPar(key, value);
		        com.mongodb.client.result.UpdateResult result = collection.get().replaceOne(eq("id", idDatabase), database);
	            return result.getModifiedCount() > 0;
		    }else {
		    	return false;
		    }
        } catch (Exception e) {
            return false;
        }
	}

	@Override
	public boolean deleteClaveValor(String idDatabase, String key) {
		try {
			Optional<Database> databaseOptional = getDatabase(idDatabase);
		    if (databaseOptional.isPresent()) {
		        Database database = databaseOptional.get();
		        database.removePar(key);
		        com.mongodb.client.result.UpdateResult result = collection.get().replaceOne(eq("id", idDatabase), database);
	            return result.getModifiedCount() > 0;
		    }else {
		    	return false;
		    }
        } catch (Exception e) {
            return false;
        }
	}
	
	@Override
	public Optional<LinkedList<Database>> getDatabases(String idUser) {
	    try {
	        // Crear una lista para almacenar las bases de datos
	        LinkedList<Database> databaseList = new LinkedList<>();

	        Bson filter = and(
	        	    eq("idUser", idUser),
	        	    nin("status", "Finish", "Started")  // Excluir documentos con status "Finish" o "Started"
	        	);
	        // Realizar una consulta para encontrar todas las bases de datos del usuario
	        FindIterable<Database> cursor = collection.get().find(filter);

	        for (Database database : cursor) {
	            // Agregar el objeto Database a la lista
	            databaseList.add(database);
	        }

	        return Optional.of(databaseList);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Optional.empty();
	    }
	}

	@Override
	public Optional<Database> getDatabase(String idDatabase) {
		return Optional.ofNullable(collection.get().find(eq("id", idDatabase)).first());
	}
	

	@Override
	public Optional<Database> getDatabaseByName(String nameDb) {
		return Optional.ofNullable(collection.get().find(eq("name", nameDb)).first());
	}


}
