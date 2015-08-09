package com.scnu.yxp.travelapp.imagepicker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.imagepicker.ListImageDirPopupWindow.OnImageDirSelected;
import com.scnu.yxp.travelapp.newedit.EditDairy;

public class ImagePicker extends Activity implements OnImageDirSelected{
	private GridView mGridView;
	private MyAdapter mAdapter;
	private ImageView backBtn;
	private TextView fileNameText;
	private RelativeLayout bottomLayout;
	private Button finishBtn;
	private ProgressDialog progressDialog;
	private ListImageDirPopupWindow mListImageDirPopupWindow;
    public static final int IMAGE_PICKER = 200;
	/**
	 * 屏幕的高度
	 */
	private int screenHeight;
	/**
	 * 存储带有图片的文件夹
	 */
	private List<ImageFolder> imageFolders = new ArrayList<ImageFolder> (); 
	/**
	 * 所有的图片
	 */
	private List<String> allImagePath = new ArrayList<String>();
	/**
	 * 文件加中的第一张图片
	 */
	private String firstImageFilePath;
	/**
	 * 所有图片文件加中的第一张图片
	 */
	private String firstImage;
	/**
	 * 图片总数
	 */
	private int imageTotalCount = 0;
	/**
	 * 用来防止重复记录有图片的文件夹
	 */
	private HashSet<String> dirPaths = new HashSet<String>();
	/**
	 * 当前打开的文件
	 */
	private File curruntFile;
	/**
	 * 当前图片的名称(路径)
	 */
	private List<String> curruntImageName = new ArrayList<String>();
	/**
	 * 当前展示的是所有图片还是文件夹
	 */
	private boolean isAllImage = true;
	/**
	 * 设置当前能选择的数量
	 */
	public static int selectCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_picker);
		//获取屏幕分辨率
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screenHeight = outMetrics.heightPixels;
		initView();
		getImage();
		setListener();
	}
	/**
	 * 设置监听事件
	 */
	public void setListener() {
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		fileNameText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mListImageDirPopupWindow.setAnimationStyle(R.style.ImagePickerPopupWindowStyle);
				mListImageDirPopupWindow.showAsDropDown(bottomLayout, 0, 0);
		
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
		finishBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				setResult(IMAGE_PICKER, intent);
				finish();
			}
		});
	}
	/**
	 * 初始化控件
	 */
	public void initView() {
		mGridView = (GridView) findViewById(R.id.activity_image_picker_gridview);
		backBtn = (ImageView) findViewById(R.id.activity_image_picker_back_btn);
		fileNameText = (TextView) findViewById(R.id.activity_image_picker_filename);
		bottomLayout = (RelativeLayout) findViewById(R.id.activity_image_picker_bottomlayout);
		finishBtn = (Button) findViewById(R.id.activity_image_picker_finishbtn);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("正在扫描图片");
		progressDialog.setMessage("请耐心等待...");
	}
	/** 
	 * 利用ContentProvider扫描手机中的图片
	 */
	public void getImage() {
		// 判断SD卡是否存在,并且是否具有读写权限 
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		//显示进度条
		progressDialog.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//查询媒体数据的URI
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver contentResolve = ImagePicker.this.getContentResolver();
				//只查询JPG和PNG的图片
				Cursor cursor = contentResolve.query(mImageUri, null, 
						MediaStore.Images.Media.MIME_TYPE + "=? or " + 
								 MediaStore.Images.Media.MIME_TYPE + "=?", 
								new String[]{"image/jpeg","image/png"}, 
								MediaStore.Images.Media.DATE_MODIFIED);
				while(cursor.moveToNext())
				{
					//获取图片路径
					String filePath = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					if(firstImage == null)
						firstImage = filePath;
					if(firstImageFilePath == null)
					{
						firstImageFilePath = filePath;
					}
					//获取该图片的父路径
					File parentFile = new File(filePath).getParentFile();
					String parentPath = parentFile.getAbsolutePath();
					//存储图片的文件夹
					ImageFolder imageFolder = null;
					//已经查找过的文件夹就不再重复
					if(dirPaths.contains(parentPath))
					{
						continue;
					}else{
						imageFolder = new ImageFolder();
						dirPaths.add(parentPath);
						imageFolder.setFirstImagePath(filePath);
						imageFolder.setDir(parentPath);
						imageFolder.setSelected(false);
						String[] imageFile = parentFile.list(new FilenameFilter() {
							
							@Override
							public boolean accept(File dir, String name) {
								if(name.endsWith(".jpeg") || name.endsWith(".png")
										|| name.endsWith(".jpg"))
									return true;
								return false;
							}
						});
						for(String s: imageFile)
						{
							allImagePath.add(parentPath + "/" + s);
						}
						int picNum = imageFile.length;
						imageFolder.setCount(picNum);
						imageTotalCount += picNum;
						imageFolders.add(imageFolder);
						//清空第一张图片
						firstImageFilePath = null;
					}
				}
				cursor.close();
				//扫描完后可以释放辅助的dirPath的内存
				dirPaths = null;
				//通知handler图片扫描完了
				handler.sendEmptyMessage(0x123);
			}
		}).start();
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			initGridView();
			initListDirPopupWindw();
			progressDialog.dismiss();
		}
	};
	/** 
	 * 初始化gridview,显示所有图片
	 */
	private void initGridView() {
		if(allImagePath.size() <= 0 )
		{
			Toast.makeText(getApplicationContext(), "没有扫描到图片",
					Toast.LENGTH_SHORT).show();
			return;
		}
		curruntImageName = allImagePath;
		isAllImage = true;
		createTakePhotoBtn();
		mAdapter = new MyAdapter(getApplicationContext(), allImagePath,
				R.layout.image_picker_gridview_item, "");
		mGridView.setAdapter(mAdapter);
		
	}
	/**
	 * 初始化文件夹的popupwindow
	 */
	public void initListDirPopupWindw() {
		ImageFolder folder = new ImageFolder();
		folder.setFirstImagePath(firstImage);
		folder.setCount(imageTotalCount);
		folder.setDir("");
		folder.setSelected(true);
		imageFolders.add(0, folder);
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (screenHeight * 0.7),
				imageFolders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.image_picker_list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	public void selected(ImageFolder folder) {
		for(ImageFolder f: imageFolders)
		{
			f.setSelected(false);
		}
		folder.setSelected(true);
		if(folder.getName().equals("所有图片"))
		{
			isAllImage = true;
			createTakePhotoBtn();
			mAdapter = new MyAdapter(getApplicationContext(), allImagePath, 
					R.layout.image_picker_gridview_item, "");
			mGridView.setAdapter(mAdapter);
			fileNameText.setText(folder.getName());
		}else{
			isAllImage = false;
			curruntFile = new File(folder.getDir());
			curruntImageName = Arrays.asList(curruntFile.list(new FilenameFilter()
			{
				@Override
				public boolean accept(File dir, String filename)
				{
					if (filename.endsWith(".jpg") || filename.endsWith(".png")
							|| filename.endsWith(".jpeg"))
						return true;
					return false;
				}
			}));
			createTakePhotoBtn();
			/**
			 * 将文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
			 */
			mAdapter = new MyAdapter(getApplicationContext(), curruntImageName,
					R.layout.image_picker_gridview_item, curruntFile.getAbsolutePath());
			mGridView.setAdapter(mAdapter);
			fileNameText.setText(folder.getName());
		}
		mListImageDirPopupWindow.dismiss();
	}

	/**
	 * 增加拍照的按钮
	 */
	public void createTakePhotoBtn(){
		if(!curruntImageName.contains("takephoto")){
			curruntImageName = new ArrayList<>(curruntImageName);
			curruntImageName.add(0,"takephoto");
		}
		if(!allImagePath.contains("takephoto")){
			allImagePath.add(0,"takephoto");
		}
	}
}
