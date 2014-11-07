package me.kuangneipro.entity;

import org.json.JSONObject;

import android.util.Log;

public class ReturnInfo {

	public static final int SUCCESS = 0;
	public static final int NotOppositeSex = 5;
	
	private final int returnCode;
	private final String returnMessage;
	
	public ReturnInfo(int returnCode,String returnMessage){
		this.returnCode = returnCode;
		this.returnMessage = returnMessage;
	}
	
	public static ReturnInfo fromJSONObject(JSONObject jsonObj){
		int returnCode = jsonObj.optInt("returnCode", 0);
		String  returnMessage = jsonObj.optString("returnMessage", "");
		Log.i("ReturnInfo", returnMessage);
		return new ReturnInfo(returnCode,returnMessage);
	}

	public int getReturnCode() {
		return returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}
	
	
}
