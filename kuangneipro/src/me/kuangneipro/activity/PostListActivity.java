package me.kuangneipro.activity;

import java.util.ArrayList;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.PostListAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.manager.ChannelEntityManager;
import me.kuangneipro.manager.PostEntityManager;
import me.kuangneipro.util.ApplicationWorker;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class PostListActivity extends HttpActivity {
	private static final String TAG = PostListActivity.class.getSimpleName();  //tag 用于测试log用  
	
	public final static String SELECT_CHANNEL_INFO = "me.kuangnei.select.CHANNEL";
	
	private ChannelEntity mChannel;
	
	private PullToRefreshListView mListView;
	private ArrayList<PostEntity> mPostList;
	private PostListAdapter mPostListAdapter;
	private int index = 1;
	
	public PostListActivity() {
		mPostList = new ArrayList<PostEntity>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_list);
		
		mChannel = (ChannelEntity) (getIntent().getParcelableExtra(SELECT_CHANNEL_INFO));

		if(mChannel==null && savedInstanceState != null)
			mChannel = savedInstanceState.getParcelable(SELECT_CHANNEL_INFO);
		if(mChannel==null)
			mChannel = ChannelEntityManager.loadChannel();
		if(mChannel==null){
			ApplicationWorker.getInstance().executeOnUIThrean(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			});
			return;
		}
		
		
		getSupportActionBar().setTitle(mChannel.getTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);

        mListView = (PullToRefreshListView)findViewById(R.id.pull_to_refresh_listview);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            	index = 1;
            	PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY_REFRESH), mChannel.getId(), 1);
            }
        });
        mListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY_REFRESH_MORE), mChannel.getId(), ++index);
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
        
        PostEntityManager.getPostList(getHttpRequest(PostEntityManager.POSTING_KEY_REFRESH), mChannel.getId(), 1);
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

		default:
			break;
		}
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(mChannel!=null)
			outState.putParcelable(SELECT_CHANNEL_INFO, mChannel);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onStop() {
		if(mChannel!=null)
			ChannelEntityManager.saveChannel(mChannel);
		super.onStop();
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
