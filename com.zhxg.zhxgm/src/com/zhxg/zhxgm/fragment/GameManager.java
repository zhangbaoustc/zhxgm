package com.zhxg.zhxgm.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.zhxg.zhxgm.R;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.service.GameTransportService;
import com.zhxg.zhxgm.vo.Game;

public class GameManager extends GeneralFragment {

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
   
	//game info
	private Spinner gameSpinner;
	private	ArrayAdapter<Game> adapter;
	//transport
	private Button beginTransport;
	private Button endTransport;
	
	private static List<Game> data;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		data = new ArrayList<Game>();
		new loadGamesTask().execute();
		//loadGameData
		
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
		gameSpinner = (Spinner) rootView.findViewById(R.id.gameNameSpinner);
		adapter = new ArrayAdapter<Game>(getActivity(), android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		gameSpinner.setAdapter(adapter);
		gameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		
		//transport
		beginTransport = (Button) rootView.findViewById(R.id.game_transport_action_begin);
		beginTransport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
				Intent intent = new Intent(getActivity(), GameTransportService.class);
				getActivity().stopService(intent);
			}
		});
	}
	
	//set game data
	private void setGameData(){
		adapter.clear();
		adapter.addAll(data);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		mLocationClient = new LocationClient(getActivity().getApplicationContext());     //����LocationClient��
		mLocationClient.registerLocationListener( myLocationListener );    //ע���������
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
		option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(10000);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(true);//���صĶ�λ���������ַ��Ϣ
		option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
		mLocationClient.setLocOption(option);
		
		//mLocationClient.start();
		
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
		//����Markerͼ��  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.mark);  
		//����MarkerOption�������ڵ�ͼ�����Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(point)  
		    .icon(bitmap);  
		//�ڵ�ͼ�����Marker������ʾ
		mBaiduMap.clear();		  
		//mBaiduMap.addOverlay(option);
	}
	
	public void locationAndAddMapMark(BDLocation location){
		// ������λͼ��  
		mBaiduMap.setMyLocationEnabled(true);  
		// ���춨λ����  
		MyLocationData locData = new MyLocationData.Builder()  
		    .accuracy(location.getRadius())  
		    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
		    .direction(100).latitude(location.getLatitude())  
		    .longitude(location.getLongitude()).build();  
		// ���ö�λ����  
		mBaiduMap.setMyLocationData(locData);  
		// ���ö�λͼ������ã���λģʽ���Ƿ���������Ϣ���û��Զ��嶨λͼ�꣩  
		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory  
		    .fromResource(R.drawable.mark);  
		MyLocationConfigeration config = new MyLocationConfigeration(
				com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.FOLLOWING, 
				false, null);  
		
		mBaiduMap.setMyLocationConfigeration(config);  
		// ������Ҫ��λͼ��ʱ�رն�λͼ��  
		//mBaiduMap.setMyLocationEnabled(false);
	}
	
	public void addTrace(BDLocation location){
		LatLng pt1 = new LatLng(39.93923, 116.357428);  
		LatLng pt2 = new LatLng(39.91923, 116.327428);  
		LatLng pt3 = new LatLng(39.89923, 116.347428);  
		LatLng pt4 = new LatLng(39.89923, 116.367428);  
		LatLng pt5 = new LatLng(39.91923, 116.387428);
		LatLng pt6 = new LatLng(location.getLatitude(), location.getLongitude());
		List<LatLng> pts = new ArrayList<LatLng>();  
		pts.add(pt1);  
		pts.add(pt2);  
		pts.add(pt3);  
		pts.add(pt4);  
		pts.add(pt5);
		pts.add(pt6);  
		//�����û����ƶ���ε�Option����
		PolylineOptions polylineOption = new PolylineOptions()
				.points(pts).color(0xAAFF0000);
		//�ڵ�ͼ����Ӷ����Option��������ʾ  
		mBaiduMap.addOverlay(polylineOption);
	}
	
	
	public class loadGamesTask extends AsyncTask<Void, Void, Boolean> {
		private boolean resultCode = false;
		private JSONObject result;
		@Override
		protected Boolean doInBackground(Void... params) {
			
			result = new GameFunction().getGames("340000");
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
			
			Toast.makeText(getActivity(), "��ȡ������Ϣʧ�ܣ�", Toast.LENGTH_LONG).show();
		}

	}
	

}
