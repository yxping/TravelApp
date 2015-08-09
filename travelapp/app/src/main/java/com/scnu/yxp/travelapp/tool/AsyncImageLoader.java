package com.scnu.yxp.travelapp.tool;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

public class AsyncImageLoader {
	/** 
	 * 图片缓存
	 */
	private LruCache<String, Drawable> imgCachMap;
	
	public AsyncImageLoader()
	{
		//获取可用最大内存
		int maxMemory = (int)Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory/8;
		imgCachMap = new LruCache<String, Drawable>(cacheSize){

			//用来计算内存
			@Override
			protected int sizeOf(String key, Drawable value) {
				//得到每一张图片的大小
				Bitmap bmp = ((BitmapDrawable)value).getBitmap();
				return bmp.getRowBytes() * bmp.getHeight();
			}
			
		};
	}
	
	public Drawable loaDrawable(final String imgUrl,final ImageCallback imageCallback){
		if(getBitmapFromCache(imgUrl) != null)
		{
			Drawable drawable=imgCachMap.get(imgUrl);
			if(drawable!=null)
			{
				return drawable;
			}
		}
		
		final Handler handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				imageCallback.imageLoaded((Drawable)msg.obj, imgUrl);
			}
		};
		new Thread(){

			@Override
			public void run() {
				super.run();
				Drawable drawable=loadImageFromUrl(imgUrl);
				imgCachMap.put(imgUrl, drawable);
				Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
			}
		}.start();
		
		return null;
		
	};
	
	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Options opt = new Options();
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = 3;
		Bitmap bitmap = BitmapFactory.decodeStream(i, null, opt);
//		Drawable d = Drawable.createFromStream(i, "src");
		
		Drawable d = new BitmapDrawable(bitmap);
		
		return d;
	}
	
	public Drawable getBitmapFromCache(String key){
		
		return imgCachMap.get(key);
	}
		
    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable, String imageUrl);
    }
    
}
