package com.zhxg.zhxgm;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zhxg.zhxgm.control.TraceHistoryAdapter;
import com.zhxg.zhxgm.library.Action;
import com.zhxg.zhxgm.library.CustomGallery;
import com.zhxg.zhxgm.library.ExpandableHeightGridView;
import com.zhxg.zhxgm.library.GalleryAdapter;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.library.UserFunction;
import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.Trace;

public class TraceMarkActivity extends Activity implements Callback{
 
	ExpandableHeightGridView gridGallery;
	GalleryAdapter adapter;
	
	Button image_select;
	Button trace_send;
	
	ImageLoader imageLoader;
	String[] all_path;
	EditText trace_content;
	private String bsid;
	private ProgressBar trace_loading;
	
	private ListView trace_history;
	private TraceHistoryAdapter hisAdapter;
	private ArrayList<Trace> data;
	private Handler mHandler;
	private static final int trace_message_success= 111;
	private static final int trace_message_error= 123;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace_mark);
		
		if(getIntent() != null){
			bsid = getIntent().getStringExtra(Const.BSID);
		}
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		initImageLoader();
		init_layout();
		setData();
	}
	
	private void initImageLoader() {
		@SuppressWarnings("deprecation")
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
				new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	
	private void init_layout() {

		gridGallery = (ExpandableHeightGridView) findViewById(R.id.gridGallery);
		gridGallery.setExpanded(true);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);
		gridGallery.setAdapter(adapter);

		trace_content = (EditText) findViewById(R.id.trace_content);

		image_select = (Button) findViewById(R.id.image_select);
		image_select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
				i.putExtra(Const.BSID, bsid);
				startActivityForResult(i, 100);
			}
		});
		
		trace_send = (Button) findViewById(R.id.trace_send);
		trace_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptSendMark();				
			}
		});
		
		trace_loading = (ProgressBar) findViewById(R.id.trace_loading);
		trace_history = (ListView) findViewById(R.id.trace_history);
		
	}
	
	private void setData(){
		mHandler = new Handler(this);
		data = new ArrayList<Trace>();
		hisAdapter = new TraceHistoryAdapter(data,this); 
		trace_history.setAdapter(hisAdapter);
		
		new TraceHistoryTask().execute(bsid);
	}
	
	//prepare send trace mark
	private void attemptSendMark(){
		boolean cancel = false;
		
		if(TextUtils.isEmpty(trace_content.getText().toString())){
			cancel = true;
			trace_content.setError(getString(R.string.error_field_required));
			return;
		}
		
		new Thread(new SendTraceMark()).start();
		//new TraceMarkTask().execute(all_path);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
		    all_path = data.getStringArrayExtra("all_path");

			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

			for (String string : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;
				dataT.add(item);
			}

			adapter.addAll(dataT);
			gridGallery.setVisibility(View.VISIBLE);
		}
	}

	
	//
	private class SendTraceMark implements Runnable{

		@Override
		public void run() {
			Message msg = new Message();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("bsid", bsid);
			map.put("info", trace_content.getText().toString());
			map.put("userid", UserFunction.getUserInfo(TraceMarkActivity.this, Const.USER_ID));
			Boolean result = new GameFunction().addTraceMark(map, all_path); 
			if(result == true){
				msg.what = trace_message_success;
			}else{
				msg.what = trace_message_error;
			}
			mHandler.sendMessage(msg);
		}
		
	}
	
	
	
	class TraceMarkTask extends AsyncTask<String[], Integer, Boolean>{
		
		private ProgressDialog dialog = null;
		private JSONObject result;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(TraceMarkActivity.this); 
			dialog.setMessage(getString(R.string.trace_uploading));
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setCancelable(false);
			dialog.show();

		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			dialog.setProgress(values[0]);

		}

		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String[]... arg0) {
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("bsid", bsid);
			map.put("info", trace_content.getText().toString());
			map.put("userid", UserFunction.getUserInfo(TraceMarkActivity.this, Const.USER_ID));
			Boolean msg = new GameFunction().addTraceMark(map, arg0[0]); 
			return msg;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				TraceMarkActivity.this.finish();
				Toast.makeText(getApplicationContext(), getString(R.string.add_trace_success), Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), getString(R.string.add_trace_error), Toast.LENGTH_LONG).show();
			}
			dialog.dismiss(); 
		}
		
	}
	
	class TraceHistoryTask extends AsyncTask<String, Integer, Boolean>{
		
		private ProgressDialog dialog = null;
		private JSONObject result;
		private boolean resultCode = false;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String... arg0) {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			result = new GameFunction().getTraceHistry(arg0[0]);
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
			ArrayList<Trace> tempTrace = null;
			if (success) {
				try {
					tempTrace = new GameFunction().traceConvert(result.getJSONArray("msg"));
					trace_loading.setVisibility(View.GONE);
					trace_history.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				
			}
			
			data.clear();
			data.addAll(tempTrace);
			hisAdapter.notifyDataSetChanged();
			setListViewHeightBasedOnChildren(trace_history);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.trace_mark, menu);
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
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case trace_message_success:
			this.finish();
			Toast.makeText(getApplicationContext(), getString(R.string.add_trace_success), Toast.LENGTH_LONG).show();
			break;

		case trace_message_error:
			Toast.makeText(getApplicationContext(), getString(R.string.add_trace_error), Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		return false;
	}

}
