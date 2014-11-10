package me.kuangneipro.activity;

import java.util.ArrayList;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.PostListAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.emoticon.EmoticonInputDialog;
import me.kuangneipro.emoticon.EmoticonInputView.OnEmoticonMessageSendListener;
import me.kuangneipro.emoticon.EmoticonPopupable;
import me.kuangneipro.entity.KuangInfo;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.entity.TopicInfo;
import me.kuangneipro.entity.UpInfo;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.entity.VersionInfo;
import me.kuangneipro.manager.PostEntityManager;
import me.kuangneipro.manager.ReplyInfoManager;
import me.kuangneipro.manager.TopicInfoManager;
import me.kuangneipro.manager.UnreadManager;
import me.kuangneipro.manager.UpInfoManager;
import me.kuangneipro.manager.VersionManager;
import me.kuangneipro.util.DownloadUtil;
import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.SexUtil;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.igexin.sdk.PushManager;

public class PostListActivity extends HttpActivity implements OnEmoticonMessageSendListener, android.view.View.OnClickListener {
	private static final String TAG = PostListActivity.class.getSimpleName();  //tag 用于测试log用  
	
	public final static String SELECT_CHANNEL_INFO = "me.kuangnei.select.CHANNEL";
	
	private static final int channelID = 1;
	private View headerView;
	private PullToRefreshListView mListView;
	private ArrayList<PostEntity> mPostList;
	private PostListAdapter mPostListAdapter;
	private int index = 1;
	private EmoticonPopupable mEmoticonPopupable;
	
	private TextView name;
	private View ping;
	private View posting;
	private View setting;
	private View message;
	
	private TextView topicInfo;
	private TextView topicName;
	private TopicInfo topic;
	
	public PostListActivity() {
		mPostList = new ArrayList<PostEntity>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_list);
		
		//个推请求clientid,并注册接收监听
        PushManager.getInstance().initialize(this.getApplicationContext());
        
        if (UserInfo.loadSelfUserInfo() == null) {
			Intent intent = new Intent(this, IntroActivity.class);
			this.startActivity(intent);
			this.finish();
			return;
		}

		if (mEmoticonPopupable == null) {
			mEmoticonPopupable = new EmoticonInputDialog(this, this);
			//下方输入字数限制.
			mEmoticonPopupable.getEmoticonInputView().setMaxTextCount(140);
		}
		
		name = (TextView) findViewById(R.id.school);
		KuangInfo kuang = KuangInfo.loadSelfKuangInfo();
		if (kuang == null) {
			name.setText("黑洞");
		} else {
			name.setText(kuang.getName());
		}
		
		ping = findViewById(R.id.ping);
		posting = findViewById(R.id.posting);
		setting = findViewById(R.id.setting);
		message = findViewById(R.id.message);
		
		posting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				writePost("");
			}
		});
		
		ping.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				writePing();
			}
		});
		
		setting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PostListActivity.this, PersonalInfoActivity.class);
				startActivity(intent);
			}
		});
		
		message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PostListActivity.this, MessageListActivity.class);
				startActivity(intent);
			}
		});
		
		headerView = LayoutInflater.from(this).inflate(R.layout.activity_post_list_header, null);
		headerView.setOnClickListener(this);
		topicInfo = (TextView)headerView.findViewById(R.id.topic_info);
		topicName = (TextView)headerView.findViewById(R.id.topic_name);
		
		
        mListView = (PullToRefreshListView)findViewById(R.id.pull_to_refresh_listview);
        mListView.getRefreshableView().addHeaderView(headerView);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            	index = 1;
            	UnreadManager.dorequest(getHttpRequest(UnreadManager.REQUEST_UNREAD));
            	PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY_REFRESH), channelID, 1);
            }
        });
        mListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY_REFRESH_MORE),channelID, ++index);
			}
		});
        
        mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("ShowToast")
			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PostListActivity.this, PostDetailActivity.class);

        		Bundle bundle = new Bundle();
        		PostEntity post = mPostList.get(position-2);
        		Log.i(TAG, "clicked position " + position + " " + post.mContent + " " + post.mPictures.size());
        	    bundle.putParcelable(PostDetailActivity.SELECT_POST_INFO, post);     
        	    intent.putExtras(bundle);
        		
        		startActivity(intent);
            }
        });
        
        updateTopic();
        TopicInfoManager.getTopic(getHttpRequest(TopicInfoManager.GET));
        double rand = Math.random();
        if(rand<0.2)
        	VersionManager.doCheck(getHttpRequest(VersionManager.CHECK));
		PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY_REFRESH), channelID, 1);
		HttpHelper.feedback();
	}
	
	@Override
	protected void onResume() {
		UnreadManager.dorequest(getHttpRequest(UnreadManager.REQUEST_UNREAD));
		super.onResume();
	}
	
	private void updateTopic(){
		if(topic!=null){
			
			if(TextUtils.isEmpty(topic.topicInfo)){
				topicInfo.setVisibility(View.GONE);
			}else{
				topicInfo.setText(topic.topicInfo);
				topicInfo.setVisibility(View.VISIBLE);
			}
			
			topicName.setText(topic.topicName);
		}
	}
	
	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		switch (id) {
		case VersionManager.CHECK:
		{
			VersionInfo version = VersionManager.getUnReadCountFromJson(jsonObj);
			if(version.hasNewVersion){
				DownloadUtil.showDialog(this, version.description, version.url);
			}
			break;
		}
		case PostEntityManager.POSTING_KEY_REFRESH:
			mPostList.clear();
		case PostEntityManager.POSTING_KEY_REFRESH_MORE:
			PostEntityManager.fillPostListFromJson(jsonObj, mPostList);
			if(mPostListAdapter==null){
				mPostListAdapter = new PostListAdapter(this, mPostList);
				mListView.setAdapter(mPostListAdapter);
			}else{
				mPostListAdapter.notifyDataSetChanged();
				
				mListView.onRefreshComplete();
			}
			break;
		case ReplyInfoManager.DO_REPLY:
			ReturnInfo ri = ReplyInfoManager.getReplyReturnInfo(jsonObj);
			if(ri!=null&& ri.getReturnCode() == ReturnInfo.SUCCESS){
				Toast.makeText(this, "回复成功", Toast.LENGTH_SHORT).show();
				if(postEntity != null){
					postEntity.mReplyNum++;
					if(mPostListAdapter!=null)
						mPostListAdapter.notifyDataSetChanged();
				}
			}else{
				if(ri!=null){
					Toast.makeText(this, ri.getReturnMessage(), Toast.LENGTH_SHORT).show();
				}else
					Toast.makeText(this, "回复失败", Toast.LENGTH_SHORT).show();
			}
			break;
		case UpInfoManager.DO_UP:
			if(postEntity != null){
				
				ReturnInfo returnInfo = ReturnInfo.fromJSONObject(jsonObj);
				if(returnInfo != null  && returnInfo.getReturnCode() == ReturnInfo.SUCCESS){
					UpInfo upInfo = UpInfoManager.getReplyReturnInfo(jsonObj);
					postEntity.mLikeNum = upInfo.upCount;
					postEntity.mLikeSelected = upInfo.isDo();
					if(mPostListAdapter!=null)
						mPostListAdapter.notifyDataSetChanged();
					Toast.makeText(this, "点赞成功", Toast.LENGTH_SHORT).show();
				}else if(returnInfo != null){
					Toast.makeText(this, returnInfo.getReturnMessage(), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "点赞失败", Toast.LENGTH_SHORT).show();
				}
				
				
			}
			break;
		case UnreadManager.REQUEST_UNREAD:
			int count = UnreadManager.getUnReadCountFromJson(jsonObj);
			if(count>0)
				message.setSelected(true);
			else
				message.setSelected(false);
			break;
		case TopicInfoManager.GET:
		{
			topic = TopicInfoManager.fillReplyListFromJson(jsonObj);
			updateTopic();
			break;
		}
		default:
			break;
		}
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	
	private void writePing() {
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		if(userInfo!=null && !TextUtils.isEmpty(userInfo.getName())  && SexUtil.isValid(userInfo.getSex())){
			Intent intent = new Intent(this, PingActivity.class);
	    	startActivity(intent);
		}else{
			new AlertDialog.Builder(this)
			 .setMessage("请先设置昵称和性别后发送漂流瓶")
			 .setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(PostListActivity.this, PersonalInfoActivity.class);
			    	startActivity(intent);
				}
			}) .setNegativeButton("取消", null)
			 .show(); 
		}
	}
	
	private void writePost(String text) {
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		if(userInfo!=null && !TextUtils.isEmpty(userInfo.getName())  && SexUtil.isValid(userInfo.getSex())){
			Intent intent = new Intent(this, PostingActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("text", text);
			intent.putExtras(bundle);
	    	startActivity(intent);
		}else{
			new AlertDialog.Builder(this)
			 .setMessage("请先设置昵称和性别后发送帖子")
			 .setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(PostListActivity.this, PersonalInfoActivity.class);
			    	startActivity(intent);
				}
			}) .setNegativeButton("取消", null)
			 .show(); 
		}
	}

	
	public void doUp(PostEntity postEntity){
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		if(userInfo!=null && !TextUtils.isEmpty(userInfo.getName())  && SexUtil.isValid(userInfo.getSex())){
			
			this.postEntity = postEntity;
			UpInfoManager.doUp(getHttpRequest(UpInfoManager.DO_UP), postEntity.mPostId+"");
			
		}else{
			new AlertDialog.Builder(this)
			 .setMessage("请先设置昵称和性别后发送帖子")
			 .setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(PostListActivity.this, PersonalInfoActivity.class);
			    	startActivity(intent);
				}
			}) .setNegativeButton("取消", null)
			 .show(); 
		}
		
	}
	
	public void doReplay(PostEntity postEntity){
		
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		if(userInfo!=null && !TextUtils.isEmpty(userInfo.getName())  && SexUtil.isValid(userInfo.getSex())){

			if(mEmoticonPopupable!=null){
				if(postEntity!=null)
					mEmoticonPopupable.getEmoticonEditText().setHint("回复 "+postEntity.mUserName);
				mEmoticonPopupable.show();
				mEmoticonPopupable.getEmoticonSendButton().setTag(postEntity);
			}
			
		}else{
			new AlertDialog.Builder(this)
			 .setMessage("请先设置昵称和性别后发送帖子")
			 .setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(PostListActivity.this, PersonalInfoActivity.class);
			    	startActivity(intent);
				}
			}) .setNegativeButton("取消", null)
			 .show(); 
		}
		
	}

	/**
	 * 记录回复的帖子用于加线
	 */
	private PostEntity postEntity;
	
	@Override
	public void onSend(View v, String text) {
		if(!TextUtils.isEmpty(text)){
			postEntity = (PostEntity)v.getTag();
			
			ReplyInfoManager.doReplay(getHttpRequest(ReplyInfoManager.DO_REPLY), postEntity.mUserId, postEntity.mPostId+"", text);
			mEmoticonPopupable.cleatEmoticonEditText();
		}else{
			Toast.makeText(this, "请输入回复的话", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View arg0) {
		if(topic!=null)
			writePost(topic.topicName);
	}
}
