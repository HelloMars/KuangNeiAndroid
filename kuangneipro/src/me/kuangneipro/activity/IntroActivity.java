package me.kuangneipro.activity;

import com.igexin.sdk.PushManager;

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
		//个推请求clientid,并注册接收监听
        PushManager.getInstance().initialize(this.getApplicationContext());
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
