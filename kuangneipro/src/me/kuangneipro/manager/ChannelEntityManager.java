package me.kuangneipro.manager;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.util.DataStorage;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChannelEntityManager {

	public static final int CHANNEL_LIST_KEY = 0;
	public static final String CHANNEL_ID = "channel_id";
	public static final String CHANNEL_SUBTITLE = "channel_subtitle";
	public static final String CHANNEL_TITLE = "channel_title";

	public static void saveChannel(ChannelEntity ce) {
		DataStorage.save(CHANNEL_ID, ce.getId() + "");
		DataStorage.save(CHANNEL_SUBTITLE, ce.getSubtitle());
		DataStorage.save(CHANNEL_TITLE, ce.getTitle());
	}

	public static ChannelEntity loadChannel() {
		if ("-1".equals(Integer.parseInt(DataStorage.load(CHANNEL_ID, "-1"))))
			return null;
		ChannelEntity ce = new ChannelEntity();

		ce.setId(Integer.parseInt(DataStorage.load(CHANNEL_ID, "0")));
		ce.setSubtitle(DataStorage.load(CHANNEL_SUBTITLE));
		ce.setTitle(DataStorage.load(CHANNEL_TITLE));
		return ce;
	}

	public static void fillChannelListFromJson(JSONObject jsonObj,
			List<ChannelEntity> channelList) {
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

	public static void getChannelList(HttpHelper httpRequest) {
		httpRequest.setUrl(HostUtil.CHANNEL_LIST_URL).asyncGet();
	}

}
