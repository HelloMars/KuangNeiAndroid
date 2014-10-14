package me.kuangneipro.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.PostDetailAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.emoticon.EmoticonInputDialog;
import me.kuangneipro.emoticon.EmoticonInputView.OnEmoticonMessageSendListener;
import me.kuangneipro.emoticon.EmoticonPopupable;
import me.kuangneipro.entity.FirstLevelReplyEntity;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.manager.PostReplyManager;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class PostDetailActivity extends HttpActivity {
	private static final String TAG = "PostDetailActivity";
	
	public final static String SELECT_POST_INFO = "me.kuangnei.select.POST";
	
	private int firstReplyIndex;
	private PostEntity mPost;
	private List<FirstLevelReplyEntity> mReplyList;
	private List<FirstLevelReplyEntity> mTempReplyList;
	
	private ExpandableListView mExpandableList;
	private PostDetailAdapter mPostDetailAdapter;
	
	private EmoticonPopupable mFirstReplyEmoticonPopupable;
	private EmoticonPopupable mSecondReplyEmoticonPopupable;
	
	private ImageButton btnReply;
	
	public PostDetailActivity() {
		firstReplyIndex = 0;
		mReplyList = new ArrayList<FirstLevelReplyEntity>();
		mTempReplyList = new ArrayList<FirstLevelReplyEntity>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_detail);
		mPost = (PostEntity) (getIntent().getParcelableExtra(SELECT_POST_INFO));
		btnReply = (ImageButton) findViewById(R.id.btnReply);
		btnReply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(mPost!=null)
					doReplayFirst();
			}
		});
		
		
		mExpandableList = (ExpandableListView) findViewById(R.id.expandableListView);
		
		mPostDetailAdapter = new PostDetailAdapter(this, mPost, mReplyList);
		mExpandableList.setAdapter(mPostDetailAdapter);
		
		this.fillDetailData();
		
		PostReplyManager.getFirstlevelReplyList(getHttpRequest(PostReplyManager.POST_REPLY_FIRST), mPost.mPostId, 1);
		
		if (mFirstReplyEmoticonPopupable == null) {
			mFirstReplyEmoticonPopupable = new EmoticonInputDialog(this, new OnEmoticonMessageSendListener() {
				@Override
				public void onSend(View v, String text) {
					if(!TextUtils.isEmpty(text) && mPost!=null){
						PostReplyManager.doReplayFirst(getHttpRequest(PostReplyManager.DO_REPLAY_FIRST), text, mPost.mPostId);
						mFirstReplyEmoticonPopupable.cleatEmoticonEditText();
					}else{
						Toast.makeText(PostDetailActivity.this, "请输入回复的话", Toast.LENGTH_SHORT).show();
					}
					
				}
			});
			//下方输入字数限制.
			mFirstReplyEmoticonPopupable.getEmoticonInputView().setMaxTextCount(100);
		}
		
		if (mSecondReplyEmoticonPopupable == null) {
			mSecondReplyEmoticonPopupable = new EmoticonInputDialog(this, new OnEmoticonMessageSendListener() {
				@Override
				public void onSend(View v, String text) {
					if(!TextUtils.isEmpty(text) && mPost!=null){
						PostReplyManager.doReplaySecond(getHttpRequest(PostReplyManager.DO_REPLAY_FIRST), text,mPost.mPostId , (Integer)v.getTag());
						mSecondReplyEmoticonPopupable.cleatEmoticonEditText();
					}else{
						Toast.makeText(PostDetailActivity.this, "请输入回复的话", Toast.LENGTH_SHORT).show();
					}
					
				}
			});
			//下方输入字数限制.
			mSecondReplyEmoticonPopupable.getEmoticonInputView().setMaxTextCount(100);
		}
	}
	
	public void doReplayFirst(){
		if(mFirstReplyEmoticonPopupable!=null){
			mFirstReplyEmoticonPopupable.show();
		}
	}
	
	public void doReplaySecond(int firstReplyId){
		if(mSecondReplyEmoticonPopupable!=null){
			mSecondReplyEmoticonPopupable.show();
			mSecondReplyEmoticonPopupable.getEmoticonSendButton().setTag(firstReplyId);
		}
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
			firstReplyIndex = 0;
			for (int i = 0; i < mTempReplyList.size(); ++i) {
				FirstLevelReplyEntity reply = mTempReplyList.get(i);
				Log.i(TAG, "FirstLevelReplyEntity:" + reply.mContent);
				PostReplyManager.getSecondlevelReplyList(getHttpRequest(PostReplyManager.POST_REPLY_SECOND), reply.mFirstLevelReplyId, 1);
			}
			break;
		case PostReplyManager.POST_REPLY_SECOND:
			int firstLevelReplyId = PostReplyManager.peekFirstLevelReplyId(jsonObj);
			//int firstLevelReplyId = PostReplyManager.peekFirstLevelReplyId(jsonObj);
			if (firstLevelReplyId > 0) {
				FirstLevelReplyEntity firstReply = getFirstLevelReply(firstLevelReplyId);
				if(firstReply!=null){
					firstReply.mSecondlevelReplyList.clear();
					PostReplyManager.fillSecondReplyListFromJson(jsonObj, firstReply.mSecondlevelReplyList);
					mReplyList.add(firstReply);
				}
			}
			Collections.sort(mReplyList);
			mPostDetailAdapter.notifyDataSetChanged();
			break;
		case PostReplyManager.DO_REPLAY_FIRST:
			ReturnInfo ri = PostReplyManager.getReplyReturnInfo(jsonObj);
			if(ri!=null&& ri.getReturnCode() == ReturnInfo.SUCCESS){
				Toast.makeText(this, "回复成功", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, ri.getReturnMessage(), Toast.LENGTH_SHORT).show();
			}
		case PostReplyManager.DO_REPLAY_SECOND:
			ReturnInfo returnInfo = PostReplyManager.getReplyReturnInfo(jsonObj);
			if(returnInfo!=null&& returnInfo.getReturnCode() == ReturnInfo.SUCCESS){
				Toast.makeText(this, "回复成功", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, returnInfo.getReturnMessage(), Toast.LENGTH_SHORT).show();
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
