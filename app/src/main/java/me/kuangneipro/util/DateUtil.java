package me.kuangneipro.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	@SuppressLint("SimpleDateFormat")
	public static Date parseDateFromStr(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return formatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}
	
	public static String getReadableDateStr(Date date){
		String[] measure = {"秒", "分钟", "小时", "天"};
		int[] units = {60, 60, 24};
		
		long between=(new Date().getTime() - date.getTime())/1000;//除以1000是为了转换成秒
		int i = 0;
		for (; i < 3; ++i) {
			if (between < units[i]) {
				return between + measure[i] + "前";
			} else {
				between /= units[i];
			}
		}
		return between + measure[i] + "前";
	}
}
