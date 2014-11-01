package me.kuangneipro.activity;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.util.GeoUtil;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapDoubleClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
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
	
	private List<List<LatLng>> mPolygons = new ArrayList<List<LatLng>>();
	
	/**
	 * 当前地点击点
	 */
	private LatLng currentPt;
	private String touchType;
	private String locInfo = "UNKNOWN";

	/**
	 * 用于显示地图状态的面板
	 */
	private TextView mStateBar;
	private TextView mState2Bar;
	
	private MapActivity mActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        
        setContentView(R.layout.activity_map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        mBaiduMap = mMapView.getMap();

        // zhumeng
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
        mPolygons.add(new ArrayList<LatLng>(pts));
        //构建用户绘制多边形的Option对象
        OverlayOptions polygonOption = new PolygonOptions()
            .points(pts)
            .stroke(new Stroke(5, 0xAAFF0000))
            .fillColor(0x10101010);
        //在地图上添加多边形Option，用于显示
        mBaiduMap.addOverlay(polygonOption);
        
        // yangze_hebei
        pts.clear();
        pts.add(new LatLng(39.438210,118.893864));
        pts.add(new LatLng(39.439429,118.893721));
        pts.add(new LatLng(39.440704,118.893595));
        pts.add(new LatLng(39.440961,118.892733));
        pts.add(new LatLng(39.440871,118.891511));
        pts.add(new LatLng(39.440843,118.890271));
        pts.add(new LatLng(39.439742,118.890361));
        pts.add(new LatLng(39.439101,118.890361));
        pts.add(new LatLng(39.437994,118.890496));
        pts.add(new LatLng(39.438071,118.891484));
        mPolygons.add(new ArrayList<LatLng>(pts));
        polygonOption = new PolygonOptions()
	        .points(pts)
	        .stroke(new Stroke(5, 0xAAFF0000))
	        .fillColor(0x10101010);
        mBaiduMap.addOverlay(polygonOption);
        
        // yangze
        pts.clear();
        pts.add(new LatLng(39.986279,116.423740));
        pts.add(new LatLng(39.987080,116.423767));
        pts.add(new LatLng(39.988110,116.423749));
        pts.add(new LatLng(39.989571,116.423655));
        pts.add(new LatLng(39.989571,116.422370));
        pts.add(new LatLng(39.989509,116.421068));
        pts.add(new LatLng(39.988072,116.421023));
        pts.add(new LatLng(39.986448,116.421014));
        pts.add(new LatLng(39.986275,116.422388));
        mPolygons.add(new ArrayList<LatLng>(pts));
        polygonOption = new PolygonOptions()
	        .points(pts)
	        .stroke(new Stroke(5, 0xAAFF0000))
	        .fillColor(0x10101010);
        mBaiduMap.addOverlay(polygonOption);
        
        // zhangjijian
        pts.clear();
        pts.add(new LatLng(31.291121,120.751078));
        pts.add(new LatLng(31.291098,120.751563));
        pts.add(new LatLng(31.290974,120.751581));
        pts.add(new LatLng(31.290986,120.751064));
        mPolygons.add(new ArrayList<LatLng>(pts));
        polygonOption = new PolygonOptions()
	        .points(pts)
	        .stroke(new Stroke(5, 0xAAFF0000))
	        .fillColor(0x10101010);
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
 		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
 		
 		mLocClient.setLocOption(option);
 		mLocClient.start();
 		
 		mStateBar = (TextView) findViewById(R.id.state);
 		mState2Bar = (TextView) findViewById(R.id.state2);
		initListener();
		
		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(16);
		mBaiduMap.animateMapStatus(u);
		
		boolean isGPSEnabled = GeoUtil.isGPSEnabled(mActivity);
		boolean isWifiEnabled = GeoUtil.isWifiEnabled(mActivity);
		if (!GeoUtil.isOnline(mActivity)) {
			mState2Bar.setText("请连接网络");
		} else if (!isGPSEnabled && !isWifiEnabled) {
			mState2Bar.setText("请打开wifi或者GPS");
		} else {
			if (isGPSEnabled && !isWifiEnabled) {
				mState2Bar.setText("GPS定位中(仅室外可用)...");
			} else if (!isGPSEnabled && isWifiEnabled) {
				mState2Bar.setText("wifi定位中...");
			} else {
				mState2Bar.setText("定位中...");
			}
		}
    }
    
    private boolean isIn(LatLng point) {
    	boolean isIn = false;
		for (List<LatLng> pts : mPolygons) {
			isIn = (isIn || GeoUtil.isPointInPolygon(point, pts));
		}
		return isIn;
    }
    
	private void initListener() {
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				boolean isIn = isIn(point);
				touchType = "单击" + (isIn ? "In":"Out");
				currentPt = point;
				updateMapState();
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng point) {
				boolean isIn = isIn(point);
				touchType = "长按" + (isIn ? "In":"Out");
				currentPt = point;
				updateMapState();
			}
		});
		mBaiduMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
			public void onMapDoubleClick(LatLng point) {
				boolean isIn = isIn(point);
				touchType = "双击" + (isIn ? "In":"Out");
				currentPt = point;
				updateMapState();
			}
		});
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			public void onMapStatusChangeStart(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChangeFinish(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChange(MapStatus status) {
				updateMapState();
			}
		});
	}

	/**
	 * 更新地图状态显示面板
	 */
	private void updateMapState() {
		if (mStateBar == null) {
			return;
		}
		String state = "";
		if (currentPt == null) {
			state = "点击、长按、双击地图以获取经纬度和地图状态";
		} else {
			state = String.format(touchType + ",当前经度： %f 当前纬度：%f",
					currentPt.longitude, currentPt.latitude);
		}
		state += "\n";
		MapStatus ms = mBaiduMap.getMapStatus();
		state += String.format(
				"zoom=%.1f rotate=%d overlook=%d, %s",
				ms.zoom, (int) ms.rotate, (int) ms.overlook, locInfo);
		mStateBar.setText(state);
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

			boolean isGPSEnabled = GeoUtil.isGPSEnabled(mActivity);
			boolean isWifiEnabled = GeoUtil.isWifiEnabled(mActivity);
			if (!GeoUtil.isOnline(mActivity)) {
				mState2Bar.setText("请连接网络");
			} else if (!isGPSEnabled && !isWifiEnabled) {
				mState2Bar.setText("请打开wifi或者GPS");
			} else {
				boolean isIn = isIn(new LatLng(location.getLatitude(), location.getLongitude()));
				if (location.getLocType() == 61) { // GPS 定位结果
					if (isIn) {
						mState2Bar.setText("定位认证成功");
					} else {
						mState2Bar.setText("定位不在框内，无法进入(等待GPS调整位置中...)");
					}
				} else if (location.getLocType() == 161 && location.getNetworkLocationType().equals("wf")) { // wifi 定位结果
					if (isIn) {
						mState2Bar.setText("定位认证成功");
					} else {
						if (isGPSEnabled) {
							mState2Bar.setText("wifi定位不在框内，请等待更精确的GPS定位(仅室外可用)结果");
						} else {
							mState2Bar.setText("wifi定位不在框内，请打开GPS尝试更精确的定位(仅室外可用)");
						}
					}
				} else {
					mState2Bar.setText("非wifi或GPS定位结果，请稍等...");
				}
			}
			
			locInfo = String.format("[%d,%s,%d]",
					location.getLocType(),
					location.getNetworkLocationType(),
					location.getSatelliteNumber());
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(-1)
					.latitude(location.getLatitude())
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
