package me.kuangneipro.entity;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.util.DataStorage;
import me.kuangneipro.util.GeoUtil;
import android.text.TextUtils;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

public class KuangInfo {

	private int id;
	private String name;
	private String area;
	private List<LatLng> position;
	
	private static final String KUANGID_KEY = "kuangId";
	private static final String KUANGNAME_KEY = "kuangName";
	private static final String KUANGAREA_KEY = "kuangArea";
	
	public static void saveSelfKuangInfo(KuangInfo kuangInfo){
		DataStorage.save(KUANGID_KEY, kuangInfo.getId());
		DataStorage.save(KUANGNAME_KEY, kuangInfo.getName());
		DataStorage.save(KUANGAREA_KEY, kuangInfo.getArea());
	}
	
	public static KuangInfo loadSelfKuangInfo(){
		if(!TextUtils.isEmpty(DataStorage.loadString(KUANGNAME_KEY))){
			int id = DataStorage.loadInt(KUANGID_KEY);
			String name = DataStorage.loadString(KUANGNAME_KEY);
			String area = DataStorage.loadString(KUANGAREA_KEY);
			return new KuangInfo(id, name, area, new ArrayList<LatLng>());
		}
		return null;
	}
	
	public KuangInfo(int id, String name, String area, List<LatLng> pts) {
		this.id = id;
		this.name = name;
		this.area = area;
		position = new ArrayList<LatLng>(pts);
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getArea() {
		return area;
	}
	
	public boolean isIn(LatLng point) {
		return GeoUtil.isPointInPolygon(point, position);
	}
	
	public LatLngBounds buildBounds() {
		return GeoUtil.buildBounds(position);
	}
	
	public OverlayOptions buildPolygon(Stroke stroke, int bgcolor) {
		return new PolygonOptions().points(position).stroke(stroke).fillColor(bgcolor);
	}
}
