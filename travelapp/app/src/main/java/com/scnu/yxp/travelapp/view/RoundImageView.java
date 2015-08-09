package com.scnu.yxp.travelapp.view;

import com.scnu.yxp.travelapp.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.graphics.PorterDuff;

public class RoundImageView extends ImageView {
	//设置画笔
	private Paint paint;
	//定义圆角宽高
	private int roundWidth = 10;
	private int roundHeight = 10;
    private Paint paint2;  
  
    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init(context, attrs);  
    }  
  
    public RoundImageView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context, attrs);  
    }  
  
    public RoundImageView(Context context) {  
        super(context);  
        init(context, null);  
    }  
      
    private void init(Context context, AttributeSet attrs) {  
          
        if(attrs != null) {     
        	//如果有设定属性，则获取获取相应的属性
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);   
            roundWidth= a.getDimensionPixelSize(R.styleable.RoundImageView_roundWidth, roundWidth);  
            roundHeight= a.getDimensionPixelSize(R.styleable.RoundImageView_roundHeight, roundHeight);  
        }else {  
        	//如果没有设定相应的属性则直接设置相应的属性
            float density = context.getResources().getDisplayMetrics().density;  
            roundWidth = (int) (roundWidth*density);  
            roundHeight = (int) (roundHeight*density);  
        }   
          
        paint = new Paint();  
        paint.setColor(Color.WHITE);  
        //设置抗锯齿
        paint.setAntiAlias(true);  
        //设置叠加则两层重叠区消失的效果
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));  
          
        paint2 = new Paint();  
        //此画笔的效果要设置为无
        paint2.setXfermode(null);  
    }  
      
    @Override  
    public void draw(Canvas canvas) {  
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);  
        //创建一个以bitmap为图像的画布，然后在画布上进行修改
        Canvas canvas2 = new Canvas(bitmap);  
        //将imageview中的drawable转为bitmap
        super.draw(canvas2);  
        drawLiftUp(canvas2);  
        drawRightUp(canvas2);  
        drawLiftDown(canvas2);  
        drawRightDown(canvas2);  
        canvas.drawBitmap(bitmap, 0, 0, paint2);  
        //强制回收图片
        bitmap.recycle();  
    }  
      
    private void drawLiftUp(Canvas canvas) {  
    	//绘制一个扇形
        Path path = new Path();  
        path.moveTo(0, roundHeight);  
        path.lineTo(0, 0);  
        path.lineTo(roundWidth, 0);  
        path.arcTo(new RectF(  
                0,   
                0,   
                roundWidth*2,   
                roundHeight*2),   
                -90,   
                -90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
      
    private void drawLiftDown(Canvas canvas) {  
    	//绘制一个扇形
        Path path = new Path();  
        path.moveTo(0, getHeight()-roundHeight);  
        path.lineTo(0, getHeight());  
        path.lineTo(roundWidth, getHeight());  
        path.arcTo(new RectF(  
                0,   
                getHeight()-roundHeight*2,   
                0+roundWidth*2,   
                getHeight()),  
                90,   
                90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
      
    private void drawRightDown(Canvas canvas) {  
    	//绘制一个扇形
        Path path = new Path();  
        path.moveTo(getWidth()-roundWidth, getHeight());  
        path.lineTo(getWidth(), getHeight());  
        path.lineTo(getWidth(), getHeight()-roundHeight);  
        path.arcTo(new RectF(  
                getWidth()-roundWidth*2,   
                getHeight()-roundHeight*2,   
                getWidth(),   
                getHeight()), 0, 90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
      
    private void drawRightUp(Canvas canvas) {  
    	//绘制一个扇形
        Path path = new Path();  
        path.moveTo(getWidth(), roundHeight);  
        path.lineTo(getWidth(), 0);  
        path.lineTo(getWidth()-roundWidth, 0);  
        path.arcTo(new RectF(  
                getWidth()-roundWidth*2,   
                0,   
                getWidth(),   
                0+roundHeight*2),   
                -90,   
                90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
}
