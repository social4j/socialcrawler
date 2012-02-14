package com.socialCrawler.crawler;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class DBDelete {

	public static void main(String[] args) {

		try {
			Mongo m = new Mongo("localhost");
			DB db = m.getDB("twitter");
			DBCollection coll = db.getCollection("tweets");

			// clear records if any
			DBCursor cur = coll.find();
			while (cur.hasNext())
				coll.remove(cur.next());

		}
		catch (UnknownHostException ex) {
			ex.printStackTrace();
		}
		catch (MongoException ex) {
			ex.printStackTrace();
		}

	}
}

