package me.kuangneipro.manager;

import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONObject;

public class UnreadManager {

	public static final int REQUEST_UNREAD = 11111;
	
	public static void dorequest(HttpHelper httpRequest){
		httpRequest.setUrl(HostUtil.UN_READ).asyncGet();
	}
	
	public static int getUnReadCountFromJson(JSONObject jsonObj ){
		if(jsonObj!=null)
			if(jsonObj.has("unReadMessageCount"))
				return jsonObj.optInt("unReadMessageCount",0);
		
		return 0;
			
	}
	
}
