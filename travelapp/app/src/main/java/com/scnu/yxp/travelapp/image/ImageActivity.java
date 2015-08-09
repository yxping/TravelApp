package com.scnu.yxp.travelapp.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.imageclip.ClipZoomImageView;
import com.scnu.yxp.travelapp.imagepicker.ImageLoader;
import com.scnu.yxp.travelapp.info.PersonalInfo;
import com.scnu.yxp.travelapp.newedit.EditDairy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends Activity implements OnClickListener{
	private RelativeLayout layout, titleBar;
	private ClipZoomImageView image1, image2, blankImage;
	private ImageView backBtn, rubbishBtn;
	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;
	private List<View> pagerList;
	private List<String> fileList;
	private String type;
	private Animation bar_in_anim, bar_out_anim;
	private TextView countText;
	/**
	 * 图片数量
	 */
	private int imgCount = 0;
	/**
	 * 指向第几张图片
	 */
	private int imgPointer = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image);
		layout = (RelativeLayout) findViewById(R.id.activity_image_layout);
		backBtn = (ImageView) findViewById(R.id.activity_imag_back_btn);
		rubbishBtn = (ImageView) findViewById(R.id.activity_image_rubbish);
		viewPager = (ViewPager) findViewById(R.id.activity_image_viewPager);
		titleBar = (RelativeLayout) findViewById(R.id.activity_image_title_bar);
		countText = (TextView) findViewById(R.id.activity_image_counts);
		//空白的图片
		blankImage = new ClipZoomImageView(this);
		//这是个view事件的观察者   当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，所要调用的回调函数的接口类
		titleBar.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(titleBar.getWidth(),
								titleBar.getHeight());
						params.topMargin = getStatusHeight();
						titleBar.setLayoutParams(params);
						titleBar.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						startBarAnimOut();
					}
				});
		setTitleBarAnim();
		backBtn.setOnClickListener(this);
		layout.setOnClickListener(this);
		rubbishBtn.setOnClickListener(this);
		initAttr();
		initViewPager();
	}

	public void initAttr() {
		Intent intent = getIntent();
		if(intent.getExtras() != null) {
			type = intent.getStringExtra("type");
		}else
			return;
		if(type.equals("head")) {
			if (PersonalInfo.headBitmap != null) {
				blankImage.setImageBitmap(PersonalInfo.headBitmap);
			}
			countText.setVisibility(View.GONE);
			rubbishBtn.setVisibility(View.GONE);
		}else if(type.equals("local")) {
			fileList = EditDairy.testPathList;
			imgCount = fileList.size();
			imgPointer = 0;
			countText.setText(imgPointer + 1 + "/" + imgCount);
		}else if(type.equals("network")) {
			fileList = (ArrayList<String>)intent.getSerializableExtra("list");
			imgCount = fileList.size();
			imgPointer = 0;
			countText.setText(imgPointer + 1 + "/" + imgCount);
			rubbishBtn.setVisibility(View.GONE);
		}

	}

	public void initViewPager(){
		pagerList = new ArrayList<View>();
		if(type.equals("head")) {
			pagerList.add(blankImage);
		}else if(type.equals("local")) {
			for(String path : fileList) {
				ClipZoomImageView view = new ClipZoomImageView(this);
				ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(path, view);
				pagerList.add(view);
			}
		}else if(type.equals("network")) {
			for(String path : fileList) {
				ClipZoomImageView view = new ClipZoomImageView(this);
				ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(path, view);
				pagerList.add(view);
			}
		}
		pagerAdapter = new PagerAdapter() {
			@Override
			public int getCount() {
				return pagerList.size();
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				//装载有用的page页面
				((ViewPager)container).addView(pagerList.get(position));
				return pagerList.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
									Object object) {
				//销毁没有用的page页面
				((ViewPager)container).removeView(pagerList.get(position));
			}
		};
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);
	}

	/**
	 * 预加载动画效果
	 */
	public void setTitleBarAnim(){
		bar_in_anim = AnimationUtils.loadAnimation(this, R.anim.image_activity_bar_in);
		bar_in_anim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {titleBar.setVisibility(View.VISIBLE);}
			@Override
			public void onAnimationEnd(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
		bar_out_anim = AnimationUtils.loadAnimation(this, R.anim.image_activity_bar_out);
		bar_out_anim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				titleBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				titleBar.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
	}

	/**
	 * 开始bar的上滑消失动画
	 */
	public void startBarAnimIn(){
		//titleBar.setVisibility(View.VISIBLE);
		titleBar.startAnimation(bar_in_anim);
	}
	/**
	 * 开始bar的下滑显示动画
	 */
	public void startBarAnimOut(){
		titleBar.startAnimation(bar_out_anim);
	}

	/**
	 * 获得状态栏的高度
	 * @return
	 */
	public int getStatusHeight(){
		int statusHeight = 0;
		Rect localRect = new Rect();
		ImageActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight){
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusHeight = ImageActivity.this.getResources().getDimensionPixelSize(i5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}


	@Override
	public void onClick(View view) {
		if(view.getId() == backBtn.getId())
			finish();
		if(view.getId() == rubbishBtn.getId())
		{

		}
	}

	/**
	 * 生成对应Intent
	 * @param context
	 * @param type
	 * @param list
	 * @return
	 */
	public static Intent makeIntent(Context context, String type, List<String> list) {
		Intent intent = new Intent(context, ImageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("type",type);
		bundle.putSerializable("list", (Serializable)list);
		intent.putExtras(bundle);
		return intent;
	}

}
