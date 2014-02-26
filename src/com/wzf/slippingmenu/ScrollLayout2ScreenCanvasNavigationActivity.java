package com.wzf.slippingmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 在页面中点击一个导航按钮，屏幕向右滑动，露出左边的Menu页，此时原来页面还显示部分，当点击剩余剩余部分的Screen时，又回到原来的Screen中
 * Datetime   ： 2013-4-19 上午9:36:43
 * author     :  wuzhengfei
 */
public class ScrollLayout2ScreenCanvasNavigationActivity extends Activity {
	private Button navigation ;
	private View contentContainer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroll_layout_2_screen_canvas_right);
		
		contentContainer = findViewById(R.id.content);
		navigation = (Button) findViewById(R.id.slide_button);
		
		navigation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScrollLayout2ScreenCanvasMenusActivity.prepareViewCoverBitmap(contentContainer);
				Intent intent = new Intent(ScrollLayout2ScreenCanvasNavigationActivity.this, ScrollLayout2ScreenCanvasMenusActivity.class);
				startActivity(intent);
				/**
				 * Activity的切换动画指的是从一个activity跳转到另外一个activity时的动画。
				 * <li>包括：第一个activity退出时的动画和第二个activity进入时的动画
				 * overridePendingTransition可以用来实现此目的，
				 * 		这个函数有两个参数，一个参数是第一个activity退出时的动画，另外一个参数则是第二个activity进入时的动画。
				 * <br>
				 * <li>它必需紧挨着startActivity()或者finish()函数之后调用; 
				 * <li>它只在android2.0以及以上版本上适用
				 */
				overridePendingTransition(0, 0 );
			}
		});
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
