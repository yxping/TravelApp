package com.scnu.yxp.travelapp.me;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.scnu.yxp.travelapp.R;

public class PasswordReviseActivity extends Activity{
	private TextView psw, repsw;
	private Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.me_password_revise);
		psw = (TextView) findViewById(R.id.me_setting_password_text);
		repsw = (TextView) findViewById(R.id.me_setting_password_retext);
		btn = (Button) findViewById(R.id.me_setting_password_btn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
	}
}
