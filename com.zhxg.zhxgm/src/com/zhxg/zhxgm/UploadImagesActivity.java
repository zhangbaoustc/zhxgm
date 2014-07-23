package com.zhxg.zhxgm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
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
import com.zhxg.zhxgm.library.CustomMultiPartEntity;
import com.zhxg.zhxgm.library.GalleryAdapter;
import com.zhxg.zhxgm.library.GameFunction;
import com.zhxg.zhxgm.library.CustomMultiPartEntity.ProgressListener;
import com.zhxg.zhxgm.utils.GpsUtils;
import com.zhxg.zhxgm.utils.ImageUtils;
import com.zhxg.zhxgm.utils.Utils;
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
		long totalSize;
		@Override
		protected void onProgressUpdate(Integer... values) {
			dialog.setProgress((int) (values[0]));
		}
		
		
		private ProgressDialog dialog = null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(UploadImagesActivity.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMessage("Uploading Picture...");
			dialog.setCancelable(false);
			dialog.show();
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(String[]... arg0) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("bsid", bsid);
			String imageUploadUrl = "http://app.zhxg.com/index.php?arg=img";
			boolean result = false;
			 try {
				 HttpClient httpClient = new DefaultHttpClient();
				 HttpPost postRequest = new HttpPost(imageUploadUrl);
				 
				 //MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				 CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,new ProgressListener()
					{
						@Override
						public void transferred(long num)
						{
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});
				 
				 
				Iterator<Entry<String, String>> iter = map.entrySet().iterator();
				while(iter.hasNext()){
					Map.Entry entry = (Map.Entry) iter.next(); 
					reqEntity.addPart(entry.getKey().toString(),new StringBody(entry.getValue().toString(),Charset.forName("UTF-8")));
				}
				
				int i = 0;
				for ( String name : arg0[0]){
					File file = new File(name);
					String[] image_info = file.getName().replace(".jpg", "").split("_");
					//new ImageUtils().addWatermark(name,image_info[2],GpsUtils.DDDToDMS(image_info[0]),GpsUtils.DDDToDMS(image_info[1]));
					
					ExifInterface exifInterface = new ExifInterface(name);
					reqEntity.addPart("set["+i+ "][ydot]" ,new StringBody(image_info[0]));
					reqEntity.addPart("set["+i+ "][xdot]" ,new StringBody(image_info[1]));
					reqEntity.addPart("set["+i+ "][pubdate]" ,new StringBody(image_info[2]));
					reqEntity.addPart("set["+i+ "][status]" ,new StringBody(image_info[3]));
					
					String markTime = Utils.getTimeFromUTC(image_info[2], "yyyy-MM-dd HH:mm:ss");
					String markGPS = GpsUtils.DDDToDMS(image_info[0]) + "  " + GpsUtils.DDDToDMS(image_info[1]);
					
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					Bitmap bitmap = BitmapFactory.decodeFile(name,options);
					
					
					bitmap = ImageUtils.mark(bitmap, markGPS,markTime,255, 204, 15, false);
					
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bitmap.compress(CompressFormat.JPEG, 100, bos);
					byte[] data = bos.toByteArray();
					ByteArrayBody bab = new ByteArrayBody(data, Math.floor(Math.random() * 11)+".jpg");
					reqEntity.addPart("set["+i+ "][img]" ,bab);
					i++;
				}
				
				 totalSize = reqEntity.getContentLength();
				 postRequest.setEntity(reqEntity);       
			     HttpResponse response = httpClient.execute(postRequest);
			     BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			     String sResponse;
			     StringBuilder s = new StringBuilder();
			     while ((sResponse = reader.readLine()) != null) {
			         s = s.append(sResponse);
			     }
				 JSONObject jObj = new JSONObject(s.toString());
				 if("TRUE".equals(jObj.getString("flag").toUpperCase())){
					 result = true;
				 }else{
					 result = false;
				 }
			 }catch(Exception es){
				 result = false;
			 }
			
			return result;
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
			finish();           
	        return true;    
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

}
