package me.kuangneipro.fragment;

import java.util.ArrayList;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.PostListAdapter;
import me.kuangneipro.core.HttpListFragment;
import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.manager.PostEntityManager;

import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PostListFragment extends HttpListFragment  {
	
	private static final String TAG = PostListFragment.class.getSimpleName(); // tag ”√”⁄≤‚ ‘log”√  
	
	private int mSectionNum;
	private PullToRefreshListView mListView;
	private ArrayList<PostEntity> mPostList;
	private PostListAdapter mPostListAdapter;
	
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_SECTION_CHANNEL = "section_channel";
	
	@SuppressWarnings("unused")
	private ChannelEntity mChannel;
	
	public static PostListFragment newInstance(int sectionNumber,ChannelEntity channel) {
		Log.i(TAG, "newInstance" + sectionNumber);
		PostListFragment fragment = new PostListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putParcelable(ARG_SECTION_CHANNEL, channel);
		fragment.setArguments(args);
		return fragment;
	}

	public PostListFragment() {
		mPostList = new ArrayList<PostEntity>();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSectionNum = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;
        mChannel = getArguments() != null ? (ChannelEntity)getArguments().getParcelable(ARG_SECTION_CHANNEL) : null;
    }
	
	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		mPostList.clear();
		PostEntityManager.fillPostListFromJson(jsonObj,mPostList);
		if(mPostListAdapter==null){
			mPostListAdapter = new PostListAdapter(getActivity(), mPostList);
			mListView.setAdapter(mPostListAdapter);
		}else{
			mPostListAdapter.notifyDataSetChanged();
			
			mListView.onRefreshComplete();
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int layout = this.mSectionNum == 0 ? R.layout.fragment_latest : R.layout.fragment_main;
		View v = inflater.inflate(layout, container, false);
        mListView = (PullToRefreshListView)v.findViewById(R.id.pull_to_refresh_listview);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
            	PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY), mChannel.getId(), 1);
            }
        });
        
        PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY), mChannel.getId(), 1);
        
        Log.i(TAG, "onCreateView" + mSectionNum);
        return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(TAG, "onListItemClick" + position);
		
	}
}