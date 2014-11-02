package me.kuangneipro.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.kuangneipro.util.DateUtil;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public final class PostEntity implements Parcelable {
	public Date mDate;
	public int mSex;
	public int mPostId;
	public int mChannelId;
	public String mUserId;
	public String mUserName;
	public String mUserAvatar;
	public String mContent;	
	public boolean mLikeSelected;
	public int mDislikeNum;
	public int mLikeNum;
	public int mReplyNum;
	public List<String> mPictures;
	
	public PostEntity(){}
	
	@SuppressLint("SimpleDateFormat")
	public PostEntity(
			int postId,
			int channelId,
			String userId,
			String name,
			String avatar,
			String content,
			int dislikeNum,
			int likeNum,
			int replyNum,
			String date,
			int sex,
			List<String> picList
			){
		mPostId = postId;
		mChannelId = channelId;
		mUserId = userId;
		mUserName = name;
		mUserAvatar = avatar;
        mContent = content;
        mDislikeNum = dislikeNum;
        mLikeNum = likeNum;
        mReplyNum = replyNum;
        mPictures = picList;
        mSex = sex;
        mDate = DateUtil.parseDateFromStr(date);
    }
	
	public String getDate() {
		return DateUtil.getReadableDateStr(mDate);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(mPostId);
		parcel.writeInt(mChannelId);
		parcel.writeString(mUserId);
		parcel.writeString(mUserName);
		parcel.writeString(mUserAvatar);
		parcel.writeString(mContent);
		parcel.writeInt(mDislikeNum);
		parcel.writeInt(mLikeNum);
		parcel.writeInt(mReplyNum);
		parcel.writeInt(mSex);
		parcel.writeSerializable(mDate);
		parcel.writeInt(mPictures.size());
		for (int i = 0; i < mPictures.size(); ++i)
			parcel.writeString(mPictures.get(i));
	}
	
	public static final Parcelable.Creator<PostEntity> CREATOR = new Creator<PostEntity>() {

		@Override
		public PostEntity createFromParcel(Parcel source) {
			PostEntity postEntity = new PostEntity();
			postEntity.mPostId = source.readInt();
			postEntity.mChannelId = source.readInt();
			postEntity.mUserId = source.readString();
			postEntity.mUserName = source.readString();
			postEntity.mUserAvatar = source.readString();
			postEntity.mContent = source.readString();
			postEntity.mDislikeNum = source.readInt();
			postEntity.mLikeNum = source.readInt();
			postEntity.mReplyNum = source.readInt();
			postEntity.mSex = source.readInt();
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
