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
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zhxg.zhxgm.library.Action;
import com.zhxg.zhxgm.library.CustomGallery;
import com.zhxg.zhxgm.library.GalleryAdapter;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.vo.Const;

public class UploadImagesActivity extends Activity {

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgSinglePick;
	Button btnGalleryPickMul;
	Button btnUploadImage;

	String action;
	ImageLoader imageLoader;
	String[] all_path;
	String bsid;
	String status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_images);
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		if(getIntent() != null && getIntent().getExtras() != null){
			bsid = getIntent().getStringExtra(Const.GAME_ID);
			status = getIntent().getStringExtra(Const.GAME_STATUS);
		}
		
		initImageLoader();
		init();
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

	private void init() {

		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);
		gridGallery.setAdapter(adapter);

		imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);
		btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
		btnGalleryPickMul.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
				i.putExtra(Const.GAME_ID, bsid);
				i.putExtra(Const.GAME_STATUS, status);
				startActivityForResult(i, 200);
			}
		});
		
		btnUploadImage = (Button) findViewById(R.id.btnUpload);
		btnUploadImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new FileuploadTask().execute(all_path);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
		    all_path = data.getStringArrayExtra("all_path");

			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

			for (String string : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;
				dataT.add(item);
			}

			adapter.addAll(dataT);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.upload_images, menu);
		return true;
	}
	
	
	
	class FileuploadTask extends AsyncTask<String[], Integer, Boolean>{
		
		private ProgressDialog dialog = null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(UploadImagesActivity.this, "", "Uploading file...", true);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String[]... arg0) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("bsid", bsid);
			//map.put("status", status);
			
			return new GameFunction().uploadImages( map, arg0[0]);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result == true){
				UploadImagesActivity.this.finish();
				Toast.makeText(UploadImagesActivity.this, getString(R.string.upload_images_success), Toast.LENGTH_LONG);
			}else{
				Toast.makeText(UploadImagesActivity.this, getString(R.string.upload_images_error), Toast.LENGTH_LONG);
			}
			dialog.dismiss(); 
		}
		
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
