package me.kuangneipro.activity;

import me.kuangneipro.R;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.emoticon.EmoticonEditText;
import me.kuangneipro.emoticon.EmoticonInputPopupView;
import me.kuangneipro.emoticon.EmoticonPopupable;
import me.kuangneipro.emoticon.EmoticonRelativeLayout;
import me.kuangneipro.entity.KuangInfo;
import me.kuangneipro.manager.ReplyInfoManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class PingActivity extends HttpActivity{
	
	private EmoticonEditText mEditText;
	
	private EmoticonPopupable mEmoticonPopupable;
	private View back;
	private View posting;
	
	
	public PingActivity(){
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ping);
		
		back = findViewById(R.id.back);
		posting = findViewById(R.id.posting);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		posting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				sendPing();
			}
		});
		
		mEditText = (EmoticonEditText) findViewById(R.id.editTextPost);
		
		KuangInfo kuang = KuangInfo.loadSelfKuangInfo();
		String kuangName = "";
		if (kuang == null) {
			kuangName="内部";
		} else {
			kuangName=kuang.getName();
		}
		
		mEmoticonPopupable = new EmoticonInputPopupView(this);
		mEmoticonPopupable.setParentView( findViewById(R.id.inputContainer));
		mEmoticonPopupable.bindEmoticonEditText(mEditText,null,100);
		mEmoticonPopupable.getEmoticonEditText().setHint("随机发送给"+kuangName+"的一位用户");
		EmoticonRelativeLayout rootLayout = (EmoticonRelativeLayout) findViewById(R.id.RelativeLayout1);
		rootLayout.setEmoticonInputPopupView((EmoticonInputPopupView)mEmoticonPopupable);
		
	}
	
	
	private void sendPing() {
		setPostingButtonEable(false);
    	String message = mEditText.toString();
    	if (message.isEmpty()) {
    		String warnning = this.getString(R.string.info_post_empty);
    		Toast.makeText(this, warnning, Toast.LENGTH_LONG).show();
    		setPostingButtonEable(true);
    	} else {
    		ReplyInfoManager.doFloater(this, message);
    		finish();
    	}
	}
	
	private void setPostingButtonEable(boolean enable){
		if(posting!=null)
			posting.setEnabled(enable);
	}
	
}
