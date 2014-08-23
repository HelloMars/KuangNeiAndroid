package me.kuangneipro.activity;

import me.kuangneipro.R;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.util.LoginUtil;
import me.kuangneipro.util.LoginUtil.OnSignInLisener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	private Button registerButton;
	private EditText editPhone;
	private EditText editPassword;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_register);
		registerButton = (Button)findViewById(R.id.btnSignin);
		editPhone = (EditText)findViewById(R.id.editPhone);
		editPassword = (EditText)findViewById(R.id.editPassword);
		
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(TextUtils.isEmpty(editPhone.getText())|| TextUtils.isEmpty(editPassword.getText())){
					Toast.makeText(RegisterActivity.this, "请输入账号密码", Toast.LENGTH_SHORT).show();
				}else{
					LoginUtil.register(editPhone.getText().toString(), editPassword.getText().toString(), new OnSignInLisener() {
						
						@Override
						public void onSignInFinish(boolean isSuccess, UserInfo userInfo) {
							if(isSuccess){
								Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
						    	startActivity(intent);
							}else{
								Toast.makeText(RegisterActivity.this, "登录失败，请重试！", Toast.LENGTH_SHORT).show();
							}
							
						}
					});
				}
				
				
			}
		});
	}
}
