/*
 * 实现表情输入的弹框默认情况下自带文本编辑框和发送按钮，也可与某个外部的文本编辑框绑定使用
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;

import me.kuangneipro.emoticon.EmoticonInputView.OnEmoticonMessageSendListener;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 实现表情输入的弹框默认情况下自带文本编辑框和发送按钮，也可与某个外部的文本编辑框绑定使用
 * @author connorlu
 * @since qqlive_3.6.1
 */
public class EmoticonInputPopupView extends EmoticonPopupable {
	
	private boolean shown;
	private EmoticonInputView emoticonInputView;
	private OnEmoticonMessageSendListener onSendListener;
	
	public EmoticonInputPopupView(Context context) {
		this(context, null);
	}
	
	public EmoticonInputPopupView(Context context,OnEmoticonMessageSendListener onSendListener) {
		emoticonInputView = new EmoticonInputView(context);
		emoticonInputView.setLazyShowEnable(true);
        emoticonInputView.setOnEmoticonMessageSendListener(this);
        this.onSendListener = onSendListener;
        shown = false;
	}
	
	/**
	 * 显示输入弹框
	 * @param view 输入法弹框的父视图该view应该仅用于放置输入法
	 */
	public void show(){
		if(parentView instanceof ViewGroup){
			ViewGroup container = (ViewGroup)parentView;
			boolean hasEmoticonInputView = false;
			for(int i=0 ;i<container.getChildCount();i++){
				if(container.getChildAt(i)==emoticonInputView){
					hasEmoticonInputView = true;
					break;
				}
			}
			if(!hasEmoticonInputView)
				container.addView(emoticonInputView);
		}
		
		emoticonInputView.setEmoticonToggleButton(false);
		emoticonInputView.setVisibility(View.VISIBLE);
		shown = true;
	}
	
	/**
	 * 隐藏输入弹框
	 */
	public void dismiss() {
		emoticonInputView.hideInputMethod();
//		emoticonInputView.clearEmoticonEditText();
		emoticonInputView.setVisibility(View.GONE);
		shown = false;
	}
	
	/**
	 * 判断输入框是否弹出
	 * @return
	 */
	public boolean isShowing() {
//		Log.e("isShowing", ""+emoticonInputPopupWindow.isShowing());
		return shown;
	}
	
	/**
	 * 绑定输入文本框，暂时不支持该方法,InputDialog暂时无法较好的实现绑定外部的EmoticonEditText，请使用EmoticonInputPupupView
	 * @param emoticonEditTextTips 文本输入框字数限制提示文本,若为null则不会显示提示文本
	 * @param maxTextCount 文本输入框最大字数限制， <=0代表没有显示
	 */
	public void bindEmoticonEditText(EmoticonEditText emoticonEditText,TextView emoticonEditTextTips,int maxTextCount){
		emoticonEditText.bindEmoticonInputMethod(this);
		emoticonInputView.bindEmoticonEditText(emoticonEditText,emoticonEditTextTips,maxTextCount);
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
