/*
 * 用于作为EmoticonInputPopupView的root view元素，监听点击事件,并在点击输入框外部时隐藏输入框
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
/**
 * 用于作为EmoticonInputPopupView的root view元素，监听点击事件,并在点击输入框外部时隐藏输入框
 * @author connorlu
 * @since qqlive_3.6.1
 */
public class EmoticonRelativeLayout extends RelativeLayout{
	
	private EmoticonPopupable emoticonPopupable;
	private EmoticonInputView emoticonInputView;
	private EmoticonEditText emoticonEditText;
	int[] editTextPosition = new int[2];
	int[] inputViewPosition = new int[2];
	private Rect editTextRect = new Rect();
	private Rect inputViewRect = new Rect();
	private int x = 0;
	private int y = 0;

	public EmoticonRelativeLayout(Context context) {
		super(context);
	}

	public EmoticonRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmoticonRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event){
		if(event.getAction()==MotionEvent.ACTION_DOWN&&emoticonPopupable!=null&&emoticonInputView!=null&&emoticonEditText!=null){
			x = (int) event.getRawX();   
	        y = (int) event.getRawY(); 
	        emoticonEditText.getLocalVisibleRect(editTextRect);
	        emoticonEditText.getLocationOnScreen(editTextPosition);
			emoticonInputView.getLocalVisibleRect(inputViewRect);
			emoticonInputView.getLocationOnScreen(inputViewPosition); 
//			Log.e("editTextRect", ""+editTextPosition[0]+","+(editTextPosition[0]+editTextRect.right)+","+editTextPosition[1]+","+(editTextPosition[1]+editTextRect.bottom));
//			Log.e("inputViewRect",""+inputViewPosition[0]+","+(inputViewPosition[0]+inputViewRect.right)+","+inputViewPosition[1]+","+(inputViewPosition[1]+inputViewRect.bottom));
//			Log.e("event", "x:"+x+",y:"+y);
			if(!(editTextPosition[0]<=x&&x<=(editTextPosition[0]+editTextRect.right)&&editTextPosition[1]<=y&&y<=(editTextPosition[1]+editTextRect.bottom))
					&&!(inputViewPosition[0]<=x&&x<=(inputViewPosition[0]+inputViewRect.right)&&inputViewPosition[1]<=y&&y<=(inputViewPosition[1]+inputViewRect.bottom))
					&&emoticonPopupable.isShowing()){
//				Log.e("inininin", "ininin");
				emoticonPopupable.dismiss();
			}
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 设置布局相关的EmoticonInputPopuView
	 * @param emoticonPopupable
	 */
	public void setEmoticonInputPopupView(
			EmoticonPopupable emoticonPopupable) {
		this.emoticonPopupable = emoticonPopupable;
		this.emoticonInputView = emoticonPopupable.getEmoticonInputView();
		this.emoticonEditText = emoticonPopupable.getEmoticonEditText();
	}
	
	
}
