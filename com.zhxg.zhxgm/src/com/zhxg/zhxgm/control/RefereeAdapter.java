package com.zhxg.zhxgm.control;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zhxg.zhxgm.R;

public class RefereeAdapter extends BaseAdapter {
	
	private List<String> data;
	private Activity mActivity;
	private Callbacks callback;

	public interface Callbacks{
		public void onDataSetChanged();
	}
	
	public RefereeAdapter(List<String> data, Activity activity) {
		super();
		this.data = data;
		this.mActivity = activity;
		this.callback = (Callbacks) mActivity;
	}

	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.referee_item, null);
			holder.referee = (TextView) convertView.findViewById(R.id.referee);
			holder.action = (ImageButton) convertView.findViewById(R.id.action);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		//bind data		
		holder.referee.setText(data.get(position));
		if(position == (getCount()-1)){
			holder.referee.setVisibility(View.GONE);
			holder.action.setImageResource(R.drawable.referee_add);
		}else{
			holder.referee.setVisibility(View.VISIBLE);
			holder.action.setImageResource(R.drawable.referee_plus);
		}
		
		holder.action.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(position == (getCount()-1)){
					
					final EditText refereeV = new EditText(mActivity);
					new AlertDialog.Builder(mActivity)
							.setTitle("请输入")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(refereeV)
							.setPositiveButton("确定", new  DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											data.add(getCount()-1,refereeV.getText().toString());
											notifyDataSetChanged();
											callback.onDataSetChanged();
										}
									} )
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											
										}
									}).show();
				}else{
					data.remove(position);
					if(data.size() == 0){
						data.add("");
					}
					notifyDataSetChanged();
					callback.onDataSetChanged();
				}
			}
		});
		return convertView;
	}
	
	final class ViewHolder{
		TextView referee;
		ImageButton action;
	}


}
