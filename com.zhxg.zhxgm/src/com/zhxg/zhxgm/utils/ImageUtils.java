package com.zhxg.zhxgm.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.baidu.location.BDLocation;

public class ImageUtils {

	public ImageUtils() {
		super();		
	}
	
	
	public void addWatermark(Context context,String filePath,BDLocation mLocation){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		Point point = new Point(60, 80); 
		
		String markStr = mLocation.getTime() + " : " + mLocation.getLatitude() + " : " + mLocation.getLongitude();
		
		Bitmap afterMark = mark(bitmap, markStr, point, 255, 1, 15, false);
		BitmapTofile(afterMark,filePath);
	}
	
	public static Bitmap mark(Bitmap src, String watermark, Point location, int color, int alpha, int size, boolean underline) {
	    int w = src.getWidth();
	    int h = src.getHeight();
	    Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
	 
	    Canvas canvas = new Canvas(result);
	    canvas.drawBitmap(src, 0, 0, null);
	 
	    Paint paint = new Paint();
	    paint.setColor(Color.RED);
	    //paint.setAlpha(alpha);
	    //paint.setTextSize(size);
	    paint.setAntiAlias(true);
	    paint.setUnderlineText(underline);
	    canvas.drawText(watermark, location.x, location.y, paint);
	 
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
}
