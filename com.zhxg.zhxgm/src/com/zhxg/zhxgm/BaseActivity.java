package com.zhxg.zhxgm;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}

	
	
    @Override
    public void onBackPressed() {
    	ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(10);
		if(taskList.get(0).numActivities == 1){
			new AlertDialog.Builder(this).setTitle(getString(R.string.confirm_quit_app))
				.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {		 
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	BaseActivity.this.finish();
				    }
				})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
		 
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	
				    }
				}).show();
		  }else{		
			  super.onBackPressed();
		  }
    	
	}

}
