package me.kuangneipro.manager;

import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.PushUtil;

import org.json.JSONObject;

public class UserInfoManager {
	
	public static final int UPDATE_USER_INFO = 203;
	
	public static void updateUserInfo(HttpHelper httpRequest,UserInfo userInfo){
		httpRequest.setUrl(HostUtil.ADD_USER_INFO).put("token", PushUtil.getToken()).put("nickname", userInfo.getName()).put("avatar", userInfo.getAvatar()).put("sex", userInfo.getSex()+"").put("birthday", userInfo.getBirthday()+"").put("sign", userInfo.getSign()).asyncPost();
	}
	
	public static UserInfo fillUserInfoFromJson(JSONObject jsonObj){
		if(jsonObj!=null){
			if(UserInfo.loadSelfUserInfo()!=null){
				UserInfo.saveSelfUserInfo(UserInfo.fromJson(UserInfo.loadSelfUserInfo().getUsername(), UserInfo.loadSelfUserInfo().getPassword(), jsonObj));
			}
		}
		return UserInfo.loadSelfUserInfo();
	}

}
