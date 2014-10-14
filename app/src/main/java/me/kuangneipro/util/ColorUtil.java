package me.kuangneipro.util;

import me.kuangneipro.R;
import android.content.Context;

public class ColorUtil {
	
	public static int NORMAL_TEXT_COLOR;
	public static int DEFAULT_TEXT_COLOR;
	
	public static void init(Context context){
		NORMAL_TEXT_COLOR = context.getResources().getColor(R.color.normal_text_color);
		DEFAULT_TEXT_COLOR = context.getResources().getColor(R.color.default_text_color);
	}

}
