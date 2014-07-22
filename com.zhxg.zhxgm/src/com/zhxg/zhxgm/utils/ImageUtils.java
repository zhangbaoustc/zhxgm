package com.zhxg.zhxgm.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.baidu.location.BDLocation;

public class ImageUtils {

	public ImageUtils() {
		super();		
	}
	
	
	public void addExif(String filePath, BDLocation location){
		try {
			ExifInterface exif = new ExifInterface(filePath);
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, location.getLatitude()+"");
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, location.getLongitude()+"");

			exif.setAttribute(ExifInterface.TAG_DATETIME,Utils.getUTCTime(location.getTime(), "yyyy-MM-dd HH:mm:ss"));
			exif.saveAttributes();
			
			exif.getAttribute(ExifInterface.TAG_DATETIME);
		} catch (IOException e) {
			Log.i("zhxg", "些人exif信息失败");
		}
		
		
	}
	
	public static Bitmap mark(Bitmap src, String watermarkGPS,String watermarkTime, int color, int alpha, int size, boolean underline) {
	    int w = src.getWidth();
	    int h = src.getHeight();
	    Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
	 
	    Canvas canvas = new Canvas(result);
	    canvas.drawBitmap(src, 0, 0, null);
	 
	    Paint paint = new Paint();
	    paint.setColor(Color.WHITE);
	    paint.setTextSize(36);
	    paint.setAlpha(alpha);
	    paint.setAntiAlias(true);
	    paint.setUnderlineText(underline);
	    
	    Paint paint1 = new Paint();
	    paint1.setColor(Color.WHITE);
	    paint1.setTextSize(24);
	    paint1.setAlpha(alpha);
	    paint1.setAntiAlias(true);
	    paint1.setUnderlineText(underline);
	    
	    canvas.drawText(watermarkTime, 45, h - 60, paint);
	    canvas.drawText(watermarkGPS, 45, h - 20, paint1);
	 
	    return result;
	}
	

	public void BitmapTofile(Bitmap bmp, String filename){
		FileOutputStream out = null;
		File mFile = new File(filename);
		if(mFile.exists()){
			mFile.delete();
		}
		try {
		       out = new FileOutputStream(filename);
		       bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		       try{
		    	   out.flush();
		           out.close();
		       } catch(Throwable ignore) {}
		}
	}
	
	 public static Bitmap getBitmapFromURL(String src) {
	        try {
	            Log.e("src",src);
	            URL url = new URL(src);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoInput(true);
	            connection.connect();
	            InputStream input = connection.getInputStream();
	            Bitmap myBitmap = BitmapFactory.decodeStream(input);
	            Log.e("Bitmap","returned");
	            return myBitmap;
	        } catch (IOException e) {
	            e.printStackTrace();
	            Log.e("Exception",e.getMessage());
	            return null;
	        }
	    }
	 
	 public Uri exportToGallery(Context mContext,String filename) {
	        // Save the name and description of a video in a ContentValues map.
	        final ContentValues values = new ContentValues(2);
	        values.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
	        values.put(MediaStore.Video.Media.DATA, filename);
	        // Add a new record (identified by uri)
	        final Uri uri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	                values);
	        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
	                Uri.parse("file://"+ filename)));
	        return uri;
	    }
}
