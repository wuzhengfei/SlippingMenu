package com.wzf.slippingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
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
public class ScrollLayout3ScreenWidthView extends LinearLayout {
	private static final String TAG = ScrollLayout3ScreenWidthView.class.getSimpleName() ;
	public static final int LEFT_SCREEN = 0 ;
	public static final int MIDDLE_SCREEN = 1;
	public static final int RIGHT_SCREEN = 2;
	
	private Scroller scroller ;
	/**
	 * Left Screen和Right Screen的宽度
	 */
	private int sideScreenWidth = 360 ;
	
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
	private int flingTo = -1 ;
	private int minFlingVelocity;
	private int touchSlop ;
	private int startX ;
	private int currentX ;
	/**
	 * 最开始，默认显示中间的Screen
	 */
	private boolean scrollToMiddle = true;
	
	public ScrollLayout3ScreenWidthView(Context context) {
		super(context);
		initView(context);
	}

	public ScrollLayout3ScreenWidthView(Context context, AttributeSet attrs) {
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
		minFlingVelocity = config.getScaledMinimumFlingVelocity();
		touchSlop = config.getScaledTouchSlop();
		
		gestureDetector = new GestureDetector(context, new MyGuesterListenerImpl());
	}
	
	

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		Log.i(TAG, "onLayout    left="+left+"   right="+right) ;
		int count = getChildCount() ;
		int leftSideWidth = 0 ;
		for( int i = 0; i<count; i++ ){
			View child = getChildAt(i);
			int windowWidth = right - left;
			int childWidth = windowWidth ;
			if( i != MIDDLE_SCREEN ){
				childWidth = windowWidth < sideScreenWidth ? windowWidth : sideScreenWidth ; 
			}
			child.measure(childWidth, bottom-top);
			child.layout(leftSideWidth , 0, leftSideWidth + childWidth, getHeight());
			leftSideWidth = leftSideWidth + childWidth ;
			
//			child.setVisibility(View.VISIBLE);
		}
		
		if( scrollToMiddle ){
			scrollToScreen(MIDDLE_SCREEN, false);
			scrollToMiddle = false;
		}
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
	 * 滑动结束以后，判断是应该滑动到Left Screen, Middle Scree还是Right Screen
	 * <ol>
	 * 	<li>如果是飞速滑动，那么直接滑动到指定的window
	 * 	<li>如果不是飞速滑动
	 * 		<ol>
	 * 			<li>当scrollX < ( screenWidth - menuWith )/2, 滑动到Left Screen
	 * 			<li>当scrollX > ( screenWidth - menuWith )/2, 并且scrollX < (screenWidth - menuWith + screenWidth/2) 滑动到Middle Screen
	 * 			<li>当scrollX > (screenWidth - menuWith + screenWidth/2), 滑动到Right Screen
	 */
	private void snapToDestination(){
		int scrollX = getScrollX() ;
		int screenWidth = getWidth() ;
		if( fling ){
			if( flingTo >= 0 ){
				scrollToScreen(flingTo, true);
			}
		}else{
			if ( scrollX  < sideScreenWidth /2 ) {
				scrollToScreen(LEFT_SCREEN, true);
			}else if( scrollX  < ( sideScreenWidth + sideScreenWidth/2 )){
				scrollToScreen(MIDDLE_SCREEN, true);
			}else {
				scrollToScreen(RIGHT_SCREEN, true) ;
			}
		}
		
	}
	
	/**
	 * 滑动到指定屏幕
	 * <li>如果current Screen和 to scroll screen不是一个，检查当前focused view是不是current view，
	 * 	如果是则需要清除focus信息，因为需要聚焦到滑动到的那个screen去
	 * <li>如果要滑动到Left Screen去，那么滑动的距离应该是 -getScrollX() ；
	 * <li>如果要滑动到Middle Screen去，那么滑动的距离应该是  getWidth() - menuWith -getScrollX() ；
	 * <li>如果要滑动到Right Screen去，那么滑动的距离应该是 getWidth() - menuWith - getScrollX() ；
	 * @param scrollToScreen
	 */
	public void scrollToScreen(int scrollToScreen, boolean anim){
		
		if( scrollToScreen != currentScreen ){	
			View focusedView = getFocusedChild();
			if( focusedView != null && getChildAt(currentScreen) == focusedView ){
				focusedView.clearFocus() ;
			}
		}
		int scrollX = getScrollX() ;
		int dx = 0 ;
		if( scrollToScreen == LEFT_SCREEN ){
			dx = - scrollX ;
		}else if( scrollToScreen == MIDDLE_SCREEN ){
			dx = sideScreenWidth - scrollX ;
		}else if( scrollToScreen == RIGHT_SCREEN ){
			dx = 2 * sideScreenWidth - scrollX  ;
		}
		Log.i(TAG, "scrollToScreen:  scroll to screen="+scrollToScreen
				+"  scroll distance="+dx +"   scrollX="+scrollX +"   width="+getWidth());
		if( anim ){
			scroller.startScroll(scrollX, 0, dx, Math.abs(dx) * 4) ;
		}else{
			scroller.startScroll(scrollX, 0, dx, 0) ;
		}
		//让UI线程在未来某个时间重新绘制view
		invalidate() ;
		currentScreen = scrollToScreen ;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		currentX = (int)ev.getX() ;
		
		gestureDetector.onTouchEvent(ev);
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = (int)ev.getX() ;
			break;
		case MotionEvent.ACTION_UP:
			snapToDestination();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		default:
			break;
		}
		return true;
	}




	public class MyGuesterListenerImpl implements OnGestureListener{
		
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.i(TAG, "gestureDetector->onSingleTapUp");
			return false;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {
		}
		
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			/**
			 * 如果当前显示的是Middle screen
			 * <li>如果之前显示过Left Screen，用户向右滑动时，即需要显示Left Screen，此时因为Left Screen处于Visiable状态，
			 * 		不需要调用onScrollSideChange来显示Left Screen；如果用户向左滑动，即需要显示Right Screen，
			 * 		此时因为Right Screen处于Gone的装填，需要调用onScrollSideChange来显示Right Screen。
			 * <li>如果之前显示过Right Screen，用户向右滑动时，即需要显示Left Screen，此时因为Left Screen处于Gone状态，
			 * 		需要调用onScrollSideChange来显示的Left Screen；如果用户向左滑动，即需要显示Right Screen，
			 * 		此时因为Right Screen处于Visiable装填，不需要调用onScrollSideChange显示Right Screen。
			 */
			int deltaX = currentX - startX ;
			int offset = Math.abs(deltaX) ;
			if( offset > touchSlop ){
				if( LEFT_SCREEN == currentScreen ){	//当前处于Left Screen
					if( deltaX < 0 ){ //往左滑动，希望显示右侧的window
						scrollBy((int) distanceX, 0) ;
					}
				}else if( MIDDLE_SCREEN == currentScreen ){//当前处于Middle Screen
					int scrollX = getScrollX() ;
					Log.i(TAG, "gestureDetector->onScroll:   scrollX="+scrollX);
					
					scrollBy((int) distanceX, 0) ;
				}else{//当前处于Right Screen
					if( deltaX > 0 ){ //往右滑动，希望显示左侧的window
						scrollBy((int) distanceX, 0) ;
					}
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
			 * <li>当前用户在Left Screen，如果飞速向left滑动，那么需要将屏幕滑动到Middle Screen
			 * <li>当前用户在Middle Screen，如果飞速向left滑动，那么需要将屏幕滑动到Right Screen;如果飞速向Right滑动，那么需要将屏幕滑动到Left Screen
			 * <li>当前用户在Right Screen，如果飞速向Right滑动，那么需要将屏幕滑动到Middle Screen
			 */
			
			if( Math.abs(velocityX) >= minFlingVelocity ){
				fling = true; 
				if( currentScreen == LEFT_SCREEN ){
					if(velocityX < 0){
						//拖动Middle Screen向左飞速滑动，
						flingTo = MIDDLE_SCREEN ;
					}
				}else if( currentScreen == MIDDLE_SCREEN ){
					if( velocityX < 0 ){
						//拖动Middle Screen向左飞速滑动，
						flingTo = LEFT_SCREEN ;
					}else if(velocityX > 0){
						//拖动Middle Screen向右飞速滑动，
						flingTo = RIGHT_SCREEN ;
					}
				}else{
					if (velocityX > 0) {
						// 拖动Middle Screen向右飞速滑动，
						flingTo = MIDDLE_SCREEN;
					}
				}
				Log.i(TAG, "gestureDetector->onFling:  flingTo="+flingTo);
				fling = false; 
				flingTo = -1 ;
			}
			return true ;
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
	
	}
	
}
