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
	private static final String TAG = ChannelListAdapter.class.getSimpleName(); // tag ”√”⁄≤‚ ‘log”√  
	private final Activity context;
	private final List<ChannelEntity> mChannels;

	static class ViewHolder {
		public TextView title;
		public TextView subtitle;
		//public ImageView image;
	}

	public ChannelListAdapter(Activity context, List<ChannelEntity> channels) {
		super(context, R.layout.channel_row_layout, channels);
		this.context = context;
		this.mChannels = channels;
	}
	
    @Override  
    public int getCount() {
    	Log.i(TAG, "getCount " + mChannels.size());
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
		Log.i(TAG, "getView" + position);

		ViewHolder viewHolder = null;
		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.channel_row_layout, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
			convertView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		ChannelEntity channel = mChannels.get(position);
		holder.title.setText(channel.getTitle());
		holder.subtitle.setText(channel.getSubtitle());
		
		return convertView;
	}
} 