package es.um.sisdist.backend.dao.databaseMapReduce;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Optional;
import java.util.function.Supplier;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.json.JSONObject;

import es.um.sisdist.backend.dao.models.DatabaseMapReduce;
import es.um.sisdist.backend.dao.utils.Lazy;

public class MongoDatabaseMapReduceDAO implements IDatabaseMapReduce{
	
	private Supplier<MongoCollection<DatabaseMapReduce>> collection;
	
	public MongoDatabaseMapReduceDAO() {
		CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
				.conventions(asList(Conventions.ANNOTATION_CONVENTION)).automatic(true).build();
		CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

		String uri = "mongodb://root:root@" + Optional.ofNullable(System.getenv("MONGO_SERVER")).orElse("localhost")
				+ ":27017/ssdd?authSource=admin";

		collection = Lazy.lazily(() -> {
			MongoClient mongoClient = MongoClients.create(uri);
			MongoDatabase databaseMr = mongoClient
					.getDatabase(Optional.ofNullable(System.getenv("DB_NAME")).orElse("ssdd"))
					.withCodecRegistry(pojoCodecRegistry);
			return databaseMr.getCollection("databases", DatabaseMapReduce.class);
		});
	}

	
	// create MR new database, return mrId
	@Override
	public String createDatabase(String idUser, String databaseName) {
		try {
			DatabaseMapReduce db_mr = new DatabaseMapReduce(idUser, databaseName); // Crear objeto DB MapReduce
			//db_mr.setPares(pares); // Asignar la lista de pares clave-valor
	        collection.get().insertOne(db_mr);
	        return db_mr.getMrId(); 
        } catch (Exception e) {
            return null;
        }
	}

	// return the created db
	@Override
	public Optional<DatabaseMapReduce> getDatabase(String idDatabase) {
		return Optional.ofNullable(collection.get().find(eq("id", idDatabase)).first());
	}
	
	 // Método para obtener la data cuando el estado es 'Finish' y cuando no, devuelve vacio y estado actual
    public String getDataStatus(String id) {
        Optional<DatabaseMapReduce> optionalDb = Optional.ofNullable(collection.get().find(Filters.eq("mrId", id), DatabaseMapReduce.class).first());
		JSONObject data = new JSONObject();

        if (optionalDb.isPresent()) {
    		if ("Finish".equals(optionalDb.get().getStatus())) {
    			data.put("mapreduce",optionalDb.get().getData() );
    			data.put("status",optionalDb.get().getStatus() );

    		}else {
    			data.put("mapreduce","" );
    			data.put("status",optionalDb.get().getStatus());
    		}
        }
		return data.toString();
    }

	// procedimiento para indicar estado completado
	public void completeStatus(String id)
	{
		collection.get().updateOne(Filters.eq("mrId",id),Updates.set("status","Finish"));
	}
	
	// procedimiento para comprobar si un procesamiento ya ha terminado (Finish) o está a medio (cadena vacía)
	public String getStatus(String id) {
		 Optional<DatabaseMapReduce> optionalDb = Optional.ofNullable(collection.get().find(Filters.eq("mrId", id), DatabaseMapReduce.class).first());
		    
		 return optionalDb.map(DatabaseMapReduce::getStatus).orElse(null);
	}
	
	
	
}
