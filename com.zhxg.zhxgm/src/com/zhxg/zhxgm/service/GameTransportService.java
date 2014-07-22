package com.zhxg.zhxgm.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.zhxg.zhxgm.control.SqliteController;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.utils.GpsUtils;
import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.Trace;

public class GameTransportService extends Service {

	private static final int HashMap = 0;
	private LocationClient mLocationClient;
	private BDLocationListener myLocationListener = new MyLocationListener();
	private	SqliteController controller;
	private HashMap<String,String> locationMap;
	private String bsid = "";

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
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(10000);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(intent != null){
			bsid = intent.getStringExtra(Const.BSID);
		}
		
		controller.clearGameLocation(bsid);
		new getTransportLocationTask().execute(bsid);
		return super.onStartCommand(intent, flags, startId);
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
			new TransportLocationInsertTask().execute(location);
			
			Toast.makeText(getApplicationContext(), "sevice location", Toast.LENGTH_LONG).show();
			Log.i("transport service: ", location.getLatitude() + " --" + location.getLongitude());
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
		
	}
	
	
	class getTransportLocationTask extends AsyncTask<String, Integer, Boolean>{
		
		private JSONObject result;
		private boolean resultCode = false;
		
		
		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String... arg0) {
			
			result = new GameFunction().getTransportLocation(arg0[0]);
			try {
				if("TRUE".equals(result.getString("flag").toUpperCase())){
					resultCode = true;
				}else{
					resultCode = false;
				}
			} catch (JSONException e) {
				resultCode = false;
			}
			return resultCode;
		}
		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			ArrayList<BDLocation> tempLocation = new ArrayList<BDLocation>();
			if (success) {
				try {
					tempLocation = new GameFunction().transportConvert(result.getJSONArray("msg"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				
			}
			
			for (BDLocation location : tempLocation){
				HashMap locationMap = new HashMap<String, String>();
				locationMap.put("bsid", bsid);
				locationMap.put("xdot", location.getLongitude()+"");
				locationMap.put("ydot", location.getLatitude()+"");
				locationMap.put("time", location.getTime());
				locationMap.put("to_server", "Y");
				controller.insertLocation(locationMap);
			}
		}
		
	}
	
	class TransportLocationInsertTask extends AsyncTask<BDLocation, Integer, Boolean>{
		
		private JSONObject result;
		private boolean resultCode = false;
		private BDLocation mLocation;
		
		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(BDLocation... arg0) {
			
			result = new GameFunction().TransportInsert(arg0[0],bsid);
			mLocation = arg0[0];
			try {
				if("TRUE".equals(result.getString("flag").toUpperCase())){
					resultCode = true;
				}else{
					resultCode = false;
				}
			} catch (JSONException e) {
				resultCode = false;
			}
			return resultCode;
		}
		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			if (success) {
				insertToSqlite(mLocation);
			}else{
				
			}
			
		}
	}
	
	private void insertToSqlite(BDLocation location){
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		locationMap = new HashMap<String, String>();
		locationMap.put("bsid", bsid);
		locationMap.put("xdot", GpsUtils.DDDToDMS(location.getLongitude()+""));
		locationMap.put("ydot", GpsUtils.DDDToDMS(location.getLatitude()+""));
		locationMap.put("to_server", "Y");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(format.parse(location.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		locationMap.put("time", c.getTimeInMillis()/1000+"");
		controller.insertLocation(locationMap);
		
	}
	
	

}
