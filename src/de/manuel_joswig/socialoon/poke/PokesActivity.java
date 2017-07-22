package de.manuel_joswig.socialoon.poke;

import java.util.ArrayList;

import android.R.color;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.manuel_joswig.socialoon.R;
import de.manuel_joswig.socialoon.map.MapActivity;
import de.manuel_joswig.socialoon.user.User;
import de.manuel_joswig.socialoon.user.UserHandler;
import de.manuel_joswig.socialoon.user.UserProfileActivity;
import de.manuel_joswig.socialoon.user.UserProfileEditActivity;

/**
 * Lists the users who poked you
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class PokesActivity extends ListActivity {
	private ArrayList<Poke> pokes;
	private ListAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pokes);
		
		// not recommended and only used because asynctask is not working properly
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String userId = appPrefs.getString("userId", "0");
		
		pokes = PokeHandler.getPokes(userId, false);
		
		if (pokes != null) {
			adapter = new PokeAdapter(this, pokes);
			setListAdapter(adapter);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		
		MenuItem pokesMenu = menu.findItem(R.id.mi_pokes);
		pokesMenu.setIcon(R.drawable.btn_pokes_active);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		String prevActivity = (String) this.getIntent().getExtras().getSerializable("last_activity");
		
		switch (item.getItemId()) {
			case R.id.mi_map:
				intent = new Intent(PokesActivity.this, MapActivity.class);
				intent.putExtra("last_activity", "PokesActivity");
				
				if (prevActivity.equals("MapActivity")) {
					finish();
					super.onBackPressed();
				}
				else {
					startActivity(intent);
				}
				
				return true;
				
			case R.id.mi_profile:
				intent = new Intent(PokesActivity.this, UserProfileEditActivity.class);
				intent.putExtra("last_activity", "PokesActivity");
				
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
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		PokeHandler.readPoke(pokes.get(position).getId());
		l.getChildAt(position).setBackgroundColor(getResources().getColor(android.R.color.transparent));
		
		final String pokeUserId = pokes.get(position).getUserId();
		Intent intent = new Intent(PokesActivity.this, UserProfileActivity.class);
		intent.putExtra("last_activity", "PokesActivity");
		intent.putExtra("profile_id", pokeUserId);
		intent.putExtra("balloon_id", "0");
				
		startActivity(intent);
	}
}
