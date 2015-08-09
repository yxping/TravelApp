package com.scnu.yxp.travelapp.me;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.homepage.HomePageActivity;
import com.scnu.yxp.travelapp.info.PersonalInfo;

public class NickNameReviseActivity extends Activity{
	private TextView text;
	private Button btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.me_setting_nickname);
		text = (TextView) findViewById(R.id.me_setting_nickname_text);
		btn = (Button) findViewById(R.id.me_setting_nickname_btn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				PersonalInfo.nickname = text.getText().toString();
				setResult(HomePageActivity.NICK_NAME_REVISE);
				finish();
			}
		});
	}
}
