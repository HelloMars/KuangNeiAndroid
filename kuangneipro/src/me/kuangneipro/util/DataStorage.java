package me.kuangneipro.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataStorage {

	private static Context mAppContext;
	private static final String CONFIG_FILE_NAME = "kuangnei.cfg";
	
	
	public static void init(Context context){
		if(mAppContext!=null)
			return ;
		mAppContext = context;
	}
	
	
	public static void save(String key,String value){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
	    Editor editor = preferences.edit();  
	    editor.putString(key, value);  
	    editor.commit();
	}
	
	public static String load(String key){
		return load(key,null);
	}
	
	public static String load(String key,String defaultValue){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
		return preferences.getString(key, defaultValue);  
	}
}
