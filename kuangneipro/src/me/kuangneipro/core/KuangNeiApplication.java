package me.kuangneipro.core;

import me.kuangneipro.util.DataStorage;
import android.app.Application;

//TODO ����Ĭ�ϵ�Application,���ȫ��������Ϣ
public class KuangNeiApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		DataStorage.init(this);
	}

	
}
