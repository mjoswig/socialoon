package de.manuel_joswig.socialoon.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.manuel_joswig.socialoon.util.HttpReader;

/**
 * Manages user-related actions
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class UserHandler {
	public UserHandler() { }
	
	public static User getUserById(String id) {
		String getUserUrl = "http://manuel-joswig.de/socialoon/api/get_user.php?by_id=" + id;
		String getUserResponse = new HttpReader(getUserUrl).getResponse();
		
        try {
        	JSONObject getUserData = new JSONObject(getUserResponse).getJSONObject("0");
        	
        	if (getUserData != null) return new User(getUserData);
        } catch (JSONException e) {
			Log.e("SOCIALOON_APP", "Could not parse json data!");
		}
        
		return null;
	}
	
	public static User getUserByAlias(String alias) {
		String getUserUrl = "http://manuel-joswig.de/socialoon/api/get_user.php?by_alias=" + alias;
		String getUserResponse = new HttpReader(getUserUrl).getResponse();
		
        try {
        	JSONObject getUserData = new JSONObject(getUserResponse).getJSONObject("0");
        	
        	if (getUserData != null) return new User(getUserData);
        } catch (JSONException e) {
			Log.e("SOCIALOON_APP", "Could not parse json data!");
		}
        
		return null;
	}
	
	public static User getUserByLogin(String email, String password) {
		String getUserUrl = "http://manuel-joswig.de/socialoon/api/get_user.php?email=" + email + "&password=" + password;
		String getUserResponse = new HttpReader(getUserUrl).getResponse();
		
        try {
        	JSONObject getUserData = new JSONObject(getUserResponse).getJSONObject("0");
        	
        	if (getUserData != null) return new User(getUserData);
        } catch (JSONException e) {
			Log.e("SOCIALOON_APP", "Could not parse json data!");
		}
        
		return null;
	}
	
	public static void addUser(User userObject) {
		String username = "", password = "", email = "";
		
		try {
			username = URLEncoder.encode(userObject.getUsername(), "UTF-8");
			password = URLEncoder.encode(userObject.getPassword(), "UTF-8");
			email = URLEncoder.encode(userObject.getEmail(), "UTF-8");
		} catch (UnsupportedEncodingException e) { }
		
		String addUserUrl = "http://manuel-joswig.de/socialoon/api/add_user.php?username=" + username + "&password=" + password + "&email=" + email;
		String addUserResponse = new HttpReader(addUserUrl).getResponse();
	}
	
	public static void editUser(User userObject, String action) {
		String id = userObject.getId();
		
		String editUserUrl = "http://manuel-joswig.de/socialoon/api/edit_user.php?id=" + id + "&action=" + action;
		
		if (action.equals("editBio")) {
			String biography = "";
			
			try {
				biography = URLEncoder.encode(userObject.getBiography(), "UTF-8");
			} catch (UnsupportedEncodingException e) { }
			
			editUserUrl += ("&biography=" + biography);
		}
		else if (action.equals("editLoc")) {
			String latitude = userObject.getLatitude();
			String longitude = userObject.getLongitude();
			
			editUserUrl += ("&location_latitude=" + latitude + "&location_longitude=" + longitude);
		}
		
		String editUserResponse = new HttpReader(editUserUrl).getResponse();
	}
}
