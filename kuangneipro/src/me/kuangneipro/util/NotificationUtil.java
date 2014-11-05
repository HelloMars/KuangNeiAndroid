package me.kuangneipro.util;

import me.kuangneipro.R;
import me.kuangneipro.activity.MessageListActivity;
import me.kuangneipro.core.KuangNeiApplication;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationUtil {

	@SuppressWarnings("deprecation")
	public static void makeANotification(Context context, String content){
		
		NotificationManager notificationManager=(NotificationManager)KuangNeiApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		Notification  notification=new Notification();
		notification.icon=R.drawable.ic_launcher;
		//当我们点击通知时显示的内容
		notification.tickerText= content;
		//通知时发出的默认声音
		notification.defaults=Notification.DEFAULT_SOUND;
		//设置通知显示的参数
		Intent mIntent = new Intent(context, MessageListActivity.class);  
		PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
		notification.setLatestEventInfo(context, "",content,mPendingIntent);
		//这个可以理解为开始执行这个通知
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(1,notification);
		
//		Intent mIntent = new Intent(context, PostListActivity.class);  
//        //主要在于PendingIntent的getActivity方法中的参数  
//		PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
//        //如果使用下面注释掉的代码，将会出现上面说讲到的问题,当然在SecondActivi
//		
//		Notification notification = new NotificationCompat.Builder(KuangNeiApplication.getInstance())
//        .setLargeIcon(BitmapFactory.decodeResource( KuangNeiApplication.getInstance().getResources(), R.drawable.ic_launcher)).setSmallIcon(R.drawable.ic_launcher)
//        .setTicker(content).setContentInfo("用户名")
//        .setContentTitle("框内").setContentText(content)
//        .setNumber(1)
//        .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
//        .setDeleteIntent(mPendingIntent)
//        .build();
//		NotificationManager nm = (NotificationManager)KuangNeiApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE); 
//		nm.notify(0, notification);

	}
	
}
