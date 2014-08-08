package me.kuangneipro.core;

import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.HttpHelper.RequestCallBackListener;

import org.json.JSONObject;

import android.support.v4.app.ListFragment;


public class HttpListFragment extends ListFragment implements RequestCallBackListener {

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
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				requestComplete(jsonObj);
			}
			
		});
		
	}
}
