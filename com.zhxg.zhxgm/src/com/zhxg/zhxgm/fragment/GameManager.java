package com.zhxg.zhxgm.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.EditText;
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
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.zhxg.zhxgm.AddGameActivity;
import com.zhxg.zhxgm.CameraActivity;
import com.zhxg.zhxgm.R;
import com.zhxg.zhxgm.TraceMarkActivity;
import com.zhxg.zhxgm.UpdateGameActivity;
import com.zhxg.zhxgm.UploadImagesActivity;
import com.zhxg.zhxgm.control.SqliteController;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.library.UserFunction;
import com.zhxg.zhxgm.service.GameTransportService;
import com.zhxg.zhxgm.utils.GpsUtils;
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
	private ProgressDialog progressDialog;  
	private HashMap<String, String> updatedData;
    
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
	private EditText game_gather_num;
	private EditText game_gather_memo;
	private TextView game_gather_longitude;
	private TextView game_gather_latitude;
	private boolean gather_locationing;
	private TextView gather_location;
	
	//transport
	private TextView game_transport_name;
	private Button beginTransport;
	private Button endTransport;
	private TextView game_transport_start_point;
	private TextView game_transport_current_point;
	private TextView game_transport_distance;
	
	
	//let fly
	private TextView game_letfly_name;
	private EditText game_letfly_place;
	private TextView game_letfly_time;
	private TextView game_letfly_longitude;
	private TextView game_letfly_latitude;
	private boolean letfly_locationing;
	private TextView letfly_location;
	private TextView game_letfly_distance;
	private Button game_letfly_done;
	
	private static ArrayList<Game> data;
	private Game currentGame;
	private	SqliteController controller;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(getString(R.string.game_loading));
		
		updatedData = new HashMap<String, String>();
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
		p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);
		
		//geme info
		game_type = (TextView) rootView.findViewById(R.id.game_type);
		game_add = (Button) rootView.findViewById(R.id.game_add);
		game_add.setOnClickListener(this);
		game_gather_num = (EditText) rootView.findViewById(R.id.game_gather_num);
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
		game_gather_memo = (EditText) rootView.findViewById(R.id.game_gather_memo);
		rootView.findViewById(R.id.game_info_trace).setOnClickListener(this);
		rootView.findViewById(R.id.game_info_camera).setOnClickListener(this);
		rootView.findViewById(R.id.game_gather_update_btn).setOnClickListener(this);
		rootView.findViewById(R.id.game_gather_image_upload).setOnClickListener(this);
		game_gather_longitude = (EditText) rootView.findViewById(R.id.game_gather_longitude);
		game_gather_latitude = (EditText) rootView.findViewById(R.id.game_gather_latitude);
		gather_location = (TextView) rootView.findViewById(R.id.gather_location);
		gather_location.setOnClickListener(this);
		
		//transport
		rootView.findViewById(R.id.game_transport_trace).setOnClickListener(this);
		rootView.findViewById(R.id.game_transport_camera).setOnClickListener(this);
		rootView.findViewById(R.id.game_transport_image_upload).setOnClickListener(this);
		game_transport_name = (TextView) rootView.findViewById(R.id.game_transport_name);
		beginTransport = (Button) rootView.findViewById(R.id.game_transport_action_begin);
		beginTransport.setOnClickListener(this);
		
		endTransport = (Button) rootView.findViewById(R.id.game_transport_action_end);
		endTransport.setOnClickListener(this);
		game_transport_start_point = (TextView) rootView.findViewById(R.id.game_transport_start_point);
		game_transport_current_point = (TextView) rootView.findViewById(R.id.game_transport_current_point);
		game_transport_distance = (TextView) rootView.findViewById(R.id.game_transport_distance);
				
		//let fly
		game_letfly_name = (TextView) rootView.findViewById(R.id.game_letfly_name);		
		rootView.findViewById(R.id.game_letfly_trace).setOnClickListener(this);
		rootView.findViewById(R.id.game_letfly_camera).setOnClickListener(this);
		rootView.findViewById(R.id.game_letfly_image_upload).setOnClickListener(this);
		game_letfly_place = (EditText) rootView.findViewById(R.id.game_letfly_place);
		game_letfly_time = (TextView) rootView.findViewById(R.id.game_letfly_time);
		game_letfly_longitude = (TextView) rootView.findViewById(R.id.game_letfly_longitude);
		game_letfly_latitude = (TextView) rootView.findViewById(R.id.game_letfly_latitude);
		letfly_location = (TextView) rootView.findViewById(R.id.letfly_location);
		letfly_location.setOnClickListener(this);
		game_letfly_distance = (TextView) rootView.findViewById(R.id.game_letfly_distance);
		game_letfly_done = (Button) rootView.findViewById(R.id.game_letfly_done);
		game_letfly_done.setOnClickListener(this);
	}
	
	//set layout view data
	private void setLayoutData(Game game){
		if(game.getType() != null && !"".equals(game.getType())){
			game_type.setText(Utils.getGameTypeNameByType(mActivity, Integer.parseInt(game.getType())));
		}else{	
			game_type.setText("");
		} 
		game_distance.setText(game.getDistance());
		game_bonus.setText(game.getBonus());
		game_gather_time.setText(game.getJgDate());
		game_gather_place.setText(game.getJgAddress());
		game_gather_longitude.setText(GpsUtils.DDDToDMS(game.getJgLongitude()));
		game_gather_latitude.setText(GpsUtils.DDDToDMS(game.getJgLatitude()));
		
		game_fly_date.setText(game.getFlyDate());
		game_fly_place.setText(game.getFlyAddress());
		
		game_gather_name.setText(game.getName());
		game_gather_num.setText(game.getTotal());
		game_transport_name.setText(game.getName());
		game_letfly_name.setText(game.getName()); 
		
		game_gather_memo.setText(game.getInfo());
		
		game_referee.setText(game.getReferee());
		game_letfly_place.setText(game.getFlyAddress());
		game_letfly_time.setText(game.getFlyDate());
		game_letfly_longitude.setText(GpsUtils.DDDToDMS(game.getFlyLongitude()));
		game_letfly_latitude.setText(GpsUtils.DDDToDMS(game.getFlyLatitude()));
		
		if(game.getStatus().equals(Const.STATUS_COMPLETE+"") 
				|| game.getStatus().equals(Const.STATUS_ARRIVED+"")
				|| game.getStatus().equals(Const.STATUS_LETFLY+"")){
			beginTransport.setVisibility(View.GONE);
			endTransport.setVisibility(View.VISIBLE);
			endTransport.setText(mActivity.getString(R.string.action_transport_complete));
			endTransport.setEnabled(false);
		}else{
			beginTransport.setVisibility(View.VISIBLE);
			endTransport.setVisibility(View.GONE);
		}

		
		game_transport_start_point.setText(GpsUtils.DDDToDMS(game.getJgLongitude()) + "    " + GpsUtils.DDDToDMS(game.getJgLatitude()));
		GameFunction.setGameStatus(rg, Integer.parseInt(game.getStatus()));
	}
	
	//set game data
	private void setGameData(){
		adapter.clear();
		adapter.addAll(data);
		adapter.notifyDataSetChanged();
		if(data.size()>0){
			currentGame = data.get(0);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		targetid = UserFunction.getUserInfo(getActivity(), Const.TARGETID);
		mActivity = getActivity();
		prepareLoadData();

		mLocationClient = new LocationClient(getActivity().getApplicationContext()); 
		mLocationClient.registerLocationListener( myLocationListener );    
		
		rg.setOnCheckedChangeListener(
				new OnCheckedChangeListener() {
					@Override
				public void onCheckedChanged(RadioGroup arg0, int item) {
					changeGamePage(item);
				}
		});
	}
	
	
	//start or stop gps location 
	private void startGPSLocation(int interval){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(interval);
		option.setIsNeedAddress(false);
		option.setNeedDeviceDirect(true);
		mLocationClient.setLocOption(option);
		if(mLocationClient.isStarted()){
			mLocationClient.stop();
		}
		mLocationClient.start();
	}
	
	private void stopGPSLocation(){
		mLocationClient.stop();
		gather_locationing = false;
		gather_location.setClickable(true);
		letfly_locationing=false;
		letfly_location.setClickable(true);
		
		if(mBaiduMapView.getParent() != null){
			if(mBaiduMapView.getParent() == game_transport_ll){
				game_transport_ll.removeView(mBaiduMapView);
			}else if(mBaiduMapView.getParent() == game_letfly_ll){
				game_letfly_ll.removeView(mBaiduMapView);
			}
		}
	}
	
	
	
	private void changeGamePage(int item){
		switch (item) {
		case R.id.pageMgr:
			game_mgr_ll.setVisibility(View.VISIBLE);
			game_info_ll.setVisibility(View.GONE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.GONE);
			
			stopGPSLocation();
			break;
		case R.id.pageInfo:
			game_mgr_ll.setVisibility(View.GONE);
			game_info_ll.setVisibility(View.VISIBLE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.GONE);
			
			stopGPSLocation();
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
			startGPSLocation(120*1000);
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
						game_letfly_ll.addView(mBaiduMapView,6,p);
					}							
				}
			}else{
				game_letfly_ll.addView(mBaiduMapView,6,p);
			}
			mBaiduMapView.onResume();
				
			startGPSLocation(120*1000);
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
			if(mBaiduMapView.getParent() != null)
			{
				mBaiduMap.clear();	
				locationAndAddMapMark(location);
				if(mBaiduMapView.getParent() == game_transport_ll){
					game_transport_current_point.setText(GpsUtils.DDDToDMS(location.getLongitude()+"") + "  " + GpsUtils.DDDToDMS(location.getLatitude()+""));
					LatLng ptB = new LatLng(Double.valueOf(currentGame.getJgLatitude()), Double.valueOf(currentGame.getJgLongitude()));
					LatLng ptE = new LatLng(location.getLatitude(), location.getLongitude());
					game_transport_distance.setText(DistanceUtil.getDistance(ptB, ptE)+"");
					addTrace(location);
				}else if(letfly_locationing == true){
					letfly_locationing = false;
					letfly_location.setClickable(false);
					game_letfly_longitude.setText(GpsUtils.DDDToDMS(location.getLongitude()+""));
					game_letfly_latitude.setText(GpsUtils.DDDToDMS(location.getLatitude()+""));
					game_letfly_time.setText(location.getTime());
					LatLng ptB = new LatLng(Double.valueOf(currentGame.getJgLatitude()), Double.valueOf(currentGame.getJgLongitude()));
					LatLng ptE = new LatLng(location.getLatitude(), location.getLongitude());
					game_letfly_distance.setText(DistanceUtil.getDistance(ptB, ptE)+"");
					startGPSLocation(60*1000);
				}else{
					
				}
			}else{
				//location gather gps
				if(gather_locationing == true){
					gather_locationing = false;
					gather_location.setClickable(true);
					game_gather_latitude.setText(GpsUtils.DDDToDMS(location.getLatitude()+""));
					game_gather_longitude.setText(GpsUtils.DDDToDMS(location.getLongitude()+""));
				}
				stopGPSLocation();
			}
		}
	}
	

	
	public void locationAndAddMapMark(BDLocation location){
		// ������λͼ��  
		mBaiduMap.setMyLocationEnabled(true);  
		// ���춨λ���  
		MyLocationData locData = new MyLocationData.Builder()  
		    .accuracy(location.getRadius())  
		    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
		    .direction(100).latitude(location.getLatitude())  
		    .longitude(location.getLongitude()).build();  
		// ���ö�λ���  
		mBaiduMap.setMyLocationData(locData);  
		// ���ö�λͼ������ã���λģʽ���Ƿ����?����Ϣ���û��Զ��嶨λͼ�꣩  
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
		ArrayList<HashMap<String, String>> wordList = controller.getAllLocations("1234");
		if(wordList.size() == 0 || wordList.size() > 199)
		{
			return;
		}
		List<LatLng> pts = new ArrayList<LatLng>();  
		for(int i=0;i<wordList.size();i++){
			LatLng pt = new LatLng(Double.valueOf(GpsUtils.DMSToDDD(wordList.get(i).get("ydot"))),Double.valueOf(GpsUtils.DDDToDMS(wordList.get(i).get("xdot"))));
			pts.add(pt);
		}
		
		LatLng ptCurrent = new LatLng(location.getLatitude(), location.getLongitude());
		pts.add(ptCurrent);  
		PolylineOptions polylineOption = new PolylineOptions()
				.points(pts).color(0xAAFF0000);
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
					progressDialog.dismiss();
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			progressDialog.dismiss();
			Toast.makeText(mActivity, mActivity.getString(R.string.game_loading_error), Toast.LENGTH_LONG).show();
		}
	}
	
	public class letflyDoneTask extends AsyncTask<Game, Void, Boolean> {
		private boolean resultCode = false;
		private JSONObject result;
		@Override
		protected Boolean doInBackground(Game... params) {

			result = new GameFunction().letflyDone(params[0]);
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
				Toast.makeText(mActivity, mActivity.getString(R.string.letfly_done_success), Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(mActivity, mActivity.getString(R.string.letfly_done_error), Toast.LENGTH_LONG).show();
			}
			progressDialog.dismiss();
			
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.game_add:
			Intent intentAdd = new Intent(getActivity(), AddGameActivity.class);			
			startActivityForResult(intentAdd, Const.ADD_GAME_INTENT_FOR_RESULT);
			break;
		case R.id.game_edit:
			if(currentGame.getId() != null && !"".equals(currentGame.getId())){
				Intent intentEdit = new Intent(getActivity(), UpdateGameActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(Const.ACTIVITY_OBJECT, currentGame);
				intentEdit.putExtras(bundle);
				startActivityForResult(intentEdit, Const.EDIT_GAME_INTENT_FOR_RESULT);
			}
			break;
		case R.id.game_letfly_trace:
		case R.id.game_transport_trace:
		case R.id.game_info_trace:
			Intent intentTrace = new Intent(getActivity(), TraceMarkActivity.class);
			intentTrace.putExtra(Const.BSID, currentGame.getId());
			startActivity(intentTrace);
			break;
		case R.id.game_info_camera:
			Intent intentNormalCamera= new Intent(getActivity(), CameraActivity.class);
			intentNormalCamera.putExtra(Const.CAMERA_STYLE, Const.NORMAL);
			intentNormalCamera.putExtra(Const.GAME_STATUS, "1");
			intentNormalCamera.putExtra(Const.GAME_ID, currentGame.getId());
			startActivity(intentNormalCamera);
			break;
		case R.id.game_transport_camera:
			Intent intentNormalCameraT= new Intent(getActivity(), CameraActivity.class);
			intentNormalCameraT.putExtra(Const.CAMERA_STYLE, Const.NORMAL);
			intentNormalCameraT.putExtra(Const.GAME_STATUS, "2");
			intentNormalCameraT.putExtra(Const.GAME_ID, currentGame.getId());
			startActivity(intentNormalCameraT);
			break;
		case R.id.game_letfly_camera:
			Intent intentLetflyCamera= new Intent(getActivity(), CameraActivity.class);
			intentLetflyCamera.putExtra(Const.CAMERA_STYLE, Const.CONTINUE_CAMERA);
			intentLetflyCamera.putExtra(Const.GAME_STATUS, "3");
			intentLetflyCamera.putExtra(Const.GAME_ID, currentGame.getId());
			startActivity(intentLetflyCamera);
			break;
		case R.id.game_gather_update_btn:
			attempUpdateGatherInfo();
			break;
		case R.id.game_gather_image_upload:
			Intent intentUpload= new Intent(getActivity(), UploadImagesActivity.class);
			intentUpload.putExtra(Const.GAME_ID, currentGame.getId());
			intentUpload.putExtra(Const.GAME_STATUS, "1");
			startActivity(intentUpload);
			break;
		case R.id.game_transport_image_upload:
			Intent intentTransport= new Intent(getActivity(), UploadImagesActivity.class);
			intentTransport.putExtra(Const.GAME_ID, currentGame.getId());
			intentTransport.putExtra(Const.GAME_STATUS, "2");
			startActivity(intentTransport);
			break;
		case R.id.game_letfly_image_upload:
			Intent intentLetfly= new Intent(getActivity(), UploadImagesActivity.class);
			intentLetfly.putExtra(Const.GAME_ID, currentGame.getId());
			intentLetfly.putExtra(Const.GAME_STATUS, "3");
			startActivity(intentLetfly);
			break;
		case R.id.game_transport_action_begin:
			if(currentGame.getStatus().equals(Const.STATUS_STARTED+"")){
				new beginTransportTask().execute(currentGame);
			}else{
				startTransportService();
			}
			break;
		case R.id.game_transport_action_end:
			if(currentGame.getStatus().equals(Const.STATUS_TRANSPORTING+"")){
				new stopTransportTask().execute(currentGame);
			}else{
				stopTransportService();
			}
			break;
		case R.id.gather_location:
			gather_locationing = true;
			startGPSLocation(1000);
			gather_location.setClickable(false);
			break;
		case R.id.letfly_location:
			letfly_locationing = true;
			startGPSLocation(1000);
			letfly_location.setClickable(false);
			break;
		case R.id.game_letfly_done:
			new letflyDoneTask().execute(currentGame);
			break;
		default:
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void attempUpdateGatherInfo(){
		

		
		game_gather_latitude.setError(null);
		game_gather_longitude.setError(null);
		
		if(!GpsUtils.isDMS(game_gather_latitude.getText().toString())){
			game_gather_latitude.setError(mActivity.getString(R.string.dms_gps_format_error));
			return;
		}
		else if(!GpsUtils.isDMS(game_gather_longitude.getText().toString())){
			game_gather_longitude.setError(mActivity.getString(R.string.dms_gps_format_error));
			return;
		}
		
		updatedData.clear();
		updatedData.put(Const.GAME_ID, currentGame.getId());
		updatedData.put(Const.GAME_TOTAL, game_gather_num.getText().toString());
		updatedData.put(Const.GAME_INFO, game_gather_memo.getText().toString());
		if(currentGame.getStatus().equals(Const.STATUS_PREPARING + "")){
			updatedData.put(Const.GAME_STATUS, Const.STATUS_STARTED+"");
		}
		
		updatedData.put(Const.GAME_JGLATITUDE, GpsUtils.DMSToDDD(game_gather_latitude.getText().toString()));
		updatedData.put(Const.GAME_JGLONGITUDE, GpsUtils.DMSToDDD(game_gather_longitude.getText().toString()));
		
		progressDialog.show();
		new updateGatherInfoTask().execute(updatedData);
	}
	
	public class updateGatherInfoTask extends AsyncTask<HashMap<String, String>, Void, Boolean> {
		private boolean resultCode = false;
		private JSONObject result;
		@SuppressLint("DefaultLocale")
		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {
			
			result = new GameFunction().updateGatherInfo(params[0]);
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
		protected void onPostExecute(final Boolean success) {

			if (success) {
				new GameFunction().updateLocalGameData(data, updatedData);
				setLayoutData(currentGame);
				Toast.makeText(mActivity, R.string.update_gather_info_success, Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(mActivity, R.string.update_gather_info_error, Toast.LENGTH_LONG).show();
			}
			progressDialog.dismiss();
		}
	}
	
	
	public class beginTransportTask extends AsyncTask<Game, Void, Boolean> {
		private boolean resultCode = false;
		private JSONObject result;
		@SuppressLint("DefaultLocale")
		@Override
		protected Boolean doInBackground(Game... params) {
			
			result = new GameFunction().beginTransport(params[0]);
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
		protected void onPostExecute(final Boolean success) {

			if (success) {
				startTransportService();
			}else{
				Toast.makeText(mActivity, R.string.begin_transport_error, Toast.LENGTH_LONG).show();
			}
			progressDialog.dismiss();
		}
	}
	
	public class stopTransportTask extends AsyncTask<Game, Void, Boolean> {
		private boolean resultCode = false;
		private JSONObject result;
		@SuppressLint("DefaultLocale")
		@Override
		protected Boolean doInBackground(Game... params) {
			
			result = new GameFunction().stopTransport(params[0]);
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
		protected void onPostExecute(final Boolean success) {

			if (success) {
				stopTransportService();
			}else{
				Toast.makeText(mActivity, R.string.end_transport_error, Toast.LENGTH_LONG).show();
			}
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if((requestCode == Const.ADD_GAME_INTENT_FOR_RESULT && resultCode == Const.ADD_GAME_INTENT_FOR_RESULT)
				|| (requestCode == Const.EDIT_GAME_INTENT_FOR_RESULT && resultCode == Const.EDIT_GAME_INTENT_FOR_RESULT)){
			prepareLoadData();
		}
	}
	
	private void prepareLoadData(){
		progressDialog.show();
		new loadGamesTask().execute();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void startTransportService(){
		Intent intent = new Intent(mActivity, GameTransportService.class);
		intent.putExtra(Const.BSID, currentGame.getId());
		getActivity().startService(intent);
		beginTransport.setVisibility(View.GONE);
		endTransport.setVisibility(View.VISIBLE);
		endTransport.setEnabled(true);
		endTransport.setText(mActivity.getString(R.string.action_transport_end));
	}
	
	private void stopTransportService(){
		
		updatedData.clear();
		updatedData.put(Const.GAME_STATUS, Const.STATUS_ARRIVED+"");
		new GameFunction().updateLocalGameData(data, updatedData);
		
		Intent intent = new Intent(getActivity().getApplicationContext(), GameTransportService.class);
		getActivity().getApplicationContext().stopService(intent);
		endTransport.setVisibility(View.VISIBLE);
		endTransport.setEnabled(false);
		endTransport.setText(mActivity.getString(R.string.action_transport_complete));
	}

}
