package com.zhxg.zhxgm.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.zhxg.zhxgm.AddGameActivity;
import com.zhxg.zhxgm.CameraActivity;
import com.zhxg.zhxgm.EditGameActivity;
import com.zhxg.zhxgm.R;
import com.zhxg.zhxgm.TraceMarkActivity;
import com.zhxg.zhxgm.control.SqliteController;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.library.UserFunction;
import com.zhxg.zhxgm.service.GameTransportService;
import com.zhxg.zhxgm.utils.Utils;
import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.Game;

public class GameManager extends GeneralFragment implements OnClickListener{

	private View rootView;
	private RadioGroup rg;
	private LinearLayout game_mgr_ll;
	private LinearLayout game_info_ll;
	private LinearLayout game_transport_ll;
	private LinearLayout game_letfly_ll;

	private LinearLayout.LayoutParams p;
	
	private MapView mBaiduMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocationClient;
	private BDLocationListener myLocationListener = new MyLocationListener();
	private String targetid;
	private Activity mActivity;
	
   
	//game info
	private Spinner gameSpinner;
	private	ArrayAdapter<Game> adapter;
	private TextView game_distance;
	private TextView game_bonus;
	private TextView game_gather_time;
	private TextView game_gather_place;
	private TextView game_fly_date;
	private TextView game_fly_place;
	private TextView game_referee;
	private Button game_add;
	private Button game_edit;
	private TextView game_type;
	
	//game gather
	private TextView game_gather_name;
	
	//transport
	private TextView game_transport_name;
	private Button beginTransport;
	private Button endTransport;
	
	
	//let fly
	private TextView game_letfly_name;
	
	private static List<Game> data;
	private Game currentGame;
	private	SqliteController controller;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		data = new ArrayList<Game>();
		controller = new SqliteController(getActivity().getApplicationContext());
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.game_manager, container,false);
		initLayout();
		return rootView;
	}
	
	private void initLayout(){
		rg = (RadioGroup) rootView.findViewById(R.id.game_nav_group);
		game_mgr_ll = (LinearLayout) rootView.findViewById(R.id.game_mgr_ll);
		game_info_ll = (LinearLayout) rootView.findViewById(R.id.game_info_ll);
		game_transport_ll = (LinearLayout) rootView.findViewById(R.id.game_transport_ll);
		game_letfly_ll = (LinearLayout) rootView.findViewById(R.id.game_letfly_ll);
				
		
		mBaiduMapView = new MapView(getActivity());
		mBaiduMapView.setClickable(true);
		mBaiduMap = mBaiduMapView.getMap();
		p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
		
		//geme info
		game_type = (TextView) rootView.findViewById(R.id.game_type);
		game_add = (Button) rootView.findViewById(R.id.game_add);
		game_add.setOnClickListener(this);
		game_edit = (Button) rootView.findViewById(R.id.game_edit);
		game_edit.setOnClickListener(this);
		gameSpinner = (Spinner) rootView.findViewById(R.id.gameNameSpinner);
		adapter = new ArrayAdapter<Game>(getActivity(), android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		gameSpinner.setAdapter(adapter);
		gameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Log.i("dd", "23423423");
				currentGame = data.get(position);
				setLayoutData(currentGame);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		game_distance = (TextView) rootView.findViewById(R.id.game_distance);
		game_bonus = (TextView) rootView.findViewById(R.id.game_bonus);
		game_gather_time = (TextView) rootView.findViewById(R.id.game_gather_time);
		game_gather_place = (TextView) rootView.findViewById(R.id.game_gather_place);
		game_fly_date = (TextView) rootView.findViewById(R.id.game_fly_date);
		game_fly_place = (TextView) rootView.findViewById(R.id.game_fly_place);
		game_referee = (TextView) rootView.findViewById(R.id.game_referee);
		
		//gather
		game_gather_name = (TextView) rootView.findViewById(R.id.game_gather_name);
		rootView.findViewById(R.id.game_info_trace).setOnClickListener(this);
		rootView.findViewById(R.id.game_info_camera).setOnClickListener(this);
		
		
		//transport
		rootView.findViewById(R.id.game_transport_trace).setOnClickListener(this);
		rootView.findViewById(R.id.game_transport_camera).setOnClickListener(this);
		game_transport_name = (TextView) rootView.findViewById(R.id.game_transport_name);
		beginTransport = (Button) rootView.findViewById(R.id.game_transport_action_begin);
		beginTransport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//save game id to sharedPreferences
				SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Const.transport_page, Context.MODE_PRIVATE); //私有数据
				Editor editor = sharedPreferences.edit();//获取编辑器
				editor.putString("game_id", currentGame.getId());
				editor.commit();//提交修改
				
				Intent intent = new Intent(getActivity(), GameTransportService.class);
				getActivity().startService(intent);
				beginTransport.setVisibility(View.GONE);
				endTransport.setVisibility(View.VISIBLE);
			}
		});
		endTransport = (Button) rootView.findViewById(R.id.game_transport_action_end);
		endTransport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity().getApplicationContext(), GameTransportService.class);
				getActivity().getApplicationContext().stopService(intent);
			}
		});
		
		//let fly
		game_letfly_name = (TextView) rootView.findViewById(R.id.game_letfly_name);		
		rootView.findViewById(R.id.game_letfly_trace).setOnClickListener(this);
		rootView.findViewById(R.id.game_letfly_camera).setOnClickListener(this);
	}
	
	//set layout view data
	private void setLayoutData(Game game){
		game_type.setText(Utils.getGameTypeNameByType(mActivity, Integer.parseInt(game.getType())));
		game_distance.setText(game.getDistance());
		game_bonus.setText(game.getBonus());
		game_gather_time.setText(game.getJgDate());
		game_gather_place.setText(game.getJgAddress());
		game_fly_date.setText(game.getFlyDate());
		game_fly_place.setText(game.getFlyAddress());
		
		game_gather_name.setText(game.getName());
		game_transport_name.setText(game.getName());
		game_letfly_name.setText(game.getName());
		
	}
	
	//set game data
	private void setGameData(){
		adapter.clear();
		adapter.addAll(data);
		adapter.notifyDataSetChanged();
		currentGame = data.get(0);
		setLayoutData(currentGame);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		targetid = UserFunction.getUserInfo(getActivity(), Const.TARGETID);
		mActivity = getActivity();
		new loadGamesTask().execute();

		mLocationClient = new LocationClient(getActivity().getApplicationContext());     //声明LocationClient类
		mLocationClient.registerLocationListener( myLocationListener );    //注册监听函数
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(10000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		
		mLocationClient.start();
		
		rg.setOnCheckedChangeListener(
				new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup arg0, int item) {
						changeGamePage(item);
					}
				});
	}
	
	private void changeGamePage(int item){
		switch (item) {
		case R.id.pageMgr:
			game_mgr_ll.setVisibility(View.VISIBLE);
			game_info_ll.setVisibility(View.GONE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.GONE);
			break;
		case R.id.pageInfo:
			game_mgr_ll.setVisibility(View.GONE);
			game_info_ll.setVisibility(View.VISIBLE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.GONE);
			break;
		case R.id.pageTransport:
			game_mgr_ll.setVisibility(View.GONE);
			game_info_ll.setVisibility(View.GONE);
			game_transport_ll.setVisibility(View.VISIBLE);
			game_letfly_ll.setVisibility(View.GONE);
			
			if(mBaiduMapView.getParent() != null){
				if(mBaiduMapView.getParent() != game_transport_ll){
					if(mBaiduMapView.getParent() == game_letfly_ll)
					{
						mBaiduMap.clear();		
						mBaiduMapView.onPause();
						game_letfly_ll.removeView(mBaiduMapView);
						game_transport_ll.addView(mBaiduMapView,5,p);
					}							
				}
			}else{
				game_transport_ll.addView(mBaiduMapView,5,p);
			}
			
			mBaiduMapView.onResume();
			break;
		case R.id.pageLetFly:
			game_mgr_ll.setVisibility(View.GONE);
			game_info_ll.setVisibility(View.GONE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.VISIBLE);
			
			if(mBaiduMapView.getParent() != null){
				if(mBaiduMapView.getParent() != game_letfly_ll){
					if(mBaiduMapView.getParent() == game_transport_ll)
					{
						mBaiduMap.clear();		
						mBaiduMapView.onPause();
						game_transport_ll.removeView(mBaiduMapView);
						game_letfly_ll.addView(mBaiduMapView,5,p);
					}							
				}
			}else{
				game_letfly_ll.addView(mBaiduMapView,5,p);
			}
			mBaiduMapView.onResume();
				
			
			break;
		default:
			game_mgr_ll.setVisibility(View.VISIBLE);
			game_info_ll.setVisibility(View.GONE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.GONE);			
			break;
		}
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
			
			//addMapMark(location.getLongitude(), location.getLatitude());
			if(mBaiduMapView.getParent() != null)
			{
				mBaiduMap.clear();	
				locationAndAddMapMark(location);
				if(mBaiduMapView.getParent() == game_transport_ll){
					addTrace(location);
				}
			}
		}
	}
	
	public void addMapMark(double longitute, double latitude){
		LatLng point = new LatLng(latitude, longitute);  
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.mark);  
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(point)  
		    .icon(bitmap);  
		//在地图上添加Marker，并显示
		mBaiduMap.clear();		  
		//mBaiduMap.addOverlay(option);
	}
	
	public void locationAndAddMapMark(BDLocation location){
		// 开启定位图层  
		mBaiduMap.setMyLocationEnabled(true);  
		// 构造定位数据  
		MyLocationData locData = new MyLocationData.Builder()  
		    .accuracy(location.getRadius())  
		    // 此处设置开发者获取到的方向信息，顺时针0-360  
		    .direction(100).latitude(location.getLatitude())  
		    .longitude(location.getLongitude()).build();  
		// 设置定位数据  
		mBaiduMap.setMyLocationData(locData);  
		// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）  
		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory  
		    .fromResource(R.drawable.mark);  
		MyLocationConfigeration config = new MyLocationConfigeration(
				com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.FOLLOWING, 
				false, null);  
		
		mBaiduMap.setMyLocationConfigeration(config);  
		// 当不需要定位图层时关闭定位图层  
		//mBaiduMap.setMyLocationEnabled(false);
	}
	
	public void addTrace(BDLocation location){
		ArrayList<HashMap<String, String>> wordList = controller.getAllLocations("1234");
		List<LatLng> pts = new ArrayList<LatLng>();  
		for(int i=0;i<wordList.size();i++){
			LatLng pt = new LatLng(Double.valueOf(wordList.get(i).get("ydot")),Double.valueOf(wordList.get(i).get("xdot")));
			pts.add(pt);
		}
		
		LatLng ptCurrent = new LatLng(location.getLatitude(), location.getLongitude());
		pts.add(ptCurrent);  
		//构建用户绘制多边形的Option对象
		PolylineOptions polylineOption = new PolylineOptions()
				.points(pts).color(0xAAFF0000);
		//在地图上添加多边形Option，用于显示  
		mBaiduMap.addOverlay(polylineOption);
	}
	
	
	public class loadGamesTask extends AsyncTask<Void, Void, Boolean> {
		private boolean resultCode = false;
		private JSONObject result;
		@Override
		protected Boolean doInBackground(Void... params) {
			
			result = new GameFunction().getGames(targetid);
			try {
				if("TRUE".equals(result.getString("flag").toUpperCase())){
					resultCode = true;
				}else{
					resultCode = false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return resultCode;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (success) {
				try {
					data = new GameFunction().gameConvert(result.getJSONArray("msg"));
					
					setGameData();
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			Toast.makeText(getActivity(), "获取比赛信息失败！", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.game_add:
			Intent intentAdd = new Intent(getActivity(), AddGameActivity.class);
			startActivity(intentAdd);
			break;
		case R.id.game_edit:
			Intent intentEdit = new Intent(getActivity(), EditGameActivity.class);
			startActivity(intentEdit);
			break;
		case R.id.game_letfly_trace:
		case R.id.game_transport_trace:
		case R.id.game_info_trace:
			Intent intentTrace = new Intent(getActivity(), TraceMarkActivity.class);
			intentTrace.putExtra(Const.BSID, currentGame.getId());
			startActivity(intentTrace);
			break;
		case R.id.game_info_camera:
		case R.id.game_transport_camera:
		case R.id.game_letfly_camera:
			Intent intentCamera= new Intent(getActivity(), CameraActivity.class);
			intentCamera.putExtra(Const.BSID, currentGame.getId());
			startActivity(intentCamera);
			break;
		default:
			break;
		}
	}
	

}
