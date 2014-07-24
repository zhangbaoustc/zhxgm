package com.zhxg.zhxgm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zhxg.zhxgm.control.RefereeAdapter;
import com.zhxg.zhxgm.control.RefereeAdapter.Callbacks;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.library.UserFunction;
import com.zhxg.zhxgm.utils.Utils;
import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.Game;


public class UpdateGameActivity extends BaseActivity implements OnTouchListener,Callbacks {

	
	private Game mGame;
	private TextView game_name;
	private Spinner game_type;
	private TextView game_distance;
	private TextView game_bonus;
	private TextView game_gather_time;
	private TextView game_gather_place;
	private TextView game_fly_date;
	private TextView game_fly_place;
	private ListView referee_listview;
	private RefereeAdapter refereeAdapter;
	private ArrayList<String> data;
	
	private SimpleDateFormat dateTimeFormat;
	private SimpleDateFormat dateFormat;
	private Calendar gather_time_cal;
	private Calendar fly_date_cal;
	private ProgressDialog progressDialog;  
	private Button updateGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_game);
		
		dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		data = new ArrayList<String>();
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		if(getIntent() != null){
			mGame = (Game) getIntent().getSerializableExtra(Const.ACTIVITY_OBJECT);
		}
		
		setupViews();
		setpuData();
	}
	
	private void setupViews(){
		game_name = (EditText) findViewById(R.id.game_name);
		game_type = (Spinner) findViewById(R.id.game_type);
		game_distance = (EditText) findViewById(R.id.game_distance);
		game_bonus = (EditText) findViewById(R.id.game_bonus);
		game_gather_time = (EditText) findViewById(R.id.game_gather_time);
		game_gather_time.setOnTouchListener(this);
		game_gather_place = (EditText) findViewById(R.id.game_gather_place);
		game_fly_date = (EditText) findViewById(R.id.game_fly_date);
		game_fly_date.setOnTouchListener(this);
		game_fly_place = (EditText) findViewById(R.id.game_fly_place);
		
		referee_listview = (ListView) findViewById(R.id.referee_listview);
		updateGame = (Button) findViewById(R.id.game_update_confirm_btn);
	}
	
	private void setpuData(){
		game_name.setText(mGame.getName());
		
		
		//set game type spinner
		ArrayList<String> gameTypes = Utils.getTypeByRole(this, UserFunction.getUserInfo(this, Const.ROLE));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, gameTypes);
		game_type.setAdapter(adapter);
		
		String gameTypeName = Utils.getGameTypeNameByType(this, Integer.parseInt(mGame.getType()));
		for(int i=0;i<gameTypes.size();i++){
			if(gameTypes.get(i).equals(gameTypeName)){
				game_type.setSelection(i);
				break;
			}
		}
		
		game_distance.setText(mGame.getDistance());
		game_bonus.setText(mGame.getBonus());
		game_gather_time.setText(mGame.getJgDate());
		game_gather_place.setText(mGame.getJgAddress());
		game_fly_date.setText(mGame.getFlyDate());
		game_fly_place.setText(mGame.getFlyAddress());
		
		
		gather_time_cal = Calendar.getInstance();
		fly_date_cal = Calendar.getInstance();
		try {
			gather_time_cal.setTime(dateTimeFormat.parse(mGame.getJgDate()));
			fly_date_cal.setTime(dateFormat.parse(mGame.getFlyDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		data = new ArrayList<String>(Arrays.asList(mGame.getReferee().split(",")));
		if(!mGame.getReferee().equals("")){
			data.add("");
		}
		refereeAdapter = new RefereeAdapter(data, this);
		referee_listview.setAdapter(refereeAdapter);
		setListViewHeightBasedOnChildren(referee_listview);
		
		updateGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptUpdate();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.update_game, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
	        return true;    
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);  
	        View view = View.inflate(this, R.layout.date_time_dialog, null);  
	        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);  
	        final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);  
	        TextView date_title = (TextView) view.findViewById(R.id.date_title);
	        TextView time_title = (TextView) view.findViewById(R.id.time_title);
	        builder.setView(view);  

	         
	        datePicker.init(gather_time_cal.get(Calendar.YEAR), gather_time_cal.get(Calendar.MONTH), gather_time_cal.get(Calendar.DAY_OF_MONTH), null);  

	        timePicker.setIs24HourView(true);  
	        timePicker.setCurrentHour(gather_time_cal.get(Calendar.HOUR_OF_DAY));  
	        timePicker.setCurrentMinute(gather_time_cal.get(Calendar.MINUTE));
	        
	        if(v.getId() == R.id.game_gather_time){
	        	datePicker.init(gather_time_cal.get(Calendar.YEAR), gather_time_cal.get(Calendar.MONTH), gather_time_cal.get(Calendar.DAY_OF_MONTH), null);
	        	timePicker.setVisibility(View.VISIBLE);
	        	date_title.setVisibility(View.VISIBLE);
	        	time_title.setVisibility(View.VISIBLE);
	        	builder.setTitle(getString(R.string.game_add_select_gather_time_title));  
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {  
  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
  
                        StringBuffer sb = new StringBuffer();  
                        sb.append(String.format("%d-%02d-%02d",   
                                datePicker.getYear(),   
                                datePicker.getMonth() + 1,  
                                datePicker.getDayOfMonth()));  
                        sb.append("  ");  
                        sb.append(timePicker.getCurrentHour())  
                        .append(":").append(timePicker.getCurrentMinute());  
                        game_gather_time.setText(sb);  
                        game_gather_place.requestFocus();
                        
                        gather_time_cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                        dialog.cancel();  
                    }  
                });  
	        }else{
	        	datePicker.init(fly_date_cal.get(Calendar.YEAR), fly_date_cal.get(Calendar.MONTH), fly_date_cal.get(Calendar.DAY_OF_MONTH), null);
	        	timePicker.setVisibility(View.GONE);
	        	date_title.setVisibility(View.GONE);
	        	time_title.setVisibility(View.GONE);
		        builder.setTitle(getString(R.string.game_add_select_fly_time_title));  
	            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {  
	
	                @Override  
	                public void onClick(DialogInterface dialog, int which) {  
	
	                    StringBuffer sb = new StringBuffer();  
	                    sb.append(String.format("%d-%02d-%02d",   
	                            datePicker.getYear(),   
	                            datePicker.getMonth() + 1,  
	                            datePicker.getDayOfMonth()));  
	                    game_fly_date.setText(sb);  
	                    fly_date_cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
	                    game_fly_place.requestFocus();
	                    dialog.cancel();  
	                }  
	            });  
		        
	        }
	        Dialog dialog = builder.create();  
            dialog.show(); 
		}
		return true;
	}
	
	
	private void attemptUpdate(){
		game_name.setError(null);
		game_gather_time.setError(null);
		game_distance.setError(null);
		game_bonus.setError(null);
		game_gather_time.setError(null);
		game_gather_place.setError(null);
		game_fly_date.setError(null);
		game_fly_place.setError(null);
		
		boolean cancel = false;
		View focusView = null;
		
		if(TextUtils.isEmpty(game_fly_place.getText().toString())){
			game_fly_place.setError(getString(R.string.error_field_required));
			focusView = game_fly_place;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(game_fly_date.getText().toString())){
			game_fly_date.setError(getString(R.string.error_field_required));
			focusView = game_fly_date;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(game_gather_place.getText().toString())){
			game_gather_place.setError(getString(R.string.error_field_required));
			focusView = game_gather_place;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(game_gather_time.getText().toString())){
			game_gather_time.setError(getString(R.string.error_field_required));
			focusView = game_gather_time;
			cancel = true;
		}

		if(TextUtils.isEmpty(game_bonus.getText().toString())){
			game_bonus.setError(getString(R.string.error_field_required));
			focusView = game_bonus;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(game_distance.getText().toString())){
			game_distance.setError(getString(R.string.error_field_required));
			focusView = game_distance;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(game_gather_time.getText().toString())){
			game_gather_time.setError(getString(R.string.error_field_required));
			focusView = game_gather_time;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(game_name.getText().toString())){
			game_name.setError(getString(R.string.error_field_required));
			focusView = game_name;
			cancel = true;
		}
		
		Game game = new Game();
		game.setId(mGame.getId());
		game.setName(game_name.getText().toString());
		game.setBonus(game_bonus.getText().toString());
		game.setType(Utils.getGameTypeByPosition(this,game_type.getSelectedItemPosition()));
		game.setTargetID(Utils.getGameTargetIDByPosition(this, game_type.getSelectedItemPosition()));
		game.setDate("");
		game.setDistance(game_distance.getText().toString());
		
		game.setJgAddress(game_gather_place.getText().toString());
		game.setJgLatitude("");
		game.setJgLongitude("");
		
		game.setJgDate(gather_time_cal.getTimeInMillis()/1000+"");
		game.setFlyAddress(game_fly_place.getText().toString());
		game.setFlyDate(fly_date_cal.getTimeInMillis()/1000+"");
		game.setFlyLatitude("");
		game.setFlyLongitude("");
		game.setReferee(Utils.ArrayListToString(data));
		
		if (cancel) {
			focusView.requestFocus();
		} else {
			progressDialog = ProgressDialog.show(this, "", getString(R.string.add_game_loading), true, false);  
			new updateGameTask().execute(game);
		}
	}
	
	public class updateGameTask extends AsyncTask<Game, Void, Boolean> {
		private boolean resultCode = false;
		private JSONObject result;
		@SuppressLint("DefaultLocale")
		@Override
		protected Boolean doInBackground(Game... params) {
			
			result = new GameFunction().updateGame(params[0]);
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
				Intent backIntent = new Intent();
				setResult(Const.EDIT_GAME_INTENT_FOR_RESULT, backIntent);
				UpdateGameActivity.this.finish();
			}else{
				Toast.makeText(UpdateGameActivity.this, R.string.add_error_from_server, Toast.LENGTH_LONG).show();
			}
			progressDialog.dismiss();
		}
	}
	
	
	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null)
	        return;

	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0)
	            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}

	@Override
	public void onDataSetChanged() {
		setListViewHeightBasedOnChildren(referee_listview);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

}
