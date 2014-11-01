package me.kuangneipro.activity;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.MessageListAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.MessageEntity;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.manager.MessageEntityManager;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MessageListActivity extends HttpActivity {
	private static final String TAG = MessageListActivity.class.getSimpleName(); // tag 用于测试log用
	private List<MessageEntity> mMessageList;
	private MessageListAdapter mMessageListAdapter;
	private int index = 1;
	private PullToRefreshListView mListView;
	private View back;
	
	
	public MessageListActivity() {
		mMessageList = new ArrayList<MessageEntity>();
	}

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_message_list);
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
            	MessageEntityManager.getMessageList(
            			getHttpRequest(MessageEntityManager.MESSAGE_KEY_REFRESH), 1);
            }
        });
        mListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				MessageEntityManager.getMessageList(
						getHttpRequest(MessageEntityManager.MESSAGE_KEY_REFRESH_MORE), ++index);
			}
		});
        
        mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "clicked position " + position);
            }
        });
        
        MessageEntityManager.getMessageList(getHttpRequest(MessageEntityManager.MESSAGE_KEY_REFRESH), 1);
	}
	


	@Override
	protected void requestComplete(int id, JSONObject jsonObj) {
		super.requestComplete(id, jsonObj);
		switch (id) {
		case MessageEntityManager.MESSAGE_KEY_REFRESH:
			mMessageList.clear();
		case MessageEntityManager.MESSAGE_KEY_REFRESH_MORE:
			ReturnInfo info = ReturnInfo.fromJSONObject(jsonObj);
			Log.i(TAG, "ReturnInfo:" + info.getReturnMessage() + " " + info.getReturnCode());
			MessageEntityManager.fillMessageListFromJson(jsonObj, mMessageList);
			if(mMessageListAdapter==null){
				mMessageListAdapter = new MessageListAdapter(this, mMessageList);
				mListView.setAdapter(mMessageListAdapter);
			}else{
				mMessageListAdapter.notifyDataSetChanged();
				mListView.onRefreshComplete();
			}
			break;
		default:
			break;
		}
	}
}
