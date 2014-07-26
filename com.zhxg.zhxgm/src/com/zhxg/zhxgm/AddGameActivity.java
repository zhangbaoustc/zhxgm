package com.zhxg.zhxgm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import com.zhxg.zhxgm.utils.Utils;
import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.Game;

public class AddGameActivity extends BaseActivity implements View.OnTouchListener, Callbacks{

	private EditText game_name;
	private Spinner game_type; 
	private EditText game_distance;
	private EditText game_bonus;
	private EditText game_gather_time;
	private EditText game_add_gather_place;
	private EditText game_add_fly_date;
	private EditText game_add_fly_place;
	
	private Calendar gather_time_cal;
	private Calendar fly_date_cal;
	
	private ListView referee_listview;
	private RefereeAdapter refereeAdapter;
	private ArrayList<String> data;
	
	private Button addGame;
	private ProgressDialog progressDialog;  
	private HashMap<String, String> updatedData;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_game);
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		game_name = (EditText) findViewById(R.id.game_name);
		game_type = (Spinner) findViewById(R.id.game_type);
		ArrayAdapter<CharSequence> adapter = 
			ArrayAdapter.createFromResource(this, R.array.game_type_array, android.R.layout.simple_spinner_item);
		game_type.setAdapter(adapter);		
		
		game_distance = (EditText) findViewById(R.id.game_distance);
		game_bonus = (EditText) findViewById(R.id.game_bonus);
		
		game_gather_time = (EditText) findViewById(R.id.game_add_gather_time);
		game_gather_time.setOnTouchListener(this);
		game_add_gather_place = (EditText) findViewById(R.id.game_add_gather_place);
		
		game_add_fly_date = (EditText) findViewById(R.id.game_add_fly_date);
		game_add_fly_date.setOnTouchListener(this);
		game_add_fly_place = (EditText) findViewById(R.id.game_add_fly_place);

		referee_listview = (ListView) findViewById(R.id.referee_listview);
		
		updatedData = new HashMap<String, String>();
		data = new ArrayList<String>();
		data.add("");
		
		refereeAdapter = new RefereeAdapter(data, this);
		referee_listview.setAdapter(refereeAdapter);
		setListViewHeightBasedOnChildren(referee_listview);
		
		addGame = (Button) findViewById(R.id.game_add_confirm_btn);
		addGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				attemptAdd();
			}
		});
		
		
		gather_time_cal = Calendar.getInstance(); 
		fly_date_cal = Calendar.getInstance();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_game, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
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
	        
	        if(v.getId() == R.id.game_add_gather_time){
	        	datePicker.init(gather_time_cal.get(Calendar.YEAR), gather_time_cal.get(Calendar.MONTH), gather_time_cal.get(Calendar.DAY_OF_MONTH), null);
	        	timePicker.setVisibility(View.VISIBLE);
	        	date_title.setVisibility(View.VISIBLE);
	        	time_title.setVisibility(View.VISIBLE);
	        	builder.setTitle(getString(R.string.game_add_select_gather_time_title));  
                builder.setPositiveButton("ȷ  ��", new DialogInterface.OnClickListener() {  
  
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
                        game_add_gather_place.requestFocus();
                        
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
	            builder.setPositiveButton("ȷ  ��", new DialogInterface.OnClickListener() {  
	
	                @Override  
	                public void onClick(DialogInterface dialog, int which) {  
	
	                    StringBuffer sb = new StringBuffer();  
	                    sb.append(String.format("%d-%02d-%02d",   
	                            datePicker.getYear(),   
	                            datePicker.getMonth() + 1,  
	                            datePicker.getDayOfMonth()));  
	                    game_add_fly_date.setText(sb);  
	                    fly_date_cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
	                    game_add_fly_place.requestFocus();
	                    dialog.cancel();  
	                }  
	            });  
		        
	        }
	        Dialog dialog = builder.create();  
            dialog.show(); 
		}
		return true;
	}
	
	private void attemptAdd(){
		game_name.setError(null);
		game_gather_time.setError(null);
		game_distance.setError(null);
		game_bonus.setError(null);
		game_gather_time.setError(null);
		game_add_gather_place.setError(null);
		game_add_fly_date.setError(null);
		game_add_fly_place.setError(null);
		
		boolean cancel = false;
		View focusView = null;
		
		if(TextUtils.isEmpty(game_add_fly_place.getText().toString())){
			game_add_fly_place.setError(getString(R.string.error_field_required));
			focusView = game_add_fly_place;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(game_add_fly_date.getText().toString())){
			game_add_fly_date.setError(getString(R.string.error_field_required));
			focusView = game_add_fly_date;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(game_add_gather_place.getText().toString())){
			game_add_gather_place.setError(getString(R.string.error_field_required));
			focusView = game_add_gather_place;
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
		
		updatedData.put(Const.GAME_BONUS, game_bonus.getText().toString());
		updatedData.put(Const.GAME_NAME, game_name.getText().toString());
		updatedData.put(Const.GAME_TYPE, Utils.getGameTypeByPosition(this,game_type.getSelectedItemPosition()));
		updatedData.put(Const.GAME_TARGET_ID, Utils.getGameTargetIDByPosition(this, game_type.getSelectedItemPosition()));
		updatedData.put(Const.GAME_DISTANCE, game_distance.getText().toString());
		updatedData.put(Const.GAME_JG_ADDRESS, game_add_gather_place.getText().toString());
		updatedData.put(Const.GAME_JG_TIME, gather_time_cal.getTimeInMillis()/1000+"");
		updatedData.put(Const.GAME_FLY_DATE, fly_date_cal.getTimeInMillis()/1000+"");
		updatedData.put(Const.GAME_FLY_ADDRESS, game_add_fly_place.getText().toString());
		updatedData.put(Const.GAME_REFEREE, Utils.ArrayListToString(data));
		
		
		if (cancel) {
			focusView.requestFocus();
		} else {
			progressDialog = ProgressDialog.show(this, "", getString(R.string.add_game_loading), true, false);  
			new addGameTask().execute(updatedData);
		}
	}
	
	
	public class addGameTask extends AsyncTask<HashMap<String, String>, Void, Boolean> {
		private boolean resultCode = false;
		private JSONObject result;
		@SuppressLint("DefaultLocale")
		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {
			
			result = new GameFunction().gameAction(params[0]);
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
				setResult(Const.ADD_GAME_INTENT_FOR_RESULT, backIntent);
				AddGameActivity.this.finish();
			}else{
				Toast.makeText(AddGameActivity.this, R.string.add_error_from_server, Toast.LENGTH_LONG).show();
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
		addGame.requestFocus();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}


}
