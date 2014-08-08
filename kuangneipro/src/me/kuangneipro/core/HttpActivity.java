package me.kuangneipro.core;

import org.json.JSONObject;

import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.HttpHelper.RequestCallBackListener;
import android.support.v7.app.ActionBarActivity;
/**
 * 可发送Http请求的基类
 * @author connor
 */
public class HttpActivity extends ActionBarActivity implements RequestCallBackListener {

	private HttpHelper httpRequest;
	
	
	
	protected final HttpHelper getHttpRequest(){
		httpRequest = new HttpHelper();
		httpRequest.setRequestCallBackListener(this);
		return httpRequest;
	}


	protected void requestComplete(JSONObject jsonObj) {
		
	}
	

	@Override
	public final void onRequestComplete(final JSONObject jsonObj) {
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				requestComplete(jsonObj);
			}
			
		});
		
	}
	
	
}
