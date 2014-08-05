package me.kuangneipro.util;

public class PushUtil {
	private static final String TOKEN_KEY="token";
	
	public static void saveToken(String token){
		DataStorage.save(TOKEN_KEY, token);
	}
	
	public static String getToken(){
		return DataStorage.load(TOKEN_KEY);
	}
}
