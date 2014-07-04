package com.zhxg.zhxgm.utils;

public class Utils {

	public static boolean checkUserName(String username){		
		return username.matches("^[a-zA-Z][a-zA-Z0-9_]{4,16}[a-zA-Z0-9]$");
	}
	
	public static boolean checkMobile(String mobile){		
		return mobile.matches("\\d{11}");
	}
}
