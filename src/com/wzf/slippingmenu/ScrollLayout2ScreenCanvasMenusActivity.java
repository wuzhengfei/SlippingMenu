package com.wzf.slippingmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

/**
 * 
 * Datetime   ： 2013-4-19 上午9:42:04
 * author     :  wuzhengfei
 */
public class ScrollLayout2ScreenCanvasMenusActivity extends Activity {
	/** 滑动导航栏以后省下的距离 */
	private Animation leftToRightAnimation ;
	private Animation rightToLeftAnimation ;
	private ImageView slideCover ;
	private ListView menuList ;
	private ArrayAdapter<String> adapter ;
	private static Bitmap coverBitmap ;
	private final int DURATION = 200 ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroll_layout_2_screen_canvas_menu);
		
		WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		int windowWidth = windowManager.getDefaultDisplay().getWidth() ;
		final int dx = (int)(windowWidth * 0.75f) ;
		
		initAnimation(dx);
		
		menuList = (ListView) findViewById(R.id.menu_list) ;
		adapter = new ArrayAdapter<String>(ScrollLayout2ScreenCanvasMenusActivity.this,
				android.R.layout.simple_list_item_1, 
				new String[] { " First", " Second", " Third", " Fourth", " Fifth", " Sixth" }) ;
		menuList.setAdapter(adapter);
		menuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				closeMenu() ;
				
			}
		});
		
		slideCover = (ImageView) findViewById(R.id.slidedout_cover);
		slideCover.setImageBitmap(coverBitmap);
		slideCover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeMenu();
			}
		});
		
		openMenu();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( KeyEvent.KEYCODE_BACK == keyCode ){
			closeMenu() ;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 为指定的View创建一个画布，
	 * @param view
	 * @return
	 */
	public static void prepareViewCoverBitmap(View view){
		if( coverBitmap != null ){
			coverBitmap.recycle();
		}
		coverBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(coverBitmap);
		view.draw(canvas);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * 初始化动画
	 * <li>从左滑到右以后需要将slideCover的为止固定
	 * <li>从右滑到左以后结束当前Activity，
	 * @param dx
	 */
	private void initAnimation(final int dx){
		leftToRightAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, dx, TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, 0) ;
		leftToRightAnimation.setDuration(DURATION);
		leftToRightAnimation.setFillAfter(true);
		
		rightToLeftAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, -dx, TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, 0) ;
		rightToLeftAnimation.setDuration(DURATION);
		rightToLeftAnimation.setFillAfter(true);
		
//		不能使用以下方式创建Animation，这样会导致屏幕闪烁，目前没有找到原因		
//		leftToRightAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_animation_to_right);
//		rightToLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_animation_to_left);
		
		leftToRightAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				slideCover.clearAnimation() ;
				AbsoluteLayout.LayoutParams p = new AbsoluteLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, dx, 0) ;
				slideCover.setLayoutParams(p);
			}
		}) ;
		
		rightToLeftAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				finish();
				overridePendingTransition(0, 0);
			}
		}) ;
	}
	
	private void openMenu(){
		slideCover.clearAnimation();
		slideCover.startAnimation(leftToRightAnimation);
	}
	
	private void closeMenu(){
		slideCover.clearAnimation();
		slideCover.startAnimation(rightToLeftAnimation);
	}
}
