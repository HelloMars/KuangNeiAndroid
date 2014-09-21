package me.kuangneipro.Adapter;

import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.entity.SecondLevelReplyEntity;
import me.kuangneipro.util.ListUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SecondLevelReplyAdapter extends ArrayAdapter<SecondLevelReplyEntity> {
	private static final String TAG = SecondLevelReplyAdapter.class.getSimpleName(); // tag 用于测试log用  
	private final Activity context;
	private final List<SecondLevelReplyEntity> mReplys;

	static class ViewHolder {
		public TextView content;
	}

	public SecondLevelReplyAdapter(Activity context, List<SecondLevelReplyEntity> replys) {
		super(context, R.layout.post_detail_row_second_reply, replys);
		this.context = context;
		this.mReplys = replys;
	}
	
    @Override  
    public int getCount() {
    	Log.i(TAG, "getCount " + mReplys.size());
        return mReplys.size();  
    }
    
    @Override  
    public SecondLevelReplyEntity getItem(int position) {  
        return mReplys.get(position);  
    }
      
    @Override  
    public long getItemId(int position) {  
        return position;  
    }

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG, "getView" + position);

		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.post_detail_row_second_reply, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.content = (TextView) convertView.findViewById(R.id.txtSecondContent);
			convertView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		SecondLevelReplyEntity reply = mReplys.get(position);
		Log.i(TAG, "username: " + reply.mUserName);
		Log.i(TAG, "content: " + reply.mContent);
		String content = String.format("<font color=#6495ED>%s:</font><font color=#000000>%s %s</font>",
				reply.mUserName, reply.mContent, reply.getDate());
		holder.content.setText(Html.fromHtml(content));
		
		return convertView;
	}

}
