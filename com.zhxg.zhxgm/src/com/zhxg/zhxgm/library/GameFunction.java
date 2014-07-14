package com.zhxg.zhxgm.library;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

import com.zhxg.zhxgm.vo.Game;

public class GameFunction {
	private JSONParser jsonParser;
	private String gameInfoUrl = "http://app.zhxg.com/index.php?arg=bslist";
	private String addGameUrl = "http://app.zhxg.com/index.php?arg=bsadd";
	
	public GameFunction(){
		jsonParser = new JSONParser();
	}
	
	public JSONObject getGames(String targetid)
	{
		String url = gameInfoUrl + "&targetid="+targetid;
		JSONObject jObj = jsonParser.getJSONFromUrl(url,null, "GET");
		return jObj;
	}
	
	
	public JSONObject addGame(Game game){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("target_id", game.getTargetID()));
		params.add(new BasicNameValuePair("title", game.getName()));
		params.add(new BasicNameValuePair("bsdate", game.getName()));
		params.add(new BasicNameValuePair("distince", game.getDistance()));
		params.add(new BasicNameValuePair("ji_address", game.getJgAddress()));
		params.add(new BasicNameValuePair("ji_wd", game.getJgLatitude())); 
		params.add(new BasicNameValuePair("ji_jd", game.getJgLongitude()));
		params.add(new BasicNameValuePair("ji_date", game.getJgDate()));
		params.add(new BasicNameValuePair("fly_address", game.getFlyAddress()));
		params.add(new BasicNameValuePair("fly_jd", game.getFlyLongitude()));
		params.add(new BasicNameValuePair("fly_wd", game.getFlyLatitude()));
		params.add(new BasicNameValuePair("fly_date ", game.getFlyDate()));
		
		JSONObject jObj = jsonParser.getJSONFromUrl(addGameUrl,params, "POST");
		return jObj;
	}
	
	
	//添加行程记录
	public static boolean addTraceMark(String url, HashMap<String, String> info, String[] files){
		boolean result = false;

		 try {
			 HttpClient httpClient = new DefaultHttpClient();
			 HttpPost postRequest = new HttpPost(url);
			 MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			 
			Iterator<Entry<String, String>> iter = info.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next(); 
				reqEntity.addPart(entry.getKey().toString(),new StringBody(entry.getValue().toString()));
			}
			
			int i = 0;
			for ( String name : files){
				Bitmap bitmap = BitmapFactory.decodeFile(name);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 25, bos);
				byte[] data = bos.toByteArray();
				ByteArrayBody bab = new ByteArrayBody(data, Math.floor(Math.random() * 11)+".jpg");
				reqEntity.addPart("image"+i, bab);
				i++;
			}
			
			 postRequest.setEntity(reqEntity);       
		     HttpResponse response = httpClient.execute(postRequest);
		     BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		     String sResponse;
		     StringBuilder s = new StringBuilder();
		     while ((sResponse = reader.readLine()) != null) {
		         s = s.append(sResponse);
		     }
			 JSONObject jObj = new JSONObject(s.toString());
			 if("TRUE".equals(jObj.getString("flag".toUpperCase()))){
				 result = true;
			 }else{
				 result = false;
			 }
		 }catch(Exception es){
			 result = false;
		 }
		
		
		return result; 
	}
	
	public ArrayList<Game> gameConvert(JSONArray arr){
		ArrayList<Game> data = new ArrayList<Game>();
		for(int i=0;i<arr.length();i++){
			Game game = new Game();
			try {
				JSONObject obj = arr.getJSONObject(i);
				game.setName(obj.getString("title") !=null?obj.getString("title"):"");
				game.setId(obj.getString("id") != null?obj.getString("id"):"");
				game.setTargetID(obj.getString("target_id") !=null?obj.getString("target_id"):"");
				game.setDate(obj.getString("bsdate") !=null?obj.getString("bsdate"):"");
				game.setDistance(obj.getString("distince") !=null?obj.getString("distince"):"");
				game.setBonus(obj.getString("bonus") !=null?obj.getString("bonus"):"");
				
				game.setJgDate(obj.getString("ji_date") !=null?obj.getString("ji_date"):"");
				game.setJgLatitude(obj.getString("ji_wd") !=null?obj.getString("ji_wd"):"");
				game.setJgLongitude(obj.getString("ji_jd") !=null?obj.getString("ji_jd"):"");
				game.setJgAddress(obj.getString("ji_address") !=null?obj.getString("ji_address"):"");
				
				game.setFlyAddress(obj.getString("fly_address") !=null?obj.getString("fly_address"):"");
				game.setFlyDate(obj.getString("fly_date") !=null?obj.getString("fly_date"):"");
				game.setFlyLatitude(obj.getString("fly_wd") !=null?obj.getString("fly_wd"):"");
				game.setFlyLongitude(obj.getString("fly_jd") !=null?obj.getString("fly_jd"):"");
				
				game.setType(obj.getString("type") !=null?obj.getString("type"):"0");
				
				data.add(game);
				
			} catch (JSONException e) {
				
			}
		}
		return data;
	}
}
