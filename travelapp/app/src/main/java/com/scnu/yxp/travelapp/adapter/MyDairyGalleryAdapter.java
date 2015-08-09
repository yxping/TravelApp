package com.scnu.yxp.travelapp.adapter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.tool.AsyncImageLoader;
import com.scnu.yxp.travelapp.tool.AsyncImageLoader.ImageCallback;

public class MyDairyGalleryAdapter extends BaseAdapter{
	private Context context;
	private ListView listView;
	private List<Map<String, String>> list;
	private AsyncImageLoader imageLoader_img;

	public MyDairyGalleryAdapter(Context context, ListView list, List<Map<String, String>> list1) {
		this.context = context;
		this.listView = list;
		this.list = list1;
		imageLoader_img=new AsyncImageLoader();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parentView) {
		convertView = LayoutInflater.from(context).inflate(R.layout.me_dairy_gallery_listviewitem, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.image = (ImageView) convertView.findViewById(R.id.me_dairy_gallery_image);
		viewHolder.date = (TextView) convertView.findViewById(R.id.me_dairy_gallery_time);
		viewHolder.content = (TextView) convertView.findViewById(R.id.me_dairy_gallery_content);
		viewHolder.content.setText(list.get(position).get("content"));
		viewHolder.date.setText(list.get(position).get("date"));
		String img_url = "";
		img_url = list.get(position).get("img_url").toString();
		if(!img_url.equals(""))
		{
			try {
				img_url = new URI(img_url).toASCIIString();

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			viewHolder.image.setTag(img_url+position);
			Drawable cachedImage_image=imageLoader_img.loaDrawable(img_url, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl+position);
					if (imageViewByTag != null) {
						imageViewByTag.setImageDrawable(imageDrawable);
					}
				}
			});
			if(cachedImage_image!=null)
				viewHolder.image.setImageDrawable(cachedImage_image);
			else
				viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.bg1));
		}

		return convertView;
	}

	class ViewHolder {
		ImageView image;
		TextView day, date, content;
	}

}
