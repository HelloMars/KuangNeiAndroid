package me.kuangneipro.Adapter;

import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.entity.UploadImage;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PostingImageAdapter extends BaseAdapter{

	private List<UploadImage> uploadInfos;
	private Activity activity;
	
	public PostingImageAdapter(Activity activity,List<UploadImage> uploadInfos){
		this.activity = activity;
		this.uploadInfos = uploadInfos;
	}
	
	@Override
	public int getCount() {
		return uploadInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return uploadInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.item_image_view, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.image.setImageBitmap(uploadInfos.get(position).getBitmap());
		
		return convertView;
	}
	
	private static final class ViewHolder {
		public ImageView image;
	}

}
