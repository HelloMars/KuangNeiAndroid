package me.kuangneipro.manager;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.entity.MessageEntity;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MessageEntityManager {
	public static final int MESSAGE_LIST_KEY = 0;

	// flag=1代表是对帖子的回复，2代表是对一级回复的回复，3代表对二级回复的回复
	public static int getId(JSONObject json, int flag) throws JSONException {
		switch (flag) {
		case 2:
			return json.getInt("firstLevelReplyId");
		case 3:
			return json.getInt("secondLevelReplyId");
		}
		return json.getInt("postId");
	}
	
	public static String getPreText(int flag) {
		switch (flag) {
		case 2: case 3:
			return "回复我的评论：";
		}
		return "回复我的帖子：";
	}

	public static void fillMessageListFromJson(JSONObject jsonObj,
			List<MessageEntity> messageList) {
		try {
			Log.i("fillMessageListFromJson", jsonObj.toString());
			JSONArray jsonarray = jsonObj.getJSONArray("list");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject oneMessageJson = jsonarray.getJSONObject(i);
				JSONObject user = oneMessageJson.getJSONObject("showUser");
				// flag=1代表是对帖子的回复，2代表是对一级回复的回复，3代表对二级回复的回复
				int flag = oneMessageJson.getInt("flag");
				MessageEntity message = new MessageEntity(
						oneMessageJson.getString("replyTime"),
						user.getString("id"),
	    				user.getString("name"),
	    				user.getString("avatar"),
	    				oneMessageJson.getString("replyContent"),
	    				oneMessageJson.getString("repliedBriefContent"),
	    				flag,
	    				getId(oneMessageJson, flag));
				if (messageList == null)
					messageList = new ArrayList<MessageEntity>();
				messageList.add(message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getMessageList(HttpHelper httpRequest, int page) {
		httpRequest.setUrl(HostUtil.MESSAGE_LIST_URL).put("page", page+"").asyncGet();
	}
}
