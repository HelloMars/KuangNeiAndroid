package me.kuangneipro.entity;

import java.util.List;

public class PostingInfo {

	private List<UploadImage> uploadImage;
	private String content;
	private ChannelEntity channel;
	private String userid;

	public PostingInfo() {
	}
	
	public PostingInfo(List<UploadImage> uploadImage, String content,
			ChannelEntity channel, String userid) {
		this.uploadImage = uploadImage;
		this.content = content;
		this.channel = channel;
		this.userid = userid;
	}

	public List<UploadImage> getUploadImage() {
		return uploadImage;
	}

	public void setUploadImage(List<UploadImage> uploadImage) {
		this.uploadImage = uploadImage;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ChannelEntity getChannel() {
		return channel;
	}

	public void setChannel(ChannelEntity channel) {
		this.channel = channel;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
}
