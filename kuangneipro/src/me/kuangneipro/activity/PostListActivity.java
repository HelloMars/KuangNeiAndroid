package me.kuangneipro.activity;

import java.util.ArrayList;

import me.kuangneipro.Adapter.PostListAdapter;
import me.kuangneipro.activity.MainActivity;
import me.kuangneipro.R;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.util.JasonReader;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PostListActivity extends ActionBarActivity {
	private static final String TAG = PostListActivity.class.getSimpleName(); // tag 用于测试log用  
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;
	
	private static final int TAB_NUM = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_list);
		
		Intent intent = getIntent();
		String title = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_TITLE);
		
		getSupportActionBar().setTitle(title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
	                @Override
	                public void onPageSelected(int position) {
	                    // When swiping between pages, select the
	                    // corresponding tab.
	                	getSupportActionBar().setSelectedNavigationItem(position);
	                }
	            });
		
		final ActionBar actionBar = getSupportActionBar();

	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // show the given tab
	        	mViewPager.setCurrentItem(tab.getPosition());
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // hide the given tab
	        }

	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // probably ignore this event
	        }
	    };

	    // Add 3 tabs, specifying the tab's text and TabListener
	    String[] tabNames = getResources().getStringArray(R.array.post_list_tab_names);
        for (int i = 0; i < TAB_NUM; i++) {
        	Log.i(TAG, "addTab" + i);
            actionBar.addTab(actionBar.newTab()
                    		.setText(tabNames[i])
                            .setTabListener(tabListener));
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_list, menu);
		return true;
	}
	
	private void writePost() {
		Intent intent = new Intent(this, PostingActivity.class);
		//TODO 这里应传入channel_id
//		intent.putExtra(PostingActivity.CHANNEL_ID_KEY, mSectionsPagerAdapter.getItem(position))
    	//EditText editText = (EditText) findViewById(R.id.edit_message);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public static class SectionsPagerAdapter extends FragmentPagerAdapter {
		private static final String TAG = SectionsPagerAdapter.class.getSimpleName(); // tag 用于测试log用  
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			Log.i(TAG, "getItem" + position);
			return PlaceholderFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			Log.i(TAG, "getCount" + TAB_NUM);
			return TAB_NUM;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends ListFragment  {
		
		private static final String TAG = PlaceholderFragment.class.getSimpleName(); // tag 用于测试log用  
		
		private int mSectionNum;
		private ListView mListView;
		private ArrayList<PostEntity> mPostList;
		
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			Log.i("PlaceholderFragment", "newInstance" + sectionNumber);
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mSectionNum = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;
        }
		
		@Override
	    public void onActivityCreated(Bundle savedInstanceState) {
			Log.i(TAG, "onActivityCreated(((" + mSectionNum);
	        super.onActivityCreated(savedInstanceState);
	        
	        new RetrivePostList().execute("http://kuangnei.me/kuangnei/api/postlist/");
	        /*ChannelData[] channels = new ChannelData[] {
            		new ChannelData("兴趣", "不有趣可能会被踢得很惨哦 不有趣可能会被踢得很惨哦 不有趣可能会被踢得很惨哦。真的！"),
            		new ChannelData("缘分", "约会、表白、同性异性不限 约会、表白、同性异性不限 约会、表白、同性异性不限")};
	        mListView.setAdapter(new ChannelListAdapter(getActivity(), channels));
	        */
	        Log.i(TAG, "onActivityCreated)))" + mSectionNum);
	    }
		
		class RetrivePostList extends AsyncTask<String, Void, ArrayList<PostEntity>> {
    	    protected ArrayList<PostEntity> doInBackground(String... urls) {
    	    	mPostList = new ArrayList<PostEntity>();
    	        try {
    	        	JSONObject jsonObj = JasonReader.readJsonFromUrl(urls[0]);
    	        	JSONArray jsonarray = jsonObj.getJSONArray("list");
    	        	for (int i = 0; i < jsonarray.length()-1; i++) {
    	        		JSONObject oneJson = jsonarray.getJSONObject(i);
    	        		JSONObject user = oneJson.getJSONObject("user");
    	        		if (user == null)
    	        			continue;
    	        		PostEntity channel = new PostEntity(
    	        				user.getInt("id"),
    	        				user.getString("name"),
    	        				oneJson.getString("title"),
    	        				oneJson.getString("content"),
    	        				oneJson.getInt("opposedCount"),
    	        				oneJson.getInt("upCount"),
    	        				oneJson.getString("postTime"));
    	        		mPostList.add(channel);
                    }
    	        	return mPostList;
    	        	
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	            return mPostList;
    	        }
    	    }

    	    protected void onPostExecute(ArrayList<PostEntity> postList) {
    	        // TODO: check this.exception 
    	        // TODO: do something with the feed
    	    	for (int i = 0; i < postList.size(); ++i)
    	    		System.out.println((postList.get(i).toString()));
    	    	mListView.setAdapter(new PostListAdapter(getActivity(), postList));
    	    }
    	}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int layout = this.mSectionNum == 0 ? R.layout.fragment_latest : R.layout.fragment_main;
			View v = inflater.inflate(layout, container, false);
            mListView = (ListView)v.findViewById(android.R.id.list);
            
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
	
	

}