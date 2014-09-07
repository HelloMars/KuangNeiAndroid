package me.kuangneipro.manager;

import java.util.List;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.entity.FirstLevelReplyEntity;
import me.kuangneipro.entity.SecondLevelReplyEntity;

public class PostReplyManager {
	private static final String TAG = "PostReplyManager";
	
	public static final int POST_REPLY_FIRST = 0;
	public static final int POST_REPLY_SECOND = 1;
	
	public static void getFirstlevelReplyList(HttpHelper httpRequest, int postId, int page){
		Log.i(TAG, "getFirstlevelReplyList:" + postId + " " + page);
		httpRequest.setUrl(HostUtil.REPLY_FIRST_LEVEL).put("postId", postId+"").put("page", page+"").asyncGet();
	}
	
	public static void getSecondlevelReplyList(HttpHelper httpRequest, int firstLevelReplyId, int page){
		httpRequest.setUrl(HostUtil.REPLY_SECOND_LEVEL).put("firstLevelReplyId", firstLevelReplyId+"").put("page", page+"").asyncGet();
	}
	
	public static void fillFirstReplyListFromJson(JSONObject jsonObj, List<FirstLevelReplyEntity> replyList){
		try {
			JSONArray jsonarray = jsonObj.getJSONArray("list");
			assert (jsonarray != null);
			ReturnInfo info = PostReplyManager.getReplyReturnInfo(jsonObj);
			Log.i(TAG, "ReturnInfo:" + info.getReturnMessage());
			Log.i(TAG, "fillReplyListFromJson " + jsonarray.length());
			for (int i = 0; i < jsonarray.length(); i++) {
	    		JSONObject oneJson = jsonarray.getJSONObject(i);
	    		Log.i(TAG, oneJson.toString());
	    		FirstLevelReplyEntity reply = FirstLevelReplyEntity.fromJson(oneJson);
	    		replyList.add(reply);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void fillSecondReplyListFromJson(JSONObject jsonObj, List<SecondLevelReplyEntity> replyList){
		try {
			JSONArray jsonarray = jsonObj.getJSONArray("list");
			assert (jsonarray != null);
			ReturnInfo info = PostReplyManager.getReplyReturnInfo(jsonObj);
			Log.i(TAG, "ReturnInfo:" + info.getReturnMessage());
			Log.i(TAG, "fillReplyListFromJson " + jsonarray.length());
			
			for (int i = 0; i < jsonarray.length(); i++) {
	    		JSONObject oneJson = jsonarray.getJSONObject(0);
	    		Log.i(TAG, oneJson.toString());
	    		SecondLevelReplyEntity reply = SecondLevelReplyEntity.fromJson(oneJson);
	    		replyList.add(reply);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static int peekFirstLevelReplyId(JSONObject jsonObj){
		try {
			JSONArray jsonarray = jsonObj.getJSONArray("list");
			assert (jsonarray != null);
			ReturnInfo info = PostReplyManager.getReplyReturnInfo(jsonObj);
			Log.i(TAG, "ReturnInfo:" + info.getReturnMessage());
			Log.i(TAG, "peekFirstLevelReplyId " + jsonarray.length());
			
    		JSONObject oneJson = jsonarray.getJSONObject(0);
    		return oneJson.getInt("secondLevelReplyId");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static ReturnInfo getReplyReturnInfo(JSONObject jsonObj){
    	return ReturnInfo.fromJSONObject(jsonObj);
	}
}
