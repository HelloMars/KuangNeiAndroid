package me.kuangneipro.entity;

import android.os.Parcel;
import android.os.Parcelable;

public final class ChannelEntity implements Parcelable{
	private int mId;
	private String mTitle;
	private String mSubtitle;	
	
	public ChannelEntity(){}
	
	public ChannelEntity(int id,String title, String subtitle){
		mId = id;
        mTitle = title;
        mSubtitle = subtitle;
    }

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getSubtitle() {
		return mSubtitle;
	}

	public void setSubtitle(String mSubtitle) {
		this.mSubtitle = mSubtitle;
	}
	
	public static final Parcelable.Creator<ChannelEntity> CREATOR = new Creator<ChannelEntity>() {  
		  
        @Override  
        public ChannelEntity createFromParcel(Parcel source) {  
        	ChannelEntity channelEntity = new ChannelEntity();  
        	channelEntity.mId = source.readInt();  
        	channelEntity.mTitle = source.readString();  
        	channelEntity.mSubtitle = source.readString();  
            return channelEntity;  
        }  
  
        @Override  
        public ChannelEntity[] newArray(int size) {  
            return new ChannelEntity[size];  
        }  
    };

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(mId);
		parcel.writeString(mTitle);
		parcel.writeString(mSubtitle);
	}
	
	
	
}
