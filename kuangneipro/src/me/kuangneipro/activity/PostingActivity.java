package me.kuangneipro.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONObject;

import me.kuangneipro.R;
import me.kuangneipro.util.JasonReader;
import me.kuangneipro.util.PushUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class PostingActivity extends ActionBarActivity {
	public static final String CHANNEL_ID_KEY = "CHANNEL_ID_KEY";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posting);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);
		//getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_edit);
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
				        	JSONObject jsonObj = JasonReader.readJsonFromUrl(urls[0]);
				        	int returnCode = jsonObj.optInt("returnCode", -1);
				        	if(returnCode!=0)
				        		return false;
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
				}.execute("http://182.92.100.49/kuangnei/api/post/?userid="+PushUtil.getToken()+"&channelId=0&content="+URLDecoder.decode(message,"UTF-8"));
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
