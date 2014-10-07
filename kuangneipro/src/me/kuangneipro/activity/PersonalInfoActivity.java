package me.kuangneipro.activity;

import java.util.Calendar;

import me.kuangneipro.R;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.manager.UserInfoManager;
import me.kuangneipro.util.ColorUtil;
import me.kuangneipro.util.SexUtil;

import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * 个人信息页面
 * @author connorlu
 *
 */
public class PersonalInfoActivity extends HttpActivity implements OnClickListener, OnDateSetListener, OnItemSelectedListener{
	private static final int RESULT_LOAD_IMAGE = 1;
	private static final int SELECT_DATE = 2;
	private String userSeletedImagePath;
	private Calendar birthdayCalendar;
	
	private ImageView avatar;
	private View nameLayout;
	private TextView name;
	private EditText nameEdit;
	private View sexLayout;
	private TextView sex;
	private Spinner sexEdit;
	private View birthdayLayout;
	private TextView birthday;
	private View signLayout;
	private TextView sign;
	private EditText signEdit;
	
	private boolean canSave;
	
	private MenuItem saveButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
//		getSupportActionBar().setCustomView(R.layout.title_bar_save);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);
		
		
		canSave = false;
		
		View background = findViewById(R.id.background);
		background.setOnClickListener(this);
		
		avatar = (ImageView)findViewById(R.id.avatar);
		nameLayout = findViewById(R.id.name_layout);
		name = (TextView)findViewById(R.id.name);
		nameEdit = (EditText)findViewById(R.id.name_edit);
		sexLayout = findViewById(R.id.sex_layout);
		sex = (TextView)findViewById(R.id.sex);
		sexEdit = (Spinner)findViewById(R.id.sex_edit);
		sexEdit.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, SexUtil.getAllSex()));
		sexEdit.setOnItemSelectedListener(this);
		birthdayLayout = findViewById(R.id.birthday_layout);
		birthday = (TextView)findViewById(R.id.birthday);
		signLayout = findViewById(R.id.sign_layout);
		sign = (TextView)findViewById(R.id.sign);
		signEdit = (EditText)findViewById(R.id.sign_edit);
		
		
		UserInfoManager.getUserInfo(getHttpRequest(UserInfoManager.GET_USER_INFO));
		
		avatar.setOnClickListener(this);
		nameLayout.setOnClickListener(this);
		sexLayout.setOnClickListener(this);
		birthdayLayout.setOnClickListener(this);
		signLayout.setOnClickListener(this);
		background.requestFocus();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save, menu);
		saveButton = menu.findItem(R.id.save);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.save:
			save();
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
	        	.placeholder(R.drawable.avatar)
	        	.error(R.drawable.avatar)
	        	.resize(120, 120)
	        	.centerCrop()
	        	.into(avatar);
				
				if(!TextUtils.isEmpty(userInfo.getName())){
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
				
				if(!TextUtils.isEmpty(userInfo.getSign())){
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
	private void disableEdit(){
		name.setVisibility(View.VISIBLE);
		nameEdit.setVisibility(View.GONE);
		if(TextUtils.isEmpty(nameEdit.getText())||getResources().getString(R.string.name_title).equals(nameEdit.getText().toString())){
			name.setText(getResources().getString(R.string.name_title));
			name.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
		}else{
			name.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
			name.setText(nameEdit.getText());
		}
		
		sex.setVisibility(View.VISIBLE);
		sexEdit.setVisibility(View.GONE);
		if(SexUtil.isValid(sex.getText().toString())){
			sex.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
		}else{
			sex.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
		}
		
		sign.setVisibility(View.VISIBLE);
		signEdit.setVisibility(View.GONE);
		if(TextUtils.isEmpty(signEdit.getText())||getResources().getString(R.string.sign_title).equals(signEdit.getText().toString())){
			sign.setText(getResources().getString(R.string.sign_title));
			sign.setTextColor(ColorUtil.DEFAULT_TEXT_COLOR);
		}else{
			sign.setText(signEdit.getText());
			sign.setTextColor(ColorUtil.NORMAL_TEXT_COLOR);
		}
	}
	
	private void save(){
		disableEdit();
		UserInfo userInfo = UserInfo.loadSelfUserInfo();
		if(!TextUtils.isEmpty(name.getText()) && !getResources().getString(R.string.name_title).equals(name.getText().toString()))
			userInfo.setName(name.getText().toString());
		
		if(!TextUtils.isEmpty(userSeletedImagePath))
				userInfo.setAvatar(userSeletedImagePath);
		
		if(birthdayCalendar!=null && !TextUtils.isEmpty(birthday.getText()) && !getResources().getString(R.string.birthday_title).equals(birthday.getText().toString()))
			userInfo.setBirthday(birthdayCalendar.getTime());
		
		if(!TextUtils.isEmpty(sign.getText()) && !getResources().getString(R.string.sign_title).equals(sign.getText().toString()))
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
        	.placeholder(R.drawable.avatar)
        	.error(R.drawable.avatar)
        	.resize(120, 120)
        	.centerCrop()
        	.into(avatar);
            
            disableEdit();
        }
    }

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.background:
			disableEdit();
			break;
		case R.id.avatar:
			disableEdit();
			Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
            break;
		case R.id.name_layout:
			disableEdit();
			name.setVisibility(View.GONE);
			nameEdit.setVisibility(View.VISIBLE);
			if(TextUtils.isEmpty(name.getText())||getResources().getString(R.string.name_title).equals(name.getText().toString())){
				nameEdit.setText("");
			}else{
				nameEdit.setText(name.getText());
				nameEdit.requestFocus();
				nameEdit.setSelection(0, nameEdit.getText().length());
			}
            break;
		case R.id.sex_layout:
            disableEdit();
			sex.setVisibility(View.GONE);
			sexEdit.setVisibility(View.VISIBLE);
			sexEdit.performClick();
			break;
		case R.id.birthday_layout:
			disableEdit();
			showDialog(SELECT_DATE); 
			break;
		case R.id.sign_layout:
			disableEdit();
			sign.setVisibility(View.GONE);
			signEdit.setVisibility(View.VISIBLE);
			if(TextUtils.isEmpty(sign.getText())||getResources().getString(R.string.sign_title).equals(sign.getText().toString())){
				signEdit.setText("");
			}else{
				signEdit.setText(sign.getText());
				signEdit.requestFocus();
				signEdit.setSelection(0, signEdit.getText().length());
			}
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
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int position,
			long arg3) {
		sex.setText(parent.getItemAtPosition(position).toString());
		disableEdit();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		disableEdit();
	}

}
