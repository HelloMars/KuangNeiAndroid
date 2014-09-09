/*
 * 该类用于处理表情字符串的转义，如在EmoticonTextView中将字符串转化为代表情的SpannableString，并对EmoticonEditView中发送的字符进行处理
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import me.kuangneipro.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
/**
 * 该类用于处理表情字符串的转义，如在EmoticonTextView中将字符串转化为代表情的SpannableString，并对EmoticonEditView中发送的字符进行处理
 * @author connorlu
 * @since qqlive_3.6.1
 */
public class EmoticonUtil {
	
	/**
	 * 表情占位符宽度和高度的展示策略
	 * @author connorlu
	 *
	 */
	public static final class EmoticonShowStrategy{
		/**
		 * 设置这一项表情的宽度或高度将与文本行高一直
		 */
		public static final int LINE_HEIGHT = -1;
		/**
		 * 设置这一项表情的宽度或高度将为图片本身的大小
		 */
		public static final int IMAGE_SIZE = 0;
	}

	/**
	 * 存储表情匹配字符串和表情名称
	 * @author connorlu
	 *
	 */
	public static class ImageInfo{
		/**
		 * 用于匹配的字符串
		 */
		public String pattern;
		/**
		 * 表情的resourceID
		 */
		public int imageResourceID;
		/**
		 * 表情的类型目前有SMILEY_IMAGE和EMOJI_IMAGE两种
		 */
		public int imageType;
	}
	
	public static final char ESCAPE_CHAR_2 = '/';
	public static final char ESCAPE_CHAR_2_PATTEN =  0x0003;
	public static final char ESCAPE_CHAR = '[';
	public static final char ESCAPE_CHAR_PATTEN = ']';
	public static final String SMILEY_PREFIX = "smiley_";
	public static final String EMOJI_PREFIX = "emoji_";
	public static final int SMILEY_MAX_PATTERN_SIZE_ONE = 5;
	public static final int SMILEY_MAX_PATTERN_SIZE_TWO = 5;
	public static final int SMILEY_MAX_PATTERN_SIZE_THREE = 10;
	public static final int EMOJI_MAX_PATTERN_SIZE = 4;
	private static volatile boolean isNotInited = true;
	private static final String UNICODEPREFIX = "\\u";
	private static final int SMILEYMAPCAPACITY = 420;
	private static final int EMOJIMAPCAPACITY = 628;

	private static final Map<String, Integer> smiley_pattern2DrawableMap_one = new HashMap<String, Integer>(SMILEYMAPCAPACITY);
	private static final Map<String, Integer> smiley_pattern2DrawableMap_two = new HashMap<String, Integer>(SMILEYMAPCAPACITY);
	private static final Map<String, Integer> smiley_pattern2DrawableMap_three = new HashMap<String, Integer>(SMILEYMAPCAPACITY);
	private static final Map<String, Integer> emoji_pattern2DrawableMap = new HashMap<String, Integer>(EMOJIMAPCAPACITY);
	private static final List<ImageInfo> pattern2DrawableList = new ArrayList<ImageInfo>();
	private static String[] smileys_pattern_one;
	private static String[] smileys_pattern_two;
	private static String[] smileys_pattern_three;
	
	public static final int SMILEY_IMAGE = 0;
	public static final int EMOJI_IMAGE = 2;

	
	/**
	 * 检查表情资源初始化是否完成
	 */
	private static void checkInit(Context context){
		
		if(isNotInited){
			synchronized (EmoticonUtil.class) {
				if(isNotInited){
					doInit(context);
					isNotInited = false;
				}
			}
		}
		
	}

	/**
	 * 对表情资源进行初始化
	 */
	private static void doInit(Context context) {
			
		Resources res = context.getResources();
		smileys_pattern_one = res.getStringArray(R.array.smileys_pattern_one);
		smileys_pattern_two = res.getStringArray(R.array.smileys_pattern_two);
		smileys_pattern_three = res.getStringArray(R.array.smileys_pattern_three);
		ImageInfo imageInfo = null;
		String imageID = null;
		int imageResourceID = -1;
		for(int i =0;i<smileys_pattern_one.length;i++){
			imageID = SMILEY_PREFIX+i;
			imageResourceID = getResId(imageID, R.drawable.class);
			smiley_pattern2DrawableMap_one.put(smileys_pattern_one[i], imageResourceID);
			smiley_pattern2DrawableMap_two.put(smileys_pattern_two[i], imageResourceID);
			smiley_pattern2DrawableMap_three.put(smileys_pattern_three[i], imageResourceID);
			imageInfo = new ImageInfo();
			imageInfo.pattern = smileys_pattern_one[i];
			imageInfo.imageResourceID = imageResourceID;
			imageInfo.imageType = SMILEY_IMAGE;
			pattern2DrawableList.add(imageInfo);
		}
		
		String[] emojis_pattern = res.getStringArray(R.array.emojis_pattern);
		for(int i=0;i<emojis_pattern.length;i++){
			imageID = EMOJI_PREFIX+i;
			imageResourceID = getResId(imageID, R.drawable.class);
			emoji_pattern2DrawableMap.put(convertUTF_16ToStr(emojis_pattern[i]), imageResourceID);
		}
		
		String[] emojis_show = res.getStringArray(R.array.emojis_show);
		for(int i=0;i<emojis_show.length;i++){
			imageInfo = new ImageInfo();
			imageInfo.pattern = convertUTF_16ToStr(emojis_show[i]);
//			Log.e("pattern"+i, emojis_show[i]+"  "+emoji_pattern2DrawableMap.get(convertUTF_16ToStr(emojis_show[i])));
			imageInfo.imageResourceID = emoji_pattern2DrawableMap.get(convertUTF_16ToStr(emojis_show[i]));
			imageInfo.imageType = EMOJI_IMAGE;
			pattern2DrawableList.add(imageInfo);
		}
	}
	
	public static List<ImageInfo> getPattern2DrawableList(Context context){
		checkInit(context);
		return pattern2DrawableList;
	}
	
	/**
	 * 将字符串型的UTF16转化为String
	 * @param uTF16Str 使用UTF16表示的字符串
	 * @return 转义后的字符串
	 */
	private static String convertUTF_16ToStr(String uTF16Str) {  
	    if (uTF16Str == null || uTF16Str.length() == 0||!uTF16Str.contains(UNICODEPREFIX)) {  
	        return "";  
	    }  
	    String tempStr;  
	    String tempHexStr;  
	    StringBuffer sb = new StringBuffer();  
	    while (uTF16Str.contains(UNICODEPREFIX)) {  
	        // 获取第一次出现\\u的index  
	        int firstIndex = uTF16Str.indexOf(UNICODEPREFIX);  
	        // 获取第二次出现\\u的index  
	        int secondIndex = uTF16Str.indexOf(UNICODEPREFIX, firstIndex + 2);  
	        // 将第一出现与第二次出现中间的部分，截取下来  
	        if (secondIndex == -1) {  
	            tempStr = uTF16Str.substring(firstIndex);  
	        } else {  
	            tempStr = uTF16Str.substring(firstIndex, secondIndex);  
	        }  
	        tempHexStr = tempStr.substring(tempStr.indexOf(UNICODEPREFIX) + 2);  
	        if (tempHexStr.length() == 4) {  
	            sb.append((char) Integer.parseInt(tempHexStr, 16));  
	        }  
	        // 将第二次出现以后的部分截取下来  
	        if (secondIndex == -1) {  
	            uTF16Str = "";  
	        } else {  
	            uTF16Str = uTF16Str.substring(secondIndex);  
	        }  
	    }  
	    return sb.toString();  
	}  

	/**
	 * 给指定的TextView这设置带表情文字。
	 * @param context 上下文
	 * @param textView 被设置表情字符串的textView
	 * @param str 带表情编码的字符串
	 */
	public static void setEmoticonTextView(Context context, TextView textView,
			String str) {
		setEmoticonTextView(context, textView,str,EmoticonShowStrategy.LINE_HEIGHT,EmoticonShowStrategy.LINE_HEIGHT);
	}
	

	/**
	 * 给指定的TextView这设置带表情文字。
	 * @param context 上下文
	 * @param textView 被设置表情字符串的textView
	 * @param str 带表情编码的字符串
	 * @param emoticonWidth 指定表情的宽度，该宽度可为EmoticonShowStrategy.LINE_HEIGHT,EmoticonShowStrategy.IMAGE_SIZE或指定的像素值
	 * @param emoticonHeight 指定表情的高度，该高度可为EmoticonShowStrategy.LINE_HEIGHT,EmoticonShowStrategy.IMAGE_SIZE或指定的像素值
	 */
	public static void setEmoticonTextView(Context context, TextView textView,
			String str,int emoticonWidth,int emoticonHeight) {
		
		if (textView == null || context == null || str == null)
			return;
		
		checkInit(context);

		textView.setText(parseString2EmoticonSpannableString(context,textView,str, emoticonWidth, emoticonHeight));

	}
	
	
	/**
	 * 匹配[XXX]类型的表情模式
	 * @param str 表情字符串
	 * @param i 表情字符串"["的位置
	 * @return 返回一个成功匹配的最小字符串长度-1为匹配失败
	 */
	private static int patternStrategySmiley_One(String str,int i){
		
		int index = -1;
		int temp = i;

		for(i++;i<str.length()&&i-temp<SMILEY_MAX_PATTERN_SIZE_ONE;i++){
			if(str.charAt(i) == ESCAPE_CHAR_PATTEN&&smiley_pattern2DrawableMap_one.containsKey(str.substring(temp, i+1))){
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	/**
	 * 匹配/XXX类型的表情模式
	 * @param str 表情字符串
	 * @param i 表情字符串"/"的位置
	 * @return 返回一个成功匹配的最小字符串长度-1为匹配失败
	 */
	private static int patternStrategySmiley_Two(String str,int i){
		
		int index = -1;
		int temp = i;
		
		for(i++;i<str.length()&&i-temp<SMILEY_MAX_PATTERN_SIZE_TWO;i++){
			if(smiley_pattern2DrawableMap_two.containsKey(str.substring(temp, i+1))){
				index = i;
				break;
			}
		}
		
		if(index==-1){
			i = temp;
			for(i++,i++;i<str.length()&&i-temp<SMILEY_MAX_PATTERN_SIZE_THREE;i++){
				if(smiley_pattern2DrawableMap_three.containsKey(str.substring(temp, i+1))){
					index = i;
					break;
				}
			}
		}
		
		return index;
	}
	
	
	/**
	 * 匹配emoji类型的表情模式
	 * @param str 表情字符串
	 * @param i 表情可能的起始位置
	 * @return 返回一个成功匹配的最小字符串长度-1为匹配失败
	 */
	private static int patternStrategyEmoji(String str,int i){
		
		int index = -1;
		int temp = i;
		
		if(i<0)
			return index;
		
		for(;i<str.length()&&i-temp<EMOJI_MAX_PATTERN_SIZE;i++){
			if(emoji_pattern2DrawableMap.containsKey(str.substring(temp, i+1))){
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	/**
	 * 在指定的字符串位置创建表情占位符
	 * @param context 上下文
	 * @param textView 设置表情占位符的文本框
	 * @param str 设置表情占位符的表情文本
	 * @param width 表情符的宽度
	 * @param height 表情符的该读
	 * @return 创建的表情符
	 */
	private static ImageSpan createImageSpan(Context context,TextView textView,String str,int width,int height,int image_type){
		Drawable drawable = null;
		
		switch (image_type) {
			case SMILEY_IMAGE:
				Integer rId = smiley_pattern2DrawableMap_one.get(str);
				if(rId == null)
					rId = smiley_pattern2DrawableMap_two.get(str);
				if(rId == null)
					rId = smiley_pattern2DrawableMap_three.get(str);
				drawable = context.getResources().getDrawable(rId);
				break;
	
			case EMOJI_IMAGE:
				drawable = context.getResources().getDrawable(emoji_pattern2DrawableMap.get(str));
				break;
		}
		
		if(width == EmoticonShowStrategy.LINE_HEIGHT)
			width = textView.getLineHeight();
		else if(width == EmoticonShowStrategy.IMAGE_SIZE)
			width = drawable.getIntrinsicWidth();
		else if(width<0)
			drawable.getIntrinsicWidth();
		
		if(height == EmoticonShowStrategy.LINE_HEIGHT)
			height = textView.getLineHeight();
		else if(height == EmoticonShowStrategy.IMAGE_SIZE)
			height = drawable.getIntrinsicHeight();
		else if(height<0)
			drawable.getIntrinsicWidth();
		
        drawable.setBounds(0, 0, width, height);
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        
        return span;
	}
	
	/**
	 * 在指定的Textiew中插入表情符
	 * @param context 上下文
	 * @param textView 设置表情占位符的文本框
	 * @param ss 设置表情占位符的表情文本
	 * @param str 携带表情占位符的文本
	 * @param i 表情字符串的开始位置
	 * @param patternEnd 表情字符串的结束位置
	 * @param width 表情的宽度
	 * @param height 表情的高度
	 * @param image_type 生成图片的类型
	 */
	private static void insertImageSpan(Context context,TextView textView,SpannableString ss,String str,int i,int patternEnd,int width,int height,int image_type){

		ss.setSpan(createImageSpan(context, textView, str.substring(i, patternEnd+1), width, height,image_type), i, patternEnd+1, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
	
	}
	
	/**
	 * 在指定的EditText中插入表情符
	 * @param context 上下文
	 * @param editText 设置表情占位符的文本框
	 * @param str 携带表情占位符的文本
	 * @param i 表情字符串的开始位置
	 * @param patternEnd 表情字符串的结束位置
	 * @param width 表情的宽度
	 * @param height 表情的高度
	 * @param image_type 生成图片的类型
	 */
	private static void insertImageSpan2Editable(Context context,EditText editText,String str,int i,int patternEnd,int width,int height,int image_type){
		Editable message = editText.getEditableText();

		message.setSpan(createImageSpan(context, editText, str.substring(i, patternEnd+1), width, height,image_type), i, patternEnd+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	
	}

	/**
	 * 将普通文本转义为带表情占位符的文本
	 * @param context 上下文
	 * @param textView 设置表情占位符的文本框
	 * @param str 待转义的普通文本
	 * @param emoticonWidth 表情符的宽度
	 * @param emoticonHeight 表情符的高度
	 * @return 带表情占位符的文本
	 */
	public static SpannableString parseString2EmoticonSpannableString(Context context,
			TextView textView,CharSequence charSequence,int emoticonWidth,int emoticonHeight) {

//		long nanoStart = System.nanoTime();
		
		if (TextUtils.isEmpty(charSequence))
			return null;

		checkInit(context);
		
		SpannableString ss = new SpannableString(charSequence);
		
		String str = charSequence.toString();

		for (int i = 0, patternEnd = -1; i < str.length(); i++) {

			if (str.charAt(i) == ESCAPE_CHAR&&(patternEnd = patternStrategySmiley_One(str,i))!=-1) {
				insertImageSpan(context,textView, ss, str, i, patternEnd,emoticonWidth,emoticonHeight,SMILEY_IMAGE);
	            i = patternEnd;
			}else if (str.charAt(i) == ESCAPE_CHAR_2&&(patternEnd = patternStrategySmiley_Two(str,i))!=-1) {
				insertImageSpan(context,textView, ss, str, i, patternEnd,emoticonWidth,emoticonHeight,SMILEY_IMAGE);
				i = patternEnd;
			}else if((patternEnd = patternStrategyEmoji(str,i))!=-1){
				insertImageSpan(context,textView, ss, str, i, patternEnd,emoticonWidth,emoticonHeight,EMOJI_IMAGE);
				i = patternEnd;
			}

		}
		
//		long nanoEnd = System.nanoTime();
//		Log.e("ParseTime", (nanoEnd - nanoStart)+"");
		return ss;
	}
	
	/**
	 * 对用户编辑的带表情文本进行过滤,生成更加标准的带表情文本
	 * @param str 用户编辑的带表情文本
	 * @return 更加标准的带表情文本
	 */
	public static String parseEmoticonStringToQQText(Context context,String str) {

		if (TextUtils.isEmpty(str))
			return null;
		
		checkInit(context);

		StringBuilder sb = new StringBuilder(str);

		List<Integer> insertPositionStartList = new ArrayList<Integer>();
		List<Integer> insertPositionEndList = new ArrayList<Integer>();
		List<Integer> insertSmileysIndexList = new ArrayList<Integer>();

		for (int i = 0, patternEnd = -1; i < str.length(); i++) {

			// 
			if (str.charAt(i) == ESCAPE_CHAR_2&&(patternEnd = patternStrategySmiley_Two(str,i))!=-1) {
				if((patternEnd+1<str.length()&&str.charAt(patternEnd+1)!=EmoticonUtil.ESCAPE_CHAR_2_PATTEN)||patternEnd+1==str.length())
				{
					insertPositionEndList.add(patternEnd);
					insertSmileysIndexList.add(getPatternIndex(str.substring(i, patternEnd+1)));
					insertPositionStartList.add(i);
				}
				i = patternEnd;
			}

		}
		
		for(int position=insertPositionEndList.size()-1;position>-1;position--){
			sb.replace(insertPositionStartList.get(position), insertPositionEndList.get(position)+1,smileys_pattern_one[insertSmileysIndexList.get(position)]);
		}

		return sb.toString();
	}
	
	private static int getPatternIndex(String pattern){
		for(int i=0;i<smileys_pattern_two.length;i++){
			if(pattern.equals(smileys_pattern_two[i])){
				return i;
			}
		}
		
		for(int i=0;i<smileys_pattern_three.length;i++){
			if(pattern.equals(smileys_pattern_three[i])){
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * 通过资源文件的名称找到资源文件的标识符
	 * @param variableName 资源文件的名称
	 * @param clazz 资源文件的种类
	 * @return 资源文件的标识符
	 */
	public static int getResId(String variableName, Class<?> clazz) {
	    try {
	        Field idField = clazz.getDeclaredField(variableName);
	        return idField.getInt(idField);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    } 
	}

	/**
	 * 该类用于监控一个EditText，并合理完成表情的插入和删除操作
	 * @author connorlu
	 *
	 */
	public static class EmoticonTextWatcher implements TextWatcher {

		private final Context context;
		private final EditText editText;
		private TextView editTextTips;
		private int maxTextCout;
		private final List<ImageSpan> emoticonsToRemove = new CopyOnWriteArrayList<ImageSpan>();
		private final int emoticonWidth;
		private final int emoticonHeight;

		public EmoticonTextWatcher(Context context, EditText editText) {
			this(context, editText,
					EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT,
					EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT);
		}

		public EmoticonTextWatcher(Context context, EditText editText,
				int emoticonWidth, int emoticonHeight) {
			this.context = context;
			this.editText = editText;
			this.editText.addTextChangedListener(this);
			this.emoticonWidth = emoticonWidth;
			this.emoticonHeight = emoticonHeight;
		}
		
		/**
		 * 设置EditText的文本
		 * @param context 上下文
		 * @param emoticonWidth 表情的宽度
		 * @param emoticonHeight 表情的高度
		 * @return 带表情占位符的文本
		 */
		public SpannableString parseString2EmoticonSpannableString(Context context,int emoticonWidth,int emoticonHeight) {
			return parseString2EmoticonSpannableString(context,emoticonWidth,emoticonHeight,0 ,editText.getText().length());
		}
		
		/**
		 * 设置EditText的文本
		 * @param context 上下文
		 * @param emoticonWidth 表情的宽度
		 * @param emoticonHeight 表情的高度
		 * @param start转化的开始
		 * @param end转化的结束
		 * @return 带表情占位符的文本
		 */
		private SpannableString parseString2EmoticonSpannableString(Context context,int emoticonWidth,int emoticonHeight,int start ,int end) {
			
			String str = editText.getText().toString();
			
			if (TextUtils.isEmpty(str))
				return null;
			
			checkInit(context);

			SpannableString ss = new SpannableString(str);

			for (int i = start, patternEnd = -1; i < end; i++) {

				if (str.charAt(i) == ESCAPE_CHAR&&(patternEnd = patternStrategySmiley_One(str,i))!=-1) {
					// 匹配模式[XXXXX]
					insertImageSpan2Editable(context,editText, str, i, patternEnd,emoticonWidth,emoticonHeight,SMILEY_IMAGE);
		            i = patternEnd;
				}else if (str.charAt(i) == ESCAPE_CHAR_2&&(patternEnd = patternStrategySmiley_Two(str,i))!=-1) {
					// 匹配模式/XXXXX
					insertImageSpan2Editable(context,editText, str, i, patternEnd,emoticonWidth,emoticonHeight,SMILEY_IMAGE);
					i = patternEnd;
				}else if((patternEnd = patternStrategyEmoji(str,i))!=-1){
					insertImageSpan2Editable(context,editText, str, i, patternEnd,emoticonWidth,emoticonHeight,EMOJI_IMAGE);
					i = patternEnd;
				}

			}
			return ss;
		}

		/**
		 * 在光标位置插入表情符
		 * @param emoticon 表情符名称
		 */
		public void insert(String emoticon,int imageType) {

			int start = editText.getSelectionStart();
			int end = editText.getSelectionEnd();
			Editable message = editText.getEditableText();

			checkInit(context);
			
			message.replace(start, end, emoticon);
			message.setSpan(EmoticonUtil.createImageSpan(context, editText,
					emoticon, emoticonWidth, emoticonHeight,imageType), start, start
					+ emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		}

		@Override
		public void beforeTextChanged(CharSequence text, int start, int count,
				int after) {
			
			// Check if some text will be removed.
			if (count > 0) {
				int end = start + count;
				Editable message = editText.getEditableText();
				ImageSpan[] list = message
						.getSpans(start, end, ImageSpan.class);

				for (ImageSpan span : list) {
					// Get only the emoticons that are inside of the changed
					// region.
					int spanStart = message.getSpanStart(span);
					int spanEnd = message.getSpanEnd(span);
					if ((spanStart < end) && (spanEnd > start)) {
						// Add to remove list
						emoticonsToRemove.add(span);
					}
				}
			}
			
			
		}

		@Override
		public void afterTextChanged(Editable text) {
			Editable message = editText.getEditableText();

			// Commit the emoticons to be removed.
			for (ImageSpan span : emoticonsToRemove) {
				int start = message.getSpanStart(span);
				int end = message.getSpanEnd(span);

				// Remove the span
				message.removeSpan(span);

				// Remove the remaining emoticon text.
				if (start != end) {
					message.delete(start, end);
				}
			}
			emoticonsToRemove.clear();
			
			
			if(maxTextCout>0 && editTextTips!=null)
				if(text.length() > maxTextCout){
					editTextTips.setText("-" + (text.length()-maxTextCout));
					editTextTips.setVisibility(View.VISIBLE);
				}else{
					editTextTips.setVisibility(View.GONE);
				}
//			int start = editText.getSelectionStart();
//			int end = editText.getSelectionEnd();
//			
//			ImageSpan[] list = message
//					.getSpans(start, end, ImageSpan.class);
//
//			for (ImageSpan span : list) {
//				int spanStart = message.getSpanStart(span);
//				int spanEnd = message.getSpanEnd(span);
//				if ((spanStart < start) && (spanEnd > start)) {
//					start = spanEnd;
//				}
//				if ((spanStart < end) && (spanEnd > end)) {
//					end = spanEnd;
//				}
//			}
//			
//			editText.setSelection(start, end);
			
		}

		@Override
		public void onTextChanged(CharSequence text, int start, int before,
				int count) {
			parseString2EmoticonSpannableString(context , emoticonWidth ,emoticonHeight,start,start+count);
		}

		TextView getEditTextTips() {
			return editTextTips;
		}

		void setEditTextTips(TextView editTextTips) {
			this.editTextTips = editTextTips;
		}

		int getMaxTextCout() {
			return maxTextCout;
		}

		void setMaxTextCout(int maxTextCout) {
			this.maxTextCout = maxTextCout;
		}

	}
	
}
