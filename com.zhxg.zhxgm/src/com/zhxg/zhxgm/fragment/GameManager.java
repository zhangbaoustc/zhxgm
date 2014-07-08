package com.zhxg.zhxgm.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.zhxg.zhxgm.GameDetailActivity;
import com.zhxg.zhxgm.R;
import com.zhxg.zhxgm.control.CustomGamesAdapter;
import com.zhxg.zhxgm.vo.Game;

public class GameManager extends GeneralFragment {

	private ListView gameListView;
	private CustomGamesAdapter gameAdapter;
	
	private List<Game> data;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		data = new ArrayList<Game>();
		for(int i=0;i<5;i++){
			Game mGame = new Game();
			mGame.setName("合肥鸽王争霸赛-" + i);
			mGame.setType("协会比赛");
			data.add(mGame);
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.game_manager, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		gameListView = (ListView) getActivity().findViewById(R.id.games);
		gameAdapter = new CustomGamesAdapter(data, getActivity());
		
		gameListView.setAdapter(gameAdapter);
		gameListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            startActivity(new Intent(getActivity(), GameDetailActivity.class));
	        }
	    });
	}

}
