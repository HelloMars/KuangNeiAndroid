package me.kuangneipro.Adapter;

import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.activity.PostDetailActivity;
import me.kuangneipro.entity.FirstLevelReplyEntity;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.entity.SecondLevelReplyEntity;
import me.kuangneipro.util.ListUtil;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

@SuppressLint("CutPasteId")
public class PostDetailAdapter extends BaseExpandableListAdapter {
	private static final int POST_CONTENT = 0;
	private static final int IMAGE_LIST = 1;
	private static final int REPLY_LIST = 2;
	//private static final int HOTTEST_REPLY_LIST = 2;
	//private static final int LATEST_REPLY_LIST = 3;
	
	private static final String TAG = PostDetailAdapter.class.getSimpleName(); // tag 用于测试log用  
	
	private final PostDetailActivity mPostDetailActivity;
	private final PostEntity mPost;
	private List<FirstLevelReplyEntity> mReplyList;
	
	private static class ContentViewHolder {
		TextView content;
	}
	
	private static class ImageViewHolder {
		public ImageView image;
	}
	
	public PostDetailAdapter(PostDetailActivity postDetailActivity, PostEntity post, List<FirstLevelReplyEntity> replyList) {
		this.mPostDetailActivity = postDetailActivity;
		this.mPost = post;
		this.mReplyList = replyList;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Log.i(TAG, "getChild " + groupPosition + childPosition);
		switch(groupPosition) {
		case POST_CONTENT:
			return mPost.mContent;
		case IMAGE_LIST:
			return mPost.mPictures.get(childPosition);
		case REPLY_LIST:
			return mReplyList.get(childPosition);
		}
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		Log.i(TAG, "getChildView " + groupPosition + " " + childPosition);
		switch(groupPosition) {
			case POST_CONTENT: {
				LayoutInflater inflater = mPostDetailActivity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.post_detail_row_content, parent, false);
				ContentViewHolder viewHolder = new ContentViewHolder();
				viewHolder.content = (TextView) convertView.findViewById(R.id.txtContent);
				convertView.setTag(viewHolder);

				ContentViewHolder contentHolder = (ContentViewHolder) convertView.getTag();
				contentHolder.content.setText(mPost.mContent);
				break;
			}
			case IMAGE_LIST: {
				Log.i(TAG, "convertView == null");
				LayoutInflater inflater = mPostDetailActivity.getLayoutInflater();
				//LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.post_detail_row_image, parent, false);
				ImageViewHolder viewHolder = new ImageViewHolder();
				viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView);
				convertView.setTag(viewHolder);
				
				final ImageViewHolder imageHolder = (ImageViewHolder) convertView.getTag();
				String picUrl = (String)getChild(groupPosition, childPosition);
				Log.i(TAG, "imageHolder == null ? " + (imageHolder == null));
				Log.i(TAG, "imageHolder.image == null ? " + (imageHolder.image == null));
				
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
                
				Picasso.with(mPostDetailActivity)
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
			case REPLY_LIST: {
				LayoutInflater inflater = mPostDetailActivity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.post_detail_row_reply, parent, false);

				ImageView icon = (ImageView) convertView.findViewById(R.id.imgIcon);
				TextView name = (TextView) convertView.findViewById(R.id.txtName);
				TextView date = (TextView) convertView.findViewById(R.id.txtDate);
				TextView likeNum = (TextView) convertView.findViewById(R.id.txtLikeNum);
				TextView replyNum = (TextView) convertView.findViewById(R.id.txtReplyNum);
				ImageButton btnReply = (ImageButton)convertView.findViewById(R.id.btnReply);
				TextView content = (TextView) convertView.findViewById(R.id.txtContent);
				ListView replyList = (ListView) convertView.findViewById(R.id.list_reply);
				
				// fill data
				final FirstLevelReplyEntity firstReply = mReplyList.get(childPosition);
				Log.i(TAG, firstReply.mUserName + " " + firstReply.mContent);
				Log.i(TAG, "!!!!downloading user avatar " + firstReply.mUserAvatar);
				Picasso.with(mPostDetailActivity)
		        	.load(firstReply.mUserAvatar)
		        	.placeholder(android.R.drawable.ic_menu_my_calendar)
		        	.placeholder(R.drawable.ic_launcher)
		        	.error(android.R.drawable.ic_menu_report_image)
		        	.resize(80, 80)
		        	.centerCrop()
		        	.into(icon);
				name.setText(firstReply.mUserName);
				date.setText(firstReply.getDate());
				likeNum.setText(Integer.toString(firstReply.mLikeNum));
				replyNum.setText(Integer.toString(firstReply.mReplyNum));
				btnReply.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
//						Toast.makeText(mPostDetailActivity, firstReply.mPostId +"  "+firstReply.mFirstLevelReplyId, Toast.LENGTH_LONG).show();
						mPostDetailActivity.doReplaySecond(firstReply.mFirstLevelReplyId);
					}
				});
				content.setText(firstReply.mContent);
				
				Log.i(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!before set second level reply adatper");
				if (firstReply.mSecondlevelReplyList.size() > 0) {
					Log.i(TAG, firstReply.mUserName + " " + firstReply.mContent);
					//if(firstReply.mAdapter==null){
						firstReply.mAdapter = new SecondLevelReplyAdapter(mPostDetailActivity, firstReply.mSecondlevelReplyList);
						replyList.setAdapter(firstReply.mAdapter);
					/*}else{
						firstReply.mAdapter.notifyDataSetChanged();
					}*/
					ListUtil.setListViewHeightBasedOnChildren(replyList);
				}
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
		case REPLY_LIST:
			Log.i(TAG, "getChildrenCount " + groupPosition + "" + mReplyList.size());
			return mReplyList.size();
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
		return 3;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = new LinearLayout(mPostDetailActivity);
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