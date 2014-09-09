/*
 * 该类是带表情功能的文本输入框，通常与EmoticonInputPopupWindow配合使用实现表情的编辑功能
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;

import me.kuangneipro.R;
import me.kuangneipro.emoticon.EmoticonUtil.EmoticonTextWatcher;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.TextView;


/**
 * 该类是带表情功能的文本输入框，通常与EmoticonInputPopupWindow配合使用实现表情的编辑功能
 * @author connorlu
 * @since qqlive_3.6.1
 */
@SuppressLint("NewApi") public class EmoticonEditText extends EditText implements OnLongClickListener {

	private int emoticonWidth;
	private int emoticonHeight;
	private Context context;
	private EmoticonTextWatcher emoticonTextWatcher;

	public EmoticonEditText(Context context) {
		super(context);
		initView(context, null);
	}

	public EmoticonEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public EmoticonEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs);
	}

	private void initView(Context context, AttributeSet attrs) {
		setOnLongClickListener(this);
		if (attrs == null) {
			this.context = context;
			emoticonWidth = EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT;
			emoticonHeight = EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT;
			emoticonTextWatcher = new EmoticonTextWatcher(context, this,
					emoticonWidth, emoticonHeight);
		} else {
			this.context = context;
			TypedArray typedArray = context.obtainStyledAttributes(attrs,
					R.styleable.Emoticon);

			emoticonWidth = typedArray.getInt(
					R.styleable.Emoticon_emoticonWidth,
					EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT);
			emoticonHeight = typedArray.getInt(
					R.styleable.Emoticon_emoticonHeight,
					EmoticonUtil.EmoticonShowStrategy.LINE_HEIGHT);

			typedArray.recycle();
			emoticonTextWatcher = new EmoticonTextWatcher(context, this,
					emoticonWidth, emoticonHeight);
		}
	}


	/**
	 * 与一个EmoticonInputPopupWindow绑定实现点击或获得焦点是触发输入弹框，通常不需要用户调用，
	 * 用户只需调用EmoticonInputPopupWindow的bindEmoticonEditText方法即可实现绑定操作
	 * 
	 * @param emoticonInputPopupWindow
	 *            与本EmoticonEditText绑定的弹框
	 */
	public void bindEmoticonInputMethod(
			final EmoticonPopupable emoticonInputPopupWindow) {
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (!emoticonInputPopupWindow.isShowing())
						emoticonInputPopupWindow.show();
				} else {
					emoticonInputPopupWindow.dismiss();
				}

			}
		});
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction()) {
					emoticonInputPopupWindow
							.show();
				}
				return false;
			}
		});
	}

	/**
	 * 设置文本，请不要使用EditText自带的setText((CharSequence text)方法，该方法不会调用表情过滤函数
	 * 
	 * @param text
	 *            带表情的文本
	 */
	public void setText(String text) {

		if (context == null || text == null)
			return;

		super.setText(text.toString());

		emoticonTextWatcher.parseString2EmoticonSpannableString(context,
				emoticonWidth, emoticonHeight);
	}

	/**
	 * 返回用户编辑的带表情的文本，请不要使用EditText的Editable getText(),该方法返回的Editable未经过过滤
	 * 
	 * @return 经过过滤的用户编辑的带表情的文本
	 */
	public String getParsedString() {
		return toString();
	}

	/**
	 * 返回用户编辑的带表情的文本，请不要使用EditText的Editable getText(),该方法返回的Editable未经过过滤
	 * 
	 * @return 经过过滤的用户编辑的带表情的文本
	 */
	@Override
	public String toString() {
		String str = EmoticonUtil.parseEmoticonStringToQQText(context, super
				.getText().toString());
		return str == null ? "" : str;
	}

	/**
	 * 返回EmoticonEditText的文本管理EmoticonTextWatcher
	 * 
	 * @return 返回EmoticonEditText的文本管理EmoticonTextWatcher
	 */
	public EmoticonTextWatcher getEmoticonTextWatcher() {
		return emoticonTextWatcher;
	}

	/**
	 * 保存剪贴板
	 * 
	 * @param context
	 * @param txt
	 */
	@SuppressWarnings("deprecation")
	public static void setClipTxt(Context context, CharSequence txt) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(txt);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData
					.newPlainText(null, txt);
			clipboard.setPrimaryClip(clip);
		}
	}

	/**
	 * 获取剪贴板内容
	 * 
	 * @return
	 */
	@SuppressWarnings({ "deprecation"})
	private CharSequence getClipTxt() {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext()
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboard.getText();
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext()
					.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = clipboard.getPrimaryClip();
			if (clip.getItemCount() >= 1) {
				return clip.getItemAt(clip.getItemCount() - 1).getText();
			} else {
				return null;
			}
		}
	}


	@Override
	public boolean onLongClick(View v) {
		return false;
	}
	
	void setMaxTextCount(int maxTextCout){
		emoticonTextWatcher.setMaxTextCout(maxTextCout);
	}
	
	void setEditTextTips(TextView editTextTips){
		emoticonTextWatcher.setEditTextTips(editTextTips);
	}

	
	@Override
    public boolean onTextContextMenuItem(int id) {
        int selEnd = this.getSelectionEnd();
        int selStart = this.getSelectionStart();
        int min = Math.min(selStart, selEnd);
        int max = Math.max(selStart, selEnd);

        if (min < 0) {
            min = 0;
        }
        if (max < 0) {
            max = 0;
        }
        if (min != max ) {
			Editable message = getEditableText();
			ImageSpan[] list = message.getSpans(min, max, ImageSpan.class);

			for (ImageSpan span : list) {
				int spanStart = message.getSpanStart(span);
				int spanEnd = message.getSpanEnd(span);
				if(spanStart<min)
					min = spanStart;
				if(spanEnd>max)
					max = spanEnd;
			}
		}
        switch (id) {
	        case android.R.id.paste:
	            Selection.setSelection((Spannable) this.getText(), max);
	            CharSequence paste = getClipTxt();
	
	            if (paste != null) {
	                this.getText().replace(min, max, paste.toString());
	            }
	            return true;
	            
	        case android.R.id.cut:
	            setClipTxt(getContext(),this.getText().subSequence(min, max).toString());
	            this.getText().delete(min, max);
	            return true;
	
	        case android.R.id.copy:
	            super.onTextContextMenuItem(id);
	            setClipTxt(getContext(),this.getText().subSequence(min, max).toString());
	            return true;
        }

        return super.onTextContextMenuItem(id);
    }
}
