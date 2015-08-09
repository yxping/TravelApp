package com.scnu.yxp.travelapp.me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.adapter.HomePageListViewAdapter;
import com.scnu.yxp.travelapp.adapter.MyDairyGalleryAdapter;
import com.scnu.yxp.travelapp.homepage.HomePageActivity;
import com.scnu.yxp.travelapp.image.ImageActivity;
import com.scnu.yxp.travelapp.info.PersonalInfo;
import com.scnu.yxp.travelapp.newedit.EditDairy;
import com.scnu.yxp.travelapp.tool.AsyncImageLoader;
import com.scnu.yxp.travelapp.tool.Utility;
import com.scnu.yxp.travelapp.tool.httpUtil;
import com.scnu.yxp.travelapp.view.MyListView;
import com.scnu.yxp.travelapp.view.PhotoImageView;

public class MyDairyGallery extends Activity {
	private ImageView backBtn;
	private TextView title;
	private PhotoImageView headPhoto;
	private MyListView mListView;
	private MyDairyGalleryAdapter mAdapter;
	private ImageButton newBtn;
	private Button shareBtn;
	private ImageView reviseTitleBtn;
	public static int TITLE_REVISE = 1;
	private String img_url, email;
	private String response;
	private List<Map<String, String>> list;
	private ProgressDialog pd;
	private String reviseTitle;
	private int position;
	/** 
	 * 判断是可以修改标题
	 */
	private int flag = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.me_dairy_gallery);
		Intent intent = getIntent();
		position = intent.getIntExtra("position", 0);
		img_url = intent.getStringExtra("img_url_s");
		email = intent.getStringExtra("email");
		flag = intent.getIntExtra("flag", 0);
		backBtn = (ImageView)findViewById(R.id.me_dairy_gallery_back_btn);
		title = (TextView) findViewById(R.id.me_dairy_gallery_title);
		title.setText(intent.getStringExtra("title"));
		headPhoto = (PhotoImageView) findViewById(R.id.me_dairy_gallery_photo);
		headPhoto.setImageDrawable(AsyncImageLoader.loadImageFromUrl(img_url));
		mListView = (MyListView) findViewById(R.id.me_dairy_gallery_listView);
		newBtn = (ImageButton) findViewById(R.id.me_dairy_gallery_newBtn);
		shareBtn = (Button) findViewById(R.id.me_dairy_gallery_share_btn);
		reviseTitleBtn = (ImageView) findViewById(R.id.me_dairy_gallery_revise_title);
		if(flag == 0)
		{
			reviseTitleBtn.setVisibility(View.GONE);
		}
		pd = new ProgressDialog(this);
		pd.setTitle("加载中");
		pd.setMessage("加载中，请稍后");
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		if(PersonalInfo.headBitmap != null)
			headPhoto.setImageBitmap(PersonalInfo.headBitmap);
		setListener();
		getContent();
	}
	
	public void getContent()
	{
		pd.show();
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				response=httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=note_click",
						"email="+email+"&title="+title.getText().toString());
				
				Log.i("T",response);
				try {
					JSONTokener jsonPaser = new JSONTokener(response);
					jsonPaser.nextValue();
					JSONObject obj = (JSONObject)jsonPaser.nextValue();
					JSONArray jsonObjs = obj.getJSONArray("jsonarray");//new JSONObject(response).getJSONArray("jsonarray");
					list = new ArrayList<Map<String, String>>();
					Log.i("size",jsonObjs.length()+"");
					for(int i = 0; i < jsonObjs.length(); i++){ 
						Map<String, String> listitemn = new HashMap<String, String>();
						JSONObject jsonObj = jsonObjs.getJSONObject(i);
						listitemn.put("content",jsonObj.getString("content"));
						listitemn.put("img_url",jsonObj.getString("img_url1_s"));
						listitemn.put("date",jsonObj.getString("date"));
						listitemn.put("id",jsonObj.getString("id"));
						list.add(listitemn);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0x120);
			}});
		thread.start();
	}
	public void setListener() {
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("position", position);
				bundle.putString("title", title.getText().toString());
				setResult(HomePageActivity.MY_DAIRY_GALLERY,intent);
				intent.putExtras(bundle);
				finish();
			}
		});
		newBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MyDairyGallery.this, EditDairy.class);
				startActivity(intent);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(MyDairyGallery.this,MyDairyDetail.class);
				startActivity(intent);
			}
		});
		shareBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), MyDairyGalleryShare.class);
				startActivity(intent);
			}
		});
		headPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
				intent.putExtra("type", "head");
				startActivity(intent);
			}
		});
		reviseTitleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), TitleRivse.class);
				startActivityForResult(intent, TITLE_REVISE);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == TITLE_REVISE)
		{
			reviseTitle = data.getStringExtra("title");
			new Thread(new Runnable(){

				@Override
				public void run() {
					response = httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=title_revise",
							"email="+email+"&title="+title.getText().toString()
							+"&new_title="+reviseTitle);
					Log.i("T", response);
					handler.sendEmptyMessage(0X121);
				}}).start();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private Handler handler =new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x120)
			{
				mAdapter = new MyDairyGalleryAdapter(MyDairyGallery.this,mListView, list);
				mListView.setAdapter(mAdapter);
				pd.cancel();
			}
			if(msg.what == 0x121)
			{
				if(response.contains("success"))
				{
					title.setText(reviseTitle);
					Toast.makeText(getApplicationContext(), "标题修改成功", Toast.LENGTH_SHORT).show();
					
				}else{
					Toast.makeText(getApplicationContext(), "标题修改失败", Toast.LENGTH_SHORT).show();
				}
			}
		}};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("position", position);
			bundle.putString("title", title.getText().toString());
			intent.putExtras(bundle);
			setResult(HomePageActivity.MY_DAIRY_GALLERY,intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	};
	@Override
	protected void onStop() {
		super.onStop();
	}
		
		
}
