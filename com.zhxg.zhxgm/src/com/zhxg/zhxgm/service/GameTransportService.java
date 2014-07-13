package com.zhxg.zhxgm.service;


import java.util.HashMap;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.zhxg.zhxgm.control.SqliteController;

public class GameTransportService extends Service {

	private static final int HashMap = 0;
	private LocationClient mLocationClient;
	private BDLocationListener myLocationListener = new MyLocationListener();
	private	SqliteController controller;
	private HashMap<String,String> locationMap;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		controller = new SqliteController(getApplicationContext());
		SQLiteDatabase sqliteDatabase = controller.getWritableDatabase();
		
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myLocationListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
		option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(10000);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(true);//���صĶ�λ���������ַ��Ϣ
		option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
		            return ;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			} 
			
			//insert into sqlite
			insertToSqlite(location);
			
			Toast.makeText(getApplicationContext(), "sevice location", Toast.LENGTH_LONG).show();
			Log.i("transport service: ", location.getLatitude() + " --" + location.getLongitude());
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
		
	}
	
	private void insertToSqlite(BDLocation location){
		locationMap = new HashMap<String, String>();
		locationMap.put("bsid", "1234");
		locationMap.put("xdot", location.getLongitude()+"");
		locationMap.put("ydot", location.getLatitude()+"");
		locationMap.put("to_server", "Y");
		locationMap.put("time", location.getTime());
		controller.insertLocation(locationMap);
		
	}
	
	

}
