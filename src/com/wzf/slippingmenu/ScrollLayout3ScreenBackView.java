package com.wzf.slippingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 水平滑动的Layout，此Layout适合三个Screen滑动，滑动时中间的Screen不会覆盖左右两个Screen
 * Datetime   ： 2013-4-8 下午8:54:31
 * author     :  wuzhengfei
 */
public class ScrollLayout3ScreenBackView extends LinearLayout {
	private static final String TAG = ScrollLayout3ScreenBackView.class.getSimpleName() ;
	
	private Scroller scroller ;
//	private int side_width = 240 ;
	
	private float rate = 2 ;
	private int touchSlop ;
	private int startX ;
	private int currentX ;
	private int lastMotionX ;
	private boolean recorded ;
	private boolean visiable ;
	
	public ScrollLayout3ScreenBackView(Context context) {
		super(context);
		initView(context);
	}

	public ScrollLayout3ScreenBackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	/**
	 * 初始化View
	 * @param context
	 */
	private void initView(final Context context){
		scroller = new Scroller(context, AnimationUtils.loadInterpolator(context, android.R.anim.overshoot_interpolator)) ;
		
		ViewConfiguration config = ViewConfiguration.get(context) ;
		touchSlop = config.getScaledTouchSlop();
	}
	
	

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		Log.i(TAG, "onLayout    left="+left+"   right="+right) ;
		int width = right-left;
		int count = getChildCount() ;
		for( int i = 0; i<count; i++ ){
			View child = getChildAt(i);
			child.measure(right-left, bottom-top);
			child.layout(i*getWidth() , 0, (i+1)*getWidth(), getHeight());
		}
		
		scrollTo(width, 0);
	}
	
	

	@Override
	public void computeScroll() {
		if( scroller.computeScrollOffset() ){
			//如果Scroll还没有完成
			scrollTo(scroller.getCurrX(), 0);
			postInvalidate() ;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		currentX = (int)ev.getX() ;
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			doActionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			doActionMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			doActionUp(ev) ;
			break;
		case MotionEvent.ACTION_CANCEL:
			doActionUp(ev) ;
			break;
		default:
			break;
		}
		return true;
	}
	
	private void doActionDown(MotionEvent ev){
		if( !recorded ){
			startX = (int)ev.getX();
			lastMotionX = startX ;
			recorded = true ;
		}
	}

	private void doActionMove(MotionEvent ev){
		if( recorded ){
			currentX = (int) ev.getX() ;
			
			int deltaX = currentX - startX ;
			int offset = Math.abs(deltaX);
			if( offset > touchSlop ){
				if( deltaX < 0 && visiable){
					int count = getChildCount();
					View view = getChildAt(count -1 );
					view.setVisibility(VISIBLE);
					visiable = true; 
				}
				int dx = -(currentX - lastMotionX) ;
				int scrollX = getScrollX() ;
				Log.i(TAG, "doActionMove    dx="+dx+"   scrollX="+scrollX) ;
				scrollBy((int)(dx/rate), 0) ;
				lastMotionX = currentX ;
			}
		}
	}
	private void doActionUp(MotionEvent ev){
		if( recorded ){
			recorded = false;
			
			int scrollX = getScrollX()  ;
			int dx = getWidth()  - scrollX ;
			Log.i(TAG, "doActionUp    dx="+dx+"   scrollX="+scrollX) ;
			scroller.startScroll(scrollX, 0, dx, 0, Math.abs(dx) * 4) ;
			invalidate() ;
		}
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}
	
}
