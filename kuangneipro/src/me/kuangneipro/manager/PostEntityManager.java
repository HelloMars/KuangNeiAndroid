package me.kuangneipro.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
	
	public static final int POST_LIST_KEY = 0;
	public static final int POSTING_KEY = 1;

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
	    		List<String> pictureList = new ArrayList<String>();
	    		JSONArray pictures = oneJson.getJSONArray("pictures");
	    		for (int j = 0; j < pictures.length(); ++j)
	    			pictureList.add(pictures.getString(j));
	    		PostEntity channel = new PostEntity(
	    				user.getInt("id"),
	    				user.getString("name"),
	    				user.getString("avatar"),
	    				oneJson.getString("title"),
	    				oneJson.getString("content"),
	    				oneJson.getInt("opposedCount"),
	    				oneJson.getInt("upCount"),
	    				oneJson.getString("postTime"),
	    				pictureList);
	    		mPostList.add(channel);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void doPosting(HttpHelper httpRequest,int channelId,String content,List<String> updatedImagePath ){
		try {
			httpRequest.setUrl(HostUtil.POSTING_URL).put("userid", PushUtil.getToken()).put("channelid",channelId+"").put("content", URLEncoder.encode(content,"UTF-8"));
			
			boolean isNotFirst = false;
			StringBuilder sb = new StringBuilder();
			if(updatedImagePath!=null&& !updatedImagePath.isEmpty()){
				for(String imagePath : updatedImagePath){
					if(isNotFirst){
						sb.append('@');	
					}else{
						isNotFirst = true;
						sb.append(imagePath);
					}
					
				}
			}
			httpRequest.put("imageurl", sb.toString());
			httpRequest.asyncGet();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	public static ReturnInfo getPostingReturnInfo(JSONObject jsonObj){
    	return ReturnInfo.fromJSONObject(jsonObj);
	}
	
	
}
