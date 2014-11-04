package me.kuangneipro.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;


public class GeoUtil
{
	
	//if wifi or mobile is connected, return true
	public static boolean isOnline(Activity activity) 
	{
	    ConnectivityManager connMgr = (ConnectivityManager) 
	    		activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnected());
	}  
	
	//if wifi is open
	public static boolean isWifiEnabled(Activity activity)
	{
		WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null && wifiManager.isWifiEnabled()) 
			return true;
		return false;
	}
	
	//if GPS is open
	public static boolean isGPSEnabled(Activity activity)
	{
		LocationManager locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
    	if(locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			return true;
		return false;
	}
		
	public static LatLngBounds buildBounds(List<LatLng> points) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LatLng point : points) {
			builder.include(point);
		}
		return builder.build();
	}

	// point to segment distance
	public static double pointToSegment(LatLng p, LatLng p1, LatLng p2) {
		double ans = 0;
		double a, b, c;
		a = DistanceUtil.getDistance(p1, p2);
		b = DistanceUtil.getDistance(p1, p);
		c = DistanceUtil.getDistance(p2, p);
		if (c+b==a) {//点在线段上
		  ans = 0;
		  return ans;
		}
		if (a<=0.00001) {//不是线段，是一个点
		  ans = b;
		  return ans;
		}
		if (c*c >= a*a + b*b) { //组成直角三角形或钝角三角形，p1为直角或钝角
		  ans = b;
		  return ans;
		}
		if (b * b >= a * a + c * c) {// 组成直角三角形或钝角三角形，p2为直角或钝角
		  ans = c;
		  return ans;
		}
		// 组成锐角三角形，则求三角形的高
		double p0 = (a + b + c) / 2;// 半周长
		double s = Math.sqrt(p0 * (p0 - a) * (p0 - b) * (p0 - c));// 海伦公式求面积
		ans = 2*s / a;// 返回点到线的距离（利用三角形面积公式求高）
		return ans;
	}
	
	public static boolean isPointInPolygon(LatLng p, List<LatLng> pts)
	{
		if(pts == null || pts.size() < 3)
			return false;
		
		// 先快速判断是否在bound内
		if (!buildBounds(pts).contains(p))
			return false;
		
		boolean oddNodes=false;
		int j = pts.size() - 1;
		for (int i = 0; i < pts.size(); ++i) {
			double lat = p.latitude;
			double lng = p.longitude;
			double lati = pts.get(i).latitude;
			double lngi = pts.get(i).longitude;
			double latj = pts.get(j).latitude;
			double lngj = pts.get(j).longitude;
			
			if ((lngi < lng && lngj >= lng) || (lngj < lng && lngi >= lng)) {
				double ux = ((lng-lngi) / (lngj-lngi)) * (latj-lati);
				if(lati + ux < lat) {
					oddNodes = !oddNodes; 
				}
			}
			j = i;
		}
		return oddNodes;
	}
}