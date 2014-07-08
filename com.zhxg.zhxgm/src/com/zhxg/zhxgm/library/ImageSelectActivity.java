package com.zhxg.zhxgm.library;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
import com.zhxg.zhxgm.R;

public class ImageSelectActivity extends Activity {

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgSinglePick;
	Button btnGalleryPick;
	Button btnGalleryPickMul;
	Button btnUploadImage;

	String action;
	ViewSwitcher viewSwitcher;
	ImageLoader imageLoader;
	String[] all_path;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_select);

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

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		viewSwitcher.setDisplayedChild(1);

		imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);

		btnGalleryPick = (Button) findViewById(R.id.btnGalleryPick);
		btnGalleryPick.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Action.ACTION_PICK);
				startActivityForResult(i, 100);

			}
		});

		btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
		btnGalleryPickMul.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
				startActivityForResult(i, 200);
			}
		});
		
		btnUploadImage = (Button) findViewById(R.id.btnUpload);
		btnUploadImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for(int i=0;i<all_path.length;i++){
					new FileuploadTask().execute(all_path[i],"http://hefeihua.com/uploadImage.php");
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			adapter.clear();

			viewSwitcher.setDisplayedChild(1);
			String single_path = data.getStringExtra("single_path");
			imageLoader.displayImage("file://" + single_path, imgSinglePick);

		} else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
		    all_path = data.getStringArrayExtra("all_path");

			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

			for (String string : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;
				dataT.add(item);
			}

			viewSwitcher.setDisplayedChild(0);
			adapter.addAll(dataT);
		}
	}
	
	class FileuploadTask extends AsyncTask<String, Integer, Void>{

		private ProgressDialog dialog = null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(ImageSelectActivity.this, "", "Uploading file...", true);
		}
		@Override
		protected Void doInBackground(String... arg0) {
			 String fileName = arg0[0];
			 
	          HttpURLConnection conn = null;
	          DataOutputStream dos = null;  
	          String lineEnd = "\r\n";
	          String twoHyphens = "--";
	          String boundary = "*****";
	          int bytesRead, bytesAvailable, bufferSize;
	          byte[] buffer;
	          int maxBufferSize = 1 * 1024 * 1024; 
	          File sourceFile = new File(arg0[0]); 
	          
	          if (!sourceFile.isFile()) {
		           dialog.dismiss(); 
	          }
	          else
	          {
		           try { 
		               FileInputStream fileInputStream = new FileInputStream(sourceFile);
		               URL url = new URL(arg0[1]);
		               
		               // Open a HTTP  connection to  the URL
		               conn = (HttpURLConnection) url.openConnection(); 
		               conn.setDoInput(true); // Allow Inputs
		               conn.setDoOutput(true); // Allow Outputs
		               conn.setUseCaches(false); // Don't use a Cached Copy
		               conn.setRequestMethod("POST");
		               conn.setRequestProperty("Connection", "Keep-Alive");
		               conn.setRequestProperty("ENCTYPE", "multipart/form-data");
		               conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		               conn.setRequestProperty("uploaded_file", fileName); 
		               
		               dos = new DataOutputStream(conn.getOutputStream());
		     
		               dos.writeBytes(twoHyphens + boundary + lineEnd); 
		               dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
		            		                     + fileName + "\"" + lineEnd);
		               
		               dos.writeBytes(lineEnd);
		     
		               // create a buffer of  maximum size
		               bytesAvailable = fileInputStream.available(); 
		     
		               bufferSize = Math.min(bytesAvailable, maxBufferSize);
		               buffer = new byte[bufferSize];
		     
		               // read file and write it into form...
		               bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
		                 
		               while (bytesRead > 0) {
		            	   
		                 dos.write(buffer, 0, bufferSize);
		                 bytesAvailable = fileInputStream.available();
		                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
		                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
		                 
		                }
		     
		               // send multipart form data necesssary after file data...
		               dos.writeBytes(lineEnd);
		               dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		     
		               // Responses from the server (code and message)
		               int serverResponseCode = conn.getResponseCode();
		               String serverResponseMessage = conn.getResponseMessage();
		               
		               if(serverResponseCode == 200){
		                    Toast.makeText(ImageSelectActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
		               }    
		               
		               //close the streams //
		               fileInputStream.close();
		               dos.flush();
		               dos.close();
		                
		          } catch (MalformedURLException ex) {
		              ex.printStackTrace();
		          } catch (Exception e) {
		              e.printStackTrace();
		          }
		           		          
	           } // End else block 
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss(); 
		}
		
		
	}
}
