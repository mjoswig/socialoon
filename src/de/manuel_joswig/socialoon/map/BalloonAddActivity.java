package de.manuel_joswig.socialoon.map;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.manuel_joswig.socialoon.R;
import de.manuel_joswig.socialoon.util.GuiToolkit;
import de.manuel_joswig.socialoon.util.HttpReader;
import de.manuel_joswig.socialoon.util.NonFocusingScrollView;

public class BalloonAddActivity extends Activity {
	private static int categoryId = 1;
	private static TimePicker tpMeetupStart = null;
	private static AutoCompleteTextView actvMeetupLoc = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_balloon_add);
		
		// not recommended and only used because asynctask is not working properly
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		tpMeetupStart = (TimePicker) findViewById(R.id.tp_meetup_start);
		// tpMeetupStart.setIs24HourView(true);
		
		actvMeetupLoc = (AutoCompleteTextView) findViewById(R.id.actv_meetup_location);
		actvMeetupLoc.setAdapter(new LocationSuggestionAdapter(this, actvMeetupLoc.getText().toString()));
	}
	
	public void onRadioButtonClicked(View v) {
		boolean checked = ((RadioButton) v).isChecked();
		ImageView ivBalloon = (ImageView) findViewById(R.id.iv_balloon);
		
		switch (v.getId()) {
			case R.id.rb_blue:
				if (checked) {
					categoryId = 1;
					ivBalloon.setImageResource(R.drawable.balloon_blue_big);
				}
				break;
				
			case R.id.rb_red:
				if (checked) {
					categoryId = 2;
					ivBalloon.setImageResource(R.drawable.balloon_red_big);
				}
				break;
				
			/* case R.id.rb_green:
				if (checked) {
					categoryId = 3;
					ivBalloon.setImageResource(R.drawable.balloon_green_big);
				}
				break; */
				
			default:
				break;
		}
	}
	
	@TargetApi(23)
	public void createMeetup(View v) {
		// get creator's user id
		SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String userId = appPrefs.getString("userId", "0");
		
		// get title
		EditText etMeetupTitle = (EditText) findViewById(R.id.et_meetup_title);
		String meetupTitle = etMeetupTitle.getText().toString();
		
		// get meeting date
		DatePicker dpMeetupStart = (DatePicker) findViewById(R.id.dp_meetup_start);
		int day = dpMeetupStart.getDayOfMonth();
		int month = dpMeetupStart.getMonth();
		int year = dpMeetupStart.getYear() - 1900;
		
		// get meeting time
		int hour = tpMeetupStart.getCurrentHour();
		int minute = tpMeetupStart.getCurrentMinute();
		if (Build.VERSION.SDK_INT >= 23) {
			hour = tpMeetupStart.getHour();
			minute = tpMeetupStart.getMinute();
		}
		
		// get description
		EditText etMeetupDesc = (EditText) findViewById(R.id.et_meetup_desc);
		String meetupDesc = etMeetupDesc.getText().toString();
		
		// get meeting point
		String meetupLoc = actvMeetupLoc.getText().toString();
		
		GuiToolkit.markEditTextAsError(etMeetupTitle, false);
		GuiToolkit.markEditTextAsError(actvMeetupLoc, false);
		GuiToolkit.markEditTextAsError(etMeetupDesc, false);
		
		if (GuiToolkit.isEditTextEmpty(etMeetupTitle) || GuiToolkit.isEditTextEmpty(actvMeetupLoc) || GuiToolkit.isEditTextEmpty(etMeetupDesc)) {
			if (GuiToolkit.isEditTextEmpty(etMeetupTitle)) GuiToolkit.markEditTextAsError(etMeetupTitle, true);
			if (GuiToolkit.isEditTextEmpty(actvMeetupLoc)) GuiToolkit.markEditTextAsError(actvMeetupLoc, true);
			if (GuiToolkit.isEditTextEmpty(etMeetupDesc)) GuiToolkit.markEditTextAsError(etMeetupDesc, true);
			
			Toast.makeText(getApplicationContext(), getString(R.string.error_complete_all), Toast.LENGTH_SHORT).show();
		}
		else {
			try {
				meetupLoc = URLEncoder.encode(meetupLoc, "UTF-8");
			} catch (UnsupportedEncodingException e) { }
			
			String getPlaceUrl = "http://photon.komoot.de/api/?q=" + meetupLoc + "&limit=1";
			String getPlaceResponse = new HttpReader(getPlaceUrl).getResponse();
			
			try {
				JSONObject placeData = new JSONObject(getPlaceResponse);
				JSONArray feature = placeData.getJSONArray("features");
				
				Date creationDate = new Date();
				Date meetupDate = new Date();
				meetupDate.setDate(day);
				meetupDate.setMonth(month);
				meetupDate.setYear(year);
				meetupDate.setHours(hour);
				meetupDate.setMinutes(minute);
				
				double timeDiff = meetupDate.getTime() - creationDate.getTime();
				
				// meetup must be in near future (within one month)
				if (timeDiff > 0 && timeDiff < 2.419e+9) {
					// percentage signs are there to not have to use urlencode for http post later on
					String creationTimestamp = new SimpleDateFormat("yyyy-MM-dd%HH:mm:ss").format(creationDate);
					String startTimestamp = new SimpleDateFormat("yyyy-MM-dd%HH:mm:ss").format(meetupDate);
					
					if (feature != null && feature.length() > 0) {
						// get gps data of meeting point via photon api
						JSONArray coordinates = feature.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
						
						JSONObject balloonData = new JSONObject();
						
						try {
							// get a random location nearby to prevent duplicate balloons at the same place 
							double[] nearbyLoc = getNearbyRandLocation(Double.parseDouble(coordinates.get(1).toString()), Double.parseDouble(coordinates.get(0).toString()), 100);
							String latitude = String.valueOf(nearbyLoc[0]);
							String longitude = String.valueOf(nearbyLoc[1]);
							
							balloonData.put("category_id", Integer.toString(categoryId));
							balloonData.put("user_id", userId);
							balloonData.put("creation_time", creationTimestamp);
							balloonData.put("meetup_start", startTimestamp);
							balloonData.put("meetup_end", startTimestamp);
							balloonData.put("gps_latitude", latitude);
							balloonData.put("gps_longitude", longitude);
							balloonData.put("title", meetupTitle);
							balloonData.put("description", meetupDesc);
							balloonData.put("is_approved", "1");
							
							Balloon socialoon = new Balloon(balloonData);
							BalloonHandler.addBalloon(socialoon);
							
							Intent intent = new Intent(BalloonAddActivity.this, MapActivity.class);
							intent.putExtra("last_activity", "BalloonAddActivity");
							intent.putExtra("gps_latitude", latitude);
							intent.putExtra("gps_longitude", longitude);
							startActivity(intent);
						} catch (JSONException e) {
							Log.e("SOCIALOON_APP", "Could not create json object!");
						}
					}
					else {
						GuiToolkit.markEditTextAsError(actvMeetupLoc, true);
						Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_location), Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_date), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				Log.e("SOCIALOON_APP", "Could not parse json data!");
			}
		}
	}
	
	private static double[] getNearbyRandLocation(double x0, double y0, int radius) {
	    Random random = new Random();

	    // Convert radius from meters to degrees
	    double radiusInDegrees = radius / 111000f;

	    double u = random.nextDouble();
	    double v = random.nextDouble();
	    double w = radiusInDegrees * Math.sqrt(u);
	    double t = 2 * Math.PI * v;
	    double x = w * Math.cos(t);
	    double y = w * Math.sin(t);

	    // Adjust the x-coordinate for the shrinking of the east-west distances
	    double new_x = x / Math.cos(Math.toRadians(y0));

	    double[] foundCoords = {new_x + x0, y + y0};
	    
	    return foundCoords;
	}
}