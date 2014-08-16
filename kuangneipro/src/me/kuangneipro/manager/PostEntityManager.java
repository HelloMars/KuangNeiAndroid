package me.kuangneipro.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.kuangneipro.R;
import me.kuangneipro.core.KuangNeiApplication;
import me.kuangneipro.entity.PostEntity;
import me.kuangneipro.entity.PostingInfo;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.entity.UploadImage;
import me.kuangneipro.util.ApplicationWorker;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;
import me.kuangneipro.util.HttpHelper.RequestCallBackListener;
import me.kuangneipro.util.ImageUtil;
import me.kuangneipro.util.PushUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.qiniu.auth.JSONObjectRet;
import com.qiniu.utils.QiniuException;


public class PostEntityManager {
	private static final String TAG = "PostEntityManager";
	
	public static final int POST_LIST_KEY = 0;
	public static final int POSTING_KEY = 1;

	public static void getPostList(HttpHelper httpRequest, int channelId, int page){
		httpRequest.setUrl(HostUtil.POST_LIST_URL).put("userid", "1").put("channelid", channelId+"").put("page", page+"").asyncGet();
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
	    				user.getString("id"),
	    				user.getString("name"),
	    				user.getString("avatar"),
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
	
	
	public static void doPostingTotal(final PostingInfo postingInfo){
		
		ApplicationWorker.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				HttpHelper httpTokenGet = new HttpHelper(ImageUtil.GET_IMAGE_UPLOAD_TOKEN);
				final List<UploadImage> uploadImages = postingInfo.getUploadImage();
				final int imageSize = uploadImages.size();
				final AtomicInteger index = new AtomicInteger(0);
				if(uploadImages != null && !uploadImages.isEmpty()){
					
					httpTokenGet.setRequestCallBackListener(new RequestCallBackListener() {
						@Override
						public void onRequestComplete(int id, JSONObject jsonObj) {
							String token = ImageUtil.getImageUploadToken(jsonObj);
							for(int i=0;i<imageSize;i++){
								String localPath = uploadImages.get(i).getLocalPath();
								if(!TextUtils.isEmpty(localPath)){
									File file = ImageUtil.compressBmpToTmpFile(localPath);
									if(file!=null &&file.exists()){
										final int j = i;
										ImageUtil.uploadImg(token, file, new JSONObjectRet() {
											@Override
											public void onProcess(long current, long total) {
											}
											@Override
											public void onSuccess(JSONObject resp) {
												String redirect = ImageUtil.QINIUDN_SERVER+resp.optString("hash", "");
												uploadImages.get(j).setRemotePath(redirect);
												index.incrementAndGet();
												if(index.get()>=imageSize){
													doPosting(postingInfo);
												}
												Log.e(TAG, redirect);
											}
											@Override
											public void onFailure(QiniuException ex) {
												index.incrementAndGet();
												if(index.get()>=imageSize){
													doPosting(postingInfo);
												}
												Log.e(TAG, ex.toString());
											}
										});
										Log.e(TAG, "uploading");
										
									}else{
										index.incrementAndGet();
										if(index.get()>=imageSize){
											doPosting(postingInfo);
										}
									}
								}else{
									index.incrementAndGet();
									if(index.get()>=imageSize){
										doPosting(postingInfo);
									}
								}
							}
							
						}
					});
					ImageUtil.gettingImageUploadToken(httpTokenGet);
				}else{
					doPosting(postingInfo);
				}
				
			}
		});
	}
	
	private static void doPosting(PostingInfo postingInfo){
		HttpHelper httpPostingGet = new HttpHelper(POSTING_KEY);
		
		httpPostingGet.setRequestCallBackListener(new RequestCallBackListener() {
			@Override
			public void onRequestComplete(int id, JSONObject jsonObj) {
				ReturnInfo returnInfo = ReturnInfo.fromJSONObject(jsonObj);
				final Context context = KuangNeiApplication.getInstance();
				if(returnInfo.getReturnCode() == ReturnInfo.SUCCESS){
					ApplicationWorker.getInstance().executeOnUIThrean(new Runnable() {
						@Override
						public void run() {
							Toast.makeText( context, context.getString(R.string.info_post_success), Toast.LENGTH_SHORT).show();
						}
					});
					
				}else{
					ApplicationWorker.getInstance().executeOnUIThrean(new Runnable() {
						@Override
						public void run() {
							Toast.makeText( context, context.getString(R.string.info_post_failure), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
		doPosting(httpPostingGet, postingInfo.getChannel().getId(), postingInfo.getContent(), postingInfo.getUploadImage());
	}
	
	
	
	private static void doPosting(HttpHelper httpRequest,int channelId,String content,List<UploadImage> updatedImagePath ){
			httpRequest.setUrl(HostUtil.POSTING_URL).put("userid", PushUtil.getToken()).put("channelid",channelId+"").put("content", content);
			
			boolean isNotFirst = false;
			StringBuilder sb = new StringBuilder();
			if(updatedImagePath!=null&& !updatedImagePath.isEmpty()){
				for(UploadImage imagePath : updatedImagePath){
					if(!TextUtils.isEmpty(imagePath.getRemotePath()))
						if(isNotFirst){
							sb.append('@');	
						}else{
							isNotFirst = true;
						}
						sb.append(imagePath.getRemotePath());
					
				}
			}
			if(!TextUtils.isEmpty(sb.toString()))
				httpRequest.put("imageurl", sb.toString());
			
			Log.i(TAG, sb.toString()+"!");
			
			httpRequest.asyncPost();
	}
	
	
	public static ReturnInfo getPostingReturnInfo(JSONObject jsonObj){
    	return ReturnInfo.fromJSONObject(jsonObj);
	}
	
	
}
