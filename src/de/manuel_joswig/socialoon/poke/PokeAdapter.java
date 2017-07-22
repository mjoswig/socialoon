package de.manuel_joswig.socialoon.poke;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.manuel_joswig.socialoon.R;
import de.manuel_joswig.socialoon.user.User;
import de.manuel_joswig.socialoon.user.UserHandler;
import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PokeAdapter extends ArrayAdapter<Poke> {
	private final Context context;
	private final ArrayList<Poke> values;
	
	public PokeAdapter(Context context, ArrayList<Poke> values) {
		super(context, R.layout.rowlayout, values);
		
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		TextView textView2 = (TextView) rowView.findViewById(R.id.label_small);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		
		Poke p = values.get(position);
		User u = UserHandler.getUserById(p.getUserId());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try {
			d = sdf.parse(p.getCreationTime());
		} catch (ParseException e) { }
		sdf.applyPattern("yyyy-MM-dd hh:mm aa");
		
		textView.setText(u.getUsername() + " " + context.getString(R.string.poked_you));
		textView2.setText(sdf.format(d));
		
		if (!p.isRead()) rowView.setBackgroundColor(Color.parseColor("#CAE1FF"));
		
		imageView.setImageResource(R.drawable.default_avatar);
		
		try {
			imageView.setImageDrawable(u.getAvatar());
		} catch (IOException e) { }
		
		return rowView;
	}
}
