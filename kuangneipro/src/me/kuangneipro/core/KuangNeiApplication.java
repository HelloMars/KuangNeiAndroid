package me.kuangneipro.core;

import me.kuangneipro.util.ApplicationWorker;
import me.kuangneipro.util.DataStorage;
import android.app.Application;


/**
 *  ����Ĭ�ϵ�Application,���ȫ��������Ϣ,��ʼ��ȫ�ֹ�����
 * @author connor
 *
 */
public class KuangNeiApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		//��ʼ���洢�ļ�������
		DataStorage.init(this);
		//��ʼ���̳߳�
		ApplicationWorker.getInstance();
	}

	
}
