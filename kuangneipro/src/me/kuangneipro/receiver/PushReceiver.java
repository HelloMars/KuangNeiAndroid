package me.kuangneipro.receiver;

import org.OpenUDID.OpenUDID_manager;

import me.kuangneipro.activity.RegisterActivity;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.util.LoginUtil;
import me.kuangneipro.util.LoginUtil.OnSignInLisener;
import me.kuangneipro.util.NotificationUtil;
import me.kuangneipro.util.PushUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.igexin.sdk.PushConsts;



public class PushReceiver extends BroadcastReceiver {
	
	private static final String TAG = PushReceiver.class.getSimpleName();
	
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
			case PushConsts.GET_MSG_DATA:
				// 获取透传（payload）数据
				byte[] payload = bundle.getByteArray("payload");
				
				if (payload != null)
				{
					String data = new String(payload);
					if(!TextUtils.isEmpty(data)  && LoginUtil.isSignIn()){
						NotificationUtil.makeANotification(data);
//						Log.d(TAG, "Got Payload:" + data);
//						Toast.makeText(context, TAG+" Payload:"+data, Toast.LENGTH_LONG).show();
						// TODO:接收处理透传（payload）数据 
					}
					
				}
				break;
			case PushConsts.GET_CLIENTID:
				// 获取ClientID(CID)
				final String cid = bundle.getString("clientid");
				if(!TextUtils.isEmpty(cid)){
					
					String ocid = PushUtil.getToken();
					Log.i(TAG, "Got ClientID:" + cid);
					if(!TextUtils.isEmpty(ocid) && !ocid.equals(cid) ){
						String deviceID = OpenUDID_manager.getOpenUDID();
						Toast.makeText(context, "deviceID"+deviceID, Toast.LENGTH_SHORT).show();
						LoginUtil.signin(UserInfo.loadSelfUserInfo().getUsername(), UserInfo.loadSelfUserInfo().getPassword(), deviceID, new OnSignInLisener() {
							@Override
							public void onSignInFinish(boolean isSuccess, UserInfo userInfo) {
								Toast.makeText(context, "newClientID:"+cid+" refresh failed!!!!!!!!!!!!!!!!!!", Toast.LENGTH_LONG).show();
							}
						});
						Toast.makeText(context, "newClientID:"+cid+"!!!!!!!!!!!!!!!!!", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(context, "clientID:"+cid, Toast.LENGTH_LONG).show();
						PushUtil.saveToken(cid);
					}
				}else{
					Toast.makeText(context, "clientID:is null!!!!!!!!!!!!!"+cid, Toast.LENGTH_LONG).show();
				}
				// TODO: 
				/* 第三方应用需要将ClientID上传到第三方服务器，并且将当前用户帐号和ClientID进行关联，以便以后通过用户帐号查找ClientID进行消息推送
				有些情况下ClientID可能会发生变化，为保证获取最新的ClientID，请应用程序在每次获取ClientID广播后，都能进行一次关联绑定 */
				break;
		// case PushConsts.BIND_CELL_STATUS:
		// String cell = bundle.getString("cell");
		//
		// Log.d("GetuiSdkDemo", "BIND_CELL_STATUS:" + cell);
		// if (GexinSdkDemoActivity.tLogView != null)
		// GexinSdkDemoActivity.tLogView.append("BIND_CELL_STATUS:" + cell +
		// "\n");
		// break;
			default:
				break;
		}
		
	}
		
}