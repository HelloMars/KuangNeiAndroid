package me.kuangneipro.core;

import me.kuangneipro.R;
import me.kuangneipro.activity.PersonalInfoActivity;
import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.HttpHelper.RequestCallBackListener;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
/**
 * 可发送Http请求的基类
 * @author connor
 */
public class HttpActivity extends ActionBarActivity implements RequestCallBackListener {

	private HttpHelper httpRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false); 
	}
	
	protected final HttpHelper getHttpRequest(int id){
		httpRequest = new HttpHelper(this,id);
		httpRequest.setRequestCallBackListener(this);
		return httpRequest;
	}


	protected void requestComplete(int id,JSONObject jsonObj) {
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch (id) {
		case R.id.action_search:
			return true;
		case R.id.action_settings:
			Intent intent = new Intent(HttpActivity.this, PersonalInfoActivity.class);
    		startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	public final void onRequestComplete(final int id,final JSONObject jsonObj) {
		if(!isFinishing())
			runOnUiThread(new Runnable() {
			
				@Override
				public void run() {
					requestComplete(id,jsonObj);
				}
				
			});
		
	}
	
	
}
