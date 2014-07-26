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
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); 
        
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
	
	
	//gps format convert
	public static String DDDToDMS(String ddd){
		if(isDDD(ddd)){
			String[] dmsArr = ddd.split("\\.");
			String d1 = dmsArr[0];
			String d2 = (Float.parseFloat("0."+dmsArr[1])*60 + "").split("\\.")[0];
			String d3 = (int)Math.floor((Float.parseFloat("0."+(Float.parseFloat("0."+dmsArr[1])*60 + "").split("\\.")[1])*60))+"";
			return d1 + "."+d2+d3;
		}else{
			return "";
		}
	}
	
	public static String DMSToDDD(String ddd){
		if(isDMS(ddd)){
			String[] dmsArr = ddd.split("\\.");
			int d1 = Integer.parseInt(dmsArr[0]);
			float d2 = 0;
			float d3 = 0;
			if(dmsArr[1] != null){
				if(dmsArr[1].length() >= 2){
					d2 = Float.parseFloat(dmsArr[1].substring(0, 2))/60;
				}
				if(dmsArr[1].length() >2){
					d3 = Float.parseFloat(dmsArr[1].substring(2))/3600;
				}
			}
			
			return d1 + d2 + d3 + "";
		}else{
			return "";
		}
	}
	
	public static boolean isDDD(String ddd){
		try{
			Float.parseFloat(ddd);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	public static boolean isDMS(String dms){
		try{
			Float.parseFloat(dms);
			//if(dms.split("\\.")[1].length()==4){
				return true;
			//}
		}catch(Exception e){
			
		}
		return false;
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
