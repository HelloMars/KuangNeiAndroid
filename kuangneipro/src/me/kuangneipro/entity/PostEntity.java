package me.kuangneipro.entity;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class PostEntity {
	public Date mDate;
	public int mUserId;
	public String mUserName;
	public String mUserAvatar;
	public String mTitle;
	public String mContent;	
	public int mDislikeNum;
	public int mReplyNum;
	public List<String> mPictures;
	
	
	@SuppressLint("SimpleDateFormat") 
	public PostEntity(
			int id,
			String name,
			String avatar,
			String title,
			String content,
			int dislikeNum,
			int replyNum,
			String date,
			List<String> picList
			){
		
		mUserId = id;
		mUserName = name;
		mUserAvatar = avatar;
        mTitle = title;
        mContent = content;
        mDislikeNum = dislikeNum;
        mReplyNum = replyNum;
        mPictures = picList;
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
		try {
			mDate = formatter.parse(date);
		} catch (ParseException e) {
			mDate = new Date();
			//e.printStackTrace();
		}
    }
	
	public String getDate() {
		String[] measure = {"秒", "分钟", "小时", "天"};
		int[] units = {60, 60, 24};
		
		long between=(new Date().getTime() - mDate.getTime())/1000;//除以1000是为了转换成秒
//		long day1=between/(24*3600);
//		long hour1=between%(24*3600)/3600;
//		long minute1=between%3600/60;
//		long second1=between%60/60;
		for (int i = 0; i < 4; ++i) {
			if (between < units[i] || i == 3) {
				return between + measure[i] + "前";
			} else {
				between /= units[i];
			}
		}
		return "";
	}
}
