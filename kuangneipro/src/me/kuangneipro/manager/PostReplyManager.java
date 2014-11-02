package me.kuangneipro.manager;

import java.util.List;

import me.kuangneipro.entity.FirstLevelReplyEntity;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.entity.SecondLevelReplyEntity;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PostReplyManager {
	private static final String TAG = "PostReplyManager";
	
	public static final int POST_REPLY_FIRST = 0;
	public static final int POST_REPLY_SECOND = 1;
	
	public static final int DO_REPLAY_FIRST = 20;
	public static final int DO_REPLAY_SECOND = 21;
	
	
	
	public static void doReplaySecond(HttpHelper httpRequest,String content,int postId,int firstLevelReplyId){
		Log.i(TAG, "doReplayFirst:" + content + " " + postId);
		httpRequest.setUrl(HostUtil.DO_REPLY_SECOND_LEVEL).put("postId", postId+"").put("firstLevelReplyId", firstLevelReplyId+"").put("content", content).asyncPost();
	}
	
	public static void doReplayFirst(HttpHelper httpRequest,String content,int postId){
		Log.i(TAG, "doReplayFirst:" + content + " " + postId);
		httpRequest.setUrl(HostUtil.DO_REPLY_FIRST_LEVEL).put("postId", postId+"").put("content", content).asyncPost();
	}
	
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
			Log.i(TAG, "fillFirstReplyListFromJson " + jsonarray.length());
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
			Log.i(TAG, "fillSecondReplyListFromJson " + jsonarray.length());
			
			for (int i = 0; i < jsonarray.length(); i++) {
	    		JSONObject oneJson = jsonarray.getJSONObject(i);
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
			Log.i(TAG, jsonObj.toString());
			JSONArray jsonarray = jsonObj.getJSONArray("list");
			assert (jsonarray != null);
			ReturnInfo info = PostReplyManager.getReplyReturnInfo(jsonObj);
			Log.i(TAG, "ReturnInfo:" + info.getReturnMessage());
			Log.i(TAG, "peekFirstLevelReplyId " + jsonarray.length());
			
			return jsonObj.getInt("firstLevelReplyId");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static ReturnInfo getReplyReturnInfo(JSONObject jsonObj){
    	return ReturnInfo.fromJSONObject(jsonObj);
	}
}
