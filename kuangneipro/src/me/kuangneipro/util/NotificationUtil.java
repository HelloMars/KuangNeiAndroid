package me.kuangneipro.util;

import me.kuangneipro.R;
import me.kuangneipro.core.KuangNeiApplication;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

public class NotificationUtil {

	public static void makeANotification(String content){
		Notification notification = new NotificationCompat.Builder(KuangNeiApplication.getInstance())
        .setLargeIcon(BitmapFactory.decodeResource( KuangNeiApplication.getInstance().getResources(), R.drawable.ic_launcher)).setSmallIcon(R.drawable.ic_launcher)
        .setTicker(content).setContentInfo("用户名")
        .setContentTitle("框内").setContentText(content)
        .setNumber(1)
        .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
        .build();

		NotificationManager nm = (NotificationManager)KuangNeiApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE); 
		nm.notify(0, notification);
		

	}
	
}
