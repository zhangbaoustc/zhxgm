package com.zhxg.zhxgm.utils;

public class Utils {

	public static boolean checkUserName(String username){		
		return username.matches("^[a_zA_Z](.{4}|.{16})[a_zA_Z0_9]$");
	}
	
	public static boolean checkMobile(String mobile){		
		return mobile.matches("\\d{11}");
	}
}
