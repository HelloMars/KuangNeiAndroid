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
	
	
	protected final HttpHelper getHttpRequest(int id){
		httpRequest = new HttpHelper(this,id);
		httpRequest.setRequestCallBackListener(this);
		return httpRequest;
	}


	protected void requestComplete(int id,JSONObject jsonObj) {
		
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
