/*
 * 表情输入的布局视图
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;


import me.kuangneipro.R;
import me.kuangneipro.emoticon.EmoticonPicker.OnEmoticonPickedListener;
import me.kuangneipro.emoticon.EmoticonUtil.ImageInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * 表情输入的布局视图
 * @author connorlu
 * @since qqlive_3.6.1
 */
public class EmoticonInputView extends LinearLayout implements OnClickListener,
		OnCheckedChangeListener,OnTouchListener,OnEmoticonPickedListener{
	private final int MAX_TEXT_COUNT = -1;
	
	private EmoticonPicker emoticonPicker;
	private EmoticonEditText emoticonEditText;
	private TextView emoticonEditTextTip;
	private int maxTextCount;
	private Button emoticonSendButton;
	private ToggleButton emoticonToggleButton;
	private OnEmoticonMessageSendListener listener;
	private InputMethodManager inputMethodManager;
	private boolean lazyShowEnable;
	private boolean needShow;
	Handler handler = new Handler();
	private final int checkShowDelay = 500;

	public EmoticonInputView(Context context) {
		super(context);
		initView(context);
	}

	public EmoticonInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) public EmoticonInputView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs,defStyle);
		initView(context);
	}
	
	private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.emoticon_input_view, this);
		inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		emoticonPicker = (EmoticonPicker) findViewById(R.id.emoticonpicker);
		emoticonEditText = (EmoticonEditText) findViewById(R.id.emoticon_edittext);
		emoticonEditTextTip = (TextView) findViewById(R.id.emoticon_edittext_tips);
		this.emoticonEditText.setEditTextTips(this.emoticonEditTextTip);
		maxTextCount = MAX_TEXT_COUNT;
		emoticonSendButton = (Button) findViewById(R.id.emoticon_sendbutton);
		emoticonToggleButton = (ToggleButton) findViewById(R.id.emoticon_togglebutton);
		emoticonSendButton.setOnClickListener(this);
		emoticonToggleButton.setOnCheckedChangeListener(this);
		emoticonEditText.setOnTouchListener(this);
		emoticonPicker.setOnEmoticonPickedListener(this);
		emoticonPicker.hideEmoticonPicker();
		lazyShowEnable = false;
		needShow = false;
	}
	
	
	/**
	 * 设置最大字数限制，超过最大字数会出现提示选项
	 * @param maxTextCount 最大字数，<=0代表没有限制
	 */
	public void setMaxTextCount(int maxTextCount) {
		this.maxTextCount = maxTextCount;
		this.emoticonEditText.setMaxTextCount(this.maxTextCount);
	}

	/**
	 * 获取最大字数限制，超过最大字数会出现提示选项
	 * @return 获取最大字数限制，超过最大字数会出现提示选项
	 */
	public int getMaxTextCount() {
		return maxTextCount;
	}

	/**
	 * 启动延迟显示，使得弹框
	 * @param lazyShowEnable
	 */
	public void setLazyShowEnable(boolean lazyShowEnable){
		this.lazyShowEnable = lazyShowEnable;
	}

	/**
	 * 返回EditText
	 * @return 返回EditText
	 */
	public EmoticonEditText getEmoticonEditText(){
		return emoticonEditText;
	}
	
	/**
	 * 返回发送按钮
	 * @return 返回发送按钮
	 */
	public Button getEmoticonSendButton(){
		return emoticonSendButton;
	}
	
	/**
	 * 显示输入法框
	 */
	public void showInputMethod(){
		emoticonEditText.post(new Runnable() {
			@Override
			public void run() {
				emoticonEditText.setFocusable(true);  
				emoticonEditText.setFocusableInTouchMode(true);
				emoticonEditText.requestFocus();
				inputMethodManager.showSoftInput(emoticonEditText, 0);  
			}
		});
		
	}
	
	/**
	 * 隐藏输入法框
	 */
	public void hideInputMethod(){
		inputMethodManager.hideSoftInputFromWindow(emoticonEditText.getWindowToken(), 0);
	}

	/**
	 * 这是输入文本框文字
	 * @param text 带表情的文字
	 */
	public void setEmoticonEditText(String text) {
		emoticonEditText.setText(text);
	}

	/**
	 * 设置用户点击发送按钮之后的事件监听器，该监听将直接返回用户编辑的文本
	 * @param listener 
	 */
	public void setOnEmoticonMessageSendListener(
			OnEmoticonMessageSendListener listener) {
		this.listener = listener;
	}

	/**
	 * 清空文本输入框文字
	 */
	public void clearEmoticonEditText(){
		emoticonEditText.setText("");
	}
	
	/**
	 * 绑定输入文本框，该方法将隐藏自带的输入文本框和发送按钮，并使得新绑定的输入文本框在被点击或获得焦点时弹出该弹框
	 * @param emoticonEditText 文本输入框
	 * @param emoticonEditTextTips 文本输入框字数限制提示文本,若为null则不会显示提示文本
	 * @param maxTextCount 文本输入框最大字数限制， <=0代表没有显示
	 */
	public void bindEmoticonEditText(EmoticonEditText emoticonEditText,TextView emoticonEditTextTips,int maxTextCount){
		if(this.emoticonEditText!=emoticonEditText&&emoticonEditText!=null){
			this.emoticonEditText.setVisibility(View.GONE);
			this.emoticonSendButton.setVisibility(View.GONE);
			this.emoticonEditText = emoticonEditText;
		}
		if(this.emoticonEditTextTip!=null){
			this.emoticonEditTextTip.setVisibility(View.GONE);
			this.emoticonEditTextTip = emoticonEditTextTips;
		}
		this.maxTextCount = maxTextCount;
		this.emoticonEditText.setEditTextTips(this.emoticonEditTextTip);
		this.emoticonEditText.setMaxTextCount(this.maxTextCount);
	}

	/**
	 * 设置表情/输入法转换按钮
	 * @param isChecked true:显示表情,false:显示输入法
	 */
	public void setEmoticonToggleButton(boolean isChecked) {
		if(emoticonToggleButton.isChecked()==isChecked){
			if (isChecked) {
				hideInputMethod();
				if(lazyShowEnable){
					postEmoticonPickShow();
				}else{
					emoticonPicker.showEmoticonPicker();
				}
			} else {
				showInputMethod();
				emoticonPicker.hideEmoticonPicker();
				}
			}else{
				emoticonToggleButton.setChecked(isChecked);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == emoticonToggleButton) {
			if (isChecked) {
				hideInputMethod();
				if(lazyShowEnable){
					postEmoticonPickShow();
				}else{
					emoticonPicker.showEmoticonPicker();
				}
				
			} else {
				showInputMethod();
				emoticonPicker.hideEmoticonPicker();
			}
		}
	}
	
	@Override
	public void onPicked(ImageInfo imageInfo, boolean isDeleteButton) {
		if(isDeleteButton){
			int index = emoticonEditText.getSelectionStart();  
			if(index>0){
				Editable editable = emoticonEditText.getText();  
				editable.delete(index-1, index);
			}
		}else{
			emoticonEditText.getEmoticonTextWatcher().insert(imageInfo.pattern,imageInfo.imageType);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == emoticonSendButton) {
			if (listener != null)
				listener.onSend(view,emoticonEditText.toString());
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if(v==emoticonEditText&&event.getAction() == MotionEvent.ACTION_UP&&emoticonToggleButton.isChecked()){
			emoticonToggleButton.setChecked(false);
		}
		
		return false;
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.e("onKeyDown", "onKeyDown");
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		Log.e("onSizeChanged", "onSizeChanged");
//		super.onSizeChanged(w, h, oldw, oldh);
//	}
//	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Log.e("onMeasure", "onMeasure:"+widthMeasureSpec+" "+heightMeasureSpec);
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(lazyShowEnable&&needShow){
			handler.post(new Runnable() {
			    @Override
			    public void run() {
					needShow = false;
					emoticonPicker.showEmoticonPicker();
			    }
			});
			
		}
	}
	

	/**
	 * 用户点击发送按钮的监听事件
	 * @author connorlu
	 *
	 */
	public interface OnEmoticonMessageSendListener {
		/**
		 * 发送时间
		 * @param v 触发发送事件的对象
		 * @param text 用户发送的文字
		 */
		public void onSend(View v,String text);
	}

	private void postEmoticonPickShow(){
		needShow = true;
		checkShowDelay();
	}

	private void checkShowDelay(){
		handler.postDelayed(checkShowRunnable, checkShowDelay);
	}
	
	private Runnable checkShowRunnable = new Runnable() {
		@Override
		public void run() {
			if(needShow){
				needShow = false;
				emoticonPicker.showEmoticonPicker();
			}
		}
	};
	


}
