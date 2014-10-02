package me.kuangneipro.activity;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import android.app.Activity;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends Activity  {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位

	private MapView mMapView = null;
	private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        mBaiduMap = mMapView.getMap();

        //定义多边形
        List<LatLng> pts = new ArrayList<LatLng>();
        pts.add(new LatLng(39.998559, 116.347083));
        pts.add(new LatLng(39.992465, 116.347370));
        pts.add(new LatLng(39.992617, 116.350802));
        pts.add(new LatLng(39.994496, 116.350712));
        pts.add(new LatLng(39.994565, 116.355024));
        pts.add(new LatLng(39.996016, 116.355042));
        pts.add(new LatLng(39.996058, 116.352041));
        pts.add(new LatLng(39.996666, 116.351969));
        pts.add(new LatLng(39.996680, 116.350065));
        pts.add(new LatLng(39.998656, 116.349939));
        //构建用户绘制多边形的Option对象
        OverlayOptions polygonOption = new PolygonOptions()
            .points(pts)
            .stroke(new Stroke(5, 0xAAFF0000))
            .fillColor(0x10101010);
        //在地图上添加多边形Option，用于显示
        mBaiduMap.addOverlay(polygonOption);

        // 开启定位图层
 		mBaiduMap.setMyLocationEnabled(true);
 		mBaiduMap
		.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, null));
 		
 		// 定位初始化
 		mLocClient = new LocationClient(this);
 		mLocClient.registerLocationListener(myListener);
 		LocationClientOption option = new LocationClientOption();
 		option.setOpenGps(true);// 打开gps
 		option.setCoorType("bd09ll"); // 设置坐标类型
 		option.setScanSpan(1000);
 		mLocClient.setLocOption(option);
 		mLocClient.start();
    }

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
 		mLocClient.stop();
 		// 关闭定位图层
 		mBaiduMap.setMyLocationEnabled(false);
 		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
 		mMapView.onDestroy();
 		mMapView = null;
 		super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
