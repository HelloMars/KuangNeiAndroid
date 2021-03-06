package me.kuangneipro.entity;

import java.util.Date;

import me.kuangneipro.util.DataStorage;

import org.json.JSONObject;

import android.text.TextUtils;

public class UserInfo {
	
	private String username;
	private String password;
	private String avatar;
	private String name;
	
	public static final int WOMAN = 0;
	public static final int MAN = 1;
	public static final int NEUTRAL = 2;
	public static final int NOT_SET = 3;
	private int sex;	
	private String sign;
	
	private Date birthday;
	
	
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String AVATAR_KEY = "avatar";
	private static final String NAME_KEY = "nickname";
	private static final String SEX_KEY = "sex";
	private static final String SIGN_KEY = "sign";
	private static final String BIRTHDAY_KEY = "birthday";
	
	public static void saveSelfUserInfo(UserInfo userInfo){
		DataStorage.save(USERNAME_KEY, userInfo.getUsername());
		DataStorage.save(PASSWORD_KEY, userInfo.getPassword());
		DataStorage.save(AVATAR_KEY, userInfo.getAvatar());
		DataStorage.save(NAME_KEY, userInfo.getName());
		DataStorage.save(SEX_KEY, userInfo.getSex()+"");
		DataStorage.save(SIGN_KEY, userInfo.getSign());
		if( userInfo.getBirthday()!= null)
		DataStorage.save(BIRTHDAY_KEY, userInfo.getBirthday().getTime());
	}
	
	public static UserInfo loadSelfUserInfo(){
		
		if(!TextUtils.isEmpty(DataStorage.loadString(USERNAME_KEY))){
			UserInfo userInfo = new UserInfo();
			userInfo.username = DataStorage.loadString(USERNAME_KEY);
			userInfo.password = DataStorage.loadString(PASSWORD_KEY);
			userInfo.avatar = DataStorage.loadString(AVATAR_KEY);
			userInfo.name = DataStorage.loadString(NAME_KEY);
			userInfo.sex = Integer.parseInt(DataStorage.load(SEX_KEY,UserInfo.MAN+""));
			userInfo.sign = DataStorage.loadString(SIGN_KEY);
			userInfo.birthday = new Date(DataStorage.loadLong(BIRTHDAY_KEY));
			return userInfo;
		}
		
		return null;
	}
	
	public static UserInfo fromJson(String username,String password,JSONObject jObject){
		
		if(jObject!=null){
			UserInfo userInfo = new UserInfo();
			userInfo.username = username;
			userInfo.password = password;
			userInfo.avatar = jObject.optString("avatar", "");
			userInfo.name = jObject.optString("nickname", "");
			userInfo.sex = jObject.optInt("sex", NOT_SET);
			userInfo.sign = jObject.optString("sign", "");
			userInfo.birthday = new Date(jObject.optLong("birthday", 0));
			return userInfo;
		}
		
		return null;
	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public boolean isMan(){
		return sex == MAN;
	}
	public boolean isWoman(){
		return sex == WOMAN;
	}
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	
}
