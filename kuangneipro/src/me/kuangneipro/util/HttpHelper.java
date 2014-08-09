package me.kuangneipro.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;


public class HttpHelper {
	
	public static interface RequestCallBackListener{
		public void onRequestComplete(int id,JSONObject jsonObj);
	}

	public static final String CHARSET =  HTTP.UTF_8;
	
	private final int id;
	private final Map<String,String> params;
	private volatile RequestCallBackListener requestCallBackListener;
	private volatile String url;
	
	public HttpHelper(int id){
		this(id,null);
	}
	
	public HttpHelper(int id,String url){
		this.id = id;
		this.url = url;
		params = new HashMap<String,String>();;
	}
	
	public HttpHelper setUrl(String url) {
		this.url = url;
		return this;
	}

	public void setRequestCallBackListener(
			RequestCallBackListener requestCallBackListener) {
		this.requestCallBackListener = requestCallBackListener;
	}


	public HttpHelper put(String key,String value){
		
		if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value))
			params.put(key, value);
		
		return this;
	}
	
	private HttpPost createHttpPost(){
		
		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> httpParams = new ArrayList<NameValuePair>();

		if (params != null && !params.isEmpty()) {
			for (Entry<String, String> entry : params.entrySet()) {
				httpParams.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		
		return httpPost;
	}
	
	private HttpGet createHttpGet(){
		
		StringBuilder urlSB = new StringBuilder(url);
		
		if(!url.endsWith("?")){
			urlSB.append('?');
		}
		
		boolean isNotFirstParam = false;
		
		if (params != null && !params.isEmpty()) {
			for (Entry<String, String> entry : params.entrySet()) {
				if(isNotFirstParam){
					urlSB.append('&');
				}else{
					isNotFirstParam = true;
				}
				urlSB.append(entry.getKey()).append('=').append(entry.getValue());
			}
		}

		HttpGet httpGet = new HttpGet(url);
		
		return httpGet;
	}
	
	public void asyncPost(){
		
		ApplicationWorker.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				if(requestCallBackListener!=null)
					requestCallBackListener.onRequestComplete(id,doHttpRequest(createHttpPost()));
			}
			
		});
		
	}
	
	public JSONObject syncPost(){
		
		return doHttpRequest(createHttpPost());
		
	}
	
	public void asyncGet(){
		
		ApplicationWorker.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				if(requestCallBackListener!=null)
					requestCallBackListener.onRequestComplete(id,doHttpRequest(createHttpGet()));
			}
			
		});
		
	}
	
	public JSONObject syncGet(){

		return doHttpRequest(createHttpGet());
		
	}
	
	private JSONObject doHttpRequest(HttpUriRequest request){
		
		JSONObject returnJson = null;
		HttpResponse httpResponse = null;
		try {
			httpResponse = new DefaultHttpClient().execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				returnJson = new JSONObject(EntityUtils.toString(httpResponse
						.getEntity()));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	        
		return returnJson;
		
	}
	
}
