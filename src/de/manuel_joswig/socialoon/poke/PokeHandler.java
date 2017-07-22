package de.manuel_joswig.socialoon.poke;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import de.manuel_joswig.socialoon.map.Balloon;
import de.manuel_joswig.socialoon.util.HttpReader;

/**
 * Handles all poke-related actions
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class PokeHandler {
	public PokeHandler() { }
	
	public static boolean isAllowedToPokeTarget(String userId, String balloonId) {
		String getPokesUrl = "http://manuel-joswig.de/socialoon/api/get_pokes.php?user_id=" + userId + "&balloon_id=" + balloonId;
		String getPokesResponse = new HttpReader(getPokesUrl).getResponse();
		
        return (getPokesResponse.equals("false"));
	}
	
	public static ArrayList<Poke> getPokes(String targetId, boolean excludeRead) {
		String exclRead = (excludeRead) ? "1" : "0";
		String getPokesUrl = "http://manuel-joswig.de/socialoon/api/get_pokes.php?target_id=" + targetId + "&exclude_read=" + exclRead;
		String getPokesResponse = new HttpReader(getPokesUrl).getResponse();
		
        try {
        	ArrayList<Poke> pokes = new ArrayList<Poke>();
        	JSONObject getPokesData = new JSONObject(getPokesResponse);
        	
        	for (int i = 0; i < getPokesData.length(); i++) {
        		pokes.add(new Poke(getPokesData.getJSONObject(Integer.toString(i))));
        	}
        	
        	if (getPokesData != null) return pokes;
        } catch (JSONException e) {
			Log.e("SOCIALOON_APP", "Could not parse json data!");
		}
        
		return null;
	}
	
	public static void readPoke(String pokeId) {
		String readPokeUrl = "http://manuel-joswig.de/socialoon/api/read_poke.php?id=" + pokeId;
		String readPokeResponse = new HttpReader(readPokeUrl).getResponse();
	}
	
	public static void addPoke(Poke pokeObject) {
		String userId = pokeObject.getUserId();
		String targetId = pokeObject.getTargetId();
		String balloonId = pokeObject.getBalloonId();
		
		String addPokeUrl = "http://manuel-joswig.de/socialoon/api/add_poke.php?user_id=" + userId + "&target_id=" + targetId + "&balloon_id=" + balloonId;
		String addPokeResponse = new HttpReader(addPokeUrl).getResponse();
	}
}
