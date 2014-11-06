package me.kuangneipro.activity;

import me.kuangneipro.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class IntroActivity extends Activity implements OnClickListener{

	private View intro;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		intro = findViewById(R.id.intro);
		intro.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this, MapActivity.class);
		this.startActivity(intent);
		this.finish();
	}
	
}
