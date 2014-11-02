package me.kuangneipro.entity;

import org.json.JSONObject;

/**
 * 帖子点赞数量
 * @author connorlu
 *
 */
public class UpInfo {
	
	public String action;
	public int upCount;
	
	public boolean isDo(){
		return "do".equals(action);
	}
	
	public static UpInfo fromJson(JSONObject jsonObj){
		UpInfo upInfo = new UpInfo();
		
		if(jsonObj!=null){
			upInfo.action = jsonObj.optString("action");
			upInfo.upCount = jsonObj.optInt("upCount");
		}
		
		return upInfo;
	}

}
