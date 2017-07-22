package de.manuel_joswig.socialoon.user;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import de.manuel_joswig.socialoon.R;
import de.manuel_joswig.socialoon.map.MapActivity;
import de.manuel_joswig.socialoon.poke.Poke;
import de.manuel_joswig.socialoon.poke.PokeHandler;
import de.manuel_joswig.socialoon.poke.PokesActivity;
import de.manuel_joswig.socialoon.util.HttpReader;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileEditActivity extends Activity {
	private static final int RESULT_LOAD_IMAGE = 1;
	
	// Storage Permissions
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
	        Manifest.permission.READ_EXTERNAL_STORAGE,
	        Manifest.permission.WRITE_EXTERNAL_STORAGE
	};
	
	private static ImageView ivAvatar;
	private static User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_edit);
		
		// not recommended and only used because asynctask is not working properly
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(this);
     	user = UserHandler.getUserById(appPrefs.getString("userId", "0"));
		
		ivAvatar = (ImageView) findViewById(R.id.iv_profile_avatar);
		try {
			ivAvatar.setImageDrawable(user.getAvatar());
		} catch (IOException e) { }
		
		TextView tvProfileUsername = (TextView) findViewById(R.id.tv_profile_username);
		tvProfileUsername.setText(user.getUsername());
		
		ImageView ivVerified = (ImageView) findViewById(R.id.iv_verified);
		if (user.isVerified()) ivVerified.setVisibility(View.VISIBLE);
		
		final EditText etBiography = (EditText) findViewById(R.id.et_biography);
		etBiography.setText(user.getBiography());
		etBiography.setSelection(etBiography.getText().length());
		
		Button btnSave = (Button) findViewById(R.id.btn_profile_save);
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (HttpReader.exists("http://manuel-joswig.de/socialoon/avatar/" + user.getId() + ".png")) {

					try {
						HttpURLConnection httpUrlConnection;
						
						try {
							httpUrlConnection = (HttpURLConnection) new URL("http://manuel-joswig.de/socialoon/avatar/upload.php?filename=" + user.getId() +  ".png").openConnection();
							httpUrlConnection.setDoOutput(true);
							httpUrlConnection.setRequestMethod("POST");
						
							OutputStream os;
							
							try {
								os = httpUrlConnection.getOutputStream();
								File avaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
						     	File avaFile = new File(avaDir, user.getId() + ".png");
								long totalBytes = avaFile.length();
								BufferedInputStream fis;
								
								try {
									fis = new BufferedInputStream(new FileInputStream(avaFile));
								
									for (int i = 0; i < totalBytes; i++) {
						            	os.write(fis.read());
						        	}
								
									BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
						        	String s = null;
						        
						        	while ((s = in.readLine()) != null) {
						            	System.out.println(s);
						        	}
						        
						        	in.close();
						        	fis.close();
								} catch (FileNotFoundException e) { }
							} catch (IOException e1) { }
						} catch (ProtocolException e1) { }
					} catch (MalformedURLException e2) { } catch (IOException e2) { }
				}
				
				user.setBiography(etBiography.getText().toString());
				UserHandler.editUser(user, "editBio");
				
				Toast.makeText(getApplicationContext(), getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		
		MenuItem profileEditMenu = menu.findItem(R.id.mi_profile);
		profileEditMenu.setIcon(R.drawable.btn_profile_active);
		
		ArrayList<Poke> pokes = PokeHandler.getPokes(user.getId(), true);
		
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
			case R.id.mi_map:
				intent = new Intent(UserProfileEditActivity.this, MapActivity.class);
				intent.putExtra("last_activity", "UserProfileEditActivity");
				
				if (prevActivity.equals("MapActivity")) {
					finish();
					super.onBackPressed();
				}
				else {
					startActivity(intent);
				}
				
				return true;
				
			case R.id.mi_pokes:
				intent = new Intent(UserProfileEditActivity.this, PokesActivity.class);
				intent.putExtra("last_activity", "UserProfileEditActivity");
				
				if (prevActivity.equals("PokesActivity")) {
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
	
	public void updateAvatar(View v) {
		v.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.ib_balloon_add_click));
		
		verifyStoragePermissions(this);
		
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
	}
	
	 public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
		 int targetWidth = 400;
		 int targetHeight = 400;
		 Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,Bitmap.Config.ARGB_8888);
		 Canvas canvas = new Canvas(targetBitmap);
		 Path path = new Path();
		 path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight - 1) / 2, (Math.min(((float) targetWidth), ((float) targetHeight)) / 2), Path.Direction.CCW);

		 canvas.clipPath(path);
		 Bitmap sourceBitmap = scaleBitmapImage;
		 int sourceWidth = (sourceBitmap.getWidth() > sourceBitmap.getHeight()) ? sourceBitmap.getHeight() : sourceBitmap.getWidth();
		 int sourceHeight = (sourceBitmap.getHeight() > sourceBitmap.getWidth()) ? sourceBitmap.getWidth() : sourceBitmap.getHeight();
		 canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceWidth, sourceHeight), new Rect(0, 0, targetWidth, targetHeight), null);
		 
		 return targetBitmap;
	 }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
			if (data == null) {
				//Display an error
		        return;
		    }
			
		    try {
				InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
				Bitmap bmp = BitmapFactory.decodeStream(inputStream);
				Bitmap rotatedBmp = modifyOrientation(bmp, data.getData().getPath());
				Bitmap avatar = getRoundedShape(rotatedBmp);
				
				try {
					SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			     	User user = UserHandler.getUserById(appPrefs.getString("userId", "0"));
			     	
			     	File avaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			     	File avaFile = new File(avaDir, user.getId() + ".png");
					FileOutputStream fos = new FileOutputStream(avaFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos, 8192);
					avatar.compress(Bitmap.CompressFormat.PNG, 100, bos);
					
					bos.flush();
					bos.close();
				} catch (Exception e) { }
				
				ivAvatar.setImageBitmap(avatar);
			} catch (FileNotFoundException e) { } catch (IOException e) { }
		}
	}
	
	public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
	    ExifInterface ei = new ExifInterface(image_absolute_path);
	    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

	    switch (orientation) {
	    	case ExifInterface.ORIENTATION_ROTATE_90:
	    		return rotate(bitmap, 90);

	    	case ExifInterface.ORIENTATION_ROTATE_180:
	    		return rotate(bitmap, 180);

	    	case ExifInterface.ORIENTATION_ROTATE_270:
	    		return rotate(bitmap, 270);

	    	case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
	    		return flip(bitmap, true, false);

	    	case ExifInterface.ORIENTATION_FLIP_VERTICAL:
	    		return flip(bitmap, false, true);

	    	default:
	    		return bitmap;
	    }
	}

	public static Bitmap rotate(Bitmap bitmap, float degrees) {
	    Matrix matrix = new Matrix();
	    matrix.postRotate(degrees);
	    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
	    Matrix matrix = new Matrix();
	    matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
	    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
	
	@TargetApi(23)
	public void verifyStoragePermissions(Activity activity) {
	    // Check if we have write permission
	    int permission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

	    if (permission != PackageManager.PERMISSION_GRANTED) {
	        // We don't have permission so prompt the user
	        requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
	    }
	}
}
