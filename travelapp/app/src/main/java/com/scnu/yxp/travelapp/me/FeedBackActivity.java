package com.scnu.yxp.travelapp.me;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.scnu.yxp.travelapp.R;

public class FeedBackActivity extends Activity {
	private ImageView backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.me_feedback);
		backBtn = (ImageView) findViewById(R.id.me_feedback_back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
