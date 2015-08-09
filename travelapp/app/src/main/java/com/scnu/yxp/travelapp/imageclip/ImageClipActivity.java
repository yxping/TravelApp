package com.scnu.yxp.travelapp.imageclip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.homepage.HomePageActivity;
import com.scnu.yxp.travelapp.info.PersonalInfo;

public class ImageClipActivity extends Activity{
	private ClipImageLayout layout;
	private File imgFile;
	private Button finishBtn;
	private ImageView backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_clip);
		//获取传来的图片
		imgFile = new File(getIntent().getExtras().getString("path"));
		Log.i("data",imgFile.getPath());
		layout = (ClipImageLayout) findViewById(R.id.activity_image_clip_Layout);
		finishBtn = (Button) findViewById(R.id.activity_image_clip_finish_btn);
		backBtn = (ImageView) findViewById(R.id.activity_image_clip_back_btn);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(imgFile.getPath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap bitmap  = BitmapFactory.decodeStream(fis, null, options);
		layout.setZoomImageView(bitmap);
		setListener();
	}
	public void setListener() {
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		finishBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				PersonalInfo.preheadBitmap = PersonalInfo.headBitmap;
				PersonalInfo.headBitmap = layout.clip();
				setResult(HomePageActivity.IMAGE_CLIP);
				finish();
			}
		});
	}
}
