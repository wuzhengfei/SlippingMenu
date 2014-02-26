package com.wzf.slippingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ScrollLayout3ScreenWidthActivity extends Activity {
	private static final String TAG = ScrollLayout3ScreenWidthActivity.class.getSimpleName() ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroll_layout_3_screen_width);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
