package me.kuangneipro.activity;

import me.kuangneipro.R;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.manager.UserInfoManager;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * 个人信息页面
 * @author connorlu
 *
 */
public class PersonalInfoActivity extends HttpActivity{
	private ImageView avatar;
	private TextView name;
	private EditText nameEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);
		
		View background = findViewById(R.id.background);
		background.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				disableEdit();
			}
		});
		
		avatar = (ImageView)findViewById(R.id.avatar);
		name = (TextView)findViewById(R.id.name);
		nameEdit = (EditText)findViewById(R.id.name_edit);
		
		UserInfoManager.updateUserInfo(getHttpRequest(UserInfoManager.UPDATE_USER_INFO), UserInfo.loadSelfUserInfo());
		
		
	}
	
	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		switch (id) {
		case UserInfoManager.UPDATE_USER_INFO:
			Log.e("xx", "xx");
			
			UserInfo userInfo = UserInfoManager.fillUserInfoFromJson(jsonObj);
			if(userInfo!=null){
				Picasso.with(this)
	        	.load(userInfo.getAvatar())
	        	.placeholder(android.R.drawable.ic_menu_gallery)
	        	.error(android.R.drawable.ic_menu_report_image)
	        	.resize(120, 120)
	        	.centerCrop()
	        	.into(avatar);
				name.setText(userInfo.getUsername());
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 禁用编辑态
	 */
	private void disableEdit(){
		
	}
}
