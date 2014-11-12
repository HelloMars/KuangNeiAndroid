package me.kuangneipro.activity;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.MessageListAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.emoticon.EmoticonInputDialog;
import me.kuangneipro.emoticon.EmoticonInputView.OnEmoticonMessageSendListener;
import me.kuangneipro.emoticon.EmoticonPopupable;
import me.kuangneipro.entity.MessageInfo;
import me.kuangneipro.entity.ReplyInfo;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.manager.MessageInfoManager;
import me.kuangneipro.manager.ReplyInfoManager;
import me.kuangneipro.util.SexUtil;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MessageListActivity extends HttpActivity implements OnEmoticonMessageSendListener {
	private static final String TAG = MessageListActivity.class.getSimpleName(); // tag 用于测试log用
	private PullToRefreshListView mListView;
	private List<MessageInfo> mMessageList;
	private MessageListAdapter mMessageListAdapter;
	private int index = 1;
	private View back;
	private EmoticonPopupable mEmoticonPopupable;
	
	public MessageListActivity() {
		mMessageList = new ArrayList<MessageInfo>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_message_list);
		
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
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            	index = 1;
            	MessageInfoManager.getMessageList(getHttpRequest(MessageInfoManager.MESSAGE_KEY_REFRESH), 1);
            }
        });
        mListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				MessageInfoManager.getMessageList(getHttpRequest(MessageInfoManager.MESSAGE_KEY_REFRESH_MORE), ++index);
			}
		});
        
        mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(mMessageListAdapter!=null){
					position -- ;
					if(position<0)
						position = 0;
					
					MessageInfo messageInfo = mMessageListAdapter.getItem(position);
					if(messageInfo!=null && messageInfo.postEntity!=null){
						Intent intent = new Intent(MessageListActivity.this, PostDetailActivity.class);

		        		Bundle bundle = new Bundle();
		        		
		        		bundle.putParcelable(PostDetailActivity.SELECT_POST_INFO, messageInfo.postEntity);  
		        		
	        			if(messageInfo.postEntity.mChannelId==0)
	        				bundle.putBoolean(PostDetailActivity.IS_PING_POST, true);
		        	    intent.putExtras(bundle);
		        		
		        		startActivity(intent);
					}
				}
            }
        });
        
        MessageInfoManager.getMessageList(getHttpRequest(MessageInfoManager.MESSAGE_KEY_REFRESH), 1);
	}
	


	@Override
	protected void requestComplete(int id, JSONObject jsonObj) {
		super.requestComplete(id, jsonObj);
		switch (id) {
		case MessageInfoManager.MESSAGE_KEY_REFRESH:
			mMessageList.clear();
		case MessageInfoManager.MESSAGE_KEY_REFRESH_MORE:
			ReturnInfo info = ReturnInfo.fromJSONObject(jsonObj);
			if(info!=null && info.getReturnCode() == ReturnInfo.SUCCESS){
				Log.i(TAG, "ReturnInfo:" + info.getReturnMessage() + " " + info.getReturnCode());
				MessageInfoManager.fillMessageListFromJson(jsonObj, mMessageList);
				if(mMessageListAdapter==null){
					mMessageListAdapter = new MessageListAdapter(this, mMessageList);
					mListView.setAdapter(mMessageListAdapter);
				}else{
					mMessageListAdapter.notifyDataSetChanged();
					mListView.onRefreshComplete();
				}
			}
			break;
		case ReplyInfoManager.DO_REPLY:
			ReturnInfo ri = ReplyInfoManager.getReplyReturnInfo(jsonObj);
			if(ri!=null&& ri.getReturnCode() == ReturnInfo.SUCCESS){
				Toast.makeText(this, "回复成功", Toast.LENGTH_SHORT).show();
				MessageInfoManager.getMessageList(getHttpRequest(MessageInfoManager.MESSAGE_KEY_REFRESH), 1);
			}else{
				if(ri!=null){
					Toast.makeText(this, ri.getReturnMessage(), Toast.LENGTH_SHORT).show();
				}else
					Toast.makeText(this, "回复失败", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	
	public void doReplay(ReplyInfo postEntity){
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		if(userInfo!=null && !TextUtils.isEmpty(userInfo.getName())  && SexUtil.isValid(userInfo.getSex())){
			
			if(mEmoticonPopupable!=null){
				if(postEntity!=null&&postEntity.replyUser!=null)
					mEmoticonPopupable.getEmoticonEditText().setHint("回复 "+postEntity.replyUser.name);
				mEmoticonPopupable.show();
				mEmoticonPopupable.getEmoticonSendButton().setTag(postEntity);
			}
			
		}else{
			new AlertDialog.Builder(this)
			 .setMessage("请先设置昵称和性别后发送帖子")
			 .setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(MessageListActivity.this, PersonalInfoActivity.class);
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
			if(replyInfo == null ) 
				return;
			if(replyInfo.replyUser!=null){
				Log.e("xxx", replyInfo.replyUser.id+"  "+replyInfo.postId+"");
				ReplyInfoManager.doReplay(getHttpRequest(ReplyInfoManager.DO_REPLY), replyInfo.replyUser.id+"", replyInfo.postId+"", text);
			}else{
				Log.e("xxx", replyInfo.toUser.id+" ！"+replyInfo.postId+"");
				ReplyInfoManager.doReplay(getHttpRequest(ReplyInfoManager.DO_REPLY), replyInfo.toUser.id+"", replyInfo.postId+"", text);
			}
			mEmoticonPopupable.cleatEmoticonEditText();
		}else{
			Toast.makeText(this, "请输入回复的话", Toast.LENGTH_SHORT).show();
		}
	}
}
