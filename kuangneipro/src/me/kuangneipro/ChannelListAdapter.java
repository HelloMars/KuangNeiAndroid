package me.kuangneipro;

import java.util.ArrayList;

import me.kuangneipro.entity.ChannelEntity;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ChannelListAdapter extends ArrayAdapter<ChannelEntity> {
	public String tag = this.getClass().getSimpleName(); // tag ”√”⁄≤‚ ‘log”√  
	private final Activity context;
	private final ArrayList<ChannelEntity> mChannels;

	static class ViewHolder {
		public TextView title;
		public TextView subtitle;
		//public ImageView image;
	}

	public ChannelListAdapter(Activity context, ArrayList<ChannelEntity> channels) {
		super(context, R.layout.channel_row_layout, channels);
		this.context = context;
		this.mChannels = channels;
	}
	
    @Override  
    public int getCount() {
    	Log.i(tag, "getCount " + mChannels.size());
        return mChannels.size();  
    }
    
    @Override  
    public ChannelEntity getItem(int position) {  
        return null;  
    }
      
    @Override  
    public long getItemId(int position) {  
        return position;  
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(tag, "getView" + position);
		View rowView = convertView;
		// reuse views
		ViewHolder viewHolder = null;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.channel_row_layout, parent, false);
			// configure view holder
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) rowView.findViewById(R.id.title);
			viewHolder.subtitle = (TextView) rowView.findViewById(R.id.subtitle);
			rowView.setTag(viewHolder);
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		ChannelEntity channel = mChannels.get(position);
		holder.title.setText(channel.mTitle);
		holder.subtitle.setText(channel.mSubtitle);
		
		return rowView;
	}
} 