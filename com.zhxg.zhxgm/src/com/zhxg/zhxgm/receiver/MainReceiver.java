package com.zhxg.zhxgm.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zhxg.zhxgm.service.MainService;

public class MainReceiver extends BroadcastReceiver {
	
	private static final String TAG = "MainBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {		
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			long interval = 60000;
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		    PendingIntent pi = PendingIntent.getService(context, 0, new Intent(context, MainService.class), PendingIntent.FLAG_UPDATE_CURRENT);
		    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pi);
			Log.i(TAG, "------- boot start service ----------------");
		}
	}

}
