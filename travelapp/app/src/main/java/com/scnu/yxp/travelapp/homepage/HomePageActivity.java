package com.scnu.yxp.travelapp.homepage;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.adapter.HomePageListViewAdapter;
import com.scnu.yxp.travelapp.image.ImageActivity;
import com.scnu.yxp.travelapp.imageclip.ImageClipActivity;
import com.scnu.yxp.travelapp.imagepicker.ImagePicker;
import com.scnu.yxp.travelapp.imagepicker.MyAdapter;
import com.scnu.yxp.travelapp.info.IntentKey;
import com.scnu.yxp.travelapp.info.PersonalInfo;
import com.scnu.yxp.travelapp.me.MyCollection;
import com.scnu.yxp.travelapp.me.MyDairy;
import com.scnu.yxp.travelapp.me.MyDairyGallery;
import com.scnu.yxp.travelapp.me.MySettingPage;
import com.scnu.yxp.travelapp.me.NickNameReviseActivity;
import com.scnu.yxp.travelapp.newedit.EditDairy;
import com.scnu.yxp.travelapp.tool.ToByteArray;
import com.scnu.yxp.travelapp.tool.Utility;
import com.scnu.yxp.travelapp.tool.httpUtil;
import com.scnu.yxp.travelapp.view.MListViewHeader;
import com.scnu.yxp.travelapp.view.MyScrollView;
import com.scnu.yxp.travelapp.view.MyScrollView.OnRefreshListener;
import com.scnu.yxp.travelapp.view.PhotoImageView;

public class HomePageActivity extends FragmentActivity {
	//定义滑动页面
	private ViewPager viewPager;
	//设置viewPager中的各个页面
	private View all_diaryPage, newPage, mePage;
	//设置viewPager的按钮
	private ImageView all_diaryBtn, newBtn, meBtn;
	//设置list存放三个页面
	private ArrayList<View> viewPageList ;
	//viewPage的适配器
	private PagerAdapter pagerAdapter;
	//按钮被按后的图片
	private int[] btn_pressed = new int[]{R.drawable.all_diary_pressed, R.drawable.new_btn_pressed,
			R.drawable.me_btn_pressed};
	//按钮被按后的图片
	private int[] btn_normal = new int[]{R.drawable.all_diary, R.drawable.new_btn,R.drawable.me_btn};
	//游记中的listview
	private ListView listView_homepage;
	//homepage的适配器
	private HomePageListViewAdapter adapter_homepage;
	private ArrayList<Map<String, String>> list;
	//homepage的回到顶部的按钮
	private ImageButton upBtn;
	//homepage的滑动view
	private MyScrollView myScrollView;
	private View myScrollViewHeader;
	private View myScrollViewFooter;
	private TextView myScrollViewFooterText;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	private int mState = MListViewHeader.STATE_NORMAL;
	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	private final int ROTATE_ANIM_DURATION = 180;
	private int mFooterViewHeight = 0;
	public final int FOOTER_REFRESHING = 0;
	public final int FOOTER_NORMAL = 1;
	public final int FOOTER_READY = 2;
	private int mFooterState = FOOTER_NORMAL;

	//方便测量上滑的距离然后出现回到顶部的按钮
	private LinearLayout homepage_btn_window;
	//homepage的三个label按钮
	private ImageButton landscapeBtn, foodBtn, lifeBtn;
	//homepage的搜索按钮
	private ImageButton searchBtn;

	//new游记里的控件
	private ListView newListview;
	private SimpleAdapter newAdapter;
	private List<Map<String,String>> newItems;

	//me主页的
	private MyDairy myDairy;
	private MyCollection myCollection;
	private Button myDairyBtn, myCollectionBtn;
	private TextView nickName;
	private LinearLayout layout;
	private ImageView settingBtn;
	private boolean isMyDairyShow = true;
	private RotateAnimation rotateDownAnim, rotateUpAnim;
	private PhotoImageView photoImageView;
	private Uri fileUri;
	//判断是否连按两次退出
	private int isExit = 0;
	private boolean isMeShow = true;
	public static int SETTING_HEAD_IMAGE = 2;
	public static int SETTING_FEED_BACK = 3;
	public static int SETTING_NICK_NAME = 4;
	public static int NICK_NAME_REVISE = 5;
	public static int IMAGE_CLIP = 101;
	public static int MY_DAIRY_GALLERY = 102;

	private ProgressDialog pd;
	private String response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//防止在activity打开时edittext获取焦点
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
		setContentView(R.layout.activity_homepage);
		init();//实例化
		setListener();//设置事件监听
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	//实例化各个类
	public void init()
	{
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		all_diaryBtn = (ImageView)findViewById(R.id.all_diary_btn);
		newBtn = (ImageView)findViewById(R.id.new_btn);
		meBtn = (ImageView)findViewById(R.id.me_btn);
		//通过layoutinflater载入界面
		LayoutInflater inflater = LayoutInflater.from(this);
		//最新游记的界面
		all_diaryPage = inflater.inflate(R.layout.homepage_all_diary, null);
		all_diaryBtn.setImageResource(btn_pressed[0]);
		listView_homepage = (ListView)all_diaryPage.findViewById(R.id.homepage_all_diary_listView);
		upBtn = (ImageButton)all_diaryPage.findViewById(R.id.up_btn);
		myScrollView = (MyScrollView)all_diaryPage.findViewById(R.id.homepage_all_scrollView);
		myScrollViewHeader = (View)all_diaryPage.findViewById(R.id.homepage_all_dairy_scrollview_header);
		myScrollViewFooter = (View)all_diaryPage.findViewById(R.id.homepage_all_dairy_scrollview_footer);
		myScrollViewFooterText = (TextView)all_diaryPage.findViewById(R.id.mlistview_footer_hint_textview);
		//这是个view事件的观察者   当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，所要调用的回调函数的接口类
		all_diaryPage.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mFooterViewHeight = myScrollViewFooter.getHeight();
						all_diaryPage.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		upBtn.setVisibility(View.GONE);
		homepage_btn_window = (LinearLayout)all_diaryPage.findViewById(R.id.homepage_btn_window);
		foodBtn = (ImageButton)all_diaryPage.findViewById(R.id.all_diary_food_btn);
		lifeBtn = (ImageButton)all_diaryPage.findViewById(R.id.all_diary_life_btn);
		landscapeBtn = (ImageButton)all_diaryPage.findViewById(R.id.all_diary_landscapte_btn);
		searchBtn = (ImageButton)all_diaryPage.findViewById(R.id.search_btn);
		//初始化适配器
		pd = new ProgressDialog(this);
		pd.setTitle("加载中");
		pd.setMessage("加载中，请稍后");
		getListViewContent();

		//下拉刷新控件
		mArrowImageView = (ImageView)all_diaryPage.findViewById(R.id.mlistview_header_arrow);
		mHintTextView = (TextView)all_diaryPage.findViewById(R.id.mlistview_header_hint_textview);
		mProgressBar = (ProgressBar)all_diaryPage.findViewById(R.id.mlistview_header_progressbar);
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);


		//我的界面
		mePage = inflater.inflate(R.layout.homepage_me, null);
		layout = (LinearLayout)mePage.findViewById(R.id.homepage_me_fragmentLayout);
		myDairy = new MyDairy();
		myCollection = new MyCollection();
		myDairyBtn = (Button)mePage.findViewById(R.id.myDairyBtn);
		myCollectionBtn = (Button)mePage.findViewById(R.id.myCollectionBtn);
		settingBtn = (ImageView)mePage.findViewById(R.id.me_setting_btn);
		nickName = (TextView)mePage.findViewById(R.id.homepage_me_nickname);
		nickName.setText(PersonalInfo.nickname);
		photoImageView = (PhotoImageView)mePage.findViewById(R.id.me_photoImageView);
		photoImageView.setImageBitmap(PersonalInfo.headBitmap);
//		((BitmapDrawable)photoImageView.getDrawable()).getBitmap().recycle();
		rotateDownAnim = new RotateAnimation(0f,270f,Animation.RELATIVE_TO_SELF,
				0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		rotateDownAnim.setDuration(500);
		rotateDownAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {

			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				settingBtn.setImageResource(R.drawable.me_down_arrow);
				Intent intent = new Intent(HomePageActivity.this, MySettingPage.class);
				startActivityForResult(intent, 1);
			}
		});
		rotateUpAnim = new RotateAnimation(0f,270f,Animation.RELATIVE_TO_SELF,
				0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		rotateUpAnim.setDuration(500);
		rotateUpAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				settingBtn.setImageResource(R.drawable.me_setting_btn);
			}
		});
		//创建新游记
		newPage = inflater.inflate(R.layout.homepage_new, null);
		newListview = (ListView)newPage.findViewById(R.id.newlistView);
		newItems = new ArrayList<Map<String,String>>();
		getMyDairy();

		//通过list将三个界面加入viewPage中
		viewPageList = new ArrayList<View>();
		viewPageList.add(all_diaryPage);
		viewPageList.add(newPage);
		viewPageList.add(mePage);
		//设置viewpage的适配器
		pagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return viewPageList.size();
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				((ViewPager)container).addView(viewPageList.get(position));
				return viewPageList.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
									Object object) {
				//销毁没有用的page页面
				((ViewPager)container).removeView(viewPageList.get(position));
			}

		};
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);
		Log.i("email", PersonalInfo.account);
	}
	/**
	 * 获取首页的内容
	 */
	public void getListViewContent()
	{
		pd.show();
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				String response=httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=return_list_new",
						"");
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
						listitemn.put("author",jsonObj.getString("author"));
						listitemn.put("img_url",jsonObj.getString("img_url_s"));
						listitemn.put("headImg",jsonObj.getString("img_head"));
						listitemn.put("date",jsonObj.getString("date"));
						listitemn.put("location",jsonObj.getString("location"));
						listitemn.put("title",jsonObj.getString("title"));
						listitemn.put("readTime", "100"+"次");
						listitemn.put("allDay","5"+"天");
						listitemn.put("id",jsonObj.getString("id"));
						listitemn.put("email",jsonObj.getString("email"));
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

	public void getMyDairy()
	{
		for(int i = 0 ; i < 2 ; i++)
		{
			HashMap<String, String> item = new HashMap<String, String>();
			if(i == 1)
			{
				item.put("text", "创建新游记");
			}else{
				item.put("text", "继续游记,巴黎游...");
			}

			newItems.add(item);
		}
		newAdapter = new SimpleAdapter(this,newItems,R.layout.homepage_new_listview_item,new String[]{"text"},
				new int[]{R.id.text});
		newListview.setAdapter(newAdapter);
	}


	//设置监听
	public void setListener()
	{
		onMyScrollListener listener = new onMyScrollListener();
		myScrollView.setOnScrollListener(listener);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				setBtnImage(index);
				if(index == 2 && isMeShow)
				{
					getSupportFragmentManager().beginTransaction().replace(layout.getId(), myDairy).commit();
					isMeShow = false;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int index) {

			}
		});
		all_diaryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(0);

			}
		});
		newBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(1);
			}
		});
		meBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(2);

			}
		});
		upBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//滚回到顶部
				myScrollView.scrollTo(10, 0);
				upBtn.setVisibility(View.GONE);
			}
		});
		landscapeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putString("item", "landscape");
				Intent intent = new Intent(HomePageActivity.this, HobbyActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		lifeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putString("item", "life");
				Intent intent = new Intent(HomePageActivity.this, HobbyActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		foodBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putString("item", "food");
				Intent intent = new Intent(HomePageActivity.this, HobbyActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomePageActivity.this, LocationSearchActivity.class);
				startActivity(intent);
			}
		});
		//me
		myDairyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//设置点击后的样式
				if(!isMyDairyShow)
				{
					myDairyBtn.setBackgroundResource(R.drawable.me_blankframe);
					myDairyBtn.setTextColor(getResources().getColor(R.color.sky_blue));
					myCollectionBtn.setBackgroundResource(0);
					myCollectionBtn.setTextColor(Color.WHITE);
					isMyDairyShow = true;
					getSupportFragmentManager().beginTransaction().replace(layout.getId(), myDairy).commit();
				}
			}
		});
		myCollectionBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//设置点击后的样式
				if(isMyDairyShow)
				{
					myCollectionBtn.setBackgroundResource(R.drawable.me_blankframe);
					myCollectionBtn.setTextColor(getResources().getColor(R.color.sky_blue));
					myDairyBtn.setBackgroundResource(0);
					myDairyBtn.setTextColor(Color.WHITE);
					isMyDairyShow = false;
					getSupportFragmentManager().beginTransaction().replace(layout.getId(), myCollection).commit();
				}
			}
		});
		newListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				if(pos == 1)
				{
					Intent intent = new Intent(HomePageActivity.this, EditDairy.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent(HomePageActivity.this, EditDairy.class);
					intent.putExtra("load", "load");
					startActivity(intent);
				}
			}
		});
		settingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				settingBtn.startAnimation(rotateDownAnim);
			}
		});
		photoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
				intent.putExtra("type", "head");
				startActivity(intent);
			}
		});
		listView_homepage.setOnItemClickListener(new OnItemClickListener() {

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
				startActivityForResult(intent, MY_DAIRY_GALLERY);
			}
		});
		myScrollView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void upRefresh() {
				new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						myScrollView.resetFooterView();
						setFooterViewState(FOOTER_NORMAL);
					}},1500);
			}

			@Override
			public void downRefresh() {
				new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						myScrollView.resetHeaderView();
						setState(MListViewHeader.STATE_NORMAL);
					}},1500);
			}
		});

	}

	/**
	 * 设置下拉刷新的状态
	 * @param state
	 */
	public void setState(int state) {
		if (state == mState) return ;

		if (state == MListViewHeader.STATE_REFRESHING) {	// 显示进度
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {	// 显示箭头图片
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}

		switch(state){
			case MListViewHeader.STATE_NORMAL:
				if (mState == MListViewHeader.STATE_READY) {
					mArrowImageView.startAnimation(mRotateDownAnim);
				}
				if (mState == MListViewHeader.STATE_REFRESHING) {
					mArrowImageView.clearAnimation();
				}
				mHintTextView.setText(R.string.mlistview_header_hint_normal);
				break;
			case MListViewHeader.STATE_READY:
				if (mState != MListViewHeader.STATE_READY) {
					mArrowImageView.clearAnimation();
					mArrowImageView.startAnimation(mRotateUpAnim);
					mHintTextView.setText(R.string.mlistview_header_hint_ready);
				}
				break;
			case MListViewHeader.STATE_REFRESHING:
				mHintTextView.setText(R.string.mlistview_header_hint_loading);
				break;
			default:
				break;
		}

		mState = state;
	}

	/**
	 * 获得下拉刷新的状态
	 * @return
	 */
	public int getMState()
	{
		return mState;
	}

	/**
	 * 设置FooterView的状态
	 * @param state
	 */
	public void setFooterViewState(int state){
		mFooterState = state;
		switch(state)
		{
			case FOOTER_NORMAL:
				myScrollViewFooterText.setText(getResources().getString(R.string.mlistview_footer_hint_normal));
				break;
			case FOOTER_REFRESHING:
				myScrollViewFooterText.setText("加载中");
				break;
			case FOOTER_READY:

				myScrollViewFooterText.setText(getResources().getString(R.string.mlistview_footer_hint_ready));
				break;
			default:
				break;
		}
	}

	public int getFooterViewBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)getMyScrollViewFooter().getLayoutParams();
		return lp.bottomMargin;
	}

	public void setBottomMargin(int height) {
		if (height < 0) return ;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)getMyScrollViewFooter().getLayoutParams();
		lp.bottomMargin = height;
		getMyScrollViewFooter().setLayoutParams(lp);
	}

	/**
	 * 获得上拉加载的状态
	 * @return
	 */
	public int getMFooterState()
	{
		return mFooterState;
	}


	public void setDownRefreshVisibleHeight(int height){
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getMyScrollViewHeader()
				.getLayoutParams();
		lp.height = height;
		getMyScrollViewHeader().setLayoutParams(lp);
	}


	//改变btn的图标
	public void setBtnImage(int index)
	{
		switch( index )
		{
			case 0:
				all_diaryBtn.setImageResource(btn_pressed[0]);
				newBtn.setImageResource(btn_normal[1]);
				meBtn.setImageResource(btn_normal[2]);
				break;
			case 1:
				all_diaryBtn.setImageResource(btn_normal[0]);
				newBtn.setImageResource(btn_pressed[1]);
				meBtn.setImageResource(btn_normal[2]);
				break;
			case 2:
				all_diaryBtn.setImageResource(btn_normal[0]);
				newBtn.setImageResource(btn_normal[1]);
				meBtn.setImageResource(btn_pressed[2]);
				break;
			default:
				break;
		}
	}

	//监听滑动距离然后使回到顶部按钮出现
	class onMyScrollListener implements com.scnu.yxp.travelapp.view.MyScrollView.OnScrollListener
	{

		@Override
		public void onScroll(int scrollY) {
			// TODO 
			if(scrollY >= homepage_btn_window.getHeight())
			{
				upBtn.setVisibility(View.VISIBLE);
			}else{
				upBtn.setVisibility(View.GONE);
			}
		}

	}

	public View getMyScrollViewHeader(){
		return myScrollViewHeader;
	}

	public View getMyScrollViewFooter(){
		return myScrollViewFooter;
	}

	public int getMFooterViewHeight(){
		return mFooterViewHeight;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//如果按下返回键的响应事件，退出程序
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			isExit++;
			if(isExit == 2)
			{
				System.exit(0);
			}
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					isExit = 0;
				}
			});
			thread.start();
			Toast.makeText(HomePageActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
		}
		return true;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 1)
			settingBtn.startAnimation(rotateUpAnim);
		if(resultCode == MY_DAIRY_GALLERY)
		{
			list.get(data.getIntExtra("position", 0)).put("title", data.getStringExtra("title"));
			adapter_homepage.notifyDataSetChanged();
		}
		if(requestCode == IntentKey.MY_DIARY_DETEAIL)
		{
			myDairy.changeListAdapter(data);
		}
		if(requestCode == IntentKey.MY_COLLECTION_DETEAL)
		{
			myCollection.changeListAdapter(data);
		}
		if(resultCode == SETTING_FEED_BACK)
		{
//			startActivity(new Intent(getApplicationContext(), FeedBackActivity.class));
		}
		if(resultCode == SETTING_NICK_NAME)
		{
			settingBtn.startAnimation(rotateUpAnim);
			startActivityForResult(new Intent(getApplicationContext(), NickNameReviseActivity.class),
					NICK_NAME_REVISE);
		}
		if(resultCode == NICK_NAME_REVISE)
		{
			nickName.setText(PersonalInfo.nickname);
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					String s = PersonalInfo.nickname;
					try {
						s = URLEncoder.encode(s, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					response = httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=set_name",
							"email=" + PersonalInfo.account + "&user=" + s);
					handler.sendEmptyMessage(0x122);
				}
			});
			thread.start();
		}
		if(resultCode == SETTING_HEAD_IMAGE)
		{
			settingBtn.startAnimation(rotateUpAnim);
			new AlertDialog.Builder(this).setTitle("修改头像").setItems(
					new String[] { "拍照", "从相册中选择" }, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int position) {
							if(position == 0)
							{
								Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								// 创建一个文件夹存放文件
								fileUri = EditDairy.getOutputMediaFileUri(EditDairy.MEDIA_TYPE_IMAGE);

								// 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null，如果此处指定，则后来的data为null
								intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

								startActivityForResult(intent, EditDairy.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
							}else{
								Intent intent = new Intent(getApplicationContext(), ImagePicker.class);
								ImagePicker.selectCount = 1;
								startActivityForResult(intent, ImagePicker.IMAGE_PICKER);
							}

						}

					})
					.setNegativeButton(
							"取消", null).show();
		}
		//如果是图片选择
		if(ImagePicker.IMAGE_PICKER == requestCode && ImagePicker.IMAGE_PICKER == resultCode)
		{
			List<String> path = MyAdapter.mSelectedImage;
			for(String p: path)
			{
				startImageClip(p);
			}
			//及时清空
			MyAdapter.mSelectedImage.clear();
		}
		//及时清空
		MyAdapter.mSelectedImage.clear();
		// 如果是拍照
		if (EditDairy.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode)
		{
			if (RESULT_OK == resultCode)
			{
				if (data != null)
				{
					// 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
					Toast.makeText(this, "Image saved to:\n" + data.getData(),
							Toast.LENGTH_LONG).show();
					if (data.hasExtra("data"))
					{
//                        Bitmap thumbnail = data.getParcelableExtra("data");
						startImageClip(data.getData().getPath());
					}
				}else{
					startImageClip(fileUri.getPath());
				}
			}
			else if (resultCode == RESULT_CANCELED)
			{
				// User cancelled the image capture
			}
			else
			{
				// Image capture failed, advise user
			}
		}
		if(IMAGE_CLIP == requestCode && IMAGE_CLIP == resultCode)
		{
			photoImageView.setImageBitmap(PersonalInfo.headBitmap);
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					byte[] byteArray = ToByteArray.Bitmap2Bytes(PersonalInfo.headBitmap);
					String s = Base64.encodeToString(byteArray, 0, byteArray.length,Base64.DEFAULT);
					try {
						s = URLEncoder.encode(s, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					response = httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=set_head",
							"email=" + PersonalInfo.account + "&image_head=" + s);
					handler.sendEmptyMessage(0x121);
					Log.i("response", response);
				}
			});
			thread.start();

		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private Handler handler =new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x120)
			{
				adapter_homepage = new HomePageListViewAdapter(HomePageActivity.this, list, listView_homepage);
				listView_homepage.setAdapter(adapter_homepage);
				//重新设置listview的高度
				Utility.setListViewHeightBasedOnChildren(listView_homepage);
				pd.cancel();
			}
			if(msg.what == 0x121)
			{
				if(response.contains("success"))
				{
					Toast.makeText(HomePageActivity.this, "头像修改成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(HomePageActivity.this, "头像修改失败", Toast.LENGTH_SHORT).show();
					PersonalInfo.preheadBitmap = PersonalInfo.headBitmap;
					photoImageView.setImageBitmap(PersonalInfo.headBitmap);
				}
			}
			if(msg.what == 0x122)
			{
				if(response.contains("success"))
				{
					Toast.makeText(HomePageActivity.this, "昵称修改成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(HomePageActivity.this, "昵称修改失败", Toast.LENGTH_SHORT).show();
					PersonalInfo.nickname = PersonalInfo.prenickname;
					nickName.setText(PersonalInfo.nickname);
				}
			}
		}};

	public void startImageClip(String path) {
		Intent intent = new Intent(getApplicationContext(), ImageClipActivity.class);
		intent.putExtra("path", path);
		startActivityForResult(intent, IMAGE_CLIP);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}

	public ArrayList<Map<String, String>> getList()
	{
		return list;
	}

}
