/*
 * 表情弹出的公共父类，所有表情弹出都继承该父类实现表情弹窗的功能
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;

import me.kuangneipro.emoticon.EmoticonInputView.OnEmoticonMessageSendListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 表情弹出的公共父类，所有表情弹出都继承该父类实现表情弹窗的功能
 * @author connorlu
 * @since qqlive_3.6.1
 */
public abstract class EmoticonPopupable implements OnEmoticonMessageSendListener{
	
	protected View parentView;
	
	/**
	 * 设置用于弹出的父级容器
	 */
	public void setParentView(View parentView){
		this.parentView = parentView;
	}
	
	
	/**
	 * 在父级元素中显示输入弹框
	 */
	public abstract void show();
	
	/**
	 * 隐藏输入弹框
	 */
	public abstract void dismiss();
	
	/**
	 * 判断输入框是否弹出
	 * @return
	 */
	public abstract boolean isShowing();
	
	/**
	 * 绑定输入文本框，该方法将隐藏自带的输入文本框和发送按钮，并使得新绑定的输入文本框在被点击或获得焦点时弹出该弹框
	 * @param emoticonEditText 文本输入框
	 * @param emoticonEditTextTips 文本输入框字数限制提示文本,若为null则不会显示提示文本
	 * @param maxTextCount 文本输入框最大字数限制， <=0代表没有显示
	 */
	public abstract void bindEmoticonEditText(EmoticonEditText emoticonEditText,TextView emoticonEditTextTips,int maxTextCount);
	
	/**
	 * 返回EditText
	 * @return 返回EditText
	 */
	public abstract EmoticonEditText getEmoticonEditText();
	
	/**
	 * 返回发送按钮
	 * @return 返回发送按钮
	 */
	public abstract Button getEmoticonSendButton();
	
	/**
	 * 清空表情输入框
	 */
	public abstract void cleatEmoticonEditText();
	
	/**
	 * 返回整个表情输入视频
	 * @return 返回整个表情输入视频
	 */
	public abstract EmoticonInputView getEmoticonInputView();
	
	/**
	 * 获取输入区域高度
	 * @return
	 */
	public int getInputViewHeight() {
		
		return 0;
	}
}
