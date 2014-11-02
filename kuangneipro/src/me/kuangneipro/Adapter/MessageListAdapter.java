package me.kuangneipro.Adapter;

import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.entity.MessageEntity;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MessageListAdapter extends ArrayAdapter<MessageEntity> {
	private static final String TAG = MessageListAdapter.class.getSimpleName(); // tag 用于测试log用  

	static class ViewHolder {
		private ImageView icon;
		private TextView name;
		private TextView replycontent;
		private TextView repliedcontent;
		private TextView date;
	}

	public MessageListAdapter(Activity context, List<MessageEntity> messages) {
		super(context, 0, messages);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG, "getView" + position + ", " + getCount());

		// Get the data item for this position
		MessageEntity message = getItem(position);  

		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_row_layout, parent, false);
			// Lookup view for data population
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.imgIcon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.txtName);
			viewHolder.date = (TextView) convertView.findViewById(R.id.txtDate);
			viewHolder.replycontent = (TextView) convertView.findViewById(R.id.replyContent);
			viewHolder.repliedcontent = (TextView) convertView.findViewById(R.id.repliedContent);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.name.setText(message.mUserName);
		viewHolder.replycontent.setText(message.mReplyContent);
		viewHolder.repliedcontent.setText(message.getRepliedContent());
		viewHolder.date.setText(message.getDate());

		Picasso.with(getContext())
        	.load(message.mUserAvatar)
        	.placeholder(android.R.drawable.ic_menu_my_calendar)
        	.placeholder(R.drawable.ic_launcher)
        	.error(android.R.drawable.ic_menu_report_image)
        	.into(viewHolder.icon);
		
		// Return the completed view to render on screen
		return convertView;
	}
}
