package com.scnu.yxp.travelapp;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scnu.yxp.travelapp.homepage.HomePageActivity;
import com.scnu.yxp.travelapp.info.PersonalInfo;
import com.scnu.yxp.travelapp.tool.BitmapFromUrl;
import com.scnu.yxp.travelapp.tool.httpUtil;

public class LoggingActivity extends Activity {
	//登陆按钮
	private Button loggingBtn;
	//邮箱账号和密码
	private EditText mailAccount, password;
	//飞机图像
	private ImageView plane;
	private TextView text;
	//帧动画
	private AnimationDrawable anim;
	private String response;
	private InputMethodManager mInputMethodManager;
	private List<Map<String,String>> jsonResponse;
	private String flag = "false";
	private String imgUrl = "";
	private String userName = "";
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x120)
			{
				if(flag.contains("success"))
				{
					Toast.makeText(LoggingActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
					PersonalInfo.account = mailAccount.getText().toString();
					PersonalInfo.password = password.getText().toString();
					PersonalInfo.nickname = userName;
					PersonalInfo.prenickname = userName;
					Intent intent = new Intent(LoggingActivity.this, HomePageActivity.class);
					startActivity(intent);
				}else if(flag.contains("failed")){
					loggingBtn.setEnabled(true);
					Toast.makeText(LoggingActivity.this, "账号或密码不正确", Toast.LENGTH_SHORT).show();
					mailAccount.setText("");
					password.setText("");
					text.setVisibility(View.GONE);
					anim.stop();
			        plane.setBackgroundResource(R.drawable.plane_move_anim);
				}else{
					Toast.makeText(LoggingActivity.this, "网络连接超时", Toast.LENGTH_SHORT).show();
					text.setVisibility(View.GONE);
					anim.stop();
			        plane.setBackgroundResource(R.drawable.plane_move_anim);
				}
				loggingBtn.setEnabled(true);
			}
		}};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_logging);
        jsonResponse = new ArrayList<Map<String,String>>();
        loggingBtn = (Button)findViewById(R.id.logging_btn);
        mailAccount = (EditText)findViewById(R.id.mail);
        mailAccount.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        password = (EditText)findViewById(R.id.password);
        plane = (ImageView)findViewById(R.id.plane);
        text =(TextView) findViewById(R.id.activity_logging_text);
        
        password.setText("1");
        mailAccount.setText("1@m.scnu.edu.cn");
        //帧动画设置
        plane.setBackgroundResource(R.drawable.plane_move_anim);
        anim = (AnimationDrawable)plane.getBackground();
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        loggingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getApplicationWindowToken(), 0);
//				finish();
//				Intent intent = new Intent(LoggingActivity.this, HomePageActivity.class);
//				startActivity(intent);
				if(password.getText().toString().equals("") 
						|| mailAccount.getText().toString().equals(""))
				{
					Toast.makeText(getApplicationContext(), "账号或密码不能为空", Toast.LENGTH_SHORT).show();
				}else{
					text.setVisibility(View.VISIBLE);
					anim.start();
					loggingBtn.setEnabled(false);
					Thread thread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							response = httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=login",
									"email=" + mailAccount.getText().toString() + "&password=" +
											password.getText().toString());
							Log.i("response", response);
							JSONTokener jsonParser = new JSONTokener(response);
							try {
								jsonParser.nextValue();
								JSONObject object = (JSONObject) jsonParser.nextValue();
								flag = object.getString("flag");
								userName = object.getString("user");
								imgUrl = object.getString("head");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Log.i("url", imgUrl);
							if(!imgUrl.equals(""))
							{
								Drawable headDrawable =BitmapFromUrl.loadImageFromUrl(imgUrl);
								PersonalInfo.headBitmap = ((BitmapDrawable)headDrawable).getBitmap();
								PersonalInfo.preheadBitmap = ((BitmapDrawable)headDrawable).getBitmap();
							}
							Log.i("response", flag);
							handler.sendEmptyMessage(0x120);
						}
					});
					thread.start();
				}
			}
		});
    }
    
}
