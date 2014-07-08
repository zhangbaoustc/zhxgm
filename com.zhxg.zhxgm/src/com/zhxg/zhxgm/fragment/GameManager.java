package com.zhxg.zhxgm.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.zhxg.zhxgm.GameDetailActivity;
import com.zhxg.zhxgm.R;
import com.zhxg.zhxgm.control.CustomGamesAdapter;
import com.zhxg.zhxgm.vo.Game;

public class GameManager extends GeneralFragment {

	private View rootView;
	private RadioGroup rg;
	private LinearLayout game_mgr_ll;
	private LinearLayout game_info_ll;
	private LinearLayout game_transport_ll;
	private LinearLayout game_letfly_ll;
	
	
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
		rootView = inflater.inflate(R.layout.game_manager, container,false);
		initLayout();
		return rootView;
	}
	
	private void initLayout(){
		rg = (RadioGroup) rootView.findViewById(R.id.game_nav_group);
		game_mgr_ll = (LinearLayout) rootView.findViewById(R.id.game_mgr_ll);
		game_info_ll = (LinearLayout) rootView.findViewById(R.id.game_info_ll);
		game_transport_ll = (LinearLayout) rootView.findViewById(R.id.game_transport_ll);
		game_letfly_ll = (LinearLayout) rootView.findViewById(R.id.game_letfly_ll);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		rg.setOnCheckedChangeListener(
				new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup arg0, int item) {
						changeGamePage(item);
					}
				});
	}
	
	private void changeGamePage(int item){
		switch (item) {
		case R.id.pageMgr:
			game_mgr_ll.setVisibility(View.VISIBLE);
			game_info_ll.setVisibility(View.GONE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.GONE);
			break;
		case R.id.pageInfo:
			game_mgr_ll.setVisibility(View.GONE);
			game_info_ll.setVisibility(View.VISIBLE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.GONE);
			break;
		case R.id.pageTransport:
			game_mgr_ll.setVisibility(View.GONE);
			game_info_ll.setVisibility(View.GONE);
			game_transport_ll.setVisibility(View.VISIBLE);
			game_letfly_ll.setVisibility(View.GONE);
			break;
		case R.id.pageLetFly:
			game_mgr_ll.setVisibility(View.GONE);
			game_info_ll.setVisibility(View.GONE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.VISIBLE);
			break;
		default:
			game_mgr_ll.setVisibility(View.VISIBLE);
			game_info_ll.setVisibility(View.GONE);
			game_transport_ll.setVisibility(View.GONE);
			game_letfly_ll.setVisibility(View.GONE);
			break;
		}
	}
	

}
