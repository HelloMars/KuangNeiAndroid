package me.kuangneipro.manager;

import java.io.File;

import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.HttpHelper.RequestCallBackListener;
import me.kuangneipro.util.ImageUtil;
import me.kuangneipro.util.PushUtil;

import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.qiniu.auth.JSONObjectRet;
import com.qiniu.utils.QiniuException;

public class UserInfoManager {
	private static final String TAG ="UserInfoManager";
	public static final int GET_USER_INFO = 202;
	public static final int UPDATE_USER_INFO = 203;
	public static final int REGIGSTER = 444;
	
	public static void getUserInfo(HttpHelper httpRequest){
		httpRequest.setUrl(HostUtil.ADD_USER_INFO).asyncPost();
	}
	
	public static void updateUserInfo(final Activity activity,final HttpHelper httpRequest,final UserInfo userInfo){
		if(!TextUtils.isEmpty(userInfo.getAvatar()) && !ImageUtil.isRemote(userInfo.getAvatar())){
			HttpHelper httpTokenGet = new HttpHelper(activity,ImageUtil.GET_IMAGE_UPLOAD_TOKEN);
			httpTokenGet.setRequestCallBackListener(new RequestCallBackListener() {
				@Override
				public void onRequestComplete(int id, JSONObject jsonObj) {
					String token = ImageUtil.getImageUploadToken(jsonObj);
					File file = ImageUtil.compressBmpToTmpFile(userInfo.getAvatar());
					if(file!=null &&file.exists()){
						ImageUtil.uploadImg(token, file, new JSONObjectRet() {
							@Override
							public void onProcess(long current, long total) {
							}
							@Override
							public void onSuccess(JSONObject resp) {
								String redirect = ImageUtil.QINIUDN_SERVER+resp.optString("hash", "");
								userInfo.setAvatar(redirect);
								doUpdateUserInfo(httpRequest,userInfo);
							}
							@Override
							public void onFailure(QiniuException ex) {
								doUpdateUserInfo(httpRequest,userInfo);
							}
						});
						Log.i(TAG, "uploading");
								
					}else{
						doUpdateUserInfo(httpRequest,userInfo);
					}
				}
			});
					
			ImageUtil.gettingImageUploadToken(httpTokenGet);
		}else{
			doUpdateUserInfo(httpRequest,userInfo);
		}
		
	}
	
	public static UserInfo fillUserInfoFromRegister(JSONObject jsonObj){
		if(jsonObj!=null){
			UserInfo userInfo = new UserInfo();
			userInfo.setUsername(jsonObj.optString("user", ""));
			userInfo.setPassword(jsonObj.optString("password", ""));
			UserInfo.saveSelfUserInfo(userInfo);
		}
		return UserInfo.loadSelfUserInfo();
	}
	
	public static void regester(HttpHelper httpRequest, int schoolId){
		httpRequest.setUrl(HostUtil.REGISTER).put("schoolId", schoolId+"").asyncPost();
	}
	
	public static void doUpdateUserInfo(HttpHelper httpRequest,final UserInfo userInfo){
		httpRequest.setUrl(HostUtil.ADD_USER_INFO).put("token", PushUtil.getToken()).put("nickname", userInfo.getName()).put("avatar", userInfo.getAvatar()).put("sex", userInfo.getSex()+"").put("birthday", userInfo.getBirthday().getTime()+"").put("sign", userInfo.getSign()).asyncPost();
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
