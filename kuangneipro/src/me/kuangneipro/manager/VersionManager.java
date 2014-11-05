package me.kuangneipro.manager;

import me.kuangneipro.entity.VersionInfo;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONObject;

public class VersionManager {
	
	public static final int VERSION = 1;
	
	public static final int CHECK = 1020;
	
	public static void doCheck(HttpHelper httpRequest){
		httpRequest.setUrl(HostUtil.CHECK_VERSION).put("version", VERSION+"").asyncGet();
	}
	
	public static VersionInfo getUnReadCountFromJson(JSONObject jsonObj ){
		VersionInfo versionInfo = new VersionInfo();
		
		if(jsonObj!=null){
			if(jsonObj.has("url")){
				versionInfo.hasNewVersion = true;
				versionInfo.url = jsonObj.optString("url");
				versionInfo.versionNumber = jsonObj.optInt("versionNumber");
				versionInfo.description = jsonObj.optString("description");
			}else{
				versionInfo.hasNewVersion = false;
			}
		
		}
		return versionInfo;
			
	}

}
