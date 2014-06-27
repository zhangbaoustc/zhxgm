package com.zhxg.zhxgm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class GpsUtils {
	public GpsUtils(Context context) {
		super();
	}

	public static final boolean isOPen(final Context context) {  
        LocationManager locationManager   
                                 = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
        // ͨ��GPS���Ƕ�λ����λ������Ծ�ȷ���֣�ͨ��24�����Ƕ�λ��������Ϳտ��ĵط���λ׼ȷ���ٶȿ죩  
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        // ͨ��WLAN���ƶ�����(3G/2G)ȷ����λ�ã�Ҳ����AGPS������GPS��λ����Ҫ���������ڻ��ڸ������Ⱥ��ï�ܵ����ֵȣ��ܼ��ĵط���λ��  
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); 
        if(network){
        	Toast.makeText(context, "network open", Toast.LENGTH_LONG).show();
        }else{
        	Toast.makeText(context, "network close", Toast.LENGTH_LONG).show();
        }
        if (gps) {  
            return true;  
        }  
  
        return false;  
    } 
	
	public static final void openGPS(Context mContext){
	    @SuppressWarnings("deprecation")
		String provider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(!provider.contains("gps")){ //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        mContext.sendBroadcast(poke);
	    }
	}
	
	
	public static final void openGPSSetting(Context context){
		 Intent intent = new Intent();  
	     intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
	     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	     try   
	     {  
	    	 context.startActivity(intent);  
	                   
	           
	     } catch(ActivityNotFoundException ex)   
	     {  
	           
	         // The Android SDK doc says that the location settings activity  
	         // may not be found. In that case show the general settings.  
	           
	         // General settings activity  
	         intent.setAction(Settings.ACTION_SETTINGS);  
	         try {  
	        	 context.startActivity(intent);  
	         } catch (Exception e) {  
	         }  
	     }  
	}
	
	
	public static final String getGPSLocation(Context context){
		LocationManager locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);	
		Location loc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		LocationListener locLis = new MyLocationListener(context);
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locLis);
		//locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,locLis);
		if (loc != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sdf.format(new Date(loc.getTime()));
			//return "����:" + loc.getLatitude() + " " + "ά��:" + loc.getLongitude() + " " + "\n" +  "ʱ��:" + date;
			return "ʱ��:" + date;    
		}else{
			loc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(loc != null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String date = sdf.format(new Date(loc.getTime()));
				//return "����:" + loc.getLatitude() + " " + "ά��:" + loc.getLongitude() + " " + "\n" +  "ʱ��:" + date;
				return "ʱ��:" + date;    
			}else{
				return "��ȡ����λ��ʧ��!";
			}
		}
		
	}
	
	public static class MyLocationListener implements LocationListener{

		private Context mContext = null;
		public MyLocationListener(Context context) {
			super();
			mContext = context;
		}

		@Override
		public void onLocationChanged(Location loc) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sdf.format(new Date(loc.getTime()));
			Log.i("dd", date);
			//return "����:" + loc.getLatitude() + " " + "ά��:" + loc.getLongitude() + " " + "\n" +  "ʱ��:" + date;
			Toast.makeText(mContext, "onLocationChanged" + loc.getLongitude(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProviderDisabled(String arg0) {
			Log.i("dd", "onProviderDisabled");
			Toast.makeText(mContext, arg0 + " -- onProviderDisabled", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			Log.i("dd", "onProviderEnabled");
			Toast.makeText(mContext, arg0 +  " -- onProviderEnabled", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			Log.i("dd", "onStatusChanged");
			Toast.makeText(mContext, arg0 + " -- onStatusChanged", Toast.LENGTH_LONG).show();
		}
		
	}
}
