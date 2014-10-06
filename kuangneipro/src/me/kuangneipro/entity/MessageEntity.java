package me.kuangneipro.entity;

import java.util.Date;

import me.kuangneipro.manager.MessageEntityManager;
import me.kuangneipro.util.DateUtil;
import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public final class MessageEntity implements Parcelable {
	private Date mDate;
	public String mUserId;
	public String mUserName;
	public String mUserAvatar;
	public String mReplyContent;
	private String mRepliedContent;
	public int mFlag;
	public int mId;
	
	public MessageEntity() {}
	
	@SuppressLint("SimpleDateFormat")
	public MessageEntity(
			String date,
			String userId,
			String name,
			String avatar,
			String replycontent,
			String repliedcontent,
			int flag,
			int id) {
		mUserId = userId;
		mUserName = name;
		mUserAvatar = avatar;
		mReplyContent = replycontent;
		mRepliedContent = repliedcontent;
		mFlag = flag;
		mId = id;
        mDate = DateUtil.parseDateFromStr(date);
	}
	
	public String getDate() {
		return DateUtil.getReadableDateStr(mDate);
	}
	
	public String getRepliedContent() {
		return MessageEntityManager.getPreText(mFlag) + mRepliedContent;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<MessageEntity> CREATOR = new Creator<MessageEntity>() {

		@Override
		public MessageEntity createFromParcel(Parcel source) {
			MessageEntity messageEntity = new MessageEntity();
			messageEntity.mDate = (Date)source.readSerializable();
			messageEntity.mUserId = source.readString();
			messageEntity.mUserName = source.readString();
			messageEntity.mUserAvatar = source.readString();
			messageEntity.mReplyContent = source.readString();
			messageEntity.mRepliedContent = source.readString();
			messageEntity.mId = source.readInt();
			return messageEntity;
		}

		@Override
		public MessageEntity[] newArray(int size) {
			return new MessageEntity[size];
		}
		
	};
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeSerializable(mDate);
		parcel.writeString(mUserId);
		parcel.writeString(mUserName);
		parcel.writeString(mUserAvatar);
		parcel.writeString(mReplyContent);
		parcel.writeString(mRepliedContent);
		parcel.writeInt(mId);
	}
}
