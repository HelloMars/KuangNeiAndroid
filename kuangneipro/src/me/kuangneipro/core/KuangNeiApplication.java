package me.kuangneipro.core;

import me.kuangneipro.util.ApplicationWorker;
import me.kuangneipro.util.DataStorage;
import android.app.Application;


/**
 *  覆盖默认的Application,添加全局配置信息,初始化全局工具类
 * @author connor
 *
 */
public class KuangNeiApplication extends Application {

	private static KuangNeiApplication instance;
	
	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();
		//初始化存储文件管理器
		DataStorage.init(this);
		//初始化线程池
		ApplicationWorker.getInstance();
	}
	
	public static KuangNeiApplication getInstance(){
		return instance;
	}

	
}
