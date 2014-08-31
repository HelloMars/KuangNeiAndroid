package me.kuangneipro.core;

import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.HttpHelper.RequestCallBackListener;

import org.json.JSONObject;

import android.support.v4.app.Fragment;


public class HttpFragment extends Fragment implements RequestCallBackListener {

	private HttpHelper httpRequest;
	
	
	protected final HttpHelper getHttpRequest(int id){
		httpRequest = new HttpHelper(getActivity(),id);
		httpRequest.setRequestCallBackListener(this);
		return httpRequest;
	}


	protected void requestComplete(int id,JSONObject jsonObj) {
		
	}
	

	@Override
	public final void onRequestComplete(final int id,final JSONObject jsonObj) {
		if(getActivity()!=null && !getActivity().isFinishing())
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					requestComplete(id,jsonObj);
				}
				
			});
		
	}
}
