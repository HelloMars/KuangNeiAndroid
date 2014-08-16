package me.kuangneipro.entity;

import me.kuangneipro.util.ImageUtil;
import android.graphics.Bitmap;

public class UploadImage {
	
	private String localPath;
	private Bitmap bitmap;
	private String remotePath;
	
	public UploadImage(String localPath){
		this.localPath = localPath;
		this.bitmap = ImageUtil.decodeSampledBitmap(localPath, 70);
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	
}
