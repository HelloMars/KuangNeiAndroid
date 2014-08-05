package me.kuangneipro.core;

import me.kuangneipro.util.DataStorage;
import android.app.Application;

//TODO 覆盖默认的Application,添加全局配置信息
public class KuangNeiApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		DataStorage.init(this);
	}

	
}
