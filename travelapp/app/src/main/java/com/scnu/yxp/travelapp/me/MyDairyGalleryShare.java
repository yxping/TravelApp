package com.scnu.yxp.travelapp.me;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.scnu.yxp.travelapp.R;

public class MyDairyGalleryShare extends Activity{
	private LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_dairy_gallery_share);
		layout = (LinearLayout) findViewById(R.id.me_dairy_gallery_share_layout);
		setListener();
	}
	
	public void setListener() {
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
