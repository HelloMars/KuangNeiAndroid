/*
 * 实现表情输入的弹框默认情况下自带文本编辑框和发送按钮，也可与某个外部的文本编辑框绑定使用
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;

import me.kuangneipro.emoticon.EmoticonInputView.OnEmoticonMessageSendListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.PaintDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

/**
 * 实现表情输入的弹框默认情况下自带文本编辑框和发送按钮，也可与某个外部的文本编辑框绑定使用
 * @author connorlu
 * @since qqlive_3.6.1
 */
public class EmoticonInputPopupWindow extends EmoticonPopupable {
	
	private PopupWindow emoticonInputPopupWindow;
	private EmoticonInputView emoticonInputView;
	private OnEmoticonMessageSendListener onSendListener;
	private boolean isBindNewEmoticonEditText;
	
	public EmoticonInputPopupWindow(Context context) {
		this(context, null);
	}
	
	public EmoticonInputPopupWindow(Context context,OnEmoticonMessageSendListener onSendListener) {
		emoticonInputPopupWindow = new PopupWindow(context);
		emoticonInputView = new EmoticonInputView(context);
		emoticonInputPopupWindow.setContentView(emoticonInputView);
		emoticonInputPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		emoticonInputPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);  
		emoticonInputPopupWindow.setBackgroundDrawable(new PaintDrawable(Color.WHITE));
        emoticonInputPopupWindow.setOutsideTouchable(true);  
        emoticonInputView.setOnEmoticonMessageSendListener(this);
        this.onSendListener = onSendListener;
        isBindNewEmoticonEditText = false;
	}
	
	/**
	 * 显示输入弹框
	 * @param view 父视图
	 */
	public void show(){
		if(!emoticonInputPopupWindow.isShowing()){
			emoticonInputPopupWindow.setFocusable(!isBindNewEmoticonEditText);
	        emoticonInputPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
	        emoticonInputPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	        emoticonInputPopupWindow.showAtLocation(parentView.getRootView(), Gravity.BOTTOM, 0, 0);
	        emoticonInputPopupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					emoticonInputView.hideInputMethod();
				}
			});
	        emoticonInputPopupWindow.setTouchInterceptor(new OnTouchListener() {
	
	            @Override
	            public boolean onTouch(View v, MotionEvent event) {
	                int x = (int) event.getRawX();   
	                int y = (int) event.getRawY(); 
	            	Rect editTextRect = new Rect();
	            	emoticonInputView.getEmoticonEditText().getGlobalVisibleRect(editTextRect);
	            	int rx = (int) event.getX();   
	                int ry = (int) event.getY(); 
	            	Rect popupWindowRect = new Rect();
	            	v.getGlobalVisibleRect(popupWindowRect);
	            	
//            		Log.e("点击的坐标是文本编辑框坐标则不隐藏弹窗", rect.left+"~"+rect.right+":"+x+"    "+rect.top+"~"+rect.bottom+":"+y);
//	            	Log.e("点击的坐标是文本编辑框坐标则不隐藏弹窗", popupWindowRect.left+"~"+popupWindowRect.right+":"+rx+"    "+popupWindowRect.top+"~"+popupWindowRect.bottom+":"+ry);
	                if(x>=editTextRect.left&&x<=editTextRect.right&&y>=editTextRect.top&&y<=editTextRect.bottom&&!(rx>=popupWindowRect.left&&rx<=popupWindowRect.right&&ry>=popupWindowRect.top&&ry<=popupWindowRect.bottom)){
	            	   return true;
	                }
	                return false;
	            }
	        });
		}
		emoticonInputView.setEmoticonToggleButton(false);

	}
	
	/**
	 * 隐藏输入弹框
	 */
	public void dismiss() {
		if (emoticonInputPopupWindow.isShowing()) {
			emoticonInputPopupWindow.dismiss();
		}
	}
	
	/**
	 * 判断输入框是否弹出
	 * @return
	 */
	public boolean isShowing() {
		return emoticonInputPopupWindow.isShowing();
	}
	
	/**
	 * 绑定输入文本框，该方法将隐藏自带的输入文本框和发送按钮，并使得新绑定的输入文本框在被点击或获得焦点时弹出该弹框
	 * @param emoticonEditText 文本输入框
	 * @param emoticonEditTextTips 文本输入框字数限制提示文本,若为null则不会显示提示文本
	 * @param maxTextCount 文本输入框最大字数限制， <=0代表没有显示
	 */
	public void bindEmoticonEditText(EmoticonEditText emoticonEditText,TextView emoticonEditTextTips,int maxTextCount){
		emoticonEditText.bindEmoticonInputMethod(this);
		emoticonInputView.bindEmoticonEditText(emoticonEditText,emoticonEditTextTips,maxTextCount);
		isBindNewEmoticonEditText = true;
	}
	
	/**
	 * 返回EditText
	 * @return 返回EditText
	 */
	public EmoticonEditText getEmoticonEditText(){
		return emoticonInputView.getEmoticonEditText();
	}
	
	/**
	 * 返回发送按钮
	 * @return 返回发送按钮
	 */
	public Button getEmoticonSendButton(){
		return emoticonInputView.getEmoticonSendButton();
	}
	
	/**
	 * 清空表情输入框
	 */
	public void cleatEmoticonEditText(){
		emoticonInputView.clearEmoticonEditText();
	}
	
	/**
	 * 返回整个表情输入视频
	 * @return 返回整个表情输入视频
	 */
	public EmoticonInputView getEmoticonInputView(){
		return emoticonInputView;
	}

	/**
	 * 点击发送按钮时调用
	 */
	@Override
	public void onSend(View view,String text) {
		dismiss();
		if(onSendListener!=null){
			onSendListener.onSend(view,text);
		}
	}
	
}
