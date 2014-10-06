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

public class MessageListFragment extends HttpListFragment {
	private static final String TAG = MessageListFragment.class.getSimpleName(); // tag 用于测试log用
	private int mSectionNum;
	private List<MessageEntity> mMessageList;
	private MessageListAdapter mMessageListAdapter;
	
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
		return v;
	}

    @Override
	public void onAttach(Activity activity) {
    	Log.i(TAG, "onAttach");
		super.onAttach(activity);
		MessageEntityManager.getMessageList(getHttpRequest(MessageEntityManager.MESSAGE_LIST_KEY), 1);
	}

	@Override
	protected void requestComplete(int id, JSONObject jsonObj) {
		super.requestComplete(id, jsonObj);
		mMessageList.clear();
		if (jsonObj != null) {
			ReturnInfo info = ReturnInfo.fromJSONObject(jsonObj);
			Log.i(TAG, "ReturnInfo:" + info.getReturnMessage() + " " + info.getReturnCode());
			MessageEntityManager.fillMessageListFromJson(jsonObj, mMessageList);
			if (mMessageListAdapter == null) {
				mMessageListAdapter = new MessageListAdapter(getActivity(), mMessageList);
				setListAdapter(mMessageListAdapter);
			} else {
				mMessageListAdapter.notifyDataSetChanged();
			}
		}
	}
}
