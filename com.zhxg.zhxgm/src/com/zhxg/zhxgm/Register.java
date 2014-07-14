package com.zhxg.zhxgm;


import org.json.JSONException;
import org.json.JSONObject;

import com.zhxg.zhxgm.library.UserFunction;
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
import android.widget.Toast;

public class Register extends BaseActivity {

	private UserRegisterTask mRegisterTask = null;
	private EditText usernameView;
	private EditText mobileView;
	private EditText passwordView;
	private EditText confirm_passwordView;
	
	private String username;
	private String mobile;
	private String password;
	private String confirm_password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		usernameView = (EditText) findViewById(R.id.username);
		mobileView = (EditText)findViewById(R.id.mobile);
		passwordView = (EditText) findViewById(R.id.password);
		confirm_passwordView = (EditText) findViewById(R.id.comfirm_password);
		
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
		usernameView.setError(null);
		mobileView.setError(null);
		passwordView.setError(null);
		confirm_passwordView.setError(null);
		
		username = usernameView.getText().toString();
		mobile = mobileView.getText().toString();
		password = passwordView.getText().toString();
		confirm_password = confirm_passwordView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		//check validation	
		
		if(TextUtils.isEmpty(confirm_passwordView.getText().toString())){
			confirm_passwordView.setError(getString(R.string.error_field_required));
			focusView = confirm_passwordView;
			cancel = true;
		}else if(!confirm_passwordView.getText().equals(confirm_passwordView.getText())){
			confirm_passwordView.setError(getString(R.string.error_field_not_identical));
			focusView = confirm_passwordView;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(passwordView.getText().toString())){
			passwordView.setError(getString(R.string.error_field_required));
			focusView = passwordView;
			cancel = true;
		}		
		
		if(TextUtils.isEmpty(mobileView.getText())){
			mobileView.setError(getString(R.string.error_field_required));
			focusView = mobileView;
			cancel = true;
		}else if(!Utils.checkMobile(mobileView.getText().toString())){
			mobileView.setError(getString(R.string.error_field_username_format));
			focusView = mobileView;
			cancel = true;
		}
		
		
		if(TextUtils.isEmpty(usernameView.getText())){
			usernameView.setError(getString(R.string.error_field_required));
			focusView = usernameView;
			cancel = true;
		}else if(!Utils.checkUserName(usernameView.getText().toString())){
			usernameView.setError(getString(R.string.error_field_username_format));
			focusView = usernameView;
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

		private boolean resultCode = false;
		private String errorMsg = "";
		private JSONObject mResult;
		@Override
		protected Boolean doInBackground(Void... arg0) {
			JSONObject result = new UserFunction().registerUser(username, mobile, password, confirm_password);
			try {
				if("TRUE".equals(result.getString("flag"))){
					resultCode = true;
					mResult = result;
				}else{
					resultCode = false;
					errorMsg = result.getString("msg");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return resultCode;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			mRegisterTask = null;
			if (success) {
				UserFunction.loginOK(Register.this,username,password,mResult);
				startActivity(new Intent(Register.this, MainActivity.class));
				finish();
			} else {
				Toast.makeText(Register.this, errorMsg, Toast.LENGTH_LONG).show();
				usernameView.requestFocus();
			}
		}
		
		@Override
		protected void onCancelled() {
			mRegisterTask = null;
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
