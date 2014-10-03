package me.kuangneipro.util;

import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;


public class GeoUtil
{
	public static LatLngBounds buildBounds(List<LatLng> points) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LatLng point : points) {
			builder.include(point);
		}
		return builder.build();
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