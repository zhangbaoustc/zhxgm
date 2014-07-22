package com.zhxg.zhxgm.control;

import java.io.IOException;
import java.util.List;

import com.zhxg.zhxgm.library.MyCamPara;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraPreview extends SurfaceView  implements SurfaceHolder.Callback{

	public static final String TAG = "zhxgm";
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Parameters mParameters;
	private Context mContext;
	
	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;
		mContext = context;
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
			
			if (Build.VERSION.SDK_INT >= 8) 
				 mCamera.setDisplayOrientation(90);
			 
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {			
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}		
	}
	
	
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int width, int height) {
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
			
			WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
			Display display  = wm.getDefaultDisplay();//得到当前屏幕
			 mParameters = mCamera.getParameters();
		        if (isSupportedFlashMode()) {// 需要判断是否支持闪光灯
		            mParameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
		        }
		        mParameters.setPictureFormat(PixelFormat.JPEG);
		        mParameters.setJpegQuality(100);
		        Size pictureS = MyCamPara.getInstance().getPictureSize(mParameters.getSupportedPictureSizes(), 800);
		        mParameters.setPictureSize(pictureS.width, pictureS.height);
		    mCamera.setParameters(mParameters);

			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch ( Exception e ){
			Log.d(TAG, "Error starting camera preview:" + e.getMessage());
		}
		
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		if(mCamera != null){
			mCamera.stopPreview(); 
			mCamera.release();
			mCamera = null;
		}
	}
	
	 public boolean isSupportedFlashMode() {
        if (mCamera == null) {
            mCamera = Camera.open();
        }
        Parameters parameters = mCamera.getParameters();
        List<String> modes = parameters.getSupportedFlashModes();
        if (modes != null && modes.size() != 0) {
            boolean autoSupported = modes.contains(Parameters.FLASH_MODE_AUTO);
            boolean onSupported = modes.contains(Parameters.FLASH_MODE_ON);
            boolean offSupported = modes.contains(Parameters.FLASH_MODE_OFF);
            return autoSupported && onSupported && offSupported;
        }
        return false;
    }

}
