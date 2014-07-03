package com.zhxg.zhxgm;


import com.zhxg.zhxgm.utils.Utils;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class Register extends BaseActivity {

	private UserRegisterTask mRegisterTask = null;
	private EditText username;
	private EditText mobile;
	private EditText password;
	private EditText confirm_password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		username = (EditText) findViewById(R.id.username);
		mobile = (EditText)findViewById(R.id.mobile);
		password = (EditText) findViewById(R.id.password);
		confirm_password = (EditText) findViewById(R.id.comfirm_password);
		
		findViewById(R.id.register_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						attemptRegister();
					}
				});
	}
	
	private void attemptRegister(){
		if(mRegisterTask != null){
			return;
		}
		
		// Reset errors.
		username.setError(null);
		mobile.setError(null);
		password.setError(null);
		confirm_password.setError(null);
		
		boolean cancel = false;
		View focusView = null;
		
		//check validation	
		
		if(TextUtils.isEmpty(confirm_password.getText().toString())){
			confirm_password.setError(getString(R.string.error_field_required));
			focusView = confirm_password;
			cancel = true;
		}else if(!confirm_password.getText().equals(password.getText())){
			confirm_password.setError(getString(R.string.error_field_not_identical));
			focusView = confirm_password;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(password.getText().toString())){
			password.setError(getString(R.string.error_field_required));
			focusView = password;
			cancel = true;
		}		
		
		if(TextUtils.isEmpty(mobile.getText())){
			mobile.setError(getString(R.string.error_field_required));
			focusView = mobile;
			cancel = true;
		}else if(!Utils.checkMobile(mobile.getText().toString())){
			mobile.setError(getString(R.string.error_field_username_format));
			focusView = mobile;
			cancel = true;
		}
		
		
		if(TextUtils.isEmpty(username.getText())){
			username.setError(getString(R.string.error_field_required));
			focusView = username;
			cancel = true;
		}else if(!Utils.checkUserName(username.getText().toString())){
			username.setError(getString(R.string.error_field_username_format));
			focusView = username;
			cancel = true;
		}
		
		if(cancel){
			focusView.requestFocus();
		}else{
			mRegisterTask = new UserRegisterTask();
			mRegisterTask.execute((Void)null);
		}
		
	}
	
	
	
	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
		
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, LoginActivity.class);            
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
	        startActivity(intent);            
	        return true;    
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
}
