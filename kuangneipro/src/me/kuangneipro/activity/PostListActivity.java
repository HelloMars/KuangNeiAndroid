package me.kuangneipro.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.PostListAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.manager.PostEntityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class PostListActivity extends HttpActivity {
	private static final String TAG = PostListActivity.class.getSimpleName(); // tag ”√”⁄≤‚ ‘log”√  
	
	public final static String SELECT_CHANNEL_INFO = "me.kuangnei.select.CHANNEL";
	
	private ChannelEntity mChannel;
	
	private PullToRefreshListView mListView;
	private ArrayList<PostEntity> mPostList;
	private PostListAdapter mPostListAdapter;
	
	public PostListActivity() {
		mPostList = new ArrayList<PostEntity>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_list);
		
		mChannel = (ChannelEntity) (getIntent().getParcelableExtra(SELECT_CHANNEL_INFO));

		if(mChannel==null)
			mChannel = savedInstanceState.getParcelable(SELECT_CHANNEL_INFO);

		getSupportActionBar().setTitle(mChannel.getTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);

        mListView = (PullToRefreshListView)findViewById(R.id.pull_to_refresh_listview);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
            	PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY), mChannel.getId(), 1);
            }
        });
        
        PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY), mChannel.getId(), 1);
	}
	
	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		mPostList.clear();
		PostEntityManager.fillPostListFromJson(jsonObj, mPostList);
		if(mPostListAdapter==null){
			mPostListAdapter = new PostListAdapter(this, mPostList);
			mListView.setAdapter(mPostListAdapter);
		}else{
			mPostListAdapter.notifyDataSetChanged();
			
			mListView.onRefreshComplete();
		}
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(mChannel!=null)
			outState.putParcelable(SELECT_CHANNEL_INFO, mChannel);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.post_list, menu);
		return true;
	}
	
	private void writePost() {
		Intent intent = new Intent(this, PostingActivity.class);
		
		Bundle bundle = new Bundle();     
	    bundle.putParcelable(PostListActivity.SELECT_CHANNEL_INFO, mChannel);     
	    intent.putExtras(bundle);

    	startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_write_post:
			writePost();
			return true;
		case R.id.action_settings:
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
