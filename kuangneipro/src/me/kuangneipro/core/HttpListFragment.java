package me.kuangneipro.core;

import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.HttpHelper.RequestCallBackListener;

import org.json.JSONObject;

import android.support.v4.app.ListFragment;


public class HttpListFragment extends ListFragment implements RequestCallBackListener {

	private HttpHelper httpRequest;
	
	
	protected final HttpHelper getHttpRequest(int id){
		httpRequest = new HttpHelper(id);
		httpRequest.setRequestCallBackListener(this);
		return httpRequest;
	}


	protected void requestComplete(int id,JSONObject jsonObj) {
		
	}
	

	@Override
	public final void onRequestComplete(final int id,final JSONObject jsonObj) {
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				requestComplete(id,jsonObj);
			}
			
		});
		
	}
}
