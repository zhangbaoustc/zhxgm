package com.zhxg.zhxgm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.zhxg.zhxgm.control.CameraPreview;
import com.zhxg.zhxgm.utils.GpsUtils;
import com.zhxg.zhxgm.utils.ImageUtils;

public class CameraActivity extends BaseActivity {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler mHandler;
	private int continueCameraCount = 6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		mHandler = new Handler();
		mCamera = getCameraInstance();
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		
		
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mHandler.postDelayed(takePicRunnable, 2000);
				
				//mCamera.takePicture(null, null, mPicture);				
			}
		});
		
		if(!GpsUtils.isOPen(this)){
			new GpsUtils(this);
			GpsUtils.openGPSSetting(this);
		}
		
		
		LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);	
		List<String> providers = locMan.getAllProviders();
		for(Iterator iterator = providers.iterator();iterator.hasNext();){
		    String provider = (String)iterator.next();
		    System.out.println(provider);
		    // and do something you need
		}
		
	}
	
	
	
	private Runnable takePicRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(continueCameraCount>0){
				mCamera.takePicture(null, null, mPicture);
				continueCameraCount--;
				mHandler.postDelayed(this, 2000);
			}else{
				continueCameraCount= 6;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}
	
	
	//check if the device has a camera
	private boolean checkCameraHardware(Context context){
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
		{
			return true;
		}else{
			return false;
		}
	}
	
	//get instance of the camera object
	public static Camera getCameraInstance(){
		Camera c = null;
		try{
			c = Camera.open();
		}
		catch (Exception e){
			
		}
		return c;
	}
	
	
	private PictureCallback mPicture = new PictureCallback() {

		@Override
	    public void onPictureTaken(byte[] data, Camera camera) {

	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            Log.d(CameraPreview.TAG, "Error creating media file, check storage permissions");
	            return;
	        }

	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	            ImageUtils imgUtils = new ImageUtils();
		        imgUtils.addWatermark(CameraActivity.this,pictureFile.toString());
	          // new getServerTimeTask().execute(pictureFile.toString());
	        } catch (FileNotFoundException e) {
	            Log.d(CameraPreview.TAG, "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d(CameraPreview.TAG, "Error accessing file: " + e.getMessage());
	        }
	    }
	};
	
	
	class getServerTimeTask  extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			ImageUtils imgUtils = new ImageUtils();
	        imgUtils.addWatermark(CameraActivity.this,params[0]);
			return null;
		}
		
	}
	

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}

}
