package com.zhxg.zhxgm.control;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhxg.zhxgm.R;
import com.zhxg.zhxgm.vo.Game;

public class CustomGamesAdapter extends BaseAdapter {
	
	private List<Game> data;
	private Context mContext;

	public CustomGamesAdapter(List<Game> data, Context context) {
		super();
		this.data = data;
		this.mContext = context;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.game_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.type = (TextView) convertView.findViewById(R.id.type);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		//bind data		
		holder.name.setText(data.get(position).getName());
		holder.type.setText(data.get(position).getType());
		
		return convertView;
	}
	
	final class ViewHolder{
		TextView name;
		TextView type;
	}

}
