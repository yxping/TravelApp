package com.scnu.yxp.travelapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class ChooseLogRegister extends Activity{
	//登陆注册按钮
	private Button loggingBtn, registerBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_log_or_register);
		loggingBtn = (Button)findViewById(R.id.logging_btn);
		registerBtn = (Button)findViewById(R.id.register_btn);
		loggingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ChooseLogRegister.this, LoggingActivity.class);
				startActivity(intent);
			}
		});
		registerBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ChooseLogRegister.this, RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
