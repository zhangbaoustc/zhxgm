package com.zhxg.zhxgm.library;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhxg.zhxgm.vo.Game;

public class GameFunction {
	private JSONParser jsonParser;
	private String gameInfoUrl = "http://app.zhxg.com/index.php?arg=bslist"; 
	
	public GameFunction(){
		jsonParser = new JSONParser();
	}
	
	public JSONObject getGames(String targetid)
	{
		String url = gameInfoUrl + "&targetid="+targetid;
		JSONObject jObj = jsonParser.getJSONFromUrl(url,null, "GET");
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
