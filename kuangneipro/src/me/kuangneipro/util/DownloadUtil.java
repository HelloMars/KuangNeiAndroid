package me.kuangneipro.util;

import me.kuangneipro.activity.PersonalInfoActivity;
import me.kuangneipro.activity.PostListActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;

/**
 * 用于下载一个url指定的文件
 * @author connorlu
 *
 */
public class DownloadUtil {

	private static final int VERSION = 1;
	
	public static void showDialog(final Activity activity ,final String content ,final String url){
		new AlertDialog.Builder(activity)
		 .setMessage(content)
		 .setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				download(activity, url);
			}
		}) .setNegativeButton("取消", null)
		 .show(); 
	}
	
	public static void download(Activity activity ,String url){
		Uri uri = Uri.parse(url);
		Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
		activity.startActivity(downloadIntent);
	}
	
}
