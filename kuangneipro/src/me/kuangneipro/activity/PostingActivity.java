package me.kuangneipro.activity;

import me.kuangneipro.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class PostingActivity extends ActionBarActivity {
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
    		String success = this.getString(R.string.info_post_success);
    		Toast.makeText(this, success, Toast.LENGTH_LONG).show();
    		super.finish();
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
