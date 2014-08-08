package me.kuangneipro.manager;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChannelEntityManager {

	public static void fillChannelListFromJson(JSONObject jsonObj , List<ChannelEntity> channelList){
		try {
			JSONArray jsonarray = jsonObj.getJSONArray("list");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject oneChannelJson = jsonarray.getJSONObject(i);
				ChannelEntity channel = new ChannelEntity(
						oneChannelJson.optInt("id"),
						oneChannelJson.getString("title"),
						oneChannelJson.getString("subtitle"));
				if (channelList == null)
					channelList = new ArrayList<ChannelEntity>();
				channelList.add(channel);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void getChannelList(HttpHelper httpRequest){
		httpRequest.setUrl(HostUtil.CHANNEL_LIST_URL).asyncGet();
	}
	
}
