package me.kuangneipro.activity;

import java.util.Calendar;

import me.kuangneipro.R;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.manager.UserInfoManager;
import me.kuangneipro.util.ColorUtil;
import me.kuangneipro.util.SexUtil;
import me.kuangneipro.util.SexUtil.OnSexSelectListener;

import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * 个人信息页面
 * @author connorlu
 *
 */
public class PersonalInfoActivity extends HttpActivity implements OnClickListener, OnDateSetListener{
	private static final int RESULT_LOAD_IMAGE = 1;
	private static final int SELECT_DATE = 2;
	private String userSeletedImagePath;
	private Calendar birthdayCalendar;
	
	private ImageView avatar;
	private View nameLayout;
	private TextView name;
	private EditText nameEdit;
	private View nameEditLayout;
	private View nameSubmit;
	private View sexLayout;
	private TextView sex;
	private View birthdayLayout;
	private TextView birthday;
	private View signLayout;
	private TextView sign;
	private EditText signEdit;
	private View signEditLayout;
	private View signSubmit;
	
	private boolean canSave;
	
	private String lastName;
	private String lastSign;
	
	private InputMethodManager imm ;
	
	private View back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
//		getSupportActionBar().setCustomView(R.layout.title_bar_save);
		back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		canSave = false;
		
		View background = findViewById(R.id.background);
		background.setOnClickListener(this);
		
		avatar = (ImageView)findViewById(R.id.avatar);
		nameLayout = findViewById(R.id.name_layout);
		name = (TextView)findViewById(R.id.name);
		nameEdit = (EditText)findViewById(R.id.name_edit);
		nameEditLayout = findViewById(R.id.name_edit_layout);
		nameSubmit = findViewById(R.id.name_submit);
		sexLayout = findViewById(R.id.sex_layout);
		sex = (TextView)findViewById(R.id.sex);
		birthdayLayout = findViewById(R.id.birthday_layout);
		birthday = (TextView)findViewById(R.id.birthday);
		signLayout = findViewById(R.id.sign_layout);
		sign = (TextView)findViewById(R.id.sign);
		signEditLayout = findViewById(R.id.sign_edit_layout);
		signSubmit = findViewById(R.id.sign_submit);
		signEdit = (EditText)findViewById(R.id.sign_edit);
		
		
		UserInfoManager.getUserInfo(getHttpRequest(UserInfoManager.GET_USER_INFO));
		
		avatar.setOnClickListener(this);
		nameLayout.setOnClickListener(this);
		sexLayout.setOnClickListener(this);
		birthdayLayout.setOnClickListener(this);
		signLayout.setOnClickListener(this);
		nameSubmit.setOnClickListener(this);
		signSubmit.setOnClickListener(this);
		background.requestFocus();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.save:
			disableEdit(true);
			save();
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onDestroy() {
		save();
		super.onDestroy();
	}
	
	@Override
	protected void requestComplete(int id,JSONObject jsonObj) {
		super.requestComplete(id,jsonObj);
		switch (id) {
		case UserInfoManager.UPDATE_USER_INFO:
		case UserInfoManager.GET_USER_INFO:
			
			UserInfo userInfo = UserInfoManager.fillUserInfoFromJson(jsonObj);
			if(userInfo!=null){
				if(id == UserInfoManager.UPDATE_USER_INFO){
					Toast.makeText(this, "修改成功",Toast.LENGTH_SHORT).show();
				}
				userSeletedImagePath = userInfo.getAvatar();
				Picasso.with(this)
	        	.load(userInfo.getAvatar())
	        	.placeholder(R.drawable.loading)
	        	.error(R.drawable.error)
	        	.into(avatar);
				
				if(!TextUtils.isEmpty(userInfo.getName()) && !getResources().getString(R.string.name_title).equals(userInfo.getName())){
					name.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
					name.setText(userInfo.getName());
					nameEdit.setText(userInfo.getName());
				}else{
					name.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
				}
				
				if(userInfo.getBirthday()!=null && userInfo.getBirthday().getTime()!=0){
					birthday.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
					birthday.setText(DateFormat.format("yyyy-MM-dd",userInfo.getBirthday()));
				}else{
					birthday.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
				}
				
				if(!TextUtils.isEmpty(userInfo.getSign()) && !getResources().getString(R.string.sign_title).equals(userInfo.getSign())){
					sign.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
					sign.setText(userInfo.getSign());
					signEdit.setText(userInfo.getSign());
				}else{
					sign.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
				}
				
				if(SexUtil.isValid(userInfo.getSex())){
					sex.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
					sex.setText(SexUtil.toString(userInfo.getSex()));
				}else{
					sex.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
				}
				
			}else if(id == UserInfoManager.UPDATE_USER_INFO){
				Toast.makeText(this, "修改失败",Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 禁用编辑态
	 */
	private void disableEdit(boolean isSaveText){
		name.setVisibility(View.VISIBLE);
		nameEditLayout.setVisibility(View.GONE);
		imm.hideSoftInputFromWindow(nameEdit.getWindowToken(), 0);
		if(TextUtils.isEmpty(nameEdit.getText())||getResources().getString(R.string.name_title).equals(nameEdit.getText().toString())){
			if(lastName!=null){
				name.setText(lastName);
				if(getResources().getString(R.string.name_title).equals(nameEdit.getText().toString()))
					name.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
				else
					name.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
				nameEdit.setText(lastName);
				lastName = null;
			}else{
				name.setText(getResources().getString(R.string.name_title));
				name.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
			}
		}else if(!isSaveText && lastName!=null){
			name.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
			name.setText(lastName);
			nameEdit.setText(lastName);
			lastName = null;
		}else{
			name.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
			name.setText(nameEdit.getText());
			lastName = null;
		}
		
		sex.setVisibility(View.VISIBLE);
		if(SexUtil.isValid(sex.getText().toString())){
			sex.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
		}else{
			sex.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
		}
		
		sign.setVisibility(View.VISIBLE);
		signEditLayout.setVisibility(View.GONE);
		imm.hideSoftInputFromWindow(signEdit.getWindowToken(), 0);
		if(TextUtils.isEmpty(signEdit.getText())||getResources().getString(R.string.sign_title).equals(signEdit.getText().toString())){
			if(lastSign!=null){
				sign.setText(lastSign);
				if(getResources().getString(R.string.name_title).equals(signEdit.getText().toString()))
					sign.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
				else
					sign.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
				signEdit.setText(lastSign);
				lastSign = null;
			}else{
				sign.setText(getResources().getString(R.string.sign_title));
				sign.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
			}
			
		}else if(!isSaveText && lastSign!=null){
			sign.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
			sign.setText(lastSign);
			signEdit.setText(lastSign);
			lastSign = null;
		}else{
			sign.setText(signEdit.getText());
			sign.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
			lastSign = null;
		}
	}
	
	private void save(){
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		
		if(!TextUtils.isEmpty(name.getText().toString()) && !getResources().getString(R.string.name_title).equals(name.getText().toString()))
			userInfo.setName(name.getText().toString());
		
		if(!TextUtils.isEmpty(userSeletedImagePath))
				userInfo.setAvatar(userSeletedImagePath);
		
		if(birthdayCalendar!=null && !TextUtils.isEmpty(birthday.getText()) && !getResources().getString(R.string.birthday_title).equals(birthday.getText().toString()))
			userInfo.setBirthday(birthdayCalendar.getTime());
		
		userInfo.setSign(sign.getText().toString());
		
		if(!TextUtils.isEmpty(sex.getText()) &&! getResources().getString(R.string.sex_title).equals(sex.getText().toString()))
			userInfo.setSex(SexUtil.fromString(sex.getText().toString()));
		else
			userInfo.setSex(SexUtil.UNSETTED);
			
		UserInfoManager.updateUserInfo(this,getHttpRequest(UserInfoManager.UPDATE_USER_INFO),userInfo);
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
            userSeletedImagePath = cursor.getString(columnIndex);
            cursor.close();
            
            Picasso.with(this)
        	.load("file:"+userSeletedImagePath)
        	.placeholder(R.drawable.loading)
        	.error(R.drawable.error)
        	.into(avatar);
            
            disableEdit(false);
            save();
        }
    }

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.background:
			disableEdit(false);
			break;
		case R.id.avatar:
			disableEdit(false);
			Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
            break;
		case R.id.name_layout:
			lastName = null;
			disableEdit(false);
//			saveButton.setVisible(true);
			name.setVisibility(View.GONE);
			
			nameEditLayout.setVisibility(View.VISIBLE);
			if(TextUtils.isEmpty(name.getText())||getResources().getString(R.string.name_title).equals(name.getText().toString())){
				nameEdit.setText("");
			}else{
				nameEdit.setText(name.getText());
				lastSign = null;
				lastName = name.getText().toString();
				nameEdit.requestFocus();
				nameEdit.setSelection(0, nameEdit.getText().length());
			}
			nameEdit.requestFocus();
			imm.showSoftInput(nameEdit, 0);
//			imm.showSoftInputFromInputMethod(nameEdit.getWindowToken(), 1);
            break;
		case R.id.sex_layout:
            disableEdit(false);
            SexUtil.selectSex(this, new OnSexSelectListener() {
				@Override
				public void onSexSelected(int sexValue) {
					sex.setText(SexUtil.toString(sexValue));
					if(SexUtil.isValid(sex.getText().toString())){
						sex.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
					}else{
						sex.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
					}
					save();
				}
			});
            
			break;
		case R.id.birthday_layout:
			disableEdit(false);
			showDialog(SELECT_DATE); 
			break;
		case R.id.sign_layout:
			lastSign = null;
			disableEdit(false);
//			saveButton.setVisible(true);
			sign.setVisibility(View.GONE);
			signEditLayout.setVisibility(View.VISIBLE);
			if(TextUtils.isEmpty(sign.getText())||getResources().getString(R.string.sign_title).equals(sign.getText().toString())){
				signEdit.setText("");
			}else{
				signEdit.setText(sign.getText());
				lastSign = sign.getText().toString();
				lastName = null;
				signEdit.requestFocus();
				signEdit.setSelection(0, signEdit.getText().length());
			}
			signEdit.requestFocus();
			imm.showSoftInput(signEdit, 0);
//			imm.showSoftInputFromInputMethod(signEdit.getWindowToken(), 1);
			break;
		case R.id.name_submit:
			if(TextUtils.isEmpty(nameEdit.getText())){
				Toast.makeText(this, "昵称不能为空", Toast.LENGTH_LONG).show();
				break;
			}
		case R.id.sign_submit:
			disableEdit(true);
			save();
			break;
		default:
			break;
		}
		
	}
	
	@Override 
    protected Dialog onCreateDialog(int id) {  
       switch (id) {  
       case SELECT_DATE:  
           return new DatePickerDialog(this, this, 1990, 0, 1);
       }
            
       return null;  
    }

	@Override
	public void onDateSet(DatePicker arg0, int year, int month, int day) {
		birthdayCalendar = Calendar.getInstance();
		birthdayCalendar.clear();
		birthdayCalendar.set(year, month, day);
		birthday.setText(DateFormat.format("yyyy-MM-dd",birthdayCalendar));
		save();
	}

}
