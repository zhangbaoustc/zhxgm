package com.zhxg.zhxgm.control;

import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.location.BDLocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteController extends SQLiteOpenHelper {

	private static String dbName = "zhxg_mobile.db";
	private String querry = "Create table TransportLocation ( ID INTEGER PRIMARY KEY, bsid text, xdot text, ydot text, to_server text, time INTEGER )";
	public SqliteController(Context context) {
		super(context, dbName, null, 3);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(querry);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
		String query; 
		query = "DROP TABLE IF EXISTS TransportLocation"; 
		database.execSQL(query); 
		onCreate(database);
	}
	
	public void clearGameLocation(String bsid){
		SQLiteDatabase database = this.getWritableDatabase(); 
		String query = "delete FROM TransportLocation where bsid = " + bsid; 
		database.execSQL(query); 
	}
	
	public void insertLocation(HashMap<String, String> queryValues) 
	{
		SQLiteDatabase database = this.getWritableDatabase(); 
		ContentValues values = new ContentValues(); 
		values.put("bsid", queryValues.get("bsid")); 
		values.put("xdot", queryValues.get("xdot")); 
		values.put("ydot", queryValues.get("ydot")); 
		values.put("to_server", queryValues.get("to_server"));
		values.put("time", queryValues.get("time"));
		
		database.insert("TransportLocation", null, values); 
		database.close(); 
	}
	
	public ArrayList<HashMap<String, String>> getAllLocations(String bsid) { 
		ArrayList<HashMap<String, String>> wordList; 
		wordList = new ArrayList<HashMap<String, String>>(); 
		String selectQuery = "SELECT * FROM TransportLocation where bsid = " + bsid; 
		SQLiteDatabase database = this.getWritableDatabase(); 
		Cursor cursor = database.rawQuery(selectQuery, null); 
		if (cursor.moveToFirst()) {
			do { 
					HashMap<String, String> map = new HashMap<String, String>(); 
					map.put("xdot", cursor.getString(2));
					map.put("ydot", cursor.getString(3));
					wordList.add(map); 
				} while (cursor.moveToNext()); 
			} 
		// return contact list 
		return wordList; 
	}

	
}
