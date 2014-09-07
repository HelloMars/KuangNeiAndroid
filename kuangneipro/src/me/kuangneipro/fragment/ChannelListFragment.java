package me.kuangneipro.fragment;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.ChannelListAdapter;
import me.kuangneipro.activity.PostListActivity;
import me.kuangneipro.core.HttpListFragment;
import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.manager.ChannelEntityManager;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 频道列表页
 */
public class ChannelListFragment extends HttpListFragment {
	private static final String TAG = ChannelListFragment.class.getSimpleName(); // tag 用于测试log用
	private int mSectionNum;
	private ListView mListView;
	private List<ChannelEntity> mChannelList;
	private ChannelListAdapter mChannelListAdapter;
	
	private static final String ARG_SECTION_NUMBER = "section_number";

	
	public static ChannelListFragment newInstance(int sectionNumber) {
		ChannelListFragment fragment = new ChannelListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ChannelListFragment() {
		mChannelList = new ArrayList<ChannelEntity>();
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
		View v = inflater.inflate(R.layout.fragment_channel_list, container, false);
		mListView = (ListView) v.findViewById(android.R.id.list);
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ChannelEntityManager.getChannelList(getHttpRequest(ChannelEntityManager.CHANNEL_LIST_KEY));
		Log.i(TAG, "onAttach");
	}

	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		mChannelList.clear();
		ReturnInfo info = ReturnInfo.fromJSONObject(jsonObj);
		Log.i(TAG, "jsonObj == null ?" + (jsonObj == null));
		Log.i(TAG, "ReturnInfo:" + info.getReturnMessage() + " " + info.getReturnCode());
		ChannelEntityManager.fillChannelListFromJson(jsonObj, mChannelList);
		if(mChannelListAdapter==null){
			mChannelListAdapter = new ChannelListAdapter(getActivity(), mChannelList);
			mListView.setAdapter(mChannelListAdapter);
		}else{
			mChannelListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(TAG, "onListItemClick" + position);

		Intent intent = new Intent(getActivity(), PostListActivity.class);

		Bundle bundle = new Bundle();     
	    bundle.putParcelable(PostListActivity.SELECT_CHANNEL_INFO, mChannelList.get(position));     
	    intent.putExtras(bundle);     
		
		startActivity(intent);
	}
}