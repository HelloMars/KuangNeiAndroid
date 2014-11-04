package me.kuangneipro.entity;

import me.kuangneipro.util.DataStorage;

public class TopicInfo {

	public String topicInfo = DataStorage.load("topicInfo", "今日话题");
	public String topicName = DataStorage.load("topicName", "我最悲伤的时刻");
	
	public void save(){
		DataStorage.save("topicInfo", topicInfo);
		DataStorage.save("topicName", topicName);
	}
	
}
