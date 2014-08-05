package me.kuangneipro.activity;

import java.util.ArrayList;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.ChannelListAdapter;
import me.kuangneipro.entity.ChannelEntity;
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
import android.widget.TextView;

import com.igexin.sdk.PushManager;

public class MainActivity extends ActionBarActivity {
	public final static String EXTRA_MESSAGE_TITLE = "me.kuangnei.MESSAGE";
	
	private static final String TAG = MainActivity.class.getSimpleName(); // tag 用于测试log用  
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private static final int TAB_NUM = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PushManager.getInstance().initialize(this.getApplicationContext());

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
	    String[] tabNames = getResources().getStringArray(R.array.tab_names);
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/*
	private void openSearch() {
		//Intent intent = new Intent(this, PostingActivity.class);
		Intent intent = new Intent(this, PostListActivity.class);
    	//EditText editText = (EditText) findViewById(R.id.edit_message);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
	}
	*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_search:
			//openSearch();
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
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public String tag = this.getClass().getSimpleName(); // tag 用于测试log用  
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			Log.i(tag, "getItem" + position);
			return PlaceholderFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			Log.i(tag, "getCount" + TAB_NUM);
			return TAB_NUM;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends ListFragment  {
		public String tag = this.getClass().getSimpleName(); // tag 用于测试log用  
		private int mSectionNum;
		private ListView mListView;
		private ArrayList<ChannelEntity> mChannelList;
		
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
			Log.i(tag, "onActivityCreated(((" + mSectionNum);
	        super.onActivityCreated(savedInstanceState);
	        
	        new RetriveChannelList().execute("http://kuangnei.me/kuangnei/api/channellist/");
	        /*ChannelData[] channels = new ChannelData[] {
            		new ChannelData("兴趣", "不有趣可能会被踢得很惨哦 不有趣可能会被踢得很惨哦 不有趣可能会被踢得很惨哦。真的！"),
            		new ChannelData("缘分", "约会、表白、同性异性不限 约会、表白、同性异性不限 约会、表白、同性异性不限")};
	        mListView.setAdapter(new ChannelListAdapter(getActivity(), channels));
	        */
	        Log.i(tag, "onActivityCreated)))" + mSectionNum);
	    }
		
		class RetriveChannelList extends AsyncTask<String, Void, ArrayList<ChannelEntity>> {
    	    protected ArrayList<ChannelEntity> doInBackground(String... urls) {
    	        try {
    	        	mChannelList = new ArrayList<ChannelEntity>();
    	        	JSONObject jsonObj = JasonReader.readJsonFromUrl(urls[0]);
    	        	JSONArray jsonarray = jsonObj.getJSONArray("list");
    	        	for (int i = 0; i < jsonarray.length(); i++) {
    	        		JSONObject oneJson = jsonarray.getJSONObject(i);
                        ChannelEntity channel = new ChannelEntity(oneJson.getString("title"), oneJson.getString("subtitle"));
                        mChannelList.add(channel);
                    }
    	        	return mChannelList;
    	        	
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	            return null;
    	        }
    	    }

    	    protected void onPostExecute(ArrayList<ChannelEntity> channelList) {
    	        // TODO: check this.exception 
    	        // TODO: do something with the feed
    	    	for (int i = 0; i < channelList.size(); ++i)
    	    		System.out.println((channelList.get(i).toString()));
    	    	mListView.setAdapter(new ChannelListAdapter(getActivity(), channelList));
    	    }
    	}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_main, container, false);
            mListView = (ListView)v.findViewById(android.R.id.list);
            
            Log.i(tag, "onCreateView" + mSectionNum);
            return v;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
	        super.onViewCreated(view, savedInstanceState);
		}
		
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i(tag, "onListItemClick" + position);
			
			Intent intent = new Intent(getActivity(), PostListActivity.class);
	    	//EditText editText = (EditText) findViewById(R.id.edit_message);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
			
			TextView title = (TextView) v.findViewById(R.id.title);
			intent.putExtra(EXTRA_MESSAGE_TITLE, title.getText().toString());
			
	    	startActivity(intent);
		}
	}
	
	

}