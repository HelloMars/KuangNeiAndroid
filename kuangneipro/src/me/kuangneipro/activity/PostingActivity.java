package me.kuangneipro.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONObject;

import me.kuangneipro.R;
import me.kuangneipro.util.JasonReader;
import me.kuangneipro.util.PushUtil;
import android.os.AsyncTask;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
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

public class PostingActivity extends ActionBarActivity {
	public static final String CHANNEL_ID_KEY = "CHANNEL_ID_KEY";
	private int mChoosed = 0;
	private LinearLayout mImageRow;
	private ImageView[] mImgView = new ImageView[4];
	private String[] mImgPath = new String[4];
	
	private static int RESULT_LOAD_IMAGE = 1;
	private static final String TAG = MainActivity.class.getSimpleName(); // tag 用于测试log用  
	
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
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);
		//getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_edit);
		
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
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int thumbnailSize) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > thumbnailSize || width > thumbnailSize) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)thumbnailSize);
	        } else {
	            inSampleSize = Math.round((float)width / (float)thumbnailSize);
	        }
	    }
	    return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmap(String imgFile, int thumbnailSize) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imgFile, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, thumbnailSize);
	    Log.i(TAG, "!!!!!!!!!!options.inSampleSize" + options.inSampleSize);
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(imgFile, options);
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
            
            Log.i(TAG, "!!!!!!!!!!" + picturePath);
            mImgPath[mChoosed] = picturePath;
            mImgView[mChoosed].setImageBitmap(decodeSampledBitmap(picturePath, 70));
            ++mChoosed;
            
            this.updateImages();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.posting, menu);
		return true;
	}
	
	private void sendPost() {
		EditText editText = (EditText) findViewById(R.id.editTextPost);
    	String message = editText.getText().toString();
    	if (message.isEmpty()) {
    		String warnning = this.getString(R.string.info_post_empty);
    		Toast.makeText(this, warnning, Toast.LENGTH_LONG).show();
    	} else {
    		
    		try {
				new AsyncTask<String, Void, Boolean>() {
				    protected Boolean doInBackground(String... urls) {
				        try {
				        	final JSONObject jsonObj = JasonReader.readJsonFromUrl(urls[0]);
				        	int returnCode = jsonObj.optInt("returnCode", -1);
				        	if(returnCode!=0){
				        		runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText( PostingActivity.this, jsonObj.optString("returnMessage","发送失败"), Toast.LENGTH_LONG).show();
									}
								});
				        		 
				        		return false;
				        	}
				        	return true;
				        } catch (Exception e) {
				            e.printStackTrace();
				            return false;
				        }
				    }

				    protected void onPostExecute(Boolean success) {
				        String message = PostingActivity.this.getString(R.string.info_post_failure);
				       if(success){
				    	   message = PostingActivity.this.getString(R.string.info_post_success);
				       }
				       Toast.makeText( PostingActivity.this, message, Toast.LENGTH_LONG).show();
				       PostingActivity.super.finish();
				    }
				}.execute("http://182.92.100.49/kuangnei/api/post/?userid="+PushUtil.getToken()+"&channelid=0&content="+URLDecoder.decode(message,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    		
    	}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_post:
			sendPost();
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
