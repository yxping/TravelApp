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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.adapter.HomePageListViewAdapter;
import com.scnu.yxp.travelapp.me.MyDairyGallery;
import com.scnu.yxp.travelapp.tool.httpUtil;
import com.scnu.yxp.travelapp.view.MyListView;
import com.scnu.yxp.travelapp.view.MyListView.MListViewListener;
import com.scnu.yxp.travelapp.view.MyListView.MoveBtnBarListener;

public class LocationSearchActivity extends Activity implements MListViewListener{
	//按钮
	private ImageView backBtn;
	private MyListView listView;
	private ImageView locationLabel;
	private TextView place;
	//设置适配器
	private HomePageListViewAdapter adapter;
	//回到顶部按钮
	private ImageButton upBtn;
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private ImageButton landscape_btn, food_btn, life_btn;
	private TextView landscape_text, food_text, life_text;
	private int btn_bg_top, btn_bg_bottom;
	private RelativeLayout.LayoutParams btn_bg_params;
	private int locationTop, locationBottom;
	private Handler mHandler;
	private int start = 0;
	private static int refreshCnt = 0;
	//对应的淡入淡出动画
	private AlphaAnimation out_alphaAnimation, in_alphaAnimation, location_out_alphaAnimation,
			location_in_alphaAnimation;
	//动画时间
	private int ALPHAANIMATION_TIME = 500;
	//放大动画
	private ScaleAnimation shrinkAnimation, enlargeAnimation;
	private LinearLayout landscape_layout, food_layout, life_layout, location_layout;
	private int BTN_MARGIN_TOP_MAX, BTN_MARGIN_TOP_MIN;
	private String response;
	//常量
	private final String LANDSCAPE = "landscape", FOOD = "food", LIFE = "life";
	private String item = "";
	private Handler contentHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x120)
			{
				adapter = new HomePageListViewAdapter(LocationSearchActivity.this, list, listView);
				listView.setAdapter(adapter);
				pd.cancel();
			}
			if(msg.what == 0x121)
			{
				adapter.notifyDataSetChanged();
			}
			if(msg.what == 0x122)
			{
				Toast.makeText(LocationSearchActivity.this, "已经加载完啦！", Toast.LENGTH_SHORT).show();
			}
			if(msg.what == 0x123)
			{
				Toast.makeText(LocationSearchActivity.this, "暂无数据!", Toast.LENGTH_SHORT).show();
				pd.cancel();
			}
		}

	};

	private String startId , endId;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_location_search);
		BTN_MARGIN_TOP_MAX = (int) getResources().getDimension(R.dimen.location_search_btn_margin_top);
		BTN_MARGIN_TOP_MIN = -10;
		listView = (MyListView)findViewById(R.id.listView);
		upBtn = (ImageButton)findViewById(R.id.up_btn_locationActivity);
		backBtn = (ImageView)findViewById(R.id.back_btn);
		locationLabel = (ImageView)findViewById(R.id.location_label);
		place = (TextView)findViewById(R.id.location_search_location_place);
		landscape_btn = (ImageButton)findViewById(R.id.location_search_landscape_btn);
		life_btn = (ImageButton)findViewById(R.id.location_search_life_btn);
		food_btn = (ImageButton)findViewById(R.id.location_search_food_btn);
		landscape_text = (TextView) findViewById(R.id.location_search_landscape_text);
		life_text = (TextView)findViewById(R.id.location_search_life_text);
		food_text = (TextView)findViewById(R.id.location_search_food_text);
		landscape_layout = (LinearLayout)findViewById(R.id.location_search_landscape_layout);
		food_layout = (LinearLayout)findViewById(R.id.location_search_food_layout);
		life_layout = (LinearLayout)findViewById(R.id.location_search_life_layout);
		location_layout = (LinearLayout)findViewById(R.id.location_search_location_layout);
		listView.setMListViewListener(this);
		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(true);
		mHandler = new Handler();

		shrinkAnimation = new ScaleAnimation(1, 0.7f, 1, 0.7f, Animation.RELATIVE_TO_SELF, 0.5F,
				Animation.RELATIVE_TO_SELF, 0.5F);
		shrinkAnimation.setDuration(ALPHAANIMATION_TIME);
		shrinkAnimation.setFillAfter(true);
		enlargeAnimation = new ScaleAnimation(0.7f, 1, 0.7f, 1, Animation.RELATIVE_TO_SELF, 0.5F,
				Animation.RELATIVE_TO_SELF, 0.5F);
		enlargeAnimation.setDuration(ALPHAANIMATION_TIME);
		enlargeAnimation.setFillAfter(true);
		out_alphaAnimation = new AlphaAnimation(1, 0);
		out_alphaAnimation.setDuration(ALPHAANIMATION_TIME);
		//执行完动作后停留在那个状态
		out_alphaAnimation.setFillAfter(true);
		out_alphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				landscape_text.setVisibility(View.GONE);
				food_text.setVisibility(View.GONE);
				life_text.setVisibility(View.GONE);
			}
		});
		in_alphaAnimation = new AlphaAnimation(0, 1);
		in_alphaAnimation.setDuration(ALPHAANIMATION_TIME);
		in_alphaAnimation.setFillAfter(true);
		in_alphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				landscape_text.setVisibility(View.VISIBLE);
				food_text.setVisibility(View.VISIBLE);
				life_text.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
			}
		});
		location_out_alphaAnimation = new AlphaAnimation(1, 0);
		location_out_alphaAnimation.setDuration(ALPHAANIMATION_TIME);
		//执行完动作后停留在那个状态
		location_out_alphaAnimation.setFillAfter(true);
		location_out_alphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				location_layout.setVisibility(View.GONE);
			}
		});
		location_in_alphaAnimation = new AlphaAnimation(0, 1);
		location_in_alphaAnimation.setDuration(ALPHAANIMATION_TIME);
		location_in_alphaAnimation.setFillAfter(true);
		location_in_alphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				location_layout.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
			}
		});
		pd = new ProgressDialog(this);
		pd.setTitle("加载中");
		pd.setMessage("正在努力加载中，请稍后...");
		pd.show();
		getContent();
		setListener();

	}

	public void getContent()
	{
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				String locationText = place.getText().toString();
				try {
					locationText = URLDecoder.decode(locationText, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				response=httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=searching",
						"location="+locationText+"&sign="+item);
				Log.i("T",locationText+response);
				if(!response.contains("jsonarray"))
				{
					contentHandler.sendEmptyMessage(0X123);
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
							if(i == 0)
								startId = jsonObj.getString("id");
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

	public void setListener()
	{
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		upBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//回到顶部,listview需要用setSelection,ScrollView要用scrollto
				listView.setSelection(0);
				upBtn.setVisibility(View.GONE);
			}
		});
		landscape_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				item = LANDSCAPE;
				pd.show();
				getContent();
			}
		});
		food_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				item = FOOD;
				pd.show();
				getContent();
			}
		});
		life_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				item = LIFE;
				pd.show();
				getContent();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("email", list.get(position).get("email"));
				bundle.putString("title", list.get(position).get("title"));
				bundle.putString("image", list.get(position).get("img_url_s"));
				bundle.putInt("position", position);
				bundle.putInt("flag", 0);
				Intent intent = new Intent(getApplicationContext(), MyDairyGallery.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		listView.setMoveBtnBarListener(new MoveBtnBarListener() {

			@Override
			public void onMove(float deltaY) {
				RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) landscape_layout.getLayoutParams();
				if(params1.topMargin + deltaY < BTN_MARGIN_TOP_MAX && params1.topMargin + deltaY > BTN_MARGIN_TOP_MIN)
				{
					params1.setMargins(params1.leftMargin, (int) (params1.topMargin + deltaY), params1.rightMargin, params1.bottomMargin);
					landscape_layout.setLayoutParams(params1);
					params1 = (RelativeLayout.LayoutParams) food_layout.getLayoutParams();
					params1.setMargins(params1.leftMargin, (int) (params1.topMargin + deltaY), params1.rightMargin, params1.bottomMargin);
					food_layout.setLayoutParams(params1);
					params1 = (RelativeLayout.LayoutParams) life_layout.getLayoutParams();
					params1.setMargins(params1.leftMargin, (int) (params1.topMargin + deltaY), params1.rightMargin, params1.bottomMargin);
					life_layout.setLayoutParams(params1);
				}else if(params1.topMargin + deltaY >= BTN_MARGIN_TOP_MAX)
				{
					params1.setMargins(params1.leftMargin, BTN_MARGIN_TOP_MAX, params1.rightMargin, params1.bottomMargin);
					landscape_layout.setLayoutParams(params1);
					params1 = (RelativeLayout.LayoutParams) food_layout.getLayoutParams();
					params1.setMargins(params1.leftMargin, BTN_MARGIN_TOP_MAX, params1.rightMargin, params1.bottomMargin);
					food_layout.setLayoutParams(params1);
					params1 = (RelativeLayout.LayoutParams) life_layout.getLayoutParams();
					params1.setMargins(params1.leftMargin, BTN_MARGIN_TOP_MAX, params1.rightMargin, params1.bottomMargin);
					life_layout.setLayoutParams(params1);
				}else if(params1.topMargin + deltaY <= BTN_MARGIN_TOP_MIN)
				{
					params1.setMargins(params1.leftMargin, BTN_MARGIN_TOP_MIN, params1.rightMargin, params1.bottomMargin);
					landscape_layout.setLayoutParams(params1);
					params1 = (RelativeLayout.LayoutParams) food_layout.getLayoutParams();
					params1.setMargins(params1.leftMargin, BTN_MARGIN_TOP_MIN, params1.rightMargin, params1.bottomMargin);
					food_layout.setLayoutParams(params1);
					params1 = (RelativeLayout.LayoutParams) life_layout.getLayoutParams();
					params1.setMargins(params1.leftMargin, BTN_MARGIN_TOP_MIN, params1.rightMargin, params1.bottomMargin);
					life_layout.setLayoutParams(params1);
				}

			}

			@Override
			public void onTextOut() {
				landscape_text.startAnimation(out_alphaAnimation);
				life_text.startAnimation(out_alphaAnimation);
				food_text.startAnimation(out_alphaAnimation);
				location_layout.startAnimation(location_out_alphaAnimation);
			}

			@Override
			public void onTextIn() {
				landscape_text.startAnimation(in_alphaAnimation);
				life_text.startAnimation(in_alphaAnimation);
				food_text.startAnimation(in_alphaAnimation);
				location_layout.startAnimation(location_in_alphaAnimation);
			}

			@Override
			public void onBtnShrink() {
				life_layout.startAnimation(shrinkAnimation);
				food_layout.startAnimation(shrinkAnimation);
				landscape_layout.startAnimation(shrinkAnimation);
			}

			@Override
			public void onBtnEnlarge() {
				life_layout.startAnimation(enlargeAnimation);
				food_layout.startAnimation(enlargeAnimation);
				landscape_layout.startAnimation(enlargeAnimation);
			}

			@Override
			public int getBtnMargin() {
				RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) landscape_layout.getLayoutParams();
				return params1.topMargin;
			}
		});
	}

	private void geneItems() {
		for (int i = 0; i != 5; ++i) {


		}
	}

	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("刚刚");
	}


	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getContent();
//				start = ++refreshCnt;
//				list.clear();
//				geneItems();
//				// mAdapter.notifyDataSetChanged();
//				adapter = new HomePageListViewAdapter(LocationSearchActivity.this, list, listView);				
//				listView.setAdapter(adapter);
				onLoad();
			}
		}, 2000);
	}


	/**
	 * 加载更多
	 */
	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getMoreContent();
//				geneItems();
//				adapter.notifyDataSetChanged();
				onLoad();
			}
		}, 2000);
	}

	public void getMoreContent()
	{
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				String locationText = place.getText().toString();
				try {
					locationText = URLDecoder.decode(locationText, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				response=httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=searching_down",
						"location="+locationText+"&id="+endId+"&sign="+item);
				Log.i("T",endId+response);
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
					contentHandler.sendEmptyMessage(0X121);
				}
			}});
		thread.start();
	}
}
