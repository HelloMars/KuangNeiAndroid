package me.kuangneipro.activity;

import me.kuangneipro.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.igexin.sdk.PushManager;

@SuppressLint("HandlerLeak") 
public class IntroActivity extends Activity implements OnClickListener{

	private View intro;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		//个推请求clientid,并注册接收监听
        PushManager.getInstance().initialize(this.getApplicationContext());
		intro = findViewById(R.id.intro);
		intro.setEnabled(false);
		intro.setOnClickListener(this);
		
		 
		handler.sendEmptyMessageDelayed(0, 2000);
	}
	
	private Handler handler = new Handler() {
		 
        @Override
        public void handleMessage(Message msg) {
        	if(intro!=null)
        		intro.setEnabled(true);
            super.handleMessage(msg);
        }
    };

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this, MapActivity.class);
		this.startActivity(intent);
		this.finish();
	}
	
}
