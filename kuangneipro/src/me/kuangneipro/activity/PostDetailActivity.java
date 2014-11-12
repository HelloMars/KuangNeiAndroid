package me.kuangneipro.activity;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.ReplyListAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.emoticon.EmoticonInputDialog;
import me.kuangneipro.emoticon.EmoticonInputView.OnEmoticonMessageSendListener;
import me.kuangneipro.emoticon.EmoticonPopupable;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.entity.ReplyInfo;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.entity.UpInfo;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.manager.ReplyInfoManager;
import me.kuangneipro.manager.UpInfoManager;
import me.kuangneipro.util.SexUtil;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.picasso.Picasso;

@SuppressLint("InflateParams") public class PostDetailActivity extends HttpActivity implements OnEmoticonMessageSendListener, OnClickListener {
	
	public final static String SELECT_POST_INFO = "me.kuangnei.select.POST";
	public final static String IS_PING_POST = "me.kuangnei.is.ping.post";
	
	private PullToRefreshListView mListView;
	private List<ReplyInfo> mReplyList;
	private int index = 1;
	private PostEntity mPost;
	private View back;
	private EmoticonPopupable mEmoticonPopupable;
	private ReplyListAdapter mReplyListAdapter;
	public PostDetailActivity() {
		mReplyList = new ArrayList<ReplyInfo>();
	}
	
	private View headerView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_detail);
		
		mPost = (PostEntity) (getIntent().getParcelableExtra(SELECT_POST_INFO));
		if(mPost == null){
			finish();
			return ;
		}
		
		if( getIntent().getBooleanExtra(IS_PING_POST,false) )
		{
			TextView introText = (TextView)findViewById(R.id.introtext);
			introText.setText("内部漂流瓶");
			
		}
		
		if (mEmoticonPopupable == null) {
			mEmoticonPopupable = new EmoticonInputDialog(this, this);
			//下方输入字数限制.
			mEmoticonPopupable.getEmoticonInputView().setMaxTextCount(140);
		}
		
		back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		
		mListView = (PullToRefreshListView)findViewById(R.id.list);
	
		headerView = LayoutInflater.from(this).inflate(R.layout.activity_post_detai_header, null);
		
		if(mPost.mChannelId != 0 ){
			
			ListView l = mListView.getRefreshableView();
			l.addHeaderView(headerView, null, false);
			this.fillDetailData();
		}
		
		
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            	index = 1;
            	ReplyInfoManager.getReplyList(getHttpRequest(ReplyInfoManager.REPLY_KEY_REFRESH), mPost.mPostId, 1);
            }
        });
        mListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				ReplyInfoManager.getReplyList(getHttpRequest(ReplyInfoManager.REPLY_KEY_REFRESH_MORE), mPost.mPostId, ++index);
			}
		});
        
        ReplyInfoManager.getReplyList(getHttpRequest(ReplyInfoManager.REPLY_KEY_REFRESH), mPost.mPostId, 1);
	}
	
	
	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		switch (id) {
		case ReplyInfoManager.REPLY_KEY_REFRESH:
			mReplyList.clear();
		case ReplyInfoManager.REPLY_KEY_REFRESH_MORE:
			ReplyInfoManager.fillReplyListFromJson(jsonObj, mReplyList);
			if(mReplyListAdapter==null){
				mReplyListAdapter = new ReplyListAdapter(this, mReplyList,Integer.parseInt(mPost.mUserId.trim()),mPost.mChannelId);
				mListView.setAdapter(mReplyListAdapter);
			}else{
				mReplyListAdapter.notifyDataSetChanged();
				mListView.onRefreshComplete();
			}
			break;
		case ReplyInfoManager.DO_REPLY:
			ReturnInfo ri = ReplyInfoManager.getReplyReturnInfo(jsonObj);
			if(ri!=null&& ri.getReturnCode() == ReturnInfo.SUCCESS){
				Toast.makeText(this, "回复成功", Toast.LENGTH_SHORT).show();
				index = 1;
				mPost.mReplyNum++;
				fillDetailData();
	            ReplyInfoManager.getReplyList(getHttpRequest(ReplyInfoManager.REPLY_KEY_REFRESH), mPost.mPostId, 1);
			}else{
				if(ri!=null){
					Toast.makeText(this, ri.getReturnMessage(), Toast.LENGTH_SHORT).show();
				}else
					Toast.makeText(this, "回复失败", Toast.LENGTH_SHORT).show();
			}
			break;
		case UpInfoManager.DO_UP:
			if(mPost != null){
				ReturnInfo returnInfo = ReturnInfo.fromJSONObject(jsonObj);
				if(returnInfo != null  && returnInfo.getReturnCode() == ReturnInfo.SUCCESS){
					UpInfo upInfo = UpInfoManager.getReplyReturnInfo(jsonObj);
					mPost.mLikeNum = upInfo.upCount;
					mPost.mLikeSelected = upInfo.isDo();
					fillDetailData();
					Toast.makeText(this, "点赞成功", Toast.LENGTH_SHORT).show();
				}else if(returnInfo != null){
					Toast.makeText(this, returnInfo.getReturnMessage(), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "点赞失败", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}
	
	
	private void fillDetailData() {
		TextView name = (TextView) headerView.findViewById(R.id.txtName);
		TextView date = (TextView) headerView.findViewById(R.id.txtDate);
		TextView content = (TextView)headerView. findViewById(R.id.txtContent);
		
		TextView likeNum = (TextView) headerView.findViewById(R.id.txtLikeNum);
		TextView replyNum = (TextView)headerView. findViewById(R.id.txtReplyNum);
		
		ImageView imageView1 = (ImageView)headerView.findViewById(R.id.imageView1);
		ImageView imageView2 = (ImageView)headerView.findViewById(R.id.imageView2);
		ImageView imageView3 = (ImageView)headerView.findViewById(R.id.imageView3);
		View sex = headerView.findViewById(R.id.sex);
		imageView1.setVisibility(View.GONE);
		imageView2.setVisibility(View.GONE);
		imageView3.setVisibility(View.GONE);
		
		if(SexUtil.isValid(mPost.mSex)){
			sex.setVisibility(View.VISIBLE);
			if(SexUtil.isMale(mPost.mSex)){
				sex.setSelected(false);
			}else{
				sex.setSelected(true);
			}
		}else{
			sex.setVisibility(View.GONE);
		}
		
		
		name.setText(mPost.mUserName);
		likeNum.setText(Integer.toString(mPost.mLikeNum));
		replyNum.setText(Integer.toString(mPost.mReplyNum));
		content.setText(mPost.mContent);
		date.setText(mPost.getDate());
		
		View btnReply = headerView.findViewById(R.id.btnReply);
		View btnLike = headerView.findViewById(R.id.btnLike);
		
		
		btnLike.setSelected(mPost.mLikeSelected);
		btnReply.setOnClickListener(this);
		btnLike.setOnClickListener(this);
		
		if(mPost.mPictures!=null)
			for(int i=0;i<mPost.mPictures.size();i++){
				switch (i) {
				case 0:
				{
					imageView1.setVisibility(View.VISIBLE);
					Picasso.with(this)
		        	.load(mPost.mPictures.get(i))
		        	.placeholder(R.drawable.loading)
		        	.error(R.drawable.error)
		        	.into(imageView1);
					break;
				}
				case 1:
				{
					imageView2.setVisibility(View.VISIBLE);
					Picasso.with(this)
		        	.load(mPost.mPictures.get(i))
		        	.placeholder(R.drawable.loading)
		        	.error(R.drawable.error)
		        	.into(imageView2);
					break;
				}
				case 2:
				{
					imageView3.setVisibility(View.VISIBLE);
					Picasso.with(this)
		        	.load(mPost.mPictures.get(i))
		        	.placeholder(android.R.drawable.ic_menu_my_calendar)
		        	.placeholder(R.drawable.ic_launcher)
		        	.error(android.R.drawable.ic_menu_report_image)
		        	.into(imageView3);
					break;
				}

				default:
					break;
				}
			}
		
	}
	
	public void doUp(){
		
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		if(userInfo!=null && !TextUtils.isEmpty(userInfo.getName())  && SexUtil.isValid(userInfo.getSex())){
			
			if(mPost!=null)
				UpInfoManager.doUp(getHttpRequest(UpInfoManager.DO_UP), mPost.mPostId+"");
			
		}else{
			new AlertDialog.Builder(this)
			 .setMessage("请先设置昵称和性别后发送帖子")
			 .setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(PostDetailActivity.this, PersonalInfoActivity.class);
			    	startActivity(intent);
				}
			}) .setNegativeButton("取消", null)
			 .show(); 
		}
		
	}

	public void doReplay(ReplyInfo replyInfo){
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		if(userInfo!=null && !TextUtils.isEmpty(userInfo.getName())  && SexUtil.isValid(userInfo.getSex())){
			
			if(mEmoticonPopupable!=null){
				if(replyInfo!=null&&replyInfo.replyUser!=null)
					mEmoticonPopupable.getEmoticonEditText().setHint("回复 "+replyInfo.replyUser.name);
				else
					mEmoticonPopupable.getEmoticonEditText().setHint("");
				mEmoticonPopupable.show();
				mEmoticonPopupable.getEmoticonSendButton().setTag(replyInfo);
			}
			
		}else{
			new AlertDialog.Builder(this)
			 .setMessage("请先设置昵称和性别后发送帖子")
			 .setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(PostDetailActivity.this, PersonalInfoActivity.class);
			    	startActivity(intent);
				}
			}) .setNegativeButton("取消", null)
			 .show(); 
		}
		
	}

	@Override
	public void onSend(View v, String text) {
		if(!TextUtils.isEmpty(text)){
			ReplyInfo replyInfo = (ReplyInfo)v.getTag();
			if(replyInfo==null){
				ReplyInfoManager.doReplay(getHttpRequest(ReplyInfoManager.DO_REPLY), mPost.mUserId, mPost.mPostId+"", text);
			}else{
				if(replyInfo.replyUser!=null)
					ReplyInfoManager.doReplay(getHttpRequest(ReplyInfoManager.DO_REPLY), replyInfo.replyUser.id+"", replyInfo.postId+"", text);
				else
					ReplyInfoManager.doReplay(getHttpRequest(ReplyInfoManager.DO_REPLY), replyInfo.toUser.id+"", replyInfo.postId+"", text);
			}
			
			mEmoticonPopupable.cleatEmoticonEditText();
		}else{
			Toast.makeText(this, "请输入回复的话", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnReply:
			doReplay(null);
			break;
		case R.id.btnLike:
			doUp();
			break;
		default:
			break;
		}
		
	}

}
