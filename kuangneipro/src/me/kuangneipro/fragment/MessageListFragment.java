package me.kuangneipro.fragment;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.MessageListAdapter;
import me.kuangneipro.core.HttpListFragment;
import me.kuangneipro.entity.MessageEntity;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.manager.MessageEntityManager;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MessageListFragment extends HttpListFragment {
	private static final String TAG = MessageListFragment.class.getSimpleName(); // tag 用于测试log用
	private int mSectionNum;
	private List<MessageEntity> mMessageList;
	private MessageListAdapter mMessageListAdapter;
	private int index = 1;
	private PullToRefreshListView mListView;
	
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	public static MessageListFragment newInstance(int sectionNumber) {
		MessageListFragment fragment = new MessageListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	public MessageListFragment() {
		mMessageList = new ArrayList<MessageEntity>();
	}

	public int getSectionNum() {
		return mSectionNum;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSectionNum = getArguments() != null ? getArguments().getInt(
				ARG_SECTION_NUMBER) : 1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_message_list, container, false);
		mListView = (PullToRefreshListView) v.findViewById(R.id.list);
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
		return v;
	}

    @Override
	public void onAttach(Activity activity) {
    	Log.i(TAG, "onAttach");
		super.onAttach(activity);
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
				mMessageListAdapter = new MessageListAdapter(getActivity(), mMessageList);
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
