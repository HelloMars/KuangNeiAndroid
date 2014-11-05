package me.kuangneipro.Adapter;

import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.activity.MessageListActivity;
import me.kuangneipro.entity.MessageInfo;
import me.kuangneipro.entity.ReplyInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageListAdapter extends ArrayAdapter<MessageInfo> implements OnClickListener {
	public String tag = this.getClass().getSimpleName(); // tag 用于测试log用
	private final MessageListActivity context;
	private final List<MessageInfo> messages;

	static class ViewHolder {
		private TextView content;
		private TextView from;
		private TextView to;
		private TextView reply;
		private ImageView bottle;
		private TextView date;
		private View split;
	}

	public MessageListAdapter(MessageListActivity context,
			List<MessageInfo> messages) {
		super(context, R.layout.item_reply, messages);
		this.context = context;
		this.messages = messages;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public MessageInfo getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(tag, "getView" + position);
		View rowView = convertView;
		ViewHolder viewHolder = null;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.item_reply, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.content = (TextView) rowView.findViewById(R.id.content);
			viewHolder.from = (TextView) rowView.findViewById(R.id.from);
			viewHolder.to = (TextView) rowView.findViewById(R.id.to);
			viewHolder.bottle = (ImageView) rowView.findViewById(R.id.bottle);
			viewHolder.date = (TextView) rowView.findViewById(R.id.date);
			viewHolder.reply = (TextView) rowView.findViewById(R.id.reply);
			viewHolder.split = rowView.findViewById(R.id.split);
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		MessageInfo message = messages.get(position);
		
		
		if(message.postEntity.mChannelId == 0){
			//漂流瓶
			//背景红
			holder.bottle.setVisibility(View.VISIBLE);
			
			holder.content.setOnClickListener(this);
			holder.content.setTag(message.replyInfo);
			holder.date.setOnClickListener(this);
			holder.date.setTag(message.replyInfo);
			holder.reply.setText(" to ");
			//rowView.setBackgroundColor(context.getResources().getColor(R.color.red));
		
		}else{
			//普通消息
			//背景红
			holder.bottle.setVisibility(View.GONE);
			//rowView.setBackgroundColor(context.getResources().getColor(R.color.white));
		
		}
		
		rowView.setBackgroundColor(context.getResources().getColor(R.color.white));
		
		holder.date.setText(message.replyInfo.getDate());

		holder.to.setVisibility(View.VISIBLE);
		holder.to.setTag(message.replyInfo);
		holder.from.setTag(message.replyInfo);
		holder.reply.setVisibility(View.VISIBLE);
		holder.to.setText(message.replyInfo.toUser.name);
		holder.to.setOnClickListener(this);
		holder.from.setText(message.replyInfo.fromUser.name);
		holder.from.setOnClickListener(this);
		holder.content.setText(message.replyInfo.content);
		//if (position == getCount() - 1)
			holder.split.setVisibility(View.GONE);
		//else
		//	holder.split.setVisibility(View.VISIBLE);
		

		return rowView;
	}

	@Override
	public void onClick(View view) {
		if(view!=null){
			switch (view.getId()) {
			case R.id.to:
			{
				ReplyInfo replyInfo = (ReplyInfo)view.getTag();
				replyInfo.replyUser = replyInfo.toUser;
				if(context!=null)
					context.doReplay(replyInfo);
				break;
			}
			case R.id.from:
			case R.id.date:
			case R.id.content:
			{
				ReplyInfo replyInfo = (ReplyInfo)view.getTag();
				replyInfo.replyUser = replyInfo.fromUser;
				if(context!=null)
					context.doReplay(replyInfo);
				break;
			}
			default:
				break;
			}
		}
		
	}

}