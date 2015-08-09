package com.scnu.yxp.travelapp.newedit;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scnu.yxp.travelapp.R;
import com.scnu.yxp.travelapp.homepage.HomePageActivity;
import com.scnu.yxp.travelapp.image.ImageActivity;
import com.scnu.yxp.travelapp.imagepicker.ImagePicker;
import com.scnu.yxp.travelapp.imagepicker.MyAdapter;
import com.scnu.yxp.travelapp.info.PersonalInfo;
import com.scnu.yxp.travelapp.tool.ToByteArray;
import com.scnu.yxp.travelapp.tool.httpUtil;

public class EditDairy extends Activity {
	private ImageView backBtn;
	//飞机图像
//	private ImageView plane;
	//帧动画
//	private AnimationDrawable anim;
	private EditText content;
	private Button okBtn;
	private String response;
	private String neirong = "";
	private String biaoti = "";
	//上传成功时显示的成功图片和动画效果
	private LinearLayout successLayout;
	private AlphaAnimation inAnimation;
	private int animatonTime = 2000;
	private Button labelBtn, cameraBtn, creatImageBtn;
	private ImageView landscapeLabel, foodLabel, lifeLabel;
	private ImageView image1, image2, image3;
	private Uri fileUri;
	private EditText title;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 10;
    private List<Bitmap> bitmapList;
    public static List<String> testPathList = new ArrayList<String>();
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
    private String label1="",label2="",label3="";
    
    private LocationManager locationManager;
    private GpsStatus gpsstatus;
    private NotificationManager manager;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit);
		backBtn = (ImageView)findViewById(R.id.activity_edit_back_btn);
		content = (EditText)findViewById(R.id.activity_edit_content);
		title = (EditText)findViewById(R.id.activity_edit_title);
		successLayout = (LinearLayout) findViewById(R.id.edit_success_picture);
		labelBtn = (Button) findViewById(R.id.new_edit_labelBtn);
		landscapeLabel = (ImageView) findViewById(R.id.new_label_select_landscape);
		foodLabel = (ImageView) findViewById(R.id.new_label_select_food);
		lifeLabel = (ImageView) findViewById(R.id.new_label_select_life);
		image1 = (ImageView) findViewById(R.id.new_edit_image1);
		image2 = (ImageView) findViewById(R.id.new_edit_image2);
		image3 = (ImageView) findViewById(R.id.new_edit_image3);
		cameraBtn = (Button) findViewById(R.id.new_edit_camera_btn);
		creatImageBtn = (Button) findViewById(R.id.new_edit_creatimage_btn);
		inAnimation = new AlphaAnimation(0, 1);
		inAnimation.setDuration(animatonTime);
		okBtn = (Button)findViewById(R.id.ok);
		bitmapList = new ArrayList<Bitmap>();
		manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		setListener();


		Intent intent = getIntent();
		String load = intent.getStringExtra("load");
		if(load != null && load.equals("load"))
		{
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						biaoti = URLEncoder.encode("paris", "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					response = httpUtil.sendPost("http://1.simpleboy.sinaapp.com/handleduqu",
							"user.biaoti=" + biaoti);
					Log.i("dd",response);
					handler.sendEmptyMessage(0x121);
				}
			});
			thread.start();
		}
	}
	/**
	 * 开启定位服务
	 */
	public void startLocationServer(){
		//获取到LocationManager对象
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //创建一个Criteria对象
//        Criteria criteria = new Criteria();
//        //设置粗略精确度ACCURACY_COARSE，精确ACCURACY_FINE
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        //设置是否需要返回海拔信息
//        criteria.setAltitudeRequired(false);
//        //设置是否需要返回方位信息
//        criteria.setBearingRequired(false);
//        //设置是否允许付费服务
//        criteria.setCostAllowed(true);
//        //设置电量消耗等级
//        criteria.setPowerRequirement(Criteria.POWER_HIGH);
//        //设置是否需要返回速度信息
//        criteria.setSpeedRequired(false);
 
        //根据设置的Criteria对象，获取最符合此标准的provider对象
//        String currentProvider = locationManager.getBestProvider(criteria, true);
//        Log.d("Location", "currentProvider: " + currentProvider);
        
      //根据设置的Criteria对象，获取最符合此标准的provider对象
        String currentProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();
        //根据当前provider对象获取最后一次位置信息
        Location currentLocation = locationManager.getLastKnownLocation(currentProvider);
        //如果位置信息为null，则请求更新位置信息
        if(currentLocation == null){
            locationManager.requestLocationUpdates(currentProvider, 0, 0, locationListener);
        }
        
        //增加GPS状态监听器
        locationManager.addGpsStatusListener(gpsListener);
        //直到获得最后一次位置信息为止，如果未获得最后一次位置信息，则显示默认经纬度
        //每隔10秒获取一次位置信息
        while(true){
            currentLocation = locationManager.getLastKnownLocation(currentProvider);
            if(currentLocation != null){
                Log.d("Location", "Latitude: " + currentLocation.getLatitude());
                Log.d("Location", "location: " + currentLocation.getLongitude());
                break;
            }else{
                Log.d("Location", "Latitude: " + 0);
                Log.d("Location", "location: " + 0);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                 Log.e("Location", e.getMessage());
            }
        }
        
        //解析地址并显示
        Geocoder geoCoder = new Geocoder(this);
        try {
            int latitude = (int) currentLocation.getLatitude();
            int longitude = (int) currentLocation.getLongitude();
            List<Address> list = geoCoder.getFromLocation(latitude, longitude, 2);
            for(int i=0; i<list.size(); i++){
                Address address = list.get(i); 
                Toast.makeText(EditDairy.this, address.getCountryName() + address.getAdminArea() + address.getFeatureName(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(EditDairy.this,e.getMessage(), Toast.LENGTH_LONG).show();
        }
		
	}
	
	private GpsStatus.Listener gpsListener = new GpsStatus.Listener(){
        //GPS状态发生变化时触发
        @Override
        public void onGpsStatusChanged(int event) {
            //获取当前状态
            gpsstatus=locationManager.getGpsStatus(null);
            switch(event){
                //第一次定位时的事件
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    break;
                //开始定位的事件
                case GpsStatus.GPS_EVENT_STARTED:
                    break;
                //发送GPS卫星状态事件
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Toast.makeText(EditDairy.this, "GPS_EVENT_SATELLITE_STATUS", Toast.LENGTH_SHORT).show();
                    Iterable<GpsSatellite> allSatellites = gpsstatus.getSatellites();   
                    Iterator<GpsSatellite> it=allSatellites.iterator(); 
                    int count = 0;
                    while(it.hasNext())   
                    {   
                        count++;
                    }
                    Toast.makeText(EditDairy.this, "Satellite Count:" + count, Toast.LENGTH_SHORT).show();
                    break;
                //停止定位事件
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d("Location", "GPS_EVENT_STOPPED");
                    break;
            }
        }
    };
	
	//创建位置监听器
    private LocationListener locationListener = new LocationListener(){
        //位置发生改变时调用
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", "onLocationChanged");
            Log.d("Location", "onLocationChanged Latitude" + location.getLatitude());
                 Log.d("Location", "onLocationChanged location" + location.getLongitude());
        }

        //provider失效时调用
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Location", "onProviderDisabled");
        }

        //provider启用时调用
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Location", "onProviderEnabled");
        }

        //状态改变时调用
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Location", "onStatusChanged");
        }
    };
	
	public void setListener() {
		inAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				successLayout.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				finish();
//				Intent intent = new Intent(EditDairy.this, MyDairyGallery.class);
//				startActivity(intent);
			}
		});
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		labelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), LabelSelect.class);
				startActivityForResult(intent, 1);
			}
		});
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if(title.getText().toString().equals(""))
				{
					Toast.makeText(EditDairy.this, "标题为空，无法发送", Toast.LENGTH_SHORT).show();
				}else if(!content.getText().toString().equals(""))
				{
					PendingIntent pendingIntent = PendingIntent.getActivity(EditDairy.this, 0,
							new Intent(), 0);  
					Notification notify1 = new Notification();  
		            notify1.icon = R.drawable.logo;  
		            notify1.tickerText = "旅游笔记本:正在发送！";  
		            notify1.when = System.currentTimeMillis();  
//		            notify1.number = 1;  
		            notify1.setLatestEventInfo(EditDairy.this, "旅游笔记本",  
		                    "正在发送", pendingIntent);  
		            notify1.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。  
		            // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示  
		            manager.notify(100, notify1);
		            
					//帧动画设置
					successLayout.setAnimation(inAnimation);
					try {
						neirong = URLEncoder.encode(content.getText().toString(), "UTF-8");
						biaoti = URLEncoder.encode("paris", "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					Thread thread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							BitmapFactory.Options opts = new BitmapFactory.Options();
							opts.inSampleSize = 2;
							Date date = new Date(System.currentTimeMillis());
							String dateFormat = formatter.format(date);
							String s1="",s2="",s3="";
							String s1_s="",s2_s="",s3_s="";
							String[] path1 = null , path2 = null, path3 = null;
							if(testPathList.size() >= 1)
							{
								Bitmap bm1 = BitmapFactory.decodeFile(testPathList.get(0),opts);
								path1 = testPathList.get(0).split("/");
								byte[] data1 = ToByteArray.Bitmap2Bytes(bm1);
								s1 = Base64.encodeToString(data1, 0, data1.length,Base64.DEFAULT);
								bm1.recycle();
								bm1 = decodeImage(testPathList.get(0));
								data1 = ToByteArray.Bitmap2Bytes(bm1);
								s1_s = Base64.encodeToString(data1, 0, data1.length,Base64.DEFAULT);
								bm1.recycle();
							}
							if(testPathList.size() >= 2)
							{
								Bitmap bm2 = BitmapFactory.decodeFile(testPathList.get(1),opts);
								path2 = testPathList.get(1).split("/");
								byte[] data2 = ToByteArray.Bitmap2Bytes(bm2);
								s2 = Base64.encodeToString(data2, 0, data2.length,Base64.DEFAULT);
								bm2.recycle();
								bm2 = decodeImage(testPathList.get(0));
								data2 = ToByteArray.Bitmap2Bytes(bm2);
								s2_s = Base64.encodeToString(data2, 0, data2.length,Base64.DEFAULT);
								bm2.recycle();
							}
							if(testPathList.size() >= 3)
							{
								Bitmap bm3 = BitmapFactory.decodeFile(testPathList.get(2),opts);
								path3 = testPathList.get(2).split("/");
								byte[] data3 = ToByteArray.Bitmap2Bytes(bm3);
								s3 = Base64.encodeToString(data3, 0, data3.length,Base64.DEFAULT);
								bm3.recycle();
								bm3 = decodeImage(testPathList.get(0));
								data3 = ToByteArray.Bitmap2Bytes(bm3);
								s3_s = Base64.encodeToString(data3, 0, data3.length,Base64.DEFAULT);
								bm3.recycle();
							}
							try {
								if(testPathList.size() >= 1)
									s1 = URLEncoder.encode(s1, "UTF-8");
									s1_s = URLEncoder.encode(s1_s, "UTF-8");
								if(testPathList.size() >= 2)
									s2 = URLEncoder.encode(s2, "UTF-8");
									s2_s = URLEncoder.encode(s2_s, "UTF-8");
								if(testPathList.size() >= 3)
									s3 = URLEncoder.encode(s3, "UTF-8");
									s3_s = URLEncoder.encode(s3_s, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							Log.i("email", PersonalInfo.account);
							if(testPathList.size() == 1)
							{
								response = httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=travelnote_store",
										"image1="+s1+
										"&image1_s="+s1_s+
										"&image2="+""+
										"&image2_s="+s2_s+
										"&image3="+""+
										"&image3_s="+s3_s+
										"&title="+title.getText().toString()+
										"&date="+dateFormat+
										"&sign1="+label1+
										"&sign2="+label2+
										"&sign3="+label3+
										"&email="+PersonalInfo.account+
										"&content="+content.getText().toString()
										+"&location="+"广州");
							}else if(testPathList.size() == 2)
								response = httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=travelnote_store",
										"image1="+s1+
										"&image1_s="+s1_s+
										"&image2="+s2+
										"&image2_s="+s2_s+
										"&image3="+""+
										"&image3_s="+s3_s+
										"&title="+title.getText().toString()+
										"&date="+dateFormat+
										"&sign1="+label1+
										"&sign2="+label2+
										"&sign3="+label3+
										"&email="+PersonalInfo.account+
										"&content="+content.getText().toString()
										+"&location="+"广州");
							else if(testPathList.size() == 3)
								response = httpUtil.sendPost("http://1.travelsky.sinaapp.com/index.php?c=main&a=travelnote_store",
										"image1="+s1+
										"&image1_s="+s1_s+
										"&image2="+s2+
										"&image2_s="+s2_s+
										"&image3="+s3+
										"&image3_s="+s3_s+
										"&title="+title.getText().toString()+
										"&date="+dateFormat+
										"&sign1="+label1+
										"&sign2="+label2+
										"&sign3="+label3+
										"&email="+PersonalInfo.account+
										"&content="+content.getText().toString()
										+"&location="+"广州");
							
							Log.i("success", response);
							handler.sendEmptyMessage(0x120);
						}
					});
					thread.start();
				}else{
					Toast.makeText(EditDairy.this, "内容为空，无法发送", Toast.LENGTH_SHORT).show();
				}
		        
			}
		});
		cameraBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 创建一个文件夹存放文件
	            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
	            
	            // 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null，如果此处指定，则后来的data为null
	            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

	            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});
		creatImageBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), ImagePicker.class);
				//设置可选图片为3张
				ImagePicker.selectCount = 3;
				startActivityForResult(intent, ImagePicker.IMAGE_PICKER);
			}
		});
		landscapeLabel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				label1="";
				landscapeLabel.setVisibility(View.GONE);
			}
		});
		foodLabel.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View arg0) {
				label2="";
				foodLabel.setVisibility(View.GONE);
			}
		});
		lifeLabel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				label3="";
				lifeLabel.setVisibility(View.GONE);
			}
		});
		image1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditDairy.this, ImageActivity.class);
				startActivity(intent);
			}
		});
		image2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditDairy.this, ImageActivity.class);
				startActivity(intent);
			}
		});
		image3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditDairy.this, ImageActivity.class);
				startActivity(intent);
			}
		});
	}
	
	//显示选中的图片
	public void showImage(Bitmap bitmap) {
		if(bitmapList.size() < 3)
		{
			bitmapList.add(bitmap);
		}else{
			bitmapList.clear();
			bitmapList.add(bitmap);
		}
		if(bitmapList.size() >= 1 ) {
			image1.setVisibility(View.VISIBLE);
			image1.setImageBitmap(bitmapList.get(0));
		}else{
			image1.setVisibility(View.GONE);
		}
		if(bitmapList.size() >= 2 ) {
			image2.setVisibility(View.VISIBLE);
			image2.setImageBitmap(bitmapList.get(1));
		}else{
			image2.setVisibility(View.GONE);
		}
		if(bitmapList.size() >= 3 ) {
			image3.setVisibility(View.VISIBLE);
			image3.setImageBitmap(bitmapList.get(2));
		}else{
			image3.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 创建一个URI来存放图片的位置
	 * @param type
	 * @return
	 */
    public static Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    
    /** 
     * 创建一个文件来存放图片
     * @param type
     * @return
     */
    private static File getOutputMediaFile(int type)
    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = null;
        try
        {
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been
            // uninstalled.
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MyCameraApp");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                // 在SD卡上创建文件夹需要权限：
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }else
        {
            return null;
        }

        return mediaFile;
    }
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x120)
			{
				if(response.contains("success"))
				{
					Toast.makeText(EditDairy.this, "发送成功", Toast.LENGTH_SHORT).show();
					PendingIntent pendingIntent = PendingIntent.getActivity(EditDairy.this, 0,
							new Intent(), 0);  
					Notification notify1 = new Notification();  
		            notify1.icon = R.drawable.logo;  
		            notify1.tickerText = "旅游笔记本:发送成功";  
		            notify1.when = System.currentTimeMillis();  
		            notify1.setLatestEventInfo(EditDairy.this, "旅游笔记本",  
		                    "发送成功", pendingIntent);  
		            notify1.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。  
		            // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示  
		            manager.notify(100, notify1);
					finish();
				}else{
					PendingIntent pendingIntent = PendingIntent.getActivity(EditDairy.this, 0,
							new Intent(), 0);  
					Notification notify1 = new Notification();  
		            notify1.icon = R.drawable.logo;  
		            notify1.tickerText = "旅游笔记本:发送失败";  
		            notify1.when = System.currentTimeMillis();  
//		            notify1.number = 1;  
		            notify1.setLatestEventInfo(EditDairy.this, "旅游笔记本",  
		                    "发送失败", pendingIntent);  
		            notify1.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。  
		            // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示  
		            manager.notify(100, notify1);
					Toast.makeText(EditDairy.this, "发送失败", Toast.LENGTH_SHORT).show();
//					anim.stop();
				}
				
			}
			if(msg.what == 0x121)
			{
				if(!response.contains("wu"))
				{
					content.setText(response);
				}
				
			}
	}};


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case 1:
				label1="landscape";
				landscapeLabel.setVisibility(View.VISIBLE);
				break;
			case 2:
				label2="food";
				foodLabel.setVisibility(View.VISIBLE);
				break;
			case 3:
				label3="life";
				lifeLabel.setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
		//如果是图片选择
		if(ImagePicker.IMAGE_PICKER == requestCode && ImagePicker.IMAGE_PICKER == resultCode)
		{
			List<String> path = MyAdapter.mSelectedImage;
			Log.i("imagepicker","back");
			for(String p: path)
			{
				showImage(decodeImage(p));
				if(testPathList.size() == 3)
					testPathList.removeAll(testPathList);
				testPathList.add(p);
//				showImage(((BitmapDrawable)Drawable.createFromPath(p)).getBitmap());
			}
			//及时清空
			MyAdapter.mSelectedImage.clear();
		}
		// 如果是拍照
        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode)
        {
        	Log.i("LOG_TAG", "CAPTURE_IMAGE");
            if (RESULT_OK == resultCode)
            {
            	Log.i("LOG_TAG", "RESULT_OK");
                // Check if the result includes a thumbnail Bitmap
                if (data != null)
                {
                    // 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
                    Toast.makeText(this, "Image saved to:\n" + data.getData(),
                            Toast.LENGTH_LONG).show();
                    if (data.hasExtra("data"))
                    {
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        showImage(thumbnail);
//                        image1.setImageBitmap(thumbnail);
                    }
                }else{
                    // If there is no thumbnail image data, the image
                    // will have been stored in the target output URI.
                    // Resize the full image to fit in out image view.
                    
                    showImage(decodeImage(fileUri.getPath()));
//                    image1.setImageBitmap(bitmap);
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
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 压缩图片
	 * @param path
	 * @return
	 */
	public Bitmap decodeImage(String path)
	{

        BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
        
        factoryOptions.inJustDecodeBounds = true;
        
		int width = 450;
        int height = 450;
        Bitmap bitmap = BitmapFactory.decodeFile(path, factoryOptions);
        
        int imageWidth = factoryOptions.outWidth;
        int imageHeight = factoryOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(imageWidth / width, imageHeight
                / height);

        // Decode the image file into a Bitmap sized to fill the
        // View
        factoryOptions.inJustDecodeBounds = false;
        factoryOptions.inSampleSize = scaleFactor;
        factoryOptions.inPurgeable = true;
        bitmap = BitmapFactory.decodeFile(path,
                factoryOptions);
		return bitmap;
	}
	
	
}
