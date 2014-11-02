package me.kuangneipro.activity;

import me.kuangneipro.R;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.fragment.ChannelListFragment;
import me.kuangneipro.fragment.MessageListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.igexin.sdk.PushManager;

public class MainActivity extends HttpActivity {
	
	private static final String TAG = MainActivity.class.getSimpleName(); // tag 用于测试log用  

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;
	
	private int TAB_NUM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//个推请求clientid,并注册接收监听
		PushManager.getInstance().initialize(this.getApplicationContext());
		
		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		
		String[] tabNames = getResources().getStringArray(R.array.tab_names);
		
		TAB_NUM = tabNames.length;
		
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(
			new ViewPager.SimpleOnPageChangeListener() {
			    @Override
			    public void onPageSelected(int position) {
			    	getSupportActionBar().setSelectedNavigationItem(position);
			    }
			});
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
			Log.i(tag, "getItem" + position);
			switch (position) {
			case 0: // 频道
				return ChannelListFragment.newInstance(position);
			case 1: // 消息
				return MessageListFragment.newInstance(position);
			case 2: // 发现
				return ChannelListFragment.newInstance(position);
			default:
				return ChannelListFragment.newInstance(position);
			}
		}

		@Override
		public int getCount() {
			return TAB_NUM;
		}
	}
}
