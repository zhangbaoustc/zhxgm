package com.zhxg.zhxgm.library;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
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
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.widget.RadioGroup;

import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.Game;

public class GameFunction {
	private JSONParser jsonParser;
	private String gameInfoUrl = "http://app.zhxg.com/index.php?arg=bslist";
	private String addOrUpdateGameUrl = "http://app.zhxg.com/index.php?arg=bsadd";
	
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
		params.add(new BasicNameValuePair("targetid", game.getTargetID()));
		params.add(new BasicNameValuePair("title", game.getName()));
		params.add(new BasicNameValuePair("bsdate", game.getDate()));
		params.add(new BasicNameValuePair("bonus", game.getBonus()));
		params.add(new BasicNameValuePair("distance", game.getDistance()));
		params.add(new BasicNameValuePair("ji_address", game.getJgAddress()));
		params.add(new BasicNameValuePair("ji_wd", game.getJgLatitude())); 
		params.add(new BasicNameValuePair("ji_jd", game.getJgLongitude()));
		params.add(new BasicNameValuePair("ji_date", game.getJgDate()));
		params.add(new BasicNameValuePair("fly_address", game.getFlyAddress()));
		params.add(new BasicNameValuePair("fly_jd", game.getFlyLongitude()));
		params.add(new BasicNameValuePair("fly_wd", game.getFlyLatitude()));
		params.add(new BasicNameValuePair("fly_date", game.getFlyDate()));
		params.add(new BasicNameValuePair("cp", game.getReferee()));
		
		
		JSONObject jObj = jsonParser.getJSONFromUrl(addOrUpdateGameUrl,params, "POST");
		return jObj;
	}
	
	public JSONObject updateGame(Game game){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", game.getId()));
		params.add(new BasicNameValuePair("targetid", game.getTargetID()));
		params.add(new BasicNameValuePair("title", game.getName()));
		params.add(new BasicNameValuePair("bsdate", game.getDate()));
		params.add(new BasicNameValuePair("bonus", game.getBonus()));
		params.add(new BasicNameValuePair("distance", game.getDistance()));
		params.add(new BasicNameValuePair("ji_address", game.getJgAddress()));
		params.add(new BasicNameValuePair("ji_wd", game.getJgLatitude())); 
		params.add(new BasicNameValuePair("ji_jd", game.getJgLongitude()));
		params.add(new BasicNameValuePair("ji_date", game.getJgDate()));
		params.add(new BasicNameValuePair("fly_address", game.getFlyAddress()));
		params.add(new BasicNameValuePair("fly_jd", game.getFlyLongitude()));
		params.add(new BasicNameValuePair("fly_wd", game.getFlyLatitude()));
		params.add(new BasicNameValuePair("fly_date", game.getFlyDate()));
		params.add(new BasicNameValuePair("cp", game.getReferee()));
		
		JSONObject jObj = jsonParser.getJSONFromUrl(addOrUpdateGameUrl,params, "POST");
		return jObj;
	}
	
	public JSONObject updateGatherInfo(Game game){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", game.getId()));
		params.add(new BasicNameValuePair("ji_wd", game.getJgLatitude())); 
		params.add(new BasicNameValuePair("ji_jd", game.getJgLongitude()));
		params.add(new BasicNameValuePair("total", game.getTotal()));
		params.add(new BasicNameValuePair("info", game.getInfo()));
		if(game.getStatus().equals("0")){
			params.add(new BasicNameValuePair("status", "1"));
		}
		
		JSONObject jObj = jsonParser.getJSONFromUrl(addOrUpdateGameUrl,params, "POST");
		return jObj;
	}
	
	public void updateLocalGameData(ArrayList<Game> games, Game game, String step){
		for(int i=0;i<games.size();i++){
			if(games.get(i).getId().equals(game.getId())){
				games.get(i).setJgLatitude(game.getJgLatitude());
				games.get(i).setJgLongitude(game.getJgLongitude());
				games.get(i).setInfo(game.getInfo());
				games.get(i).setTotal(game.getTotal());
				if("gather".equals(step)){
					games.get(i).setStatus("1");
				}else if("transport".equals(step)){
					games.get(i).setStatus("2");
				}
				break;
			}
		}
	}
	
	
	//����г̼�¼
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
	
	public static void setGameStatus(RadioGroup rg, int status){
		switch (status) {
		case Const.STATUS_PREPARING:
			rg.getChildAt(0).setEnabled(true);
			rg.getChildAt(2).setEnabled(true);
			rg.getChildAt(4).setEnabled(false);
			rg.getChildAt(6).setEnabled(false);
			break;
		case Const.STATUS_STARTED:
		case Const.STATUS_TRANSPORTING:
			rg.getChildAt(0).setEnabled(true);
			rg.getChildAt(2).setEnabled(true);
			rg.getChildAt(4).setEnabled(true);
			rg.getChildAt(6).setEnabled(false);
			break;
		case Const.STATUS_ARRIVED:
			rg.getChildAt(0).setEnabled(true);
			rg.getChildAt(2).setEnabled(true);
			rg.getChildAt(4).setEnabled(true);
			rg.getChildAt(6).setEnabled(true);
			break;
		default:
			break;
		}
	}
	
	public ArrayList<Game> gameConvert(JSONArray arr){
		ArrayList<Game> data = new ArrayList<Game>();
		SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		for(int i=0;i<arr.length();i++){
			Game game = new Game();
			try {
				JSONObject obj = arr.getJSONObject(i);
				game.setName(obj.getString("title").equals("null")?"":obj.getString("title"));
				game.setId(obj.getString("id").equals("null")?"":obj.getString("id"));
				game.setTargetID(obj.getString("targetid").equals("null")?"":obj.getString("targetid"));
				game.setDate(obj.getString("bsdate").equals("null")?"":obj.getString("bsdate"));
				game.setDistance(obj.getString("distance").equals("null")?"":obj.getString("distance"));
				game.setBonus(obj.getString("bonus").equals("null")?"":obj.getString("bonus"));
				
				game.setJgDate(obj.getString("ji_date").equals("null")?"":dateTime.format(Long.parseLong(obj.getString("ji_date"))*1000));
				game.setJgLatitude(obj.getString("ji_wd").equals("null")?"":obj.getString("ji_wd"));
				game.setJgLongitude(obj.getString("ji_jd").equals("null")?"":obj.getString("ji_jd"));
				game.setJgAddress(obj.getString("ji_address").equals("null")?"":obj.getString("ji_address"));
				
				game.setFlyAddress(obj.getString("fly_address").equals("null")?"":obj.getString("fly_address"));
				game.setFlyDate(obj.getString("fly_date").equals("null")?"":date.format(Long.parseLong(obj.getString("fly_date"))*1000));
				game.setFlyLatitude(obj.getString("fly_wd").equals("null")?"":obj.getString("fly_wd"));
				game.setFlyLongitude(obj.getString("fly_jd").equals("null")?"":obj.getString("fly_jd"));
				game.setReferee(obj.getString("cp").equals("null")?"":obj.getString("cp"));
				game.setType(obj.getString("type").equals("null")? "0":obj.getString("type"));
				game.setTotal(obj.getString("total").equals("null")? "":obj.getString("total"));
				game.setInfo(obj.getString("info").equals("null")? "":obj.getString("info"));
				game.setStatus(obj.getString("status").equals("null")?"":obj.getString("status"));
				
				data.add(game);
				
			} catch (JSONException e) {
				
			}
		}
		return data;
	}
	
	public static String arrayToString(ArrayList<String> arr){
		return arr.toString();
	}
}
