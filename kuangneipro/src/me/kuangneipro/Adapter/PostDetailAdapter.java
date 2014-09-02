package me.kuangneipro.Adapter;

import me.kuangneipro.R;
import me.kuangneipro.entity.PostEntity;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class PostDetailAdapter extends BaseExpandableListAdapter {
	private static final int POST_CONTENT = 0;
	private static final int IMAGE_LIST = 1;
	private static final int HOTTEST_REPLY_LIST = 2;
	private static final int LATEST_REPLY_LIST = 3;
	
	private static final String TAG = PostDetailAdapter.class.getSimpleName(); // tag 用于测试log用  
	
	private final Activity mContext;
	private final PostEntity mPost;
	
	static class ContentViewHolder {
		TextView content;
	}
	
	static class ImageViewHolder {
		public ImageView image;
	}
	
	public PostDetailAdapter(Activity context, PostEntity post) {
		this.mContext = context;
		this.mPost = post;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Log.i(TAG, "getChild " + groupPosition + childPosition);
		// TODO Auto-generated method stub
		switch(groupPosition) {
		case POST_CONTENT:
			return mPost.mContent;
		case IMAGE_LIST:
			return mPost.mPictures.get(childPosition);
		}
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "getChildView " + groupPosition + " " + childPosition);
		switch(groupPosition) {
			case POST_CONTENT: {
				if (convertView == null) {
					LayoutInflater inflater = mContext.getLayoutInflater();
					convertView = inflater.inflate(R.layout.post_detail_row_content, parent, false);
					ContentViewHolder viewHolder = new ContentViewHolder();
					viewHolder.content = (TextView) convertView.findViewById(R.id.txtContent);
					convertView.setTag(viewHolder);
				}
				ContentViewHolder contentHolder = (ContentViewHolder) convertView.getTag();
				contentHolder.content.setText(mPost.mContent);
				break;
			}
			case IMAGE_LIST: {
				if (convertView == null) {
					LayoutInflater inflater = mContext.getLayoutInflater();
					//LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.post_detail_row_image, parent, false);
					ImageViewHolder viewHolder = new ImageViewHolder();
					viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView);
					convertView.setTag(viewHolder);
				}
				final ImageViewHolder imageHolder = (ImageViewHolder) convertView.getTag();
				String picUrl = (String)getChild(groupPosition, childPosition);
				
				Transformation transformation = new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int targetWidth = imageHolder.image.getWidth();

                        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                        int targetHeight = (int) (targetWidth * aspectRatio);
                        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    }

                    @Override public String key() {
                        return "transformation" + " desiredWidth";
                    }

                };
                
				Picasso.with(mContext)
		        	.load(picUrl)
		        	.placeholder(android.R.drawable.ic_menu_gallery)
		        	.error(android.R.drawable.ic_menu_report_image)
		        	//.resize(320, 320)
		        	//.centerCrop()
		        	.transform(transformation)
		        	//.fit().centerInside()
		        	.into(imageHolder.image);
				break;
			}
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		switch(groupPosition) {
		case POST_CONTENT:
			Log.i(TAG, "getChildrenCount " + groupPosition + " " + 1);
			return 1;
		case IMAGE_LIST:
			Log.i(TAG, "getChildrenCount " + groupPosition + " " + mPost.mPictures.size());
			return mPost.mPictures.size();
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		Log.i(TAG, "getGroup " + groupPosition);
		return "getGroup is null";
	}

	@Override
	public int getGroupCount() {
		return 2;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = new LinearLayout(mContext);
	    ExpandableListView list = (ExpandableListView) parent;
	    list.expandGroup(groupPosition);
	    return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
