package me.kuangneipro.manager;

import me.kuangneipro.entity.TopicInfo;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONObject;

public class TopicInfoManager {

	public static final int GET = 1234564;
	
	public static void getTopic(HttpHelper httpRequest){
		httpRequest.setUrl(HostUtil.GET_TIPIC).asyncGet();
	}
	
	public static TopicInfo fillReplyListFromJson(JSONObject jsonObj){
		
		TopicInfo topicInfo = new TopicInfo();
		if(jsonObj!=null){
			topicInfo.topicInfo = jsonObj.optString("topicInfo");
			topicInfo.topicName = jsonObj.optString("topicName");
			topicInfo.save();
		}
		return topicInfo;
	}
	
	
	
}
