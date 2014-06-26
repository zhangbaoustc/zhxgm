package com.zhxg.zhxgm.control;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView  implements SurfaceHolder.Callback{

	public static final String TAG = "zhxgm";
	private SurfaceHolder mHolder;
	private Camera mCamera;
	
	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;
		
		// install a SurfaceHolder.Callback so we get notified when underlying
		// surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// the surface has been created, now tell the camera where to draw the preview
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {			
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// if your preview can change or rotate, take care of those events here.
		// make sure to stop the preview before resizing or reformatting it.
		
		//preview surface does not exist
		if(mHolder.getSurface() == null){
			return;
		}
		
		//stop preview before making changes
		try{
			mCamera.stopPreview();
		} catch(Exception e){
			Log.d(TAG, "Error stopPreview camera preview:" + e.getMessage());
		}
		
		//make any resize, rotate or reformatting changes here
		
		//start preview with new settings
		try{
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch ( Exception e ){
			Log.d(TAG, "Error starting camera preview:" + e.getMessage());
		}
		
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

}
