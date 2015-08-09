package com.scnu.yxp.travelapp.me;

import com.scnu.yxp.travelapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TitleRivse extends Activity {
	private TextView text;
	private Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.me_account_revise);
		text = (TextView) findViewById(R.id.me_setting_account_text);
		text.setHint("修改标题");
		btn = (Button) findViewById(R.id.me_setting_account_btn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("title", text.getText().toString());
				setResult(MyDairyGallery.TITLE_REVISE, intent);
				finish();
			}
		});
	}
}
