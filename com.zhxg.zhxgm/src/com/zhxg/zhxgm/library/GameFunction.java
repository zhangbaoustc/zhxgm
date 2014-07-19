package com.zhxg.zhxgm.library;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
import android.media.ExifInterface;
import android.widget.RadioGroup;

import com.baidu.location.BDLocation;
import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.Game;
import com.zhxg.zhxgm.vo.Trace;

public class GameFunction {
	private JSONParser jsonParser;
	private String gameInfoUrl = "http://app.zhxg.com/index.php?arg=bslist";
	private String addOrUpdateGameUrl = "http://app.zhxg.com/index.php?arg=bsadd";
	private static String traceUploadUrl = "http://app.zhxg.com/index.php?arg=say";
	private static String traceHistoryUrl = "http://app.zhxg.com/index.php?arg=getsay&bsid=";
	private static String transportHistoryUrl = "http://app.zhxg.com/index.php?arg=gpslist&bsid=";
	private static String transportInsertUrl = "http://app.zhxg.com/index.php?arg=gpsadd";
	private static String imageUploadUrl = "http://app.zhxg.com/index.php?arg=img";
	
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
	
	public JSONObject getTransportLocation(String bsid){
		String rul = transportHistoryUrl + bsid;
		
		JSONObject jObj = jsonParser.getJSONFromUrl(rul,null, "GET");
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
	
	public JSONObject updateGatherInfo(HashMap<String, String> data){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", data.get(Const.GAME_ID)));
		params.add(new BasicNameValuePair("ji_wd", data.get(Const.GAME_JGLATITUDE))); 
		params.add(new BasicNameValuePair("ji_jd", data.get(Const.GAME_JGLONGITUDE)));
		params.add(new BasicNameValuePair("total", data.get(Const.GAME_TOTAL)));
		params.add(new BasicNameValuePair("info", data.get(Const.GAME_INFO)));
		if(data.get(Const.GAME_STATUS).equals(Const.STATUS_PREPARING+"")){
			params.add(new BasicNameValuePair("status", Const.STATUS_STARTED+""));
		}
		
		JSONObject jObj = jsonParser.getJSONFromUrl(addOrUpdateGameUrl,params, "POST");
		return jObj;
	}
	
	public JSONObject beginTransport(Game game){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", game.getId()));		
		params.add(new BasicNameValuePair("status", Integer.parseInt(game.getStatus()) + 1 + ""));
		
		JSONObject jObj = jsonParser.getJSONFromUrl(addOrUpdateGameUrl,params, "POST");
		return jObj;
	}
	
	public void updateLocalGameData(ArrayList<Game> games,HashMap<String, String> columns){
		for(int i=0;i<games.size();i++){
			if(games.get(i).getId().equals(columns.get(Const.GAME_ID))){
				Iterator it = columns.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry entry = (Map.Entry) it.next(); 
					if(Const.GAME_ID.equals(entry.getKey().toString())){
						games.get(i).setStatus(entry.getValue().toString());
					}else if(Const.GAME_INFO.equals(entry.getKey().toString())){
						games.get(i).setInfo(entry.getValue().toString());
					}else if(Const.GAME_TOTAL.equals(entry.getKey().toString())){
						games.get(i).setTotal(entry.getValue().toString());
					}else if(Const.GAME_JGLATITUDE.equals(entry.getKey().toString())){
						games.get(i).setJgLatitude(entry.getValue().toString());
					}else if(Const.GAME_JGLONGITUDE.equals(entry.getKey().toString())){
						games.get(i).setJgLongitude(entry.getValue().toString());
					}else if(Const.GAME_STATUS.equals(entry.getKey().toString())){
						games.get(i).setStatus(entry.getValue().toString());
					}
				    
				}
			}
		}
	}
	
	public void updateLocalGameData1(ArrayList<Game> games, Game game, String step){
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
	
	
	public JSONObject getTraceHistry(String bsid){
		traceHistoryUrl = traceHistoryUrl + bsid;
		JSONObject jObj = jsonParser.getJSONFromUrl(traceHistoryUrl,null, "GET");
		return jObj;
	}
	
	public JSONObject TransportInsert(BDLocation location,String bsid){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("bsid", bsid));
		params.add(new BasicNameValuePair("xdot", location.getLongitude()+""));
		params.add(new BasicNameValuePair("ydot", location.getLatitude()+""));
		
		JSONObject jObj = jsonParser.getJSONFromUrl(transportInsertUrl,params, "POST");
		return jObj;
	}
	

	
	public boolean uploadImages(HashMap<String, String> info, String[] files){
		boolean result = false;

		 try {
			 HttpClient httpClient = new DefaultHttpClient();
			 HttpPost postRequest = new HttpPost(imageUploadUrl);
			 
			 MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			 
			Iterator<Entry<String, String>> iter = info.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next(); 
				reqEntity.addPart(entry.getKey().toString(),new StringBody(entry.getValue().toString(),Charset.forName("UTF-8")));
			}
			
			int i = 0;
			for ( String name : files){
				File file = new File(name);
				String[] image_info = file.getName().replace(".jpg", "").split("_");
				
				ExifInterface exifInterface = new ExifInterface(name);
				reqEntity.addPart("set["+i+ "][ydot]" ,new StringBody(image_info[0]));
				reqEntity.addPart("set["+i+ "][xdot]" ,new StringBody(image_info[1]));
				reqEntity.addPart("set["+i+ "][pubdate]" ,new StringBody(image_info[2]));
				reqEntity.addPart("set["+i+ "][status]" ,new StringBody(image_info[3]));
				Bitmap bitmap = BitmapFactory.decodeFile(name);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 25, bos);
				byte[] data = bos.toByteArray();
				ByteArrayBody bab = new ByteArrayBody(data, Math.floor(Math.random() * 11)+".jpg");
				reqEntity.addPart("set["+i+ "][img]" ,bab);
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
			 if("TRUE".equals(jObj.getString("flag").toUpperCase())){
				 result = true;
			 }else{
				 result = false;
			 }
		 }catch(Exception es){
			 result = false;
		 }
		
		
		return result; 
	}

	
	
	
	public boolean addTraceMark(HashMap<String, String> info, String[] files){
		boolean result = false;

		 try {
			 HttpClient httpClient = new DefaultHttpClient();
			 HttpPost postRequest = new HttpPost(traceUploadUrl);
			 
			 MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			 
			Iterator<Entry<String, String>> iter = info.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next(); 
				reqEntity.addPart(entry.getKey().toString(),new StringBody(entry.getValue().toString(),Charset.forName("UTF-8")));
			}
			
			int i = 0;
			for ( String name : files){
				Bitmap bitmap = BitmapFactory.decodeFile(name);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 25, bos);
				byte[] data = bos.toByteArray();
				ByteArrayBody bab = new ByteArrayBody(data, Math.floor(Math.random() * 11)+".jpg");
				reqEntity.addPart("imgs["+i+"]", bab);
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
	
	public ArrayList<BDLocation> transportConvert(JSONArray arr){
		ArrayList<BDLocation> data = new ArrayList<BDLocation>();
		for(int i=0;i<arr.length();i++){
			BDLocation location = new BDLocation();
			try {
				JSONObject obj = arr.getJSONObject(i);				
				location.setLatitude(Double.parseDouble(obj.getString("ydot")));
				location.setLongitude(Double.parseDouble(obj.getString("xdot")));
				location.setTime(obj.getString("pubdate"));
				
				data.add(location);
				
			} catch (JSONException e) {
				
			}
		}
		return data;
	}
	
	public ArrayList<Trace> traceConvert(JSONArray arr){
		ArrayList<Trace> data = new ArrayList<Trace>();
		for(int i=0;i<arr.length();i++){
			Trace trace = new Trace();
			try {
				JSONObject obj = arr.getJSONObject(i);				
				trace.setAuthor(obj.getString("username").equals("null")?"":obj.getString("username"));
				trace.setContent(obj.getString("info").equals("null")?"":obj.getString("info"));
				String[] images = obj.getString("imgs").replace("\"", "").replace("[", "").replace("]", "").split(",");
				trace.setImages(images);

				data.add(trace);
				
			} catch (JSONException e) {
				
			}
		}
		return data;
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
