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
import com.scnu.yxp.travelapp.adapter.HomePageListViewAdapter.ViewHolder;
import com.scnu.yxp.travelapp.tool.AsyncImageLoader;
import com.scnu.yxp.travelapp.tool.AsyncImageLoader.ImageCallback;
import com.scnu.yxp.travelapp.view.PhotoImageView;

public class MyDairyListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;
	private AsyncImageLoader imageLoader_img;
	private ListView listView;

	public MyDairyListViewAdapter(Context context, List<Map<String, String>> list, ListView listView)
	{
		this.context = context;
		this.listView = listView;
		this.list = list;
		imageLoader_img=new AsyncImageLoader();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parentView) {
		convertView = LayoutInflater.from(context).inflate(R.layout.me_dairy_listview_item, null);
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(R.id.me_dairy_listview_item_textView);
		holder.dateLeft = (TextView) convertView.findViewById(R.id.me_dairy_listview_item_date_left);
		holder.date = (TextView) convertView.findViewById(R.id.me_dairy_listview_item_date);
		holder.location = (TextView) convertView.findViewById(R.id.me_dairy_listview_item_location);
		holder.neckname = (TextView) convertView.findViewById(R.id.me_dairy_listview_item_neckname);
		holder.readTime = (TextView) convertView.findViewById(R.id.me_dairy_listview_item_readTime);
		holder.allDay = (TextView) convertView.findViewById(R.id.me_dairy_listview_item_time);
		if(list != null && list.size() > 0)
		{
			String img_head = "";
			img_head = list.get(position).get("headImg").toString();
			if(!img_head.equals(""))
			{
				holder.headImg = (PhotoImageView) convertView.findViewById(R.id.me_dairy_listview_item_headImage);
				holder.headImg.setTag(img_head+position);
				Drawable cachedImage_head=imageLoader_img.loaDrawable(img_head, new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl+position);
						if (imageViewByTag != null) {
							imageViewByTag.setImageDrawable(imageDrawable);
						}
					}
				});
				if(cachedImage_head!=null)
					holder.headImg.setImageDrawable(cachedImage_head);
				else
					holder.headImg.setImageDrawable(context.getResources().getDrawable(R.drawable.noimage));
			}
			String img_url = "";
			img_url = list.get(position).get("img_url").toString();
			if(!img_url.equals(""))
			{
				try {
					img_url = new URI(img_url).toASCIIString();

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				holder.img = (ImageView) convertView.findViewById(R.id.me_dairy_listview_item_image);
				holder.img.setTag(img_url+position);
				Drawable cachedImage_image=imageLoader_img.loaDrawable(img_url, new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl+position);
						if (imageViewByTag != null) {
							imageViewByTag.setImageDrawable(imageDrawable);
						}
					}
				});
				if(cachedImage_image!=null)
					holder.img.setImageDrawable(cachedImage_image);
				else
					holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.bg1));
			}

			holder.title.setText(list.get(position).get("title"));
			holder.readTime.setText(list.get(position).get("readTime"));
			holder.neckname.setText(list.get(position).get("author"));
			//		holder.location.setText(list.get(position).get("location"));
			holder.date.setText(list.get(position).get("date"));
			holder.dateLeft.setText(list.get(position).get("date"));
			holder.allDay.setText(list.get(position).get("location"));
		}
		return convertView;
	}

	class ViewHolder
	{
		ImageView img;
		TextView title;
		PhotoImageView headImg;
		TextView date;
		TextView dateLeft;
		TextView location;
		TextView neckname;
		TextView readTime;
		TextView allDay;
	}
}
