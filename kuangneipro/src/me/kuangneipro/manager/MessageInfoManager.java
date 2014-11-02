package me.kuangneipro.manager;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.entity.MessageInfo;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.entity.ReplyInfo;
import me.kuangneipro.entity.ReplyInfo.User;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.util.DateUtil;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageInfoManager {

	public static final int MESSAGE_KEY_REFRESH = 10000;
	public static final int MESSAGE_KEY_REFRESH_MORE = 20000;
	
	public static void getMessageList(HttpHelper httpRequest, int page){
		httpRequest.setUrl(HostUtil.GET_MESSAGE).put("page", page+"").asyncGet();
	}
	
	public static void fillMessageListFromJson(JSONObject jsonObj , List<MessageInfo> messageInfos){
		if(jsonObj!=null){
		try {
			
			JSONArray jsonarray = jsonObj.getJSONArray("list");
			if(jsonarray!=null)
				for (int i = 0; i < jsonarray.length(); i++) {
		    		JSONObject oneJson = jsonarray.getJSONObject(i);
		    		JSONObject toUserj = oneJson.getJSONObject("toUser");
		    		JSONObject fromUserj = oneJson.getJSONObject("fromUser");
		    		JSONObject postj = oneJson.getJSONObject("post");
		    		if (oneJson == null || toUserj == null || fromUserj == null || postj == null)
		    			continue;
		    		
		    		MessageInfo messageInfo = new MessageInfo();
		    		
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
		    		

		    		JSONObject user = postj.getJSONObject("user");
		    		if(user==null)
		    			continue;
		    		List<String> pictureList = new ArrayList<String>();
		    		JSONArray pictures = postj.getJSONArray("pictures");
		    		for (int j = 0; j < pictures.length(); ++j)
		    			pictureList.add(pictures.getString(j));
		    		
		    		PostEntity post = new PostEntity(
		    				postj.getInt("postId"),
		    				postj.getInt("channelId"),
		    				user.optString("id"),
		    				user.optString("name"),
		    				user.optString("avatar"),
		    				postj.getString("content"),
		    				postj.getInt("opposedCount"),
		    				postj.getInt("upCount"),
		    				postj.getInt("replyCount"),
		    				postj.getString("postTime"),
		    				user.optInt("sex"),
		    				pictureList);
		    		
		    		if(reply.postId == 0)
		    			reply.postId = postj.getInt("postId");
		    		
		    		messageInfo.replyInfo = reply;
		    		messageInfo.postEntity = post;
		    		
		    		messageInfos.add(messageInfo);
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
