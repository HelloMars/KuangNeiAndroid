package me.kuangneipro.util;

public class HostUtil {

	/**
	 * 框内服务器地址
	 */
	public static final String KUANG_NEI_HOST = "http://kuangnei.me/zhumeng/kuangnei/api/";
	public static final String KUANG_NEI_HOST_HTTPS = "https://kuangnei.me/zhumeng/kuangnei/api/";
	
	/**
	 * 频道列表协议地址
	 */
	public static final String CHANNEL_LIST_URL = KUANG_NEI_HOST+"channellist/";
	
	/**
	 * 帖子列表协议地址
	 */
	public static final String POST_LIST_URL = KUANG_NEI_HOST+"postlist/";
	
	/**
	 * 发帖子协议地址
	 */
	public static final String POSTING_URL = KUANG_NEI_HOST+"post/";
	
	
	/**
	 * 图片上传Token获取地址
	 */
	public static final String GET_UP_TOKEN_URL = KUANG_NEI_HOST+"getUpToken/";
	
	/**
	 * 注册
	 */
	public static final String REGISTER = KUANG_NEI_HOST_HTTPS + "register/";
	
	/**
	 * 登陆
	 */
	public static final String SIGN_IN =  KUANG_NEI_HOST_HTTPS +"signin/";
	
	/**
	 * 一级回复列表
	 */
	public static final String REPLY_FIRST_LEVEL = KUANG_NEI_HOST + "firstLevelReplyList/";
	
	/**
	 * 二级回复列表
	 */
	public static final String REPLY_SECOND_LEVEL = KUANG_NEI_HOST + "secondLevelReplyList/";
}
