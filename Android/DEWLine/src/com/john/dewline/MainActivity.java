/*
	Notas:

	1) For margins it will be dp. sp is used only for textsizes.

	2) Values of screen densities (https://stackoverflow.com/questions/3166501/getting-the-screen-density-programmatically-
			in-android)

		0.75 - ldpi    - 120 dpi
		1.0  - mdpi    - 160 dpi (OK)
		1.5  - hdpi    - 240 dpi
		2.0  - xhdpi   - 320 dpi (OK)
		3.0  - xxhdpi  - 480 dpi
		4.0  - xxxhdpi - 640 dpi

	3. Starting onCreate is (commented) how to use full screen mode

	4. Handle SD-Card

	   - Open clink
	   - Change to D:\AndroidSDK\platform-tools
	   - adb shell
	   - su
	   - mount -o rw,remount rootfs /
	   - chmod 777 /mnt/sdcard
	   - exit
*/

package com.john.dewline;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

class Globals {
	public static Context context;
	public static int widthScreen, heightScreen;
	public static int unitSystem=1;
}

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		Globals.context = this;

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Globals.widthScreen = metrics.widthPixels;
		Globals.heightScreen = metrics.heightPixels;							// Subtract the Navigation Bar Height included
		//Globals.heightScreen = metrics.heightPixels + getNavigationBarHeight();	// Subtract the Navigation Bar Height included, so
																				// it's necessary to add it again
System.out.println("1. "+Globals.widthScreen+"   "+Globals.heightScreen);
		Globals.heightScreen -= getStatusBarHeight();							// Top bar
		//Globals.heightScreen -= getActiomBarHeight();							// Title (title suppressed)
		System.out.println("Width screen for App: " + Globals.widthScreen + "; Height screen for App: " + Globals.heightScreen);
		System.out.println("Status Bar Height (top bar): "+getStatusBarHeight()+"; "+
						   "Action Bar Height (title): "+getActiomBarHeight()+"; "+
						   "Navegation Bar Height (bottom bar): " + getNavigationBarHeight());
		Globals.widthScreen = 1024;
		Globals.heightScreen = 768;

		// Display the main screen
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		FragmentMain fragmentMain = new FragmentMain();
		ft.replace(R.id.fragment,fragmentMain);
		ft.commit();

		//FragmentManager fm = getFragmentManager();
		//FragmentTransaction ft = fm.beginTransaction();
		//FragmentFuel fragmentFuel = new FragmentFuel();
		//ft.replace(R.id.fragment,fragmentFuel);
		//ft.commit();

		//FragmentManager fm = getFragmentManager();
		//FragmentTransaction ft = fm.beginTransaction();
		//FragmentSetup fragmentSetup = new FragmentSetup();
		//ft.replace(R.id.fragment,fragmentSetup);
		//ft.commit();

		//FragmentManager fm = getFragmentManager();
		//FragmentTransaction ft = fm.beginTransaction();
		//FragmentHistory fragmentHistory = new FragmentHistory();
		//ft.replace(R.id.fragment,fragmentHistory);
		//ft.commit();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			hideSystemUI();
		}
	}

	public void hideSystemUI() {
		// Enables regular immersive mode.
		// For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
		// Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(
			View.SYSTEM_UI_FLAG_IMMERSIVE
			// Set the content to appear under the system bars so that the
			// content doesn't resize when the system bars hide and show.
			| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			// Hide the nav bar and status bar
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

	// Shows the system bars by removing all the flags
	// except for the ones that make the content appear under the system bars.
	private void showSystemUI() {
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public int getActiomBarHeight() {
		int actionBarHeight = 0;
		final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
				new int[] { android.R.attr.actionBarSize }
		);
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();
		return actionBarHeight;
	}

	public int getNavigationBarHeight()
	{
		boolean hasMenuKey = ViewConfiguration.get(getBaseContext()).hasPermanentMenuKey();
		int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0 && !hasMenuKey)
		{
			return getResources().getDimensionPixelSize(resourceId);
		}
		return 0;
	}
}
