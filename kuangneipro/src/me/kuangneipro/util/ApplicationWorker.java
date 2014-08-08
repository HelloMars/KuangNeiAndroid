package me.kuangneipro.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 管理项目中的线程池
 * @author connor
 *
 */
public class ApplicationWorker {

	private static volatile ApplicationWorker instance = null;
	
	private final ExecutorService executor;
	
	private ApplicationWorker(){
		executor = Executors.newCachedThreadPool();
	}
	
	public static ApplicationWorker getInstance(){
		
		if(instance==null){
			synchronized (ApplicationWorker.class) {
				if(instance==null){
					instance = new ApplicationWorker();
				}
			}
		}
		
		return instance;
	}
	
	public void execute(Runnable runnable){
		executor.execute(runnable);
	}
	
}
