package me.kuangneipro.entity;

import java.util.Date;

import me.kuangneipro.util.DateUtil;

/**
 * 回复帖子信息
 * @author connorlu
 *
 */
public class ReplyInfo {

	public int id;
	public int postId;
	public int ReplyId;
	
	public int editStatus;
	public Date replyTime;
	public String content;
	public int upCount;
	
	public User toUser;
	public User fromUser;
	
	public User replyUser;
	
	public String getDate() {
		return DateUtil.getReadableDateStr(replyTime);
	}
	
	public static class User{
		public int id;
		public String name;
	}
	
}
