package de.manuel_joswig.socialoon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * Tool for sending a post request and receiving the html
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class HttpReader {
	private String url = "";
	
	public HttpReader(String url) {
		this.url = url;
	}
	
	public String getURL() {
		return url;
	}
	
	public String getResponse() {
		URL urlObj = null;
		HttpURLConnection connection = null;
		
		// http request
		try {
			urlObj = new URL(url);
			
			connection = (HttpURLConnection) urlObj.openConnection();
			connection.setReadTimeout(10000);
			connection.setConnectTimeout(15000);
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
		} catch (Exception e) {
			Log.e("SOCIALOON_APP", "Could not connect to web server!");
		}
		
		// read stream
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlObj.openStream()));
			StringBuffer buffer = new StringBuffer();
			char[] chars = new char[1024];
			int read = 0;
			
			while ((read = reader.read(chars)) != -1) {
    			buffer.append(chars, 0, read); 
			}
			
			reader.close();
			
			return buffer.toString();
		} catch (IOException e) {
			Log.e("SOCIALOON_APP", "Could not open stream!");
		}

		return null;
	}
	
	public static boolean exists(String URLName){
		try {
			HttpURLConnection.setFollowRedirects(false);
			
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
	  }
}
