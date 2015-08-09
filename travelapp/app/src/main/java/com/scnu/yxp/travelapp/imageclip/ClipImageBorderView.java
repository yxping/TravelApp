package com.scnu.yxp.travelapp.imageclip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ClipImageBorderView extends View
{
	/**
	 * 水平方向与View的边距
	 */
	private int mHorizontalPadding;
	/**
	 * 垂直方向与View的边距
	 */
	private int mVerticalPadding;
	/**
	 * 绘制的矩形的宽度
	 */
	private int mWidth;
	/**
	 * 边框的颜色，默认为白色
	 */
	private int mBorderColor = Color.parseColor("#FFFFFF");
	/**
	 * 边框的宽度 单位dp
	 */
	private int mBorderWidth = 1;
	private Bitmap menban;

	private Paint mPaint;
	private PorterDuffXfermode mode = new PorterDuffXfermode(Mode.CLEAR);
	

	public ClipImageBorderView(Context context)
	{
		this(context, null);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	
		mBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
						.getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// 计算矩形区域的宽度
		mWidth = getWidth() - 2 * mHorizontalPadding;
		// 计算距离屏幕垂直边界 的边距
		mVerticalPadding = (getHeight() - mWidth) / 2;
		
//		mPaint.setColor(Color.parseColor("#aa000000"));
//		mPaint.setStyle(Style.FILL);
//		// 绘制左边1
//		canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
//		// 绘制右边2
//		canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(),
//				getHeight(), mPaint);
//		// 绘制上边3
//		canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding,
//				mVerticalPadding, mPaint);
//		
//		// 绘制下边4
//		canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding,
//				getWidth() - mHorizontalPadding, getHeight(), mPaint);
		// 矩形绘制外边框
//		mPaint.setColor(mBorderColor);
//		mPaint.setStrokeWidth(mBorderWidth);
//		mPaint.setStyle(Style.STROKE);
//		canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()
//				- mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);
		if(menban == null)
			menban = drawMenBan();

		canvas.drawBitmap(menban, 0, 0, mPaint);
		
		// 圆形绘制外边框
		mPaint.setColor(mBorderColor);
		mPaint.setStrokeWidth(mBorderWidth);
		mPaint.setStyle(Style.STROKE);
		mPaint.setAntiAlias(true);
		canvas.drawCircle(mHorizontalPadding + mWidth/2, 
				mVerticalPadding + mWidth/2, mWidth/2, mPaint);
	}
	public Bitmap drawMenBan() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canva = new Canvas(bitmap);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.parseColor("#aa000000"));
		p.setStyle(Style.FILL);
		canva.drawRect(0, 0, getWidth(), getHeight(), p);
		p.setXfermode(mode);
		p.setColor(Color.BLUE);
		p.setStyle(Style.FILL);
		canva.drawCircle(mHorizontalPadding + mWidth/2, 
				mVerticalPadding + mWidth/2, mWidth/2, p);
		
		p.setXfermode(null);
		return bitmap;
	}
	public void setHorizontalPadding(int mHorizontalPadding)
	{
		this.mHorizontalPadding = mHorizontalPadding;
		
	}

}
