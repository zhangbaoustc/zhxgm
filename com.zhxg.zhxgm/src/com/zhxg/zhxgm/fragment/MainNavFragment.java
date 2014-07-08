package com.zhxg.zhxgm.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.zhxg.zhxgm.R;

public class MainNavFragment extends Fragment {

	private Callbacks callbacks = defaultCallbacks;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		//check if activity implements callback interface
		if(!(activity instanceof Callbacks)){
			throw new IllegalStateException("Activity must implements fragment's callbacks !");
		}
		
		callbacks = (Callbacks) activity;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RadioGroup radioGroup = (RadioGroup) inflater.inflate(R.layout.main_nav_bottom, container);
		
		//add listener
		radioGroup.setOnCheckedChangeListener(
				new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup arg0, int item) {
						callbacks.onItemSelected(item);
						
					}
				});
		return radioGroup;
	}


	public interface Callbacks{
		//navigation call back interface
		public void onItemSelected(int item);
	}
	
	private static Callbacks defaultCallbacks = new Callbacks() {
		
		@Override
		public void onItemSelected(int item) {
			// do nothing
		}
	};

	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = defaultCallbacks;
	}
	
}
