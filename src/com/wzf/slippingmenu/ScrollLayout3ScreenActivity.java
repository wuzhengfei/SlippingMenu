package com.wzf.slippingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class ScrollLayout3ScreenActivity extends Activity {
	private static final String TAG = ScrollLayout3ScreenActivity.class.getSimpleName() ;
	private LinearLayout leftScreen ;
	private LinearLayout middleScreen ;
	private LinearLayout rightScreen ;
	private ScrollLayout3ScreenView layout3Screen ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroll_layout_3_screen);
		
		leftScreen = (LinearLayout) findViewById(R.id.left_screen);
		middleScreen = (LinearLayout) findViewById(R.id.middle_screen);
		rightScreen = (LinearLayout) findViewById(R.id.right_screen);
		
		ScrollLayout3ScreenView scrollLayout = (ScrollLayout3ScreenView)findViewById(R.id.screen_3_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
