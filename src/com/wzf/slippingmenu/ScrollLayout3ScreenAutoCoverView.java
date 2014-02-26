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
 * 水平滑动的Layout，此Layout适合三个Screen滑动，滑动时中间的Screen会覆盖左右两个Screen
 * 当拖动一定距离以后，中间的Screen会自动覆盖左边或者右边的Screen
 * Datetime   ： 2013-4-8 下午8:54:31
 * author     :  wuzhengfei
 */
public class ScrollLayout3ScreenAutoCoverView extends LinearLayout {
	private static final String TAG = ScrollLayout3ScreenAutoCoverView.class.getSimpleName() ;
	public static final int LEFT_SCREEN = 0 ;
	public static final int MIDDLE_SCREEN = 1;
	public static final int RIGHT_SCREEN = 2;
	
	/**
	 * 滑动开始时的ScrollX
	 */
	private float startScrollX = 0;
	private Scroller scroller ;
	/**
	 * 菜单的宽度
	 */
	private static final int hideWidth = 240 ;
	private static final int minDragWidth = 80 ;
	
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
	private boolean showLeft  ;
	private OnScrollSideChangedListener onScrollSideChangedListener ;
	private int minFlingVelocity;
	/**
	 * 起始时X轴位置
	 */
	private int startX ;
	/**
	 * 当前X轴的位置
	 */
	private int currentX ;
	/**
	 * 是否允许Scroll，因为在左右拖动Screen时，当拖动距离超过minDragWidth时，将自动滚动到对应的Screen；此后，不应该再允许滚动。
	 */
	private boolean scrollEnable = false;
	
	public ScrollLayout3ScreenAutoCoverView(Context context) {
		super(context);
		initView(context);
	}

	public ScrollLayout3ScreenAutoCoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	/**
	 * 初始化View
	 * @param context
	 */
	private void initView(final Context context){
		showLeft = true;
		scroller = new Scroller(context, AnimationUtils.loadInterpolator(context, android.R.anim.overshoot_interpolator)) ;
		
		gestureDetector = new GestureDetector(context, new  OnGusterListenerImpl());
		minFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
	}
	
	

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		//首次聚焦在中间屏幕，需要在此处调用scrollToScreen方法。
		scrollToScreen(MIDDLE_SCREEN) ;
		
		int moveWith = getWidth() - hideWidth ;
		for( int i = 0 ; i<getChildCount(); i++ ){
			View child = getChildAt(i);
			Log.i(TAG, "onLayout:   move with="+moveWith +"   left="+child.getLeft() +"  top="+child.getTop() );
			child.layout(child.getLeft() + moveWith, child.getTop(), child.getRight() + moveWith, child.getBottom()) ;
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
		if( !scrollEnable ){
			return ;
		}
		if( fling ){
			if( flingTo >= 0 ){
				scrollToScreen(flingTo);
			}
		}else{
			int delatX =  currentX - startX ;
			int offset = Math.abs(delatX) ;
			Log.i(TAG, "snapToDestination  delatX="+delatX+"  currentScreen="+currentScreen) ;
			if( delatX > 0 ){ //向右拖动
				if( offset > minDragWidth && currentScreen > LEFT_SCREEN ){
					scrollToScreen(currentScreen - 1);
				}else{
					scrollToScreen(currentScreen);
				}
			}else{//向左拖动
				if( offset > minDragWidth && currentScreen < RIGHT_SCREEN ){
					scrollToScreen(currentScreen + 1);
				}else{
					scrollToScreen(currentScreen);
				}
			}
//			if ( scrollX < ( screenWidth - hideWidth )/2 ) {
//				scrollToScreen(LEFT_SCREEN);
//			}else if( scrollX < (screenWidth - hideWidth + screenWidth/2) ){
//				scrollToScreen(MIDDLE_SCREEN);
//			}else{
//				scrollToScreen(RIGHT_SCREEN) ;
//			}
		}
		scrollEnable = false;
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
	public void scrollToScreen(int scrollToScreen){
		
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
			dx = getWidth() - hideWidth - scrollX ;
		}else if( scrollToScreen == RIGHT_SCREEN ){
			dx = getWidth() * 2 - scrollX - 2 * hideWidth ;
		}
		Log.i(TAG, "scrollToScreen:  scroll to screen="+scrollToScreen
				+"  scroll distance="+dx
				+"  time="+Math.abs(dx) * 4);
		
		scroller.startScroll(scrollX, 0, dx, Math.abs(dx) * 4) ;
		//让UI线程在未来某个时间重新绘制view
		invalidate() ;
		currentScreen = scrollToScreen ;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		currentX = (int)event.getX() ;
		gestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startScrollX = getScrollX() ;
			startX = (int)event.getX() ;
			scrollEnable = true;
			break;
		case MotionEvent.ACTION_UP:
//			Log.i(TAG, "onTouchEvent: startScrollX="+startScrollX +" endScrollX="+endScrollX);
			snapToDestination();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		default:
			break;
		}
		return true;
	}


	public OnScrollSideChangedListener getOnScrollSideChangedListener() {
		return onScrollSideChangedListener;
	}

	public void setOnScrollSideChangedListener(
			OnScrollSideChangedListener onScrollSideChangedListener) {
		this.onScrollSideChangedListener = onScrollSideChangedListener;
	}





	public class OnGusterListenerImpl implements OnGestureListener {
		
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
			/**
			 * 如果当前显示的是Middle screen
			 * <li>如果之前显示过Left Screen，用户向右滑动时，即需要显示Left Screen，此时因为Left Screen处于Visiable状态，
			 * 		不需要调用onScrollSideChange来显示Left Screen；如果用户向左滑动，即需要显示Right Screen，
			 * 		此时因为Right Screen处于Gone的装填，需要调用onScrollSideChange来显示Right Screen。
			 * <li>如果之前显示过Right Screen，用户向右滑动时，即需要显示Left Screen，此时因为Left Screen处于Gone状态，
			 * 		需要调用onScrollSideChange来显示的Left Screen；如果用户向左滑动，即需要显示Right Screen，
			 * 		此时因为Right Screen处于Visiable装填，不需要调用onScrollSideChange显示Right Screen。
			 */
			int dx = currentX - startX;
			int offset = Math.abs( dx ) ;
			if( offset > minDragWidth ){	//当拖动距离超过minDragWidth，直接滚动进入对应的Screen
				snapToDestination();
			}else{
				if( MIDDLE_SCREEN == currentScreen ){
					int screenRestWidth = getWidth() - hideWidth ;
					int scrollX = getScrollX() ;
//					Log.i(TAG, "gestureDetector->onScroll: " 
//							+"  showLeft="+showLeft
//							+"  scrollX="+scrollX
//							+"  screenRestWidth="+screenRestWidth
//							+"  startScrollX="+startScrollX
//							+"  currentScrollX="+e2.getX());
					if( showLeft ){	//如果之前显示的是Left Screen
						if( scrollX > screenRestWidth || (scrollX == screenRestWidth && startScrollX < e2.getX()) ){
							showLeft = false ;
							if( onScrollSideChangedListener != null ){
								onScrollSideChangedListener.onScrollSideChange(showLeft);
							}
						}
					}else{	//如果之前显示的是Right Screen
						if( (scrollX < screenRestWidth) || (scrollX == screenRestWidth && startScrollX > e2.getX()) ){
							showLeft = true;
							if( onScrollSideChangedListener != null ){
								onScrollSideChangedListener.onScrollSideChange(showLeft);
							}
						}
					}
					scrollBy((int) distanceX, 0) ;
				}else if( currentScreen == LEFT_SCREEN  ){	
					if(  dx < 0 ){	//向左拖动
						scrollBy((int) distanceX, 0) ;
					}
				}else{
					if(  dx > 0 ){	//向左拖动
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
//				Log.i(TAG, "gestureDetector->onFling:  flingTo="+flingTo);
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












	/**
	 * Listener，监听Layout滑动到边界事件
	 * Datetime   ： 2013-4-8 下午9:02:24
	 * author     :  wuzhengfei
	 */
	public interface OnScrollSideChangedListener{
		/**
		 * Layout滑动到边界触发， 
		 * @param view
		 * @param showLeft ： 显示左边的页面吗？如果为false，表示显示右边的页面。
		 */
		public void onScrollSideChange(boolean showLeft);
	}
}
