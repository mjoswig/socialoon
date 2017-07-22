package de.manuel_joswig.socialoon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.manuel_joswig.socialoon.map.MapActivity;
import de.manuel_joswig.socialoon.user.User;
import de.manuel_joswig.socialoon.user.UserHandler;

/**
 * Main activity (login handling)
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		final SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isLoggedIn = (!appPrefs.getString("userId", "0").equals("0")) ? true : false;
		
		if (isLoggedIn) {	
			renderMapView();
		}
		else {	
			final Button btnLogin = (Button) findViewById(R.id.btn_login);
			final EditText etEmail = (EditText) findViewById(R.id.et_email);
			final EditText etPassword = (EditText) findViewById(R.id.et_password);
			final String incorrectLogin = getString(R.string.error_incorrect_login);
			
			btnLogin.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String email = etEmail.getText().toString();
					String password = etPassword.getText().toString();
					
					// not recommended and only used because asynctask is not working properly
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			        StrictMode.setThreadPolicy(policy);
			        
			        User userObject = UserHandler.getUserByLogin(email, password);
					
					if (userObject != null) {
						etEmail.setBackgroundColor(Color.rgb(217, 217, 217));
						etPassword.setBackgroundColor(Color.rgb(217, 217, 217));
						
						SharedPreferences.Editor editor = appPrefs.edit();
						editor.putString("userId", userObject.getId());
						editor.commit();
						
						renderMapView();
					}
					else {
						// wrong combination of email & password => try again
						etEmail.setBackgroundColor(Color.rgb(255, 204, 204));
						etPassword.setBackgroundColor(Color.rgb(255, 204, 204));
						
						Toast.makeText(getApplicationContext(), incorrectLogin, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
	
	public void renderSignupView(View v) {
		startActivity(new Intent(MainActivity.this, SignupActivity.class));
	}
	
	@Override
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	private void renderMapView() {
		Intent intent = new Intent(MainActivity.this, SplashActivity.class);
		intent.putExtra("last_activity", "MainActivity");
		startActivity(intent);
	}
}
