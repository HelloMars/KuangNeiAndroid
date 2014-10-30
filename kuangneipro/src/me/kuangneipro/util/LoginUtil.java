package me.kuangneipro.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import me.kuangneipro.core.KuangNeiApplication;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.entity.UserInfo;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class LoginUtil {
	private static final String TAG = "LoginUtil";
	private static final String CHARSET = "UTF-8";

	private static final AllowAllHostnameVerifier HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
	private static X509TrustManager xtm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
	private static X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };

	@SuppressLint("TrulyRandom") 
	private static boolean doHttpsPOSTRequest(String path,
			Map<String, String> params, String encoding) {

		try {
			// 1> 组拼实体数据
			// method=save&name=liming&timelength=100
			StringBuilder entityBuilder = new StringBuilder("");
			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					entityBuilder.append(entry.getKey()).append('=');
					entityBuilder.append(URLEncoder.encode(entry.getValue(),
							encoding));
					entityBuilder.append('&');
				}
				entityBuilder.deleteCharAt(entityBuilder.length() - 1);
			}
			byte[] entity = entityBuilder.toString().getBytes();
			URL url = new URL(path);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			if (conn instanceof HttpsURLConnection) {
				// Trust all certificates
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(new KeyManager[0], xtmArray, new SecureRandom());
				SSLSocketFactory socketFactory = context.getSocketFactory();
				((HttpsURLConnection) conn).setSSLSocketFactory(socketFactory);
				((HttpsURLConnection) conn)
						.setHostnameVerifier(HOSTNAME_VERIFIER);
			}
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);// 允许输出数据
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			OutputStream outStream = conn.getOutputStream();
			outStream.write(entity);
			outStream.flush();
			outStream.close();

			if (conn.getResponseCode() == 200) {
				String key;
				for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
					if (key.equalsIgnoreCase(SET_COOKIE_KEY)) {
						String cookieVal = conn.getHeaderField(i);
						cookieVal = cookieVal.substring(0,
								cookieVal.indexOf(";"));
						String session = cookieVal.split("=")[1];
						saveSession(session);
					}
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder returnJsonSB = new StringBuilder();
			String returnJsonStr = null;

			while ((returnJsonStr = br.readLine()) != null) {
				returnJsonSB.append(returnJsonStr);
			}
			
			if (conn != null)
				conn.disconnect();
			
			returnJsonStr = returnJsonSB.toString();

			JSONObject JSONObject = new JSONObject(returnJsonStr);
			
				
			final ReturnInfo returnInfo = ReturnInfo.fromJSONObject(JSONObject);

			if(returnInfo != null && returnInfo.getReturnCode() == ReturnInfo.SUCCESS){
				if(HostUtil.SIGN_IN.equals(path))
					UserInfo.saveSelfUserInfo(UserInfo.fromJson(params.get(USERNAME_KEY), params.get(PASSWORD_KEY), JSONObject));
				return true;
			}else if(returnInfo != null){
				ApplicationWorker.getInstance().executeOnUIThrean(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(KuangNeiApplication.getInstance(), returnInfo.getReturnMessage(), Toast.LENGTH_LONG).show();
					}
				});
				
			}else{
				ApplicationWorker.getInstance().executeOnUIThrean(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(KuangNeiApplication.getInstance(), "登录失败，请重试！", Toast.LENGTH_LONG).show();
					}
				});
				
			}
			
			
			
			
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, e.toString());
		} catch (KeyManagementException e) {
			Log.e(TAG, e.toString());
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		return false;
	}

	public static void register(final String username,final String password, final String deviceID, final OnSignInLisener onSignInLisener) {
		ApplicationWorker.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put(USERNAME_KEY, username);
				map.put(PASSWORD_KEY,password);
				if(!TextUtils.isEmpty(deviceID))
					map.put(DEVICE_ID_KEY, deviceID);
				if(!TextUtils.isEmpty(PushUtil.getToken()))
					map.put(TOKEN_KEY, PushUtil.getToken());

				try {
					final boolean success = doHttpsPOSTRequest(HostUtil.REGISTER, map, CHARSET);
					
					setSignIn(success);
					
					ApplicationWorker.getInstance().executeOnUIThrean(new Runnable() {
						@Override
						public void run() {
							onSignInLisener.onSignInFinish(success, UserInfo.loadSelfUserInfo());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static boolean signin(final String username,final String password, final String deviceID){

		Map<String, String> map = new HashMap<String, String>();
		map.put(USERNAME_KEY, username);
		map.put(PASSWORD_KEY, password);
		if(!TextUtils.isEmpty(deviceID))
			map.put(DEVICE_ID_KEY, deviceID);
		if(!TextUtils.isEmpty(PushUtil.getToken()))
			map.put(TOKEN_KEY, PushUtil.getToken());

		try {
			boolean result = doHttpsPOSTRequest(HostUtil.SIGN_IN, map, CHARSET);
					
			setSignIn(result);
			
			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void signin(final String username,final String password, final String deviceID, final OnSignInLisener onSignInLisener) {
		ApplicationWorker.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put(USERNAME_KEY, username);
				map.put(PASSWORD_KEY,password);
				if(!TextUtils.isEmpty(deviceID))
					map.put(DEVICE_ID_KEY, deviceID);
				if(!TextUtils.isEmpty(PushUtil.getToken()))
					map.put(TOKEN_KEY, PushUtil.getToken());
				

				try {
					final boolean success = doHttpsPOSTRequest(HostUtil.SIGN_IN, map, CHARSET);
					
					setSignIn(success);
					
					ApplicationWorker.getInstance().executeOnUIThrean(new Runnable() {
						@Override
						public void run() {
							onSignInLisener.onSignInFinish(success, UserInfo.loadSelfUserInfo());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static final String IS_SIGN_IN_KEY = "isSignIn";
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String TOKEN_KEY = "token";
	private static final String DEVICE_ID_KEY = "deviceid";
	private static final String SET_COOKIE_KEY = "set-cookie";
	public static final String COOKIE_KEY = "Cookie";
	public static final String SESSION_KEY = "sessionid";
	private static final String SESSION = "session";
	
	
	public static boolean isSignIn(){
		return DataStorage.load(IS_SIGN_IN_KEY, false);
	}
	
	private static void setSignIn(boolean isSignIn){
		DataStorage.save(IS_SIGN_IN_KEY, isSignIn);
	}

	public static void saveSession(String session) {
		DataStorage.save(SESSION, session);
	}

	public static String loadSession() {
		return DataStorage.loadString(SESSION);
	}
	
	public static interface OnSignInLisener{
		public void onSignInFinish(boolean isSuccess , UserInfo userInfo);
	}

}
