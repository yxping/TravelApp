package com.scnu.yxp.travelapp.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.homepage.HomePageActivity;
import com.scnu.yxp.travelapp.imageclip.ImageClipActivity;
import com.scnu.yxp.travelapp.info.PersonalInfo;
import com.scnu.yxp.travelapp.view.PhotoImageView;

public class MySettingPage extends Activity implements OnClickListener{
	private RelativeLayout parentLayout;
	private LinearLayout layout, layout1, layout2, layout3, layout4, layout5;
	private PhotoImageView image1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_setting_page);
		parentLayout = (RelativeLayout) findViewById(R.id.me_setting_page_parentLayout);
		layout = (LinearLayout) findViewById(R.id.me_setting_page_layout);
		layout1 = (LinearLayout) findViewById(R.id.me_setting_page_layout1);
		layout2 = (LinearLayout) findViewById(R.id.me_setting_page_layout2);
		layout3 = (LinearLayout) findViewById(R.id.me_setting_page_layout3);
		layout4 = (LinearLayout) findViewById(R.id.me_setting_page_layout4);
		layout5 = (LinearLayout) findViewById(R.id.me_setting_page_layout5);
		image1 = (PhotoImageView) findViewById(R.id.me_setting_page_image1);
		if(PersonalInfo.headBitmap != null)
		{
			setImageBitmap(PersonalInfo.headBitmap);
		}
		setListener();
	}
	
	public void setListener() {
		parentLayout.setOnClickListener(this);
		layout.setOnClickListener(this);
		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		layout3.setOnClickListener(this);
		layout4.setOnClickListener(this);
		layout5.setOnClickListener(this);
	}
	
	public void setImageBitmap(Bitmap bm) {
		image1.setImageBitmap(bm);
	}
	

	@Override
	public void onClick(View view) {
		if( view.getId() == layout1.getId() )
		{
			setResult(HomePageActivity.SETTING_HEAD_IMAGE, null);
			finish();
		}else if( view.getId() == layout2.getId()) {
			setResult(HomePageActivity.SETTING_NICK_NAME, null);
			finish();
//			startActivity(new Intent(getApplicationContext(), NickNameReviseActivity.class));
		}else if( view.getId() == layout3.getId()) {
			startActivity(new Intent(getApplicationContext(), AccountReviseActivity.class));
		}else if( view.getId() == layout4.getId()) {
			startActivity(new Intent(getApplicationContext(), PasswordReviseActivity.class));
		}else if( view.getId() == layout5.getId()) {
//			setResult(HomePageActivity.SETTING_FEED_BACK, null);
//			finish();
			startActivity(new Intent(getApplicationContext(), FeedBackActivity.class));
		}else{
			//后者可用intent来交换数据
			setResult(1, null);
			finish();
		}
	}
	
	
}
