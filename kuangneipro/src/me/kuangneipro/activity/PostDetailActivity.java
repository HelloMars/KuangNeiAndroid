package me.kuangneipro.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.PostDetailAdapter;
import me.kuangneipro.Adapter.PostListAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.FirstLevelReplyEntity;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.entity.SecondLevelReplyEntity;
import me.kuangneipro.manager.PostEntityManager;
import me.kuangneipro.manager.PostReplyManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PostDetailActivity extends HttpActivity {
	private static final String TAG = "PostDetailActivity";
	
	public final static String SELECT_POST_INFO = "me.kuangnei.select.POST";
	
	private PostEntity mPost;
	private List<FirstLevelReplyEntity> mReplyList;
	private List<FirstLevelReplyEntity> mTempReplyList;
	
	private ExpandableListView mExpandableList;
	private PostDetailAdapter mPostDetailAdapter;
	
	public PostDetailActivity() {
		mReplyList = new ArrayList<FirstLevelReplyEntity>();
		mTempReplyList = new ArrayList<FirstLevelReplyEntity>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_detail);
		
		mPost = (PostEntity) (getIntent().getParcelableExtra(SELECT_POST_INFO));
		
		//getSupportActionBar().setTitle(mChannel.getTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);
		
		mExpandableList = (ExpandableListView) findViewById(R.id.expandableListView);
		
		mPostDetailAdapter = new PostDetailAdapter(this, mPost, mReplyList);
		mExpandableList.setAdapter(mPostDetailAdapter);
		
		this.fillDetailData();
		
		PostReplyManager.getFirstlevelReplyList(getHttpRequest(PostReplyManager.POST_REPLY_FIRST), mPost.mPostId, 1);
	}
	
	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		switch (id) {
		case PostReplyManager.POST_REPLY_FIRST:
			mReplyList.clear();
			mTempReplyList.clear();
			PostReplyManager.fillFirstReplyListFromJson(jsonObj, mTempReplyList);
			Log.i(TAG, "requestComplete " + mTempReplyList.size());
			for (int i = 0; i < mTempReplyList.size(); ++i) {
				FirstLevelReplyEntity reply = mTempReplyList.get(i);
				Log.i(TAG, "FirstLevelReplyEntity:" + reply.mContent);
				PostReplyManager.getSecondlevelReplyList(getHttpRequest(PostReplyManager.POST_REPLY_SECOND), reply.mFirstLevelReplyId, 1);
			}
			break;
		case PostReplyManager.POST_REPLY_SECOND:
			int firstLevelReplyId = PostReplyManager.peekFirstLevelReplyId(jsonObj);
			FirstLevelReplyEntity firstReply = getFirstLevelReply(firstLevelReplyId);
			if(firstReply!=null){
			firstReply.mSecondlevelReplyList.clear();
			PostReplyManager.fillSecondReplyListFromJson(jsonObj, firstReply.mSecondlevelReplyList);
			mReplyList.add(firstReply);
			mPostDetailAdapter.notifyDataSetChanged();
			}
			break;
			
		default:
			break;
		}
	}
	
	private FirstLevelReplyEntity getFirstLevelReply(int firstLevelReplyId) {
		for (int i = 0; i < mTempReplyList.size(); ++i) {
			FirstLevelReplyEntity reply = mTempReplyList.get(i);
			if (firstLevelReplyId == reply.mFirstLevelReplyId)
				return reply;
		}
		return null;
	}
	
	private void fillDetailData() {
		ImageView icon = (ImageView) findViewById(R.id.imgIcon);
		TextView name = (TextView) findViewById(R.id.txtName);
		TextView date = (TextView) findViewById(R.id.txtDate);
		TextView dislikeNum = (TextView) findViewById(R.id.txtDislikeNum);
		TextView likeNum = (TextView) findViewById(R.id.txtLikeNum);
		TextView replyNum = (TextView) findViewById(R.id.txtReplyNum);
		
		name.setText(mPost.mUserName);
		dislikeNum.setText(Integer.toString(mPost.mDislikeNum));
		likeNum.setText(Integer.toString(mPost.mLikeNum));
		replyNum.setText(Integer.toString(mPost.mReplyNum));
		date.setText(mPost.getDate());
		Picasso.with(this)
        	.load(mPost.mUserAvatar)
        	.placeholder(android.R.drawable.ic_menu_my_calendar)
        	.placeholder(R.drawable.ic_launcher)
        	.error(android.R.drawable.ic_menu_report_image)
        	.resize(80, 80)
        	.centerCrop()
        	.into(icon);
	}
}
