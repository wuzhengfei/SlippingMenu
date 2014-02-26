package com.wzf.slippingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import com.wzf.slippingmenu.ScrollLayout3ScreenAutoCoverView.OnScrollSideChangedListener;


public class ScrollLayout3ScreenAutoCoverActivity extends Activity {
	private static final String TAG = ScrollLayout3ScreenAutoCoverActivity.class.getSimpleName() ;
	private LinearLayout leftScreen ;
	private LinearLayout rightScreen ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroll_layout_3_screenautocover);
		
		leftScreen = (LinearLayout) findViewById(R.id.left_screen);
		rightScreen = (LinearLayout) findViewById(R.id.right_screen);
		leftScreen.setVisibility(View.VISIBLE);
		rightScreen.setVisibility(View.GONE);
		
		ScrollLayout3ScreenAutoCoverView scrollLayout = (ScrollLayout3ScreenAutoCoverView)findViewById(R.id.middle_screen);
		scrollLayout.setOnScrollSideChangedListener(new OnScrollSideChangedListener() {
			
			@Override
			public void onScrollSideChange(boolean showLeft) {
//				Log.i(TAG, "onScrollSideChange  showLeft="+showLeft) ;
				if( showLeft ){
					leftScreen.setVisibility(View.VISIBLE);
					rightScreen.setVisibility(View.GONE);
				}else{
					leftScreen.setVisibility(View.GONE);
					rightScreen.setVisibility(View.VISIBLE);
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
