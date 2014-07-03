package com.zhxg.zhxgm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends Activity {

	MapView mMapView = null;  
	BaiduMap mBaiduMap = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map);
		
		mMapView = (MapView) findViewById(R.id.bmapView);  
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
		
		
		LatLng pt1 = new LatLng(39.93923, 116.357428);  
		LatLng pt2 = new LatLng(39.91923, 116.327428);  
		LatLng pt3 = new LatLng(39.89923, 116.347428);  
		LatLng pt4 = new LatLng(39.89923, 116.367428);  
		LatLng pt5 = new LatLng(39.91923, 116.387428);  
		List<LatLng> pts = new ArrayList<LatLng>();  
		pts.add(pt1);  
		pts.add(pt2);  
		pts.add(pt3);  
		pts.add(pt4);  
		pts.add(pt5);  
		//构建用户绘制多边形的Option对象
		PolylineOptions polylineOption = new PolylineOptions()
				.points(pts).color(0xAAFF0000);
		
		//在地图上添加多边形Option，用于显示  
		mBaiduMap.addOverlay(polylineOption);
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}
	
	
	
	
	

}
