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
	
	public static void save(String key,boolean value){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
	    Editor editor = preferences.edit();  
	    editor.putBoolean(key, value);  
	    editor.commit();
	}
	
	public static void save(String key,long value){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
	    Editor editor = preferences.edit();  
	    editor.putLong(key, value);  
	    editor.commit();
	}
	
	public static void save(String key,int value){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
	    Editor editor = preferences.edit();  
	    editor.putInt(key, value);  
	    editor.commit();
	}
	
	public static void save(String key,String value){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
	    Editor editor = preferences.edit();  
	    editor.putString(key, value);  
	    editor.commit();
	}
	
	public static String loadString(String key){
		return load(key,null);
	}
	
	public static int loadInt(String key){
		return load(key,0);
	}
	
	public static long loadLong(String key){
		return load(key,0l);
	}
	
	public static boolean loadBoolean(String key){
		return load(key,false);
	}
	
	public static String load(String key,String defaultValue){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
		return preferences.getString(key, defaultValue);  
	}
	
	public static int load(String key,int defaultValue){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
		return preferences.getInt(key, defaultValue);  
	}
	
	public static long load(String key,long defaultValue){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
		return preferences.getLong(key, defaultValue);  
	}
	
	public static boolean load(String key,boolean defaultValue){
		SharedPreferences preferences =	mAppContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);  
		return preferences.getBoolean(key, defaultValue);  
	}
}
