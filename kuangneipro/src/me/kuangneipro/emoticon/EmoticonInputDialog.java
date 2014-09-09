/*
 * 实现表情输入的弹框默认情况下自带文本编辑框和发送按钮，暂时不支持外部的文本编辑框绑定使用,一般自带文本编辑框和发送按钮使用EmoticonInputDialog效果更好，不然无法实现表情文字的粘贴
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;


import me.kuangneipro.R;
import me.kuangneipro.emoticon.EmoticonInputView.OnEmoticonMessageSendListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * 实现表情输入的弹框默认情况下自带文本编辑框和发送按钮，暂时不支持外部的文本编辑框绑定使用,一般自带文本编辑框和发送按钮使用EmoticonInputDialog效果更好，不然无法实现表情文字的粘贴
 * @author connorlu
 * @since qqlive_3.6.1
 */
public class EmoticonInputDialog extends EmoticonPopupable {
	
	private Context mContext;
	private Dialog emoticonInputDialog;
	private EmoticonRelativeLayout emoticonRelativeLayout;
	private EmoticonInputView emoticonInputView;
	private OnEmoticonMessageSendListener onSendListener;
	
	public EmoticonInputDialog(Context context) {
		this(context, null);
	}
	
	public EmoticonInputDialog(Context context,OnEmoticonMessageSendListener onSendListener) {
		this.mContext = context;
		emoticonInputDialog = new Dialog(context,R.style.EmoticonInputDialog);
		View container = LayoutInflater.from(context).inflate(R.layout.emoticon_input_dialog, null);
		emoticonInputDialog.setContentView(container);
		emoticonRelativeLayout = (EmoticonRelativeLayout) container.findViewById(R.id.root_layout);
		emoticonInputView = (EmoticonInputView)container.findViewById(R.id.emoticon_input_view);
		emoticonInputView.setLazyShowEnable(true);
		emoticonRelativeLayout.setEmoticonInputPopupView(this);
		Window window = emoticonInputDialog.getWindow();
		emoticonInputDialog.setCanceledOnTouchOutside(true);
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		window.setAttributes(layoutParams);
        emoticonInputView.setOnEmoticonMessageSendListener(this);
        this.onSendListener = onSendListener;
	}
	
	/**
	 * 显示输入弹框
	 * @param view 父视图
	 */
	public void show(){
		if(!emoticonInputDialog.isShowing()){
			emoticonInputView.setEmoticonToggleButton(false);
			emoticonInputDialog.show();
	        emoticonInputDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					emoticonInputView.hideInputMethod();
				}
			});

		}

	}
	
	@Override
	public int getInputViewHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		(((Activity) mContext).getWindowManager()).getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels-emoticonInputView.getTop();
	}
	
	/**
	 * 隐藏输入弹框
	 */
	public void dismiss() {
		if (emoticonInputDialog.isShowing()) {
			emoticonInputDialog.dismiss();
		}
	}
	
	/**
	 * 判断输入框是否弹出
	 * @return
	 */
	public boolean isShowing() {
//		Log.e("isShowing", ""+emoticonInputPopupWindow.isShowing());
		return emoticonInputDialog.isShowing();
	}
	
	/**
	 * 绑定输入文本框，暂时不支持该方法,InputDialog暂时无法较好的实现绑定外部的EmoticonEditText，请使用EmoticonInputPupupView
	 * @param emoticonEditText 文本输入框
	 * @param emoticonEditTextTips 文本输入框字数限制提示文本,若为null则不会显示提示文本
	 * @param maxTextCount 文本输入框最大字数限制， <=0代表没有显示
	 */
	public void bindEmoticonEditText(EmoticonEditText emoticonEditText,TextView emoticonEditTextTips,int maxTextCount){
		throw new IllegalStateException("InputDialog暂时无法较好的实现绑定外部的EmoticonEditText，请使用EmoticonInputPupupView");
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
