package com.zhxg.zhxgm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.zhxg.zhxgm.control.CameraPreview;
import com.zhxg.zhxgm.utils.GpsUtils;
import com.zhxg.zhxgm.utils.ImageUtils;
import com.zhxg.zhxgm.utils.Utils;
import com.zhxg.zhxgm.vo.Const;

public class CameraActivity extends BaseActivity {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler mHandler;
	private int continueCameraCount = 1;
	private int takePicInteval = 2000;
	private BDLocationListener myLocationListener;
	private LocationClient mLocationClient;
	private ProgressBar progressBar;
	private TextView gps_load_text;
	private BDLocation mLocation;
	private Button captureButton;
	private TextView image_save_text;
	private String style;
	private static String bsid;
	private static String status;
	private boolean safeToTakePicture = true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		//back
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		if(getIntent() != null){
			style = getIntent().getStringExtra(Const.CAMERA_STYLE);
			bsid = getIntent().getStringExtra(Const.GAME_ID);
			status = getIntent().getStringExtra(Const.GAME_STATUS);
		}
		
		
		//get gps info
		myLocationListener = new MyLocationListener();
		mLocationClient = new LocationClient(getApplicationContext());     
		mLocationClient.registerLocationListener( myLocationListener );    
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(false);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		
		
		mHandler = new Handler();
		mCamera = getCameraInstance();
		mHandler.postDelayed(loadPreview, 500);
		
		captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				captureButton.setEnabled(false);
				if(style.equals(Const.NORMAL)){
					continueCameraCount = 1;
					takePicInteval = 100;
					mHandler.postDelayed(takePicRunnable, takePicInteval);
				}else{
					continueCameraCount = 5;
					takePicInteval = 2000;
					mHandler.postDelayed(takePicRunnable, takePicInteval);
				}
			}
		});
		
		
		progressBar = (ProgressBar) findViewById(R.id.camera_gps_load_progressbar);
		gps_load_text = (TextView) findViewById(R.id.camera_gps_load_text);
		image_save_text = (TextView) findViewById(R.id.image_save_text);
		
		
		if(!GpsUtils.isOPen(this)){
			new GpsUtils(this);
			GpsUtils.openGPSSetting(this);
		}
		beforeTakePic();
	}
	
	

	
	private Runnable loadPreview = new Runnable() {
		
		@Override
		public void run() {
			mPreview = new CameraPreview(CameraActivity.this,mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mPreview);
		}
	};
	

	private final ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };
	
	private Runnable takePicRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(safeToTakePicture){
				safeToTakePicture = false;
				if(continueCameraCount>0){
					mCamera.takePicture(shutterCallback, null, mPicture);
					continueCameraCount--;
					mHandler.postDelayed(this, takePicInteval);
				}else{
					continueCameraCount= 1;
				}
			
				captureButton.setEnabled(true);
			}
		}
	};

	
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

			image_save_text.setVisibility(View.VISIBLE);
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

		    if (pictureFile.exists()) {
		        pictureFile.delete();
		    }

		    try {
		        FileOutputStream fos = new FileOutputStream(pictureFile);

		        Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

		        ExifInterface exif=new ExifInterface(pictureFile.toString());

		        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
		        if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
		            realImage= rotate(realImage, 90);
		        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
		            realImage= rotate(realImage, 270);
		        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
		            realImage= rotate(realImage, 180);
		        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
		            realImage= rotate(realImage, 90);
		        }

		        boolean bo = realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

		        fos.close();

		       // ((ImageView) findViewById(R.id.imageview)).setImageBitmap(realImage);
		        new ImageUtils().exportToGallery(CameraActivity.this, pictureFile.toString());
		        Log.d("Info", bo + "");

		    } catch (FileNotFoundException e) {
		        Log.d("Info", "File not found: " + e.getMessage());
		    } catch (IOException e) {
		        Log.d("TAG", "Error accessing file: " + e.getMessage());
		    }finally{
		    	 safeToTakePicture = true;
				 image_save_text.setVisibility(View.GONE);
		    }
	    }
	};

	
	public static Bitmap rotate(Bitmap bitmap, int degree) {
		    int w = bitmap.getWidth();
		    int h = bitmap.getHeight();

		    Matrix mtx = new Matrix();
		   //       mtx.postRotate(degree);
		    mtx.setRotate(degree);

		    return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
		}

	
	/** Create a File for saving an image or video */
	private File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "ZHXG");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("ZHXG", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	    	String fileName = mLocation.getLatitude() + "_" + mLocation.getLongitude() + "_" +
	        		Utils.getUTCTime(mLocation.getTime(), "yyyy-MM-dd HH:mm:ss") + "_" + status + "_" + bsid;
	    	fileName = Utils.StrReverse(fileName) + ".jpg";
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
	    	//mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
	    }else {
	        return null;
	    }

	    return mediaFile;
	}

	
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
		            return ;
			mLocation = location;
			readyForTakePic();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mCamera.stopPreview();
			this.finish();
	        return true;    
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	//should add time and gps to image, so should get the info before take picture
	private void beforeTakePic(){
		progressBar.setVisibility(View.VISIBLE);
		gps_load_text.setVisibility(View.VISIBLE);
		captureButton.setVisibility(View.GONE);
		image_save_text.setVisibility(View.GONE);
	}
	
	private void readyForTakePic(){
		progressBar.setVisibility(View.GONE);
		gps_load_text.setVisibility(View.GONE);
		captureButton.setVisibility(View.VISIBLE);
		image_save_text.setVisibility(View.GONE);
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCamera.release();
		mLocationClient.stop();
	}

	@Override
	public void onBackPressed() {
		mCamera.stopPreview();
		super.onBackPressed();
	}
	
}
