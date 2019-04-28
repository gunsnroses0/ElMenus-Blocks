package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import Commands.Command;

public class Block {

	private static final String COLLECTION_NAME = "blocks";

	private static MongoCollection<Document> collection = null;
	static String host = System.getenv("MONGO_URI");

	private static int DbPoolCount = 4;
	public static int getDbPoolCount() {
		return DbPoolCount;
	}
	public static void setDbPoolCount(int dbPoolCount) {
		DbPoolCount = dbPoolCount;
	}

	static MongoClientOptions.Builder options = null;
	static MongoClientURI uri = null;
	static MongoClient mongoClient = null; 
	
	public static void initializeDb() {
		options = MongoClientOptions.builder()
				.connectionsPerHost(DbPoolCount);
		uri = new MongoClientURI(
				host,options);
		mongoClient = new MongoClient(uri);
			
	}
	public static HashMap<String, Object> create(HashMap<String, Object> atrributes) throws ParseException {

		MongoDatabase database = mongoClient.getDatabase("El-Menus");


		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("blocks");
		Document newBlock = new Document();

		for (String key : atrributes.keySet()) {
			newBlock.append(key, atrributes.get(key));
		}
		collection.insertOne(newBlock);

		JSONParser parser = new JSONParser();
		HashMap<String, Object> returnValue = Command.jsonToMap((JSONObject) parser.parse(newBlock.toJson()));
		return returnValue;
	}
	
	public static ArrayList<HashMap<String, Object>> get(String blockId) {

		MongoDatabase database = mongoClient.getDatabase("El-Menus");


		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("blocks");
		BasicDBObject query = new BasicDBObject();
		query.put("blocker", blockId);

		FindIterable<Document> docs = collection.find(query);
		JSONParser parser = new JSONParser(); 
		ArrayList<HashMap<String, Object>> blocks = new ArrayList<HashMap<String, Object>>();

		for (Document document : docs) {
			JSONObject json;
			try {
				json = (JSONObject) parser.parse(document.toJson());
				HashMap<String, Object> block = Command.jsonToMap(json);	
				blocks.add(block);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        return blocks;
		
	}
}
