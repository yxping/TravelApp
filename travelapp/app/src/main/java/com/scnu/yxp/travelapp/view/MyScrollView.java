package com.scnu.yxp.travelapp.view;

import com.scnu.yxp.travelapp.homepage.HomePageActivity;
import com.scnu.yxp.travelapp.view.MyListView.OnXScrollListener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Scroller;

public class MyScrollView extends ScrollView {  
    private OnScrollListener onScrollListener;  
    //主要是用在用户手指离开MyScrollView，MyScrollView还在继续滑动，我们用来保存Y的距离，然后做比较 
    private int lastScrollY;  
    private HomePageActivity activity;
    private float preY, nowY, preYrecord;
    private boolean flag = false;
    private final static float OFFSET_RADIO = 2.5f;
    private boolean isRecord = false;
    private OnRefreshListener onRefreshListener;
    private Scroller mScroller;
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;
	private boolean downflag = false;
	private boolean upflag = false;
      
    public MyScrollView(Context context) {  
        this(context, null);  
        activity = (HomePageActivity)context;
		mScroller = new Scroller(context, new DecelerateInterpolator());
    }  
      
    public MyScrollView(Context context, AttributeSet attrs) {  
        this(context, attrs, 0);  
        activity = (HomePageActivity)context;
		mScroller = new Scroller(context, new DecelerateInterpolator());
    }  
  
    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle); 
        activity = (HomePageActivity)context; 
		mScroller = new Scroller(context, new DecelerateInterpolator());
    }  
    
    public void setOnScrollListener(OnScrollListener onScrollListener) {  
        this.onScrollListener = onScrollListener;  
    }  
  
  
    //用于用户手指离开MyScrollView的时候获取MyScrollView滚动的Y距离，然后回调给onScroll方法中 
    private Handler handler = new Handler() {  
  

		public void handleMessage(Message msg) {
            int scrollY = MyScrollView.this.getScrollY();  
              
            //此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息  
            if(lastScrollY != scrollY){  
                lastScrollY = scrollY;  
                handler.sendMessageDelayed(handler.obtainMessage(), 5);    
            }  
            if(onScrollListener != null){  
                onScrollListener.onScroll(scrollY);  
            }
        };  
    };   
  
    /** 
     * 重写onTouchEvent， 当用户的手在MyScrollView上面的时候， 
     * 直接将MyScrollView滑动的Y方向距离回调给onScroll方法中，当用户抬起手的时候， 
     * MyScrollView可能还在滑动，所以当用户抬起手我们隔5毫秒给handler发送消息，在handler处理 
     * MyScrollView滑动的距离 
     */  
    @Override  
    public boolean onTouchEvent(MotionEvent ev) {  
        if(onScrollListener != null){  
            onScrollListener.onScroll(lastScrollY = this.getScrollY()); 
        }
        
        switch(ev.getAction()){  
	        case MotionEvent.ACTION_UP:  
	            handler.sendMessageDelayed(handler.obtainMessage(), 5); 
	            if(downflag)
	            {
	            	if(activity.getMState() == MListViewHeader.STATE_READY)
		            {
		            	activity.setState(MListViewHeader.STATE_REFRESHING);
		            	onRefreshListener.downRefresh();
			            resetHeaderView();
		            }else if(activity.getMState() != MListViewHeader.STATE_REFRESHING){
			            resetHeaderView();
			        	activity.setState(MListViewHeader.STATE_NORMAL);
		            }
            		downflag = false;
	            }
	            if(upflag)
	            {
	            	resetFooterView();
	            	upflag = false;
	            }
	            flag=false;
	            isRecord = false;
	            break; 
	        case MotionEvent.ACTION_MOVE:
	        	//因为listview的缘故，在触摸listview的部分会跳过ACTION_DOWN所以需要在这里做判断
	        	if(!isRecord)
	        	{
	        		preY = ev.getY();
	        		isRecord = true;
	        	}
	        	nowY = ev.getY();
	        	//调整灵敏度
	        	if(preY - nowY  < -3 && activity.getMState() != MListViewHeader.STATE_REFRESHING)
	        	{
		            if(this.getScrollY() <= 0)
		            {
	            		int delY = (int) ((nowY - preY) / OFFSET_RADIO);
	            		updateHeaderView(delY);
	            		downflag = true;
	            		//必须返回false，否则会相应scrollview的上滑
	            		return false;
		            }
	        	}
	        	
	        	if(preY - nowY > 1 && activity.getMFooterState() != activity.FOOTER_REFRESHING)
	        	{
	        		//this.getHeight()是这个scrollview可见的高度，this.getChildAt(0).getMeasuredHeight()包括不可见高度
		        	if(this.getScrollY() + this.getHeight()>= this.getChildAt(0).getMeasuredHeight())
		        	{
	            		int delY = (int) ((preY - nowY) / OFFSET_RADIO);
		        		upflag = true;
		        		updateFooterView(delY);
	            		return false;
		        	}
	        	}
	        	break;
	        case MotionEvent.ACTION_DOWN:
        		preY = ev.getY();
        		isRecord = true;
	        	break;
        }
        return super.onTouchEvent(ev);  
    }  
    
    //使用scroller必须重写computeScroll()来实现渐渐滑动的效果
    @Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				activity.setDownRefreshVisibleHeight(mScroller.getCurrY());
			} else {
				activity.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}
    
    private void invokeOnScrolling() {
		if (onScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) onScrollListener;
			l.onXScrolling(this);
		}
	}
    
    /** 
     * 更新加载界面的高度
     * @param delY
     */
    public void updateFooterView(int delY){
    	int height = activity.getFooterViewBottomMargin() + (int) delY;
    	if(delY > 50)
    		activity.setFooterViewState(activity.FOOTER_READY);
    	else
    		activity.setFooterViewState(activity.FOOTER_NORMAL);
    	if(delY < 100)
    		activity.setBottomMargin(height);
    }
    
    /**
     * 重置加载的界面的高度
     */
    public void resetFooterView(){
    	int bottomMargin = activity.getFooterViewBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			//滑动时其恢复原样
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					MyListView.SCROLL_DURATION);
			invalidate();
		}
 		if(activity.getMFooterState() == activity.FOOTER_READY)
 		{
    		activity.setFooterViewState(activity.FOOTER_REFRESHING);
        	onRefreshListener.upRefresh();
 		}
    }
    
    /** 
     * 重新设置下拉刷新界面的高度
     */
    public void resetHeaderView(){
    	int height = activity.getMyScrollViewHeader().getHeight();
    	//隐藏下拉刷新的界面
    	int finalHeight = 0; // 默认返回顶部
		// 正在刷新则只返回到刚好展示的距离
		if (activity.getMState() == MListViewHeader.STATE_REFRESHING && height > 100) {
			finalHeight = 100;
		}
		mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0,  finalHeight - height,
				MyListView.SCROLL_DURATION);
        //激发scroller
 		invalidate();
    }
    
    public void updateHeaderView(int delY){
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
    			delY);
        activity.getMyScrollViewHeader().setLayoutParams(params);
        if(delY > 70)
        {
        	activity.setState(MListViewHeader.STATE_READY);
        }else{
        	activity.setState(MListViewHeader.STATE_NORMAL);
        }
    }
    
    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
    	this.onRefreshListener = onRefreshListener;
    }
  
  
    //滚动的回调接口
    public interface OnScrollListener{  
        //返回滑动的Y的距离
        public void onScroll(int scrollY);  
    }  
      
    public interface OnRefreshListener{
    	public void downRefresh();
    	public void upRefresh();
    }
  
}  
