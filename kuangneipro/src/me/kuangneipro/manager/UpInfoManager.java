package me.kuangneipro.manager;

import me.kuangneipro.entity.UpInfo;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONObject;

public class UpInfoManager {

	public static final int DO_UP = 33333;
	
	public static void doUp(HttpHelper httpRequest,String postId){
		httpRequest.setUrl(HostUtil.UP_POST).put("postId", postId).asyncPost();
	}
	public static UpInfo getReplyReturnInfo(JSONObject jsonObj){
    	return UpInfo.fromJson(jsonObj);
	}
	
}
