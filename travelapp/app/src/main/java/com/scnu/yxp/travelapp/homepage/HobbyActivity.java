package com.scnu.yxp.travelapp.homepage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.adapter.HomePageListViewAdapter;
import com.scnu.yxp.travelapp.info.IntentKey;
import com.scnu.yxp.travelapp.me.MyDairyGallery;
import com.scnu.yxp.travelapp.tool.httpUtil;
import com.scnu.yxp.travelapp.view.MyListView;
import com.scnu.yxp.travelapp.view.MyListView.MListViewListener;
import com.scnu.yxp.travelapp.view.MyListView.MoveBtnBarListener;

public class HobbyActivity extends Activity {
	//按钮
	private ImageView backBtn,  searchBtn;
	//常量
	private final String LANDSCAPE = "landscape", FOOD = "food", LIFE = "life";
	//listview
	private MyListView listView;
	private TextView contentText;
	private EditText contentEdit;
	//是否出现了搜索框
	private boolean isContentShow = false;
	private String item;
	//设置适配器
	private HomePageListViewAdapter adapter;
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private String response;
	private Handler handler;
	private ProgressDialog pd;
	private String endId;
	private Handler contentHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what)
			{
				case 0x120:
					pd.cancel();
					adapter = new HomePageListViewAdapter(HobbyActivity.this, list, listView);
					listView.setAdapter(adapter);
					break;
				case 0x121:
					Toast.makeText(HobbyActivity.this, "暂无数据!", Toast.LENGTH_SHORT).show();
					pd.cancel();
					break;
				case 0x122:
					Toast.makeText(HobbyActivity.this, "已经加载完啦！", Toast.LENGTH_SHORT).show();
					pd.cancel();
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_hobby);
		Intent intent = getIntent();
		item = intent.getStringExtra("item");
		backBtn = (ImageView)findViewById(R.id.back_btn);
		searchBtn = (ImageView)findViewById(R.id.search_btn);
		listView = (MyListView)findViewById(R.id.listView);
		contentText = (TextView)findViewById(R.id.hobby_content_text);
		contentEdit = (EditText)findViewById(R.id.hobby_content);
		pd = new ProgressDialog(this);
		pd.setTitle("加载中");
		pd.setMessage("正在加载中，请稍后...");
		//更新主题
		switch (item)
		{
			case LANDSCAPE:
				contentText.setText(getResources().getString(R.string.item1));
				break;
			case FOOD:
				contentText.setText(getResources().getString(R.string.item2));
				break;
			case LIFE:
				contentText.setText(getResources().getString(R.string.item3));
				break;
			default:
				break;
		}
		//listView不允许下拉刷新
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(true);
		listView.setTopLayoutGone();
		listView.setMoveBtnBarListener(new MoveBtnBarListener() {
			public void onTextOut() {
			}
			public void onTextIn() {
			}
			public void onMove(float deltaY) {
			}
			public void onBtnShrink() {
			}
			public void onBtnEnlarge() {
			}
			public int getBtnMargin() {
				return 0;
			}
		});
		handler = new Handler();
		//设置监听
		setListener();
		pd.show();
		getContent();
	}


	public void getContent()
	{
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				response=httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=searching",
						"sign="+item+"&location="+"");
				Log.i("T",response);
				if(!response.contains("jsonarray"))
				{
					contentHandler.sendEmptyMessage(0X121);
				}else{
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
							listitemn.put("author",jsonObj.getString("author"));
							listitemn.put("img_url",jsonObj.getString("img_url_s"));
							listitemn.put("headImg",jsonObj.getString("img_head"));
							listitemn.put("date",jsonObj.getString("date"));
							listitemn.put("location",jsonObj.getString("location"));
							listitemn.put("title",jsonObj.getString("title"));
							listitemn.put("readTime", "100"+"次");
							listitemn.put("allDay","5"+"天");
							listitemn.put("id",jsonObj.getString("id"));
							if(i == jsonObjs.length() - 1)
								endId = jsonObj.getString("id");
							listitemn.put("email",jsonObj.getString("email"));
							list.add(listitemn);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					contentHandler.sendEmptyMessage(0x120);
				}

			}});
		thread.start();
	}

	public void setListener()
	{
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(isContentShow)
				{
					contentEdit.setVisibility(View.GONE);
					contentText.setVisibility(View.VISIBLE);
					isContentShow = false;
				}else{
					finish();
				}
			}
		});
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(!isContentShow)
				{
					contentEdit.setVisibility(View.VISIBLE);
					contentText.setVisibility(View.GONE);
					isContentShow = true;
				}else{

				}
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("email", list.get(position - 1).get("email"));
				bundle.putString("title", list.get(position - 1).get("title"));
				bundle.putString("image", list.get(position - 1).get("img_url_s"));
				bundle.putInt("position", position - 1);
				bundle.putInt("flag", 0);
				Intent intent = new Intent(getApplicationContext(), MyDairyGallery.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		listView.setMListViewListener(new MListViewListener() {

			@Override
			public void onRefresh() {

			}

			@Override
			public void onLoadMore() {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						listView.stopLoadMore();
					}
				}, 2000);
			}
		});
	}

	public void getMoreContent()
	{
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
//				String locationText = place.getText().toString();
//				try {
//					locationText = URLDecoder.decode(locationText, "UTF-8");
//				} catch (UnsupportedEncodingException e1) {
//					e1.printStackTrace();
//				}
				response=httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=searching_down",
						"location="+""+"&id="+endId+"&sign="+item);
				if(!response.contains("jsonarray"))
				{
					contentHandler.sendEmptyMessage(0X122);
				}else{
					try {
						JSONTokener jsonPaser = new JSONTokener(response);
						jsonPaser.nextValue();
						JSONObject obj = (JSONObject)jsonPaser.nextValue();
						JSONArray jsonObjs = obj.getJSONArray("jsonarray");//new JSONObject(response).getJSONArray("jsonarray");
						Log.i("size",jsonObjs.length()+"");
						for(int i = 0; i < jsonObjs.length(); i++){
							Map<String, String> listitemn = new HashMap<String, String>();
							JSONObject jsonObj = jsonObjs.getJSONObject(i);
							listitemn.put("author",jsonObj.getString("author"));
							listitemn.put("img_url",jsonObj.getString("img_url_s"));
							listitemn.put("headImg",jsonObj.getString("img_head"));
							listitemn.put("date",jsonObj.getString("date"));
							listitemn.put("location",jsonObj.getString("location"));
							listitemn.put("title",jsonObj.getString("title"));
							listitemn.put("readTime", "100"+"次");
							listitemn.put("allDay","5"+"天");
							listitemn.put("id",jsonObj.getString("id"));
							if(i == jsonObjs.length() - 1)
								endId = jsonObj.getString("id");
							listitemn.put("email",jsonObj.getString("email"));
							list.add(listitemn);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					contentHandler.sendEmptyMessage(0X120);
				}
			}});
		thread.start();
	}
}
