package me.kuangneipro.activity;

import me.kuangneipro.R;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.fragment.PostListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PostListActivity extends HttpActivity {
	private static final String TAG = PostListActivity.class.getSimpleName(); // tag 用于测试log用  
	
	public final static String SELECT_CHANNEL_INFO = "me.kuangnei.select.CHANNEL";
	
	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;
	
	private ChannelEntity mChannel;
	
	private String[] mTabNames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_list);
		
		mChannel = (ChannelEntity) (getIntent().getParcelableExtra(SELECT_CHANNEL_INFO));
		mViewPager = (ViewPager) findViewById(R.id.pager);
		getSupportActionBar().setTitle(mChannel.getTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);

		
		final ActionBar actionBar = getSupportActionBar();

	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	        	mViewPager.setCurrentItem(tab.getPosition());
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	        }

	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	        }
	    };

	    mTabNames = getResources().getStringArray(R.array.post_list_tab_names);
        for (int i = 0; i < mTabNames.length; i++) {
        	Log.i(TAG, "addTab" + i);
            actionBar.addTab(actionBar.newTab()
                    		.setText(mTabNames[i])
                            .setTabListener(tabListener));
        }
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(),mTabNames,mChannel);

		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
	                @Override
	                public void onPageSelected(int position) {
	                	getSupportActionBar().setSelectedNavigationItem(position);
	                }
	            });
		
		
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

	
	public static class SectionsPagerAdapter extends FragmentPagerAdapter {
		private static final String TAG = SectionsPagerAdapter.class.getSimpleName(); // tag 用于测试log用  
		
		private String[] mTabNames;
		private ChannelEntity mChannel;
		
		public SectionsPagerAdapter(FragmentManager fm, String[] tabNames,ChannelEntity channel) {
			super(fm);
			mTabNames = tabNames;
			mChannel = channel;
		}

		@Override
		public Fragment getItem(int position) {
			Log.i(TAG, "getItem" + position);
			return PostListFragment.newInstance(position,mChannel);
		}

		@Override
		public int getCount() {
			Log.i(TAG, "getCount" + mTabNames.length);
			return mTabNames.length;
		}
	}

	
	

}
