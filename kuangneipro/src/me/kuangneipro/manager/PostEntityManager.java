package me.kuangneipro.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.PushUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PostEntityManager {

	public static void getPostList(HttpHelper httpRequest){
		httpRequest.setUrl(HostUtil.POST_LIST_URL).asyncGet();
	}
	
	public static void fillPostListFromJson(JSONObject jsonObj , List<PostEntity> mPostList){
		try {
			JSONArray jsonarray = jsonObj.getJSONArray("list");
			for (int i = 0; i < jsonarray.length(); i++) {
	    		JSONObject oneJson = jsonarray.getJSONObject(i);
	    		JSONObject user = oneJson.getJSONObject("user");
	    		if (user == null)
	    			continue;
	    		PostEntity channel = new PostEntity(
	    				user.getInt("id"),
	    				user.getString("name"),
	    				oneJson.getString("title"),
	    				oneJson.getString("content"),
	    				oneJson.getInt("opposedCount"),
	    				oneJson.getInt("upCount"),
	    				oneJson.getString("postTime"));
	    		mPostList.add(channel);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void doPosting(HttpHelper httpRequest,int channelId,String content ){
		try {
			httpRequest.setUrl(HostUtil.POSTING_URL).put("userid", PushUtil.getToken()).put("channelid",channelId+"").put("content", URLEncoder.encode(content,"UTF-8")).asyncPost();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	public static ReturnInfo getPostingReturnInfo(JSONObject jsonObj){
    	return ReturnInfo.fromJSONObject(jsonObj);
	}
	
	
}
