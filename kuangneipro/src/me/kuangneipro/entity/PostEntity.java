package me.kuangneipro.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public final class PostEntity implements Parcelable {
	public Date mDate;
	public String mUserId;
	public String mUserName;
	public String mUserAvatar;
	public String mContent;	
	public int mDislikeNum;
	public int mLikeNum;
	public int mReplyNum;
	public List<String> mPictures;
	
	public PostEntity(){}
	
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(mUserId);
		parcel.writeString(mUserName);
		parcel.writeString(mUserAvatar);
		parcel.writeString(mContent);
		parcel.writeInt(mDislikeNum);
		parcel.writeInt(mLikeNum);
		parcel.writeInt(mReplyNum);
		parcel.writeSerializable(mDate);
		parcel.writeInt(mPictures.size());
		for (int i = 0; i < mPictures.size(); ++i)
			parcel.writeString(mPictures.get(i));
	}
	
	public static final Parcelable.Creator<PostEntity> CREATOR = new Creator<PostEntity>() {

		@Override
		public PostEntity createFromParcel(Parcel source) {
			PostEntity postEntity = new PostEntity();
			postEntity.mUserId = source.readString();
			postEntity.mUserName = source.readString();
			postEntity.mUserAvatar = source.readString();
			postEntity.mContent = source.readString();
			postEntity.mDislikeNum = source.readInt();
			postEntity.mLikeNum = source.readInt();
			postEntity.mReplyNum = source.readInt();
			postEntity.mDate = (Date)source.readSerializable();
			postEntity.mPictures = new ArrayList<String>();
			int picNum = source.readInt();
			for (int i = 0; i < picNum; ++i)
				postEntity.mPictures.add(source.readString());
			return postEntity;
		}

		@Override
		public PostEntity[] newArray(int size) {
			return new PostEntity[size];
		}
		
	};
}
