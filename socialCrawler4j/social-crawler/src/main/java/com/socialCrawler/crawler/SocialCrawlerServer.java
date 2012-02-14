package com.socialCrawler.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.httpclient.HttpStatus;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class SocialCrawlerServer {

	private final static RetryMechanism retryForNetworkError = new RetryMechanism(250, 16000, RetryMechanism.RETRY_TYPE.linear);
	private final static RetryMechanism retryForServerError = new RetryMechanism(10000, 240000, RetryMechanism.RETRY_TYPE.exponential);
	private static DBAccess mongo = null;
	public static void main(String[] args) {

		
		try {
			mongo = DBAccess.getInstance();
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (MongoException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} //To Do handle the DB connection exceptions

		OAuthConsumer consumer = new DefaultOAuthConsumer(
				"eC3mHUgSiqpHU5jO7IgqKQ",
				"JpCaPgJbKt51ivoNEdvpJK5jmT9Nv9OcOvCZ7uR4Go");
		consumer.setTokenWithSecret("395165191-8vjy1MhjnoJjuroCWPnQ9FhFD9sdfvCeGTJlNgZl",
				"EteOYj7g4zWskrzdTARBD474aOdBseQ53m1wnn7ovgc");

		// create a request that requires authentication
		try{
			URL url = null;

			url = new URL("https://stream.twitter.com/1/statuses/sample.json");

			HttpURLConnection request = null;
			request = (HttpURLConnection) url.openConnection();

			request.setReadTimeout(60000);
			request.setConnectTimeout(60000);

			// sign the request
			consumer.sign(request);

			// send the request
			while(true){
				int statusCode = 0;
				try {
					request.connect();

					// response status should be 200 OK
					statusCode = request.getResponseCode();
					System.out.println("The status code is "+statusCode);
					if(statusCode == HttpStatus.SC_OK){
						InputStream is = request.getInputStream();
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader br = new BufferedReader(isr);
						String str = null;
						while((str = br.readLine() )!= null){
							System.out.println("The tweet is "+str);
							// convert JSON to DBObject directly
							DBObject dbObject = (DBObject) JSON
									.parse(str);
							mongo.getCollection().insert(dbObject);
						}
					}
				} catch (IOException e) {
					System.out.println("Error while reading data "+e);
					//Back of linearly
					try {
						retryForNetworkError.BackOff();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}finally {
					//Close the connection
					request.disconnect();
					if(statusCode > 200){
						//Backoff exponentially
						try {
							retryForServerError.BackOff();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(MalformedURLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
