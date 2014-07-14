package com.zhxg.zhxgm.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhxg.zhxgm.R;
import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint.Join;

public class UserFunction {
	private JSONParser jsonParser;
	private String loginUrl = "http://zhxg.com/api/apk/login.php?rnd=" + Math.random()*100;
	private String registerUrl = "http://zhxg.com/api/apk/reg.php";
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
	
	
	public JSONObject registerUser(String userName,String mobile,String password,String confirm_password){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("psd", password));
		params.add(new BasicNameValuePair("psd2", confirm_password));
		JSONObject jObj = jsonParser.getJSONFromUrl(registerUrl, params, "POST");
		return jObj;
	}
	
	public static String getUserInfo(Context context, String column){
		SharedPreferences pref = context.getSharedPreferences("loginSession", 0);
		return pref.getString(column, "");
	}
	
	
	public static boolean isManagerOfGame(Context context){
		int[] aRoles = context.getResources().getIntArray(R.array.game_type_array_num);
		SharedPreferences pref = context.getSharedPreferences("loginSession", 0);
		String[] roles = pref.getString(Const.ROLE, "").split(",");
		String cp = pref.getString(Const.CP, "-1");
		
		for(String role:roles){
			for(int aRole:aRoles){
				if(role.equals(aRole+"")){
					return  true;
				}
			}
		}
		
		if(Integer.parseInt(cp)>0){
			return true;
		}
		
		return false;
		
	}
	
	public static void loginOK(Context context,String username,String password,JSONObject obj)
	{
		
		
		SharedPreferences pref = context.getSharedPreferences("loginSession", 0); 
		Editor editor = pref.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		try {
			
			
			JSONArray roles = obj.getJSONArray(Const.ROLE);
			
			String 	role_id = "";
			String targetid = "";
			for(int i=0;i<roles.length();i++){
				if(i == 0){
					role_id = roles.getJSONObject(i).getString(Const.ROLE_ID);
					targetid = roles.getJSONObject(i).getString(Const.TARGETID);
				}else{
					role_id = role_id + "," +  roles.getJSONObject(i).getString(Const.ROLE_ID);
					targetid = targetid + "," + roles.getJSONObject(i).getString(Const.TARGETID);
				}
			}
			
			editor.putString(Const.ROLE, role_id);
			editor.putString(Const.TARGETID, targetid);
			
			editor.putString(Const.CP, obj.getString(Const.CP));
			
			JSONObject user = obj.getJSONObject(Const.USER);
			editor.putString(Const.USER_ID, user.getString(Const.USER_ID));
			editor.putString(Const.MOBILE, user.getString(Const.MOBILE));
			editor.putString(Const.REALNAME, user.getString(Const.REALNAME));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
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

