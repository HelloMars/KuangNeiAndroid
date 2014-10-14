package me.kuangneipro.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.kuangneipro.Adapter.SecondLevelReplyAdapter;
import me.kuangneipro.activity.MainActivity;
import me.kuangneipro.util.DateUtil;

import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;

public class FirstLevelReplyEntity implements Comparable<FirstLevelReplyEntity>{
	private static final String TAG = FirstLevelReplyEntity.class.getSimpleName();
	
	public Date mDate;
    public int mFloor;
	public int mPostId;
	public int mFirstLevelReplyId;
	public int mReplyNum;
	public String mContent;
	public int mLikeNum;
	
	public String mUserId;
	public String mUserName;
	public String mUserAvatar;
	
	public List<SecondLevelReplyEntity> mSecondlevelReplyList;
	
	public SecondLevelReplyAdapter mAdapter;
	
	public FirstLevelReplyEntity() {
		mAdapter = null;
		mSecondlevelReplyList = new ArrayList<SecondLevelReplyEntity>();
	}
	
	public static FirstLevelReplyEntity fromJson(JSONObject jsonObj){
		if(jsonObj != null) {
			Log.i(TAG+".fromJson:", jsonObj.toString());
			FirstLevelReplyEntity replyInfo = new FirstLevelReplyEntity();
			try {
	    		JSONObject user = jsonObj.getJSONObject("user");
	    		assert (user != null);
	    		replyInfo.mDate = DateUtil.parseDateFromStr(jsonObj.getString("replyTime"));
	    		replyInfo.mFloor = jsonObj.getInt("floor");
	    		replyInfo.mPostId = jsonObj.getInt("postId");
	    		replyInfo.mFirstLevelReplyId = jsonObj.getInt("firstLevelReplyId");
	    		replyInfo.mReplyNum = jsonObj.getInt("replyCount");
	    		replyInfo.mContent = jsonObj.getString("content");
	    		replyInfo.mLikeNum = jsonObj.getInt("upCount");
	    		
				replyInfo.mUserId = user.getString("id");
	    		replyInfo.mUserId = user.getString("name");
	    		replyInfo.mUserId = user.getString("avatar");
	    		return replyInfo;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}
	
	public void addSecondLevelReply(SecondLevelReplyEntity reply) {
		mSecondlevelReplyList.add(reply);
	}
	
	public String getDate() {
		return DateUtil.getReadableDateStr(mDate);
	}

	@Override
	public int compareTo(FirstLevelReplyEntity other) {
		return this.mFloor - other.mFloor;
	}
}
