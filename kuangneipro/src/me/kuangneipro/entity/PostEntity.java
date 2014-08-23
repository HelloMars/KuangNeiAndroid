package me.kuangneipro.entity;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class PostEntity {
	public Date mDate;
	public String mUserId;
	public String mUserName;
	public String mUserAvatar;
	public String mContent;	
	public int mDislikeNum;
	public int mLikeNum;
	public int mReplyNum;
	public List<String> mPictures;
	
	@SuppressLint("SimpleDateFormat") 
	public PostEntity(
			String id,
			String name,
			String avatar,
			String content,
			int dislikeNum,
			int likeNum,
			int replyNum,
			String date,
			List<String> picList
			){
		
		mUserId = id;
		mUserName = name;
		mUserAvatar = avatar;
        mContent = content;
        mDislikeNum = dislikeNum;
        mLikeNum = likeNum;
        mReplyNum = replyNum;
        mPictures = picList;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			mDate = formatter.parse(date);
		} catch (ParseException e) {
			mDate = new Date();
			e.printStackTrace();
		}
    }
	
	public String getDate() {
		String[] measure = {"秒", "分钟", "小时", "天"};
		int[] units = {60, 60, 24};
		
		long between=(new Date().getTime() - mDate.getTime())/1000;//除以1000是为了转换成秒
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
