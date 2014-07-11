package com.zhxg.zhxgm.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		params.add(new BasicNameValuePair("target_id", "340000"));
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
	
	public ArrayList<Game> gameConvert(JSONArray arr){
		ArrayList<Game> data = new ArrayList<Game>();
		for(int i=0;i<arr.length();i++){
			Game game = new Game();
			try {
				JSONObject obj = arr.getJSONObject(i);
				game.setName(obj.getString("title"));
				game.setId(obj.getString("id"));
				game.setTargetID(obj.getString("target_id"));
				game.setDate(obj.getString("bsdate"));
				game.setDistance(obj.getString("distince"));
				game.setBonus(obj.getString("bonus"));
				
				game.setJgDate(obj.getString("ji_date"));
				game.setJgLatitude(obj.getString("ji_wd"));
				game.setJgLongitude(obj.getString("ji_jd"));
				game.setJgAddress(obj.getString("ji_address"));
				
				game.setFlyAddress(obj.getString("fly_address"));
				game.setFlyDate(obj.getString("fly_date"));
				game.setFlyLatitude(obj.getString("fly_wd"));
				game.setFlyLongitude(obj.getString("fly_jd"));
				
				data.add(game);
				
			} catch (JSONException e) {
				
			}
		}
		return data;
	}
}
