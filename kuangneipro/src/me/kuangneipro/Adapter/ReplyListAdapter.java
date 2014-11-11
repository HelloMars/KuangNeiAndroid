package me.kuangneipro.Adapter;

import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.activity.PostDetailActivity;
import me.kuangneipro.entity.ReplyInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ReplyListAdapter extends ArrayAdapter<ReplyInfo> implements OnClickListener {
	public String tag = this.getClass().getSimpleName(); // tag 用于测试log用  
	private final PostDetailActivity context;
	private final List<ReplyInfo> replays;
	private final int postUserId;
	private final int channelId;

	static class ViewHolder {
		private TextView content;
		private TextView from;
		private TextView to;
		private TextView reply;
		private TextView date;
		private View split;
	}

	public ReplyListAdapter(PostDetailActivity context, List<ReplyInfo> replys,int postUserId,int channelId) {
		super(context, R.layout.item_reply, replys);
		this.context = context;
		this.replays = replys;
		this.postUserId = postUserId;
		this.channelId = channelId;
	}
	
    @Override  
    public int getCount() {
        return replays.size();  
    }
    
    @Override  
    public ReplyInfo getItem(int position) {  
        return replays.get(position);  
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
			viewHolder.date = (TextView) rowView.findViewById(R.id.date);
			viewHolder.reply = (TextView) rowView.findViewById(R.id.reply);
			viewHolder.split = rowView.findViewById(R.id.split);
			rowView.setTag(viewHolder);
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		ReplyInfo reply = replays.get(position);
		
		holder.date.setText(reply.getDate());
		
		if(reply.toUser.id == postUserId || channelId == 0){
			holder.to.setVisibility(View.GONE);
			holder.reply.setVisibility(View.GONE);
		}else{
			holder.to.setVisibility(View.VISIBLE);
			holder.reply.setVisibility(View.VISIBLE);
			holder.to.setText(reply.toUser.name);
		}
		
		holder.from.setText(reply.fromUser.name);
		holder.content.setText(reply.content);
		if(position == getCount() -1)
			holder.split.setVisibility(View.GONE);
		else
			holder.split.setVisibility(View.VISIBLE);
		
		holder.to.setOnClickListener(this);
		holder.from.setOnClickListener(this);
		holder.to.setTag(reply);
		holder.from.setTag(reply);
		
		holder.content.setOnClickListener(this);
		holder.content.setTag(reply);
		holder.date.setOnClickListener(this);
		holder.date.setTag(reply);
		
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