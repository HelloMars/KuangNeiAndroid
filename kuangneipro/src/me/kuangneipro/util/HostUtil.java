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
	 * 消息列表协议地址
	 */
	public static final String MESSAGE_LIST_URL = KUANG_NEI_HOST+"replyToMine/";
	
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
//	public static final String REGISTER = KUANG_NEI_HOST_HTTPS + "register/";
	
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
	
	/**
	 * 一级回复
	 */
	public static final String DO_REPLY_FIRST_LEVEL = KUANG_NEI_HOST + "replyFirstLevel/";
	
	/**
	 * 二级回复
	 */
	public static final String DO_REPLY_SECOND_LEVEL = KUANG_NEI_HOST + "replySecondLevel/";
	
	/**
	 * 设置用户信息
	 */
	public static final String ADD_USER_INFO = KUANG_NEI_HOST + "addUserInfo/";
	
	/**
	 * 注册
	 */
	public static final String REGISTER = KUANG_NEI_HOST + "register/";
	
	
	public static final String REPLY = KUANG_NEI_HOST + "replyList/";
	
	public static final String DO_REPLY = KUANG_NEI_HOST + "reply/";
	
	public static final String UN_READ = KUANG_NEI_HOST + "hasUnreadMessage/";
	
	public static final String UP_POST = KUANG_NEI_HOST + "uppost/";
	
	public static final String GET_MESSAGE = KUANG_NEI_HOST + "replyInfo/";
	
	public static final String GET_FLOATER = KUANG_NEI_HOST + "floater/";
	
    /**
     * 框列表协议地址
     */
    public static final String KUANG_LIST_URL = KUANG_NEI_HOST+"getSchool/";
    
    /**
     * 意见反馈地址
     */
    public static final String FEED_BACK_URL = KUANG_NEI_HOST+"feedBack/";
}
