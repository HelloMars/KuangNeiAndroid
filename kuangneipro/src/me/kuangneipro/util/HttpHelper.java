package me.kuangneipro.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.kuangneipro.activity.RegisterActivity;
import me.kuangneipro.activity.SignInActivity;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.util.LoginUtil.OnSignInLisener;

import org.OpenUDID.OpenUDID_manager;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
	private Activity activity;
	private boolean isSync;
	
	public HttpHelper(Activity activity,int id){
		this(activity,id,null);
	}
	
	public HttpHelper(Activity activity,int id,String url){
		this.id = id;
		this.url = url;
		this.activity = activity;
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
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(httpParams,CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
		
		HttpGet httpGet = new HttpGet(urlSB.toString());
		String sessionId = LoginUtil.loadSession();
		if(!TextUtils.isEmpty(sessionId))
			httpGet.setHeader(LoginUtil.COOKIE_KEY, LoginUtil.SESSION_KEY+"="+sessionId);
		return httpGet;
	}
	
	private void checkSession(final HttpUriRequest request){
		String sessionId = LoginUtil.loadSession();
		if(!TextUtils.isEmpty(sessionId))
			request.setHeader(LoginUtil.COOKIE_KEY, LoginUtil.SESSION_KEY+"="+sessionId);
		else{
			if(OpenUDID_manager.isInitialized() && UserInfo.loadSelfUserInfo()!=null && !TextUtils.isEmpty(UserInfo.loadSelfUserInfo().getUsername())  &&   !TextUtils.isEmpty(UserInfo.loadSelfUserInfo().getPassword()) ) {
				String deviceID = OpenUDID_manager.getOpenUDID();
				LoginUtil.signin(UserInfo.loadSelfUserInfo().getUsername(), UserInfo.loadSelfUserInfo().getPassword(), deviceID, new OnSignInLisener() {
					
					@Override
					public void onSignInFinish(boolean isSuccess, UserInfo userInfo) {
						if(isSuccess){
							String sessionId = LoginUtil.loadSession();
							if(!TextUtils.isEmpty(sessionId))
								request.setHeader(LoginUtil.COOKIE_KEY, LoginUtil.SESSION_KEY+"="+sessionId);
						}else{
							if(activity!=null){
								Intent intent = new Intent(activity, RegisterActivity.class);
								activity.startActivity(intent);
								activity.finish();
							}
							
						}
						
					}
				});
			}
			else {
				if(activity!=null){
					Intent intent = new Intent(activity, SignInActivity.class);
					activity.startActivity(intent);
					activity.finish();
				}
			}
		}
	}
	
	public void asyncPost(){
		isSync = false;
		ApplicationWorker.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				final HttpPost httpPost = createHttpPost();
				checkSession(httpPost);
				if(requestCallBackListener!=null)
					requestCallBackListener.onRequestComplete(id,doHttpRequest(httpPost));
			}
			
		});
		
	}
	
	public JSONObject syncPost(){
		isSync = true;
		final HttpPost httpPost = createHttpPost();
		checkSession(httpPost);
		return doHttpRequest(httpPost);
		
	}
	
	public void asyncGet(){
		isSync = false;
		ApplicationWorker.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				final HttpGet httpGet = createHttpGet();
				checkSession(httpGet);
				if(requestCallBackListener!=null)
					requestCallBackListener.onRequestComplete(id,doHttpRequest(httpGet));
			}
			
		});
		
	}
	
	public JSONObject syncGet(){
		isSync = true;
		final HttpGet httpGet = createHttpGet();
		checkSession(httpGet);
		return doHttpRequest(httpGet);
		
	}
	
	private JSONObject doHttpRequest(final HttpUriRequest request){
		
		JSONObject returnJson = null;
		HttpResponse httpResponse = null;
		try {
			httpResponse = new DefaultHttpClient().execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				returnJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
			}else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
				if(OpenUDID_manager.isInitialized() && !TextUtils.isEmpty(UserInfo.loadSelfUserInfo().getUsername())  &&   !TextUtils.isEmpty(UserInfo.loadSelfUserInfo().getPassword()) ) {
					String deviceID = OpenUDID_manager.getOpenUDID();
					if(LoginUtil.signin(UserInfo.loadSelfUserInfo().getUsername(), UserInfo.loadSelfUserInfo().getPassword(), deviceID)){
						String sessionId = LoginUtil.loadSession();
						if(!TextUtils.isEmpty(sessionId))
							request.setHeader(LoginUtil.COOKIE_KEY, LoginUtil.SESSION_KEY+"="+sessionId);
						if(isSync){
							return doHttpRequest(request);
						}
					}else{
						if(activity!=null){
							Intent intent = new Intent(activity, RegisterActivity.class);
							activity.startActivity(intent);
							activity.finish();
						}
					}
				}
				else {
					if(activity!=null){
						Intent intent = new Intent(activity, RegisterActivity.class);
						activity.startActivity(intent);
						activity.finish();
					}
				}
				returnJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
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
