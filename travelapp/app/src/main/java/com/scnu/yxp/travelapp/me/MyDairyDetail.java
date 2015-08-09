package com.scnu.yxp.travelapp.me;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.image.ImageActivity;

public class MyDairyDetail extends Activity {
	private ImageView backBtn;
	private List<Drawable> imageList;
	private GridView mGridView;
	private TextView content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.me_dairy_gallery_details);
		backBtn = (ImageView) findViewById(R.id.me_dairy_gallery_detail_back_btn);
		mGridView = (GridView) findViewById(R.id.me_dairy_gallery_detail_gridview);
		content = (TextView) findViewById(R.id.me_dairy_gallery_detail_content);
		imageList = new ArrayList<Drawable>();
		for(int i = 0 ; i < 3 ; i++)
		{
			imageList.add(getResources().getDrawable(R.drawable.bg1));
		}
		mGridView.setAdapter(new ImageAdapter(this));
		setListener();
	}
	
	public void setListener() {
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
				startActivity(intent);
			}
		});
	}
	
	public class ImageAdapter extends BaseAdapter {
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imageList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(mGridView.getWidth() / 3,
                		mGridView.getWidth() / 3));
                imageView.setScaleType(ScaleType.CENTER_INSIDE);
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageDrawable(imageList.get(position));

            return imageView;
        }

        private Context mContext;

    }
}
