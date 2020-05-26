package com.john.dewline;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentHistory extends Fragment {

	View rootView;

	public static final float HEIGHT_ROW_LEFT_PANEL=0.05f,HEIGHT_ROW_RIGHT_PANEL=0.05f;

	private static LineChart lineChartSensorBaselineComparasion,lineChartTrendVsRPM;

	public static File root,dewLineFolder;
	public static SQLiteDatabase dewLineDB;
	public static Cursor cursor = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_history, container,false);


		root = Environment.getExternalStorageDirectory();
		dewLineFolder = new File(root.getAbsolutePath()+"/DEWLine/");

		dewLineDB = Globals.context.openOrCreateDatabase(dewLineFolder+"/DEWLine.db",
				Globals.context.MODE_PRIVATE, null);
		createGUI();
		dewLineDB.close();

		createCharts();

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

		Button buttonSetup = (Button) rootView.findViewById(R.id.buttonSetup);
		buttonSetup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Display the Setup screen
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				FragmentSetup fragmentSetup = new FragmentSetup();
				ft.replace(R.id.fragment,fragmentSetup);
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

		return rootView;
	}

	public void createGUI() {

//- CONTENTS
		// Linear layout "linearLayoutContents"
		LinearLayout linearLayoutContents = (LinearLayout) rootView.findViewById(R.id.linearLayoutContents);
		ViewGroup.LayoutParams paramsLinearLayoutContents = linearLayoutContents.getLayoutParams();
		paramsLinearLayoutContents.width = Globals.widthScreen;
		paramsLinearLayoutContents.height = (int)(Globals.heightScreen * 0.9);
		linearLayoutContents.setLayoutParams(paramsLinearLayoutContents);
//-LEFT PANEL
		// Linear layout ""linearLayoutLeftPanel"
		LinearLayout linearLayoutLeftPanel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanel);
		ViewGroup.LayoutParams paramsLinearLayoutLeftPanel = linearLayoutLeftPanel.getLayoutParams();
		paramsLinearLayoutLeftPanel.width = (int)(paramsLinearLayoutContents.width * 0.4);
		paramsLinearLayoutLeftPanel.height = paramsLinearLayoutContents.height;
		linearLayoutLeftPanel.setLayoutParams(paramsLinearLayoutLeftPanel);

			LinearLayout linearLayoutLeftPanelFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFirstRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFirstRow = linearLayoutLeftPanelFirstRow.getLayoutParams();
			paramsLinearLayoutLeftPanelFirstRow.width = paramsLinearLayoutLeftPanel.width;
			paramsLinearLayoutLeftPanelFirstRow.height = (int)(paramsLinearLayoutLeftPanel.height * HEIGHT_ROW_LEFT_PANEL);
			linearLayoutLeftPanelFirstRow.setLayoutParams(paramsLinearLayoutLeftPanelFirstRow);

				LinearLayout linearLayoutLeftPanelFirstRowP1Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFirstRowP1Label);
				ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFirstRowP1Label = linearLayoutLeftPanelFirstRowP1Label.getLayoutParams();
				paramsLinearLayoutLeftPanelFirstRowP1Label.width = (int)(paramsLinearLayoutLeftPanelFirstRow.width * 0.55);
				paramsLinearLayoutLeftPanelFirstRowP1Label.height = paramsLinearLayoutLeftPanelFirstRow.height;
				linearLayoutLeftPanelFirstRowP1Label.setLayoutParams(paramsLinearLayoutLeftPanelFirstRowP1Label);
					TextView textViewtLeftPanelFirstRowP1Label = (TextView) rootView.findViewById(R.id.textViewtLeftPanelFirstRowP1Label);

				LinearLayout linearLayoutLeftColumnFirstRowP2Check = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnFirstRowP2Check);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnFirstRowP2Check = linearLayoutLeftColumnFirstRowP2Check.getLayoutParams();
				paramsLinearLayoutLeftColumnFirstRowP2Check.width = (int)(paramsLinearLayoutLeftPanelFirstRow.width * 0.15);
				paramsLinearLayoutLeftColumnFirstRowP2Check.height = paramsLinearLayoutLeftPanelFirstRow.height;
				linearLayoutLeftColumnFirstRowP2Check.setLayoutParams(paramsLinearLayoutLeftColumnFirstRowP2Check);

					LinearLayout linearLayoutLeftPanelFirstRowP2CheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFirstRowP2CheckInside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFirstRowP2CheckInside = linearLayoutLeftPanelFirstRowP2CheckInside.getLayoutParams();
					paramsLinearLayoutLeftPanelFirstRowP2CheckInside.width = (int)(paramsLinearLayoutLeftPanelFirstRow.height * 0.75);
					paramsLinearLayoutLeftPanelFirstRowP2CheckInside.height = (int)(paramsLinearLayoutLeftPanelFirstRow.height * 0.75);
					linearLayoutLeftPanelFirstRowP2CheckInside.setLayoutParams(paramsLinearLayoutLeftPanelFirstRowP2CheckInside);
						ImageView imageViewLeftColumnFifteenthRowLeftCheckInside = (ImageView) rootView.findViewById(R.id.imageViewLeftColumnFifteenthRowLeftCheck);

				LinearLayout linearLayoutLeftPanelFirstRowP3Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFirstRowP3Label);
				ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFirstRowP3Label = linearLayoutLeftPanelFirstRowP3Label.getLayoutParams();
				paramsLinearLayoutLeftPanelFirstRowP3Label.width = (int)(paramsLinearLayoutLeftPanelFirstRow.width*0.3);
				paramsLinearLayoutLeftPanelFirstRowP3Label.height = (int)(paramsLinearLayoutLeftPanelFirstRow.height * 0.75);
				linearLayoutLeftPanelFirstRowP3Label.setLayoutParams(paramsLinearLayoutLeftPanelFirstRowP3Label);
					TextView textViewtLeftPanelFirstRowP3Label = (TextView) rootView.findViewById(R.id.textViewLeftPanelFirstRowP3Label);

			LinearLayout linearLayoutLeftPanelSecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelSecondRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftPanelSecondRow = linearLayoutLeftPanelSecondRow.getLayoutParams();
			paramsLinearLayoutLeftPanelSecondRow.width = paramsLinearLayoutLeftPanel.width;
			paramsLinearLayoutLeftPanelSecondRow.height = (int)(paramsLinearLayoutLeftPanel.height*HEIGHT_ROW_LEFT_PANEL);
			linearLayoutLeftPanelSecondRow.setLayoutParams(paramsLinearLayoutLeftPanelSecondRow);

				LinearLayout linearLayoutLeftPanelSecondRowP1Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelSecondRowP1Label);
				ViewGroup.LayoutParams paramsLinearLayoutLeftPanelSecondRowP1Label = linearLayoutLeftPanelSecondRowP1Label.getLayoutParams();
				paramsLinearLayoutLeftPanelSecondRowP1Label.width = (int)(paramsLinearLayoutLeftPanelSecondRow.width*0.55);
				linearLayoutLeftPanelSecondRowP1Label.setLayoutParams(paramsLinearLayoutLeftPanelSecondRowP1Label);
					TextView textViewtLeftPanelSecondRowP1Label = (TextView) rootView.findViewById(R.id.textViewtLeftPanelSecondRowP1Label);

				LinearLayout linearLayoutLeftColumnSecondRowP2Check = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnSecondRowP2Check);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnSecondRowP2Check = linearLayoutLeftColumnSecondRowP2Check.getLayoutParams();
				paramsLinearLayoutLeftColumnSecondRowP2Check.width = (int)(paramsLinearLayoutLeftPanelSecondRow.width*0.15);
				paramsLinearLayoutLeftColumnSecondRowP2Check.height = paramsLinearLayoutLeftPanelSecondRow.height;
				linearLayoutLeftColumnSecondRowP2Check.setLayoutParams(paramsLinearLayoutLeftColumnSecondRowP2Check);

					LinearLayout linearLayoutLeftPanelSecondRowP2CheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelSecondRowP2CheckInside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftPanelSecondRowP2CheckInside = linearLayoutLeftPanelSecondRowP2CheckInside.getLayoutParams();
					paramsLinearLayoutLeftPanelSecondRowP2CheckInside.width = (int)(paramsLinearLayoutLeftPanelSecondRow.height * 0.75);
					paramsLinearLayoutLeftPanelSecondRowP2CheckInside.height = (int)(paramsLinearLayoutLeftPanelSecondRow.height * 0.75);
					linearLayoutLeftPanelSecondRowP2CheckInside.setLayoutParams(paramsLinearLayoutLeftPanelSecondRowP2CheckInside);
						ImageView imageViewLeftPanelSecondRowP2Check = (ImageView) rootView.findViewById(R.id.imageViewLeftPanelSecondRowP2Check);

				LinearLayout linearLayoutLeftPanelSecondRowP3Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelSecondRowP3Label);
				ViewGroup.LayoutParams paramsLinearLayoutLeftPanelSecondRowP3Label = linearLayoutLeftPanelSecondRowP3Label.getLayoutParams();
				paramsLinearLayoutLeftPanelSecondRowP3Label.width = (int)(paramsLinearLayoutLeftPanelSecondRow.width*0.3);
				linearLayoutLeftPanelSecondRowP3Label.setLayoutParams(paramsLinearLayoutLeftPanelSecondRowP3Label);
					TextView textViewtLeftPanelSecondRowP3Label = (TextView) rootView.findViewById(R.id.textViewLeftPanelSecondRowP3Label);

			LinearLayout linearLayoutLeftPanelThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelThirdRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftPanelThirdRow = linearLayoutLeftPanelThirdRow.getLayoutParams();
			paramsLinearLayoutLeftPanelThirdRow.width = paramsLinearLayoutLeftPanel.width;
			paramsLinearLayoutLeftPanelThirdRow.height = (int)(paramsLinearLayoutLeftPanel.height*HEIGHT_ROW_LEFT_PANEL);
			linearLayoutLeftPanelThirdRow.setLayoutParams(paramsLinearLayoutLeftPanelThirdRow);

				LinearLayout linearLayoutLeftPanelThirdRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelThirdRowP1);
				ViewGroup.LayoutParams paramsLinearLayoutLeftPanelThirdRowP1 = linearLayoutLeftPanelThirdRowP1.getLayoutParams();
				paramsLinearLayoutLeftPanelThirdRowP1.width = (int)(paramsLinearLayoutLeftPanelThirdRow.width*0.55);
				paramsLinearLayoutLeftPanelThirdRowP1.height = (int)(paramsLinearLayoutLeftPanel.height*HEIGHT_ROW_LEFT_PANEL);
				linearLayoutLeftPanelThirdRowP1.setLayoutParams(paramsLinearLayoutLeftPanelFirstRowP1Label);
					LinearLayout linearLayoutLeftPanelThirdRowP1LeftLabel = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelThirdRowP1LeftLabel);
					ViewGroup.LayoutParams paramsLinearLayoutLeftPanelThirdRowP1LeftLabel = linearLayoutLeftPanelThirdRowP1LeftLabel.getLayoutParams();
					paramsLinearLayoutLeftPanelThirdRowP1LeftLabel.width = (int)(paramsLinearLayoutLeftPanelThirdRowP1.width*0.35);
					paramsLinearLayoutLeftPanelThirdRowP1LeftLabel.height = paramsLinearLayoutLeftPanelThirdRowP1.height;
					linearLayoutLeftPanelThirdRowP1LeftLabel.setLayoutParams(paramsLinearLayoutLeftPanelThirdRowP1LeftLabel);
						TextView textViewLeftPanelThirdRowP1LeftLabel = (TextView) rootView.findViewById(R.id.textViewLeftPanelThirdRowP1LeftLabel);

					LinearLayout linearLayoutLeftPanelThirdRowP1RightSpinner = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelThirdRowP1RightSpinner);
					ViewGroup.LayoutParams paramsLinearLayoutLeftPanelThirdRowP1RightSpinner = linearLayoutLeftPanelThirdRowP1RightSpinner.getLayoutParams();
					paramsLinearLayoutLeftPanelThirdRowP1RightSpinner.width = (int)(paramsLinearLayoutLeftPanelThirdRowP1.width*0.5);
					paramsLinearLayoutLeftPanelThirdRowP1RightSpinner.height = paramsLinearLayoutLeftPanelThirdRowP1.height;
					linearLayoutLeftPanelThirdRowP1RightSpinner.setLayoutParams(paramsLinearLayoutLeftPanelThirdRowP1RightSpinner);
						Spinner spinnerLeftPanelThirdRowP1RightSpinnerPeriod = (Spinner) rootView.findViewById(R.id.spinnerLeftPanelThirdRowP1RightSpinnerPeriod);
						List<String> temp = new ArrayList<String>();
						temp.clear();
						temp.add("Week");
						String[] period = new String[temp.size()];
						temp.toArray(period);
						ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Globals.context,R.layout.spinner_item,period);
						arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
						spinnerLeftPanelThirdRowP1RightSpinnerPeriod.setAdapter(arrayAdapter);
						spinnerLeftPanelThirdRowP1RightSpinnerPeriod.setSelection(0);

				LinearLayout linearLayoutLeftColumnThirdRowP2Check = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumnThirdRowP2Check);
				ViewGroup.LayoutParams paramsLinearLayoutLeftColumnThirdRowP2Check = linearLayoutLeftColumnThirdRowP2Check.getLayoutParams();
				paramsLinearLayoutLeftColumnThirdRowP2Check.width = (int)(paramsLinearLayoutLeftPanelThirdRow.width*0.15);
				paramsLinearLayoutLeftColumnThirdRowP2Check.height = paramsLinearLayoutLeftPanelThirdRow.height;
				linearLayoutLeftColumnThirdRowP2Check.setLayoutParams(paramsLinearLayoutLeftColumnThirdRowP2Check);

					LinearLayout linearLayoutLeftPanelThirdRowP2CheckInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelThirdRowP2CheckInside);
					ViewGroup.LayoutParams paramsLinearLayoutLeftPanelThirdRowP2CheckInside = linearLayoutLeftPanelThirdRowP2CheckInside.getLayoutParams();
					paramsLinearLayoutLeftPanelThirdRowP2CheckInside.width = (int)(paramsLinearLayoutLeftPanelThirdRow.height * 0.75);
					paramsLinearLayoutLeftPanelThirdRowP2CheckInside.height = (int)(paramsLinearLayoutLeftPanelThirdRow.height * 0.75);
					linearLayoutLeftPanelThirdRowP2CheckInside.setLayoutParams(paramsLinearLayoutLeftPanelThirdRowP2CheckInside);
						ImageView imageViewLeftPanelThirdRowP2Check = (ImageView) rootView.findViewById(R.id.imageViewLeftPanelThirdRowP2Check);

				LinearLayout linearLayoutLeftPanelThirdRowP3Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelThirdRowP3Label);
				ViewGroup.LayoutParams paramsLinearLayoutLeftPanelThirdRowP3Label = linearLayoutLeftPanelThirdRowP3Label.getLayoutParams();
				paramsLinearLayoutLeftPanelThirdRowP3Label.width = (int)(paramsLinearLayoutLeftPanelThirdRow.width*0.3);
				linearLayoutLeftPanelThirdRowP3Label.setLayoutParams(paramsLinearLayoutLeftPanelThirdRowP3Label);
					TextView textViewtLeftPanelThirdRowP3Label = (TextView) rootView.findViewById(R.id.textViewLeftPanelThirdRowP3Label);

			LinearLayout linearLayoutLeftPanelFourthFifthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFourthFifthRow);
			ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFourthFifthRow = linearLayoutLeftPanelFourthFifthRow.getLayoutParams();
			paramsLinearLayoutLeftPanelFourthFifthRow.width = paramsLinearLayoutLeftPanel.width;
			paramsLinearLayoutLeftPanelFourthFifthRow.height = (int)(paramsLinearLayoutLeftPanel.height*HEIGHT_ROW_LEFT_PANEL * 2);
			linearLayoutLeftPanelFourthFifthRow.setLayoutParams(paramsLinearLayoutLeftPanelFourthFifthRow);
				LinearLayout linearLayoutLeftPanelFourthFifthRowLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFourthFifthRowLeft);
				ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFourthFifthRowLeft = linearLayoutLeftPanelFourthFifthRowLeft.getLayoutParams();
				paramsLinearLayoutLeftPanelFourthFifthRowLeft.width = (int)(paramsLinearLayoutLeftPanel.width * 0.7);
				linearLayoutLeftPanelFourthFifthRowLeft.setLayoutParams(paramsLinearLayoutLeftPanelFourthFifthRowLeft);
					LinearLayout linearLayoutLeftPanelFourthFifthRowLeftFifthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFourthFifthRowLeftFifthRow);
					ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRow = linearLayoutLeftPanelFourthFifthRowLeftFifthRow.getLayoutParams();
					paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRow.width = paramsLinearLayoutLeftPanelFourthFifthRowLeft.width;
					paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRow.height = (int)(paramsLinearLayoutLeftPanel.height*HEIGHT_ROW_LEFT_PANEL);
					linearLayoutLeftPanelFourthFifthRowLeftFifthRow.setLayoutParams(paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRow);
						LinearLayout linearLayoutLeftPanelFourthFifthRowLeftFifthRowP1Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFourthFifthRowLeftFifthRowP1Label);
						ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP1Label = linearLayoutLeftPanelFourthFifthRowLeftFifthRowP1Label.getLayoutParams();
						paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP1Label.width = (int)(paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRow.width * 0.4);
						paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP1Label.height = paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRow.height;
						linearLayoutLeftPanelFourthFifthRowLeftFifthRowP1Label.setLayoutParams(paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP1Label);
							TextView textViewLeftPanelFourthFifthRowLeftFifthRowP1Label = (TextView) rootView.findViewById(R.id.textViewLeftPanelFourthFifthRowLeftFifthRowP1Label);
						LinearLayout linearLayoutLeftPanelFourthFifthRowLeftFifthRowP2Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFourthFifthRowLeftFifthRowP2Value);
						ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP2Value = linearLayoutLeftPanelFourthFifthRowLeftFifthRowP2Value.getLayoutParams();
						paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP2Value.width = (int)(paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRow.width*0.2 - 10);
						linearLayoutLeftPanelFourthFifthRowLeftFifthRowP2Value.setLayoutParams(paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP2Value);
							EditText editTextLeftPanelFourthFifthRowLeftFifthRowP2Value = (EditText) rootView.findViewById(R.id.editTextLeftPanelFourthFifthRowLeftFifthRowP2Value);
						LinearLayout linearLayoutLeftPanelFourthFifthRowLeftFifthRowP3Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFourthFifthRowLeftFifthRowP3Value);
						ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP3Value = linearLayoutLeftPanelFourthFifthRowLeftFifthRowP3Value.getLayoutParams();
						paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP3Value.width = (int)(paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRow.width*0.2 - 10);
						linearLayoutLeftPanelFourthFifthRowLeftFifthRowP3Value.setLayoutParams(paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP3Value);
							EditText editTextLeftPanelFourthFifthRowLeftFifthRowP3Value = (EditText) rootView.findViewById(R.id.editTextLeftPanelFourthFifthRowLeftFifthRowP3Value);
						LinearLayout linearLayoutLeftPanelFourthFifthRowLeftFifthRowP4Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFourthFifthRowLeftFifthRowP4Value);
						ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP4Value = linearLayoutLeftPanelFourthFifthRowLeftFifthRowP4Value.getLayoutParams();
						paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP4Value.width = (int)(paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRow.width*0.2 - 10);
						linearLayoutLeftPanelFourthFifthRowLeftFifthRowP4Value.setLayoutParams(paramsLinearLayoutLeftPanelFourthFifthRowLeftFifthRowP4Value);
							EditText editTextLeftPanelFourthFifthRowLeftFifthRowP4Value = (EditText) rootView.findViewById(R.id.editTextLeftPanelFourthFifthRowLeftFifthRowP4Value);

					LinearLayout linearLayoutLeftPanelFourthFifthRowRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFourthFifthRowRight);
					ViewGroup.LayoutParams paramsLinearLayoutLeftPanelFourthFifthRowRight = linearLayoutLeftPanelFourthFifthRowRight.getLayoutParams();
					paramsLinearLayoutLeftPanelFourthFifthRowRight.width = (int)(paramsLinearLayoutLeftPanel.width * 0.3);
					linearLayoutLeftPanelFourthFifthRowRight.setLayoutParams(paramsLinearLayoutLeftPanelFourthFifthRowRight);
						LinearLayout linearLayoutLeftPanelFourthFifthRowRightButtonInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelFourthFifthRowRightButtonInside);
						ViewGroup.LayoutParams paramsLinearLinearLayoutLeftPanelFourthFifthRowRightButtonInside = linearLayoutLeftPanelFourthFifthRowRightButtonInside.getLayoutParams();
						paramsLinearLinearLayoutLeftPanelFourthFifthRowRightButtonInside.width = (int)(paramsLinearLayoutLeftPanelFourthFifthRowRight.width * 0.75);
						paramsLinearLinearLayoutLeftPanelFourthFifthRowRightButtonInside.height = (int)(paramsLinearLayoutLeftPanel.height*HEIGHT_ROW_LEFT_PANEL * 1.5);
						linearLayoutLeftPanelFourthFifthRowRightButtonInside.setLayoutParams(paramsLinearLinearLayoutLeftPanelFourthFifthRowRightButtonInside);

			LinearLayout linearLayoutLeftPanelRestRows = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelRestRows);
			ViewGroup.LayoutParams paramsLinearLayoutLeftPanelRestRows = linearLayoutLeftPanelRestRows.getLayoutParams();
			paramsLinearLayoutLeftPanelRestRows.width = paramsLinearLayoutLeftPanel.width;
			paramsLinearLayoutLeftPanelRestRows.height = (int)(paramsLinearLayoutLeftPanel.height*HEIGHT_ROW_LEFT_PANEL * 14);
			linearLayoutLeftPanelRestRows.setLayoutParams(paramsLinearLayoutLeftPanelRestRows);
				LinearLayout linearLayoutLeftPanelRestRowsInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftPanelRestRowsInside);
				ViewGroup.LayoutParams paramsLinearLayoutLeftPanelRestRowsInside = linearLayoutLeftPanelRestRowsInside.getLayoutParams();
				paramsLinearLayoutLeftPanelRestRowsInside.width = (int)(paramsLinearLayoutLeftPanelRestRows.width* 0.9);
				paramsLinearLayoutLeftPanelRestRowsInside.height = paramsLinearLayoutLeftPanelRestRows.height;
				linearLayoutLeftPanelRestRowsInside.setLayoutParams(paramsLinearLayoutLeftPanelRestRowsInside);
					ScrollView scrollViewLeftPanelRestRowsInside = (ScrollView) rootView.findViewById(R.id.scrollViewLeftPanelRestRowsInside);
					TextView textViewLeftPanelRestRowsMemo = (TextView) rootView.findViewById(R.id.textViewLeftPanelRestRowsMemo);
// END LEFT PANEL
// RIGHT PANEL
		// Linear layout "linearLayoutRightPanel"
		LinearLayout linearLayoutRightPanel = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanel);
		ViewGroup.LayoutParams paramsLinearLayoutRightPanel = linearLayoutRightPanel.getLayoutParams();
		paramsLinearLayoutRightPanel.width = (int)(paramsLinearLayoutContents.width * 0.6);
		paramsLinearLayoutRightPanel.height = paramsLinearLayoutContents.height;
		linearLayoutRightPanel.setLayoutParams(paramsLinearLayoutRightPanel);

			LinearLayout linearLayoutRightPanelTop = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTop);
			ViewGroup.LayoutParams paramsLinearLayoutRightPanelTop = linearLayoutRightPanelTop.getLayoutParams();
			paramsLinearLayoutRightPanelTop.width = paramsLinearLayoutRightPanel.width;
			paramsLinearLayoutRightPanelTop.height = paramsLinearLayoutRightPanel.height / 2;
			linearLayoutRightPanelTop.setLayoutParams(paramsLinearLayoutRightPanelTop);

				LinearLayout linearLayoutRightPanelTopFirstThirdRows = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRows);
				ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRows = linearLayoutRightPanelTopFirstThirdRows.getLayoutParams();
				paramsLinearLayoutRightPanelTopFirstThirdRows.width = paramsLinearLayoutRightPanel.width;
				paramsLinearLayoutRightPanelTopFirstThirdRows.height = (int)(paramsLinearLayoutRightPanel.height * HEIGHT_ROW_RIGHT_PANEL * 3 + 13);
				linearLayoutRightPanelTopFirstThirdRows.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRows);

					LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeft);
					ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsLeft = linearLayoutRightPanelTopFirstThirdRowsLeft.getLayoutParams();
					paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width = (int)(paramsLinearLayoutRightPanel.width * 0.8);
					paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.height = (int)(paramsLinearLayoutRightPanel.height * HEIGHT_ROW_RIGHT_PANEL * 3 + 3);
					linearLayoutRightPanelTopFirstThirdRowsLeft.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft);

						LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftFirstRow);
						ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsLeftFirstRow = linearLayoutRightPanelTopFirstThirdRowsLeftFirstRow.getLayoutParams();
						paramsLinearLayoutRightPanelTopFirstThirdRowsLeftFirstRow.width = paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width;
						paramsLinearLayoutRightPanelTopFirstThirdRowsLeftFirstRow.height = (int)(paramsLinearLayoutRightPanel.height * HEIGHT_ROW_RIGHT_PANEL);
						linearLayoutRightPanelTopFirstThirdRowsLeftFirstRow.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsLeftFirstRow);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP14Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP14Label);
							ViewGroup.LayoutParams paramslinearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP14Label = linearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP14Label.getLayoutParams();
							paramslinearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP14Label.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width * 0.75);
							linearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP14Label.setLayoutParams(paramslinearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP14Label);
								TextView textViewRightPanelTopFirstThirdRowsLeftFirstRowP14Label = (TextView) rootView.findViewById(R.id.textViewRightPanelTopFirstThirdRowsLeftFirstRowP14Label);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP56Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP56Label);
							ViewGroup.LayoutParams paramslinearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP56Label = linearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP56Label.getLayoutParams();
							paramslinearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP56Label.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width * 0.25);
							linearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP56Label.setLayoutParams(paramslinearLayoutRightPanelTopFirstThirdRowsLeftFirstRowP56Label);
								TextView textViewRightPanelTopFirstThirdRowsLeftFirstRowP56Label = (TextView) rootView.findViewById(R.id.textViewRightPanelTopFirstThirdRowsLeftFirstRowP56Label);

						LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftSecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftSecondRow);
						ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsLeftSecondRow = linearLayoutRightPanelTopFirstThirdRowsLeftSecondRow.getLayoutParams();
						paramsLinearLayoutRightPanelTopFirstThirdRowsLeftSecondRow.width = paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width;
						paramsLinearLayoutRightPanelTopFirstThirdRowsLeftSecondRow.height = (int)(paramsLinearLayoutRightPanel.height * HEIGHT_ROW_RIGHT_PANEL);
						linearLayoutRightPanelTopFirstThirdRowsLeftSecondRow.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsLeftSecondRow);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP1Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP1Label);
							ViewGroup.LayoutParams paramslinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP1Label = linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP1Label.getLayoutParams();
							paramslinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP1Label.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width * 0.26);
							linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP1Label.setLayoutParams(paramslinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP1Label);
								TextView textViewRightPanelTopFirstThirdRowsLeftSecondRowP1Label = (TextView) rootView.findViewById(R.id.textViewRightPanelTopFirstThirdRowsLeftSecondRowP1Label);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP24Spinner = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP24Spinner);
							ViewGroup.LayoutParams paramsLlinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP24Spinner = linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP24Spinner.getLayoutParams();
							paramsLlinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP24Spinner.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width*0.49);
							paramsLlinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP24Spinner.height = paramsLinearLayoutRightPanelTopFirstThirdRowsLeftSecondRow.height - 2;
							linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP24Spinner.setLayoutParams(paramsLlinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP24Spinner);
								Spinner spinnerRightPanelTopFirstThirdRowsLeftSecondRowP24Sensors = (Spinner) rootView.findViewById(R.id.spinnerRightPanelTopFirstThirdRowsLeftSecondRowP24Sensors);

								String commandSQL="SELECT * FROM AlarmParameters";
								cursor = dewLineDB.rawQuery(commandSQL,null);
								List<String> temp2 = new ArrayList<String>();
								if(cursor.getCount()>0) {
									cursor.moveToFirst();
									while (!cursor.isAfterLast()) {
										temp2.add(cursor.getString(cursor.getColumnIndex("Parameter_AlarmParameters")));
										cursor.moveToNext();
									}
								}
								String[] sensors = new String[temp2.size()];
								temp2.toArray(sensors);
								ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(Globals.context,R.layout.spinner_item,sensors);
								arrayAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
								spinnerRightPanelTopFirstThirdRowsLeftSecondRowP24Sensors.setAdapter(arrayAdapter2);
								spinnerRightPanelTopFirstThirdRowsLeftSecondRowP24Sensors.setSelection(0);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP56Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP56Value);
							ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP56Value = linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP56Value.getLayoutParams();
							paramsLinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP56Value.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width * 0.25 - 10);
							linearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP56Value.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsLeftSecondRowP56Value);
								EditText editTextRightPanelTopFirstThirdRowsLeftSecondRowP56Value = (EditText) rootView.findViewById(R.id.editTextRightPanelTopFirstThirdRowsLeftSecondRowP56Value);
								editTextRightPanelTopFirstThirdRowsLeftSecondRowP56Value.setText(android.text.format.DateFormat.
										format("MM-dd-yyyy", new java.util.Date()));

						LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftThirdRow);
						ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRow = linearLayoutRightPanelTopFirstThirdRowsLeftThirdRow.getLayoutParams();
						paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRow.width = paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width;
						paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRow.height = (int)(paramsLinearLayoutRightPanel.height * HEIGHT_ROW_RIGHT_PANEL + 3);
						linearLayoutRightPanelTopFirstThirdRowsLeftThirdRow.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRow);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP1Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP1Label);
							ViewGroup.LayoutParams paramslinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP1Label = linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP1Label.getLayoutParams();
							paramslinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP1Label.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width * 0.26);
							linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP1Label.setLayoutParams(paramslinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP1Label);
								TextView textViewLRightPanelTopFirstThirdRowsLeftThirdRowP1Label = (TextView) rootView.findViewById(R.id.textViewLRightPanelTopFirstThirdRowsLeftThirdRowP1Label);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP2Spinner = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP2Spinner);
							ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP2Spinner = linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP2Spinner.getLayoutParams();
							paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP2Spinner.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width * 0.2);
							paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP2Spinner.height = paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRow.height - 3;
							linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP2Spinner.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP2Spinner);
								Spinner spinnerRightPanelTopFirstThirdRowsLeftThirdRowP2SpinnerPeriod = (Spinner) rootView.findViewById(R.id.spinnerRightPanelTopFirstThirdRowsLeftThirdRowP2SpinnerPeriod);
									List<String> temp3 = new ArrayList<String>();
									temp3.clear();
									temp3.add("Week");
									String[] period3 = new String[temp3.size()];
									temp3.toArray(period3);
									ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(Globals.context,R.layout.spinner_item,period3);
									arrayAdapter3.setDropDownViewResource(R.layout.spinner_dropdown_item);
									spinnerRightPanelTopFirstThirdRowsLeftThirdRowP2SpinnerPeriod.setAdapter(arrayAdapter3);
									spinnerRightPanelTopFirstThirdRowsLeftThirdRowP2SpinnerPeriod.setSelection(0);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP3Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP3Label);
							ViewGroup.LayoutParams paramslinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP3Label = linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP3Label.getLayoutParams();
							paramslinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP3Label.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width * 0.21);
							linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP3Label.setLayoutParams(paramslinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP3Label);
								TextView textViewLRightPanelTopFirstThirdRowsLeftThirdRowP3Label = (TextView) rootView.findViewById(R.id.textViewLRightPanelTopFirstThirdRowsLeftThirdRowP3Label);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP4Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP4Value);
							ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP4Value = linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP4Value.getLayoutParams();
							paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP4Value.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width*0.1055 - 6);
							linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP4Value.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP4Value);
								EditText editTextRightPanelTopFirstThirdRowsLeftThirdRowP4Value = (EditText) rootView.findViewById(R.id.editTextRightPanelTopFirstThirdRowsLeftThirdRowP4Value);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP5Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP5Value);
							ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP5Value = linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP5Value.getLayoutParams();
							paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP5Value.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width*0.1055 - 6);
							linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP5Value.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP5Value);
								EditText editTextRightPanelTopFirstThirdRowsLeftThirdRowP5Value = (EditText) rootView.findViewById(R.id.editTextRightPanelTopFirstThirdRowsLeftThirdRowP5Value);

							LinearLayout linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP6Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP6Value);
							ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP6Value = linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP6Value.getLayoutParams();
							paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP6Value.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsLeft.width*0.1055 - 6);
							linearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP6Value.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsLeftThirdRowP6Value);
								EditText editTextRightPanelTopFirstThirdRowsLeftThirdRowP6Value = (EditText) rootView.findViewById(R.id.editTextRightPanelTopFirstThirdRowsLeftThirdRowP6Value);

					LinearLayout linearLayoutRightPanelTopFirstThirdRowsRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsRight);
					ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsRight = linearLayoutRightPanelTopFirstThirdRowsRight.getLayoutParams();
					paramsLinearLayoutRightPanelTopFirstThirdRowsRight.width = (int)(paramsLinearLayoutRightPanel.width * 0.2);
					paramsLinearLayoutRightPanelTopFirstThirdRowsRight.height = (int)(paramsLinearLayoutRightPanel.height * HEIGHT_ROW_RIGHT_PANEL * 3 + 13);
					linearLayoutRightPanelTopFirstThirdRowsRight.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsRight);
						LinearLayout linearLayoutRightPanelTopFirstThirdRowsRightInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopFirstThirdRowsRightInside);
						ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopFirstThirdRowsRightInside = linearLayoutRightPanelTopFirstThirdRowsRightInside.getLayoutParams();
						paramsLinearLayoutRightPanelTopFirstThirdRowsRightInside.width = (int)(paramsLinearLayoutRightPanelTopFirstThirdRowsRight.width * 0.75);
						paramsLinearLayoutRightPanelTopFirstThirdRowsRightInside.height = (int)(paramsLinearLayoutRightPanel.height*HEIGHT_ROW_LEFT_PANEL * 1.5);
						linearLayoutRightPanelTopFirstThirdRowsRightInside.setLayoutParams(paramsLinearLayoutRightPanelTopFirstThirdRowsRightInside);

				LinearLayout linearLayoutRightPanelTopRestRows = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelTopRestRows);
				ViewGroup.LayoutParams paramsLinearLayoutRightPanelTopRestRows = linearLayoutRightPanelTopRestRows.getLayoutParams();
				paramsLinearLayoutRightPanelTopRestRows.width = paramsLinearLayoutRightPanel.width - 30;
				paramsLinearLayoutRightPanelTopRestRows.height = paramsLinearLayoutRightPanelTop.height - paramsLinearLayoutRightPanelTopFirstThirdRows.height;
				linearLayoutRightPanelTopRestRows.setLayoutParams(paramsLinearLayoutRightPanelTopRestRows);

			LinearLayout linearLayoutRightPanelBottom = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelBottom);
			ViewGroup.LayoutParams paramsLinearLayoutRightPanelBottom = linearLayoutRightPanelBottom.getLayoutParams();
			paramsLinearLayoutRightPanelBottom.width = paramsLinearLayoutRightPanel.width;
			paramsLinearLayoutRightPanelBottom.height = paramsLinearLayoutRightPanel.height / 2 - 20;
			linearLayoutRightPanelBottom.setLayoutParams(paramsLinearLayoutRightPanelBottom);

				LinearLayout linearLayoutRightPanelBottonFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelBottomFirstRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightPanelBottonFirstRow = linearLayoutRightPanelBottonFirstRow.getLayoutParams();
				paramsLinearLayoutRightPanelBottonFirstRow.width = paramsLinearLayoutRightPanel.width;
				paramsLinearLayoutRightPanelBottonFirstRow.height = (int)(paramsLinearLayoutRightPanel.height * HEIGHT_ROW_RIGHT_PANEL);
				linearLayoutRightPanelBottonFirstRow.setLayoutParams(paramsLinearLayoutRightPanelBottonFirstRow);
					TextView textViewRightPanelBottomFirstRowLabel = (TextView) rootView.findViewById(R.id.textViewRightPanelBottomFirstRowLabel);

				LinearLayout linearLayoutRightPanelBottonSecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelBottomSecondRow);
				ViewGroup.LayoutParams paramsLinearLayoutRightPanelBottonSecondRow = linearLayoutRightPanelBottonSecondRow.getLayoutParams();
				paramsLinearLayoutRightPanelBottonSecondRow.width = paramsLinearLayoutRightPanel.width;
				paramsLinearLayoutRightPanelBottonSecondRow.height = (int)(paramsLinearLayoutRightPanel.height * HEIGHT_ROW_RIGHT_PANEL);
				linearLayoutRightPanelBottonSecondRow.setLayoutParams(paramsLinearLayoutRightPanelBottonSecondRow);
					LinearLayout linearLayoutRightPanelBottomSecondRowP1Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelBottomSecondRowP1Label);
					ViewGroup.LayoutParams paramsLlinearLayoutRightPanelBottomSecondRowP1Label = linearLayoutRightPanelBottomSecondRowP1Label.getLayoutParams();
					paramsLlinearLayoutRightPanelBottomSecondRowP1Label.width = (int)(paramsLinearLayoutRightPanelBottonSecondRow.width * 0.7);
					linearLayoutRightPanelBottomSecondRowP1Label.setLayoutParams(paramsLlinearLayoutRightPanelBottomSecondRowP1Label);
						TextView textViewRightPanelBottomSecondRowP1Label = (TextView) rootView.findViewById(R.id.textViewRightPanelBottomSecondRowP1Label);
					LinearLayout linearLayoutRightPanelBottomSecondRowP2Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelBottomSecondRowP2Value);
					ViewGroup.LayoutParams paramsLinearLayoutRightPanelBottomSecondRowP2Value = linearLayoutRightPanelBottomSecondRowP2Value.getLayoutParams();
					paramsLinearLayoutRightPanelBottomSecondRowP2Value.width = (int)(paramsLinearLayoutRightPanelBottonSecondRow.width * 0.10 - 6);
					linearLayoutRightPanelBottomSecondRowP2Value.setLayoutParams(paramsLinearLayoutRightPanelBottomSecondRowP2Value);
						EditText editTextRRightPanelBottomSecondRowP2Value = (EditText) rootView.findViewById(R.id.editTextRRightPanelBottomSecondRowP2Value);
					LinearLayout linearLayoutRightPanelBottomSecondRowP3Label = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelBottomSecondRowP3Label);
					ViewGroup.LayoutParams paramsLinearLayoutRightPanelBottomSecondRowP3Label = linearLayoutRightPanelBottomSecondRowP3Label.getLayoutParams();
					paramsLinearLayoutRightPanelBottomSecondRowP3Label.width = (int)(paramsLinearLayoutRightPanelBottonSecondRow.width * 0.03);
					linearLayoutRightPanelBottomSecondRowP3Label.setLayoutParams(paramsLinearLayoutRightPanelBottomSecondRowP3Label);
						TextView textViewRightPanelBottomSecondRowP3Label = (TextView) rootView.findViewById(R.id.textViewRightPanelBottomSecondRowP3Label);
					LinearLayout linearLayoutRightPanelBottomSecondRowP4Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelBottomSecondRowP4Value);
					ViewGroup.LayoutParams paramsLinearLayoutRightPanelBottomSecondRowP4Value = linearLayoutRightPanelBottomSecondRowP4Value.getLayoutParams();
					paramsLinearLayoutRightPanelBottomSecondRowP4Value.width = (int)(paramsLinearLayoutRightPanelBottonSecondRow.width*0.10 - 6);
					linearLayoutRightPanelBottomSecondRowP4Value.setLayoutParams(paramsLinearLayoutRightPanelBottomSecondRowP4Value);
						EditText editTextRRightPanelBottomSecondRowP4Value = (EditText) rootView.findViewById(R.id.editTextRRightPanelBottomSecondRowP4Value);

				LinearLayout linearLayoutRightPanelBottomRestRows = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightPanelBottomRestRows);
				ViewGroup.LayoutParams paramsLinearLayoutRightPanelBottomRestRows = linearLayoutRightPanelBottomRestRows.getLayoutParams();
				paramsLinearLayoutRightPanelBottomRestRows.width = paramsLinearLayoutRightPanel.width - 30;
				paramsLinearLayoutRightPanelBottomRestRows.height = (int)(paramsLinearLayoutRightPanelBottom.height - paramsLinearLayoutRightPanel.height * HEIGHT_ROW_RIGHT_PANEL * 2 - 10) ;
				linearLayoutRightPanelBottomRestRows.setLayoutParams(paramsLinearLayoutRightPanelBottomRestRows);
//- END CONTENTS

	// -BUTTONS
	// Linear layout "linearLayoutButtons"
	LinearLayout linearLayoutButtons = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtons);
	ViewGroup.LayoutParams paramsLinearLayoutButtons = linearLayoutButtons.getLayoutParams();
	paramsLinearLayoutButtons.width = Globals.widthScreen;
	paramsLinearLayoutButtons.height = (int)(Globals.heightScreen * 0.1);
	linearLayoutButtons.setLayoutParams(paramsLinearLayoutButtons);

	LinearLayout linearLayoutButtonSetup = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonSetup);
	ViewGroup.LayoutParams paramsLinearLayoutButtonSetup = linearLayoutButtonSetup.getLayoutParams();
	paramsLinearLayoutButtonSetup.width = paramsLinearLayoutButtons.width / 6;
	linearLayoutButtonSetup.setLayoutParams(paramsLinearLayoutButtonSetup);
		Button buttonSetup = (Button) rootView.findViewById(R.id.buttonSetup);
		ViewGroup.LayoutParams paramsButtonSetup = buttonSetup.getLayoutParams();
		paramsButtonSetup.height = (int)(paramsLinearLayoutButtons.height * 0.65);
		buttonSetup.setLayoutParams(paramsButtonSetup);

	LinearLayout linearLayoutButtonHistory = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonHistory);
	ViewGroup.LayoutParams paramsLinearLayoutButtonHistory = linearLayoutButtonHistory.getLayoutParams();
	paramsLinearLayoutButtonHistory.width = paramsLinearLayoutButtons.width / 6;
	linearLayoutButtonHistory.setLayoutParams(paramsLinearLayoutButtonHistory);
		Button buttonHistory = (Button) rootView.findViewById(R.id.buttonHistory);
		ViewGroup.LayoutParams paramsButtonHistory = buttonHistory.getLayoutParams();
		paramsButtonHistory.height = (int)(paramsLinearLayoutButtons.height * 0.65);
		buttonHistory.setAlpha(0.5f);
		buttonHistory.setEnabled(false);
		buttonHistory.setLayoutParams(paramsButtonHistory);

	LinearLayout linearLayoutButtonFuel = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonFuel);
	ViewGroup.LayoutParams paramsLinearLayoutButtonFuel = linearLayoutButtonFuel.getLayoutParams();
	paramsLinearLayoutButtonFuel.width = paramsLinearLayoutButtons.width / 6;
	linearLayoutButtonFuel.setLayoutParams(paramsLinearLayoutButtonFuel);
		Button buttonFuel = (Button) rootView.findViewById(R.id.buttonFuel);
		ViewGroup.LayoutParams paramsButtonFuel = buttonFuel.getLayoutParams();
		paramsButtonFuel.height = (int)(paramsLinearLayoutButtons.height * 0.65);
		buttonFuel.setLayoutParams(paramsButtonFuel);

	LinearLayout linearLayoutButtonMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonMain);
	ViewGroup.LayoutParams paramsLinearLayoutButtonMain = linearLayoutButtonMain.getLayoutParams();
	paramsLinearLayoutButtonMain.width = paramsLinearLayoutButtons.width / 6;
	linearLayoutButtonMain.setLayoutParams(paramsLinearLayoutButtonMain);
		Button buttonMain = (Button) rootView.findViewById(R.id.buttonMain);
		ViewGroup.LayoutParams paramsButtonMain = buttonMain.getLayoutParams();
		paramsButtonMain.height = (int)(paramsLinearLayoutButtons.height * 0.65);

	LinearLayout linearLayoutButton5 = (LinearLayout) rootView.findViewById(R.id.linearLayoutButton5);
	ViewGroup.LayoutParams paramsLinearLayoutButton5 = linearLayoutButton5.getLayoutParams();
	paramsLinearLayoutButton5.width = paramsLinearLayoutButtons.width / 6;
	linearLayoutButton5.setLayoutParams(paramsLinearLayoutButton5);
		Button button5 = (Button) rootView.findViewById(R.id.button5);
		ViewGroup.LayoutParams paramsButton5 = button5.getLayoutParams();
		paramsButton5.height = (int)(paramsLinearLayoutButtons.height * 0.65);

	LinearLayout linearLayoutButton6 = (LinearLayout) rootView.findViewById(R.id.linearLayoutButton6);
	ViewGroup.LayoutParams paramsLinearLayoutButton6 = linearLayoutButton6.getLayoutParams();
	paramsLinearLayoutButton6.width = paramsLinearLayoutButtons.width / 6;
	linearLayoutButton6.setLayoutParams(paramsLinearLayoutButton6);
		Button button6 = (Button) rootView.findViewById(R.id.button6);
		ViewGroup.LayoutParams paramsButton6 = button6.getLayoutParams();
		paramsButton6.height = (int)(paramsLinearLayoutButtons.height * 0.65);
// -END BUTTONS
	}

	void createCharts() {

		// CHART SENSOR BASELINE COMPARATION

		// OBJECTS
		lineChartSensorBaselineComparasion = (LineChart) rootView.findViewById(R.id.lineChartSensorBaselineComparasion);
		ArrayList<String> xVals_BaseLineComparation=new ArrayList<String>();
		LineDataSet lineDataSetBoost,lineDataSetRawWater;
		ArrayList<Entry> entriesBoost=new ArrayList<Entry>(),entriesRawWater=new ArrayList<Entry>();
		XAxis xAxis_BaseLineComparation;
		YAxis yAxis1,rightYAxis1;
		LineData data;

		// DISABLE ALL INTERACTION WITH THE CHART
		lineChartSensorBaselineComparasion.setHighlightPerTapEnabled(false);
		lineChartSensorBaselineComparasion.setHighlightPerDragEnabled(false);
		lineChartSensorBaselineComparasion.setDoubleTapToZoomEnabled(false);

		// AXIS CUSTOMIZATION
		xAxis_BaseLineComparation = lineChartSensorBaselineComparasion.getXAxis();
		xAxis_BaseLineComparation.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis_BaseLineComparation.setTextSize(14f);
		yAxis1 = lineChartSensorBaselineComparasion.getAxisLeft();
		yAxis1.setLabelCount(4, true);
		yAxis1.setAxisMinValue(0f);
		yAxis1.setAxisMaxValue(50f);
		yAxis1.setTextSize(14f);
		rightYAxis1 = lineChartSensorBaselineComparasion.getAxisRight();
		rightYAxis1.setEnabled(false);

		// HIDE GRIDS
		lineChartSensorBaselineComparasion.getXAxis().setDrawGridLines(false);
		lineChartSensorBaselineComparasion.getAxisLeft().setDrawGridLines(false);
		lineChartSensorBaselineComparasion.getAxisRight().setDrawGridLines(false);

		// OTHER CUSTOMIZATION
		lineChartSensorBaselineComparasion.setDescription("");
		//lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);
		lineChartSensorBaselineComparasion.getLegend().setEnabled(true);

		// GRAPHIC BASELINE COMPARATION
		for(int i=0;i<ConstantsGlobals.NUMBER_DEMO_DATA_SENSOR_BASELINE_COMPARASION;i++)
			xVals_BaseLineComparation.add(String.valueOf(ConstantsGlobals.SENSOR_BASELINE_COMPARASION_RPM[i]));

		// LINE BOOST
		for(int i=0;i<ConstantsGlobals.NUMBER_DEMO_DATA_SENSOR_BASELINE_COMPARASION;i++) {
			entriesBoost.add(new Entry(ConstantsGlobals.SENSOR_BASELINE_COMPARASION_BOOST[i],i));
		}
		lineDataSetBoost = new LineDataSet(entriesBoost,"Baseline");
		// LINE BOOST CUSTOMIZATION
		lineDataSetBoost.setColor(Color.parseColor("#ED7D31"));
		lineDataSetBoost.setLineWidth(2f);
		lineDataSetBoost.setDrawValues(false);
		lineDataSetBoost.setDrawCircles(false);
		// for second theData1.addDataSet(otro set);
		data = new LineData(xVals_BaseLineComparation,lineDataSetBoost);

		// LINE RAW WATER
		for(int i=0;i<ConstantsGlobals.NUMBER_DEMO_DATA_SENSOR_BASELINE_COMPARASION;i++) {
			entriesRawWater.add(new Entry(ConstantsGlobals.SENSOR_BASELINE_COMPARASION_RAW_WATER[i],i));
		}
		lineDataSetRawWater = new LineDataSet(entriesRawWater,"Sensor");
		// Line Raw Water customization
		lineDataSetRawWater.setColor(Color.parseColor("#2F528F"));
		lineDataSetRawWater.setLineWidth(2f);
		lineDataSetRawWater.setDrawValues(false);
		lineDataSetRawWater.setDrawCircles(false);
		// for second theData1.addDataSet(otro set);
		data.addDataSet(lineDataSetRawWater);

		lineChartSensorBaselineComparasion.setData(data);
		lineChartSensorBaselineComparasion.invalidate();

		// ---------------------------------------------------------------------
		// CHART TREND VS ENGINE RPM
		// OBJECTS
		lineChartTrendVsRPM = (LineChart) rootView.findViewById(R.id.lineChartTrendVsRPM);
		ArrayList<String> xVals_TrendVsRPM=new ArrayList<String>();
		LineDataSet lineDataSetRPM,lineDataSetBoost_2;
		ArrayList<Entry> entriesRPM=new ArrayList<Entry>(),entriesBoost_2=new ArrayList<Entry>();
		XAxis xAxis_TrendVSRPM;
		YAxis yAxis_TrendVSRPM,rightYAxis_TrendVSRPM;
		LineData data_TrendVSRPM;

		// DISABLE ALL INTERACTION WITH THE CHART
		lineChartTrendVsRPM.setHighlightPerTapEnabled(false);
		lineChartTrendVsRPM.setHighlightPerDragEnabled(false);
		lineChartTrendVsRPM.setDoubleTapToZoomEnabled(false);

		// AXIS CUSTOMIZATION
		xAxis_TrendVSRPM = lineChartTrendVsRPM.getXAxis();
		xAxis_TrendVSRPM.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis_TrendVSRPM.setTextSize(14f);
		yAxis_TrendVSRPM = lineChartTrendVsRPM.getAxisLeft();
		yAxis_TrendVSRPM.setLabelCount(3, true);
		yAxis_TrendVSRPM.setAxisMinValue(0f);
		yAxis_TrendVSRPM.setAxisMaxValue(20f);
		yAxis_TrendVSRPM.setTextSize(14f);
		rightYAxis_TrendVSRPM = lineChartTrendVsRPM.getAxisRight();
		rightYAxis_TrendVSRPM.setEnabled(true);
		xAxis_TrendVSRPM.setTextSize(14f);
		yAxis_TrendVSRPM = lineChartTrendVsRPM.getAxisRight();
		yAxis_TrendVSRPM.setLabelCount(3, true);
		yAxis_TrendVSRPM.setAxisMinValue(0f);
		yAxis_TrendVSRPM.setAxisMaxValue(2000f);
		yAxis_TrendVSRPM.setTextSize(14f);

		// HIDE GRIDS
		lineChartTrendVsRPM.getXAxis().setDrawGridLines(false);
		lineChartTrendVsRPM.getAxisLeft().setDrawGridLines(false);
		lineChartTrendVsRPM.getAxisRight().setDrawGridLines(false);

		// OTHER CUSTOMIZATION
		lineChartTrendVsRPM.setDescription("");
		//lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);
		lineChartTrendVsRPM.getLegend().setEnabled(true);

		// GRAPHIC TREND VS RPM
		for(int i=0;i<ConstantsGlobals.NUMBER_DEMO_DATA_TREND_VS_RPM;i++)
			xVals_TrendVsRPM.add(ConstantsGlobals.TREND_VS_RPM_LABELS[i]);

		// LINE RPM
		for(int i=0;i<ConstantsGlobals.NUMBER_DEMO_DATA_TREND_VS_RPM;i++) {
			entriesRPM.add(new Entry(ConstantsGlobals.TREND_VS_RPM_RPM[i]/100,i));
		}
		lineDataSetRPM = new LineDataSet(entriesRPM,"RPM");
		// LINE BOOST CUSTOMIZATION
		lineDataSetRPM.setColor(Color.parseColor("#ED7D31"));
		lineDataSetRPM.setLineWidth(2f);
		lineDataSetRPM.setDrawValues(false);
		lineDataSetRPM.setDrawCircles(false);
		data_TrendVSRPM = new LineData(xVals_TrendVsRPM,lineDataSetRPM);

		// LINE BOOST
		for(int i=0;i<ConstantsGlobals.NUMBER_DEMO_DATA_TREND_VS_RPM;i++) {
			entriesBoost_2.add(new Entry(ConstantsGlobals.TREND_VS_RPM_BOOST[i],i));
		}
		lineDataSetBoost_2 = new LineDataSet(entriesBoost_2,"Boost");
		// LINE BOOST CUSTOMIZATION
		lineDataSetBoost_2.setColor(Color.parseColor("#2F528F"));
		lineDataSetBoost_2.setLineWidth(2f);
		lineDataSetBoost_2.setDrawValues(false);
		lineDataSetBoost_2.setDrawCircles(false);
		data_TrendVSRPM.addDataSet(lineDataSetBoost_2);

		lineChartTrendVsRPM.setData(data_TrendVSRPM);
		lineChartTrendVsRPM.invalidate();
/*

		// LINE RAW WATER
		for(int i=0;i<ConstantsGlobals.NUMBER_DEMO_DATA_SENSOR_BASELINE_COMPARASION;i++) {
			entriesRawWater.add(new Entry(ConstantsGlobals.SENSOR_BASELINE_COMPARASION_RAW_WATER[i],i));
		}
		lineDataSetRawWater = new LineDataSet(entriesRawWater,"");
		// Line Raw Water customization
		lineDataSetRawWater.setColor(Color.parseColor("#2F528F"));
		lineDataSetRawWater.setLineWidth(2f);
		lineDataSetRawWater.setDrawValues(false);
		lineDataSetRawWater.setDrawCircles(false);
		// for second theData1.addDataSet(otro set);
		data.addDataSet(lineDataSetRawWater);

*/
	}
}
