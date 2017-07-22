package de.manuel_joswig.socialoon.map;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.manuel_joswig.socialoon.R;
import de.manuel_joswig.socialoon.util.HttpReader;

/**
 * Manages balloon-related actions
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class BalloonHandler {
	public BalloonHandler() { }
	
	public static Balloon getLastBalloonByUserId(String userId) {
		return null;
	}
	
	public static ArrayList<Balloon> getBalloons(String userId) {
		String getBalloonsUrl = "http://manuel-joswig.de/socialoon/api/get_balloon.php?by_priority=1&user_id=" + userId;
		String getBalloonsResponse = new HttpReader(getBalloonsUrl).getResponse();
		
        try {
        	ArrayList<Balloon> balloons = new ArrayList<Balloon>();
        	JSONObject getBalloonsData = new JSONObject(getBalloonsResponse);
        	
        	for (int i = 0; i < getBalloonsData.length(); i++) {
        		balloons.add(new Balloon(getBalloonsData.getJSONObject(Integer.toString(i))));
        	}
        	
        	if (getBalloonsData != null) return balloons;
        } catch (JSONException e) {
			Log.e("SOCIALOON_APP", "Could not parse json data!");
		}
        
		return null;
	}
	
	public static void addBalloon(Balloon balloonObject) {
		String catId = balloonObject.getCategoryId();
		String userId = balloonObject.getUserId();
		String creationTime = "", meetupStart = "", meetupEnd = "";
		String latitude = balloonObject.getGpsLatitude();
		String longitude = balloonObject.getGpsLongitude();
		String title = "", desc = "";
		String isApproved = (balloonObject.isApproved()) ? "1" : "0";
		
		try {
			title = URLEncoder.encode(balloonObject.getTitle(), "UTF-8");
			creationTime = URLEncoder.encode(balloonObject.getCreationTime(), "UTF-8");;
			meetupStart = URLEncoder.encode(balloonObject.getMeetupStart(), "UTF-8");
			meetupEnd = URLEncoder.encode(balloonObject.getMeetupEnd(), "UTF-8");
			desc = URLEncoder.encode(balloonObject.getDescription(), "UTF-8");
		} catch (UnsupportedEncodingException e) { }
		
		String addBalloonUrl = "http://manuel-joswig.de/socialoon/api/add_balloon.php?category_id=" + catId + "&user_id=" + userId + "&creation_time=" + creationTime + "&meetup_start=" + meetupStart + "&meetup_end=" + meetupEnd + "&gps_latitude=" + latitude + "&gps_longitude=" + longitude + "&title=" + title + "&description=" + desc + "&is_approved=" + isApproved;
		String addBalloonResponse = new HttpReader(addBalloonUrl).getResponse();
	}
	
	public static void removeBalloon(String balloonId) {
		String removeBalloonUrl = "http://manuel-joswig.de/socialoon/api/remove_balloon.php?id=" + balloonId;
		String removeBalloonResponse = new HttpReader(removeBalloonUrl).getResponse();
	}
}
