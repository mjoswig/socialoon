package de.manuel_joswig.socialoon.map;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import de.manuel_joswig.socialoon.util.HttpReader;

public class LocationSuggestionAdapter extends ArrayAdapter<String> {
	protected static final String TAG = "LocationSuggestionAdapter";
	
	private List<String> suggestions;
	
	public LocationSuggestionAdapter(Activity context, String locationFilter) {
		super(context, android.R.layout.simple_dropdown_item_1line);
		suggestions = new ArrayList<String>();
	}
	
	@Override
	public int getCount() {
		return suggestions.size();
	}
	
	@Override
	public String getItem(int index) {
		return suggestions.get(index);
	}
	
	@Override
	public Filter getFilter() {
		Filter myFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				
				if (constraint != null) {
					try {
						constraint = URLEncoder.encode(constraint.toString(), "UTF-8");
					} catch (UnsupportedEncodingException e) { }
					
					String getLocationsUrl = "http://photon.komoot.de/api/?q=" + constraint + "&limit=5";
					String getLocationsResponse = new HttpReader(getLocationsUrl).getResponse();
					
					try {
						JSONObject locationsData = new JSONObject(getLocationsResponse);
						JSONArray features = locationsData.getJSONArray("features");
						
						List<String> newSuggestions = new ArrayList<String>();
						
						for (int i = 0; i < features.length(); i++) {
							String name = features.getJSONObject(i).getJSONObject("properties").getString("name");
							String city = features.getJSONObject(i).getJSONObject("properties").getString("city");
							String country = features.getJSONObject(i).getJSONObject("properties").getString("country");
							
							String locString = name + " (" + city + ", " + country + ")";
							
							newSuggestions.add(locString);
						}
						
						suggestions.clear();
						
						for (int i = 0; i < newSuggestions.size(); i++) {
							suggestions.add(newSuggestions.get(i));
						}
						
						filterResults.values = suggestions;
						filterResults.count = suggestions.size();
					} catch (JSONException e) {
						Log.e("SOCIALOON_APP", "Could not parse json data!");
					}
				}
				
				return filterResults;
			}
			
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				}
				else {
					notifyDataSetInvalidated();
				}
			}
		};
		
		return myFilter;
	}
}
