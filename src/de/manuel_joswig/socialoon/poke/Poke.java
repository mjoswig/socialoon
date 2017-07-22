package de.manuel_joswig.socialoon.poke;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Represents a poke object
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class Poke {
	private String id, userId, targetId, balloonId, creationTime, isRead;
	
	public Poke(JSONObject dataset) {
		try {
			if (dataset.has("id")) {
				String jsonId = dataset.getString("id");
				
				if (!jsonId.equals("null") && !jsonId.equals("0")) {
					id = jsonId;
				}
			}
			
        	userId = dataset.getString("user_id");
        	targetId = dataset.getString("target_id");
        	balloonId = dataset.getString("balloon_id");
        	creationTime = dataset.getString("creation_time");
        	isRead = dataset.getString("is_read");
        } catch (JSONException e) {
			Log.e("SOCIALOON_APP", "Could not parse json data!");
		}
	}
	
	public String getId() {
		return id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getTargetId() {
		return targetId;
	}
	
	public String getBalloonId() {
		return balloonId;
	}
	
	public String getCreationTime() {
		return creationTime;
	}
	
	public boolean isRead() {
		return (isRead.equals("1"));
	}
	
	public void setRead() {
		isRead = "1";
	}
}
