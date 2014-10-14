package me.kuangneipro.entity;

import java.util.Date;

import me.kuangneipro.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SecondLevelReplyEntity {
	private static final String TAG = SecondLevelReplyEntity.class.getSimpleName();
	
	public Date mDate;
	public int mPostId;
	public int mFirstLevelReplyId;
	public int mSecondLevelReplyId;
	public String mContent;

	public String mUserId;
	public String mUserName;
	public String mUserAvatar;
	
	public static SecondLevelReplyEntity fromJson(JSONObject jsonObj){
		if(jsonObj != null) {
			Log.i(TAG+".fromJson", jsonObj.toString());
			SecondLevelReplyEntity replyInfo = new SecondLevelReplyEntity();
			try {
	    		JSONObject user = jsonObj.getJSONObject("user");
	    		assert (user != null);
	    		replyInfo.mDate = DateUtil.parseDateFromStr(jsonObj.getString("replyTime"));
	    		replyInfo.mPostId = jsonObj.getInt("postId");
	    		replyInfo.mFirstLevelReplyId = jsonObj.getInt("firstLevelReplyId");
	    		replyInfo.mSecondLevelReplyId = jsonObj.getInt("secondLevelReplyId");
	    		replyInfo.mContent = jsonObj.getString("content");
	    		
				replyInfo.mUserId = user.getString("id");
	    		replyInfo.mUserName = user.getString("name");
	    		replyInfo.mUserAvatar = user.getString("avatar");
	    		return replyInfo;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}
	
	public String getDate() {
		return DateUtil.getReadableDateStr(mDate);
	}
}
