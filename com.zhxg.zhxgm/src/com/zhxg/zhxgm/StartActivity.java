package com.zhxg.zhxgm;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhxg.zhxgm.library.UserFunction;
import com.zhxg.zhxgm.vo.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class StartActivity extends Activity {

	private User user;
	private String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		user = UserFunction.getSessionUser(this);
		username = user.getUserName();
		if("".equals(user.getUserName()) || user.getUserName() == null){
			startActivity(new Intent(this, LoginActivity.class));
		}else{
			new UserLoginTask().execute(user);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<User, Void, Boolean> {
		private boolean resultCode = false;
		
		@Override
		protected Boolean doInBackground(User... params) {
			
			JSONObject result = new UserFunction().loginUser(params[0].getUserName(), params[0].getPassword());
			try {
				if("TRUE".equals(result.getString("flag"))){
					resultCode = true;
				}else{
					resultCode = false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return resultCode;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				startActivity(new Intent(StartActivity.this, MainActivity.class));
				finish();
			} else {
				Intent failIntent = new Intent(StartActivity.this, LoginActivity.class);
				failIntent.putExtra("username", username);
				startActivity(failIntent);
				finish();
			}
		}
	}

}
