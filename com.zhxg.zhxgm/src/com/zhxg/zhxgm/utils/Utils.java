package com.zhxg.zhxgm.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zhxg.zhxgm.R;
import com.zhxg.zhxgm.vo.Const;

public class Utils {

	public static boolean checkUserName(String username){		
		return username.matches("^[a-zA-Z][a-zA-Z0-9_]{4,16}[a-zA-Z0-9]$");
	}
	
	public static boolean checkMobile(String mobile){		
		return mobile.matches("\\d{11}");
	}
	
	
	//get game type name from game type
	public static String getGameTypeNameByType(Context context,int type){
		String[] type_name = context.getResources().getStringArray(R.array.game_type_array);
		int[] type_num = context.getResources().getIntArray(R.array.game_type_array_num);
		String result="";
		for(int i=0;i<type_num.length;i++){
			if(type_num[i] == type){
				result = type_name[i];
				break;
			}
		}
		return result;
	}
	
	public static String getUTCTime(String time,String formatStr){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		try {
			c.setTime(format.parse(time));
		} catch (ParseException e) {
			Log.i(Const.TAG, "getUTCTime format error");
		}
		return c.getTimeInMillis()+""; 
	}
	
	public static String getTimeFromUTC(String time,String formatStr){
		Date date = new Date(Long.parseLong(time));
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		
		return format.format(date);	
	}
	 
	
	//get game garget id from game type
	public static String getGameTargetIDByPosition(Context context,int position){
		SharedPreferences pref = context.getSharedPreferences("loginSession", 0); 
		String[] target_ids = pref.getString(Const.TARGETID, "").split(",");
		return target_ids[position];
	}
	
	public static String getGameTypeByPosition(Context context,int position){
		SharedPreferences pref = context.getSharedPreferences("loginSession", 0); 
		String[] target_ids = pref.getString(Const.ROLE, "").split(",");
		return target_ids[position];
	}
	
	
	//get game type by user role
	public static ArrayList<String> getTypeByRole(Context context, String role){
		
		String[] type_name = context.getResources().getStringArray(R.array.game_type_array);
		int[] type_num = context.getResources().getIntArray(R.array.game_type_array_num);
		ArrayList<String> result = new ArrayList<String>();
		String[] roles = role.split(",");
		
		for(int i=0;i<type_num.length;i++){
			for(int j=0;j<roles.length;j++){
				if(type_num[i] == Integer.parseInt(roles[j])){
					result.add(type_name[i]);				
				}
			}
		}
		
		return result;
	}
	
	
	public static String ArrayListToString(ArrayList<String> arr){
		String result="";
		for(int i=0;i<arr.size();i++){
			if(!arr.get(i).equals("")){
				result += arr.get(i).toString()+",";
			}
		}
		
		return result.equals("")?"":result.substring(0, result.length()-1);
	}
	
}
