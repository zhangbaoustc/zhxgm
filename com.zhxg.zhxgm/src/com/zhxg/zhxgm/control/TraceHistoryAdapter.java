package com.zhxg.zhxgm.control;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhxg.zhxgm.R;
import com.zhxg.zhxgm.SingleImageShowActivity;
import com.zhxg.zhxgm.library.ExpandableHeightGridView;
import com.zhxg.zhxgm.utils.ImageUtils;
import com.zhxg.zhxgm.vo.Const;
import com.zhxg.zhxgm.vo.Trace;

public class TraceHistoryAdapter extends BaseAdapter {
	
	private ArrayList<Trace> data;
	private Activity mActivity;

	public interface Callbacks{
		public void onDataSetChanged();
	}
	
	public TraceHistoryAdapter(ArrayList<Trace> data, Activity activity) {
		super();
		this.data = data;
		this.mActivity = activity;
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
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.trace_history_item, null);
			holder.author = (TextView) convertView.findViewById(R.id.trace_his_user);
			holder.pubdate = (TextView) convertView.findViewById(R.id.trace_his_pubdate);
			holder.content = (TextView) convertView.findViewById(R.id.trace_his_content);
			holder.images = (ExpandableHeightGridView) convertView.findViewById(R.id.trace_his_images);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.images.setExpanded(true);
		holder.author.setText(data.get(position).getAuthor());
		holder.pubdate.setText(data.get(position).getPubdate());
		holder.content.setText(data.get(position).getContent());
		
		if(data.get(position).getImages() != null){
			holder.images.setVisibility(View.VISIBLE);
			holder.images.setAdapter(new ImageAdapter(mActivity,data.get(position).getImages()));
			holder.images.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int idx, long arg3) {
					Intent sivIntent = new Intent(mActivity, SingleImageShowActivity.class);
					sivIntent.putExtra(Const.IMAGE_INFO, data.get(position).getImages()[idx]);
					mActivity.startActivity(sivIntent);
				}
			});
		}else{
			holder.images.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	final class ViewHolder{
		TextView author;
		TextView content;
		TextView pubdate;
		ExpandableHeightGridView images;
	}
	
	
	private class ImageAdapter extends BaseAdapter{  
        private Context mContext;  
        private String[] mThumb;
  
        public ImageAdapter(Context context, String[] data) {  
            this.mContext=context;
            mThumb = data;
        }  
  
        @Override  
        public int getCount() {  
            return mThumb.length;  
        }  
  
        @Override  
        public Object getItem(int position) {  
            return mThumb[position];  
        }  
  
        @Override  
        public long getItemId(int position) {  
            return 0;  
        }  
  
        @Override  
        public View getView(int position, View convertView, ViewGroup parent) {  
            //定义一个ImageView,显示在GridView里  
            ImageView imageView;  
            if(convertView==null){  
                imageView=new ImageView(mContext);  
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));  
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);  
                imageView.setPadding(8, 8, 8, 8);  
            }else{  
                imageView = (ImageView) convertView;  
            }  
            new loadImagesTask().execute(new ImageValue(imageView,mThumb[position]));
            return imageView;  
        }  
          
    } 
	
	public class loadImagesTask extends AsyncTask<ImageValue, Void, Bitmap> {
		private ImageValue imageValue;
		@SuppressLint("DefaultLocale")
		@Override
		protected Bitmap doInBackground(ImageValue... params) {
			imageValue = params[0];
			return ImageUtils.getBitmapFromURL(imageValue.url,8);
		}

		@Override
		protected void onPostExecute(Bitmap bit) {
			imageValue.iv.setImageBitmap(bit);
		}
	}
	
	final class ImageValue{
		public ImageValue(ImageView imageView,String uri){
			iv = imageView;
			url = uri;
		}
		ImageView iv;
		String url;
	}


}
