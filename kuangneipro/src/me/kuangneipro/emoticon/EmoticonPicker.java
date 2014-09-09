/*
 * 表情选择视图
 * @author connorlu
 * @since qqlive_3.6.1
 */
package me.kuangneipro.emoticon;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.emoticon.EmoticonUtil.ImageInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 表情选择视图
 * @author connorlu
 * @since qqlive_3.6.1
 */
public class EmoticonPicker extends LinearLayout implements OnClickListener,OnPageChangeListener{
	
	
	private static final int DEFAULT_EMOTICONS_COUNT = 27;
	
	private ViewGroup emoticonPickerContainer;
    private ViewPager emoticonViewPager; 
    private EmoticonViewPagerAdapter emoticonViewPagerAdapter; 
    private OnEmoticonPickedListener onEmoticonPickedListener;
    private ArrayList<GridView> emoticonGridPickers; 
    private ImageView[] emoticonSelectPoints;
	private int currentSelectPoint;
    private ViewGroup emoticonPagerSelect;
	private ImageInfo backspaceImageInfo;
	
	private int emoticonsCount;

	public EmoticonPicker(Context context) {
		super(context);
		initView(context, null);
	}

	public EmoticonPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) public EmoticonPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs);
	}
	
	/**
	 * 视图初始化
	 * @param context
	 */
	private void initView(Context context, AttributeSet attrs){
		
		if(attrs==null){
			emoticonsCount = DEFAULT_EMOTICONS_COUNT;
		}else{
			TypedArray typedArray = context.obtainStyledAttributes(attrs,  
	                R.styleable.Emoticon);  
	          
			emoticonsCount = typedArray.getInt(R.styleable.Emoticon_emoticonPickerCount,  
					DEFAULT_EMOTICONS_COUNT);  
	          
	        typedArray.recycle();
		}
		
		LayoutInflater.from(context).inflate(R.layout.emoticon_picker, this); 
		backspaceImageInfo  = new ImageInfo();
		backspaceImageInfo.pattern = null;
		backspaceImageInfo.imageResourceID = R.drawable.emoticon_del_btn;
		backspaceImageInfo.imageType = EmoticonUtil.SMILEY_IMAGE;
		
		emoticonPickerContainer = (ViewGroup) findViewById(R.id.emoticonpickercontainer);
		emoticonViewPager = (ViewPager)findViewById(R.id.emoticonviewpager);
		emoticonPagerSelect = (ViewGroup)findViewById(R.id.emoticonpagerselect);
		
		List<ImageInfo> imageInfos = EmoticonUtil.getPattern2DrawableList(context);
		GridView onePageEmoticonGridPicker = null;
		EmoticonAdapter onePageEmoticonAdapter = null;
		emoticonGridPickers = new ArrayList<GridView>();
		emoticonSelectPoints = new ImageView[imageInfos.size()];
		
		List<ImageInfo> tempImageInfos = new ArrayList<EmoticonUtil.ImageInfo>(emoticonsCount+1);

		for(int i=0;i<imageInfos.size();i++){
			tempImageInfos.add(imageInfos.get(i));
			if(tempImageInfos.size()==emoticonsCount||i==imageInfos.size()-1){
				tempImageInfos.add(backspaceImageInfo);
				onePageEmoticonAdapter = new EmoticonAdapter(context,
						tempImageInfos);
				onePageEmoticonGridPicker =(GridView)LayoutInflater.from(context)
						.inflate(R.layout.emoticon_one_page, null);
				onePageEmoticonGridPicker.setAdapter(onePageEmoticonAdapter);
				emoticonGridPickers.add(onePageEmoticonGridPicker);
				tempImageInfos = new ArrayList<EmoticonUtil.ImageInfo>(emoticonsCount+1); 
			}
		}
		
		for(int i=0; i<emoticonGridPickers.size(); i++) { 
			final int j = i;
            ImageView image = (ImageView)LayoutInflater.from(context)
					.inflate(R.layout.emoticon_point, null);
            image.setEnabled(true);
            image.setImageResource(R.drawable.emoticon_pager_select); 
            image.setTag(j);
            image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					emoticonViewPager.setCurrentItem(j);
				}
			});
            emoticonSelectPoints[j] = image;
            emoticonPagerSelect.addView(image);
        } 
		
		emoticonViewPagerAdapter = new EmoticonViewPagerAdapter(emoticonGridPickers);
		emoticonViewPager.setAdapter(emoticonViewPagerAdapter); 
		emoticonViewPager.setOnPageChangeListener(this);
		currentSelectPoint = 0;
		emoticonSelectPoints[0].setEnabled(false);
	}

	/**
	 * 设置表情选中的监听器
	 * @param onEmoticonPickedListener 监听器
	 */
	public void setOnEmoticonPickedListener(OnEmoticonPickedListener onEmoticonPickedListener) {
		this.onEmoticonPickedListener = onEmoticonPickedListener;
	}
	
	/**
	 * 清除表情选中的监听器
	 */
	public void clearOnEmoticonPickedListener() {
		this.onEmoticonPickedListener = null;
	}


	/**
	 * 隐藏表情选择布局
	 */
	public void hideEmoticonPicker() {
		emoticonPickerContainer.setVisibility(View.GONE);
	}

	/**
	 * 显示表情选择布局
	 */
	public void showEmoticonPicker() {
		emoticonPickerContainer.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 当用户选中某一个表情将触发该监听器
	 * @author connorlu
	 *
	 */
	public interface OnEmoticonPickedListener{
		/**
		 * 用户选中了一个表情
		 * @param imageInfo 该表情的信息，有表情匹配的符号，表情资源ID和表情类型
		 * @param isDeleteButton 该表情是否代表删除操作
		 */
		void onPicked(ImageInfo imageInfo,boolean isDeleteButton);
	}
	
	@Override
	public void onClick(View view) {
		if(onEmoticonPickedListener!=null){
			ImageInfo imageInfo = (ImageInfo)view.getTag();
			if(imageInfo==backspaceImageInfo){
				onEmoticonPickedListener.onPicked(backspaceImageInfo, true);
			}else{
				onEmoticonPickedListener.onPicked(imageInfo, false);
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int index) {
		emoticonSelectPoints[currentSelectPoint].setEnabled(true);
		emoticonSelectPoints[index].setEnabled(false);
		currentSelectPoint = index;
	}
	
	private class EmoticonViewPagerAdapter extends PagerAdapter { 
	     
	    private ArrayList<? extends View> views; 
	     
	    public EmoticonViewPagerAdapter (ArrayList<? extends View> views){ 
	        this.views = views; 
	    } 
	        
	    @Override 
	    public int getCount() { 
	         if (views != null) { 
	             return views.size(); 
	         }       
	         return 0; 
	    } 

	    @Override 
	    public Object instantiateItem(View view, int position) { 
	        
	        ((ViewPager) view).addView(views.get(position), 0); 
	        
	        return views.get(position); 
	    } 
	     
	    @Override 
	    public boolean isViewFromObject(View view, Object arg1) { 
	        return (view == arg1); 
	    } 

	    @Override 
	    public void destroyItem(View view, int position, Object arg2) { 
	        ((ViewPager) view).removeView(views.get(position));        
	    } 
	} 
	
	private class EmoticonAdapter extends BaseAdapter {

		private Context context;

		private List<ImageInfo> list;

		public EmoticonAdapter(Context context, List<ImageInfo> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				
				convertView = LayoutInflater.from(context)
						.inflate(R.layout.emoticon_image, null);
			}

			ImageView imageButton = (ImageView) convertView;
			ImageInfo imageInfo = list.get(position);
			
			imageButton.setImageResource(imageInfo.imageResourceID);
			imageButton.setContentDescription(imageInfo.pattern);
			imageButton.setTag(imageInfo);
			imageButton.setOnClickListener(EmoticonPicker.this);
			return convertView;
		}

	}

	


}
