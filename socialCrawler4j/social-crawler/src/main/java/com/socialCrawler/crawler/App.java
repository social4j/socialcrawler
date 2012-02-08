package com.socialCrawler.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;


public class App {

	public static final String CONSUMER_KEY = "";
	public static final String CONSUMER_SECRET = "";
	public static void main(String[] args) throws IOException{
		//Load the authentication file
		

		System.out.print("Enter uri: ");
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
		String uri = consoleReader.readLine();
		System.out.print("Enter METHOD: ");
		String METHOD = consoleReader.readLine().toUpperCase();
		String endpoint = uri;

		HttpURLConnection request = null;
		BufferedReader rd = null;
		StringBuilder response = null;

		try{
			URL endpointUrl = new URL(endpoint);
			request = (HttpURLConnection)endpointUrl.openConnection();
			request.setRequestMethod(METHOD);

			try{
				OAuthConsumer consumer = new DefaultOAuthConsumer(
						CONSUMER_KEY, CONSUMER_SECRET);
				consumer.setTokenWithSecret("", "");
				consumer.sign(request);
			} catch(OAuthMessageSignerException ex){
				System.out.println("OAuth Signing failed - " + ex.getMessage());
			} catch(OAuthExpectationFailedException ex){
				System.out.println("OAuth failed - " + ex.getMessage());
			}

			request.connect();

			rd  = new BufferedReader(new InputStreamReader(request.getInputStream()));
			response = new StringBuilder();
			String line = null;
			while ((line = rd.readLine()) != null){
				response.append(line + '\n');
			}
		} catch (MalformedURLException e) {
			System.out.println("Exception: " + e.getMessage());
			//e.printStackTrace();
		} catch (ProtocolException e) {
			System.out.println("Exception: " + e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
			//e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			//e.printStackTrace();
		} finally {
			try{
				request.disconnect();
			} catch(Exception e){}

			if(rd != null){
				try{
					rd.close();
				} catch(IOException ex){}
				rd = null;
			}
		}

		System.out.println( (response != null) ? response.toString() : "No Response");
	}
}


