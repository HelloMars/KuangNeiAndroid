package me.kuangneipro.activity;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.KuangInfo;
import me.kuangneipro.entity.ReturnInfo;
import me.kuangneipro.entity.UserInfo;
import me.kuangneipro.manager.MapEntityManager;
import me.kuangneipro.manager.UserInfoManager;
import me.kuangneipro.util.GeoUtil;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends HttpActivity {

    private static final String TAG = MapActivity.class.getSimpleName(); // tag 用于测试log用

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean drawable = false;
	int locNum = 0;// 定位级别

	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
    private UiSettings mUiSettings;

	private List<KuangInfo> mKuangs = new ArrayList<KuangInfo>();
	private KuangInfo mKuang = null;
	private KuangInfo mNearKuang = null;
	
	/**
	 * 当前地点击点
	 *//*
	private LatLng currentPt;
	private String touchType;
	private String locInfo = "UNKNOWN";*/

	/**
	 * 用于显示地图状态的面板
	 */
	private TextView mStateBar;
	private TextView mState2Bar;
	
	private MapActivity mActivity = null;

    /**
     * 控制按钮
     */
    private Button wifiButton;
    private Button gpsButton;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_map);
      		
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        // 隐藏缩放/比例尺控件
        /*int childCount = mMapView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                child.setVisibility(View.GONE);
            }
        }*/

        // 删除百度地图logo
        mMapView.removeViewAt(1);
        // 删除缩放控件
        mMapView.removeViewAt(1);
        // 删除比例尺控件
        mMapView.removeViewAt(1);

        mBaiduMap = mMapView.getMap();
        // 隐藏指南针
        mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);

        /*
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
        mBaiduMap.addOverlay(polygonOption);*/

        // 开启定位图层
 		mBaiduMap.setMyLocationEnabled(false);
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

        MapEntityManager.getKuangList(getHttpRequest(MapEntityManager.MAP_KEY_GET));
    }

    @Override
    protected void requestComplete(int id, JSONObject jsonObj) {
        super.requestComplete(id, jsonObj);
        switch (id) {
            case MapEntityManager.MAP_KEY_GET:
                ReturnInfo info = ReturnInfo.fromJSONObject(jsonObj);
                Log.i(TAG, "ReturnInfo:" + info.getReturnMessage() + " " + info.getReturnCode());
                MapEntityManager.fillKuangListFromJson(jsonObj, mKuangs);
                if (mKuangs.isEmpty()) {
                    Toast.makeText(this, "恭喜中奖，获取框们失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case UserInfoManager.REGIGSTER:
    			UserInfo userInfo = UserInfoManager.fillUserInfoFromRegister(jsonObj);
    			if (userInfo != null) {
    				Toast.makeText(this, "注册完成啦:username="+userInfo.getUsername(), Toast.LENGTH_LONG).show();
    			} else {
    				Toast.makeText(this, "恭喜中奖，注册失败", Toast.LENGTH_LONG).show();
    			}
    			break;
            default:
                break;
        }
    }
    
    // open GPS
    private void openGPS() {
        if(!GeoUtil.isGPSEnabled(this)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            Toast.makeText(this, "GPS已经开启", Toast.LENGTH_SHORT).show();
        }
    }

    // open wifi
    private void openWifi(){
        if (!GeoUtil.isWifiEnabled(this)) {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Wifi已经开启", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isIn(LatLng point) {
    	mNearKuang = null;
    	double mindis = 30;
		for (KuangInfo kuang : mKuangs) {
			double curdis = kuang.calDistance(point);
			if (curdis < mindis) {
				mNearKuang = kuang;
				mindis = curdis;
			}
            if (kuang.isIn(point)) {
            	mNearKuang = kuang;
            	KuangInfo.saveSelfKuangInfo(kuang);
                return true;
            }
		}
		return false;
    }

    private void drawKuangs() {
    	for (KuangInfo kuang : mKuangs) {
            mBaiduMap.addOverlay(kuang.buildPolygon(new Stroke(5, 0xAAAA0000), 0x10080808));
        }
    }
    
    private void animate(int num, BDLocation location) {
    	if (locNum == num) {
			locNum ++;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
    }
    
	private void initListener() {
		/*
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
		});*/
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
        wifiButton = (Button) findViewById(R.id.wifibutton);
        gpsButton = (Button) findViewById(R.id.gpsbutton);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.equals(wifiButton)) {
                    openWifi();
                } else if (view.equals(gpsButton)) {
                    openGPS();
                }
                updateMapState();
            }
        };
        wifiButton.setOnClickListener(onClickListener);
        gpsButton.setOnClickListener(onClickListener);

        startButton = (Button) findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	UserInfoManager.regester(getHttpRequest(UserInfoManager.REGIGSTER), mKuang.getId());
            }
        });
        startButton.setVisibility(View.GONE);
	}

	/**
	 * 更新地图状态显示面板
	 */
	private void updateMapState() {
		if (mStateBar == null) {
			return;
		}
		String state = "";/*
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
				ms.zoom, (int) ms.rotate, (int) ms.overlook, locInfo);*/
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
			
			if (UserInfo.loadSelfUserInfo() != null) {
				Intent intent = new Intent(MapActivity.this, PostListActivity.class);
                startActivity(intent);
                MapActivity.this.finish();
                return;
			}
			
			if (mKuangs.isEmpty()) {
				MapEntityManager.getKuangList(getHttpRequest(MapEntityManager.MAP_KEY_GET));
			}

			/*locInfo = String.format("[%d,%s,%d]",
					location.getLocType(),
					location.getNetworkLocationType(),
					location.getSatelliteNumber());*/
			animate(0, location);
			
			boolean isGPSEnabled = GeoUtil.isGPSEnabled(mActivity);
			boolean isWifiEnabled = GeoUtil.isWifiEnabled(mActivity);
            startButton.setVisibility(View.GONE);
			if (!GeoUtil.isOnline(mActivity)) {
				mState2Bar.setText("请连接网络");
				drawable = false;
			} else if (!isGPSEnabled && !isWifiEnabled) {
				mState2Bar.setText("请打开wifi或者GPS");
				drawable = false;
			} else {
				boolean isIn = isIn(new LatLng(location.getLatitude(), location.getLongitude()));
				if (location.getLocType() == 61) { // GPS 定位结果
					if (isIn) {
						mState2Bar.setText("定位认证成功");
						startButton.setVisibility(View.VISIBLE);
					} else {
						mState2Bar.setText("定位不在框内，无法进入(等待GPS调整位置中...)");
					}
					drawable = true;
				} else if (location.getLocType() == 161 && location.getNetworkLocationType().equals("wf")) { // wifi 定位结果
					if (isIn) {
						mState2Bar.setText("定位认证成功");
						startButton.setVisibility(View.VISIBLE);
					} else {
						if (isGPSEnabled) {
							mState2Bar.setText("wifi定位不在框内，请等待更精确的GPS定位(仅室外可用)结果");
						} else {
							mState2Bar.setText("wifi定位不在框内，请打开GPS尝试更精确的定位(仅室外可用)");
						}
					}
					drawable = true;
				} else {
					mState2Bar.setText("非wifi或GPS定位结果，请等待更精确的定位");
					drawable = false;
				}
			}
			
			if (drawable) {
				MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(-1)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
				animate(1, location);
				mBaiduMap.setMyLocationEnabled(true);
				mBaiduMap.setMyLocationData(locData);
				
				// 切换展示的框
				if (mNearKuang != null && mKuang != mNearKuang) {
					mKuang = mNearKuang;
					mBaiduMap.clear();
					// for debug
					drawKuangs();
		            mBaiduMap.addOverlay(mKuang.buildPolygon(new Stroke(5, 0xFF454545), 0x50101010));
		            
		            Button button = new Button(getApplicationContext());
					button.setBackgroundResource(R.drawable.popup);
					button.setTextColor(0xFF505050);
					button.setText(mKuang.getName());
					mBaiduMap.showInfoWindow(new InfoWindow(button, mKuang.buildBounds().getCenter(), 0));
					
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(mKuang.buildBounds());
		            mBaiduMap.animateMapStatus(u);
				}
			} else {
				mKuang = null;
				mBaiduMap.clear();
				// for debug
				drawKuangs();
				mBaiduMap.setMyLocationEnabled(false);
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
