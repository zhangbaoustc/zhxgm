package com.zhxg.zhxgm.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {

	private static final String TAG = "MainService";  
	 
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "----onBind-----");  
		return null;
	}
	
	

}
