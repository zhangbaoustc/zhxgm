package com.zhxg.zhxgm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.zhxg.zhxgm.utils.ImageUtils;
import com.zhxg.zhxgm.vo.Const;

public class SingleImageShowActivity extends Activity {

	private ImageView iv;
	private String imageUrl;
	private ProgressBar image_loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_single_image_show);		
		
		iv = (ImageView) findViewById(R.id.iv);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SingleImageShowActivity.this.finish();
			}
		});
		
		image_loading = (ProgressBar) findViewById(R.id.image_loading);
		if(getIntent() != null){
			imageUrl = getIntent().getStringExtra(Const.IMAGE_INFO);
		}
		
		if(imageUrl != null) 
			 new loadImagesTask().execute(imageUrl);
	}
	
	
	public class loadImagesTask extends AsyncTask<String, Void, Bitmap> {
		@SuppressLint("DefaultLocale")
		@Override
		protected Bitmap doInBackground(String... params) {
			return ImageUtils.getBitmapFromURL(params[0],2);
		}

		@Override
		protected void onPostExecute(Bitmap bit) {
		   image_loading.setVisibility(View.GONE);
		   iv.setVisibility(View.VISIBLE);
		   iv.setImageBitmap(bit);
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.single_image_show, menu);
		return true;
	}

	
}
