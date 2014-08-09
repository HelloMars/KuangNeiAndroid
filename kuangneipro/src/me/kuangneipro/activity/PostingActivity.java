package me.kuangneipro.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.manager.PostEntityManager;
import me.kuangneipro.util.ImageUtil;

import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qiniu.auth.JSONObjectRet;
import com.qiniu.utils.QiniuException;

public class PostingActivity extends HttpActivity{
	public static final String CHANNEL_ID_KEY = "CHANNEL_ID_KEY";
	private int mChoosed = 0;
	private LinearLayout mImageRow;
	private ImageView[] mImgView = new ImageView[4];
	private String[] mImgPath = new String[4];
	
	private ChannelEntity mChannel;
	
	private String message;
	
	private static int RESULT_LOAD_IMAGE = 1;
	private static final String TAG = PostingActivity.class.getSimpleName(); // tag ”√”⁄≤‚ ‘log”√  
	
	private MenuItem postingButton;
	
	private final List<String> updatedImagePath;
	
	public PostingActivity(){
		updatedImagePath = new ArrayList<String>();
	}
	
	protected void updateImages() {
		for (int i = 0; i < 4; ++i) {
			if (i <= mChoosed)
				mImgView[i].setVisibility(View.VISIBLE);
			else
				mImgView[i].setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posting);
		
		mChannel = (ChannelEntity)getIntent().getParcelableExtra(PostListActivity.SELECT_CHANNEL_INFO);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);
		
		ImageButton imgBtnChoose = (ImageButton) findViewById(R.id.imgBtnChoose);
		mImageRow = (LinearLayout) findViewById(R.id.imageRow);
		mImageRow.setVisibility(View.INVISIBLE);
		
		mImgView[0] = (ImageView) findViewById(R.id.imageView1);
		mImgView[1] = (ImageView) findViewById(R.id.imageView2);
		mImgView[2] = (ImageView) findViewById(R.id.imageView3);
		mImgView[3] = (ImageView) findViewById(R.id.imageView4);
		this.updateImages();
		
		for (int i = 0; i < 4; ++i) {
			mImgView[i].setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	                startActivityForResult(i, RESULT_LOAD_IMAGE);
				}
			});
		}
		
		imgBtnChoose.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mImageRow.setVisibility(View.VISIBLE);
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
            mImgPath[mChoosed] = picturePath;
            mImgView[mChoosed].setImageBitmap(ImageUtil.decodeSampledBitmap(picturePath, 70));
            ++mChoosed;
            
            this.updateImages();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.posting, menu);
		postingButton = menu.findItem(R.id.action_post);
		return true;
	}
	
	private void sendPost() {
		EditText editText = (EditText) findViewById(R.id.editTextPost);
    	message = editText.getText().toString();
    	updatedImagePath.clear();
    	if (message.isEmpty()) {
    		String warnning = this.getString(R.string.info_post_empty);
    		Toast.makeText(this, warnning, Toast.LENGTH_LONG).show();
    	} else {
    		ImageUtil.gettingImageUploadToken(getHttpRequest(ImageUtil.GET_IMAGE_UPLOAD_TOKEN));
    	}
	}
	
	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		switch (id) {
		case PostEntityManager.POST_LIST_KEY:
			ReturnInfo returnInfo = PostEntityManager.getPostingReturnInfo(jsonObj);
			
			if(returnInfo.getReturnCode() == ReturnInfo.SUCCESS){
				Toast.makeText( PostingActivity.this, getString(R.string.info_post_success), Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText( PostingActivity.this, getString(R.string.info_post_failure), Toast.LENGTH_LONG).show();
			}
			finish();
			break;
		case ImageUtil.GET_IMAGE_UPLOAD_TOKEN:
			String token = ImageUtil.getImageUploadToken(jsonObj);
			int uploadCount = 0;
			if(mImgPath!=null){
				for(int i=0;i<mImgPath.length;i++){
					if(!TextUtils.isEmpty(mImgPath[i])){
						uploadCount ++;
					}
				}
			}
			final int updloadCountF = uploadCount;
			
			if(mImgPath!=null){
				for(int i=0;i<mImgPath.length;i++){
					if(!TextUtils.isEmpty(mImgPath[i])){
						File file = ImageUtil.compressBmpToTmpFile(mImgPath[i]);
						if(file!=null &&file.exists()){
							Log.i(TAG, "begin uploading:"+file.getAbsolutePath());
							ImageUtil.uploadImg(token, file, new JSONObjectRet() {
								@Override
								public void onProcess(long current, long total) {
									Log.i(TAG, "uploading:"+(current + "/" + total));
								}

								@Override
								public void onSuccess(JSONObject resp) {
									String redirect = ImageUtil.QINIUDN_SERVER+resp.optString("hash", "");
									Toast.makeText(PostingActivity.this, redirect, Toast.LENGTH_LONG).show();
									updatedImagePath.add(redirect);
									if(updatedImagePath.size()>=updloadCountF){
										PostEntityManager.doPosting(getHttpRequest(PostEntityManager.POST_LIST_KEY), mChannel.getId(), message,updatedImagePath);
									}
								}

								@Override
								public void onFailure(QiniuException ex) {
									Log.e(TAG, "uploading failed!");
								}
							});
							
							
						}
					}
				}
			}
//			
			Log.i(TAG, "getUploadToken:"+token);
			break;
		default:
			break;
		}
		
	}
	
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
