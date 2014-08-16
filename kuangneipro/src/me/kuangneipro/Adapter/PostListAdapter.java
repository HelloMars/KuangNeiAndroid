package me.kuangneipro.Adapter;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import me.kuangneipro.R;
import me.kuangneipro.entity.PostEntity;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class PostListAdapter extends ArrayAdapter<PostEntity> {
	public String tag = this.getClass().getSimpleName(); // tag ”√”⁄≤‚ ‘log”√  
	private final Activity context;
	private final ArrayList<PostEntity> mPosts;

	static class ViewHolder {
		public ImageView icon;
		public TextView name;
		public TextView content;
		public TextView date;
		public TextView dislikeNum;
		public TextView replyNum;
		public ImageView[] pictures;
	}

	public PostListAdapter(Activity context, ArrayList<PostEntity> posts) {
		super(context, R.layout.post_row_layout, posts);
		this.context = context;
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
			viewHolder.replyNum = (TextView) rowView.findViewById(R.id.txtReplyNum);
			viewHolder.pictures = new ImageView[3];
			viewHolder.pictures[0] = (ImageView) rowView.findViewById(R.id.imageView1);
			viewHolder.pictures[1] = (ImageView) rowView.findViewById(R.id.imageView2);
			viewHolder.pictures[2] = (ImageView) rowView.findViewById(R.id.imageView3);
			rowView.setTag(viewHolder);
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		PostEntity post = mPosts.get(position);
		holder.name.setText(post.mUserName);
		holder.dislikeNum.setText(Integer.toString(post.mDislikeNum));
		holder.replyNum.setText(Integer.toString(post.mReplyNum));
		holder.content.setText(post.mContent);
		holder.date.setText(post.getDate());
		Log.i(tag, "!!!!downloading user avatar " + post.mUserAvatar);
		Picasso.with(context)
        	.load(post.mUserAvatar)
        	//.placeholder(android.R.drawable.ic_menu_my_calendar)
        	.placeholder(R.drawable.ic_launcher)
        	.error(android.R.drawable.ic_menu_report_image)
        	.fit()
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
		        	.fit()
		        	.into(holder.pictures[i]);
			} else {
				holder.pictures[i].setVisibility(View.INVISIBLE);
			}
		}
		
		return rowView;
	}
} 