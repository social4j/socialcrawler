package com.socialCrawler.crawler;
import java.net.UnknownHostException;

import com.mongodb.*;

public class DBAccess {
	
	private static Mongo mongo = null;
	private static DB db = null;
	private static DBCollection coll = null;
	private static DBAccess instance = null;
	
	private DBAccess() throws UnknownHostException, MongoException{
		mongo = new Mongo("localhost", 27017); //To Do : read the port from config
		db = mongo.getDB("twitter"); //To Do : read the db name from config
		coll = db.getCollection("tweet"); //To Do : read the collection name from config
	}
	
	public static synchronized DBAccess getInstance() throws UnknownHostException, MongoException{
		if(instance == null){
			instance = new DBAccess();
		}
		return instance;
	}
	
	public DB getDB(){
		return db;
	}
	
	public DBCollection getCollection(){
		return coll;
	}
}

