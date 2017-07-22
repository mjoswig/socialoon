package de.manuel_joswig.socialoon.util;

import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.manuel_joswig.socialoon.R;
import de.manuel_joswig.socialoon.SplashActivity;
import de.manuel_joswig.socialoon.map.BalloonHandler;
import de.manuel_joswig.socialoon.map.MapActivity;
import de.manuel_joswig.socialoon.user.UserProfileActivity;

/**
 * Default implementation of InfoWindow. 
 * It handles a text and a description. 
 * It also handles optionally a sub-description and an image. 
 * Clicking on the bubble will close it. 
 * 
 * @author M.Kergall
 */
public class DefaultInfoWindow extends InfoWindow {

	static int mTitleId=0, mDescriptionId=0, mSubDescriptionId=0, mImageId=0; //resource ids
	private Activity mapActivity;

	private static void setResIds(Context context){
		String packageName = context.getPackageName(); //get application package name
		mTitleId = context.getResources().getIdentifier("id/bubble_title", null, packageName);
		mDescriptionId = context.getResources().getIdentifier("id/bubble_description", null, packageName);
		mSubDescriptionId = context.getResources().getIdentifier("id/bubble_subdescription", null, packageName);
		mImageId = context.getResources().getIdentifier("id/bubble_image", null, packageName);
		if (mTitleId == 0 || mDescriptionId == 0 || mSubDescriptionId == 0 || mImageId == 0) {
			Log.e(BonusPackHelper.LOG_TAG, "DefaultInfoWindow: unable to get res ids in "+packageName);
		}
	}
	
	public DefaultInfoWindow(int layoutResId, MapView mapView, Activity mapActivity) {
		super(layoutResId, mapView);
		
		if (mTitleId == 0)
			setResIds(mapView.getContext());
		
		//default behavior: close it when clicking on the bubble:
		mView.setOnTouchListener(new View.OnTouchListener() {
			@Override public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_UP)
					close();
				return true; //From Osmdroid 3.0.10, event is properly consumed. 
			}
		});
		
		this.mapActivity = mapActivity;
	}
	
	@Override public void onOpen(Object item) {
		final ExtendedOverlayItem extendedOverlayItem = (ExtendedOverlayItem)item;
		String title = extendedOverlayItem.getTitle();
		if (title == null)
			title = "";
		((TextView)mView.findViewById(mTitleId /*R.id.title*/)).setText(title);
		
		String snippet = extendedOverlayItem.getDescription();
		if (snippet == null)
			snippet = "";
		((TextView)mView.findViewById(mDescriptionId /*R.id.description*/)).setText(snippet);
		
		//handle sub-description, hidding or showing the text view:
		TextView subDescText = (TextView)mView.findViewById(mSubDescriptionId);
		String subDesc = extendedOverlayItem.getSubDescription();
		if (subDesc != null && !("".equals(subDesc))){
			subDescText.setText(subDesc);
			subDescText.setVisibility(View.VISIBLE);
		} else {
			subDescText.setVisibility(View.GONE);
		}

		//handle image
		ImageView imageView = (ImageView)mView.findViewById(mImageId /*R.id.image*/);
		Drawable image = extendedOverlayItem.getImage();
		if (image != null){
			imageView.setImageDrawable(image); //or setBackgroundDrawable(image)?
			imageView.setVisibility(View.VISIBLE);
			
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mapActivity, UserProfileActivity.class);
					intent.putExtra("last_activity", "MapActivity");
					intent.putExtra("profile_id", extendedOverlayItem.getBalloon().getUserId());
					intent.putExtra("balloon_id", extendedOverlayItem.getBalloon().getId());
					
					mapActivity.startActivity(intent);
				}
			});
		} else {
			imageView.setVisibility(View.GONE);
		}
		
		ImageView ivRemove = (ImageView) mView.findViewById(R.id.iv_trash);
		ivRemove.setVisibility(View.GONE);
		ivRemove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(mapActivity);
				exitDialogBuilder.setTitle(R.string.remove_balloon_title);
				exitDialogBuilder.setMessage(R.string.remove_balloon_message);
				exitDialogBuilder.setNegativeButton(android.R.string.no, null);
				exitDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						BalloonHandler.removeBalloon(extendedOverlayItem.getBalloon().getId());
						
						mapActivity.finish();
						Intent intent = mapActivity.getIntent();
						intent.putExtra("last_activity", "MapActivity");
						
						mapActivity.startActivity(intent);
					}
				});
		        		
				exitDialogBuilder.create().show();
			}
		});
		
		if (extendedOverlayItem.isBalloonOwner()) {
			ivRemove.setVisibility(View.VISIBLE);
		}
	}

	@Override public void onClose() {
		//by default, do nothing
	}
}
