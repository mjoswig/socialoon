package de.manuel_joswig.socialoon.map;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import de.manuel_joswig.socialoon.MainActivity;
import de.manuel_joswig.socialoon.R;
import de.manuel_joswig.socialoon.map.BalloonHandler;
import de.manuel_joswig.socialoon.poke.Poke;
import de.manuel_joswig.socialoon.poke.PokeHandler;
import de.manuel_joswig.socialoon.poke.PokesActivity;
import de.manuel_joswig.socialoon.user.User;
import de.manuel_joswig.socialoon.user.UserHandler;
import de.manuel_joswig.socialoon.user.UserProfileEditActivity;
import de.manuel_joswig.socialoon.util.ExtendedOverlayItem;
import de.manuel_joswig.socialoon.util.ItemizedOverlayWithBubble;

/**
 * Shows the map with the socialoons
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class MapActivity extends Activity implements LocationListener {
	private MapView mapView = null;
	private String userId;
	
	@TargetApi(23) @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.AppTheme);
		
		Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
		setContentView(R.layout.activity_map);
		
		// not recommended and only used because asynctask is not working properly
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        // get user id
        SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		userId = appPrefs.getString("userId", "0");
		
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
            
        final IMapController mapController = mapView.getController();
        mapController.setZoom(12);
            
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        	requestPermissions(new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, 42);
        }
        else {
        	initLocationUpdates();
        }
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 42:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					initLocationUpdates();
				}
				else {
					Toast.makeText(getApplication(), "Permission denied", Toast.LENGTH_SHORT).show();
				}
				
				return;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		invalidateOptionsMenu();
		
		Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
		
		Bundle extras = this.getIntent().getExtras();
		
		if (extras != null) {
			String lastActivity = (String) extras.getSerializable("last_activity");
			
			// zoom to recently added balloon
			if (lastActivity.equals("BalloonAddActivity")) {
				double latitude = Double.parseDouble((String) extras.getSerializable("gps_latitude"));
				double longitude = Double.parseDouble((String) extras.getSerializable("gps_longitude"));
				
				mapView.getController().animateTo((new GeoPoint(latitude, longitude)));
				mapView.getController().setZoom(19);
				
				this.getIntent().putExtra("last_activity", "MapActivity");
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		
		MenuItem mapMenu = menu.findItem(R.id.mi_map);
		mapMenu.setIcon(R.drawable.btn_map_active);
		
		ArrayList<Poke> pokes = PokeHandler.getPokes(userId, true);
		
		if (pokes != null) {
			if (pokes.size() > 0) {
				MenuItem pokesMenu = menu.findItem(R.id.mi_pokes);
				pokesMenu.setIcon(getResources().getDrawable(R.drawable.btn_pokes_new));
			}
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		String prevActivity = (String) this.getIntent().getExtras().getSerializable("last_activity");
		
		switch (item.getItemId()) {
			case R.id.mi_pokes:
				intent = new Intent(MapActivity.this, PokesActivity.class);
				intent.putExtra("last_activity", "MapActivity");
				
				if (prevActivity.equals("PokesActivity")) {
					finish();
					super.onBackPressed();
				}
				else {
					startActivity(intent);
				}
				
				return true;
				
			case R.id.mi_profile:
				intent = new Intent(MapActivity.this, UserProfileEditActivity.class);
				intent.putExtra("last_activity", "MapActivity");
				
				if (prevActivity.equals("UserProfileEditActivity")) {
					finish();
					super.onBackPressed();
				}
				else {
					startActivity(intent);
				}
				
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ib_balloon_add:
				v.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.ib_balloon_add_click));
				startActivity(new Intent(MapActivity.this, BalloonAddActivity.class));
				break;
		}
	}
	
	private void logoutUser() {
		SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(MapActivity.this);
		SharedPreferences.Editor editor = appPrefs.edit();
		editor.putString("userId", "0");
		editor.commit();
		
		Intent intent = new Intent(MapActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void initLocationUpdates() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if (lastKnownLocation != null) {
			double latitude = lastKnownLocation.getLatitude();
			double longitude = lastKnownLocation.getLongitude();
			
			mapView.getController().setCenter(new GeoPoint(latitude, longitude));
			
			User user = UserHandler.getUserById(userId);
			user.setLatitude(String.valueOf(latitude));
			user.setLongitude(String.valueOf(longitude));
			UserHandler.editUser(user, "editLoc");
			
			loadBalloons();
		}
		else {
			// check gps location
			locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
		}
	}
	
	public void loadBalloons() {
		final ArrayList<ExtendedOverlayItem> balloonItems = new ArrayList<ExtendedOverlayItem>();
		ArrayList<Balloon> balloons = BalloonHandler.getBalloons(userId);
		
		// get id of the user that is logged in
     	SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(this);
     	String userId = appPrefs.getString("userId", "0");
		
		if (balloons != null) {
			for (Balloon balloon : balloons) {
				boolean isBalloonOwner = userId.equals(balloon.getUserId());
				ExtendedOverlayItem balloonItem = new ExtendedOverlayItem(balloon.getTitle(), balloon.getDescription(), new GeoPoint(Float.parseFloat(balloon.getGpsLatitude()), Float.parseFloat(balloon.getGpsLongitude())), balloon, isBalloonOwner, this);
	        
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d = null;
				try {
					d = sdf.parse(balloon.getMeetupStart());
				} catch (ParseException e) { }
				sdf.applyPattern("yyyy-MM-dd hh:mm aa");
				balloonItem.setSubDescription(sdf.format(d));
	        
				int balloonColor = R.drawable.balloon_blue;
	        
				switch (Integer.valueOf(balloon.getCategoryId())) {
	        		case 2:
	        			balloonColor = R.drawable.balloon_red;
	        			break;
	        		
	        		/* case 3:
	        			balloonColor = R.drawable.balloon_green;
	        			break; */
	        		
	        		default:
	        			break;
				}
	        
				Drawable myCurrentLocationMarker = getResources().getDrawable(balloonColor);
				Drawable avatar = getResources().getDrawable(R.drawable.default_avatar);
				
				try {
					avatar = UserHandler.getUserById(balloon.getUserId()).getAvatar();
				} catch (IOException e) { }
				
				balloonItem.setImage(avatar);
				balloonItem.setMarker(myCurrentLocationMarker);
	        
				balloonItems.add(balloonItem);
			}
        
			Drawable blueBalloon = getResources().getDrawable(R.drawable.balloon_blue);
			ItemizedOverlayWithBubble<ExtendedOverlayItem> iconOverlay = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(this, balloonItems, blueBalloon, mapView, null, this);
			mapView.getOverlays().add(iconOverlay);
		}
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MapActivity.this);
		exitDialogBuilder.setTitle(R.string.exit_alert_title);
		exitDialogBuilder.setMessage(R.string.exit_alert_message);
		exitDialogBuilder.setNegativeButton(android.R.string.no, null);
		exitDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				logoutUser();
			}
		});
        		
		exitDialogBuilder.create().show();
	}

	@Override
	public void onLocationChanged(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		
		mapView.getController().setCenter(new GeoPoint(latitude, longitude));
		
		User user = UserHandler.getUserById(userId);
		user.setLatitude(String.valueOf(latitude));
		user.setLongitude(String.valueOf(longitude));
		UserHandler.editUser(user, "editLoc");
		
		loadBalloons();
	}

	@Override
	public void onProviderDisabled(String provider) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }
}
