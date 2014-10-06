package me.kuangneipro.Adapter;

import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.entity.ChannelEntity;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ChannelListAdapter extends ArrayAdapter<ChannelEntity> {
	private static final String TAG = ChannelListAdapter.class.getSimpleName(); // tag 用于测试log用  

	static class ViewHolder {
		public TextView title;
		public TextView subtitle;
		//public ImageView image;
	}

	public ChannelListAdapter(Activity context, List<ChannelEntity> channels) {
		super(context, 0, channels);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG, "getView" + position + ", " + getCount());

		// Get the data item for this position
		ChannelEntity channel = getItem(position);  

		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.channel_row_layout, parent, false);
			// Lookup view for data population
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		// Populate the data into the template view using the data object
		viewHolder.title.setText(channel.getTitle());
		if (channel.getSubtitle().isEmpty()) {
			viewHolder.subtitle.setVisibility(View.GONE);
		} else {
			viewHolder.subtitle.setText(channel.getSubtitle());
		}
		viewHolder.subtitle.setText(channel.getSubtitle());
		
		// Return the completed view to render on screen
		return convertView;
	}
} 