package com.zhxg.zhxgm;


import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;

import com.baidu.mapapi.SDKInitializer;
import com.zhxg.zhxgm.fragment.GeneralFragment;
import com.zhxg.zhxgm.fragment.MainNavFragment.Callbacks;

public class MainActivity extends BaseActivity implements Callbacks{

	public static final String Item = "item";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemSelected(int item) {
		Bundle arguments = new Bundle();
		arguments.putInt(Item, item);
		
		GeneralFragment generalFragment = new GeneralFragment();
		generalFragment.setArguments(arguments);
		
		FragmentManager fm = this.getFragmentManager();
		fm.beginTransaction().replace(R.id.main_detail_FrameLayout, generalFragment).commit();
	}

}
