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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zhxg.zhxgm.control.TraceHistoryAdapter;
import com.zhxg.zhxgm.library.Action;
import com.zhxg.zhxgm.library.CustomGallery;
import com.zhxg.zhxgm.library.GalleryAdapter;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.library.UserFunction;
import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.Trace;

public class TraceMarkActivity extends Activity {

	GridView gridGallery;
	GalleryAdapter adapter;
	
	Button image_select;
	Button trace_send;
	
	ImageLoader imageLoader;
	String[] all_path;
	EditText trace_content;
	private String bsid;
	
	private ListView trace_history;
	private TraceHistoryAdapter hisAdapter;
	private ArrayList<Trace> data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace_mark);
		
		
		Bundle bundle = getIntent().getBundleExtra(Const.BSID);
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

		gridGallery = (GridView) findViewById(R.id.gridGallery);
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
		
		trace_history = (ListView) findViewById(R.id.trace_history);

	}
	
	private void setData(){
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
		}
		
		new TraceMarkTask().execute(all_path);
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

	
	class TraceMarkTask extends AsyncTask<String[], Integer, Boolean>{
		
		private ProgressDialog dialog = null;
		private JSONObject result;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(TraceMarkActivity.this, "", getString(R.string.trace_uploading), true);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String[]... arg0) {
			
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
			dialog = ProgressDialog.show(TraceMarkActivity.this, "", getString(R.string.trace_uploading), true);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String... arg0) {
			
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				
			}
			
			data.clear();
			data.addAll(tempTrace);
			hisAdapter.notifyDataSetChanged();
			dialog.dismiss(); 
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
			Intent intent = new Intent(this, MainActivity.class);            
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
	        startActivity(intent);            
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

}
