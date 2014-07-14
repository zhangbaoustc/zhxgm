package com.zhxg.zhxgm;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zhxg.zhxgm.library.Action;
import com.zhxg.zhxgm.library.CustomGallery;
import com.zhxg.zhxgm.library.GalleryAdapter;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.library.ImageSelectActivity;
import com.zhxg.zhxgm.vo.Const;

public class TraceMarkActivity extends Activity {

	GridView gridGallery;
	GalleryAdapter adapter;
	
	Button image_select;
	Button trace_send;
	
	ImageLoader imageLoader;
	String[] all_path;
	EditText trace_content;
	private String bsid;
	
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

	
	class TraceMarkTask extends AsyncTask<String[], Integer, Void>{
		
		private ProgressDialog dialog = null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(TraceMarkActivity.this, "", "信息发布中...", true);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(String[]... arg0) {
			String url = "http://hefeihua.com/uploadImage.php";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("bsid", bsid);
			map.put("info", trace_content.getText().toString());
			map.put("userid", "userid");
			
			GameFunction.addTraceMark(url, map, arg0[0]);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss(); 
		}
		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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

}
