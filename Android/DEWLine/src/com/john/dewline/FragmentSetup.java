package com.john.dewline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentSetup extends Fragment {

	View rootView;

	public static File root,dewLineFolder;
	public static SQLiteDatabase dewLineDB;
	public static Cursor cursor = null;
	public static String commandSQL;
	//public static Button buttonMain;
	public static Spinner spinnerLeftColumnSecondRowSensors;
	public static boolean flagAlarmsLowerLimitsExist,flagAlarmsUpperLimitsExist,flagAlarmDynamicalExist,
				flagAlarmsShutdownLowerLimitsExist,flagAlarmsShutdownUpperLimitsExist;
	public static LinearLayout linearLayoutLeftColumnThirdRowP2TopImage,linearLayoutLeftColumnThirdRowP3TopImage,
				linearLayoutLeftColumnThirdRowP4TopImage,
				linearLayoutLeftColumnSixthRowP2Image,linearLayoutLeftColumnSixthRowP3Image,
				linearLayoutLeftColumnSixthRowP4Image;
	public static ImageView imageViewLeftColumnThirdRowP2TopImage,imageViewLeftColumnThirdRowP3TopImage,
				imageViewLeftColumnThirdRowP4TopImage,
				imageViewLeftColumnSixthRowP2Image,imageViewLeftColumnSixthRowP3Image,
				imageViewLeftColumnSixthRowP4Image;
	public static EditText editTextLeftColumnFourthRowP2LeftValue,editTextLeftColumnFourthRowP2RightValue,
				editTextLeftColumnFourthRowP3LeftValue,editTextLeftColumnFourthRowP3RightValue,
				editTextLeftColumnFifthRowP2LeftValue,editTextLeftColumnFifthRowP2RightValue,
				editTextLeftColumnFifthRowP3LeftValue,editTextLeftColumnFifthRowP3RightValue;
	public static TextView 	textViewLeftColumnSeventhRowP2Value,textViewLeftColumnSeventhRowP3Value,
				textViewLeftColumnEighthRowP2Value,textViewLeftColumnEighthRowP3Value;

	public static boolean flagFullScreen = true;
	public static final float HEIGHT_ROW_LEFT_COLUMN=0.0435f,HEIGHT_ROW_RIGHT_COLUMN=0.048f;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_setup, container,false);

		root = Environment.getExternalStorageDirectory();
		dewLineFolder = new File(root.getAbsolutePath()+"/DEWLine/");

		dewLineDB = Globals.context.openOrCreateDatabase(dewLineFolder+"/DEWLine.db",
				Globals.context.MODE_PRIVATE, null);
		createGUI();
		dewLineDB.close();

		// LISTENERS

// ********** SOLUTION PROVISSIONAL TO DISPLAY IN MODE FULL SCREEN (Click on the screen)
		rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				View decorView = getActivity().getWindow().getDecorView();
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
				System.out.println("Main");
			}
		});

		Button buttonHistory = (Button) rootView.findViewById(R.id.buttonHistory);
		buttonHistory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Display the Setup screen
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				FragmentHistory fragmentHistory = new FragmentHistory();
				ft.replace(R.id.fragment,fragmentHistory);
				ft.commit();
			}
		});

		Button buttonMain = (Button) rootView.findViewById(R.id.buttonMain);
		buttonMain.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Display the Setup screen
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				FragmentMain fragmentMain = new FragmentMain();
				ft.replace(R.id.fragment,fragmentMain);
				ft.commit();
			}
		});

		Button buttonFuel = (Button) rootView.findViewById(R.id.buttonFuel);
		buttonFuel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(!ConstantsGlobals.FLAG_DEVELOPMENT)
					return;
				// Display the Fuel screen
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				FragmentFuel fragmentFuel = new FragmentFuel();
				ft.replace(R.id.fragment,fragmentFuel);
				ft.commit();
			}
		});

		spinnerLeftColumnSecondRowSensors.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				dewLineDB = Globals.context.openOrCreateDatabase(dewLineFolder+"/DEWLine.db",
						Globals.context.MODE_PRIVATE, null);
				updateAlarmsGUI();
				dewLineDB.close();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

/*
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
					    Rect r = new Rect();
					    rootView.getWindowVisibleDisplayFrame(r);
					    int screenHeight = rootView.getRootView().getHeight();

					    // r.bottom is the position above soft keypad or device button.
					    // if keypad is shown, the r.bottom is smaller than that before.
					    int keypadHeight = screenHeight - r.bottom;

					    System.out.println("keypadHeight = "+keypadHeight);

					    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
					        // keyboard is opened
					    }
					    else {
					        // keyboard is closed
					    }
              handler.postDelayed(this, 5000);
		  }
		}, 5000);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

System.out.println("heightDiff: "+heightDiff);
                if (heightDiff > 200) {
System.out.println("Keyboard visible");
                } else {
System.out.println("Keyboard oculto");
                }
            }
		});

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
              int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

System.out.println("heightDiff: "+heightDiff);
              if (heightDiff > 200) {
System.out.println("Keyboard visible");
              } else {
System.out.println("Keyboard oculto");
              }
              handler.postDelayed(this, 5000);
		  }
		}, 5000);
*/

		return rootView;
	}

	public void createGUI() {

// -SETTINGS
		// Linear layout "LinearLayoutSettings"
		LinearLayout linearLayoutSettings = (LinearLayout) rootView.findViewById(R.id.linearLayoutSettings);
		ViewGroup.LayoutParams paramsLlinearLayoutSettings = linearLayoutSettings.getLayoutParams();
		paramsLlinearLayoutSettings.width = Globals.widthScreen;
		paramsLlinearLayoutSettings.height = (int)(Globals.heightScreen * 0.9);
		linearLayoutSettings.setLayoutParams(paramsLlinearLayoutSettings);
/// -LEFT COLUMN
		// Linear layout "LinearLayoutLeftColumn"
		LinearLayout linearLayoutLeftColumn = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumn);
		ViewGroup.LayoutParams paramsLinearLayoutLeftColumn = linearLayoutLeftColumn.getLayoutParams();
		paramsLinearLayoutLeftColumn.width = paramsLlinearLayoutSettings.width / 2;
		paramsLinearLayoutLeftColumn.height = paramsLlinearLayoutSettings.height;
		linearLayoutLeftColumn.setLayoutParams(paramsLinearLayoutLeftColumn);

			LinearLayout linearLayoutLeftColumnFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFirstRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFirstRow = linearLayoutLeftColumnFirstRow.getLayoutParams();
			paramsLinearLayoutLeftColumnFirstRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnFirstRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN*1.2);
			linearLayoutLeftColumnFirstRow.setLayoutParams(paramsLinearLayoutLeftColumnFirstRow);
				TextView textViewLeftColumnFirstRow = (TextView) rootView.findViewById(R.id.textViewLeftColumnFirstRowTitle);

			LinearLayout linearLayoutLeftColumnSecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSecondRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSecondRow = linearLayoutLeftColumnSecondRow.getLayoutParams();
			paramsLinearLayoutLeftColumnSecondRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnSecondRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnSecondRow.setLayoutParams(paramsLinearLayoutLeftColumnSecondRow);

				LinearLayout linearLayoutLeftColumnSecondRowlabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSecondRowlabel);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSecondRowlabel = linearLayoutLeftColumnSecondRowlabel.getLayoutParams();
				paramsLinearLayoutLeftColumnSecondRowlabel.width = (int)(paramsLinearLayoutLeftColumnSecondRow.width*0.2);
				paramsLinearLayoutLeftColumnSecondRowlabel.height = paramsLinearLayoutLeftColumnSecondRow.height;
				linearLayoutLeftColumnSecondRowlabel.setLayoutParams(paramsLinearLayoutLeftColumnSecondRowlabel);
					TextView textViewLeftColumnSecondRowLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnSecondRowLabel);

				LinearLayout linearLayoutLeftColumnSecondRowSensors = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSecondRowSpinner);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSecondRowSensors = linearLayoutLeftColumnSecondRowSensors.getLayoutParams();
				paramsLinearLayoutLeftColumnSecondRowSensors.width = (int)(paramsLinearLayoutLeftColumnSecondRow.width*0.5);
				paramsLinearLayoutLeftColumnSecondRowSensors.height = paramsLinearLayoutLeftColumnSecondRow.height;
				linearLayoutLeftColumnSecondRowSensors.setLayoutParams(paramsLinearLayoutLeftColumnSecondRowSensors);
					spinnerLeftColumnSecondRowSensors = (Spinner) rootView.findViewById(R.id.spinnerLeftColumnSecondRowSensors);

					commandSQL="SELECT * FROM AlarmParameters";
					cursor = dewLineDB.rawQuery(commandSQL,null);
					List<String> temp = new ArrayList<String>();
					if(cursor.getCount()>0) {
						cursor.moveToFirst();
						while (!cursor.isAfterLast()) {
							temp.add(cursor.getString(cursor.getColumnIndex("Parameter_AlarmParameters")));
							cursor.moveToNext();
						}
					}
					String[] sensors = new String[temp.size()];
					temp.toArray(sensors);
					ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Globals.context,R.layout.spinner_item,sensors);
					arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					spinnerLeftColumnSecondRowSensors.setAdapter(arrayAdapter);
					spinnerLeftColumnSecondRowSensors.setSelection(0);

			LinearLayout linearLayoutLeftColumnThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRow = linearLayoutLeftColumnThirdRow.getLayoutParams();
			paramsLinearLayoutLeftColumnThirdRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnThirdRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN*1.4);
			linearLayoutLeftColumnThirdRow.setLayoutParams(paramsLinearLayoutLeftColumnThirdRow);

				LinearLayout linearLayoutLeftColumnThirdRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP1);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP1 = linearLayoutLeftColumnThirdRowP1.getLayoutParams();
				paramsLinearLayoutLeftColumnThirdRowP1.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnThirdRowP1.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP1);
					TextView textViewtLeftColumnThirdRowP1Label = (TextView) rootView.findViewById(R.id.textViewtLeftColumnThirdRowP1Label);

				LinearLayout linearLayoutLeftColumnThirdRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP2);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP2 = linearLayoutLeftColumnThirdRowP2.getLayoutParams();
				paramsLinearLayoutLeftColumnThirdRowP2.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				paramsLinearLayoutLeftColumnThirdRowP2.height = paramsLinearLayoutLeftColumnThirdRow.height;
				linearLayoutLeftColumnThirdRowP2.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP2);

					LinearLayout linearLayoutLeftColumnThirdRowP2Top = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP2Top);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP2Top = linearLayoutLeftColumnThirdRowP2Top.getLayoutParams();
					paramsLinearLayoutLeftColumnThirdRowP2Top.width = paramsLinearLayoutLeftColumnThirdRowP2.width;
					paramsLinearLayoutLeftColumnThirdRowP2Top.height = (int)(paramsLinearLayoutLeftColumnThirdRowP2.height*0.5);
					linearLayoutLeftColumnThirdRowP2Top.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP2Top);

						LinearLayout linearLayoutLeftColumnThirdRowP2TopLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP2TopLabel);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP2TopLabel = linearLayoutLeftColumnThirdRowP2TopLabel.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP2TopLabel.width = (int)(paramsLinearLayoutLeftColumnThirdRowP2Top.width*0.75);
						paramsLinearLayoutLeftColumnThirdRowP2TopLabel.height = paramsLinearLayoutLeftColumnThirdRowP2Top.height;
						linearLayoutLeftColumnThirdRowP2TopLabel.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP2TopLabel);
							TextView textViewLeftColumnThirdRowP2TopLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnThirdRowP2TopLabel);

						linearLayoutLeftColumnThirdRowP2TopImage = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP2TopImage);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP2TopImage = linearLayoutLeftColumnThirdRowP2TopImage.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP2TopImage.width = paramsLinearLayoutLeftColumnThirdRowP2Top.height;
						paramsLinearLayoutLeftColumnThirdRowP2TopImage.height = paramsLinearLayoutLeftColumnThirdRowP2Top.height;
						linearLayoutLeftColumnThirdRowP2TopImage.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP2TopImage);
							imageViewLeftColumnThirdRowP2TopImage = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnThirdRowP2TopImage);
							commandSQL="SELECT * FROM AlarmParameters WHERE Parameter_AlarmParameters='"+
										spinnerLeftColumnSecondRowSensors.getSelectedItem().toString()+"'";
							cursor = dewLineDB.rawQuery(commandSQL,null);
							if(cursor.getCount()>0) {
								cursor.moveToFirst();
								linearLayoutLeftColumnThirdRowP2TopImage.removeAllViews();
								if((cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
									!cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtIdle_AlarmParameters")).trim().equals("―"))||
								   (cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
									!cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtIdle_AlarmParameters")).trim().equals("―"))) {
									linearLayoutLeftColumnThirdRowP2TopImage.addView(imageViewLeftColumnThirdRowP2TopImage);
									flagAlarmsLowerLimitsExist = true;
								}
								else
									flagAlarmsLowerLimitsExist = false;
							}

					LinearLayout linearLayoutLeftColumnThirdRowP2Bottom = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP2Bottom);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP2Bottom = linearLayoutLeftColumnThirdRowP2Bottom.getLayoutParams();
					paramsLinearLayoutLeftColumnThirdRowP2Bottom.width = paramsLinearLayoutLeftColumnThirdRowP2.width;
					paramsLinearLayoutLeftColumnThirdRowP2Bottom.height = (int)(paramsLinearLayoutLeftColumnThirdRowP2.height*0.5);
					linearLayoutLeftColumnThirdRowP2Bottom.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP2Bottom);

						LinearLayout linearLayoutLeftColumnThirdRowP2BottomLabelRed = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP2BottomLabelRed);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP2BottomLabelRed = linearLayoutLeftColumnThirdRowP2BottomLabelRed.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP2BottomLabelRed.width = (int)(paramsLinearLayoutLeftColumnThirdRowP2Bottom.width*0.5);
						paramsLinearLayoutLeftColumnThirdRowP2BottomLabelRed.height = paramsLinearLayoutLeftColumnThirdRowP2Bottom.height;
						linearLayoutLeftColumnThirdRowP2BottomLabelRed.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP2BottomLabelRed);
							TextView textViewLeftColumnThirdRowP2BottomLabelRed = (TextView) rootView.findViewById(R.id.textViewLeftColumnThirdRowP2BottomLabelRed);

						LinearLayout linearLayoutLeftColumnThirdRowP2BottomLabelYellow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP2BottomLabelYellow);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP2BottomLabelYellow = linearLayoutLeftColumnThirdRowP2BottomLabelYellow.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP2BottomLabelYellow.width = (int)(paramsLinearLayoutLeftColumnThirdRowP2Bottom.width*0.5);
						paramsLinearLayoutLeftColumnThirdRowP2BottomLabelYellow.height = paramsLinearLayoutLeftColumnThirdRowP2Bottom.height;
						linearLayoutLeftColumnThirdRowP2BottomLabelYellow.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP2BottomLabelYellow);
							TextView textViewLeftColumnThirdRowP2BottomLabelYellow = (TextView) rootView.findViewById(R.id.textViewLeftColumnThirdRowP2BottomLabelYellow);

				LinearLayout linearLayoutLeftColumnThirdRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP3);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP3 = linearLayoutLeftColumnThirdRowP3.getLayoutParams();
				paramsLinearLayoutLeftColumnThirdRowP3.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				paramsLinearLayoutLeftColumnThirdRowP3.height = paramsLinearLayoutLeftColumnThirdRow.height;
				linearLayoutLeftColumnThirdRowP3.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP3);

					LinearLayout linearLayoutLeftColumnThirdRowP3Top = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP3Top);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP3Top = linearLayoutLeftColumnThirdRowP3Top.getLayoutParams();
					paramsLinearLayoutLeftColumnThirdRowP3Top.width = paramsLinearLayoutLeftColumnThirdRowP3.width;
					paramsLinearLayoutLeftColumnThirdRowP3Top.height = (int)(paramsLinearLayoutLeftColumnThirdRowP3.height*0.5);
					linearLayoutLeftColumnThirdRowP3Top.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP3Top);

						LinearLayout linearLayoutLeftColumnThirdRowP3TopLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP3TopLabel);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP3TopLabel = linearLayoutLeftColumnThirdRowP3TopLabel.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP3TopLabel.width = (int)(paramsLinearLayoutLeftColumnThirdRowP3Top.width*0.75);
						paramsLinearLayoutLeftColumnThirdRowP3TopLabel.height = paramsLinearLayoutLeftColumnThirdRowP3Top.height;
						linearLayoutLeftColumnThirdRowP3TopLabel.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP3TopLabel);
							TextView textViewLeftColumnThirdRowP3TopLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnThirdRowP3TopLabel);

						linearLayoutLeftColumnThirdRowP3TopImage = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP3TopImage);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP3TopImage = linearLayoutLeftColumnThirdRowP3TopImage.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP3TopImage.width = paramsLinearLayoutLeftColumnThirdRowP3Top.height;
						paramsLinearLayoutLeftColumnThirdRowP3TopImage.height = paramsLinearLayoutLeftColumnThirdRowP3Top.height;
						linearLayoutLeftColumnThirdRowP3TopImage.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP3TopImage);
							imageViewLeftColumnThirdRowP3TopImage = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnThirdRowP3TopImage);
							if(cursor.getCount()>0) {
								cursor.moveToFirst();
								linearLayoutLeftColumnThirdRowP3TopImage.removeAllViews();
								if((cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
									!cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtIdle_AlarmParameters")).trim().equals("?"))||
								   (cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtIdle__AlarmParameters")).trim().length()>0&&
									!cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtIdle__AlarmParameters")).trim().equals("?"))) {
									linearLayoutLeftColumnThirdRowP3TopImage.addView(imageViewLeftColumnThirdRowP3TopImage);
									flagAlarmsUpperLimitsExist = true;
								}
								else
									flagAlarmsUpperLimitsExist = false;
							}

					LinearLayout linearLayoutLeftColumnThirdRowP3Bottom = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP3Bottom);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP3Bottom = linearLayoutLeftColumnThirdRowP3Bottom.getLayoutParams();
					paramsLinearLayoutLeftColumnThirdRowP3Bottom.width = paramsLinearLayoutLeftColumnThirdRowP3.width;
					paramsLinearLayoutLeftColumnThirdRowP3Bottom.height = (int)(paramsLinearLayoutLeftColumnThirdRowP3.height*0.5);
					linearLayoutLeftColumnThirdRowP3Bottom.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP3Bottom);

						LinearLayout linearLayoutLeftColumnThirdRowP3BottomLabelYellow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP3BottomLabelYellow);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP3BottomLabelYellow = linearLayoutLeftColumnThirdRowP3BottomLabelYellow.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP3BottomLabelYellow.width = (int)(paramsLinearLayoutLeftColumnThirdRowP3Bottom.width*0.5);
						paramsLinearLayoutLeftColumnThirdRowP3BottomLabelYellow.height = paramsLinearLayoutLeftColumnThirdRowP3Bottom.height;
						linearLayoutLeftColumnThirdRowP3BottomLabelYellow.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP3BottomLabelYellow);
							TextView textViewLeftColumnThirdRowP3BottomLabelYellow = (TextView) rootView.findViewById(R.id.textViewLeftColumnThirdRowP3BottomLabelYellow);

						LinearLayout linearLayoutLeftColumnThirdRowP3BottomLabelRed = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP3BottomLabelRed);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP3BottomLabelRed = linearLayoutLeftColumnThirdRowP3BottomLabelRed.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP3BottomLabelRed.width = (int)(paramsLinearLayoutLeftColumnThirdRowP3Bottom.width*0.5);
						paramsLinearLayoutLeftColumnThirdRowP3BottomLabelRed.height = paramsLinearLayoutLeftColumnThirdRowP3Bottom.height;
						linearLayoutLeftColumnThirdRowP3BottomLabelRed.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP3BottomLabelRed);
							TextView textViewLeftColumnThirdRowP3BottomLabelRed = (TextView) rootView.findViewById(R.id.textViewLeftColumnThirdRowP3BottomLabelRed);

				LinearLayout linearLayoutLeftColumnThirdRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP4);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP4 = linearLayoutLeftColumnThirdRowP4.getLayoutParams();
				paramsLinearLayoutLeftColumnThirdRowP4.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				paramsLinearLayoutLeftColumnThirdRowP4.height = paramsLinearLayoutLeftColumnThirdRow.height;
				linearLayoutLeftColumnThirdRowP4.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP4);

					LinearLayout linearLayoutLeftColumnThirdRowP4Top = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP4Top);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP4Top = linearLayoutLeftColumnThirdRowP4Top.getLayoutParams();
					paramsLinearLayoutLeftColumnThirdRowP4Top.width = paramsLinearLayoutLeftColumnThirdRowP4.width;
					paramsLinearLayoutLeftColumnThirdRowP4Top.height = (int)(paramsLinearLayoutLeftColumnThirdRowP4.height*0.5);
					linearLayoutLeftColumnThirdRowP4Top.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP4Top);

						LinearLayout linearLayoutLeftColumnThirdRowP4TopLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP4TopLabel);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP4TopLabel = linearLayoutLeftColumnThirdRowP4TopLabel.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP4TopLabel.width = (int)(paramsLinearLayoutLeftColumnThirdRowP4Top.width*0.75);
						paramsLinearLayoutLeftColumnThirdRowP4TopLabel.height = paramsLinearLayoutLeftColumnThirdRowP4Top.height;
						linearLayoutLeftColumnThirdRowP4TopLabel.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP4TopLabel);
							TextView textViewLeftColumnThirdRowP4TopLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnThirdRowP4TopLabel);

						linearLayoutLeftColumnThirdRowP4TopImage = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP4TopImage);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP4TopImage = linearLayoutLeftColumnThirdRowP4TopImage.getLayoutParams();
						paramsLinearLayoutLeftColumnThirdRowP4TopImage.width = paramsLinearLayoutLeftColumnThirdRowP4Top.height;
						paramsLinearLayoutLeftColumnThirdRowP4TopImage.height = paramsLinearLayoutLeftColumnThirdRowP4Top.height;
						linearLayoutLeftColumnThirdRowP4TopImage.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP4TopImage);
							imageViewLeftColumnThirdRowP4TopImage = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnThirdRowP4TopImage);
							if(cursor.getCount()>0) {
								linearLayoutLeftColumnThirdRowP4TopImage.removeAllViews();
								if(cursor.getString(cursor.getColumnIndex("Dynamic_AlarmParameters")).trim().equals("Yes")) {
									linearLayoutLeftColumnThirdRowP4TopImage.addView(imageViewLeftColumnThirdRowP4TopImage);
									flagAlarmDynamicalExist = true;
								}
								else
									flagAlarmDynamicalExist = false;
							}

			LinearLayout linearLayoutLeftColumnFourthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourthRow = linearLayoutLeftColumnFourthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnFourthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnFourthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnFourthRow.setLayoutParams(paramsLinearLayoutLeftColumnFourthRow);

				LinearLayout linearLayoutLeftColumnFourthRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourthRowP1);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourthRowP1 = linearLayoutLeftColumnFourthRowP1.getLayoutParams();
				paramsLinearLayoutLeftColumnFourthRowP1.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnFourthRowP1.setLayoutParams(paramsLinearLayoutLeftColumnFourthRowP1);
					TextView textViewtLeftColumnFourthRowP1Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnFourthRowP1Label);

				LinearLayout linearLayoutLeftColumnFourthRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourthRowP2);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourthRowP2 = linearLayoutLeftColumnFourthRowP2.getLayoutParams();
				paramsLinearLayoutLeftColumnFourthRowP2.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnFourthRowP2.setLayoutParams(paramsLinearLayoutLeftColumnFourthRowP2);

					LinearLayout linearLayoutLeftColumnFourthRowP2Left = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourthRowP2Left);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourthRowP2Left = linearLayoutLeftColumnFourthRowP2Left.getLayoutParams();
					paramsLinearLayoutLeftColumnFourthRowP2Left.width = paramsLinearLayoutLeftColumnFourthRowP2.width / 2 - 10;	// -10: Margins left and right
					linearLayoutLeftColumnFourthRowP2Left.setLayoutParams(paramsLinearLayoutLeftColumnFourthRowP2Left);
						editTextLeftColumnFourthRowP2LeftValue = (EditText) rootView.findViewById(R.id.editTextLeftColumnFourthRowP2LeftValue);
						editTextLeftColumnFourthRowP2LeftValue.setText(cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtIdle_AlarmParameters")).trim());

					LinearLayout linearLayoutLeftColumnFourthRowP2Right = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourthRowP2Right);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourthRowP2Right = linearLayoutLeftColumnFourthRowP2Right.getLayoutParams();
					paramsLinearLayoutLeftColumnFourthRowP2Right.width = paramsLinearLayoutLeftColumnFourthRowP2.width / 2 - 10;	// -10: Margins left and right
					linearLayoutLeftColumnFourthRowP2Right.setLayoutParams(paramsLinearLayoutLeftColumnFourthRowP2Right);
						editTextLeftColumnFourthRowP2RightValue = (EditText) rootView.findViewById(R.id.editTextLeftColumnFourthRowP2RightValue);
						editTextLeftColumnFourthRowP2RightValue.setText(cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtIdle_AlarmParameters")).trim());

				LinearLayout linearLayoutLeftColumnFourthRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourthRowP3);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourthRowP3 = linearLayoutLeftColumnFourthRowP3.getLayoutParams();
				paramsLinearLayoutLeftColumnFourthRowP3.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnFourthRowP3.setLayoutParams(paramsLinearLayoutLeftColumnFourthRowP3);

					LinearLayout linearLayoutLeftColumnFourthRowP3Left = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourthRowP3Left);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourthRowP3Left = linearLayoutLeftColumnFourthRowP3Left.getLayoutParams();
					paramsLinearLayoutLeftColumnFourthRowP3Left.width = paramsLinearLayoutLeftColumnFourthRowP3.width / 2 - 10;	// -10: Margins left and right
					linearLayoutLeftColumnFourthRowP3Left.setLayoutParams(paramsLinearLayoutLeftColumnFourthRowP3Left);
						editTextLeftColumnFourthRowP3LeftValue = (EditText) rootView.findViewById(R.id.editTextLeftColumnFourthRowP3LeftValue);
						editTextLeftColumnFourthRowP3LeftValue.setText(cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtIdle_AlarmParameters")).trim());

					LinearLayout linearLayoutLeftColumnFourthRowP3Right = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourthRowP3Right);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourthRowP3Right = linearLayoutLeftColumnFourthRowP3Right.getLayoutParams();
					paramsLinearLayoutLeftColumnFourthRowP3Right.width = paramsLinearLayoutLeftColumnFourthRowP3.width / 2 - 10;	// -10: Margins left and right
					linearLayoutLeftColumnFourthRowP3Right.setLayoutParams(paramsLinearLayoutLeftColumnFourthRowP3Right);
						editTextLeftColumnFourthRowP3RightValue = (EditText) rootView.findViewById(R.id.editTextLeftColumnFourthRowP3RightValue);
						editTextLeftColumnFourthRowP3RightValue.setText(cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtIdle_AlarmParameters")).trim());

			LinearLayout linearLayoutLeftColumnFifthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifthRow = linearLayoutLeftColumnFifthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnFifthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnFifthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnFifthRow.setLayoutParams(paramsLinearLayoutLeftColumnFifthRow);

				LinearLayout linearLayoutLeftColumnFifthRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifthRowP1);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifthRowP1 = linearLayoutLeftColumnFifthRowP1.getLayoutParams();
				paramsLinearLayoutLeftColumnFifthRowP1.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnFifthRowP1.setLayoutParams(paramsLinearLayoutLeftColumnFifthRowP1);
					TextView textViewtLeftColumnFifthRowP1Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnFifthRowP1Label);

				LinearLayout linearLayoutLeftColumnFifthRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifthRowP2);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifthRowP2 = linearLayoutLeftColumnFifthRowP2.getLayoutParams();
				paramsLinearLayoutLeftColumnFifthRowP2.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnFifthRowP2.setLayoutParams(paramsLinearLayoutLeftColumnFifthRowP2);

					LinearLayout linearLayoutLeftColumnFifthRowP2Left = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifthRowP2Left);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifthRowP2Left = linearLayoutLeftColumnFifthRowP2Left.getLayoutParams();
					paramsLinearLayoutLeftColumnFifthRowP2Left.width = paramsLinearLayoutLeftColumnFifthRowP2.width / 2 - 10;	// -10: Margins left and right
					linearLayoutLeftColumnFifthRowP2Left.setLayoutParams(paramsLinearLayoutLeftColumnFifthRowP2Left);
						editTextLeftColumnFifthRowP2LeftValue = (EditText) rootView.findViewById(R.id.editTextLeftColumnFifthRowP2LeftValue);
						editTextLeftColumnFifthRowP2LeftValue.setText(cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtWot_AlarmParameters")).trim());

					LinearLayout linearLayoutLeftColumnFifthRowP2Right = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifthRowP2Right);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifthRowP2Right = linearLayoutLeftColumnFifthRowP2Right.getLayoutParams();
					paramsLinearLayoutLeftColumnFifthRowP2Right.width = paramsLinearLayoutLeftColumnFifthRowP2.width / 2 - 10;	// -10: Margins left and right
					linearLayoutLeftColumnFifthRowP2Right.setLayoutParams(paramsLinearLayoutLeftColumnFifthRowP2Right);
						editTextLeftColumnFifthRowP2RightValue = (EditText) rootView.findViewById(R.id.editTextLeftColumnFifthRowP2RightValue);
						editTextLeftColumnFifthRowP2RightValue.setText(cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtWot_AlarmParameters")).trim());

				LinearLayout linearLayoutLeftColumnFifthRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifthRowP3);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifthRowP3 = linearLayoutLeftColumnFifthRowP3.getLayoutParams();
				paramsLinearLayoutLeftColumnFifthRowP3.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnFifthRowP3.setLayoutParams(paramsLinearLayoutLeftColumnFifthRowP3);

					LinearLayout linearLayoutLeftColumnFifthRowP3Left = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifthRowP3Left);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifthRowP3Left = linearLayoutLeftColumnFifthRowP3Left.getLayoutParams();
					paramsLinearLayoutLeftColumnFifthRowP3Left.width = paramsLinearLayoutLeftColumnFifthRowP3.width / 2 - 10;	// -10: Margins left and right
					linearLayoutLeftColumnFifthRowP3Left.setLayoutParams(paramsLinearLayoutLeftColumnFifthRowP3Left);
						editTextLeftColumnFifthRowP3LeftValue = (EditText) rootView.findViewById(R.id.editTextLeftColumnFifthRowP3LeftValue);
						editTextLeftColumnFifthRowP3LeftValue.setText(cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtWot_AlarmParameters")).trim());

					LinearLayout linearLayoutLeftColumnFifthRowP3Right = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifthRowP3Right);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifthRowP3Right = linearLayoutLeftColumnFifthRowP3Right.getLayoutParams();
					paramsLinearLayoutLeftColumnFifthRowP3Right.width = paramsLinearLayoutLeftColumnFifthRowP3.width / 2 - 10;	// -10: Margins left and right
					linearLayoutLeftColumnFifthRowP3Right.setLayoutParams(paramsLinearLayoutLeftColumnFifthRowP3Right);
						editTextLeftColumnFifthRowP3RightValue = (EditText) rootView.findViewById(R.id.editTextLeftColumnFifthRowP3RightValue);
						editTextLeftColumnFifthRowP3RightValue.setText(cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtWot_AlarmParameters")).trim());

			LinearLayout linearLayoutLeftColumnSixthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRow = linearLayoutLeftColumnSixthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnSixthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnSixthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN * 0.75);
			linearLayoutLeftColumnSixthRow.setLayoutParams(paramsLinearLayoutLeftColumnSixthRow);

				LinearLayout linearLayoutLeftColumnSixthRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP1);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP1 = linearLayoutLeftColumnSixthRowP1.getLayoutParams();
				paramsLinearLayoutLeftColumnSixthRowP1.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnSixthRowP1.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP1);
					TextView textViewtLeftColumnSixthRowP1Label = (TextView) rootView.findViewById(R.id.textViewtLeftColumnSixthRowP1Label);

				LinearLayout linearLayoutLeftColumnSixthRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP2);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP2 = linearLayoutLeftColumnSixthRowP2.getLayoutParams();
				paramsLinearLayoutLeftColumnSixthRowP2.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				paramsLinearLayoutLeftColumnSixthRowP2.height = paramsLinearLayoutLeftColumnSixthRow.height;
				linearLayoutLeftColumnSixthRowP2.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP2);

					LinearLayout linearLayoutLeftColumnSixthRowP2Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP2Label);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP2Label = linearLayoutLeftColumnSixthRowP2Label.getLayoutParams();
					paramsLinearLayoutLeftColumnSixthRowP2Label.width = (int)(paramsLinearLayoutLeftColumnSixthRowP2.width*0.75);
					paramsLinearLayoutLeftColumnSixthRowP2Label.height = paramsLinearLayoutLeftColumnSixthRowP2.height;
					linearLayoutLeftColumnSixthRowP2Label.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP2Label);
						TextView textViewLeftColumnSixthRowP2Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnSixthRowP2Label);

					linearLayoutLeftColumnSixthRowP2Image = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP2Image);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP2Image = linearLayoutLeftColumnSixthRowP2Image.getLayoutParams();
					paramsLinearLayoutLeftColumnSixthRowP2Image.width = paramsLinearLayoutLeftColumnSixthRowP2.height;
					paramsLinearLayoutLeftColumnSixthRowP2Image.height = paramsLinearLayoutLeftColumnSixthRowP2.height;
					linearLayoutLeftColumnSixthRowP2Image.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP2Image);
						imageViewLeftColumnSixthRowP2Image = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnSixthRowP2Image);
						if(cursor.getCount()>0) {
							cursor.moveToFirst();
							linearLayoutLeftColumnSixthRowP2Image.removeAllViews();
//System.out.println(cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters"))+" |  "+
	//cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters"))+" |  "+
	//cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtWot_AlarmParameters"))+" |  "+
	//cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtWot_AlarmParameters"))+" |  ");
							if((cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
								!cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters")).trim().equals("―"))||
							   (cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtWot_AlarmParameters")).trim().length()>0&&
								!cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtWot_AlarmParameters")).trim().equals("―"))) {
								linearLayoutLeftColumnSixthRowP2Image.addView(imageViewLeftColumnSixthRowP2Image);
								flagAlarmsShutdownLowerLimitsExist = true;
							}
							else
								flagAlarmsShutdownLowerLimitsExist = false;
						}

				LinearLayout linearLayoutLeftColumnSixthRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP3);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP3 = linearLayoutLeftColumnSixthRowP3.getLayoutParams();
				paramsLinearLayoutLeftColumnSixthRowP3.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				paramsLinearLayoutLeftColumnSixthRowP3.height = paramsLinearLayoutLeftColumnSixthRow.height;
				linearLayoutLeftColumnSixthRowP3.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP3);

					LinearLayout linearLayoutLeftColumnSixthRowP3Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP3Label);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP3Label = linearLayoutLeftColumnSixthRowP3Label.getLayoutParams();
					paramsLinearLayoutLeftColumnSixthRowP3Label.width = (int)(paramsLinearLayoutLeftColumnSixthRowP3.width*0.75);
					paramsLinearLayoutLeftColumnSixthRowP3Label.height = paramsLinearLayoutLeftColumnSixthRowP3.height;
					linearLayoutLeftColumnSixthRowP3Label.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP3Label);
						TextView textViewLeftColumnSixthRowP3Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnSixthRowP3Label);

					linearLayoutLeftColumnSixthRowP3Image = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP3Image);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP3Image = linearLayoutLeftColumnSixthRowP3Image.getLayoutParams();
					paramsLinearLayoutLeftColumnSixthRowP3Image.width = paramsLinearLayoutLeftColumnSixthRowP3.height;
					paramsLinearLayoutLeftColumnSixthRowP3Image.height = paramsLinearLayoutLeftColumnSixthRowP3.height;
					linearLayoutLeftColumnSixthRowP3Image.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP3Image);
						imageViewLeftColumnSixthRowP3Image = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnSixthRowP3Image);
						if(cursor.getCount()>0) {
							cursor.moveToFirst();
							linearLayoutLeftColumnSixthRowP3Image.removeAllViews();
							if((cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
								!cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtIdle_AlarmParameters")).trim().equals("―"))||
							   (cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtWot_AlarmParameters")).trim().length()>0&&
								!cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtWot_AlarmParameters")).trim().equals("―"))) {
								linearLayoutLeftColumnSixthRowP3Image.addView(imageViewLeftColumnSixthRowP3Image);
								flagAlarmsShutdownUpperLimitsExist = true;
							}
							else
								flagAlarmsShutdownUpperLimitsExist = false;
						}
				LinearLayout linearLayoutLeftColumnSixthRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP4);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP4 = linearLayoutLeftColumnSixthRowP4.getLayoutParams();
				paramsLinearLayoutLeftColumnSixthRowP4.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				paramsLinearLayoutLeftColumnSixthRowP4.height = paramsLinearLayoutLeftColumnSixthRow.height;
				linearLayoutLeftColumnSixthRowP4.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP4);

					LinearLayout linearLayoutLeftColumnSixthRowP4Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP4Label);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP4Label = linearLayoutLeftColumnSixthRowP4Label.getLayoutParams();
					paramsLinearLayoutLeftColumnSixthRowP4Label.width = (int)(paramsLinearLayoutLeftColumnSixthRowP4.width*0.75);
					paramsLinearLayoutLeftColumnSixthRowP4Label.height = paramsLinearLayoutLeftColumnSixthRowP4.height;
					linearLayoutLeftColumnSixthRowP4Label.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP4Label);
					TextView textViewLeftColumnSixthRowP4Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnSixthRowP4Label);

					linearLayoutLeftColumnSixthRowP4Image = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixthRowP4Image);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixthRowP4Image = linearLayoutLeftColumnSixthRowP4Image.getLayoutParams();
					paramsLinearLayoutLeftColumnSixthRowP4Image.width = paramsLinearLayoutLeftColumnSixthRowP4.height;
					paramsLinearLayoutLeftColumnSixthRowP4Image.height = paramsLinearLayoutLeftColumnSixthRowP4.height;
					linearLayoutLeftColumnSixthRowP4Image.setLayoutParams(paramsLinearLayoutLeftColumnSixthRowP4Image);
						imageViewLeftColumnSixthRowP4Image = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnSixthRowP4Image);
						if(cursor.getCount()>0) {
							linearLayoutLeftColumnSixthRowP4Image.removeAllViews();
							if(cursor.getString(cursor.getColumnIndex("Dynamic_AlarmParameters")).trim().equals("Yes")) {
								linearLayoutLeftColumnSixthRowP4Image.addView(imageViewLeftColumnSixthRowP4Image);
								flagAlarmDynamicalExist = true;
							}
							else
								flagAlarmDynamicalExist = false;
						}

			LinearLayout linearLayoutLeftColumnSeventhRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventhRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventhRow = linearLayoutLeftColumnSeventhRow.getLayoutParams();
			paramsLinearLayoutLeftColumnSeventhRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnSeventhRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnSeventhRow.setLayoutParams(paramsLinearLayoutLeftColumnSeventhRow);

				LinearLayout linearLayoutLeftColumnSeventhRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventhRowP1);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventhRowP1 = linearLayoutLeftColumnSeventhRowP1.getLayoutParams();
				paramsLinearLayoutLeftColumnSeventhRowP1.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnSeventhRowP1.setLayoutParams(paramsLinearLayoutLeftColumnSeventhRowP1);
					TextView textViewtLeftColumnSeventhRowP1Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnSeventhRowP1Label);

				LinearLayout linearLayoutLeftColumnSeventhRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventhRowP2);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventhRowP2 = linearLayoutLeftColumnSeventhRowP2.getLayoutParams();
				paramsLinearLayoutLeftColumnSeventhRowP2.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnSeventhRowP2.setLayoutParams(paramsLinearLayoutLeftColumnSeventhRowP2);

					LinearLayout linearLayoutLeftColumnSeventhRowP2Inside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventhRowP2Inside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventhRowP2Iside = linearLayoutLeftColumnSeventhRowP2Inside.getLayoutParams();
					paramsLinearLayoutLeftColumnSeventhRowP2Iside.width = paramsLinearLayoutLeftColumnSeventhRowP2.width / 2;	// -10: Margins left and right
					linearLayoutLeftColumnSeventhRowP2Inside.setLayoutParams(paramsLinearLayoutLeftColumnSeventhRowP2Iside);
						textViewLeftColumnSeventhRowP2Value = (TextView) rootView.findViewById(R.id.textViewLeftColumnSeventhRowP2Value);
						textViewLeftColumnSeventhRowP2Value.setText(cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters")).trim());

				LinearLayout linearLayoutLeftColumnSeventhRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventhRowP3);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventhRowP3 = linearLayoutLeftColumnSeventhRowP3.getLayoutParams();
				paramsLinearLayoutLeftColumnSeventhRowP3.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnSeventhRowP3.setLayoutParams(paramsLinearLayoutLeftColumnSeventhRowP3);

					LinearLayout linearLayoutLeftColumnSeventhRowP3Inside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventhRowP3Inside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventhRowP3Iside = linearLayoutLeftColumnSeventhRowP3Inside.getLayoutParams();
					paramsLinearLayoutLeftColumnSeventhRowP3Iside.width = paramsLinearLayoutLeftColumnSeventhRowP3.width / 2;	// -10: Margins left and right
					linearLayoutLeftColumnSeventhRowP3Inside.setLayoutParams(paramsLinearLayoutLeftColumnSeventhRowP3Iside);
						textViewLeftColumnSeventhRowP3Value = (TextView) rootView.findViewById(R.id.textViewLeftColumnSeventhRowP3Value);
						textViewLeftColumnSeventhRowP3Value.setText(cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtIdle_AlarmParameters")).trim());

			LinearLayout linearLayoutLeftColumnEighthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighthRow = linearLayoutLeftColumnEighthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnEighthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnEighthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnEighthRow.setLayoutParams(paramsLinearLayoutLeftColumnEighthRow);

				LinearLayout linearLayoutLeftColumnEighthRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighthRowP1);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighthRowP1 = linearLayoutLeftColumnEighthRowP1.getLayoutParams();
				paramsLinearLayoutLeftColumnEighthRowP1.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnEighthRowP1.setLayoutParams(paramsLinearLayoutLeftColumnEighthRowP1);
					TextView textViewtLeftColumnEighthRowP1Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnEighthRowP1Label);

				LinearLayout linearLayoutLeftColumnEighthRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighthRowP2);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighthRowP2 = linearLayoutLeftColumnEighthRowP2.getLayoutParams();
				paramsLinearLayoutLeftColumnEighthRowP2.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnEighthRowP2.setLayoutParams(paramsLinearLayoutLeftColumnEighthRowP2);

					LinearLayout linearLayoutLeftColumnEighthRowP2Inside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighthRowP2Inside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighthRowP2Iside = linearLayoutLeftColumnEighthRowP2Inside.getLayoutParams();
					paramsLinearLayoutLeftColumnEighthRowP2Iside.width = paramsLinearLayoutLeftColumnEighthRowP2.width / 2;	// -10: Margins left and right
					linearLayoutLeftColumnEighthRowP2Inside.setLayoutParams(paramsLinearLayoutLeftColumnEighthRowP2Iside);
						textViewLeftColumnEighthRowP2Value = (TextView) rootView.findViewById(R.id.textViewLeftColumnEighthRowP2Value);
						textViewLeftColumnEighthRowP2Value.setText(cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtWot_AlarmParameters")).trim());

				LinearLayout linearLayoutLeftColumnEighthRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighthRowP3);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighthRowP3 = linearLayoutLeftColumnEighthRowP3.getLayoutParams();
				paramsLinearLayoutLeftColumnEighthRowP3.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnEighthRowP3.setLayoutParams(paramsLinearLayoutLeftColumnEighthRowP3);

					LinearLayout linearLayoutLeftColumnEighthRowP3Inside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighthRowP3Inside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighthRowP3Iside = linearLayoutLeftColumnEighthRowP3Inside.getLayoutParams();
					paramsLinearLayoutLeftColumnEighthRowP3Iside.width = paramsLinearLayoutLeftColumnEighthRowP3.width / 2;	// -10: Margins left and right
					linearLayoutLeftColumnEighthRowP3Inside.setLayoutParams(paramsLinearLayoutLeftColumnEighthRowP3Iside);
						textViewLeftColumnEighthRowP3Value = (TextView) rootView.findViewById(R.id.textViewLeftColumnEighthRowP3Value);
						textViewLeftColumnEighthRowP3Value.setText(cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtWot_AlarmParameters")).trim());

			LinearLayout linearLayoutLeftColumnNinethRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRow = linearLayoutLeftColumnNinethRow.getLayoutParams();
			paramsLinearLayoutLeftColumnNinethRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnNinethRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN * 0.75);
			linearLayoutLeftColumnNinethRow.setLayoutParams(paramsLinearLayoutLeftColumnNinethRow);

				LinearLayout linearLayoutLeftColumnNinethRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP1);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP1 = linearLayoutLeftColumnNinethRowP1.getLayoutParams();
				paramsLinearLayoutLeftColumnNinethRowP1.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnNinethRowP1.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP1);
					TextView textViewtLeftColumnNinethRowP1Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnNinethRowP1Label);

				LinearLayout linearLayoutLeftColumnNinethRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP2);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP2 = linearLayoutLeftColumnNinethRowP2.getLayoutParams();
				paramsLinearLayoutLeftColumnNinethRowP2.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				paramsLinearLayoutLeftColumnNinethRowP2.height = paramsLinearLayoutLeftColumnNinethRow.height;
				linearLayoutLeftColumnNinethRowP2.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP2);

					LinearLayout linearLayoutLeftColumnNinethRowP2Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP2Label);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP2Label = linearLayoutLeftColumnNinethRowP2Label.getLayoutParams();
					paramsLinearLayoutLeftColumnNinethRowP2Label.width = (int)(paramsLinearLayoutLeftColumnNinethRowP2.width*0.75);
					paramsLinearLayoutLeftColumnNinethRowP2Label.height = paramsLinearLayoutLeftColumnNinethRowP2.height;
					linearLayoutLeftColumnNinethRowP2Label.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP2Label);
						TextView textViewLeftColumnNinethRowP2Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnNinethRowP2Label);

					LinearLayout linearLayoutLeftColumnNinethRowP2Image = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP2Image);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP2Image = linearLayoutLeftColumnNinethRowP2Image.getLayoutParams();
					paramsLinearLayoutLeftColumnNinethRowP2Image.width = paramsLinearLayoutLeftColumnNinethRowP2.height;
					paramsLinearLayoutLeftColumnNinethRowP2Image.height = paramsLinearLayoutLeftColumnNinethRowP2.height;
					linearLayoutLeftColumnNinethRowP2Image.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP2Image);
						ImageView imageViewLeftColumnNinethRowP2Image = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnNinethRowP2Image);
						linearLayoutLeftColumnNinethRowP2Image.removeAllViews();

				LinearLayout linearLayoutLeftColumnNinethRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP3);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP3 = linearLayoutLeftColumnNinethRowP3.getLayoutParams();
				paramsLinearLayoutLeftColumnNinethRowP3.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				paramsLinearLayoutLeftColumnNinethRowP3.height = paramsLinearLayoutLeftColumnNinethRow.height;
				linearLayoutLeftColumnNinethRowP3.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP3);

					LinearLayout linearLayoutLeftColumnNinethRowP3Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP3Label);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP3Label = linearLayoutLeftColumnNinethRowP3Label.getLayoutParams();
					paramsLinearLayoutLeftColumnNinethRowP3Label.width = (int)(paramsLinearLayoutLeftColumnNinethRowP3.width*0.75);
					paramsLinearLayoutLeftColumnNinethRowP3Label.height = paramsLinearLayoutLeftColumnNinethRowP3.height;
					linearLayoutLeftColumnNinethRowP3Label.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP3Label);
						TextView textViewLeftColumnNinethRowP3Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnNinethRowP3Label);

					LinearLayout linearLayoutLeftColumnNinethRowP3Image = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP3Image);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP3Image = linearLayoutLeftColumnNinethRowP3Image.getLayoutParams();
					paramsLinearLayoutLeftColumnNinethRowP3Image.width = paramsLinearLayoutLeftColumnNinethRowP3.height;
					paramsLinearLayoutLeftColumnNinethRowP3Image.height = paramsLinearLayoutLeftColumnNinethRowP3.height;
					linearLayoutLeftColumnNinethRowP3Image.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP3Image);
						ImageView imageViewLeftColumnNinethRowP3Image = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnNinethRowP3Image);
						linearLayoutLeftColumnNinethRowP3Image.removeAllViews();

				LinearLayout linearLayoutLeftColumnNinethRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP4);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP4 = linearLayoutLeftColumnNinethRowP4.getLayoutParams();
				paramsLinearLayoutLeftColumnNinethRowP4.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				paramsLinearLayoutLeftColumnNinethRowP4.height = paramsLinearLayoutLeftColumnNinethRow.height;
				linearLayoutLeftColumnNinethRowP4.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP4);

					LinearLayout linearLayoutLeftColumnNinethRowP4Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP4Label);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP4Label = linearLayoutLeftColumnNinethRowP4Label.getLayoutParams();
					paramsLinearLayoutLeftColumnNinethRowP4Label.width = (int)(paramsLinearLayoutLeftColumnNinethRowP4.width*0.6);
					paramsLinearLayoutLeftColumnNinethRowP4Label.height = paramsLinearLayoutLeftColumnNinethRowP4.height;
					linearLayoutLeftColumnNinethRowP4Label.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP4Label);
						TextView textViewLeftColumnNinethRowP4Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnNinethRowP4Label);

					LinearLayout linearLayoutLeftColumnNinethRowP4Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNinethRowP4Value);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNinethRowP4Value = linearLayoutLeftColumnNinethRowP4Value.getLayoutParams();
					paramsLinearLayoutLeftColumnNinethRowP4Value.width = (int)(paramsLinearLayoutLeftColumnNinethRowP4.width*0.4);
					paramsLinearLayoutLeftColumnNinethRowP4Value.height = paramsLinearLayoutLeftColumnNinethRowP4.height;
					linearLayoutLeftColumnNinethRowP4Value.setLayoutParams(paramsLinearLayoutLeftColumnNinethRowP4Value);
						TextView textViewLeftColumnNinethRowP4Value = (TextView) rootView.findViewById(R.id.textViewLeftColumnNinethRowP4Value);

			LinearLayout linearLayoutLeftColumnTenthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTenthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTenthRow = linearLayoutLeftColumnTenthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnTenthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnTenthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnTenthRow.setLayoutParams(paramsLinearLayoutLeftColumnTenthRow);

			LinearLayout linearLayoutLeftColumnEleventhRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEleventhRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEleventhRow = linearLayoutLeftColumnEleventhRow.getLayoutParams();
			paramsLinearLayoutLeftColumnEleventhRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnEleventhRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnEleventhRow.setLayoutParams(paramsLinearLayoutLeftColumnEleventhRow);

			LinearLayout linearLayoutLeftColumnTwelfthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRow = linearLayoutLeftColumnTwelfthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnTwelfthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnTwelfthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnTwelfthRow.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRow);

				LinearLayout linearLayoutLeftColumnTwelfthRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP1);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP1 = linearLayoutLeftColumnTwelfthRowP1.getLayoutParams();
				paramsLinearLayoutLeftColumnTwelfthRowP1.width = (int)(paramsLinearLayoutLeftColumn.width*0.2);
				linearLayoutLeftColumnTwelfthRowP1.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP1);
					TextView textViewtLeftColumnTwelfthRowP1Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwelfthRowP1Label);

				LinearLayout linearLayoutLeftColumnTwelfthRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP2);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP2 = linearLayoutLeftColumnTwelfthRowP2.getLayoutParams();
				paramsLinearLayoutLeftColumnTwelfthRowP2.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnTwelfthRowP2.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP2);

					LinearLayout linearLayoutLeftColumnTwelfthRowP2Left = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP2Left);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP2Left = linearLayoutLeftColumnTwelfthRowP2Left.getLayoutParams();
					paramsLinearLayoutLeftColumnTwelfthRowP2Left.width = (int)(paramsLinearLayoutLeftColumnTwelfthRowP2.width*0.4);
					linearLayoutLeftColumnTwelfthRowP2Left.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP2Left);
						TextView textViewLeftColumnTwelfthRowP2Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwelfthRowP2Label);

					LinearLayout linearLayoutLeftColumnTwelfthRowP2Right = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP2Right);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP2Right = linearLayoutLeftColumnTwelfthRowP2Right.getLayoutParams();
					paramsLinearLayoutLeftColumnTwelfthRowP2Right.width = (int)(paramsLinearLayoutLeftColumnTwelfthRowP2.width*0.6);
					linearLayoutLeftColumnTwelfthRowP2Right.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP2Right);
						TextView textViewLeftColumnTwelfthRowP2Value = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwelfthRowP2Value);

				LinearLayout linearLayoutLeftColumnTwelfthRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP3);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP3 = linearLayoutLeftColumnTwelfthRowP3.getLayoutParams();
				paramsLinearLayoutLeftColumnTwelfthRowP3.width = (int)(paramsLinearLayoutLeftColumn.width*0.25);
				linearLayoutLeftColumnTwelfthRowP3.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP3);

					LinearLayout linearLayoutLeftColumnTwelfthRowP3Left = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP3Left);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP3Left = linearLayoutLeftColumnTwelfthRowP3Left.getLayoutParams();
					paramsLinearLayoutLeftColumnTwelfthRowP3Left.width = (int)(paramsLinearLayoutLeftColumnTwelfthRowP3.width*0.4);
					linearLayoutLeftColumnTwelfthRowP3Left.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP3Left);
						TextView textViewLeftColumnTwelfthRowP3Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwelfthRowP3Label);

					LinearLayout linearLayoutLeftColumnTwelfthRowP3Right = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP3Right);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP3Right = linearLayoutLeftColumnTwelfthRowP3Right.getLayoutParams();
					paramsLinearLayoutLeftColumnTwelfthRowP3Right.width = (int)(paramsLinearLayoutLeftColumnTwelfthRowP3.width*0.6);
					linearLayoutLeftColumnTwelfthRowP3Right.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP3Right);
						TextView textViewLeftColumnTwelfthRowP3Value = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwelfthRowP3Value);

				LinearLayout linearLayoutLeftColumnTwelfthRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP4);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP4 = linearLayoutLeftColumnTwelfthRowP4.getLayoutParams();
				paramsLinearLayoutLeftColumnTwelfthRowP4.width = (int)(paramsLinearLayoutLeftColumn.width*0.15);
				linearLayoutLeftColumnTwelfthRowP4.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP4);

					LinearLayout linearLayoutLeftColumnTwelfthRowP4Left = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP4Left);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP4Left = linearLayoutLeftColumnTwelfthRowP4Left.getLayoutParams();
					paramsLinearLayoutLeftColumnTwelfthRowP4Left.width = (int)(paramsLinearLayoutLeftColumnTwelfthRowP4.width*0.6);
					linearLayoutLeftColumnTwelfthRowP4Left.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP4Left);
						TextView textViewLeftColumnTwelfthRowP4Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwelfthRowP4Label);

					LinearLayout linearLayoutLeftColumnTwelfthRowP4Right = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP4Right);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP4Right = linearLayoutLeftColumnTwelfthRowP4Right.getLayoutParams();
					paramsLinearLayoutLeftColumnTwelfthRowP4Right.width = (int)(paramsLinearLayoutLeftColumnTwelfthRowP4.width*0.4);
					linearLayoutLeftColumnTwelfthRowP4Right.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP4Right);
						TextView textViewLeftColumnTwelfthRowP4Value = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwelfthRowP4Value);

				LinearLayout linearLayoutLeftColumnTwelfthRowP5 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP5);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP5 = linearLayoutLeftColumnTwelfthRowP5.getLayoutParams();
				paramsLinearLayoutLeftColumnTwelfthRowP5.width = (int)(paramsLinearLayoutLeftColumn.width*0.15);
				linearLayoutLeftColumnTwelfthRowP5.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP5);

					LinearLayout linearLayoutLeftColumnTwelfthRowP5Left = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP5Left);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP5Left = linearLayoutLeftColumnTwelfthRowP5Left.getLayoutParams();
					paramsLinearLayoutLeftColumnTwelfthRowP5Left.width = (int)(paramsLinearLayoutLeftColumnTwelfthRowP5.width*0.6);
					linearLayoutLeftColumnTwelfthRowP5Left.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP5Left);
						TextView textViewLeftColumnTwelfthRowP5Label = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwelfthRowP5Label);

					LinearLayout linearLayoutLeftColumnTwelfthRowP5Right = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwelfthRowP5Right);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwelfthRowP5Right = linearLayoutLeftColumnTwelfthRowP5Right.getLayoutParams();
					paramsLinearLayoutLeftColumnTwelfthRowP5Right.width = (int)(paramsLinearLayoutLeftColumnTwelfthRowP5.width*0.4);
					linearLayoutLeftColumnTwelfthRowP5Right.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRowP5Right);
						TextView textViewLeftColumnTwelfthRowP5Value = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwelfthRowP5Value);

			LinearLayout linearLayoutLeftColumnThirteenthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirteenthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirteenthRow = linearLayoutLeftColumnThirteenthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnThirteenthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnThirteenthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnThirteenthRow.setLayoutParams(paramsLinearLayoutLeftColumnThirteenthRow);

				LinearLayout linearLayoutLeftColumnThirteenthRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirteenthRowLeft);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirteenthRowLeft = linearLayoutLeftColumnThirteenthRowLeft.getLayoutParams();
				paramsLinearLayoutLeftColumnThirteenthRowLeft.width = (int)(paramsLinearLayoutLeftColumnThirteenthRow.width*0.6);
				paramsLinearLayoutLeftColumnThirteenthRowLeft.height = paramsLinearLayoutLeftColumnThirteenthRow.height;
				linearLayoutLeftColumnThirteenthRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnThirteenthRowLeft);

					LinearLayout linearLayoutLeftColumnThirteenthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirteenthRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirteenthRowLeftLabel = linearLayoutLeftColumnThirteenthRowLeftLabel.getLayoutParams();
					paramsLinearLayoutLeftColumnThirteenthRowLeftLabel.width = (int)(paramsLinearLayoutLeftColumnThirteenthRowLeft.width*0.5);
					linearLayoutLeftColumnThirteenthRowLeftLabel.setLayoutParams(paramsLinearLayoutLeftColumnThirteenthRowLeftLabel);
						TextView textViewLeftColumnThirteenthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnThirteenthRowLeftLabel);

					LinearLayout linearLayoutLeftColumnThirteenthRowLeftButton = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirteenthRowLeftButton);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirteenthRowLeftButton = linearLayoutLeftColumnThirteenthRowLeftButton.getLayoutParams();
					paramsLinearLayoutLeftColumnThirteenthRowLeftButton.width = (int)(paramsLinearLayoutLeftColumnThirteenthRowLeft.width*0.3);
					linearLayoutLeftColumnThirteenthRowLeftButton.setLayoutParams(paramsLinearLayoutLeftColumnThirteenthRowLeftButton);
						Button buttonLeftColumnThirteenthRowLeftButton = (Button) rootView.findViewById(R.id.buttonLeftColumnThirteenthRowLeftButton);

					LinearLayout linearLayoutLeftColumnThirteenthRowLeftValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirteenthRowLeftValue);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirteenthRowLeftValue = linearLayoutLeftColumnThirteenthRowLeftValue.getLayoutParams();
					paramsLinearLayoutLeftColumnThirteenthRowLeftValue.width = (int)(paramsLinearLayoutLeftColumnThirteenthRowLeft.width*0.2) - 20;
					linearLayoutLeftColumnThirteenthRowLeftValue.setLayoutParams(paramsLinearLayoutLeftColumnThirteenthRowLeftValue);
						TextView textViewLeftColumnThirteenthRowLeftValue = (TextView) rootView.findViewById(R.id.textViewLeftColumnThirteenthRowLeftValue);

				LinearLayout linearLayoutLeftColumnThirteenthRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirteenthRowRight);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirteenthRowRight = linearLayoutLeftColumnThirteenthRowRight.getLayoutParams();
				paramsLinearLayoutLeftColumnThirteenthRowRight.width = (int)(paramsLinearLayoutLeftColumnThirteenthRow.width*0.4);
				paramsLinearLayoutLeftColumnThirteenthRowRight.height = paramsLinearLayoutLeftColumnThirteenthRow.height;
				linearLayoutLeftColumnThirteenthRowRight.setLayoutParams(paramsLinearLayoutLeftColumnThirteenthRowRight);

					LinearLayout linearLayoutLeftColumnThirteenthRowRightButton = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirteenthRowRightButton);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirteenthRowRightButton = linearLayoutLeftColumnThirteenthRowRightButton.getLayoutParams();
					paramsLinearLayoutLeftColumnThirteenthRowRightButton.width = (int)(paramsLinearLayoutLeftColumnThirteenthRowRight.width*0.5);
					linearLayoutLeftColumnThirteenthRowRightButton.setLayoutParams(paramsLinearLayoutLeftColumnThirteenthRowRightButton);
						Button buttonLeftColumnThirteenthRowRightButton = (Button) rootView.findViewById(R.id.buttonLeftColumnThirteenthRowRightButton);

			LinearLayout linearLayoutLeftColumnFourteenthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourteenthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourteenthRow = linearLayoutLeftColumnFourteenthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnFourteenthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnFourteenthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnFourteenthRow.setLayoutParams(paramsLinearLayoutLeftColumnFourteenthRow);

				LinearLayout linearLayoutLeftColumnFourteenthRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourteenthRowLeft);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourteenthRowLeft = linearLayoutLeftColumnFourteenthRowLeft.getLayoutParams();
				paramsLinearLayoutLeftColumnFourteenthRowLeft.width = (int)(paramsLinearLayoutLeftColumnFourteenthRow.width*0.58);
				paramsLinearLayoutLeftColumnFourteenthRowLeft.height = paramsLinearLayoutLeftColumnFourteenthRow.height;
				linearLayoutLeftColumnFourteenthRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnFourteenthRowLeft);
					TextView textViewLeftColumnFourteenthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnFourteenthRowLeftLabel);

				LinearLayout linearLayoutLeftColumnFourteenthRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFourteenthRowRight);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFourteenthRowRight = linearLayoutLeftColumnFourteenthRowRight.getLayoutParams();
				paramsLinearLayoutLeftColumnFourteenthRowRight.width = (int)(paramsLinearLayoutLeftColumnFourteenthRow.width*0.42);
				paramsLinearLayoutLeftColumnFourteenthRowRight.height = paramsLinearLayoutLeftColumnFourteenthRow.height;
				linearLayoutLeftColumnFourteenthRowRight.setLayoutParams(paramsLinearLayoutLeftColumnFourteenthRowRight);
					TextView textViewLeftColumnFourteenthRowRightLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnFourteenthRowRightLabel);

			LinearLayout linearLayoutLeftColumnFifteenthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifteenthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifteenthRow = linearLayoutLeftColumnFifteenthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnFifteenthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnFifteenthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnFifteenthRow.setLayoutParams(paramsLinearLayoutLeftColumnFifteenthRow);

				LinearLayout linearLayoutLeftColumnFifteenthRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifteenthRowLeft);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifteenthRowLeft = linearLayoutLeftColumnFifteenthRowLeft.getLayoutParams();
				paramsLinearLayoutLeftColumnFifteenthRowLeft.width = (int)(paramsLinearLayoutLeftColumnFifteenthRow.width*0.58);
				paramsLinearLayoutLeftColumnFifteenthRowLeft.height = paramsLinearLayoutLeftColumnFifteenthRow.height;
				linearLayoutLeftColumnFifteenthRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnFifteenthRowLeft);

					LinearLayout linearLayoutLeftColumnFifteenthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifteenthRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifteenthRowLeftLabel = linearLayoutLeftColumnFifteenthRowLeftLabel.getLayoutParams();
					paramsLinearLayoutLeftColumnFifteenthRowLeftLabel.width = (int)(paramsLinearLayoutLeftColumnFifteenthRowLeft.width*0.8);
					paramsLinearLayoutLeftColumnFifteenthRowLeftLabel.height = paramsLinearLayoutLeftColumnFifteenthRowLeft.height;
					linearLayoutLeftColumnFifteenthRowLeftLabel.setLayoutParams(paramsLinearLayoutLeftColumnFifteenthRowLeftLabel);
						TextView textViewLeftColumnFifteenthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnFifteenthRowLeftLabel);

					LinearLayout linearLayoutLeftColumnFifteenthRowLeftCheck = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifteenthRowLeftCheck);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifteenthRowLeftCheck = linearLayoutLeftColumnFifteenthRowLeftCheck.getLayoutParams();
					paramsLinearLayoutLeftColumnFifteenthRowLeftCheck.width = (int)(paramsLinearLayoutLeftColumnFifteenthRowLeft.width*0.2);
					paramsLinearLayoutLeftColumnFifteenthRowLeftCheck.height = paramsLinearLayoutLeftColumnFifteenthRowLeft.height;
					linearLayoutLeftColumnFifteenthRowLeftCheck.setLayoutParams(paramsLinearLayoutLeftColumnFifteenthRowLeftCheck);

						LinearLayout linearLayoutLeftColumnFifteenthRowLeftCheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifteenthRowLeftCheckInside);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifteenthRowLeftCheckInside = linearLayoutLeftColumnFifteenthRowLeftCheckInside.getLayoutParams();
						paramsLinearLayoutLeftColumnFifteenthRowLeftCheckInside.width = (int)(paramsLinearLayoutLeftColumnFifteenthRow.height * 0.75);
						paramsLinearLayoutLeftColumnFifteenthRowLeftCheckInside.height = (int)(paramsLinearLayoutLeftColumnFifteenthRow.height * 0.75);
						linearLayoutLeftColumnFifteenthRowLeftCheckInside.setLayoutParams(paramsLinearLayoutLeftColumnFifteenthRowLeftCheckInside);
							ImageView imageViewLeftColumnFifteenthRowLeftCheckInside = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnFifteenthRowLeftCheck);

				LinearLayout linearLayoutLeftColumnFifteenthRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifteenthRowRight);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifteenthRowRight = linearLayoutLeftColumnFifteenthRowRight.getLayoutParams();
				paramsLinearLayoutLeftColumnFifteenthRowRight.width = (int)(paramsLinearLayoutLeftColumnFifteenthRow.width*0.42);
				paramsLinearLayoutLeftColumnFifteenthRowRight.height = paramsLinearLayoutLeftColumnFifteenthRow.height;
				linearLayoutLeftColumnFifteenthRowRight.setLayoutParams(paramsLinearLayoutLeftColumnFifteenthRowRight);

					LinearLayout linearLayoutLeftColumnFifteenthRowRightValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFifteenthRowRightValueInside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFifteenthRowRightValueInside = linearLayoutLeftColumnFifteenthRowRightValueInside.getLayoutParams();
					paramsLinearLayoutLeftColumnFifteenthRowRightValueInside.width = (int)(paramsLinearLayoutLeftColumnFifteenthRowRight.width*0.6);
					paramsLinearLayoutLeftColumnFifteenthRowRightValueInside.height = (int)(paramsLinearLayoutLeftColumnFifteenthRowRight.height*0.95);
					linearLayoutLeftColumnFifteenthRowRightValueInside.setLayoutParams(paramsLinearLayoutLeftColumnFifteenthRowRightValueInside);
						TextView textViewLeftColumnFifteenthRowRightValue = (TextView) rootView.findViewById(R.id.textViewLeftColumnFifteenthRowRightValue);

			LinearLayout linearLayoutLeftColumnSixteenthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixteenthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixteenthRow = linearLayoutLeftColumnSixteenthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnSixteenthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnSixteenthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnSixteenthRow.setLayoutParams(paramsLinearLayoutLeftColumnSixteenthRow);

				LinearLayout linearLayoutLeftColumnSixteenthRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixteenthRowLeft);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixteenthRowLeft = linearLayoutLeftColumnSixteenthRowLeft.getLayoutParams();
				paramsLinearLayoutLeftColumnSixteenthRowLeft.width = (int)(paramsLinearLayoutLeftColumnSixteenthRow.width*0.58);
				paramsLinearLayoutLeftColumnSixteenthRowLeft.height = paramsLinearLayoutLeftColumnSixteenthRow.height;
				linearLayoutLeftColumnSixteenthRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnSixteenthRowLeft);

					LinearLayout linearLayoutLeftColumnSixteenthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixteenthRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixteenthRowLeftLabel = linearLayoutLeftColumnSixteenthRowLeftLabel.getLayoutParams();
					paramsLinearLayoutLeftColumnSixteenthRowLeftLabel.width = (int)(paramsLinearLayoutLeftColumnSixteenthRowLeft.width*0.8);
					paramsLinearLayoutLeftColumnSixteenthRowLeftLabel.height = paramsLinearLayoutLeftColumnSixteenthRowLeft.height;
					linearLayoutLeftColumnSixteenthRowLeftLabel.setLayoutParams(paramsLinearLayoutLeftColumnSixteenthRowLeftLabel);
						TextView textViewLeftColumnSixteenthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnSixteenthRowLeftLabel);

					LinearLayout linearLayoutLeftColumnSixteenthRowLeftCheck = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixteenthRowLeftCheck);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixteenthRowLeftCheck = linearLayoutLeftColumnSixteenthRowLeftCheck.getLayoutParams();
					paramsLinearLayoutLeftColumnSixteenthRowLeftCheck.width = (int)(paramsLinearLayoutLeftColumnSixteenthRowLeft.width*0.2);
					paramsLinearLayoutLeftColumnSixteenthRowLeftCheck.height = paramsLinearLayoutLeftColumnSixteenthRowLeft.height;
					linearLayoutLeftColumnSixteenthRowLeftCheck.setLayoutParams(paramsLinearLayoutLeftColumnSixteenthRowLeftCheck);

						LinearLayout linearLayoutLeftColumnSixteenthRowLeftCheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixteenthRowLeftCheckInside);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixteenthRowLeftCheckInside = linearLayoutLeftColumnSixteenthRowLeftCheckInside.getLayoutParams();
						paramsLinearLayoutLeftColumnSixteenthRowLeftCheckInside.width = (int)(paramsLinearLayoutLeftColumnSixteenthRow.height * 0.75);
						paramsLinearLayoutLeftColumnSixteenthRowLeftCheckInside.height = (int)(paramsLinearLayoutLeftColumnSixteenthRow.height * 0.75);
						linearLayoutLeftColumnSixteenthRowLeftCheckInside.setLayoutParams(paramsLinearLayoutLeftColumnSixteenthRowLeftCheckInside);
							ImageView imageViewLeftColumnSixteenthRowLeftCheckInside = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnSixteenthRowLeftCheck);

				LinearLayout linearLayoutLeftColumnSixteenthRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixteenthRowRight);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixteenthRowRight = linearLayoutLeftColumnSixteenthRowRight.getLayoutParams();
				paramsLinearLayoutLeftColumnSixteenthRowRight.width = (int)(paramsLinearLayoutLeftColumnSixteenthRow.width*0.42);
				paramsLinearLayoutLeftColumnSixteenthRowRight.height = paramsLinearLayoutLeftColumnSixteenthRow.height;
				linearLayoutLeftColumnSixteenthRowRight.setLayoutParams(paramsLinearLayoutLeftColumnSixteenthRowRight);

					LinearLayout linearLayoutLeftColumnSixteenthRowRightValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSixteenthRowRightValueInside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSixteenthRowRightValueInside = linearLayoutLeftColumnSixteenthRowRightValueInside.getLayoutParams();
					paramsLinearLayoutLeftColumnSixteenthRowRightValueInside.width = (int)(paramsLinearLayoutLeftColumnSixteenthRowRight.width*0.6);
					paramsLinearLayoutLeftColumnSixteenthRowRightValueInside.height = (int)(paramsLinearLayoutLeftColumnSixteenthRowRight.height*0.95);
					linearLayoutLeftColumnSixteenthRowRightValueInside.setLayoutParams(paramsLinearLayoutLeftColumnSixteenthRowRightValueInside);
						TextView textViewLeftColumnSixteenthRowRightValue = (TextView) rootView.findViewById(R.id.textViewLeftColumnSixteenthRowRightValue);

			LinearLayout linearLayoutLeftColumnSeventeenthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventeenthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventeenthRow = linearLayoutLeftColumnSeventeenthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnSeventeenthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnSeventeenthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnSeventeenthRow.setLayoutParams(paramsLinearLayoutLeftColumnSeventeenthRow);

				LinearLayout linearLayoutLeftColumnSeventeenthRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventeenthRowLeft);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventeenthRowLeft = linearLayoutLeftColumnSeventeenthRowLeft.getLayoutParams();
				paramsLinearLayoutLeftColumnSeventeenthRowLeft.width = (int)(paramsLinearLayoutLeftColumnSeventeenthRow.width*0.58);
				paramsLinearLayoutLeftColumnSeventeenthRowLeft.height = paramsLinearLayoutLeftColumnSeventeenthRow.height;
				linearLayoutLeftColumnSeventeenthRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnSeventeenthRowLeft);

					LinearLayout linearLayoutLeftColumnSeventeenthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventeenthRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventeenthRowLeftLabel = linearLayoutLeftColumnSeventeenthRowLeftLabel.getLayoutParams();
					paramsLinearLayoutLeftColumnSeventeenthRowLeftLabel.width = (int)(paramsLinearLayoutLeftColumnSeventeenthRowLeft.width*0.8);
					paramsLinearLayoutLeftColumnSeventeenthRowLeftLabel.height = paramsLinearLayoutLeftColumnSeventeenthRowLeft.height;
					linearLayoutLeftColumnSeventeenthRowLeftLabel.setLayoutParams(paramsLinearLayoutLeftColumnSeventeenthRowLeftLabel);
						TextView textViewLeftColumnSeventeenthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnSeventeenthRowLeftLabel);

					LinearLayout linearLayoutLeftColumnSeventeenthRowLeftCheck = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventeenthRowLeftCheck);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventeenthRowLeftCheck = linearLayoutLeftColumnSeventeenthRowLeftCheck.getLayoutParams();
					paramsLinearLayoutLeftColumnSeventeenthRowLeftCheck.width = (int)(paramsLinearLayoutLeftColumnSeventeenthRowLeft.width*0.2);
					paramsLinearLayoutLeftColumnSeventeenthRowLeftCheck.height = paramsLinearLayoutLeftColumnSeventeenthRowLeft.height;
					linearLayoutLeftColumnSeventeenthRowLeftCheck.setLayoutParams(paramsLinearLayoutLeftColumnSeventeenthRowLeftCheck);

						LinearLayout linearLayoutLeftColumnSeventeenthRowLeftCheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventeenthRowLeftCheckInside);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventeenthRowLeftCheckInside = linearLayoutLeftColumnSeventeenthRowLeftCheckInside.getLayoutParams();
						paramsLinearLayoutLeftColumnSeventeenthRowLeftCheckInside.width = (int)(paramsLinearLayoutLeftColumnSeventeenthRow.height * 0.75);
						paramsLinearLayoutLeftColumnSeventeenthRowLeftCheckInside.height = (int)(paramsLinearLayoutLeftColumnSeventeenthRow.height * 0.75);
						linearLayoutLeftColumnSeventeenthRowLeftCheckInside.setLayoutParams(paramsLinearLayoutLeftColumnSeventeenthRowLeftCheckInside);
							ImageView imageViewLeftColumnSeventeenthRowLeftCheckInside = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnSeventeenthRowLeftCheck);

				LinearLayout linearLayoutLeftColumnSeventeenthRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventeenthRowRight);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventeenthRowRight = linearLayoutLeftColumnSeventeenthRowRight.getLayoutParams();
				paramsLinearLayoutLeftColumnSeventeenthRowRight.width = (int)(paramsLinearLayoutLeftColumnSeventeenthRow.width*0.42);
				paramsLinearLayoutLeftColumnSeventeenthRowRight.height = paramsLinearLayoutLeftColumnSeventeenthRow.height;
				linearLayoutLeftColumnSeventeenthRowRight.setLayoutParams(paramsLinearLayoutLeftColumnSeventeenthRowRight);

					LinearLayout linearLayoutLeftColumnSeventeenthRowRightValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSeventeenthRowRightValueInside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSeventeenthRowRightValueInside = linearLayoutLeftColumnSeventeenthRowRightValueInside.getLayoutParams();
					paramsLinearLayoutLeftColumnSeventeenthRowRightValueInside.width = (int)(paramsLinearLayoutLeftColumnSeventeenthRowRight.width*0.6);
					paramsLinearLayoutLeftColumnSeventeenthRowRightValueInside.height = (int)(paramsLinearLayoutLeftColumnSeventeenthRowRight.height*0.95);
					linearLayoutLeftColumnSeventeenthRowRightValueInside.setLayoutParams(paramsLinearLayoutLeftColumnSeventeenthRowRightValueInside);
						TextView textViewLeftColumnSeventeenthRowRightValue = (TextView) rootView.findViewById(R.id.textViewLeftColumnSeventeenthRowRightValue);

			LinearLayout linearLayoutLeftColumnEighteenthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighteenthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighteenthRow = linearLayoutLeftColumnEighteenthRow.getLayoutParams();
			paramsLinearLayoutLeftColumnEighteenthRow.width = paramsLinearLayoutLeftColumn.width;
			paramsLinearLayoutLeftColumnEighteenthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
			linearLayoutLeftColumnEighteenthRow.setLayoutParams(paramsLinearLayoutLeftColumnEighteenthRow);

				LinearLayout linearLayoutLeftColumnEighteenthRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighteenthRowLeft);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighteenthRowLeft = linearLayoutLeftColumnEighteenthRowLeft.getLayoutParams();
				paramsLinearLayoutLeftColumnEighteenthRowLeft.width = (int)(paramsLinearLayoutLeftColumnEighteenthRow.width*0.58);
				paramsLinearLayoutLeftColumnEighteenthRowLeft.height = paramsLinearLayoutLeftColumnEighteenthRow.height;
				linearLayoutLeftColumnEighteenthRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnEighteenthRowLeft);

					LinearLayout linearLayoutLeftColumnEighteenthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighteenthRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighteenthRowLeftLabel = linearLayoutLeftColumnEighteenthRowLeftLabel.getLayoutParams();
					paramsLinearLayoutLeftColumnEighteenthRowLeftLabel.width = (int)(paramsLinearLayoutLeftColumnEighteenthRowLeft.width*0.8);
					paramsLinearLayoutLeftColumnEighteenthRowLeftLabel.height = paramsLinearLayoutLeftColumnEighteenthRowLeft.height;
					linearLayoutLeftColumnEighteenthRowLeftLabel.setLayoutParams(paramsLinearLayoutLeftColumnEighteenthRowLeftLabel);
						TextView textViewLeftColumnEighteenthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnEighteenthRowLeftLabel);

					LinearLayout linearLayoutLeftColumnEighteenthRowLeftCheck = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighteenthRowLeftCheck);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighteenthRowLeftCheck = linearLayoutLeftColumnEighteenthRowLeftCheck.getLayoutParams();
					paramsLinearLayoutLeftColumnEighteenthRowLeftCheck.width = (int)(paramsLinearLayoutLeftColumnEighteenthRowLeft.width*0.2);
					paramsLinearLayoutLeftColumnEighteenthRowLeftCheck.height = paramsLinearLayoutLeftColumnEighteenthRowLeft.height;
					linearLayoutLeftColumnEighteenthRowLeftCheck.setLayoutParams(paramsLinearLayoutLeftColumnEighteenthRowLeftCheck);

						LinearLayout linearLayoutLeftColumnEighteenthRowLeftCheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighteenthRowLeftCheckInside);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighteenthRowLeftCheckInside = linearLayoutLeftColumnEighteenthRowLeftCheckInside.getLayoutParams();
						paramsLinearLayoutLeftColumnEighteenthRowLeftCheckInside.width = (int)(paramsLinearLayoutLeftColumnEighteenthRow.height * 0.75);
						paramsLinearLayoutLeftColumnEighteenthRowLeftCheckInside.height = (int)(paramsLinearLayoutLeftColumnEighteenthRow.height * 0.75);
						linearLayoutLeftColumnEighteenthRowLeftCheckInside.setLayoutParams(paramsLinearLayoutLeftColumnEighteenthRowLeftCheckInside);
							ImageView imageViewLeftColumnEighteenthRowLeftCheckInside = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnEighteenthRowLeftCheck);

				LinearLayout linearLayoutLeftColumnEighteenthRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighteenthRowRight);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighteenthRowRight = linearLayoutLeftColumnEighteenthRowRight.getLayoutParams();
				paramsLinearLayoutLeftColumnEighteenthRowRight.width = (int)(paramsLinearLayoutLeftColumnEighteenthRow.width*0.42);
				paramsLinearLayoutLeftColumnEighteenthRowRight.height = paramsLinearLayoutLeftColumnEighteenthRow.height;
				linearLayoutLeftColumnEighteenthRowRight.setLayoutParams(paramsLinearLayoutLeftColumnEighteenthRowRight);

					LinearLayout linearLayoutLeftColumnEighteenthRowRightValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnEighteenthRowRightValueInside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnEighteenthRowRightValueInside = linearLayoutLeftColumnEighteenthRowRightValueInside.getLayoutParams();
					paramsLinearLayoutLeftColumnEighteenthRowRightValueInside.width = (int)(paramsLinearLayoutLeftColumnEighteenthRowRight.width*0.6);
					paramsLinearLayoutLeftColumnEighteenthRowRightValueInside.height = (int)(paramsLinearLayoutLeftColumnEighteenthRowRight.height*0.95);
					linearLayoutLeftColumnEighteenthRowRightValueInside.setLayoutParams(paramsLinearLayoutLeftColumnEighteenthRowRightValueInside);
						TextView textViewLeftColumnEighteenthRowRightValue = (TextView) rootView.findViewById(R.id.textViewLeftColumnEighteenthRowRightValue);

				LinearLayout linearLayoutLeftColumnNineteenthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNineteenthRow);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNineteenthRow = linearLayoutLeftColumnNineteenthRow.getLayoutParams();
				paramsLinearLayoutLeftColumnNineteenthRow.width = paramsLinearLayoutLeftColumn.width;
				paramsLinearLayoutLeftColumnNineteenthRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
				linearLayoutLeftColumnNineteenthRow.setLayoutParams(paramsLinearLayoutLeftColumnNineteenthRow);

					LinearLayout linearLayoutLeftColumnNineteenthRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNineteenthRowLeft);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNineteenthRowLeft = linearLayoutLeftColumnNineteenthRowLeft.getLayoutParams();
					paramsLinearLayoutLeftColumnNineteenthRowLeft.width = (int)(paramsLinearLayoutLeftColumnNineteenthRow.width*0.58);
					paramsLinearLayoutLeftColumnNineteenthRowLeft.height = paramsLinearLayoutLeftColumnNineteenthRow.height;
					linearLayoutLeftColumnNineteenthRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnNineteenthRowLeft);

						LinearLayout linearLayoutLeftColumnNineteenthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNineteenthRowLeftLabel);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNineteenthRowLeftLabel = linearLayoutLeftColumnNineteenthRowLeftLabel.getLayoutParams();
						paramsLinearLayoutLeftColumnNineteenthRowLeftLabel.width = (int)(paramsLinearLayoutLeftColumnNineteenthRowLeft.width*0.8);
						paramsLinearLayoutLeftColumnNineteenthRowLeftLabel.height = paramsLinearLayoutLeftColumnNineteenthRowLeft.height;
						linearLayoutLeftColumnNineteenthRowLeftLabel.setLayoutParams(paramsLinearLayoutLeftColumnNineteenthRowLeftLabel);
							TextView textViewLeftColumnNineteenthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnNineteenthRowLeftLabel);

						LinearLayout linearLayoutLeftColumnNineteenthRowLeftCheck = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNineteenthRowLeftCheck);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNineteenthRowLeftCheck = linearLayoutLeftColumnNineteenthRowLeftCheck.getLayoutParams();
						paramsLinearLayoutLeftColumnNineteenthRowLeftCheck.width = (int)(paramsLinearLayoutLeftColumnNineteenthRowLeft.width*0.2);
						paramsLinearLayoutLeftColumnNineteenthRowLeftCheck.height = paramsLinearLayoutLeftColumnNineteenthRowLeft.height;
						linearLayoutLeftColumnNineteenthRowLeftCheck.setLayoutParams(paramsLinearLayoutLeftColumnNineteenthRowLeftCheck);

							LinearLayout linearLayoutLeftColumnNineteenthRowLeftCheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNineteenthRowLeftCheckInside);
							ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNineteenthRowLeftCheckInside = linearLayoutLeftColumnNineteenthRowLeftCheckInside.getLayoutParams();
							paramsLinearLayoutLeftColumnNineteenthRowLeftCheckInside.width = (int)(paramsLinearLayoutLeftColumnNineteenthRow.height * 0.75);
							paramsLinearLayoutLeftColumnNineteenthRowLeftCheckInside.height = (int)(paramsLinearLayoutLeftColumnNineteenthRow.height * 0.75);
							linearLayoutLeftColumnNineteenthRowLeftCheckInside.setLayoutParams(paramsLinearLayoutLeftColumnNineteenthRowLeftCheckInside);
								ImageView imageViewLeftColumnNineteenthRowLeftCheckInside = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnNineteenthRowLeftCheck);

					LinearLayout linearLayoutLeftColumnNineteenthRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNineteenthRowRight);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNineteenthRowRight = linearLayoutLeftColumnNineteenthRowRight.getLayoutParams();
					paramsLinearLayoutLeftColumnNineteenthRowRight.width = (int)(paramsLinearLayoutLeftColumnNineteenthRow.width*0.42);
					paramsLinearLayoutLeftColumnNineteenthRowRight.height = paramsLinearLayoutLeftColumnNineteenthRow.height;
					linearLayoutLeftColumnNineteenthRowRight.setLayoutParams(paramsLinearLayoutLeftColumnNineteenthRowRight);

						LinearLayout linearLayoutLeftColumnNineteenthRowRightValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnNineteenthRowRightValueInside);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnNineteenthRowRightValueInside = linearLayoutLeftColumnNineteenthRowRightValueInside.getLayoutParams();
						paramsLinearLayoutLeftColumnNineteenthRowRightValueInside.width = (int)(paramsLinearLayoutLeftColumnNineteenthRowRight.width*0.6);
						paramsLinearLayoutLeftColumnNineteenthRowRightValueInside.height = (int)(paramsLinearLayoutLeftColumnNineteenthRowRight.height*0.95);
						linearLayoutLeftColumnNineteenthRowRightValueInside.setLayoutParams(paramsLinearLayoutLeftColumnNineteenthRowRightValueInside);
							TextView textViewLeftColumnNineteenthRowRightValue = (TextView) rootView.findViewById(R.id.textViewLeftColumnNineteenthRowRightValue);

				LinearLayout linearLayoutLeftColumnTwentiethRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentiethRow);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentiethRow = linearLayoutLeftColumnTwentiethRow.getLayoutParams();
				paramsLinearLayoutLeftColumnTwentiethRow.width = paramsLinearLayoutLeftColumn.width;
				paramsLinearLayoutLeftColumnTwentiethRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
				linearLayoutLeftColumnTwentiethRow.setLayoutParams(paramsLinearLayoutLeftColumnTwentiethRow);

					LinearLayout linearLayoutLeftColumnTwentiethRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentiethRowLeft);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentiethRowLeft = linearLayoutLeftColumnTwentiethRowLeft.getLayoutParams();
					paramsLinearLayoutLeftColumnTwentiethRowLeft.width = (int)(paramsLinearLayoutLeftColumnTwentiethRow.width*0.58);
					paramsLinearLayoutLeftColumnTwentiethRowLeft.height = paramsLinearLayoutLeftColumnTwentiethRow.height;
					linearLayoutLeftColumnTwentiethRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnTwentiethRowLeft);

						LinearLayout linearLayoutLeftColumnTwentiethRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentiethRowLeftLabel);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentiethRowLeftLabel = linearLayoutLeftColumnTwentiethRowLeftLabel.getLayoutParams();
						paramsLinearLayoutLeftColumnTwentiethRowLeftLabel.width = (int)(paramsLinearLayoutLeftColumnTwentiethRowLeft.width*0.8);
						paramsLinearLayoutLeftColumnTwentiethRowLeftLabel.height = paramsLinearLayoutLeftColumnTwentiethRowLeft.height;
						linearLayoutLeftColumnTwentiethRowLeftLabel.setLayoutParams(paramsLinearLayoutLeftColumnTwentiethRowLeftLabel);
							TextView textViewLeftColumnTwentiethRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwentiethRowLeftLabel);

						LinearLayout linearLayoutLeftColumnTwentiethRowLeftCheck = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentiethRowLeftCheck);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentiethRowLeftCheck = linearLayoutLeftColumnTwentiethRowLeftCheck.getLayoutParams();
						paramsLinearLayoutLeftColumnTwentiethRowLeftCheck.width = (int)(paramsLinearLayoutLeftColumnTwentiethRowLeft.width*0.2);
						paramsLinearLayoutLeftColumnTwentiethRowLeftCheck.height = paramsLinearLayoutLeftColumnTwentiethRowLeft.height;
						linearLayoutLeftColumnTwentiethRowLeftCheck.setLayoutParams(paramsLinearLayoutLeftColumnTwentiethRowLeftCheck);

							LinearLayout linearLayoutLeftColumnTwentiethRowLeftCheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentiethRowLeftCheckInside);
							ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentiethRowLeftCheckInside = linearLayoutLeftColumnTwentiethRowLeftCheckInside.getLayoutParams();
							paramsLinearLayoutLeftColumnTwentiethRowLeftCheckInside.width = (int)(paramsLinearLayoutLeftColumnTwentiethRow.height * 0.75);
							paramsLinearLayoutLeftColumnTwentiethRowLeftCheckInside.height = (int)(paramsLinearLayoutLeftColumnTwentiethRow.height * 0.75);
							linearLayoutLeftColumnTwentiethRowLeftCheckInside.setLayoutParams(paramsLinearLayoutLeftColumnTwentiethRowLeftCheckInside);
								ImageView imageViewLeftColumnTwentiethRowLeftCheckInside = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnTwentiethRowLeftCheck);

					LinearLayout linearLayoutLeftColumnTwentiethRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentiethRowRight);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentiethRowRight = linearLayoutLeftColumnTwentiethRowRight.getLayoutParams();
					paramsLinearLayoutLeftColumnTwentiethRowRight.width = (int)(paramsLinearLayoutLeftColumnTwentiethRow.width*0.42);
					paramsLinearLayoutLeftColumnTwentiethRowRight.height = paramsLinearLayoutLeftColumnTwentiethRow.height;
					linearLayoutLeftColumnTwentiethRowRight.setLayoutParams(paramsLinearLayoutLeftColumnTwentiethRowRight);

						LinearLayout linearLayoutLeftColumnTwentiethRowRightValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentiethRowRightValueInside);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentiethRowRightValueInside = linearLayoutLeftColumnTwentiethRowRightValueInside.getLayoutParams();
						paramsLinearLayoutLeftColumnTwentiethRowRightValueInside.width = (int)(paramsLinearLayoutLeftColumnTwentiethRowRight.width*0.6);
						paramsLinearLayoutLeftColumnTwentiethRowRightValueInside.height = (int)(paramsLinearLayoutLeftColumnTwentiethRowRight.height*0.95);
						linearLayoutLeftColumnTwentiethRowRightValueInside.setLayoutParams(paramsLinearLayoutLeftColumnTwentiethRowRightValueInside);
							TextView textViewLeftColumnTwentiethRowRightValue = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwentiethRowRightValue);

				LinearLayout linearLayoutLeftColumnTwentyFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentyFirstRow);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentyFirstRow = linearLayoutLeftColumnTwentyFirstRow.getLayoutParams();
				paramsLinearLayoutLeftColumnTwentyFirstRow.width = paramsLinearLayoutLeftColumn.width;
				paramsLinearLayoutLeftColumnTwentyFirstRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
				linearLayoutLeftColumnTwentyFirstRow.setLayoutParams(paramsLinearLayoutLeftColumnTwentyFirstRow);

					LinearLayout linearLayoutLeftColumnTwentyFirstRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentyFirstRowLeft);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentyFirstRowLeft = linearLayoutLeftColumnTwentyFirstRowLeft.getLayoutParams();
					paramsLinearLayoutLeftColumnTwentyFirstRowLeft.width = (int)(paramsLinearLayoutLeftColumnTwentyFirstRow.width*0.58);
					paramsLinearLayoutLeftColumnTwentyFirstRowLeft.height = paramsLinearLayoutLeftColumnTwentyFirstRow.height;
					linearLayoutLeftColumnTwentyFirstRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnTwentyFirstRowLeft);

						LinearLayout linearLayoutLeftColumnTwentyFirstRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentyFirstRowLeftLabel);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentyFirstRowLeftLabel = linearLayoutLeftColumnTwentyFirstRowLeftLabel.getLayoutParams();
						paramsLinearLayoutLeftColumnTwentyFirstRowLeftLabel.width = paramsLinearLayoutLeftColumnTwentyFirstRowLeft.width;
						paramsLinearLayoutLeftColumnTwentyFirstRowLeftLabel.height = paramsLinearLayoutLeftColumnTwentyFirstRowLeft.height;
						linearLayoutLeftColumnTwentyFirstRowLeftLabel.setLayoutParams(paramsLinearLayoutLeftColumnTwentyFirstRowLeftLabel);
							TextView textViewLeftColumnTwentyFirstRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwentyFirstRowLeftLabel);

					LinearLayout linearLayoutLeftColumnTwentyFirstRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentyFirstRowRight);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentyFirstRowRight = linearLayoutLeftColumnTwentyFirstRowRight.getLayoutParams();
					paramsLinearLayoutLeftColumnTwentyFirstRowRight.width = (int)(paramsLinearLayoutLeftColumnTwentyFirstRow.width*0.42);
					paramsLinearLayoutLeftColumnTwentyFirstRowRight.height = paramsLinearLayoutLeftColumnTwentyFirstRow.height;
					linearLayoutLeftColumnTwentyFirstRowRight.setLayoutParams(paramsLinearLayoutLeftColumnTwentyFirstRowRight);

						LinearLayout linearLayoutLeftColumnTwentyFirstRowRightValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentyFirstRowRightValueInside);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentyFirstRowRightValueInside = linearLayoutLeftColumnTwentyFirstRowRightValueInside.getLayoutParams();
						paramsLinearLayoutLeftColumnTwentyFirstRowRightValueInside.width = (int)(paramsLinearLayoutLeftColumnTwentyFirstRowRight.width*0.6);
						paramsLinearLayoutLeftColumnTwentyFirstRowRightValueInside.height = (int)(paramsLinearLayoutLeftColumnTwentyFirstRowRight.height*0.95);
						linearLayoutLeftColumnTwentyFirstRowRightValueInside.setLayoutParams(paramsLinearLayoutLeftColumnTwentyFirstRowRightValueInside);
							TextView textViewLeftColumnTwentyFirstRowRightValue = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwentyFirstRowRightValue);

				LinearLayout linearLayoutLeftColumnTwentySecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentySecondRow);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentySecondRow = linearLayoutLeftColumnTwentySecondRow.getLayoutParams();
				paramsLinearLayoutLeftColumnTwentySecondRow.width = paramsLinearLayoutLeftColumn.width;
				paramsLinearLayoutLeftColumnTwentySecondRow.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_LEFT_COLUMN);
				linearLayoutLeftColumnTwentySecondRow.setLayoutParams(paramsLinearLayoutLeftColumnTwentySecondRow);

					LinearLayout linearLayoutLeftColumnTwentySecondRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentySecondRowLeft);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentySecondRowLeft = linearLayoutLeftColumnTwentySecondRowLeft.getLayoutParams();
					paramsLinearLayoutLeftColumnTwentySecondRowLeft.width = (int)(paramsLinearLayoutLeftColumnTwentySecondRow.width*0.58);
					paramsLinearLayoutLeftColumnTwentySecondRowLeft.height = paramsLinearLayoutLeftColumnTwentySecondRow.height;
					linearLayoutLeftColumnTwentySecondRowLeft.setLayoutParams(paramsLinearLayoutLeftColumnTwentySecondRowLeft);

						LinearLayout linearLayoutLeftColumnTwentySecondRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentySecondRowLeftLabel);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentySecondRowLeftLabel = linearLayoutLeftColumnTwentySecondRowLeftLabel.getLayoutParams();
						paramsLinearLayoutLeftColumnTwentySecondRowLeftLabel.width = (int)(paramsLinearLayoutLeftColumnTwentySecondRowLeft.width*0.8);
						paramsLinearLayoutLeftColumnTwentySecondRowLeftLabel.height = paramsLinearLayoutLeftColumnTwentySecondRowLeft.height;
						linearLayoutLeftColumnTwentySecondRowLeftLabel.setLayoutParams(paramsLinearLayoutLeftColumnTwentySecondRowLeftLabel);
							TextView textViewLeftColumnTwentySecondRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwentySecondRowLeftLabel);

						LinearLayout linearLayoutLeftColumnTwentySecondRowLeftCheck = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentySecondRowLeftCheck);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentySecondRowLeftCheck = linearLayoutLeftColumnTwentySecondRowLeftCheck.getLayoutParams();
						paramsLinearLayoutLeftColumnTwentySecondRowLeftCheck.width = (int)(paramsLinearLayoutLeftColumnTwentySecondRowLeft.width*0.2);
						paramsLinearLayoutLeftColumnTwentySecondRowLeftCheck.height = paramsLinearLayoutLeftColumnTwentySecondRowLeft.height;
						linearLayoutLeftColumnTwentySecondRowLeftCheck.setLayoutParams(paramsLinearLayoutLeftColumnTwentySecondRowLeftCheck);

							LinearLayout linearLayoutLeftColumnTwentySecondRowLeftCheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentySecondRowLeftCheckInside);
							ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentySecondRowLeftCheckInside = linearLayoutLeftColumnTwentySecondRowLeftCheckInside.getLayoutParams();
							paramsLinearLayoutLeftColumnTwentySecondRowLeftCheckInside.width = (int)(paramsLinearLayoutLeftColumnTwentySecondRow.height * 0.75);
							paramsLinearLayoutLeftColumnTwentySecondRowLeftCheckInside.height = (int)(paramsLinearLayoutLeftColumnTwentySecondRow.height * 0.75);
							linearLayoutLeftColumnTwentySecondRowLeftCheckInside.setLayoutParams(paramsLinearLayoutLeftColumnTwentySecondRowLeftCheckInside);
								ImageView imageViewLeftColumnTwentySecondRowLeftCheckInside = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnTwentySecondRowLeftCheck);

					LinearLayout linearLayoutLeftColumnTwentySecondRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentySecondRowRight);
					ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentySecondRowRight = linearLayoutLeftColumnTwentySecondRowRight.getLayoutParams();
					paramsLinearLayoutLeftColumnTwentySecondRowRight.width = (int)(paramsLinearLayoutLeftColumnTwentySecondRow.width*0.42);
					paramsLinearLayoutLeftColumnTwentySecondRowRight.height = paramsLinearLayoutLeftColumnTwentySecondRow.height;
					linearLayoutLeftColumnTwentySecondRowRight.setLayoutParams(paramsLinearLayoutLeftColumnTwentySecondRowRight);

						LinearLayout linearLayoutLeftColumnTwentySecondRowRightValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnTwentySecondRowRightValueInside);
						ViewGroup.LayoutParams paramsLinearLayoutLeftColumnTwentySecondRowRightValueInside = linearLayoutLeftColumnTwentySecondRowRightValueInside.getLayoutParams();
						paramsLinearLayoutLeftColumnTwentySecondRowRightValueInside.width = (int)(paramsLinearLayoutLeftColumnTwentySecondRowRight.width*0.6);
						paramsLinearLayoutLeftColumnTwentySecondRowRightValueInside.height = (int)(paramsLinearLayoutLeftColumnTwentySecondRowRight.height*0.95);
						linearLayoutLeftColumnTwentySecondRowRightValueInside.setLayoutParams(paramsLinearLayoutLeftColumnTwentySecondRowRightValueInside);
							TextView textViewLeftColumnTwentySecondRowRightValue = (TextView) rootView.findViewById(R.id.textViewLeftColumnTwentySecondRowRightValue);
// -END LEFT COLUMN

// -RIGHT COLUMN
		// Linear layout "LinearLayoutRightColumn"
		LinearLayout linearLayoutRightColumn = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumn);
		ViewGroup.LayoutParams paramsLinearLayoutRightColumn = linearLayoutRightColumn.getLayoutParams();
		paramsLinearLayoutRightColumn.width = paramsLlinearLayoutSettings.width / 2;
		paramsLinearLayoutRightColumn.height = paramsLlinearLayoutSettings.height;
		linearLayoutRightColumn.setLayoutParams(paramsLinearLayoutRightColumn);

			LinearLayout linearLayoutRightColumnTop = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTop);
			ViewGroup.LayoutParams paramsLinearLayoutRightColumnTop = linearLayoutRightColumnTop.getLayoutParams();
			paramsLinearLayoutRightColumnTop.width = paramsLinearLayoutRightColumn.width;
			paramsLinearLayoutRightColumnTop.height = (int)(paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_RIGHT_COLUMN * 8 +
					HEIGHT_ROW_LEFT_COLUMN * 1.2);
			linearLayoutRightColumnTop.setLayoutParams(paramsLinearLayoutRightColumnTop);

				LinearLayout linearLayoutRightColumnTopLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeft);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeft = linearLayoutRightColumnTopLeft.getLayoutParams();
				paramsLinearLayoutRightColumnTopLeft.width = (int)(paramsLinearLayoutRightColumnTop.width * 0.7);
				paramsLinearLayoutRightColumnTopLeft.height = paramsLinearLayoutRightColumnTop.height;
				linearLayoutRightColumnTopLeft.setLayoutParams(paramsLinearLayoutRightColumnTopLeft);

					LinearLayout linearLayoutRightColumnTopLeftFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftFirstRow);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftFirstRow = linearLayoutRightColumnTopLeftFirstRow.getLayoutParams();
					paramsLinearLayoutRightColumnTopLeftFirstRow.width = paramsLinearLayoutRightColumnTopLeft.width;
					paramsLinearLayoutRightColumnTopLeftFirstRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_LEFT_COLUMN*1.2);
					linearLayoutRightColumnTopLeftFirstRow.setLayoutParams(paramsLinearLayoutRightColumnTopLeftFirstRow);
						TextView textViewRightColumnTopLeftFirstRowTitle = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftFirstRowTitle);

					LinearLayout linearLayoutRightColumnTopLeftSecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSecondRow);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSecondRow = linearLayoutRightColumnTopLeftSecondRow.getLayoutParams();
					paramsLinearLayoutRightColumnTopLeftSecondRow.width = paramsLinearLayoutRightColumnTopLeft.width;
					paramsLinearLayoutRightColumnTopLeftSecondRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
					linearLayoutRightColumnTopLeftSecondRow.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSecondRow);

						LinearLayout linearLayoutRightColumnTopLeftSecondRowlabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSecondRowlabel);
						ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSecondRowlabel = linearLayoutRightColumnTopLeftSecondRowlabel.getLayoutParams();
						paramsLinearLayoutRightColumnTopLeftSecondRowlabel.width = (int)(paramsLinearLayoutRightColumnTopLeftSecondRow.width*0.5);
						paramsLinearLayoutRightColumnTopLeftSecondRowlabel.height = paramsLinearLayoutRightColumnTopLeftSecondRow.height;
						linearLayoutRightColumnTopLeftSecondRowlabel.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSecondRowlabel);
							TextView textViewRightColumnTopLeftSecondRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftSecondRowLabel);

						LinearLayout linearLayoutRightColumnTopLeftSecondRowGlobalSystemUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSecondRowSpinner);
						ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSecondRowGlobalSystemUnits = linearLayoutRightColumnTopLeftSecondRowGlobalSystemUnits.getLayoutParams();
						paramsLinearLayoutRightColumnTopLeftSecondRowGlobalSystemUnits.width = (int)(paramsLinearLayoutRightColumnTopLeftSecondRow.width*0.5);
						paramsLinearLayoutRightColumnTopLeftSecondRowGlobalSystemUnits.height = paramsLinearLayoutRightColumnTopLeftSecondRow.height;
						linearLayoutRightColumnTopLeftSecondRowGlobalSystemUnits.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSecondRowGlobalSystemUnits);
							Spinner spinnerRightColumnTopLeftTopLeftSecondRowGlobalSystemUnits = (Spinner) rootView.findViewById(R.id.spinnerRightColumnTopLeftSecondRowGlobalSystemUnits);

							temp.clear();
							temp.add("Nautical Miles");
							String[] globalSystemUnits = new String[temp.size()];
							temp.toArray(globalSystemUnits);
							ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(Globals.context,R.layout.spinner_item,globalSystemUnits);
							arrayAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
							spinnerRightColumnTopLeftTopLeftSecondRowGlobalSystemUnits.setAdapter(arrayAdapter2);
							spinnerRightColumnTopLeftTopLeftSecondRowGlobalSystemUnits.setSelection(0);

						LinearLayout linearLayoutRightColumnTopLeftThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftThirdRow);
						ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftThirdRow = linearLayoutRightColumnTopLeftThirdRow.getLayoutParams();
						paramsLinearLayoutRightColumnTopLeftThirdRow.width = paramsLinearLayoutRightColumnTopLeft.width;
						paramsLinearLayoutRightColumnTopLeftThirdRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
						linearLayoutRightColumnTopLeftThirdRow.setLayoutParams(paramsLinearLayoutRightColumnTopLeftThirdRow);

							LinearLayout linearLayoutRightColumnTopLeftThirdRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftThirdRowLabel);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftThirdRowLabel = linearLayoutRightColumnTopLeftThirdRowLabel.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftThirdRowLabel.width = (int)(paramsLinearLayoutRightColumnTopLeftThirdRow.width*0.6);
							paramsLinearLayoutRightColumnTopLeftThirdRowLabel.height = paramsLinearLayoutRightColumnTopLeftThirdRow.height;
							linearLayoutRightColumnTopLeftThirdRowLabel.setLayoutParams(paramsLinearLayoutRightColumnTopLeftThirdRowLabel);
								TextView textViewRightColumnTopLeftThirdRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftThirdRowLabel);

							LinearLayout linearLayoutRightColumnTopLeftThirdRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftThirdRowValue);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftThirdRowValue = linearLayoutRightColumnTopLeftThirdRowValue.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftThirdRowValue.width = (int)(paramsLinearLayoutRightColumnTopLeftThirdRow.width*0.15);
							linearLayoutRightColumnTopLeftThirdRowValue.setLayoutParams(paramsLinearLayoutRightColumnTopLeftThirdRowValue);
								TextView textViewRightColumnTopLeftThirdRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftThirdRowValue);

							LinearLayout linearLayoutRightColumnTopLeftThirdRowUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftThirdRowUnits);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftThirdRowUnits = linearLayoutRightColumnTopLeftThirdRowUnits.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftThirdRowUnits.width = (int)(paramsLinearLayoutRightColumnTopLeftThirdRow.width*0.25);
							paramsLinearLayoutRightColumnTopLeftThirdRowUnits.height = paramsLinearLayoutRightColumnTopLeftThirdRow.height;
							linearLayoutRightColumnTopLeftThirdRowUnits.setLayoutParams(paramsLinearLayoutRightColumnTopLeftThirdRowUnits);
								TextView textViewRightColumnTopLeftThirdRowUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftThirdRowUnits);

						LinearLayout linearLayoutRightColumnTopLeftFourthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftFourthRow);
						ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftFourthRow = linearLayoutRightColumnTopLeftFourthRow.getLayoutParams();
						paramsLinearLayoutRightColumnTopLeftFourthRow.width = paramsLinearLayoutRightColumnTopLeft.width;
						paramsLinearLayoutRightColumnTopLeftFourthRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
						linearLayoutRightColumnTopLeftFourthRow.setLayoutParams(paramsLinearLayoutRightColumnTopLeftFourthRow);

							LinearLayout linearLayoutRightColumnTopLeftFourthRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftFourthRowLabel);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftFourthRowLabel = linearLayoutRightColumnTopLeftFourthRowLabel.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftFourthRowLabel.width = (int)(paramsLinearLayoutRightColumnTopLeftFourthRow.width*0.6);
							paramsLinearLayoutRightColumnTopLeftFourthRowLabel.height = paramsLinearLayoutRightColumnTopLeftFourthRow.height;
							linearLayoutRightColumnTopLeftFourthRowLabel.setLayoutParams(paramsLinearLayoutRightColumnTopLeftFourthRowLabel);
								TextView textViewRightColumnTopLeftFourthRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftFourthRowLabel);

							LinearLayout linearLayoutRightColumnTopLeftFourthRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftFourthRowValue);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftFourthRowValue = linearLayoutRightColumnTopLeftFourthRowValue.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftFourthRowValue.width = (int)(paramsLinearLayoutRightColumnTopLeftFourthRow.width*0.15);
							linearLayoutRightColumnTopLeftFourthRowValue.setLayoutParams(paramsLinearLayoutRightColumnTopLeftFourthRowValue);
								TextView textViewRightColumnTopLeftFourthRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftFourthRowValue);

							LinearLayout linearLayoutRightColumnTopLeftFourthRowUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftFourthRowUnits);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftFourthRowUnits = linearLayoutRightColumnTopLeftFourthRowUnits.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftFourthRowUnits.width = (int)(paramsLinearLayoutRightColumnTopLeftFourthRow.width*0.25);
							paramsLinearLayoutRightColumnTopLeftFourthRowUnits.height = paramsLinearLayoutRightColumnTopLeftFourthRow.height;
							linearLayoutRightColumnTopLeftFourthRowUnits.setLayoutParams(paramsLinearLayoutRightColumnTopLeftFourthRowUnits);
								TextView textViewRightColumnTopLeftFourthRowUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftFourthRowUnits);

						LinearLayout linearLayoutRightColumnTopLeftFifthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftFifthRow);
						ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftFifthRow = linearLayoutRightColumnTopLeftFifthRow.getLayoutParams();
						paramsLinearLayoutRightColumnTopLeftFifthRow.width = paramsLinearLayoutRightColumnTopLeft.width;
						paramsLinearLayoutRightColumnTopLeftFifthRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
						linearLayoutRightColumnTopLeftFifthRow.setLayoutParams(paramsLinearLayoutRightColumnTopLeftFifthRow);

							LinearLayout linearLayoutRightColumnTopLeftFifthRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftFifthRowLabel);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftFifthRowLabel = linearLayoutRightColumnTopLeftFifthRowLabel.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftFifthRowLabel.width = (int)(paramsLinearLayoutRightColumnTopLeftFifthRow.width*0.6);
							paramsLinearLayoutRightColumnTopLeftFifthRowLabel.height = paramsLinearLayoutRightColumnTopLeftFifthRow.height;
							linearLayoutRightColumnTopLeftFifthRowLabel.setLayoutParams(paramsLinearLayoutRightColumnTopLeftFifthRowLabel);
								TextView textViewRightColumnTopLeftFifthRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftFifthRowLabel);

							LinearLayout linearLayoutRightColumnTopLeftFifthRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftFifthRowValue);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftFifthRowValue = linearLayoutRightColumnTopLeftFifthRowValue.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftFifthRowValue.width = (int)(paramsLinearLayoutRightColumnTopLeftFifthRow.width*0.15);
							linearLayoutRightColumnTopLeftFifthRowValue.setLayoutParams(paramsLinearLayoutRightColumnTopLeftFifthRowValue);
								TextView textViewRightColumnTopLeftFifthRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftFifthRowValue);

							LinearLayout linearLayoutRightColumnTopLeftFifthRowUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftFifthRowUnits);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftFifthRowUnits = linearLayoutRightColumnTopLeftFifthRowUnits.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftFifthRowUnits.width = (int)(paramsLinearLayoutRightColumnTopLeftFifthRow.width*0.25);
							paramsLinearLayoutRightColumnTopLeftFifthRowUnits.height = paramsLinearLayoutRightColumnTopLeftFifthRow.height;
							linearLayoutRightColumnTopLeftFifthRowUnits.setLayoutParams(paramsLinearLayoutRightColumnTopLeftFifthRowUnits);
								TextView textViewRightColumnTopLeftFifthRowUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftFifthRowUnits);

						LinearLayout linearLayoutRightColumnTopLeftSixthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSixthRow);
						ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSixthRow = linearLayoutRightColumnTopLeftSixthRow.getLayoutParams();
						paramsLinearLayoutRightColumnTopLeftSixthRow.width = paramsLinearLayoutRightColumnTopLeft.width;
						paramsLinearLayoutRightColumnTopLeftSixthRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
						linearLayoutRightColumnTopLeftSixthRow.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSixthRow);

							LinearLayout linearLayoutRightColumnTopLeftSixthRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSixthRowLabel);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSixthRowLabel = linearLayoutRightColumnTopLeftSixthRowLabel.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftSixthRowLabel.width = (int)(paramsLinearLayoutRightColumnTopLeftSixthRow.width*0.6);
							paramsLinearLayoutRightColumnTopLeftSixthRowLabel.height = paramsLinearLayoutRightColumnTopLeftSixthRow.height;
							linearLayoutRightColumnTopLeftSixthRowLabel.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSixthRowLabel);
								TextView textViewRightColumnTopLeftSixthRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftSixthRowLabel);

							LinearLayout linearLayoutRightColumnTopLeftSixthRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSixthRowValue);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSixthRowValue = linearLayoutRightColumnTopLeftSixthRowValue.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftSixthRowValue.width = (int)(paramsLinearLayoutRightColumnTopLeftSixthRow.width*0.15);
							linearLayoutRightColumnTopLeftSixthRowValue.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSixthRowValue);
								TextView textViewRightColumnTopLeftSixthRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftSixthRowValue);

						LinearLayout linearLayoutRightColumnTopLeftSixthRowUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSixthRowUnits);
						ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSixthRowUnits = linearLayoutRightColumnTopLeftSixthRowUnits.getLayoutParams();
						paramsLinearLayoutRightColumnTopLeftSixthRowUnits.width = (int)(paramsLinearLayoutRightColumnTopLeftSixthRow.width*0.25);
						paramsLinearLayoutRightColumnTopLeftSixthRowUnits.height = paramsLinearLayoutRightColumnTopLeftSixthRow.height;
						linearLayoutRightColumnTopLeftSixthRowUnits.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSixthRowUnits);
							TextView textViewRightColumnTopLeftSixthRowUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftSixthRowUnits);

						LinearLayout linearLayoutRightColumnTopLeftSeventhRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSeventhRow);
						ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSeventhRow = linearLayoutRightColumnTopLeftSeventhRow.getLayoutParams();
						paramsLinearLayoutRightColumnTopLeftSeventhRow.width = paramsLinearLayoutRightColumnTopLeft.width;
						paramsLinearLayoutRightColumnTopLeftSeventhRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
						linearLayoutRightColumnTopLeftSeventhRow.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSeventhRow);

							LinearLayout linearLayoutRightColumnTopLeftSeventhRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSeventhRowLabel);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSeventhRowLabel = linearLayoutRightColumnTopLeftSeventhRowLabel.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftSeventhRowLabel.width = (int)(paramsLinearLayoutRightColumnTopLeftSeventhRow.width*0.6);
							paramsLinearLayoutRightColumnTopLeftSeventhRowLabel.height = paramsLinearLayoutRightColumnTopLeftSeventhRow.height;
							linearLayoutRightColumnTopLeftSeventhRowLabel.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSeventhRowLabel);
								TextView textViewRightColumnTopLeftSeventhRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftSeventhRowLabel);

							LinearLayout linearLayoutRightColumnTopLeftSeventhRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftSeventhRowValue);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftSeventhRowValue = linearLayoutRightColumnTopLeftSeventhRowValue.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftSeventhRowValue.width = (int)(paramsLinearLayoutRightColumnTopLeftSeventhRow.width*0.15);
							linearLayoutRightColumnTopLeftSeventhRowValue.setLayoutParams(paramsLinearLayoutRightColumnTopLeftSeventhRowValue);
								TextView textViewRightColumnTopLeftSeventhRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftSeventhRowValue);

						LinearLayout linearLayoutRightColumnTopLeftEighthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftEighthRow);
						ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftEighthRow = linearLayoutRightColumnTopLeftEighthRow.getLayoutParams();
						paramsLinearLayoutRightColumnTopLeftEighthRow.width = paramsLinearLayoutRightColumnTopLeft.width;
						paramsLinearLayoutRightColumnTopLeftEighthRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
						linearLayoutRightColumnTopLeftEighthRow.setLayoutParams(paramsLinearLayoutRightColumnTopLeftEighthRow);

							LinearLayout linearLayoutRightColumnTopLeftEighthRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftEighthRowLabel);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftEighthRowLabel = linearLayoutRightColumnTopLeftEighthRowLabel.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftEighthRowLabel.width = (int)(paramsLinearLayoutRightColumnTopLeftEighthRow.width*0.6);
							paramsLinearLayoutRightColumnTopLeftEighthRowLabel.height = paramsLinearLayoutRightColumnTopLeftEighthRow.height;
							linearLayoutRightColumnTopLeftEighthRowLabel.setLayoutParams(paramsLinearLayoutRightColumnTopLeftEighthRowLabel);
								TextView textViewRightColumnTopLeftEighthRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftEighthRowLabel);

							LinearLayout linearLayoutRightColumnTopLeftEighthRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopLeftEighthRowValue);
							ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopLeftEighthRowValue = linearLayoutRightColumnTopLeftEighthRowValue.getLayoutParams();
							paramsLinearLayoutRightColumnTopLeftEighthRowValue.width = (int)(paramsLinearLayoutRightColumnTopLeftEighthRow.width*0.15);
							linearLayoutRightColumnTopLeftEighthRowValue.setLayoutParams(paramsLinearLayoutRightColumnTopLeftEighthRowValue);
								TextView textViewRightColumnTopLeftEighthRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnTopLeftEighthRowValue);

				LinearLayout linearLayoutRightColumnTopRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopRight);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopRight = linearLayoutRightColumnTopRight.getLayoutParams();
				paramsLinearLayoutRightColumnTopRight.width = (int)(paramsLinearLayoutRightColumnTop.width * 0.30);
				paramsLinearLayoutRightColumnTopRight.height = paramsLinearLayoutRightColumnTop.height;
				linearLayoutRightColumnTopRight.setLayoutParams(paramsLinearLayoutRightColumnTopRight);

					LinearLayout linearLayoutRightColumnTopRightFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopRightFirstRow);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopRightFirstRow = linearLayoutRightColumnTopRightFirstRow.getLayoutParams();
					paramsLinearLayoutRightColumnTopRightFirstRow.width = paramsLinearLayoutRightColumnTopRight.width;
					paramsLinearLayoutRightColumnTopRightFirstRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_LEFT_COLUMN * 1.2);
					linearLayoutRightColumnTopRightFirstRow.setLayoutParams(paramsLinearLayoutRightColumnTopRightFirstRow);
						TextView textViewRightColumnTopRightFirstRowTitle = (TextView) rootView.findViewById(R.id.textViewRightColumnTopRightFirstRowTitle);

					LinearLayout linearLayoutRightColumnTopRightSecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopRightSecondRow);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopRightSecondRow = linearLayoutRightColumnTopRightSecondRow.getLayoutParams();
					paramsLinearLayoutRightColumnTopRightSecondRow.width = paramsLinearLayoutRightColumnTopRight.width;
					paramsLinearLayoutRightColumnTopRightSecondRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
					linearLayoutRightColumnTopRightSecondRow.setLayoutParams(paramsLinearLayoutRightColumnTopRightSecondRow);
						Button buttonRightColumnTopRightSecondRowButton = (Button) rootView.findViewById(R.id.buttonRightColumnTopRightSecondRowButton);

					LinearLayout linearLayoutRightColumnTopRightThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopRightThirdRow);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopRightThirdRow = linearLayoutRightColumnTopRightThirdRow.getLayoutParams();
					paramsLinearLayoutRightColumnTopRightThirdRow.width = paramsLinearLayoutRightColumnTopRight.width;
					paramsLinearLayoutRightColumnTopRightThirdRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
					linearLayoutRightColumnTopRightThirdRow.setLayoutParams(paramsLinearLayoutRightColumnTopRightThirdRow);
						Button buttonRightColumnTopRightThirdRowButton = (Button) rootView.findViewById(R.id.buttonRightColumnTopRightThirdRowButton);

					LinearLayout linearLayoutRightColumnTopRightFourthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopRightFourthRow);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopRightFourthRow = linearLayoutRightColumnTopRightFourthRow.getLayoutParams();
					paramsLinearLayoutRightColumnTopRightFourthRow.width = paramsLinearLayoutRightColumnTopRight.width;
					paramsLinearLayoutRightColumnTopRightFourthRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
					linearLayoutRightColumnTopRightFourthRow.setLayoutParams(paramsLinearLayoutRightColumnTopRightFourthRow);
						Button buttonRightColumnTopRightFourthRowButton = (Button) rootView.findViewById(R.id.buttonRightColumnTopRightFourthRowButton);

					LinearLayout linearLayoutRightColumnTopRightFifthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopRightFifthRow);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopRightFifthRow = linearLayoutRightColumnTopRightFifthRow.getLayoutParams();
					paramsLinearLayoutRightColumnTopRightFifthRow.width = paramsLinearLayoutRightColumnTopRight.width;
					paramsLinearLayoutRightColumnTopRightFifthRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
					linearLayoutRightColumnTopRightFifthRow.setLayoutParams(paramsLinearLayoutRightColumnTopRightFifthRow);
						Button buttonRightColumnTopRightFifthRowButton = (Button) rootView.findViewById(R.id.buttonRightColumnTopRightFifthRowButton);

					LinearLayout linearLayoutRightColumnTopRightSixthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTopRightSixthRow);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnTopRightSixthRow = linearLayoutRightColumnTopRightSixthRow.getLayoutParams();
					paramsLinearLayoutRightColumnTopRightSixthRow.width = paramsLinearLayoutRightColumnTopRight.width;
					paramsLinearLayoutRightColumnTopRightSixthRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN * 2);
					linearLayoutRightColumnTopRightSixthRow.setLayoutParams(paramsLinearLayoutRightColumnTopRightSixthRow);
						Button buttonRightColumnTopRightSixthRowButton = (Button) rootView.findViewById(R.id.buttonRightColumnTopRightSixthRowButton);

			LinearLayout linearLayoutRightColumnBottom = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottom);
			ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottom = linearLayoutRightColumnBottom.getLayoutParams();
			paramsLinearLayoutRightColumnBottom.width = paramsLinearLayoutRightColumn.width;
			paramsLinearLayoutRightColumnBottom.height = (int)(HEIGHT_ROW_LEFT_COLUMN * 0.8 + paramsLinearLayoutLeftColumn.height*HEIGHT_ROW_RIGHT_COLUMN * 12.2);
			linearLayoutRightColumnBottom.setLayoutParams(paramsLinearLayoutRightColumnBottom);

				LinearLayout linearLayoutRightColumnBottomFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFirstRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFirstRow = linearLayoutRightColumnBottomFirstRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomFirstRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomFirstRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomFirstRow.setLayoutParams(paramsLinearLayoutLeftColumnFirstRow);
					LinearLayout linearLayoutRightColumnBottomFirstRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFirstRowLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFirstRowLabel = linearLayoutRightColumnBottomFirstRowLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFirstRowLabel.width = (int)(paramsLinearLayoutRightColumnBottomFirstRow.width * 0.4);
					linearLayoutRightColumnBottomFirstRowLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomFirstRowLabel);
						TextView textViewRightColumnBottomFirstRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFirstRowLabel);
					LinearLayout linearLayoutRightColumnBottomFirstRowLabelDay = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFirstRowLabelDay);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFirstRowLabelDay = linearLayoutRightColumnBottomFirstRowLabelDay.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFirstRowLabelDay.width = (int)(paramsLinearLayoutRightColumnBottomFirstRow.width * 0.12);
					linearLayoutRightColumnBottomFirstRowLabelDay.setLayoutParams(paramsLinearLayoutRightColumnBottomFirstRowLabelDay);
						TextView textViewRightColumnBottomFirstRowLabelDay = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFirstRowLabelDay);
					LinearLayout linearLayoutRightColumnBottomFirstRowValueDay = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFirstRowValueDay);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFirstRowValueDay = linearLayoutRightColumnBottomFirstRowValueDay.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFirstRowValueDay.width = (int)(paramsLinearLayoutRightColumnBottomFirstRow.width * 0.08);
					linearLayoutRightColumnBottomFirstRowValueDay.setLayoutParams(paramsLinearLayoutRightColumnBottomFirstRowValueDay);
						TextView textViewRightColumnBottomFirstRowValueDay = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFirstRowValueDay);
					LinearLayout linearLayoutRightColumnBottomFirstRowLabelCloud = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFirstRowLabelCloud);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFirstRowLabelCloud = linearLayoutRightColumnBottomFirstRowLabelCloud.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFirstRowLabelCloud.width = (int)(paramsLinearLayoutRightColumnBottomFirstRow.width * 0.12);
					linearLayoutRightColumnBottomFirstRowLabelCloud.setLayoutParams(paramsLinearLayoutRightColumnBottomFirstRowLabelCloud);
						TextView textViewRightColumnBottomFirstRowLabelCloud = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFirstRowLabelCloud);
					LinearLayout linearLayoutRightColumnBottomFirstRowValueCloud = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFirstRowValueCloud);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFirstRowValueCloud = linearLayoutRightColumnBottomFirstRowValueCloud.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFirstRowValueCloud.width = (int)(paramsLinearLayoutRightColumnBottomFirstRow.width * 0.08);
					linearLayoutRightColumnBottomFirstRowValueCloud.setLayoutParams(paramsLinearLayoutRightColumnBottomFirstRowValueCloud);
						TextView textViewRightColumnBottomFirstRowValueCloud = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFirstRowValueCloud);
					LinearLayout linearLayoutRightColumnBottomFirstRowLabelNight = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFirstRowLabelNight);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFirstRowLabelNight = linearLayoutRightColumnBottomFirstRowLabelNight.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFirstRowLabelNight.width = (int)(paramsLinearLayoutRightColumnBottomFirstRow.width * 0.12);
					linearLayoutRightColumnBottomFirstRowLabelNight.setLayoutParams(paramsLinearLayoutRightColumnBottomFirstRowLabelNight);
						TextView textViewRightColumnBottomFirstRowLabelNight = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFirstRowLabelNight);
					LinearLayout linearLayoutRightColumnBottomFirstRowValueNight = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFirstRowValueNight);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFirstRowValueNight = linearLayoutRightColumnBottomFirstRowValueNight.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFirstRowValueNight.width = (int)(paramsLinearLayoutRightColumnBottomFirstRow.width * 0.08);
					linearLayoutRightColumnBottomFirstRowValueNight.setLayoutParams(paramsLinearLayoutRightColumnBottomFirstRowValueNight);
						TextView textViewRightColumnBottomFirstRowValueNight = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFirstRowValueNight);

				LinearLayout linearLayoutRightColumnBottomSecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSecondRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSecondRow = linearLayoutRightColumnBottomSecondRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomSecondRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomSecondRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomSecondRow.setLayoutParams(paramsLinearLayoutRightColumnBottomSecondRow);
					LinearLayout linearLayoutRightColumnBottomSecondRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSecondRowLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSecondRowLabel = linearLayoutRightColumnBottomSecondRowLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSecondRowLabel.width = (int)(paramsLinearLayoutRightColumnBottomSecondRow.width * 0.55);
					linearLayoutRightColumnBottomSecondRowLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomSecondRowLabel);
						TextView textViewRightColumnBottomSecondRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSecondRowLabel);
					LinearLayout linearLayoutRightColumnBottomSecondRowButton = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSecondRowButton);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSecondRowButton = linearLayoutRightColumnBottomSecondRowButton.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSecondRowButton.width = (int)(paramsLinearLayoutRightColumnBottomSecondRow.width*0.2);
					linearLayoutRightColumnBottomSecondRowButton.setLayoutParams(paramsLinearLayoutRightColumnBottomSecondRowButton);
						Button buttonRightColumnBottomSecondRowButton = (Button) rootView.findViewById(R.id.buttonRightColumnBottomSecondRowButton);
					LinearLayout linearLayoutRightColumnBottomSecondRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSecondRowValue);
					ViewGroup.LayoutParams paramsLinearLinearLayoutRightColumnBottomSecondRowValue = linearLayoutRightColumnBottomSecondRowValue.getLayoutParams();
					paramsLinearLinearLayoutRightColumnBottomSecondRowValue.width = (int)(paramsLinearLayoutRightColumnBottomSecondRow.width * 0.1);
					linearLayoutRightColumnBottomSecondRowValue.setLayoutParams(paramsLinearLinearLayoutRightColumnBottomSecondRowValue);
						LinearLayout linearLayoutRightColumnBottomSecondRowValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSecondRowValueInside);
						ViewGroup.LayoutParams paramsLinearLinearLayoutRightColumnBottomSecondRowValueInside = linearLayoutRightColumnBottomSecondRowValueInside.getLayoutParams();
						paramsLinearLinearLayoutRightColumnBottomSecondRowValueInside.width = (int)(paramsLinearLinearLayoutRightColumnBottomSecondRowValue.width * 0.8);
						linearLayoutRightColumnBottomSecondRowValueInside.setLayoutParams(paramsLinearLinearLayoutRightColumnBottomSecondRowValueInside);
							TextView textViewRightColumnBottomSecondRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSecondRowValue);
					LinearLayout linearLayoutRightColumnBottomSecondRowUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSecondRowUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSecondRowUnits = linearLayoutRightColumnBottomSecondRowUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSecondRowUnits.width = (int)(paramsLinearLayoutRightColumnBottomSecondRow.width * 0.15);
					linearLayoutRightColumnBottomSecondRowUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomSecondRowUnits);
						TextView textViewRightColumnBottomSecondRowUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSecondRowUnits);

				LinearLayout linearLayoutRightColumnBottomThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomThirdRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomThirdRow = linearLayoutRightColumnBottomThirdRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomThirdRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomThirdRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomThirdRow.setLayoutParams(paramsLinearLayoutLeftColumnThirdRow);
					LinearLayout linearLayoutRightColumnBottomThirdRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomThirdRowLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomThirdRowLabel = linearLayoutRightColumnBottomThirdRowLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomThirdRowLabel.width = (int)(paramsLinearLayoutRightColumnBottomThirdRow.width * 0.55);
					paramsLinearLayoutRightColumnBottomThirdRowLabel.height = paramsLinearLayoutRightColumnBottomThirdRow.height;
					linearLayoutRightColumnBottomThirdRowLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomThirdRowLabel);
						TextView textViewRightColumnBottomThirdRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomThirdRowLabel);
					LinearLayout linearLayoutRightColumnBottomThirdRowButton = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomThirdRowButton);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomThirdRowButton = linearLayoutRightColumnBottomThirdRowButton.getLayoutParams();
					paramsLinearLayoutRightColumnBottomThirdRowButton.width = (int)(paramsLinearLayoutRightColumnBottomThirdRow.width*0.2);
					linearLayoutRightColumnBottomThirdRowButton.setLayoutParams(paramsLinearLayoutRightColumnBottomThirdRowButton);
						Button buttonRightColumnBottomThirdRowButton = (Button) rootView.findViewById(R.id.buttonRightColumnBottomThirdRowButton);
					LinearLayout linearLayoutRightColumnBottomThirdRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomThirdRowValue);
					ViewGroup.LayoutParams paramsLinearLinearLayoutRightColumnBottomThirdRowValue = linearLayoutRightColumnBottomThirdRowValue.getLayoutParams();
					paramsLinearLinearLayoutRightColumnBottomThirdRowValue.width = (int)(paramsLinearLayoutRightColumnBottomThirdRow.width * 0.1);
					linearLayoutRightColumnBottomThirdRowValue.setLayoutParams(paramsLinearLinearLayoutRightColumnBottomThirdRowValue);
						LinearLayout linearLayoutRightColumnBottomThirdRowValueInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomThirdRowValueInside);
						ViewGroup.LayoutParams paramsLinearLinearLayoutRightColumnBottomThirdRowValueInside = linearLayoutRightColumnBottomThirdRowValueInside.getLayoutParams();
						paramsLinearLinearLayoutRightColumnBottomThirdRowValueInside.width = (int)(paramsLinearLinearLayoutRightColumnBottomThirdRowValue.width * 0.8);
						linearLayoutRightColumnBottomThirdRowValueInside.setLayoutParams(paramsLinearLinearLayoutRightColumnBottomThirdRowValueInside);
							TextView textViewRightColumnBottomThirdRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomThirdRowValue);
					LinearLayout linearLayoutRightColumnBottomThirdRowUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomThirdRowUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomThirdRowUnits = linearLayoutRightColumnBottomThirdRowUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomThirdRowUnits.width = (int)(paramsLinearLayoutRightColumnBottomThirdRow.width * 0.15);
					linearLayoutRightColumnBottomThirdRowUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomThirdRowUnits);
						TextView textViewRightColumnBottomThirdRowUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomThirdRowUnits);

				LinearLayout linearLayoutRightColumnBottomFourthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFourthRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFourthRow = linearLayoutRightColumnBottomFourthRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomFourthRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomFourthRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomFourthRow.setLayoutParams(paramsLinearLayoutRightColumnBottomFourthRow);
					LinearLayout linearLayoutRightColumnBottomFourthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFourthRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFourthRowLeftLabel = linearLayoutRightColumnBottomFourthRowLeftLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFourthRowLeftLabel.width = (int)(paramsLinearLayoutRightColumnBottomFourthRow.width * 0.5);
					linearLayoutRightColumnBottomFourthRowLeftLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomFourthRowLeftLabel);
						TextView textViewRightColumnBottomFourthRowLeftLabell = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFourthRowLeftLabell);

				LinearLayout linearLayoutRightColumnBottomFifthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFifthRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFifthRow = linearLayoutRightColumnBottomFifthRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomFifthRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomFifthRow.height = (int)(paramsLinearLayoutRightColumn.height*HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomFifthRow.setLayoutParams(paramsLinearLayoutRightColumnBottomFifthRow);
					LinearLayout linearLayoutRightColumnBottomFifthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFifthRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFifthRowLeftLabel = linearLayoutRightColumnBottomFifthRowLeftLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFifthRowLeftLabel.width = (int)(paramsLinearLayoutRightColumnBottomFifthRow.width*0.30);
					linearLayoutRightColumnBottomFifthRowLeftLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomFifthRowLeftLabel);
						TextView textViewRightColumnBottomFifthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFifthRowLeftLabel);
					LinearLayout linearLayoutRightColumnBottomFifthRowLeftValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFifthRowLeftValue);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFifthRowLeftValue = linearLayoutRightColumnBottomFifthRowLeftValue.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFifthRowLeftValue.width = (int)(paramsLinearLayoutRightColumnBottomFifthRow.width * 0.12);
					linearLayoutRightColumnBottomFifthRowLeftValue.setLayoutParams(paramsLinearLayoutRightColumnBottomFifthRowLeftValue);
						TextView textViewRightColumnBottomFifthRowLeftValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFifthRowLeftValue);
					LinearLayout linearLayoutRightColumnBottomFifthRowLeftUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFifthRowLeftUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFifthRowLeftUnits = linearLayoutRightColumnBottomFifthRowLeftUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFifthRowLeftUnits.width = (int)(paramsLinearLayoutRightColumnBottomFifthRow.width*0.08);
					linearLayoutRightColumnBottomFifthRowLeftUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomFifthRowLeftUnits);
						TextView textViewRightColumnBottomFifthRowLeftUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFifthRowLeftUnits);
					LinearLayout linearLayoutRightColumnBottomFifthRowRightLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFifthRowRightLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFifthRowRightLabel = linearLayoutRightColumnBottomFifthRowRightLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFifthRowRightLabel.width = (int)(paramsLinearLayoutRightColumnBottomFifthRow.width*0.30);
					linearLayoutRightColumnBottomFifthRowRightLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomFifthRowRightLabel);
						TextView textViewRightColumnBottomFifthRowRightLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFifthRowRightLabel);
					LinearLayout linearLayoutRightColumnBottomFifthRowRightValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFifthRowRightValue);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFifthRowRightValue = linearLayoutRightColumnBottomFifthRowRightValue.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFifthRowRightValue.width = (int)(paramsLinearLayoutRightColumnBottomFifthRow.width * 0.15);
					linearLayoutRightColumnBottomFifthRowRightValue.setLayoutParams(paramsLinearLayoutRightColumnBottomFifthRowRightValue);
						TextView textViewRightColumnBottomFifthRowRightValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFifthRowRightValue);
					LinearLayout linearLayoutRightColumnBottomFifthRowRightUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomFifthRowRightUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomFifthRowRightUnits = linearLayoutRightColumnBottomFifthRowRightUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomFifthRowRightUnits.width = (int)(paramsLinearLayoutRightColumnBottomFifthRow.width*0.05);
					linearLayoutRightColumnBottomFifthRowRightUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomFifthRowRightUnits);
						TextView textViewRightColumnBottomFifthRowRightUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomFifthRowRightUnits);

				LinearLayout linearLayoutRightColumnBottomSixthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSixthRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSixthRow = linearLayoutRightColumnBottomSixthRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomSixthRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomSixthRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomSixthRow.setLayoutParams(paramsLinearLayoutRightColumnBottomSixthRow);
					LinearLayout linearLayoutRightColumnBottomSixthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSixthRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSixthRowLeftLabel = linearLayoutRightColumnBottomSixthRowLeftLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSixthRowLeftLabel.width = (int)(paramsLinearLayoutRightColumnBottomSixthRow.width*0.3);
					linearLayoutRightColumnBottomSixthRowLeftLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomSixthRowLeftLabel);
						TextView textViewRightColumnBottomSixthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSixthRowLeftLabel);
					LinearLayout linearLayoutRightColumnBottomSixthRowLeftValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSixthRowLeftValue);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSixthRowLeftValue = linearLayoutRightColumnBottomSixthRowLeftValue.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSixthRowLeftValue.width = (int)(paramsLinearLayoutRightColumnBottomSixthRow.width * 0.12);
					linearLayoutRightColumnBottomSixthRowLeftValue.setLayoutParams(paramsLinearLayoutRightColumnBottomSixthRowLeftValue);
						TextView textViewRightColumnBottomSixthRowLeftValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSixthRowLeftValue);
					LinearLayout linearLayoutRightColumnBottomSixthRowLeftUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSixthRowLeftUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSixthRowLeftUnits = linearLayoutRightColumnBottomSixthRowLeftUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSixthRowLeftUnits.width = (int)(paramsLinearLayoutRightColumnBottomSixthRow.width*0.08);
					linearLayoutRightColumnBottomSixthRowLeftUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomSixthRowLeftUnits);
						TextView textViewRightColumnBottomSixthRowLeftUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSixthRowLeftUnits);
					LinearLayout linearLayoutRightColumnBottomSixthRowRightLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSixthRowRightLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSixthRowRightLabel = linearLayoutRightColumnBottomSixthRowRightLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSixthRowRightLabel.width = (int)(paramsLinearLayoutRightColumnBottomSixthRow.width*0.30);
					linearLayoutRightColumnBottomSixthRowRightLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomSixthRowRightLabel);
						TextView textViewRightColumnBottomSixthRowRightLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSixthRowRightLabel);
					LinearLayout linearLayoutRightColumnBottomSixthRowRightValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSixthRowRightValue);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSixthRowRightValue = linearLayoutRightColumnBottomSixthRowRightValue.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSixthRowRightValue.width = (int)(paramsLinearLayoutRightColumnBottomSixthRow.width * 0.15);
					linearLayoutRightColumnBottomSixthRowRightValue.setLayoutParams(paramsLinearLayoutRightColumnBottomSixthRowRightValue);
						TextView textViewRightColumnBottomSixthRowRightValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSixthRowRightValue);
					LinearLayout linearLayoutRightColumnBottomSixthRowRightUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSixthRowRightUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSixthRowRightUnits = linearLayoutRightColumnBottomSixthRowRightUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSixthRowRightUnits.width = (int)(paramsLinearLayoutRightColumnBottomSixthRow.width*0.05);
					linearLayoutRightColumnBottomSixthRowRightUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomSixthRowRightUnits);
						TextView textViewRightColumnBottomSixthRowRightUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSixthRowRightUnits);

				LinearLayout linearLayoutRightColumnBottomSeventhRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSeventhRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSeventhRow = linearLayoutRightColumnBottomSeventhRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomSeventhRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomSeventhRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomSeventhRow.setLayoutParams(paramsLinearLayoutRightColumnBottomSeventhRow);
					LinearLayout linearLayoutRightColumnBottomSeventhRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSeventhRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSeventhRowLeftLabel = linearLayoutRightColumnBottomSeventhRowLeftLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSeventhRowLeftLabel.width = (int)(paramsLinearLayoutRightColumnBottomSeventhRow.width*0.3);
					linearLayoutRightColumnBottomSeventhRowLeftLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomSeventhRowLeftLabel);
						TextView textViewRightColumnBottomSeventhRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSeventhRowLeftLabel);
					LinearLayout linearLayoutRightColumnBottomSeventhRowLeftValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSeventhRowLeftValue);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSeventhRowLeftValue = linearLayoutRightColumnBottomSeventhRowLeftValue.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSeventhRowLeftValue.width = (int)(paramsLinearLayoutRightColumnBottomSeventhRow.width * 0.12);
					linearLayoutRightColumnBottomSeventhRowLeftValue.setLayoutParams(paramsLinearLayoutRightColumnBottomSeventhRowLeftValue);
						TextView textViewRightColumnBottomSeventhRowLeftValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSeventhRowLeftValue);
					LinearLayout linearLayoutRightColumnBottomSeventhRowLeftUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSeventhRowLeftUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSeventhRowLeftUnits = linearLayoutRightColumnBottomSeventhRowLeftUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSeventhRowLeftUnits.width = (int)(paramsLinearLayoutRightColumnBottomSeventhRow.width*0.08);
					linearLayoutRightColumnBottomSeventhRowLeftUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomSeventhRowLeftUnits);
						TextView textViewRightColumnBottomSeventhRowLeftUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSeventhRowLeftUnits);
					LinearLayout linearLayoutRightColumnBottomSeventhRowRightLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSeventhRowRightLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSeventhRowRightLabel = linearLayoutRightColumnBottomSeventhRowRightLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSeventhRowRightLabel.width = (int)(paramsLinearLayoutRightColumnBottomSeventhRow.width*0.30);
					linearLayoutRightColumnBottomSeventhRowRightLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomSeventhRowRightLabel);
						TextView textViewRightColumnBottomSeventhRowRightLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSeventhRowRightLabel);
					LinearLayout linearLayoutRightColumnBottomSeventhRowRightValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSeventhRowRightValue);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSeventhRowRightValue = linearLayoutRightColumnBottomSeventhRowRightValue.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSeventhRowRightValue.width = (int)(paramsLinearLayoutRightColumnBottomSeventhRow.width * 0.15);
					linearLayoutRightColumnBottomSeventhRowRightValue.setLayoutParams(paramsLinearLayoutRightColumnBottomSeventhRowRightValue);
						TextView textViewRightColumnBottomSeventhRowRightValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSeventhRowRightValue);
					LinearLayout linearLayoutRightColumnBottomSeventhRowRightUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomSeventhRowRightUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomSeventhRowRightUnits = linearLayoutRightColumnBottomSeventhRowRightUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomSeventhRowRightUnits.width = (int)(paramsLinearLayoutRightColumnBottomSeventhRow.width*0.05);
					linearLayoutRightColumnBottomSeventhRowRightUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomSeventhRowRightUnits);
						TextView textViewRightColumnBottomSeventhRowRightUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomSeventhRowRightUnits);

				LinearLayout linearLayoutRightColumnBottomEighthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomEighthRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomEighthRow = linearLayoutRightColumnBottomEighthRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomEighthRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomEighthRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomEighthRow.setLayoutParams(paramsLinearLayoutRightColumnBottomEighthRow);
					LinearLayout linearLayoutRightColumnBottomEighthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomEighthRowLeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomEighthRowLeftLabel = linearLayoutRightColumnBottomEighthRowLeftLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomEighthRowLeftLabel.width = (int)(paramsLinearLayoutRightColumnBottomEighthRow.width*0.3);
					linearLayoutRightColumnBottomEighthRowLeftLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomEighthRowLeftLabel);
						TextView textViewRightColumnBottomEighthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomEighthRowLeftLabel);
					LinearLayout linearLayoutRightColumnBottomEighthRowLeftValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomEighthRowLeftValue);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomEighthRowLeftValue = linearLayoutRightColumnBottomEighthRowLeftValue.getLayoutParams();
					paramsLinearLayoutRightColumnBottomEighthRowLeftValue.width = (int)(paramsLinearLayoutRightColumnBottomEighthRow.width * 0.12);
					linearLayoutRightColumnBottomEighthRowLeftValue.setLayoutParams(paramsLinearLayoutRightColumnBottomEighthRowLeftValue);
						TextView textViewRightColumnBottomEighthRowLeftValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomEighthRowLeftValue);
					LinearLayout linearLayoutRightColumnBottomEighthRowLeftUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomEighthRowLeftUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomEighthRowLeftUnits = linearLayoutRightColumnBottomEighthRowLeftUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomEighthRowLeftUnits.width = (int)(paramsLinearLayoutRightColumnBottomEighthRow.width*0.08);
					linearLayoutRightColumnBottomEighthRowLeftUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomEighthRowLeftUnits);
						TextView textViewRightColumnBottomEighthRowLeftUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomEighthRowLeftUnits);
					LinearLayout linearLayoutRightColumnBottomEighthRowRightLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomEighthRowRightLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomEighthRowRightLabel = linearLayoutRightColumnBottomEighthRowRightLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomEighthRowRightLabel.width = (int)(paramsLinearLayoutRightColumnBottomEighthRow.width*0.30);
					linearLayoutRightColumnBottomEighthRowRightLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomEighthRowRightLabel);
						TextView textViewRightColumnBottomEighthRowRightLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomEighthRowRightLabel);
					LinearLayout linearLayoutRightColumnBottomEighthRowRightValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomEighthRowRightValue);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomEighthRowRightValue = linearLayoutRightColumnBottomEighthRowRightValue.getLayoutParams();
					paramsLinearLayoutRightColumnBottomEighthRowRightValue.width = (int)(paramsLinearLayoutRightColumnBottomEighthRow.width * 0.15);
					linearLayoutRightColumnBottomEighthRowRightValue.setLayoutParams(paramsLinearLayoutRightColumnBottomEighthRowRightValue);
						TextView textViewRightColumnBottomEighthRowRightValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomEighthRowRightValue);
					LinearLayout linearLayoutRightColumnBottomEighthRowRightUnits = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomEighthRowRightUnits);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomEighthRowRightUnits = linearLayoutRightColumnBottomEighthRowRightUnits.getLayoutParams();
					paramsLinearLayoutRightColumnBottomEighthRowRightUnits.width = (int)(paramsLinearLayoutRightColumnBottomEighthRow.width*0.05);
					linearLayoutRightColumnBottomEighthRowRightUnits.setLayoutParams(paramsLinearLayoutRightColumnBottomEighthRowRightUnits);
						TextView textViewRightColumnBottomEighthRowRightUnits = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomEighthRowRightUnits);

				LinearLayout linearLayoutRightColumnBottomNinethRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomNinethRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomNinethRow = linearLayoutRightColumnBottomNinethRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomNinethRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomNinethRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomNinethRow.setLayoutParams(paramsLinearLayoutRightColumnBottomNinethRow);
					LinearLayout linearLayoutRightColumnBottomNinethRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomNinethRowLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomNinethRowLabel = linearLayoutRightColumnBottomNinethRowLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomNinethRowLabel.width = (int)(paramsLinearLayoutRightColumnBottomNinethRow.width*0.3);
					linearLayoutRightColumnBottomNinethRowLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomNinethRowLabel);
						TextView textViewRightColumnBottomNinethRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomNinethRowLabel);
					LinearLayout linearLayoutRightColumnBottomNinethRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomNinethRowValue);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomNinethRowValue = linearLayoutRightColumnBottomNinethRowValue.getLayoutParams();
					paramsLinearLayoutRightColumnBottomNinethRowValue.width = (int)(paramsLinearLayoutRightColumnBottomNinethRow.width * 0.5);
					linearLayoutRightColumnBottomNinethRowValue.setLayoutParams(paramsLinearLayoutRightColumnBottomNinethRowValue);
						TextView textViewRightColumnBottomNinethRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomNinethRowValue);

				LinearLayout linearLayoutRightColumnBottomTenthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomTenthRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomTenthRow = linearLayoutRightColumnBottomTenthRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomTenthRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomTenthRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomTenthRow.setLayoutParams(paramsLinearLayoutLeftColumnTenthRow);
					LinearLayout linearLayoutRightColumnBottomTenthRowLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomTenthRowLabel);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomTenthRowLabel = linearLayoutRightColumnBottomTenthRowLabel.getLayoutParams();
					paramsLinearLayoutRightColumnBottomTenthRowLabel.width = (int)(paramsLinearLayoutRightColumnBottomTenthRow.width*0.3);
					linearLayoutRightColumnBottomTenthRowLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomTenthRowLabel);
						TextView textViewRightColumnBottomTenthRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomTenthRowLabel);

				LinearLayout linearLayoutRightColumnBottomEleventhRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomEleventhRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomEleventhRow = linearLayoutRightColumnBottomEleventhRow.getLayoutParams();
				paramsLinearLayoutRightColumnBottomEleventhRow.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnBottomEleventhRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
				linearLayoutRightColumnBottomEleventhRow.setLayoutParams(paramsLinearLayoutLeftColumnEleventhRow);
					TextView textViewRightColumnBottomEleventhRowLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomEleventhRowLabel);

			LinearLayout linearLayoutRightColumnBottomTwelfthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomTwelfthRow);
			ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomTwelfthRow = linearLayoutRightColumnBottomTwelfthRow.getLayoutParams();
			paramsLinearLayoutRightColumnBottomTwelfthRow.width = paramsLinearLayoutRightColumn.width;
			paramsLinearLayoutRightColumnBottomTwelfthRow.height = (int)(paramsLinearLayoutRightColumn.height * HEIGHT_ROW_RIGHT_COLUMN);
			linearLayoutRightColumnBottomTwelfthRow.setLayoutParams(paramsLinearLayoutLeftColumnTwelfthRow);
				LinearLayout linearLayoutRightColumnBottomTwelfthRowLeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomTwelfthRowLeftLabel);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomTwelfthRowLeftLabel = linearLayoutRightColumnBottomTwelfthRowLeftLabel.getLayoutParams();
				paramsLinearLayoutRightColumnBottomTwelfthRowLeftLabel.width = (int)(paramsLinearLayoutRightColumnBottomTwelfthRow.width*0.25);
				linearLayoutRightColumnBottomTwelfthRowLeftLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomTwelfthRowLeftLabel);
					TextView textViewRightColumnBottomTwelfthRowLeftLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomTwelfthRowLeftLabel);
				LinearLayout linearLayoutRightColumnBottomTwelfthRowValue = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomTwelfthRowValue);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomTwelfthRowValue = linearLayoutRightColumnBottomTwelfthRowValue.getLayoutParams();
				paramsLinearLayoutRightColumnBottomTwelfthRowValue.width = (int)(paramsLinearLayoutRightColumnBottomTwelfthRow.width * 0.20);
				linearLayoutRightColumnBottomTwelfthRowValue.setLayoutParams(paramsLinearLayoutRightColumnBottomTwelfthRowValue);
					TextView textViewRightColumnBottomTwelfthRowValue = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomTwelfthRowValue);
				LinearLayout linearLayoutRightColumnBottomTwelfthRowRightLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomTwelfthRowRightLabel);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomTwelfthRowRightLabel = linearLayoutRightColumnBottomTwelfthRowRightLabel.getLayoutParams();
				paramsLinearLayoutRightColumnBottomTwelfthRowRightLabel.width = (int)(paramsLinearLayoutRightColumnBottomTwelfthRow.width*0.35);
				linearLayoutRightColumnBottomTwelfthRowRightLabel.setLayoutParams(paramsLinearLayoutRightColumnBottomTwelfthRowRightLabel);
					TextView textViewRightColumnBottomTwelfthRowRightLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnBottomTwelfthRowRightLabel);
				LinearLayout linearLayoutRightColumnBottomTwelfthRowButton = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnBottomTwelfthRowButton);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnBottomTwelfthRowButton = linearLayoutRightColumnBottomTwelfthRowButton.getLayoutParams();
				paramsLinearLayoutRightColumnBottomTwelfthRowButton.width = (int)(paramsLinearLayoutRightColumnBottomTwelfthRow.width * 0.20);
				linearLayoutRightColumnBottomTwelfthRowButton.setLayoutParams(paramsLinearLayoutRightColumnBottomTwelfthRowButton);
					Button buttonRightColumnBottomTwelfthRowButton = (Button) rootView.findViewById(R.id.buttonRightColumnBottomTwelfthRowButton);
// -END RIGHT COLUMN

// -BUTTONS
		// Linear layout "linearLayoutButtons"
		LinearLayout linearLayoutButtons = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtons);
		ViewGroup.LayoutParams paramsLinearLayoutButtons = linearLayoutButtons.getLayoutParams();
		paramsLinearLayoutButtons.width = Globals.widthScreen;
		paramsLinearLayoutButtons.height = (int)(Globals.heightScreen * 0.1);
		linearLayoutButtons.setLayoutParams(paramsLinearLayoutButtons);

			LinearLayout linearLayoutButtonSetup = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonSetup);
			ViewGroup.LayoutParams paramsLinearLayoutButtonSetup = linearLayoutButtonSetup.getLayoutParams();
			paramsLinearLayoutButtonSetup.width = paramsLinearLayoutButtons.width / 7;
			linearLayoutButtonSetup.setLayoutParams(paramsLinearLayoutButtonSetup);
				Button buttonSetup = (Button) rootView.findViewById(R.id.buttonSetup);
				ViewGroup.LayoutParams paramsButtonSetup = buttonSetup.getLayoutParams();
				paramsButtonSetup.height = (int)(paramsLinearLayoutButtons.height * 0.65);
				buttonSetup.setAlpha(0.5f);
				buttonSetup.setEnabled(false);
				buttonSetup.setLayoutParams(paramsButtonSetup);

			LinearLayout linearLayoutButtonHistory = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonHistory);
			ViewGroup.LayoutParams paramsLinearLayoutButtonHistory = linearLayoutButtonHistory.getLayoutParams();
			paramsLinearLayoutButtonHistory.width = paramsLinearLayoutButtons.width / 7;
			linearLayoutButtonHistory.setLayoutParams(paramsLinearLayoutButtonHistory);
				Button buttonHistory = (Button) rootView.findViewById(R.id.buttonHistory);
				ViewGroup.LayoutParams paramsButtonHistory = buttonHistory.getLayoutParams();
				paramsButtonHistory.height = (int)(paramsLinearLayoutButtons.height * 0.65);
				buttonHistory.setLayoutParams(paramsButtonHistory);

			LinearLayout linearLayoutButtonFuel = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonFuel);
			ViewGroup.LayoutParams paramsLinearLayoutButtonFuel = linearLayoutButtonFuel.getLayoutParams();
			paramsLinearLayoutButtonFuel.width = paramsLinearLayoutButtons.width / 7;
			linearLayoutButtonFuel.setLayoutParams(paramsLinearLayoutButtonFuel);
				Button buttonFuel = (Button) rootView.findViewById(R.id.buttonFuel);
				ViewGroup.LayoutParams paramsButtonFuel = buttonFuel.getLayoutParams();
				paramsButtonFuel.height = (int)(paramsLinearLayoutButtons.height * 0.65);
				buttonFuel.setLayoutParams(paramsButtonFuel);

			LinearLayout linearLayoutButtonMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonMain);
			ViewGroup.LayoutParams paramsLinearLayoutButtonMain = linearLayoutButtonMain.getLayoutParams();
			paramsLinearLayoutButtonMain.width = paramsLinearLayoutButtons.width / 7;
			linearLayoutButtonMain.setLayoutParams(paramsLinearLayoutButtonMain);
				Button buttonMain = (Button) rootView.findViewById(R.id.buttonMain);
				ViewGroup.LayoutParams paramsButtonMain = buttonMain.getLayoutParams();
				paramsButtonMain.height = (int)(paramsLinearLayoutButtons.height * 0.65);
				buttonMain.setLayoutParams(paramsButtonMain);

			LinearLayout linearLayoutButtonSave = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonSave);
			ViewGroup.LayoutParams paramsLinearLayoutButtonSave = linearLayoutButtonSave.getLayoutParams();
			paramsLinearLayoutButtonSave.width = paramsLinearLayoutButtons.width / 7;
			linearLayoutButtonSave.setLayoutParams(paramsLinearLayoutButtonSave);
				Button buttonSave = (Button) rootView.findViewById(R.id.buttonSave);
				ViewGroup.LayoutParams paramsButtonSave = buttonSave.getLayoutParams();
				paramsButtonSave.height = (int)(paramsLinearLayoutButtons.height * 0.65);
				buttonSave.setLayoutParams(paramsButtonSave);

			LinearLayout linearLayoutButtonBacklight = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonBacklight);
			ViewGroup.LayoutParams paramsLinearLayoutButtonBacklight = linearLayoutButtonBacklight.getLayoutParams();
			paramsLinearLayoutButtonBacklight.width = paramsLinearLayoutButtons.width / 7;
			linearLayoutButtonBacklight.setLayoutParams(paramsLinearLayoutButtonBacklight);
				Button buttonBacklight = (Button) rootView.findViewById(R.id.buttonBacklight);
				ViewGroup.LayoutParams paramsButtonBacklight = buttonBacklight.getLayoutParams();
				paramsButtonBacklight.height = (int)(paramsLinearLayoutButtons.height * 0.65);
				buttonBacklight.setLayoutParams(paramsButtonBacklight);

			LinearLayout linearLayoutButtonPower = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonPower);
			ViewGroup.LayoutParams paramsLinearLayoutButtonPower = linearLayoutButtonPower.getLayoutParams();
			paramsLinearLayoutButtonPower.width = paramsLinearLayoutButtons.width / 7;
			linearLayoutButtonPower.setLayoutParams(paramsLinearLayoutButtonPower);
				Button buttonPower = (Button) rootView.findViewById(R.id.buttonPower);
				ViewGroup.LayoutParams paramsButtonPower = buttonPower.getLayoutParams();
				paramsButtonPower.height = (int)(paramsLinearLayoutButtons.height * 0.65);
				buttonPower.setLayoutParams(paramsButtonPower);
// -END BUTTONS
	}

	public void updateAlarmsGUI() {
		commandSQL="SELECT * FROM AlarmParameters WHERE Parameter_AlarmParameters='"+
				spinnerLeftColumnSecondRowSensors.getSelectedItem().toString()+"'";
		cursor = dewLineDB.rawQuery(commandSQL,null);
		if(cursor.getCount()>0) {
			cursor.moveToFirst();
			linearLayoutLeftColumnThirdRowP2TopImage.removeAllViews();
			if((cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
				!cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtIdle_AlarmParameters")).trim().equals("―"))||
			   (cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
				!cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtIdle_AlarmParameters")).trim().equals("―"))) {
				linearLayoutLeftColumnThirdRowP2TopImage.addView(imageViewLeftColumnThirdRowP2TopImage);
				flagAlarmsLowerLimitsExist = true;
			}
			else
				flagAlarmsLowerLimitsExist = false;

			linearLayoutLeftColumnThirdRowP3TopImage.removeAllViews();
			if((cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
				!cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtIdle_AlarmParameters")).trim().equals("?"))||
			   (cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtIdle__AlarmParameters")).trim().length()>0&&
				!cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtIdle__AlarmParameters")).trim().equals("?"))) {
				linearLayoutLeftColumnThirdRowP3TopImage.addView(imageViewLeftColumnThirdRowP3TopImage);
				flagAlarmsUpperLimitsExist = true;
			}
			else
				flagAlarmsUpperLimitsExist = false;

			linearLayoutLeftColumnThirdRowP4TopImage.removeAllViews();
			if(cursor.getString(cursor.getColumnIndex("Dynamic_AlarmParameters")).trim().equals("Yes")) {
				linearLayoutLeftColumnThirdRowP4TopImage.addView(imageViewLeftColumnThirdRowP4TopImage);
				flagAlarmDynamicalExist = true;
			}
			else
				flagAlarmDynamicalExist = false;

			editTextLeftColumnFourthRowP2LeftValue.setText(cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtIdle_AlarmParameters")).trim());
			editTextLeftColumnFourthRowP2RightValue.setText(cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtIdle_AlarmParameters")).trim());
			editTextLeftColumnFourthRowP3LeftValue.setText(cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtIdle_AlarmParameters")).trim());
			editTextLeftColumnFourthRowP3RightValue.setText(cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtIdle_AlarmParameters")).trim());
			editTextLeftColumnFifthRowP2LeftValue.setText(cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtWot_AlarmParameters")).trim());
			editTextLeftColumnFifthRowP2RightValue.setText(cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtWot_AlarmParameters")).trim());
			editTextLeftColumnFifthRowP3LeftValue.setText(cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtWot_AlarmParameters")).trim());
			editTextLeftColumnFifthRowP3RightValue.setText(cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtWot_AlarmParameters")).trim());

			linearLayoutLeftColumnSixthRowP2Image.removeAllViews();
			if((cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
					!cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters")).trim().equals("―"))||
				   (cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtWot_AlarmParameters")).trim().length()>0&&
					!cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtWot_AlarmParameters")).trim().equals("―"))) {
				linearLayoutLeftColumnSixthRowP2Image.addView(imageViewLeftColumnSixthRowP2Image);
				flagAlarmsShutdownLowerLimitsExist = true;
			}
			else
				flagAlarmsShutdownLowerLimitsExist = false;

			linearLayoutLeftColumnSixthRowP3Image.removeAllViews();
			if((cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtIdle_AlarmParameters")).trim().length()>0&&
				!cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtIdle_AlarmParameters")).trim().equals("―"))||
			   (cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtWot_AlarmParameters")).trim().length()>0&&
				!cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtWot_AlarmParameters")).trim().equals("―"))) {
				linearLayoutLeftColumnSixthRowP3Image.addView(imageViewLeftColumnSixthRowP3Image);
				flagAlarmsShutdownUpperLimitsExist = true;
			}
			else
				flagAlarmsShutdownUpperLimitsExist = false;

			linearLayoutLeftColumnSixthRowP4Image.removeAllViews();
			if(cursor.getString(cursor.getColumnIndex("Dynamic_AlarmParameters")).trim().equals("Yes")) {
				linearLayoutLeftColumnSixthRowP4Image.addView(imageViewLeftColumnSixthRowP4Image);
				flagAlarmDynamicalExist = true;
			}
			else
				flagAlarmDynamicalExist = false;
		}

		textViewLeftColumnSeventhRowP2Value.setText(cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters")).trim());
		textViewLeftColumnSeventhRowP3Value.setText(cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtIdle_AlarmParameters")).trim());
		textViewLeftColumnEighthRowP2Value.setText(cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtWot_AlarmParameters")).trim());
		textViewLeftColumnEighthRowP3Value.setText(cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtWot_AlarmParameters")).trim());
	}
}
