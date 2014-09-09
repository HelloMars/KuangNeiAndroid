/*
 * 来类用于显示带表情符号的文本
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;

import me.kuangneipro.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 来类用于显示带表情符号的文本
 * @author connorlu
 * @since qqlive_3.6.1
 */
public class EmoticonTextView extends TextView {

	private int emoticonWidth;
	private int emoticonHeight;
	private Context context;
	
	public EmoticonTextView(Context context) {
		super(context);
		initView(context,null); 
	}

	public EmoticonTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context,attrs); 
	}

	public EmoticonTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context,attrs);
	}
	
	private void initView(Context context,AttributeSet attrs){
		if(attrs==null){
			this.context = context;
			emoticonWidth = EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT;
			emoticonHeight = EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT;
		}else{
			this.context = context;
			TypedArray typedArray = context.obtainStyledAttributes(attrs,  
	                R.styleable.Emoticon);  
	          
	        emoticonWidth = typedArray.getInt(R.styleable.Emoticon_emoticonWidth,  
	                EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT);  
	        emoticonHeight = typedArray.getInt(R.styleable.Emoticon_emoticonHeight,
	        		EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT);  
	          
	          
	        typedArray.recycle();
		}
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		
		if ( context == null || text == null){
			super.setText(null,type);
			return;
		}
		
		super.setText(EmoticonUtil.parseString2EmoticonSpannableString(context, this, text, emoticonWidth, emoticonHeight), type);
	
	}
	
	

}
