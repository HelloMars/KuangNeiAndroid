package me.kuangneipro.receiver;

import me.kuangneipro.util.PushUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.igexin.sdk.PushConsts;



public class GeTuiPushReceiver extends BroadcastReceiver {
	
	private static final String TAG = GeTuiPushReceiver.class.getSimpleName();
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
			case PushConsts.GET_MSG_DATA:
				// ��ȡ͸����payload������
				byte[] payload = bundle.getByteArray("payload");
				
				if (payload != null)
				{
					String data = new String(payload);
					Log.d(TAG, "Got Payload:" + data);
					Toast.makeText(context, TAG+" Payload:"+data, Toast.LENGTH_LONG).show();
					// TODO:���մ���͸����payload������ 
				}
				break;
			case PushConsts.GET_CLIENTID:
				// ��ȡClientID(CID)
				String cid = bundle.getString("clientid");
				Log.i(TAG, "Got ClientID:" + cid);
				Toast.makeText(context, "clientID:"+cid, Toast.LENGTH_LONG).show();
				PushUtil.saveToken(cid);
				// TODO: 
				/* ������Ӧ����Ҫ��ClientID�ϴ��������������������ҽ���ǰ�û��ʺź�ClientID���й������Ա��Ժ�ͨ���û��ʺŲ���ClientID������Ϣ����
				��Щ�����ClientID���ܻᷢ���仯��Ϊ��֤��ȡ���µ�ClientID����Ӧ�ó�����ÿ�λ�ȡClientID�㲥�󣬶��ܽ���һ�ι����� */

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