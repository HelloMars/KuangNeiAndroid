package me.kuangneipro.entity;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.util.GeoUtil;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

public class KuangInfo {

	private int id;
	private String name;
	private String area;
	private List<LatLng> position;
	
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
}
