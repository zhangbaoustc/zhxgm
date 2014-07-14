package com.zhxg.zhxgm.utils;

import java.util.ArrayList;

import com.zhxg.zhxgm.R;

import android.content.Context;

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
	
	
}
