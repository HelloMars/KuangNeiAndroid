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
import me.kuangneipro.entity.UpInfo;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.manager.PostEntityManager;
import me.kuangneipro.manager.ReplyInfoManager;
import me.kuangneipro.manager.UnreadManager;
import me.kuangneipro.manager.UpInfoManager;
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

public class PostListActivity extends HttpActivity implements OnEmoticonMessageSendListener {
	private static final String TAG = PostListActivity.class.getSimpleName();  //tag 用于测试log用  
	
	public final static String SELECT_CHANNEL_INFO = "me.kuangnei.select.CHANNEL";
	
	private static final int channelID = 1;
	
	private PullToRefreshListView mListView;
	private ArrayList<PostEntity> mPostList;
	private PostListAdapter mPostListAdapter;
	private int index = 1;
	private EmoticonPopupable mEmoticonPopupable;
	
	private View posting;
	private View setting;
	private View message;
	
	private TextView name;
	
	public PostListActivity() {
		mPostList = new ArrayList<PostEntity>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_list);

		if (mEmoticonPopupable == null) {
			mEmoticonPopupable = new EmoticonInputDialog(this, this);
			//下方输入字数限制.
			mEmoticonPopupable.getEmoticonInputView().setMaxTextCount(100);
		}
		
		name = (TextView) findViewById(R.id.school);
		KuangInfo kuang = KuangInfo.loadSelfKuangInfo();
		if (kuang == null) {
			name.setText("黑洞");
		} else {
			name.setText(kuang.getName());
		}
		
		posting = findViewById(R.id.posting);
		setting = findViewById(R.id.setting);
		message = findViewById(R.id.message);
		
		posting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				writePost();
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
		
		
        mListView = (PullToRefreshListView)findViewById(R.id.pull_to_refresh_listview);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            	index = 1;
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
        		PostEntity post = mPostList.get(position-1);
        		Log.i(TAG, "clicked position " + position + " " + post.mContent + " " + post.mPictures.size());
        	    bundle.putParcelable(PostDetailActivity.SELECT_POST_INFO, post);     
        	    intent.putExtras(bundle);
        		
        		startActivity(intent);
            }
        });
		PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY_REFRESH), channelID, 1);
		UnreadManager.dorequest(getHttpRequest(UnreadManager.REQUEST_UNREAD));
	}
	
	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		switch (id) {
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

	
	private void writePost() {
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		if(userInfo!=null && !TextUtils.isEmpty(userInfo.getName())  && SexUtil.isValid(userInfo.getSex())){
			Intent intent = new Intent(this, PostingActivity.class);
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
		this.postEntity = postEntity;
		UpInfoManager.doUp(getHttpRequest(UpInfoManager.DO_UP), postEntity.mPostId+"");
	}
	
	public void doReplay(PostEntity postEntity){
		if(mEmoticonPopupable!=null){
			mEmoticonPopupable.show();
			mEmoticonPopupable.getEmoticonSendButton().setTag(postEntity);
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
}
