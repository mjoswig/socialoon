package de.manuel_joswig.socialoon.map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Represents a balloon object
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class Balloon {
	private String id, categoryId, userId, creationTime, meetupStart, meetupEnd, latitude, longitude, title, description, isApproved;
	
	public Balloon(JSONObject dataset) {
		try {
			if (dataset.has("id")) {
				String jsonId = dataset.getString("id");
				
				if (!jsonId.equals("null") && !jsonId.equals("0")) {
					id = jsonId;
				}
			}
			
        	categoryId = dataset.getString("category_id");
        	userId = dataset.getString("user_id");
        	creationTime = dataset.getString("creation_time");
        	meetupStart = dataset.getString("meetup_start");
        	meetupEnd = dataset.getString("meetup_end");
        	latitude = dataset.getString("gps_latitude");
        	longitude = dataset.getString("gps_longitude");
        	title = dataset.getString("title");
        	description = dataset.getString("description");
        	isApproved = dataset.getString("is_approved");
        } catch (JSONException e) {
			Log.e("SOCIALOON_APP", "Could not parse json data!");
		}
	}
	
	public String getId() {
		return id;
	}
	
	public String getCategoryId() {
		return categoryId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getCreationTime() {
		return creationTime;
	}
	
	public String getMeetupStart() {
		return meetupStart;
	}
	
	public String getMeetupEnd() {
		return meetupEnd;
	}
	
	public String getGpsLatitude() {
		return latitude;
	}
	
	public String getGpsLongitude() {
		return longitude;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isApproved() {
		return (isApproved.equals("1"));
	}
}
