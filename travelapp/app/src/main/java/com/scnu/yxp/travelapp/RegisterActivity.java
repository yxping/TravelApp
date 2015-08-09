package com.scnu.yxp.travelapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.scnu.yxp.travelapp.tool.httpUtil;
import com.scnu.yxp.travelapp.view.EmailAutoCompleteTextView;

public class RegisterActivity extends Activity{
	//注册按钮
	private Button registerBtn;
	private EditText neckname, psw, repsw;
	private EmailAutoCompleteTextView mail;
	private String response;
	private ProgressDialog pd;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x120)
			{
				Log.i("response", response);
				pd.cancel();
				if(response.contains("success"))
				{
					Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(RegisterActivity.this, LoggingActivity.class);
					startActivity(intent);
				}else if(response.contains("used")){
					Toast.makeText(RegisterActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(RegisterActivity.this, "注册失败，请重试!", Toast.LENGTH_SHORT).show();
				}
			}
		}};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		psw = (EditText)findViewById(R.id.password);
		repsw = (EditText)findViewById(R.id.repassword);
		mail = (EmailAutoCompleteTextView)findViewById(R.id.mail);
		neckname = (EditText)findViewById(R.id.neckname);
		registerBtn = (Button)findViewById(R.id.register_btn);
		pd = new ProgressDialog(this);
		pd.setTitle("注册中");
		pd.setMessage("正在注册，请稍后...");
		registerBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.i("correct",mail.isCorrect()+"");
				if(!mail.isCorrect())
				{
					Toast.makeText(RegisterActivity.this, "邮箱格式不正确！", Toast.LENGTH_SHORT).show();
				}else if( psw.getText().toString().trim().equals("") || repsw.getText().toString().trim().equals("")
						|| neckname.getText().toString().trim().equals("") || mail.getText().toString().trim().equals(""))
				{
					Toast.makeText(RegisterActivity.this, "注册信息不能为空，请完善", Toast.LENGTH_SHORT).show();
					
				}else{
					if(psw.getText().toString().trim().equals(repsw.getText().toString().trim()))
					{
						pd.show();
						Thread thread = new Thread(new Runnable() {
							
							@Override
							public void run() {

								response = httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=register",
										"email=" + mail.getText().toString() + "&password=" +psw.getText().toString() + 
										"&user=" + neckname.getText().toString());
								handler.sendEmptyMessage(0x120);
							}
						});
						thread.start();
					}else{
						Toast.makeText(RegisterActivity.this, "两次输入的密码不同，请重新输入", Toast.LENGTH_SHORT).show();
						psw.setText("");
						repsw.setText("");
					}
				}
				
			}
		});
	}
}
