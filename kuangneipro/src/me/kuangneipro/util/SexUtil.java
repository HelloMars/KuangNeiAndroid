package me.kuangneipro.util;

import me.kuangneipro.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SexUtil {
	//默认3 {0:female, 1:male, 2:neutral, 3:未设置})
	public static final int FEMALE = 0;
	public static final int MALE = 1;
	public static final int NEUTRAL = 2;
	public static final int UNSETTED = 3;
	
	
	public static final String FEMALES = "女";
	public static final String MALES = "男";
	public static final String NEUTRALS = "中性";
	public static final String UNSETTEDS = "未设置";
	private static final String[] ALL_SEXS = new String[]{FEMALES,MALES};//,NEUTRALS
	public static String toString(int sex){
		switch (sex) {
		case FEMALE:
			return FEMALES;
		case MALE:
			return MALES;
		case NEUTRAL:
			return NEUTRALS;
		case UNSETTED:
			return UNSETTEDS;
		default:
			return UNSETTEDS;
		}
	}
	
	public static int fromString(String sex){
		if(FEMALES.equals(sex))
			return FEMALE;
		if(MALES.equals(sex))
			return MALE;
		if(NEUTRALS.equals(sex))
			return NEUTRAL;
		if(UNSETTEDS.equals(sex))
			return UNSETTED;
		
		return UNSETTED;
	}
	
	public static boolean isValid(String sex){
		return isValid(fromString(sex));
	}
	
	public static boolean isMale(int sex){
		return sex == MALE;
	}
	
	public static boolean isValid(int sex){
		if(sex<3 && sex>-1)
			return true;
		return false;
	}
	
	public static String[] getAllSex(){
		return ALL_SEXS;
	}
	
	public static void selectSex(Activity activity,OnSexSelectListener onSexSelectListener){
		sOnSexSelectListener = onSexSelectListener;
		LayoutInflater inflater = activity.getLayoutInflater();  
		View sexDialogView = inflater.inflate(R.layout.dialog_sex_select, null);
		ListView sexListView = (ListView)sexDialogView.findViewById(R.id.list_view);
		sexListView.setAdapter(new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item, SexUtil.getAllSex()));
		sexListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(sDialog!=null){
					sDialog.dismiss();
					sDialog = null;
				}
				if(sOnSexSelectListener!=null)
					sOnSexSelectListener.onSexSelected(fromString(SexUtil.getAllSex()[arg2]));
				sOnSexSelectListener = null;
			}
		});
		AlertDialog.Builder facotry = new AlertDialog.Builder(activity);  
		facotry.setView(sexDialogView); 
		sDialog = facotry.create();
		sDialog.show();
	}
	
	private static OnSexSelectListener sOnSexSelectListener;
	private static Dialog sDialog;
	
	public static interface OnSexSelectListener{
		public void onSexSelected(int sex);
	}
	
}
