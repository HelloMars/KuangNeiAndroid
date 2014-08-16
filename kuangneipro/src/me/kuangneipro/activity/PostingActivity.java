package me.kuangneipro.activity;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.Adapter.PostingImageAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.entity.PostingInfo;
import me.kuangneipro.entity.UploadImage;
import me.kuangneipro.manager.PostEntityManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

public class PostingActivity extends HttpActivity{
	public static final String CHANNEL_ID_KEY = "CHANNEL_ID_KEY";

	private PostingInfo mPostingInfo;
	private ChannelEntity mChannel;
	private String message;
	
	private static int RESULT_LOAD_IMAGE = 1;
	private static final String TAG = PostingActivity.class.getSimpleName(); // tag ”√”⁄≤‚ ‘log”√  
	
	private MenuItem postingButton;
	
	private final List<UploadImage> uploadImages;
	
	
	private EditText editText;
	private GridView imageGrid;
	private PostingImageAdapter postingImageAdapter;
	
	public PostingActivity(){
		uploadImages = new ArrayList<UploadImage>();
		mPostingInfo = new PostingInfo();
		mPostingInfo.setUploadImage(uploadImages);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posting);
		
		mChannel = (ChannelEntity)getIntent().getParcelableExtra(PostListActivity.SELECT_CHANNEL_INFO);
		mPostingInfo.setChannel(mChannel);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);
		
		ImageButton imgBtnChoose = (ImageButton) findViewById(R.id.imgBtnChoose);
		editText = (EditText) findViewById(R.id.editTextPost);
		imageGrid = (GridView) findViewById(R.id.imageGrid);
		postingImageAdapter = new PostingImageAdapter(this, uploadImages);
		imageGrid.setAdapter(postingImageAdapter);
		imgBtnChoose.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
	    });
	}
	
	
	
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
  
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
  
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            
            Log.i(TAG, "choice picture:" + picturePath);
            uploadImages.add(new UploadImage(picturePath));
            postingImageAdapter.notifyDataSetChanged();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.posting, menu);
		postingButton = menu.findItem(R.id.action_post);
		return true;
	}
	
	private void sendPost() {
    	message = editText.getText().toString();
    	if (message.isEmpty()) {
    		String warnning = this.getString(R.string.info_post_empty);
    		Toast.makeText(this, warnning, Toast.LENGTH_LONG).show();
    	} else {
    		mPostingInfo.setContent(message);
    		mPostingInfo.setUserid("123");
    		PostEntityManager.doPostingTotal(mPostingInfo);
    		finish();
//    		ImageUtil.gettingImageUploadToken(getHttpRequest(ImageUtil.GET_IMAGE_UPLOAD_TOKEN));
    	}
	}
	
//	@Override
//	protected void requestComplete(int id,JSONObject jsonObj) {
//		super.requestComplete(id,jsonObj);
//		switch (id) {
//		case PostEntityManager.POST_LIST_KEY:
//			ReturnInfo returnInfo = PostEntityManager.getPostingReturnInfo(jsonObj);
//			
//			if(returnInfo.getReturnCode() == ReturnInfo.SUCCESS){
//				Toast.makeText( PostingActivity.this, getString(R.string.info_post_success), Toast.LENGTH_LONG).show();
//			}else{
//				Toast.makeText( PostingActivity.this, getString(R.string.info_post_failure), Toast.LENGTH_LONG).show();
//			}
//			finish();
//			break;
//		case ImageUtil.GET_IMAGE_UPLOAD_TOKEN:
//			String token = ImageUtil.getImageUploadToken(jsonObj);
//			int uploadCount = 0;
//			if(mImgPath!=null){
//				for(int i=0;i<mImgPath.length;i++){
//					if(!TextUtils.isEmpty(mImgPath[i])){
//						uploadCount ++;
//					}
//				}
//			}
//			final int updloadCountF = uploadCount;
//			
//			if(updloadCountF == 0){
//				PostEntityManager.doPosting(getHttpRequest(PostEntityManager.POST_LIST_KEY), mChannel.getId(), message,uploadImages);
//			}
//			
//			if(mImgPath!=null){
//				for(int i=0;i<mImgPath.length;i++){
//					if(!TextUtils.isEmpty(mImgPath[i])){
//						File file = ImageUtil.compressBmpToTmpFile(mImgPath[i]);
//						if(file!=null &&file.exists()){
//							Log.i(TAG, "begin uploading:"+file.getAbsolutePath());
//							ImageUtil.uploadImg(token, file, new JSONObjectRet() {
//								@Override
//								public void onProcess(long current, long total) {
//									Log.i(TAG, "uploading:"+(current + "/" + total));
//								}
//
//								@Override
//								public void onSuccess(JSONObject resp) {
//									String redirect = ImageUtil.QINIUDN_SERVER+resp.optString("hash", "");
//									Toast.makeText(PostingActivity.this, redirect, Toast.LENGTH_LONG).show();
//									uploadImages.add(redirect);
//									if(uploadImages.size()>=updloadCountF){
//										PostEntityManager.doPosting(getHttpRequest(PostEntityManager.POST_LIST_KEY), mChannel.getId(), message,uploadImages);
//									}
//								}
//
//								@Override
//								public void onFailure(QiniuException ex) {
//									Log.e(TAG, "uploading failed!");
//								}
//							});
//							
//							
//						}
//					}
//				}
//			}
////			
//			Log.i(TAG, "getUploadToken:"+token);
//			break;
//		default:
//			break;
//		}
//		
//	}
	
	private void setPostingButtonEable(boolean enable){
		if(postingButton!=null)
			postingButton.setEnabled(false);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_post:
			setPostingButtonEable(false);
			sendPost();
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
