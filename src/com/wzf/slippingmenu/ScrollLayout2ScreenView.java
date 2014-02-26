package com.wzf.slippingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Scroller;

/**
 * 水平滑动的Screen，适合于两个Screen。滑动时不会相互覆盖，而是将另外的Screen往边上挤。
 * Datetime   ： 2013-4-15 下午4:09:43
 * author     :  wuzhengfei
 */
public class ScrollLayout2ScreenView extends ViewGroup {
	private static final String TAG = ScrollLayout2ScreenView.class.getSimpleName() ;
	private final int LEFT_SCREEN = 0 ;
	private final int RIGHT_SCREEN = 1;
	
	/**
	 * 滑动开始时的ScrollX
	 */
	private float startScrollX = 0;
	/**
	 * 滑动结束时的ScrollX
	 */
	private float endScrollX = 0 ;
	private Scroller scroller ;
	
	/**
	 * 当前显示的是第几屏
	 */
	private int currentScreen = LEFT_SCREEN ;
	private GestureDetector gestureDetector ;
	/**
	 * 是否是在飞速滑动Screen
	 */
	private boolean fling;
	/**
	 * fling=true时有效，表示飞速滑动的目的
	 */
	private int flingTo = RIGHT_SCREEN ;
	private int minFlingVelocity ;
	
	public ScrollLayout2ScreenView(Context context) {
		super(context);
		initView(context);
	}

	public ScrollLayout2ScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	/**
	 * 初始化View
	 * @param context
	 */
	private void initView(final Context context){
		currentScreen = LEFT_SCREEN ;
		scroller = new Scroller(context, AnimationUtils.loadInterpolator(context, android.R.anim.overshoot_interpolator)) ;
		minFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
		
		gestureDetector = new GestureDetector(context, new MyGuesterListenerImpl());
	}
	
	

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		View leftScreen = getChildAt(LEFT_SCREEN);
		leftScreen.measure(right-left, bottom-top);
		leftScreen.layout(0 , 0, getWidth(), getHeight());
		leftScreen.setVisibility(View.VISIBLE);
		
		
		View rightScreen = getChildAt(RIGHT_SCREEN);
		rightScreen.measure(right-left, bottom-top);
		rightScreen.layout(getWidth(), 0, 2*getWidth(), getHeight());
		rightScreen.setVisibility(View.VISIBLE);
	}
	
	

	@Override
	public void computeScroll() {
		if( scroller.computeScrollOffset() ){
			//如果Scroll还没有完成
			scrollTo(scroller.getCurrX(), 0);
			postInvalidate() ;
		}
	}

	/**
	 * 滑动结束以后，判断是应该滑动到Left Screen还是Right Screen
	 * <ol>
	 * 	<li>如果是飞速滑动，那么直接滑动到指定的window
	 * 	<li>如果不是飞速滑动
	 * 		<ol>
	 * 			<li>当scrollX < ( screenWidth - menuWith )/2, 滑动到Left Screen
	 * 			<li>当scrollX > ( screenWidth - menuWith )/2, 滑动到Right Screen
	 */
	private void snapToDestination(){
		int scrollX = getScrollX() ;
		int screenWidth = getWidth() ;
		if( fling ){
			if( flingTo >= 0 ){
				scrollToScreen(flingTo);
			}
		}else{
			if ( scrollX < screenWidth/2 ) {
				scrollToScreen(LEFT_SCREEN);
			}else{
				scrollToScreen(RIGHT_SCREEN) ;
			}
		}
	}
	
	/**
	 * 滑动到指定屏幕
	 * <li>如果current Screen和 to scroll screen不是一个，检查当前focused view是不是current view，
	 * 	如果是则需要清除focus信息，因为需要聚焦到滑动到的那个screen去
	 * <li>如果要滑动到Left Screen去，那么滑动的距离应该是 -getScrollX() ；
	 * <li>如果要滑动到Right Screen去，那么滑动的距离应该是 getWidth() - menuWith - getScrollX() ；
	 * @param scrollToScreen
	 */
	private void scrollToScreen(int scrollToScreen){
		
		if( scrollToScreen != currentScreen ){	
			View focusedView = getFocusedChild();
			if( focusedView != null && getChildAt(currentScreen) == focusedView ){
				focusedView.clearFocus() ;
			}
		}
		int scrollX = getScrollX() ;
		int dx = 0 ;
		if( scrollToScreen == LEFT_SCREEN ){
			dx = -scrollX ;
		}else if( scrollToScreen == RIGHT_SCREEN ){
			dx = getWidth() - scrollX ;
		}
		Log.i(TAG, "scrollToScreen:  scroll to screen="+scrollToScreen
				+"  scroll distance="+dx
				+"  time="+Math.abs(dx) * 4);
		
		scroller.startScroll(scrollX, 0, dx, Math.abs(dx) * 4) ;
		//让UI线程在未来某个时间重新绘制view
		invalidate() ;
		currentScreen = scrollToScreen ;
		fling = false ;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startScrollX = getScrollX() ;
			break;
		case MotionEvent.ACTION_UP:
			endScrollX = getScrollX() ;
//			Log.i(TAG, "onTouchEvent: startScrollX="+startScrollX +" endScrollX="+endScrollX);
			if( endScrollX != startScrollX ){
				snapToDestination();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		default:
			break;
		}
		return true;
	}
	
	class MyGuesterListenerImpl implements OnGestureListener{

		
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {
		}
		
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			if( currentScreen == LEFT_SCREEN ){
				//当显示Left Screen时，只能向右滑动
				if( distanceX > 0 ){
					scrollBy((int) distanceX, 0) ;
				}
			}else{
				//当显示Right Screen，只能向左滑动
				if( distanceX < 0 ){
					scrollBy((int)distanceX, 0) ;
				}
			}
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			/**
			 * 如果velocityX的绝对值大于飞滑的最小速度，那么说明用户正在进行飞速滑动操作，此时需要将屏幕滑动到指定Screen
			 * <li>当前用户在Left Screen，如果飞速向left滑动，那么需要将屏幕滑动到Right Screen;否则滑回原来的Screen
			 * <li>当前用户在Right Screen，如果飞速向Right滑动，那么需要将屏幕滑动到Left Screen;否则滑回原来的Screen
			 */
			
//			int maxFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
//			Log.i(TAG, "gestureDetector->onFling: velocityX="+velocityX
//					+ "  minFlingVelocity="+minFlingVelocity
//					+ "  maxFlingVelocity="+maxFlingVelocity
//					+ "  current screen="+currentScreen);
			
			if( Math.abs(velocityX) >= minFlingVelocity ){
				flingTo = -1 ;
				fling = true; 
				if( currentScreen == LEFT_SCREEN ){
					if(velocityX < 0){
						//拖动Right Screen向左飞速滑动，
						flingTo = RIGHT_SCREEN ;
					}else{
						flingTo = LEFT_SCREEN;
					}
				}else{
					if (velocityX > 0) {
						// 拖动Left Screen向右飞速滑动，
						flingTo = LEFT_SCREEN;
					}else{
						flingTo = RIGHT_SCREEN ;
					}
				}
				Log.i(TAG, "gestureDetector->onFling:  flingTo="+flingTo);
			}
			return true ;
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
	
	}
}
