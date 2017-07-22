package de.manuel_joswig.socialoon.user;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import de.manuel_joswig.socialoon.R;
import de.manuel_joswig.socialoon.map.BalloonAddActivity;
import de.manuel_joswig.socialoon.map.MapActivity;
import de.manuel_joswig.socialoon.poke.Poke;
import de.manuel_joswig.socialoon.poke.PokeHandler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileActivity extends Activity {
	private String userId, profileId, balloonId;
	private User user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_profile);
		
		// not recommended and only used because asynctask is not working properly
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		// get user id
        SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		userId = appPrefs.getString("userId", "0");
		
		Bundle extras = this.getIntent().getExtras();
		
		if (extras != null) {
			profileId = (String) extras.getSerializable("profile_id");
			balloonId = (String) extras.getSerializable("balloon_id");
			user = UserHandler.getUserById(profileId);
			
			ImageView ivAvatar = (ImageView) findViewById(R.id.iv_profile_avatar);
			try {
				ivAvatar.setImageDrawable(user.getAvatar());
			} catch (IOException e) { }
			
			ImageView ivPoke = (ImageView) findViewById(R.id.iv_poke);
			if (!userId.equals(profileId) && !balloonId.equals("0") && PokeHandler.isAllowedToPokeTarget(userId, balloonId)) {
				ivPoke.setVisibility(View.VISIBLE);
			}
			
			TextView tvUsername = (TextView) findViewById(R.id.tv_profile_username);
			tvUsername.setText(user.getUsername());
			
			ImageView ivVerified = (ImageView) findViewById(R.id.iv_verified);
			if (user.isVerified()) ivVerified.setVisibility(View.VISIBLE);
			
			TextView tvBiography = (TextView) findViewById(R.id.tv_biography);
			tvBiography.setText(user.getBiography());
		}
	}
	
	public void poke(View v) {
		v.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.ib_balloon_add_click));		
		
		JSONObject pokeData = new JSONObject();
		
		try {
			pokeData.put("user_id", userId);
			pokeData.put("target_id", profileId);
			pokeData.put("balloon_id", balloonId);
		}
		catch (JSONException e) {
			Log.e("SOCIALOON_APP", "Could not create json object!");
		}
		
		Poke p = new Poke(pokeData);
		PokeHandler.addPoke(p);
		
		v.setVisibility(View.GONE);
	}
}
