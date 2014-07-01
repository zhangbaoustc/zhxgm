package com.zhxg.zhxgm.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.zhxg.zhxgm.vo.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserFunction {
	private JSONParser jsonParser;
	private String loginUrl = "http://zhxg.com/api/apk/login.php";
	public UserFunction() {
		jsonParser = new JSONParser();
	}

	public JSONObject loginUser(String userName,String password){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user", userName));
		params.add(new BasicNameValuePair("psd", password));
		JSONObject jObj = jsonParser.getJSONFromUrl(loginUrl, params, "POST");
		return jObj;
	}
	
	public static void loginOK(Context context,String username,String password)
	{
		SharedPreferences pref = context.getSharedPreferences("loginSession", 0); 
		Editor editor = pref.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.commit();
	}
	
	public static User getSessionUser(Context context){
		User user = new User();
		SharedPreferences pref = context.getSharedPreferences("loginSession", 0); 
		user.setUserName(pref.getString("username", ""));
		user.setPassword(pref.getString("password", ""));
		return user;
	}
	
}
