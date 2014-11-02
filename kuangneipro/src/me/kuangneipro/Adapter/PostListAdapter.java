package me.kuangneipro.Adapter;

import java.util.ArrayList;

import me.kuangneipro.R;
import me.kuangneipro.activity.PostListActivity;
import me.kuangneipro.entity.PostEntity;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class PostListAdapter extends ArrayAdapter<PostEntity> implements OnClickListener {
	public String tag = this.getClass().getSimpleName(); // tag 用于测试log用  
	private final PostListActivity context;
	private final ArrayList<PostEntity> mPosts;

	static class ViewHolder {
		private ImageView icon;
		private TextView name;
		private TextView content;
		private TextView date;
		private TextView dislikeNum;
		private TextView likeNum;
		private TextView replyNum;
		private ImageView[] pictures;
		private ImageView btnReply;
	}

	public PostListAdapter(Activity context, ArrayList<PostEntity> posts) {
		super(context, R.layout.post_row_layout, posts);
		this.context = (PostListActivity) context;
		this.mPosts = posts;
	}
	
    @Override  
    public int getCount() {
    	//Log.i(tag, "getCount " + mPosts.size());
        return mPosts.size();  
    }
    
    @Override  
    public PostEntity getItem(int position) {  
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
			rowView = inflater.inflate(R.layout.post_row_layout, parent, false);
			// configure view holder
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) rowView.findViewById(R.id.imgIcon);
			viewHolder.name = (TextView) rowView.findViewById(R.id.txtName);
			viewHolder.date = (TextView) rowView.findViewById(R.id.txtDate);
			viewHolder.content = (TextView) rowView.findViewById(R.id.txtContent);
			viewHolder.dislikeNum = (TextView) rowView.findViewById(R.id.txtDislikeNum);
			viewHolder.likeNum = (TextView) rowView.findViewById(R.id.txtLikeNum);
			viewHolder.replyNum = (TextView) rowView.findViewById(R.id.txtReplyNum);
			viewHolder.btnReply = (ImageView) rowView.findViewById(R.id.btnReply);
			viewHolder.btnReply.setOnClickListener(this);
			viewHolder.btnReply.setTag(mPosts.get(position));
			viewHolder.pictures = new ImageView[3];
			viewHolder.pictures[0] = (ImageView) rowView.findViewById(R.id.imageView1);
			viewHolder.pictures[1] = (ImageView) rowView.findViewById(R.id.imageView2);
			viewHolder.pictures[2] = (ImageView) rowView.findViewById(R.id.imageView3);
			rowView.setTag(viewHolder);
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		PostEntity post = mPosts.get(position);
		Log.i(tag, "fill post data" + " " + post.mUserName + " " + post.mContent);
		holder.name.setText(post.mUserName);
		holder.dislikeNum.setText(Integer.toString(post.mDislikeNum));
		holder.likeNum.setText(Integer.toString(post.mLikeNum));
		holder.replyNum.setText(Integer.toString(post.mReplyNum));
		holder.content.setText(post.mContent);
		holder.date.setText(post.getDate());
		Log.i(tag, "!!!!downloading user avatar " + post.mUserAvatar);
		if(!TextUtils.isEmpty(post.mUserAvatar))
		Picasso.with(context)
        	.load(post.mUserAvatar)
        	.placeholder(android.R.drawable.ic_menu_my_calendar)
        	.placeholder(R.drawable.ic_launcher)
        	.error(android.R.drawable.ic_menu_report_image)
        	.into(holder.icon);
		for (int i = 0; i < holder.pictures.length; ++i) {
			if (i < post.mPictures.size()){
				holder.pictures[i].setVisibility(View.VISIBLE);
				String picUrl = post.mPictures.get(i);
				if (picUrl.isEmpty()) {
					holder.pictures[i].setVisibility(View.INVISIBLE);
					continue;
				}
				Log.i(tag, "!!!!downloading pic " + i + picUrl);
				Picasso.with(context)
		        	.load(picUrl)
		        	.placeholder(android.R.drawable.ic_menu_gallery)
		        	.error(android.R.drawable.ic_menu_report_image)
		        	.into(holder.pictures[i]);
			} else {
				holder.pictures[i].setVisibility(View.GONE);
			}
		}
		
		return rowView;
	}

	@Override
	public void onClick(View view) {
		if(view!=null){
			switch (view.getId()) {
			case R.id.btnReply:
				context.doReplay((PostEntity)view.getTag());
				break;
			default:
				break;
			}
		}
		
	}
} 