package me.kuangneipro.manager;

import java.util.List;

import me.kuangneipro.entity.ReplyInfo;
import me.kuangneipro.entity.ReplyInfo.User;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.util.DateUtil;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReplyInfoManager {

	public static final int REPLY_KEY_REFRESH = 1111;
	public static final int REPLY_KEY_REFRESH_MORE = 2222;
	public static final int DO_REPLY = 3333;
	
	public static void getReplyList(HttpHelper httpRequest, int postId, int page){
		httpRequest.setUrl(HostUtil.REPLY).put("postId", postId+"").put("page", page+"").asyncGet();
	}
	
	public static void fillReplyListFromJson(JSONObject jsonObj , List<ReplyInfo> replyInfos){
		if(jsonObj!=null){
		try {
			
			JSONArray jsonarray = jsonObj.getJSONArray("list");
			if(jsonarray!=null)
				for (int i = 0; i < jsonarray.length(); i++) {
		    		JSONObject oneJson = jsonarray.getJSONObject(i);
		    		JSONObject toUserj = oneJson.getJSONObject("toUser");
		    		JSONObject fromUserj = oneJson.getJSONObject("fromUser");
		    		if (oneJson == null || toUserj == null || fromUserj == null)
		    			continue;
		    		ReplyInfo reply = new ReplyInfo();
		    		reply.postId = oneJson.optInt("postId");
		    		reply.id = oneJson.optInt("id");
		    		reply.ReplyId = oneJson.optInt("ReplyId");
		    		reply.editStatus = oneJson.optInt("editStatus");
		    		reply.replyTime = DateUtil.parseDateFromStr(oneJson.optString("replyTime"));
		    		reply.content = oneJson.optString("content");
		    		reply.upCount = oneJson.optInt("upCount");
		    		User toUser = new User();
		    		toUser.id = toUserj.getInt("id");
		    		toUser.name = toUserj.optString("name");
		    		reply.toUser = toUser;
		    		User fromUser = new User();
		    		fromUser.id = fromUserj.getInt("id");
		    		fromUser.name = fromUserj.optString("name");
		    		reply.fromUser = fromUser;
		    		
		    		replyInfos.add(reply);
				}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		}
	}
	
	public static void doReplay(HttpHelper httpRequest,String toUserId,String postId,String content){
		httpRequest.setUrl(HostUtil.DO_REPLY).put("toUserId",toUserId).put("postId", postId).put("content", content).asyncPost();
	}
	public static ReturnInfo getReplyReturnInfo(JSONObject jsonObj){
    	return ReturnInfo.fromJSONObject(jsonObj);
	}
	
}
