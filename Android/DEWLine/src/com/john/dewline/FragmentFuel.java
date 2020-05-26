package com.john.dewline;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import junit.framework.Assert;

public class FragmentFuel extends Fragment {

	View rootView;

	public static File root,dewLineFolder;
	public static SQLiteDatabase dewLineDB;
	public static Cursor cursor = null;
	public static Timer tDemoRPM_FuelScreen = new Timer(),tDemo_FuelScreen = new Timer();

	// ********** CIRCULAR GAUGES
// CHANGE WITH NEW CIRCULAR GAUGES
	public static final int NUMBER_CIRCULAR_GAUGES=9;
	public static final int SEGMENTS_GAUGE[]={5,6,5,6,5,7,5,5,7};

// CHANGE WITH NEW CIRCULAR GAUGES
	public static String SCALES_CIRCULAR_GAUGES_SAE[][]= {
			{"0","15","30","45","60","75","-","-"},								// Genset Oil Pressure -SAE -PSI
			{"100","120","140","160","180","200","220","-"},					// Genset Temp -SAE -°F
			{"0","3","6","9","12","15","-","-"},								// Genset Burn -SAE -GPH -Not thresholds
			{"0","10","20","30","40","50","60","-"},							// Port Burn -SAE -GPH -Not thresholds
			{"0","10","20","30","40","50","-","-"},								// GPS Speed -Nautical -Kn -Not thresholds
			{"0","0.5","1","1.5","2","2.5","3","3.5"},							// Vessel Efficiency -SAE -GPM
			{"0","20","40","60","80","100","-","-"},							// Vessel Burn -SAE -GPH
			{"0","10","20","30","40","50","-","-"},								// Water Speed -Nautical -Kn
			{"0","5","10","15","20","25","30","35"}								// RPM
	};
	public static String parameter[]={
			"Genset 1 Oil Pressure",
			"Genset 1 Coolant Temp",
			"Genset Burn",
			"Port Engine Burn",
			"GPS Speed",
			"Vessel Eficciency",
			"Vessel Burn",
			"Water Speed",
			"RPM"
	};
	public static String NAME_ICONS_CIRCULAR_GAUGES[]={
			"genset_oil_pressure_missing",
			"genset_temp_missing",
			"genset_fuel_burn_missing",
			"engine_fuel_burn_missing",
			"gps_speed_missing",
			"vessel_efficiency_missing",
			"vessel_burn_missing",
			"vessel_speed_over_water_missing",
			"engine_rpm_port_background"
	};
	public static String UNITS_CIRCULAR_GAUGES_SAE[]={
			"PSI",
			"°F",
			"GPH",
			"GPH",
			"Kn",
			"GPM",
			"GPH",
			"Kn",
			"RPM"
	};
	public static int MAXIMUM_ANGLE_GAUGES[]={135,125,120};						// 5, 6 and 7 segments
	public static final int DASHES_CIRCULAR_GAUGES[]={26,31,36};
	public static double SHIFT_X_SCALE_GAUGE_PER_NUMBER=3;
	public static final int ADJUST_POSITION_NEEDLE_X=2,ADJUST_POSITION_ICON_X=8,ADJUST_POSITION_VALUE_GAUGE_X=8;
	public static final int positionBigGauge=8;

	LinearLayout linearLayoutFirstRowFirstGauge,linearLayoutFirstRowSecondGauge,linearLayoutFirstRowThirdGauge,
						linearLayoutFirstRowFourthGauge,linearLayoutFirstRowFifthGauge,
				 linearLayoutSecondRowThirdGauge,linearLayoutSecondRowFourthGauge,linearLayoutSecondRowFifhtGauge,
				 linearLayoutRightColumnFirstSecondRowsBigGauge;
	ViewGroup.LayoutParams paramsLinearLayoutFirstRowFirstGauge,paramsLinearLayoutFirstRowSecondGauge,
							paramsLinearLayoutFirstRowThirdGauge,paramsLinearLayoutFirstRowFourthGauge,
							paramsLinearLayoutFirstRowFifthGauge,
				paramsLinearLayoutSecondRowThirdGauge,paramsLinearLayoutSecondRowFourthGauge,paramsLinearLayoutSecondRowFifthGauge,
				paramsLinearLayoutRightColumnFirstSecondRowBigGauge;
	SVGImageView []svgImageView = new SVGImageView[NUMBER_CIRCULAR_GAUGES];

	public static RelativeLayout gaugeFirstRowFirstGauge,gaugeFirstRowSecondGauge,gaugeFirstRowThirdGauge,gaugeFirstRowFourthGauge,
								 gaugeFirstRowFifthGauge,
								 gaugeSecondRowThirdGauge,gaugeSecondRowFourthGauge,gaugeSecondRowFifthGauge,
								 gaugeFirstSecondRowsBigGauge;
	public static int MARGIN_RELATIVE_LAYOUT_GAUGE=10,marginNeedle=8;
	public static LinearLayout gaugeNeedle = new LinearLayout(Globals.context);
	LinearLayout[] linearLayoutNeedle = new LinearLayout[NUMBER_CIRCULAR_GAUGES];
	TextView[] textViewValueGauge = new TextView[NUMBER_CIRCULAR_GAUGES];
	public static DecimalFormat zeroDecForm = new DecimalFormat("#"),oneDecForm = new DecimalFormat("#.#");

	public static float valueMinimum=0.0f,valueMaximum,increment=2;
	public static int positionDegrees;
	public static String unitsSystem="SAE";
	public static Document docGauge=null;

// CHANGE WITH NEW CIRCULAR GAUGES
	public static float actualValueCircularGauge[]={0.0f,100f,0.0f,0.0f,0.0f,
			0.0f,0.0f,0.0f,
			0.0f};
	public static float valueParameterCircularGaugePrevious[]={0.0f,0.0f,0.0f,0.0f,0.0f,
			0.0f,0.0f,0.0f,
			0.0f};
	public static int actualFaceDisplayed[]={0,0,0,0,0,
			0,0,0,
			0};

	// Thresholds variables
	float lowThresholdShutdown_Idle=-1,lowThresholdRed_Idle=-1,lowThresholdYellow_Idle=-1;
	float highThresholdYellow_Idle=-1,highThresholdRed_Idle=-1,highThresholdShutdown_Idle=-1;
	float lowThresholdShutdown_WOT=-1,lowThresholdRed_WOT=-1,lowThresholdYellow_WOT=-1;
	float highThresholdYellow_WOT=-1,highThresholdRed_WOT=-1,highThresholdShutdown_WOT=-1;

	public static float valueRPMCurrent = ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES_RPM[0];

	// ********** LINEAR GAUGES
	LinearLayout linearLayoutSecondRowFirstGaugeGauge,linearLayoutSecondRowSecondGaugeGauge;
	public static final int NUMBER_LINEAR_GAUGES=2;
	public static final int SEGMENTS_LINEAR_GAUGE[]={4,4};
	public static String SCALES_LINEAR_GAUGES_SAE[][]= {
			{"Empty","1/4","1/2","3/4","Full","-","-","-","-"},					// Fuel Tank 1
			{"Empty","1/4","1/2","3/4","Full","-","-","-","-"}					// Fuel Tank 2
	};
	Document docLinearGauge[]=new Document[NUMBER_LINEAR_GAUGES];

	public static LinearLayout linearGaugeSecondRowFirstGauge,linearGaugeSecondRowSecondGauge;

	public static final float HEIGHT_ROW_THIRD_ROW_LEFT_PANEL=0.167f,
							  HEIGHT_ROW_THIRD_ROW_CENTER_PANEL=HEIGHT_ROW_THIRD_ROW_LEFT_PANEL*0.65f,
							  HEIGHT_ROW_THIRD_ROW_RIGHT_PANEL=HEIGHT_ROW_THIRD_ROW_LEFT_PANEL*0.8f;

	TextView textViewRightColumnSecondRowTimeInsideRightTime;
	public static Date date;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_fuel, container,false);

		root = Environment.getExternalStorageDirectory();
		dewLineFolder = new File(root.getAbsolutePath()+"/DEWLine/");

		dewLineDB = Globals.context.openOrCreateDatabase(dewLineFolder+"/DEWLine.db",Globals.context.MODE_PRIVATE, null);
		createGUI();

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

		// LISTENERS
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

		Button buttonHistory = (Button) rootView.findViewById(R.id.buttonHistory);
		buttonHistory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Display the History screen
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
				// Display the Main screen
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				FragmentMain fragmentMain = new FragmentMain();
				ft.replace(R.id.fragment,fragmentMain);
				ft.commit();
			}
		});

		return rootView;
	}

	@Override
	public void onPause(){
		super.onPause();

		tDemoRPM_FuelScreen.cancel();
		tDemoRPM_FuelScreen = null;
		tDemo_FuelScreen.cancel();
		tDemo_FuelScreen = null;
		//dewLineDB.close();													// The DB is closed, but activity don't stop so ...error
	}

	@Override
	public void onResume(){
		super.onResume();

		tDemoRPM_FuelScreen = new Timer();
		TimerTask timerDemoRPM = new TimerTask(){
			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						simulatorCircularGaugeDemoRPM();
					}
				});
			}

		};
		tDemoRPM_FuelScreen.scheduleAtFixedRate(timerDemoRPM, 0 , 500);

		tDemo_FuelScreen = new Timer();
		TimerTask timerDemoCircularGauge = new TimerTask(){
			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						simulatorCircularGaugesDemo();
						simulatorLinearGaugesDemo();
						date = new Date();
						textViewRightColumnSecondRowTimeInsideRightTime.setText(DateFormat.format("h:mm a", date));
					}
				});
			}

		};
		tDemo_FuelScreen.scheduleAtFixedRate(timerDemoCircularGauge, 0 , 1000);

	}

	public void createGUI() {

//- GAUGES
		// Linear layout "LinearLayoutGauges"
		RelativeLayout relativeLayoutGauges = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutGauges);
		ViewGroup.LayoutParams paramsRelativeLayoutGauges = relativeLayoutGauges.getLayoutParams();
		paramsRelativeLayoutGauges.width = Globals.widthScreen;
		paramsRelativeLayoutGauges.height = (int)(Globals.heightScreen * 0.9);
		relativeLayoutGauges.setLayoutParams(paramsRelativeLayoutGauges);
//-LEFT COLUMN
			// Linear layout "LinearLayoutLeftColumn" (uses all the width space)
			LinearLayout linearLayoutLefColumn = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeftColumn);
			ViewGroup.LayoutParams paramsLinearLayoutLefColumn = linearLayoutLefColumn.getLayoutParams();
			paramsLinearLayoutLefColumn.width = paramsRelativeLayoutGauges.width;
			paramsLinearLayoutLefColumn.height = paramsRelativeLayoutGauges.height;
			linearLayoutLefColumn.setLayoutParams(paramsLinearLayoutLefColumn);

//-FIRST ROW
				// RelativeLayout "RelativeLayoutFirstRow"
				RelativeLayout relativeLayoutFirstRow = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFirstRow);
				ViewGroup.LayoutParams paramsLinearLayoutFirstRow = relativeLayoutFirstRow.getLayoutParams();
				paramsLinearLayoutFirstRow.width = paramsLinearLayoutLefColumn.width;
				paramsLinearLayoutFirstRow.height = (int)(Globals.heightScreen * 0.28);
				relativeLayoutFirstRow.setLayoutParams(paramsLinearLayoutFirstRow);

					LinearLayout linearLayoutFirstRowFirstGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowFirstGauge);
					ViewGroup.LayoutParams paramsLinearLayoutFirstRowFirstGauge = linearLayoutFirstRowFirstGauge.getLayoutParams();
					paramsLinearLayoutFirstRowFirstGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowFirstGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowFirstGauge.setX(0);
					linearLayoutFirstRowFirstGauge.setY(0);
					linearLayoutFirstRowFirstGauge.setLayoutParams(paramsLinearLayoutFirstRowFirstGauge);
					gaugeFirstRowFirstGauge = createCircularGauge(0,linearLayoutFirstRowFirstGauge,paramsLinearLayoutFirstRowFirstGauge);
					linearLayoutFirstRowFirstGauge.addView(gaugeFirstRowFirstGauge);

					LinearLayout linearLayoutFirstRowSecondGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowSecondGauge);
					ViewGroup.LayoutParams paramsLinearLayoutFirstRowSecondGauge = linearLayoutFirstRowSecondGauge.getLayoutParams();
					paramsLinearLayoutFirstRowSecondGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowSecondGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowSecondGauge.setX(paramsLinearLayoutFirstRowSecondGauge.width*23/32);
					linearLayoutFirstRowSecondGauge.setY(0);
					linearLayoutFirstRowSecondGauge.setLayoutParams(paramsLinearLayoutFirstRowSecondGauge);
					gaugeFirstRowSecondGauge = createCircularGauge(1,linearLayoutFirstRowSecondGauge,paramsLinearLayoutFirstRowSecondGauge);
					linearLayoutFirstRowSecondGauge.addView(gaugeFirstRowSecondGauge);

					LinearLayout linearLayoutFirstRowThirdGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowThirdGauge);
					ViewGroup.LayoutParams paramsLinearLayoutFirstRowThirdGauge = linearLayoutFirstRowThirdGauge.getLayoutParams();
					paramsLinearLayoutFirstRowThirdGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowThirdGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowThirdGauge.setX(paramsLinearLayoutFirstRowThirdGauge.width*46/32);
					linearLayoutFirstRowThirdGauge.setY(0);
					linearLayoutFirstRowThirdGauge.setLayoutParams(paramsLinearLayoutFirstRowThirdGauge);
					gaugeFirstRowThirdGauge = createCircularGauge(2,linearLayoutFirstRowThirdGauge,paramsLinearLayoutFirstRowThirdGauge);
					linearLayoutFirstRowThirdGauge.addView(gaugeFirstRowThirdGauge);

					LinearLayout linearLayoutFirstRowFourthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowFourthGauge);
					ViewGroup.LayoutParams paramsLinearLayoutFirstRowFourthGauge = linearLayoutFirstRowFourthGauge.getLayoutParams();
					paramsLinearLayoutFirstRowFourthGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowFourthGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowFourthGauge.setX(paramsLinearLayoutFirstRowFourthGauge.width*69/32);
					linearLayoutFirstRowFourthGauge.setY(0);
					linearLayoutFirstRowFourthGauge.setLayoutParams(paramsLinearLayoutFirstRowFourthGauge);
					gaugeFirstRowFourthGauge = createCircularGauge(3,linearLayoutFirstRowThirdGauge,paramsLinearLayoutFirstRowFourthGauge);
					linearLayoutFirstRowFourthGauge.addView(gaugeFirstRowFourthGauge);

					LinearLayout linearLayoutFirstRowFifthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowFifthGauge);
					ViewGroup.LayoutParams paramsLinearLayoutFirstRowFifthGauge = linearLayoutFirstRowFifthGauge.getLayoutParams();
					paramsLinearLayoutFirstRowFifthGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowFifthGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowFifthGauge.setX(paramsLinearLayoutFirstRowFifthGauge.width*92/32);
					linearLayoutFirstRowFifthGauge.setY(0);
					linearLayoutFirstRowFifthGauge.setLayoutParams(paramsLinearLayoutFirstRowFifthGauge);
					gaugeFirstRowFifthGauge = createCircularGauge(4,linearLayoutFirstRowFifthGauge,paramsLinearLayoutFirstRowFifthGauge);
					linearLayoutFirstRowFifthGauge.addView(gaugeFirstRowFifthGauge);
//-END FIRST ROW

//-SECOND ROW
				// RelativeLayout "RelativeLayoutSecondRow"
				RelativeLayout relativeLayoutSecondRow = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSecondRow);
				ViewGroup.LayoutParams paramsLinearLayoutSecondRow = relativeLayoutSecondRow.getLayoutParams();
				paramsLinearLayoutSecondRow.width = paramsLinearLayoutLefColumn.width;
				paramsLinearLayoutSecondRow.height = (int)(Globals.heightScreen *  0.28);
				relativeLayoutSecondRow.setLayoutParams(paramsLinearLayoutSecondRow);

					LinearLayout linearLayoutSecondRowFirstG = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowFirstG);
					ViewGroup.LayoutParams paramsLinearLayoutSecondRowFirstG = linearLayoutSecondRowFirstG.getLayoutParams();
					paramsLinearLayoutSecondRowFirstG.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowFirstG.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowFirstG.setX(0);
					linearLayoutSecondRowFirstG.setY(0);
					linearLayoutSecondRowFirstG.setLayoutParams(paramsLinearLayoutSecondRowFirstG);
						LinearLayout linearLayoutSecondRowFirstGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowFirstGauge);
						ViewGroup.LayoutParams paramsLinearLayoutSecondRowFirstGauge = linearLayoutSecondRowFirstGauge.getLayoutParams();
						paramsLinearLayoutSecondRowFirstGauge.width = (int)(paramsLinearLayoutSecondRowFirstG.width * 0.3);
						paramsLinearLayoutSecondRowFirstGauge.height = paramsLinearLayoutSecondRow.height;
						linearLayoutSecondRowFirstGauge.setLayoutParams(paramsLinearLayoutSecondRowFirstGauge);
							LinearLayout linearLayoutSecondRowFirstGaugeDescription = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowFirstGaugeDescription);
							ViewGroup.LayoutParams paramsLinearLayoutSecondRowFirstGaugeDescription = linearLayoutSecondRowFirstGaugeDescription.getLayoutParams();
							paramsLinearLayoutSecondRowFirstGaugeDescription.width = paramsLinearLayoutSecondRowFirstGauge.width;
							paramsLinearLayoutSecondRowFirstGaugeDescription.height = (int)(paramsLinearLayoutSecondRowFirstGauge.height*0.2);
							linearLayoutSecondRowFirstGaugeDescription.setLayoutParams(paramsLinearLayoutSecondRowFirstGaugeDescription);
								TextView textViewSecondRowFirstGaugeDescription = (TextView) rootView.findViewById(R.id.textViewSecondRowFirstGaugeDescription);
							linearLayoutSecondRowFirstGaugeGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowFirstGaugeGauge);
							ViewGroup.LayoutParams paramsLinearLayoutSecondRowFirstGaugeGauge = linearLayoutSecondRowFirstGaugeGauge.getLayoutParams();
							paramsLinearLayoutSecondRowFirstGaugeGauge.width = paramsLinearLayoutSecondRowFirstGauge.width;
							paramsLinearLayoutSecondRowFirstGaugeGauge.height = (int)(paramsLinearLayoutSecondRowFirstGauge.height*0.8);
							linearLayoutSecondRowFirstGaugeGauge.setLayoutParams(paramsLinearLayoutSecondRowFirstGaugeGauge);
								linearGaugeSecondRowFirstGauge = createLinearGauge(0,linearLayoutSecondRowFirstGaugeGauge,paramsLinearLayoutSecondRowFirstGaugeGauge);
								linearLayoutSecondRowFirstGaugeGauge.addView(linearGaugeSecondRowFirstGauge);

					LinearLayout linearLayoutSecondRowSecondG = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowSecondG);
					ViewGroup.LayoutParams paramsLinearLayoutSecondRowSecondG = linearLayoutSecondRowSecondG.getLayoutParams();
					paramsLinearLayoutSecondRowSecondG.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowSecondG.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowSecondG.setX(paramsLinearLayoutSecondRowSecondG.width*23/32);
					linearLayoutSecondRowSecondG.setY(0);
					linearLayoutSecondRowSecondG.setLayoutParams(paramsLinearLayoutSecondRowSecondG);
						LinearLayout linearLayoutSecondRowSecondGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowSecondGauge);
						ViewGroup.LayoutParams paramsLinearLayoutSecondRowSecondGauge = linearLayoutSecondRowSecondGauge.getLayoutParams();
						paramsLinearLayoutSecondRowSecondGauge.width = (int)(paramsLinearLayoutSecondRowSecondG.width * 0.3);
						paramsLinearLayoutSecondRowSecondGauge.height = paramsLinearLayoutSecondRow.height;
						linearLayoutSecondRowSecondGauge.setLayoutParams(paramsLinearLayoutSecondRowSecondGauge);
							LinearLayout linearLayoutSecondRowSecondGaugeDescription = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowSecondGaugeDescription);
							ViewGroup.LayoutParams paramsLinearLayoutSecondRowSecondGaugeDescription = linearLayoutSecondRowSecondGaugeDescription.getLayoutParams();
							paramsLinearLayoutSecondRowSecondGaugeDescription.width = paramsLinearLayoutSecondRowSecondGauge.width;
							paramsLinearLayoutSecondRowSecondGaugeDescription.height = (int)(paramsLinearLayoutSecondRowSecondGauge.height*0.2);
							linearLayoutSecondRowSecondGaugeDescription.setLayoutParams(paramsLinearLayoutSecondRowSecondGaugeDescription);
								TextView textViewSecondRowSecondGaugeDescription = (TextView) rootView.findViewById(R.id.textViewSecondRowSecondGaugeDescription);
							linearLayoutSecondRowSecondGaugeGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowSecondGaugeGauge);
							ViewGroup.LayoutParams paramsLinearLayoutSecondRowSecondGaugeGauge = linearLayoutSecondRowSecondGaugeGauge.getLayoutParams();
							paramsLinearLayoutSecondRowSecondGaugeGauge.width = paramsLinearLayoutSecondRowSecondGauge.width;
							paramsLinearLayoutSecondRowSecondGaugeGauge.height = (int)(paramsLinearLayoutSecondRowSecondGauge.height*0.8);
							linearLayoutSecondRowSecondGaugeGauge.setLayoutParams(paramsLinearLayoutSecondRowSecondGaugeGauge);
								linearGaugeSecondRowSecondGauge = createLinearGauge(1,linearLayoutSecondRowSecondGaugeGauge,paramsLinearLayoutSecondRowSecondGaugeGauge);
								linearLayoutSecondRowSecondGaugeGauge.addView(linearGaugeSecondRowSecondGauge);

					LinearLayout linearLayoutSecondRowThirdGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowThirdGauge);
					ViewGroup.LayoutParams paramsLinearLayoutSecondRowThirdGauge = linearLayoutSecondRowThirdGauge.getLayoutParams();
					paramsLinearLayoutSecondRowThirdGauge.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowThirdGauge.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowThirdGauge.setX(paramsLinearLayoutSecondRowThirdGauge.width*46/32);
					linearLayoutSecondRowThirdGauge.setY(0);
					linearLayoutSecondRowThirdGauge.setLayoutParams(paramsLinearLayoutSecondRowThirdGauge);
					gaugeSecondRowThirdGauge = createCircularGauge(5,linearLayoutSecondRowThirdGauge,paramsLinearLayoutSecondRowThirdGauge);
					linearLayoutSecondRowThirdGauge.addView(gaugeSecondRowThirdGauge);

					LinearLayout linearLayoutSecondRowFourthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowFourthGauge);
					ViewGroup.LayoutParams paramsLinearLayoutSecondRowFourthGauge = linearLayoutSecondRowFourthGauge.getLayoutParams();
					paramsLinearLayoutSecondRowFourthGauge.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowFourthGauge.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowFourthGauge.setX(paramsLinearLayoutSecondRowFourthGauge.width*69/32);
					linearLayoutSecondRowFourthGauge.setY(0);
					linearLayoutSecondRowFourthGauge.setLayoutParams(paramsLinearLayoutSecondRowFourthGauge);
					gaugeSecondRowFourthGauge = createCircularGauge(6,linearLayoutSecondRowFourthGauge,paramsLinearLayoutSecondRowFourthGauge);
					linearLayoutSecondRowFourthGauge.addView(gaugeSecondRowFourthGauge);

					LinearLayout linearLayoutSecondRowFifthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowFifthGauge);
					ViewGroup.LayoutParams paramsLinearLayoutSecondRowFifthGauge = linearLayoutSecondRowFifthGauge.getLayoutParams();
					paramsLinearLayoutSecondRowFifthGauge.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowFifthGauge.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowFifthGauge.setX(paramsLinearLayoutSecondRowFifthGauge.width*92/32);
					linearLayoutSecondRowFifthGauge.setY(0);
					linearLayoutSecondRowFifthGauge.setLayoutParams(paramsLinearLayoutSecondRowFifthGauge);
					gaugeSecondRowFifthGauge = createCircularGauge(7,linearLayoutSecondRowFifthGauge,paramsLinearLayoutSecondRowFifthGauge);
					linearLayoutSecondRowFifthGauge.addView(gaugeSecondRowFifthGauge);
//-END SECOND ROW

//-THIRD ROW (Exceeds the bottom limit. Adjusting in the panels -provisional)
				LinearLayout linearLayoutThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRow);
				ViewGroup.LayoutParams paramsLinearLayoutThirdRow = linearLayoutThirdRow.getLayoutParams();
				paramsLinearLayoutThirdRow.width = paramsLinearLayoutLefColumn.width - 20;
				paramsLinearLayoutThirdRow.height = (int)(Globals.heightScreen * 0.28 - 0);
				linearLayoutThirdRow.setLayoutParams(paramsLinearLayoutThirdRow);

//-LEFT PANEL
					LinearLayout linearLayoutThirdRowLeftPanel = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanel);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanel = linearLayoutThirdRowLeftPanel.getLayoutParams();
					paramsLinearLayoutThirdRowLeftPanel.width = paramsLinearLayoutThirdRow.width / 3 - 5;
					paramsLinearLayoutThirdRowLeftPanel.height = paramsLinearLayoutThirdRow.height - 18;
					linearLayoutThirdRowLeftPanel.setLayoutParams(paramsLinearLayoutThirdRowLeftPanel);

						LinearLayout linearLayoutThirdRowLeftPanelFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFirstRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFirstRow = linearLayoutThirdRowLeftPanelFirstRow.getLayoutParams();
						paramsLinearLayoutThirdRowLeftPanelFirstRow.width = paramsLinearLayoutThirdRowLeftPanel.width;
						paramsLinearLayoutThirdRowLeftPanelFirstRow.height = (int)(paramsLinearLayoutThirdRowLeftPanel.height * HEIGHT_ROW_THIRD_ROW_LEFT_PANEL);
						linearLayoutThirdRowLeftPanelFirstRow.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFirstRow);
							TextView textViewThirdRowLeftPanelFirstRowTitle = (TextView) rootView.findViewById(R.id.textViewThirdRowLeftPanelFirstRowTitle);

						LinearLayout linearLayoutThirdRowLeftPanelSecondThirdRows = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelSecondThirdRows);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelSecondThirdRows = linearLayoutThirdRowLeftPanelSecondThirdRows.getLayoutParams();
						paramsLinearLayoutThirdRowLeftPanelSecondThirdRows.width = paramsLinearLayoutThirdRowLeftPanel.width;
						paramsLinearLayoutThirdRowLeftPanelSecondThirdRows.height = (int)(paramsLinearLayoutThirdRowLeftPanel.height * HEIGHT_ROW_THIRD_ROW_LEFT_PANEL * 1.35);
						linearLayoutThirdRowLeftPanelSecondThirdRows.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelSecondThirdRows);
							TextView textViewThirdRowLeftPanelSecondThirdRows = (TextView) rootView.findViewById(R.id.textViewThirdRowLeftPanelSecondThirdRows);

						LinearLayout linearLayoutThirdRowLeftPanelFourthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFourthRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFourthRow = linearLayoutThirdRowLeftPanelFourthRow.getLayoutParams();
						paramsLinearLayoutThirdRowLeftPanelFourthRow.width = paramsLinearLayoutThirdRowLeftPanel.width - 40;
						paramsLinearLayoutThirdRowLeftPanelFourthRow.height = (int)(paramsLinearLayoutThirdRowLeftPanel.height * HEIGHT_ROW_THIRD_ROW_LEFT_PANEL);
						linearLayoutThirdRowLeftPanelFourthRow.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFourthRow);

							LinearLayout linearLayoutThirdRowLeftPanelFourthRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFourthRowP1);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFourthRowP1 = linearLayoutThirdRowLeftPanelFourthRowP1.getLayoutParams();
							paramsLinearLayoutThirdRowLeftPanelFourthRowP1.width = (int)(paramsLinearLayoutThirdRowLeftPanelFourthRow.width * 0.2);
							paramsLinearLayoutThirdRowLeftPanelFourthRowP1.height = paramsLinearLayoutThirdRowLeftPanelFourthRow.height;
							linearLayoutThirdRowLeftPanelFourthRowP1.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFourthRowP1);
								TextView textViewThirdRowLeftPanelFourthRowLabel = (TextView) rootView.findViewById(R.id.textViewThirdRowLeftPanelFourthRowLabel);

							LinearLayout linearLayoutThirdRowLeftPanelFourthRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFourthRowP2);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFourthRowP2 = linearLayoutThirdRowLeftPanelFourthRowP2.getLayoutParams();
							paramsLinearLayoutThirdRowLeftPanelFourthRowP2.width = (int)(paramsLinearLayoutThirdRowLeftPanelFourthRow.width * 0.35);
							paramsLinearLayoutThirdRowLeftPanelFourthRowP2.height = paramsLinearLayoutThirdRowLeftPanelFourthRow.height - 6;
							linearLayoutThirdRowLeftPanelFourthRowP2.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFourthRowP2);
								EditText editTextThirdRowLeftPanelFourthRowValue = (EditText) rootView.findViewById(R.id.editTextThirdRowLeftPanelFourthRowValue);

							LinearLayout linearLayoutThirdRowLeftPanelFourthRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFourthRowP3);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFourthRowP3 = linearLayoutThirdRowLeftPanelFourthRowP3.getLayoutParams();
							paramsLinearLayoutThirdRowLeftPanelFourthRowP3.width = (int)(paramsLinearLayoutThirdRowLeftPanelFourthRow.width * 0.15);
							paramsLinearLayoutThirdRowLeftPanelFourthRowP3.height = paramsLinearLayoutThirdRowLeftPanelFourthRow.height;
							linearLayoutThirdRowLeftPanelFourthRowP3.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFourthRowP3);
								TextView  textViewThirdRowLeftPanelFourthRowUnits = (TextView) rootView.findViewById(R.id.textViewThirdRowLeftPanelFourthRowUnits);

							LinearLayout linearLayoutThirdRowLeftPanelFourthRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFourthRowP4);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFourthRowP4 = linearLayoutThirdRowLeftPanelFourthRowP4.getLayoutParams();
							paramsLinearLayoutThirdRowLeftPanelFourthRowP4.width = (int)(paramsLinearLayoutThirdRowLeftPanelFourthRow.width * 0.3);
							paramsLinearLayoutThirdRowLeftPanelFourthRowP4.height = paramsLinearLayoutThirdRowLeftPanelFourthRow.height - 2;
							linearLayoutThirdRowLeftPanelFourthRowP4.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFourthRowP4);
								Button buttonThirdRowLeftPanelFourthRowButton = (Button) rootView.findViewById(R.id.buttonThirdRowLeftPanelFourthRowButton);

						LinearLayout linearLayoutThirdRowLeftPanelFifthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFifthRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFifthRow = linearLayoutThirdRowLeftPanelFifthRow.getLayoutParams();
						paramsLinearLayoutThirdRowLeftPanelFifthRow.width = paramsLinearLayoutThirdRowLeftPanel.width - 40;
						paramsLinearLayoutThirdRowLeftPanelFifthRow.height = (int)(paramsLinearLayoutThirdRowLeftPanel.height * HEIGHT_ROW_THIRD_ROW_LEFT_PANEL);
						linearLayoutThirdRowLeftPanelFifthRow.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFifthRow);

							LinearLayout linearLayoutThirdRowLeftPanelFifthRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFifthRowP1);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFifthRowP1 = linearLayoutThirdRowLeftPanelFifthRowP1.getLayoutParams();
							paramsLinearLayoutThirdRowLeftPanelFifthRowP1.width = (int)(paramsLinearLayoutThirdRowLeftPanelFifthRow.width * 0.2);
							paramsLinearLayoutThirdRowLeftPanelFifthRowP1.height = paramsLinearLayoutThirdRowLeftPanelFifthRow.height;
							linearLayoutThirdRowLeftPanelFifthRowP1.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFifthRowP1);
								TextView textViewThirdRowLeftPanelFifthRowLabel = (TextView) rootView.findViewById(R.id.textViewThirdRowLeftPanelFifthRowLabel);

							LinearLayout linearLayoutThirdRowLeftPanelFifthRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFifthRowP2);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFifthRowP2 = linearLayoutThirdRowLeftPanelFifthRowP2.getLayoutParams();
							paramsLinearLayoutThirdRowLeftPanelFifthRowP2.width = (int)(paramsLinearLayoutThirdRowLeftPanelFifthRow.width * 0.35);
							paramsLinearLayoutThirdRowLeftPanelFifthRowP2.height = paramsLinearLayoutThirdRowLeftPanelFifthRow.height - 6;
							linearLayoutThirdRowLeftPanelFifthRowP2.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFifthRowP2);
								EditText editTextThirdRowLeftPanelFifthRowValue = (EditText) rootView.findViewById(R.id.editTextThirdRowLeftPanelFifthRowValue);

							LinearLayout linearLayoutThirdRowLeftPanelFifthRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFifthRowP3);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFifthRowP3 = linearLayoutThirdRowLeftPanelFifthRowP3.getLayoutParams();
							paramsLinearLayoutThirdRowLeftPanelFifthRowP3.width = (int)(paramsLinearLayoutThirdRowLeftPanelFifthRow.width * 0.15);
							paramsLinearLayoutThirdRowLeftPanelFifthRowP3.height = paramsLinearLayoutThirdRowLeftPanelFifthRow.height;
							linearLayoutThirdRowLeftPanelFifthRowP3.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFifthRowP3);
								TextView  textViewThirdRowLeftPanelFifthRowUnits = (TextView) rootView.findViewById(R.id.textViewThirdRowLeftPanelFifthRowUnits);

							LinearLayout linearLayoutThirdRowLeftPanelFifthRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelFifthRowP4);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelFifthRowP4 = linearLayoutThirdRowLeftPanelFifthRowP4.getLayoutParams();
							paramsLinearLayoutThirdRowLeftPanelFifthRowP4.width = (int)(paramsLinearLayoutThirdRowLeftPanelFifthRow.width * 0.3);
							paramsLinearLayoutThirdRowLeftPanelFifthRowP4.height = paramsLinearLayoutThirdRowLeftPanelFifthRow.height - 2;
							linearLayoutThirdRowLeftPanelFifthRowP4.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelFifthRowP4);
								Button buttonThirdRowLeftPanelFifthRowButton = (Button) rootView.findViewById(R.id.buttonThirdRowLeftPanelFifthRowButton);

						LinearLayout linearLayoutThirdRowLeftPanelSixthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLeftPanelSixthRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLeftPanelSixthRow = linearLayoutThirdRowLeftPanelSixthRow.getLayoutParams();
						paramsLinearLayoutThirdRowLeftPanelSixthRow.width = paramsLinearLayoutThirdRowLeftPanel.width - 40;
						paramsLinearLayoutThirdRowLeftPanelSixthRow.height = (int)(paramsLinearLayoutThirdRowLeftPanel.height * HEIGHT_ROW_THIRD_ROW_LEFT_PANEL);
						linearLayoutThirdRowLeftPanelSixthRow.setLayoutParams(paramsLinearLayoutThirdRowLeftPanelSixthRow);
							Button buttonThirdRowLeftPanelSixthRowButton = (Button) rootView.findViewById(R.id.buttonThirdRowLeftPanelSixthRowButton);
							ViewGroup.LayoutParams paramsButtonThirdRowLeftPanelSixthRowButton = buttonThirdRowLeftPanelSixthRowButton.getLayoutParams();
							paramsButtonThirdRowLeftPanelSixthRowButton.width = paramsLinearLayoutThirdRowLeftPanelSixthRow.width / 2;
							buttonThirdRowLeftPanelSixthRowButton.setLayoutParams(paramsButtonThirdRowLeftPanelSixthRowButton);
//-END LEFT PANEL

//-CENTER PANEL
					LinearLayout linearLayoutThirdRowCenterPanel = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanel);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanel = linearLayoutThirdRowCenterPanel.getLayoutParams();
					paramsLinearLayoutThirdRowCenterPanel.width = paramsLinearLayoutThirdRow.width / 3 - 10;
					paramsLinearLayoutThirdRowCenterPanel.height = paramsLinearLayoutThirdRow.height - 18;
					linearLayoutThirdRowCenterPanel.setLayoutParams(paramsLinearLayoutThirdRowCenterPanel);

						LinearLayout linearLayoutThirdRowCenterPanelFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelFirstRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelFirstRow = linearLayoutThirdRowCenterPanelFirstRow.getLayoutParams();
						paramsLinearLayoutThirdRowCenterPanelFirstRow.width = paramsLinearLayoutThirdRowCenterPanel.width;
						paramsLinearLayoutThirdRowCenterPanelFirstRow.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_LEFT_PANEL);
						linearLayoutThirdRowCenterPanelFirstRow.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelFirstRow);
							TextView textViewThirdRowCenterPanelFirstRowTitle = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelFirstRowTitle);

						LinearLayout linearLayoutThirdRowCenterPanelSecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSecondRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSecondRow = linearLayoutThirdRowCenterPanelSecondRow.getLayoutParams();
						paramsLinearLayoutThirdRowCenterPanelSecondRow.width = paramsLinearLayoutThirdRowCenterPanel.width;
						paramsLinearLayoutThirdRowCenterPanelSecondRow.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_CENTER_PANEL * 0.8);
						linearLayoutThirdRowCenterPanelSecondRow.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSecondRow);

							LinearLayout linearLayoutThirdRowCenterPanelSecondRowP12 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSecondRowP12);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSecondRowP12 = linearLayoutThirdRowCenterPanelSecondRowP12.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelSecondRowP12.width = (paramsLinearLayoutThirdRowCenterPanel.width - 40) / 2;
							paramsLinearLayoutThirdRowCenterPanelSecondRowP12.height = paramsLinearLayoutThirdRowCenterPanelSecondRow.height;
							linearLayoutThirdRowCenterPanelSecondRowP12.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSecondRowP12);
								TextView textViewThirdRowCenterPanelSecondRowP12Label = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelSecondRowP12Label);

							LinearLayout linearLayoutThirdRowCenterPanelSecondRowP34 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSecondRowP34);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSecondRowP34 = linearLayoutThirdRowCenterPanelSecondRowP34.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelSecondRowP34.width = (paramsLinearLayoutThirdRowCenterPanel.width - 40) / 2;
							paramsLinearLayoutThirdRowCenterPanelSecondRowP34.height = paramsLinearLayoutThirdRowCenterPanelSecondRow.height;
							linearLayoutThirdRowCenterPanelSecondRowP34.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSecondRowP34);
								TextView textViewThirdRowCenterPanelSecondRowP34Label = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelSecondRowP34Label);

						LinearLayout linearLayoutThirdRowCenterPanelThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelThirdRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelThirdRow = linearLayoutThirdRowCenterPanelThirdRow.getLayoutParams();
						paramsLinearLayoutThirdRowCenterPanelThirdRow.width = paramsLinearLayoutThirdRowCenterPanel.width;
						paramsLinearLayoutThirdRowCenterPanelThirdRow.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_CENTER_PANEL);
						linearLayoutThirdRowCenterPanelThirdRow.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelThirdRow);

							LinearLayout linearLayoutThirdRowCenterPanelThirdRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelThirdRowP1);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelThirdRowP1 = linearLayoutThirdRowCenterPanelThirdRowP1.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelThirdRowP1.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.3);
							paramsLinearLayoutThirdRowCenterPanelThirdRowP1.height = paramsLinearLayoutThirdRowCenterPanelThirdRow.height;
							linearLayoutThirdRowCenterPanelThirdRowP1.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelThirdRowP1);
								TextView textViewThirdRowCenterPanelThirdRowP1Value = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelThirdRowP1Value);

							LinearLayout linearLayoutThirdRowCenterPanelThirdRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelThirdRowP2);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelThirdRowP2 = linearLayoutThirdRowCenterPanelThirdRowP2.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelThirdRowP2.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.2);
							paramsLinearLayoutThirdRowCenterPanelThirdRowP2.height = paramsLinearLayoutThirdRowCenterPanelThirdRow.height;
							linearLayoutThirdRowCenterPanelThirdRowP2.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelThirdRowP2);
								TextView textViewThirdRowCenterPanelThirdRowP2Units = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelThirdRowP2Units);

							LinearLayout linearLayoutThirdRowCenterPanelThirdRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelThirdRowP3);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelThirdRowP3 = linearLayoutThirdRowCenterPanelThirdRowP3.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelThirdRowP3.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.3);
							paramsLinearLayoutThirdRowCenterPanelThirdRowP3.height = paramsLinearLayoutThirdRowCenterPanelThirdRow.height;
							linearLayoutThirdRowCenterPanelThirdRowP3.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelThirdRowP3);
								TextView textViewThirdRowCenterPanelThirdRowP3Value = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelThirdRowP3Value);

							LinearLayout linearLayoutThirdRowCenterPanelThirdRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelThirdRowP4);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelThirdRowP4 = linearLayoutThirdRowCenterPanelThirdRowP4.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelThirdRowP4.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.2);
							paramsLinearLayoutThirdRowCenterPanelThirdRowP4.height = paramsLinearLayoutThirdRowCenterPanelThirdRow.height;
							linearLayoutThirdRowCenterPanelThirdRowP4.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelThirdRowP4);
								TextView textViewThirdRowCenterPanelThirdRowP4Units = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelThirdRowP4Units);

						LinearLayout linearLayoutThirdRowCenterPanelFourthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelFourthRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelFourthRow = linearLayoutThirdRowCenterPanelFourthRow.getLayoutParams();
						paramsLinearLayoutThirdRowCenterPanelFourthRow.width = paramsLinearLayoutThirdRowCenterPanel.width;
						paramsLinearLayoutThirdRowCenterPanelFourthRow.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_CENTER_PANEL * 0.8);
						linearLayoutThirdRowCenterPanelFourthRow.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelFourthRow);

							LinearLayout linearLayoutThirdRowCenterPanelFourthRowP12 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelFourthRowP12);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelFourthRowP12 = linearLayoutThirdRowCenterPanelFourthRowP12.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelFourthRowP12.width = (paramsLinearLayoutThirdRowCenterPanel.width - 40) / 2;
							paramsLinearLayoutThirdRowCenterPanelFourthRowP12.height = paramsLinearLayoutThirdRowCenterPanelFourthRow.height;
							linearLayoutThirdRowCenterPanelFourthRowP12.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelFourthRowP12);
								TextView textViewThirdRowCenterPanelFourthRowP12Label = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelFourthRowP12Label);

							LinearLayout linearLayoutThirdRowCenterPanelFourthRowP34 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelFourthRowP34);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelFourthRowP34 = linearLayoutThirdRowCenterPanelFourthRowP34.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelFourthRowP34.width = (paramsLinearLayoutThirdRowCenterPanel.width - 40) / 2;
							paramsLinearLayoutThirdRowCenterPanelFourthRowP34.height = paramsLinearLayoutThirdRowCenterPanelFourthRow.height;
							linearLayoutThirdRowCenterPanelFourthRowP34.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelFourthRowP34);
								TextView textViewThirdRowCenterPanelFourthRowP34Label = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelFourthRowP34Label);

						LinearLayout linearLayoutThirdRowCenterPanelFifthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelFifthRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelFifthRow = linearLayoutThirdRowCenterPanelFifthRow.getLayoutParams();
						paramsLinearLayoutThirdRowCenterPanelFifthRow.width = paramsLinearLayoutThirdRowCenterPanel.width;
						paramsLinearLayoutThirdRowCenterPanelFifthRow.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_CENTER_PANEL);
						linearLayoutThirdRowCenterPanelFifthRow.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelFifthRow);

							LinearLayout linearLayoutThirdRowCenterPanelFifthRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelFifthRowP1);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelFifthRowP1 = linearLayoutThirdRowCenterPanelFifthRowP1.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelFifthRowP1.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.3);
							paramsLinearLayoutThirdRowCenterPanelFifthRowP1.height = paramsLinearLayoutThirdRowCenterPanelFifthRow.height;
							linearLayoutThirdRowCenterPanelFifthRowP1.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelFifthRowP1);
								TextView textViewThirdRowCenterPanelFifthRowP1Value = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelFifthRowP1Value);

							LinearLayout linearLayoutThirdRowCenterPanelFifthRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelFifthRowP2);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelFifthRowP2 = linearLayoutThirdRowCenterPanelFifthRowP2.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelFifthRowP2.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.2);
							paramsLinearLayoutThirdRowCenterPanelFifthRowP2.height = paramsLinearLayoutThirdRowCenterPanelFifthRow.height;
							linearLayoutThirdRowCenterPanelFifthRowP2.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelFifthRowP2);
								TextView textViewThirdRowCenterPanelFifthRowP2Units = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelFifthRowP2Units);

							LinearLayout linearLayoutThirdRowCenterPanelFifthRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelFifthRowP3);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelFifthRowP3 = linearLayoutThirdRowCenterPanelFifthRowP3.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelFifthRowP3.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.3);
							paramsLinearLayoutThirdRowCenterPanelFifthRowP3.height = paramsLinearLayoutThirdRowCenterPanelFifthRow.height;
							linearLayoutThirdRowCenterPanelFifthRowP3.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelFifthRowP3);
								TextView textViewThirdRowCenterPanelFifthRowP3Value = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelFifthRowP3Value);

							LinearLayout linearLayoutThirdRowCenterPanelFifthRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelFifthRowP4);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelFifthRowP4 = linearLayoutThirdRowCenterPanelFifthRowP4.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelFifthRowP4.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.2);
							paramsLinearLayoutThirdRowCenterPanelFifthRowP4.height = paramsLinearLayoutThirdRowCenterPanelFifthRow.height;
							linearLayoutThirdRowCenterPanelFifthRowP4.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelFifthRowP4);
								TextView textViewThirdRowCenterPanelFifthRowP4Units = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelFifthRowP4Units);

						LinearLayout linearLayoutThirdRowCenterPanelSixthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSixthRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSixthRow = linearLayoutThirdRowCenterPanelSixthRow.getLayoutParams();
						paramsLinearLayoutThirdRowCenterPanelSixthRow.width = paramsLinearLayoutThirdRowCenterPanel.width;
						paramsLinearLayoutThirdRowCenterPanelSixthRow.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_CENTER_PANEL * 0.8);
						linearLayoutThirdRowCenterPanelSixthRow.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSixthRow);

							LinearLayout linearLayoutThirdRowCenterPanelSixthRowP12 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSixthRowP12);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSixthRowP12 = linearLayoutThirdRowCenterPanelSixthRowP12.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelSixthRowP12.width = (paramsLinearLayoutThirdRowCenterPanel.width - 40) / 2;
							paramsLinearLayoutThirdRowCenterPanelSixthRowP12.height = paramsLinearLayoutThirdRowCenterPanelSixthRow.height;
							linearLayoutThirdRowCenterPanelSixthRowP12.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSixthRowP12);
								TextView textViewThirdRowCenterPanelSixthRowP12Label = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelSixthRowP12Label);

							LinearLayout linearLayoutThirdRowCenterPanelSixthRowP34 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSixthRowP34);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSixthRowP34 = linearLayoutThirdRowCenterPanelSixthRowP34.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelSixthRowP34.width = (paramsLinearLayoutThirdRowCenterPanel.width - 40) / 2;
							paramsLinearLayoutThirdRowCenterPanelSixthRowP34.height = paramsLinearLayoutThirdRowCenterPanelSixthRow.height;
							linearLayoutThirdRowCenterPanelSixthRowP34.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSixthRowP34);
								TextView textViewThirdRowCenterPanelSixthRowP34Label = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelSixthRowP34Label);

						LinearLayout linearLayoutThirdRowCenterPanelSeventhRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSeventhRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSeventhRow = linearLayoutThirdRowCenterPanelSeventhRow.getLayoutParams();
						paramsLinearLayoutThirdRowCenterPanelSeventhRow.width = paramsLinearLayoutThirdRowCenterPanel.width;
						paramsLinearLayoutThirdRowCenterPanelSeventhRow.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_CENTER_PANEL);
						linearLayoutThirdRowCenterPanelSeventhRow.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSeventhRow);

							LinearLayout linearLayoutThirdRowCenterPanelSeventhRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSeventhRowP1);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSeventhRowP1 = linearLayoutThirdRowCenterPanelSeventhRowP1.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelSeventhRowP1.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.25);
							linearLayoutThirdRowCenterPanelSeventhRowP1.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSeventhRowP1);

								LinearLayout linearLayoutThirdRowCenterPanelSeventhRowP11 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSeventhRowP11);
								ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSeventhRowP11 = linearLayoutThirdRowCenterPanelSeventhRowP11.getLayoutParams();
								paramsLinearLayoutThirdRowCenterPanelSeventhRowP11.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.10);
								linearLayoutThirdRowCenterPanelSeventhRowP11.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSeventhRowP11);

								LinearLayout linearLayoutThirdRowCenterPanelSeventhRowP12 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSeventhRowP12);
								ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSeventhRowP12 = linearLayoutThirdRowCenterPanelSeventhRowP12.getLayoutParams();
								paramsLinearLayoutThirdRowCenterPanelSeventhRowP12.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.15);
								linearLayoutThirdRowCenterPanelSeventhRowP12.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSeventhRowP12);

							LinearLayout linearLayoutThirdRowCenterPanelSeventhRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSeventhRowP2);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSeventhRowP2 = linearLayoutThirdRowCenterPanelSeventhRowP2.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelSeventhRowP2.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.25);
							linearLayoutThirdRowCenterPanelSeventhRowP2.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSeventhRowP2);

							LinearLayout linearLayoutThirdRowCenterPanelSeventhRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSeventhRowP3);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSeventhRowP3 = linearLayoutThirdRowCenterPanelSeventhRowP3.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelSeventhRowP3.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.3);
							paramsLinearLayoutThirdRowCenterPanelSeventhRowP3.height = paramsLinearLayoutThirdRowCenterPanelSeventhRow.height;
							linearLayoutThirdRowCenterPanelSeventhRowP3.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSeventhRowP3);
								TextView textViewThirdRowCenterPanelSeventhRowP3Value = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelSeventhRowP3Value);

							LinearLayout linearLayoutThirdRowCenterPanelSeventhRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelSeventhRowP4);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelSeventhRowP4 = linearLayoutThirdRowCenterPanelSeventhRowP4.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelSeventhRowP4.width = (int)((paramsLinearLayoutThirdRowCenterPanel.width - 40)* 0.2);
							paramsLinearLayoutThirdRowCenterPanelSeventhRowP4.height = paramsLinearLayoutThirdRowCenterPanelSeventhRow.height;
							linearLayoutThirdRowCenterPanelSeventhRowP4.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelSeventhRowP4);
								TextView textViewThirdRowCenterPanelSeventhRowP4Units = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelSeventhRowP4Units);

						LinearLayout linearLayoutThirdRowCenterPanelEighthNinethRows = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelEighthNinethRows);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelEighthNinethRows = linearLayoutThirdRowCenterPanelEighthNinethRows.getLayoutParams();
						paramsLinearLayoutThirdRowCenterPanelEighthNinethRows.width = paramsLinearLayoutThirdRowCenterPanel.width - 40;
						paramsLinearLayoutThirdRowCenterPanelEighthNinethRows.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_CENTER_PANEL * 1.8);
						linearLayoutThirdRowCenterPanelEighthNinethRows.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelEighthNinethRows);

							LinearLayout linearLayoutThirdRowCenterPanelEighthNinethRowsLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelEighthNinethRowsLeft);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeft = linearLayoutThirdRowCenterPanelEighthNinethRowsLeft.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeft.width = paramsLinearLayoutThirdRowCenterPanelEighthNinethRows.width / 2;
							linearLayoutThirdRowCenterPanelEighthNinethRowsLeft.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeft);

								LinearLayout linearLayoutThirdRowCenterPanelEighthNinethRowsLeftEighthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelEighthNinethRowsLeftEighthRow);
								ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeftEighthRow = linearLayoutThirdRowCenterPanelEighthNinethRowsLeftEighthRow.getLayoutParams();
								paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeftEighthRow.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_CENTER_PANEL * 0.8);
								linearLayoutThirdRowCenterPanelEighthNinethRowsLeftEighthRow.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeftEighthRow);
									TextView textViewThirdRowCenterPanelEighthNinethRowsLeftEighthRowLabel = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelEighthNinethRowsLeftEighthRowLabel);

								LinearLayout linearLayoutThirdRowCenterPanelEighthNinethRowsLeftNinethRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelEighthNinethRowsLeftNinethRow);
								ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeftNinethRow = linearLayoutThirdRowCenterPanelEighthNinethRowsLeftNinethRow.getLayoutParams();
								paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeftNinethRow.height = (int)(paramsLinearLayoutThirdRowCenterPanel.height * HEIGHT_ROW_THIRD_ROW_CENTER_PANEL);
								paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeftNinethRow.width = (int)(paramsLinearLayoutThirdRowCenterPanelEighthNinethRows.width * 0.3);
								linearLayoutThirdRowCenterPanelEighthNinethRowsLeftNinethRow.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsLeftNinethRow);
									TextView textViewThirdRowCenterPanelEightNinethRowsLeftNinethRowValue = (TextView) rootView.findViewById(R.id.textViewThirdRowCenterPanelEightNinethRowsLeftNinethRowValue);

							LinearLayout linearLayoutThirdRowCenterPanelEighthNinethRowsRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowCenterPanelEighthNinethRowsRight);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsRight = linearLayoutThirdRowCenterPanelEighthNinethRowsRight.getLayoutParams();
							paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsRight.width = paramsLinearLayoutThirdRowCenterPanelEighthNinethRows.width / 2;
							linearLayoutThirdRowCenterPanelEighthNinethRowsRight.setLayoutParams(paramsLinearLayoutThirdRowCenterPanelEighthNinethRowsRight);
								Button buttonThirdRowCenterPanelEighthNinethRowsRightButton = (Button) rootView.findViewById(R.id.buttonThirdRowCenterPanelEighthNinethRowsRightButton);
//-END CENTER PANEL

//-RIGHT PANEL
					LinearLayout linearLayoutThirdRowRightPanel = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanel);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanel = linearLayoutThirdRowRightPanel.getLayoutParams();
					paramsLinearLayoutThirdRowRightPanel.width = paramsLinearLayoutThirdRow.width / 3 - 5;
					paramsLinearLayoutThirdRowRightPanel.height = paramsLinearLayoutThirdRow.height - 18;
					linearLayoutThirdRowRightPanel.setLayoutParams(paramsLinearLayoutThirdRowRightPanel);

						LinearLayout linearLayoutThirdRowRightPanelFirstRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFirstRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFirstRow = linearLayoutThirdRowRightPanelFirstRow.getLayoutParams();
						paramsLinearLayoutThirdRowRightPanelFirstRow.width = paramsLinearLayoutThirdRowRightPanel.width;
						paramsLinearLayoutThirdRowRightPanelFirstRow.height = (int)(paramsLinearLayoutThirdRowRightPanel.height * HEIGHT_ROW_THIRD_ROW_LEFT_PANEL);
						linearLayoutThirdRowRightPanelFirstRow.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFirstRow);
							TextView textViewThirdRowRightPanelFirstRowTitle = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFirstRowTitle);

						LinearLayout linearLayoutThirdRowRightPanelSecondRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSecondRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSecondRow = linearLayoutThirdRowRightPanelSecondRow.getLayoutParams();
						paramsLinearLayoutThirdRowRightPanelSecondRow.width = (int)(paramsLinearLayoutThirdRowRightPanel.width * 0.66);
						paramsLinearLayoutThirdRowRightPanelSecondRow.height = (int)(paramsLinearLayoutThirdRowRightPanel.height * HEIGHT_ROW_THIRD_ROW_RIGHT_PANEL);
						linearLayoutThirdRowRightPanelSecondRow.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSecondRow);
							TextView textViewThirdRowRightPanelSecondRowTitle = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelSecondRowTitle);

						LinearLayout linearLayoutThirdRowRightPanelThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelThirdRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelThirdRow = linearLayoutThirdRowRightPanelThirdRow.getLayoutParams();
						paramsLinearLayoutThirdRowRightPanelThirdRow.width = paramsLinearLayoutThirdRowRightPanel.width;
						paramsLinearLayoutThirdRowRightPanelThirdRow.height = (int)(paramsLinearLayoutThirdRowRightPanel.height * HEIGHT_ROW_THIRD_ROW_RIGHT_PANEL);
						linearLayoutThirdRowRightPanelThirdRow.setLayoutParams(paramsLinearLayoutThirdRowRightPanelThirdRow);

							LinearLayout linearLayoutThirdRowRightPanelThirdRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelThirdRowP1);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelThirdRowP1 = linearLayoutThirdRowRightPanelThirdRowP1.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelThirdRowP1.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.23);
							paramsLinearLayoutThirdRowRightPanelThirdRowP1.height = paramsLinearLayoutThirdRowRightPanelThirdRow.height;
							linearLayoutThirdRowRightPanelThirdRowP1.setLayoutParams(paramsLinearLayoutThirdRowRightPanelThirdRowP1);
								TextView textViewThirdRowRightPanelThirdRowP1Value = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelThirdRowP1Value);

							LinearLayout linearLayoutThirdRowRightPanelThirdRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelThirdRowP2);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelThirdRowP2 = linearLayoutThirdRowRightPanelThirdRowP2.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelThirdRowP2.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.1);
							paramsLinearLayoutThirdRowRightPanelThirdRowP2.height = paramsLinearLayoutThirdRowRightPanelThirdRow.height;
							linearLayoutThirdRowRightPanelThirdRowP2.setLayoutParams(paramsLinearLayoutThirdRowRightPanelThirdRowP2);
								TextView textViewThirdRowRightPanelThirdRowP2Units = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelThirdRowP2Units);

							LinearLayout linearLayoutThirdRowRightPanelThirdRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelThirdRowP3);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelThirdRowP3 = linearLayoutThirdRowRightPanelThirdRowP3.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelThirdRowP3.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.23);
							paramsLinearLayoutThirdRowRightPanelThirdRowP3.height = paramsLinearLayoutThirdRowRightPanelThirdRow.height;
							linearLayoutThirdRowRightPanelThirdRowP3.setLayoutParams(paramsLinearLayoutThirdRowRightPanelThirdRowP3);
								TextView textViewThirdRowRightPanelThirdRowP3Value = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelThirdRowP3Value);

							LinearLayout linearLayoutThirdRowRightPanelThirdRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelThirdRowP4);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelThirdRowP4 = linearLayoutThirdRowRightPanelThirdRowP4.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelThirdRowP4.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.1);
							paramsLinearLayoutThirdRowRightPanelThirdRowP4.height = paramsLinearLayoutThirdRowRightPanelThirdRow.height;
							linearLayoutThirdRowRightPanelThirdRowP4.setLayoutParams(paramsLinearLayoutThirdRowRightPanelThirdRowP4);
								TextView textViewThirdRowRightPanelThirdRowP4Units = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelThirdRowP4Units);

							LinearLayout linearLayoutThirdRowRightPanelThirdRowP5 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelThirdRowP5);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelThirdRowP5 = linearLayoutThirdRowRightPanelThirdRowP5.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelThirdRowP5.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.23);
							paramsLinearLayoutThirdRowRightPanelThirdRowP5.height = paramsLinearLayoutThirdRowRightPanelThirdRow.height;
							linearLayoutThirdRowRightPanelThirdRowP5.setLayoutParams(paramsLinearLayoutThirdRowRightPanelThirdRowP5);
								TextView textViewThirdRowRightPanelThirdRowP5Value = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelThirdRowP5Value);

							LinearLayout linearLayoutThirdRowRightPanelThirdRowP6 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelThirdRowP6);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelThirdRowP6 = linearLayoutThirdRowRightPanelThirdRowP6.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelThirdRowP6.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.1);
							paramsLinearLayoutThirdRowRightPanelThirdRowP6.height = paramsLinearLayoutThirdRowRightPanelThirdRow.height;
							linearLayoutThirdRowRightPanelThirdRowP6.setLayoutParams(paramsLinearLayoutThirdRowRightPanelThirdRowP6);
								TextView textViewThirdRowRightPanelThirdRowP6Units = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelThirdRowP6Units);

						LinearLayout linearLayoutThirdRowRightPanelFourthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFourthRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFourthRow = linearLayoutThirdRowRightPanelFourthRow.getLayoutParams();
						paramsLinearLayoutThirdRowRightPanelFourthRow.width = paramsLinearLayoutThirdRowRightPanel.width;
						paramsLinearLayoutThirdRowRightPanelFourthRow.height = (int)(paramsLinearLayoutThirdRowRightPanel.height * HEIGHT_ROW_THIRD_ROW_RIGHT_PANEL);
						linearLayoutThirdRowRightPanelFourthRow.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFourthRow);

							LinearLayout linearLayoutThirdRowRightPanelFourthRowP12 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFourthRowP12);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFourthRowP12 = linearLayoutThirdRowRightPanelFourthRowP12.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelFourthRowP12.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40) * 0.33);
							paramsLinearLayoutThirdRowRightPanelFourthRowP12.height = paramsLinearLayoutThirdRowRightPanelFourthRow.height;
							linearLayoutThirdRowRightPanelFourthRowP12.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFourthRowP12);
								TextView textViewThirdRowRightPanelFourthRowP12Label = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFourthRowP12Label);

							LinearLayout linearLayoutThirdRowRightPanelFourthRowP34 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFourthRowP34);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFourthRowP34 = linearLayoutThirdRowRightPanelFourthRowP34.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelFourthRowP34.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40) * 0.33);
							paramsLinearLayoutThirdRowRightPanelFourthRowP34.height = paramsLinearLayoutThirdRowRightPanelFourthRow.height;
							linearLayoutThirdRowRightPanelFourthRowP34.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFourthRowP34);
								TextView textViewThirdRowRightPanelFourthRowP34Label = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFourthRowP34Label);

							LinearLayout linearLayoutThirdRowRightPanelFourthRowP56 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFourthRowP56);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFourthRowP56 = linearLayoutThirdRowRightPanelFourthRowP56.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelFourthRowP56.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40) * 0.33);
							paramsLinearLayoutThirdRowRightPanelFourthRowP56.height = paramsLinearLayoutThirdRowRightPanelFourthRow.height;
							linearLayoutThirdRowRightPanelFourthRowP56.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFourthRowP56);
								TextView textViewThirdRowRightPanelFourthRowP56Label = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFourthRowP56Label);

						LinearLayout linearLayoutThirdRowRightPanelFifthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFifthRow);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFifthRow = linearLayoutThirdRowRightPanelFifthRow.getLayoutParams();
						paramsLinearLayoutThirdRowRightPanelFifthRow.width = paramsLinearLayoutThirdRowRightPanel.width;
						paramsLinearLayoutThirdRowRightPanelFifthRow.height = (int)(paramsLinearLayoutThirdRowRightPanel.height * HEIGHT_ROW_THIRD_ROW_RIGHT_PANEL);
						linearLayoutThirdRowRightPanelFifthRow.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFifthRow);

							LinearLayout linearLayoutThirdRowRightPanelFifthRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFifthRowP1);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFifthRowP1 = linearLayoutThirdRowRightPanelFifthRowP1.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelFifthRowP1.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.23);
							paramsLinearLayoutThirdRowRightPanelFifthRowP1.height = paramsLinearLayoutThirdRowRightPanelFifthRow.height;
							linearLayoutThirdRowRightPanelFifthRowP1.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFifthRowP1);
								TextView textViewThirdRowRightPanelFifthRowP1Value = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFifthRowP1Value);

							LinearLayout linearLayoutThirdRowRightPanelFifthRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFifthRowP2);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFifthRowP2 = linearLayoutThirdRowRightPanelFifthRowP2.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelFifthRowP2.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.1);
							paramsLinearLayoutThirdRowRightPanelFifthRowP2.height = paramsLinearLayoutThirdRowRightPanelFifthRow.height;
							linearLayoutThirdRowRightPanelFifthRowP2.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFifthRowP2);
								TextView textViewThirdRowRightPanelFifthRowP2Units = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFifthRowP2Units);

							LinearLayout linearLayoutThirdRowRightPanelFifthRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFifthRowP3);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFifthRowP3 = linearLayoutThirdRowRightPanelFifthRowP3.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelFifthRowP3.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.23);
							paramsLinearLayoutThirdRowRightPanelFifthRowP3.height = paramsLinearLayoutThirdRowRightPanelFifthRow.height;
							linearLayoutThirdRowRightPanelFifthRowP3.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFifthRowP3);
								TextView textViewThirdRowRightPanelFifthRowP3Value = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFifthRowP3Value);

							LinearLayout linearLayoutThirdRowRightPanelFifthRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFifthRowP4);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFifthRowP4 = linearLayoutThirdRowRightPanelFifthRowP4.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelFifthRowP4.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.1);
							paramsLinearLayoutThirdRowRightPanelFifthRowP4.height = paramsLinearLayoutThirdRowRightPanelFifthRow.height;
							linearLayoutThirdRowRightPanelFifthRowP4.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFifthRowP4);
								TextView textViewThirdRowRightPanelFifthRowP4Units = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFifthRowP4Units);

							LinearLayout linearLayoutThirdRowRightPanelFifthRowP5 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFifthRowP5);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFifthRowP5 = linearLayoutThirdRowRightPanelFifthRowP5.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelFifthRowP5.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.23);
							paramsLinearLayoutThirdRowRightPanelFifthRowP5.height = paramsLinearLayoutThirdRowRightPanelFifthRow.height;
							linearLayoutThirdRowRightPanelFifthRowP5.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFifthRowP5);
								TextView textViewThirdRowRightPanelFifthRowP5Value = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFifthRowP5Value);

							LinearLayout linearLayoutThirdRowRightPanelFifthRowP6 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelFifthRowP6);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelFifthRowP6 = linearLayoutThirdRowRightPanelFifthRowP6.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelFifthRowP6.width = (int)((paramsLinearLayoutThirdRowRightPanel.width - 40)* 0.1);
							paramsLinearLayoutThirdRowRightPanelFifthRowP6.height = paramsLinearLayoutThirdRowRightPanelFifthRow.height;
							linearLayoutThirdRowRightPanelFifthRowP6.setLayoutParams(paramsLinearLayoutThirdRowRightPanelFifthRowP6);
								TextView textViewThirdRowRightPanelFifthRowP6Units = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelFifthRowP6Units);

						LinearLayout linearLayoutThirdRowRightPanelSixthSeventhRows = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSixthSeventhRows);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSixthSeventhRows = linearLayoutThirdRowRightPanelSixthSeventhRows.getLayoutParams();
						paramsLinearLayoutThirdRowRightPanelSixthSeventhRows.width = paramsLinearLayoutThirdRowRightPanel.width - 40;
						paramsLinearLayoutThirdRowRightPanelSixthSeventhRows.height = (int)(paramsLinearLayoutThirdRowRightPanel.height * HEIGHT_ROW_THIRD_ROW_RIGHT_PANEL * 2);
						linearLayoutThirdRowRightPanelSixthSeventhRows.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSixthSeventhRows);

							LinearLayout linearLayoutThirdRowRightPanelSixthSeventhRowsLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSixthSeventhRowsLeft);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeft = linearLayoutThirdRowRightPanelSixthSeventhRowsLeft.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeft.width = (int)(paramsLinearLayoutThirdRowRightPanelSixthSeventhRows.width * 0.66);
							linearLayoutThirdRowRightPanelSixthSeventhRowsLeft.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeft);

								LinearLayout linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSixthRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSixthRow);
								ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSixthRow = linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSixthRow.getLayoutParams();
								paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSixthRow.height = (int)(paramsLinearLayoutThirdRowRightPanel.height * HEIGHT_ROW_THIRD_ROW_RIGHT_PANEL);
								linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSixthRow.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSixthRow);
									TextView textViewThirdRowRightPanelSixthSeventhRowsLeftSixthRowLabel = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelSixthSeventhRowsLeftSixthRowLabel);

								LinearLayout linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRow);
								ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRow = linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRow.getLayoutParams();
								paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRow.height = (int)(paramsLinearLayoutThirdRowRightPanel.height * HEIGHT_ROW_THIRD_ROW_RIGHT_PANEL);
								linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRow.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRow);

									LinearLayout linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP1);
									ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP1 = linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP1.getLayoutParams();
									paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP1.width = (int)(paramsLinearLayoutThirdRowRightPanelSixthSeventhRows.width * 0.23);
									linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP1.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP1);
										TextView textViewThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP1Value = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP1Value);

									LinearLayout linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP2);
									ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP2 = linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP2.getLayoutParams();
									paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP2.width = (int)(paramsLinearLayoutThirdRowRightPanelSixthSeventhRows.width * 0.1);
									linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP2.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP2);
										TextView textViewThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP2Units = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP2Units);

									LinearLayout linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP3);
									ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP3 = linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP3.getLayoutParams();
									paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP3.width = (int)(paramsLinearLayoutThirdRowRightPanelSixthSeventhRows.width * 0.23);
									linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP3.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP3);
										TextView textViewThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP3Value = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP3Value);

									LinearLayout linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP4);
									ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP4 = linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP4.getLayoutParams();
									paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP4.width = (int)(paramsLinearLayoutThirdRowRightPanelSixthSeventhRows.width * 0.1);
									linearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP4.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP4);
										TextView textViewThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP4Units = (TextView) rootView.findViewById(R.id.textViewThirdRowRightPanelSixthSeventhRowsLeftSeventhRowP4Units);

							LinearLayout linearLayoutThirdRowRightPanelSixthSeventhRowsRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowRightPanelSixthSeventhRowsRight);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsRight = linearLayoutThirdRowRightPanelSixthSeventhRowsRight.getLayoutParams();
							paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsRight.width = (int)(paramsLinearLayoutThirdRowRightPanelSixthSeventhRows.width * 0.33);
							linearLayoutThirdRowRightPanelSixthSeventhRowsRight.setLayoutParams(paramsLinearLayoutThirdRowRightPanelSixthSeventhRowsRight);
								Button buttonThirdRowRightPanelSixthSeventhRowsRightButton = (Button) rootView.findViewById(R.id.buttonThirdRowRightPanelSixthSeventhRowsRightButton);
//-END RIGHT PANEL
//-END THIRD ROW

//-END LEFT COLUMN

//-RIGHT COLUMN
			// Linear layout "LinearLayoutRightColumn"
			LinearLayout linearLayoutRightColumn = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumn);
			ViewGroup.LayoutParams paramsLinearLayoutRightColumn = linearLayoutRightColumn.getLayoutParams();
			paramsLinearLayoutRightColumn.width = (int)(paramsRelativeLayoutGauges.width * 0.26);
			paramsLinearLayoutRightColumn.height = (int)(Globals.heightScreen * 0.56+36);
			linearLayoutRightColumn.setX(paramsLinearLayoutLefColumn.width-paramsLinearLayoutRightColumn.width);
			linearLayoutRightColumn.setLayoutParams(paramsLinearLayoutRightColumn);

				// Linear layout "LinearLayoutRightColumnRightColumnTitleScreen"
				LinearLayout linearLayoutRightColumnTitleScreen = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnTitleScreen);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnTitleScreen = linearLayoutRightColumnTitleScreen.getLayoutParams();
				paramsLinearLayoutRightColumnTitleScreen.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutRightColumnTitleScreen.height = (int)(paramsLinearLayoutRightColumn.height * 0.13);
				linearLayoutRightColumnTitleScreen.setLayoutParams(paramsLinearLayoutRightColumnTitleScreen);
					TextView textViewRightColumnTitleScreen = (TextView) rootView.findViewById(R.id.textViewRightColumnTitleScreen);
					Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 
					textViewRightColumnTitleScreen.setTypeface(type);
					textViewRightColumnTitleScreen.setTypeface(textViewRightColumnTitleScreen.getTypeface(), Typeface.ITALIC);

			linearLayoutRightColumnFirstSecondRowsBigGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnFirstSecondRowsBigGauge);
			paramsLinearLayoutRightColumnFirstSecondRowBigGauge = linearLayoutRightColumnFirstSecondRowsBigGauge.getLayoutParams();
			paramsLinearLayoutRightColumnFirstSecondRowBigGauge.width = paramsLinearLayoutRightColumn.width - MARGIN_RELATIVE_LAYOUT_GAUGE;
			paramsLinearLayoutRightColumnFirstSecondRowBigGauge.height = paramsLinearLayoutRightColumn.width - MARGIN_RELATIVE_LAYOUT_GAUGE;
			linearLayoutRightColumnFirstSecondRowsBigGauge.setLayoutParams(paramsLinearLayoutRightColumnFirstSecondRowBigGauge);
			gaugeFirstSecondRowsBigGauge = createCircularGauge(8,linearLayoutRightColumnFirstSecondRowsBigGauge,paramsLinearLayoutRightColumnFirstSecondRowBigGauge);
			linearLayoutRightColumnFirstSecondRowsBigGauge.addView(gaugeFirstSecondRowsBigGauge);

			LinearLayout linearLayoutRightColumnSecondRowTime = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnSecondRowTime);
			ViewGroup.LayoutParams paramsLinearlnearLayoutRightColumnSecondRowTime = linearLayoutRightColumnSecondRowTime.getLayoutParams();
			paramsLinearlnearLayoutRightColumnSecondRowTime.width = paramsLinearLayoutRightColumn.width - 20;
			paramsLinearlnearLayoutRightColumnSecondRowTime.height = (int)(paramsLinearLayoutRightColumn.height * 0.07);
			linearLayoutRightColumnSecondRowTime.setLayoutParams(paramsLinearlnearLayoutRightColumnSecondRowTime);
				LinearLayout linearLayoutRightColumnSecondRowTimeInside = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnSecondRowTimeInside);
				ViewGroup.LayoutParams paramsLinearLayoutRightColumnSecondRowTimeInside = linearLayoutRightColumnSecondRowTimeInside.getLayoutParams();
				paramsLinearLayoutRightColumnSecondRowTimeInside.width = (int)(paramsLinearlnearLayoutRightColumnSecondRowTime.width * 0.6);
				linearLayoutRightColumnSecondRowTimeInside.setLayoutParams(paramsLinearLayoutRightColumnSecondRowTimeInside);
					LinearLayout linearLayoutRightColumnSecondRowTimeInsideLeft = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnSecondRowTimeInsideLeft);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnSecondRowTimeInsideLeft = linearLayoutRightColumnSecondRowTimeInsideLeft.getLayoutParams();
					paramsLinearLayoutRightColumnSecondRowTimeInsideLeft.width = (int)(paramsLinearLayoutRightColumnSecondRowTimeInside.width * 0.4);
					linearLayoutRightColumnSecondRowTimeInsideLeft.setLayoutParams(paramsLinearLayoutRightColumnSecondRowTimeInsideLeft);
						TextView textViewRightColumnSecondRowTimeInsideLeftLabel = (TextView) rootView.findViewById(R.id.textViewRightColumnSecondRowTimeInsideLeftLabel);
					LinearLayout linearLayoutRightColumnSecondRowTimeInsideRight = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumnSecondRowTimeInsideRight);
					ViewGroup.LayoutParams paramsLinearLayoutRightColumnSecondRowTimeInsideRight = linearLayoutRightColumnSecondRowTimeInsideRight.getLayoutParams();
					paramsLinearLayoutRightColumnSecondRowTimeInsideRight.width = (int)(paramsLinearLayoutRightColumnSecondRowTimeInside.width * 0.6);
					linearLayoutRightColumnSecondRowTimeInsideRight.setLayoutParams(paramsLinearLayoutRightColumnSecondRowTimeInsideRight);
						textViewRightColumnSecondRowTimeInsideRightTime = (TextView) rootView.findViewById(R.id.textViewRightColumnSecondRowTimeInsideRightTime);
						date = new Date();
						textViewRightColumnSecondRowTimeInsideRightTime.setText(DateFormat.format("h:mm a", date));
//-END RIGHT COLUMN

// -BUTTONS
		// Linear layout "LinearLayoutGauges"
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
				buttonHistory.setLayoutParams(paramsButtonHistory);

			LinearLayout linearLayoutButtonFuel = (LinearLayout) rootView.findViewById(R.id.linearLayoutButtonFuel);
			ViewGroup.LayoutParams paramsLinearLayoutButtonFuel = linearLayoutButtonFuel.getLayoutParams();
			paramsLinearLayoutButtonFuel.width = paramsLinearLayoutButtons.width / 6;
			linearLayoutButtonFuel.setLayoutParams(paramsLinearLayoutButtonFuel);
				Button buttonFuel = (Button) rootView.findViewById(R.id.buttonFuel);
				ViewGroup.LayoutParams paramsButtonFuel = buttonFuel.getLayoutParams();
				paramsButtonFuel.height = (int)(paramsLinearLayoutButtons.height * 0.65);
				buttonFuel.setAlpha(0.5f);
				buttonFuel.setEnabled(false);
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

	public RelativeLayout createCircularGauge(int numberGauge,LinearLayout linearLayout,ViewGroup.LayoutParams paramsLinearLayout) {
		RelativeLayout gauge = new RelativeLayout(Globals.context);
		LinearLayout.LayoutParams paramsRelativeLayoutGauge = new LinearLayout.LayoutParams(paramsLinearLayout.width-
				2*MARGIN_RELATIVE_LAYOUT_GAUGE,paramsLinearLayout.height-2*MARGIN_RELATIVE_LAYOUT_GAUGE);
		gauge.setX(MARGIN_RELATIVE_LAYOUT_GAUGE);
		gauge.setY(MARGIN_RELATIVE_LAYOUT_GAUGE);
		gauge.setLayoutParams(paramsRelativeLayoutGauge);

		String stringGauge=null;
		// GAUGE
		// Read SVG file
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			if(SEGMENTS_GAUGE[numberGauge]==5) {
				docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_5_segments_background));
			}
			else if(SEGMENTS_GAUGE[numberGauge]==6) {
				docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_6_segments_background));
			}
			else
				docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_7_segments_background));
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		// Prepare gauge

		// Incorporate thresholds
		String commandSQL="SELECT * FROM AlarmParameters WHERE Parameter_AlarmParameters='"+parameter[numberGauge]+"'";
		cursor = dewLineDB.rawQuery(commandSQL,null);
		if(cursor.getCount()>0) {
			cursor.moveToFirst();
			assignValuesThresholdVariablesStatic();
			paintDashes(numberGauge);
		}

		// Add scale to gauge
		putScaleCircularGauge(numberGauge);

		try {
			DOMSource domSource = new DOMSource(docGauge);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			stringGauge = writer.toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		// Create gauge
		try {
			SVG svg = SVG.getFromString(stringGauge);
			svgImageView[numberGauge] = new SVGImageView(Globals.context);
			svgImageView[numberGauge].setSVG(svg);
			LayoutParams paramsSVG=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			svgImageView[numberGauge].setLayoutParams(paramsSVG);
			gauge.addView(svgImageView[numberGauge]);

			// Add icon to gauge
			LinearLayout linearLayoutIcon = new LinearLayout(Globals.context);
			LinearLayout.LayoutParams paramsLinearLayoutIcon = new LinearLayout.LayoutParams(paramsLinearLayout.width/5,
					paramsLinearLayout.height/6);
			linearLayoutIcon.setX(paramsLinearLayout.width*2/5-ADJUST_POSITION_ICON_X);
			linearLayoutIcon.setY(paramsLinearLayout.height/2-paramsLinearLayout.height*3/12);
			linearLayoutIcon.setLayoutParams(paramsLinearLayoutIcon);
			ImageView imageViewIcon = new ImageView(Globals.context);
			imageViewIcon.setImageResource(getDrawable(Globals.context,NAME_ICONS_CIRCULAR_GAUGES[numberGauge]));
			imageViewIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageViewIcon.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			linearLayoutIcon.addView(imageViewIcon);
			gauge.addView(linearLayoutIcon);

			// Add text value to gauge
			LinearLayout linearLayoutTextValue = new LinearLayout(Globals.context);
			LinearLayout.LayoutParams paramsLinearLayoutValueGauge = new LinearLayout.LayoutParams(paramsLinearLayout.width/4,
					paramsLinearLayout.height/6);
			linearLayoutTextValue.setGravity(Gravity.CENTER);
			linearLayoutTextValue.setX(paramsLinearLayout.width*3/8-ADJUST_POSITION_VALUE_GAUGE_X);
			linearLayoutTextValue.setBackgroundResource(R.drawable.border_with_background);
			linearLayoutTextValue.setY(paramsLinearLayout.height/2+paramsLinearLayout.height/36);
			linearLayoutTextValue.setLayoutParams(paramsLinearLayoutValueGauge);
			textViewValueGauge[numberGauge] = new TextView(Globals.context);
			textViewValueGauge[numberGauge].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			textViewValueGauge[numberGauge].setGravity(Gravity.CENTER);
			textViewValueGauge[numberGauge].setTextSize(TypedValue.COMPLEX_UNIT_PX,20);
			textViewValueGauge[numberGauge].setTextColor(Color.BLACK);
			textViewValueGauge[numberGauge].setTypeface(textViewValueGauge[numberGauge].getTypeface(), Typeface.BOLD);
			textViewValueGauge[numberGauge].setText(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0]);
			linearLayoutTextValue.addView(textViewValueGauge[numberGauge]);
			gauge.addView(linearLayoutTextValue);

			// Add units label to gauge
			LinearLayout linearLayoutUnits = new LinearLayout(Globals.context);
			LinearLayout.LayoutParams paramsLinearLayoutUnits = new LinearLayout.LayoutParams(paramsLinearLayout.width/4,
					paramsLinearLayout.height/6);
			linearLayoutUnits.setGravity(Gravity.CENTER);
			linearLayoutUnits.setX((int)(paramsLinearLayout.width * 0.55));
			linearLayoutUnits.setY((int)(paramsLinearLayout.height *0.69));
			linearLayoutUnits.setBackgroundColor(Color.parseColor("#00000000"));
			linearLayoutUnits.setLayoutParams(paramsLinearLayoutUnits);
			TextView textViewUnits = new TextView(Globals.context);
			textViewUnits.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			textViewUnits.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
			textViewUnits.setTextSize(TypedValue.COMPLEX_UNIT_PX,17);
			textViewUnits.setTextColor(Color.BLACK);
			textViewUnits.setTypeface(textViewUnits.getTypeface(), Typeface.BOLD);
			textViewUnits.setText(UNITS_CIRCULAR_GAUGES_SAE[numberGauge]);
			linearLayoutUnits.addView(textViewUnits);
			gauge.addView(linearLayoutUnits);

			// Add needle
			linearLayoutNeedle[numberGauge] = new LinearLayout(Globals.context);
			LinearLayout.LayoutParams paramsLinearLayoutNeedle = new LinearLayout.LayoutParams(paramsRelativeLayoutGauge.width-
					2*marginNeedle,paramsRelativeLayoutGauge.height-2*marginNeedle);
			linearLayoutNeedle[numberGauge].setX(marginNeedle+ADJUST_POSITION_NEEDLE_X);
			linearLayoutNeedle[numberGauge].setY(marginNeedle);
			linearLayoutNeedle[numberGauge].setBackgroundColor(Color.parseColor("#00FFFFFF"));
			linearLayoutNeedle[numberGauge].setLayoutParams(paramsLinearLayoutNeedle);
			gauge.addView(linearLayoutNeedle[numberGauge]);
			svg = SVG.getFromResource(Globals.context, R.raw.needle_background);
			SVGImageView svgImageViewNeedle = new SVGImageView(Globals.context);
			svgImageViewNeedle.setSVG(svg);
			svgImageViewNeedle.setLayoutParams(
					new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			linearLayoutNeedle[numberGauge].addView(svgImageViewNeedle);
			linearLayoutNeedle[numberGauge].animate().rotation(-90).start();
		} catch (SVGParseException e){
			e.printStackTrace();
		}

		return gauge;
	}

	public void putScaleCircularGauge (int numberGauge) {
		NamedNodeMap attr;
		Node positionX,text;
		String numberScale;
		int lengthNumber;

		for (int i=0; i < docGauge.getElementsByTagName("text").getLength(); i++) {
			text = docGauge.getElementsByTagName("text").item(i);
			attr = text.getAttributes();
			positionX = attr.getNamedItem("x");
			numberScale = SCALES_CIRCULAR_GAUGES_SAE[numberGauge][i];
			lengthNumber = numberScale.length();
			if(unitsSystem.equals("SAE")) {
				switch(SEGMENTS_GAUGE[numberGauge]) {
					case 5:
						if(i==0) {
							if(lengthNumber==2)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER / 2));
							else if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						else if(i==1) {
							if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						else if(i==4) {
							if(lengthNumber==2)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER / 2));
							else if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
							if(lengthNumber==4)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - 3 * SHIFT_X_SCALE_GAUGE_PER_NUMBER / 2));
						}
						else if(i==5) {
							if(lengthNumber==2)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
							else if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - 2 * SHIFT_X_SCALE_GAUGE_PER_NUMBER));
							else if(lengthNumber==4)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - 2 * SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						break;
					case 6:
						if(i==0) {
							if(lengthNumber==2)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER / 2));
							else if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						else if(i==1) {
							if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						else if(i==5) {
							if(lengthNumber==2)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER / 2));
							else if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						else if(i==6) {
							if(lengthNumber==2)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER / 2));
							else if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						break;
					case 7:
						if(i==0) {
							if(lengthNumber==2)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER / 2));
							else if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						else if(i==1) {
							if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						else if(i==6) {
							if(lengthNumber==2)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER / 2));
							else if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						else if(i==7) {
							if(lengthNumber==2)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER / 2));
							else if(lengthNumber==3)
								positionX.setNodeValue(String.format("%.5f",Double.parseDouble(attr.getNamedItem("x").
										getNodeValue()) - SHIFT_X_SCALE_GAUGE_PER_NUMBER));
						}
						break;
				}
			}
			text.setTextContent(numberScale);
		}
	}

	public void assignValuesThresholdVariablesStatic() {
		try {
			lowThresholdShutdown_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			lowThresholdShutdown_Idle = -1;
		}
		try {
			lowThresholdRed_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			lowThresholdRed_Idle = -1;
		}
		try {
			lowThresholdYellow_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			lowThresholdYellow_Idle = -1;
		}

		try {
			highThresholdYellow_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			highThresholdYellow_Idle = -1;
		}
		try {
			highThresholdRed_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			highThresholdRed_Idle = -1;
		}
		try {
			highThresholdShutdown_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			highThresholdShutdown_Idle = -1;
		}
	}

	public void paintDashes(int numberGauge) {
		Node dash;
		NamedNodeMap attr;
		Node style;

		int numberDashes = docGauge.getElementsByTagName("path").getLength();
		int iInitial=0;
		float valueBetweenDashes=0.0f;
		switch(SEGMENTS_GAUGE[numberGauge]) {
			case 5:
				iInitial = 1;
				valueBetweenDashes = (Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][5]) -
						Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0])) / (DASHES_CIRCULAR_GAUGES[0] - 1);
				break;
			case 6:
				iInitial = 0;
				valueBetweenDashes = (Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][6]) -
						Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0])) / (DASHES_CIRCULAR_GAUGES[1] - 1);
				break;
			case 7:
				iInitial = 0;
				valueBetweenDashes = (Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][7]) -
						Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0])) / (DASHES_CIRCULAR_GAUGES[2] - 1);
				if(numberGauge==8)
					valueBetweenDashes *= 100;
				break;
		}
		for (int i=iInitial; i<numberDashes; ++i) {
			dash = docGauge.getElementsByTagName("path").item(i);
			attr = dash.getAttributes();
			style = attr.getNamedItem("style");
			// LOW THRESHOLDS
			if(lowThresholdRed_Idle>0&&i!=iInitial&&Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0]) +
					valueBetweenDashes*(i-iInitial)<=lowThresholdRed_Idle) {
				style.setTextContent("stroke:#FF0000;stroke-width:0.5px");
			}
			else if(lowThresholdYellow_Idle>0&&i!=iInitial&&Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0]) +
					valueBetweenDashes*(i-iInitial)<=lowThresholdYellow_Idle) {
				style.setTextContent("stroke:#FFDF00;stroke-width:0.6px");
			}
			// HIGH THRESHOLDS
			else if(highThresholdRed_Idle>0&&Math.round(Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0]) +
					valueBetweenDashes*(i-iInitial))>=highThresholdRed_Idle) {
				style.setTextContent("stroke:#FF0000;stroke-width:0.5px");
			}
			else if(highThresholdYellow_Idle>0&&Math.round(Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0]) +
					valueBetweenDashes*(i-iInitial))>=highThresholdYellow_Idle) {
				style.setTextContent("stroke:#FFDF00;stroke-width:0.6px");
			}
			else {
				style.setTextContent("stroke:#000000;stroke-width:0.6px");
			}
		}
	}

	public void updateFaceCircularGauge(int numberGauge,LinearLayout linearLayoutGauge,ViewGroup.LayoutParams paramsLinearLayout,
			float valueParameter) {
		String commandSQL="SELECT * FROM AlarmParameters WHERE Parameter_AlarmParameters='"+parameter[numberGauge]+"'";
		cursor = dewLineDB.rawQuery(commandSQL,null);
		if(cursor.getCount()>0) {
			cursor.moveToFirst();
			assignValuesThresholdVariablesStatic();
		}
		else
			return;
		// LOW THRESHOLDS
		if(lowThresholdRed_Idle>0&&Math.round(valueParameter)!=Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0])&&
				valueParameter<=lowThresholdRed_Idle) {
			if(actualFaceDisplayed[numberGauge]!=2) {
				linearLayoutGauge.setBackgroundResource(R.drawable.ringed_gauge_face_with_red_alert);
				actualFaceDisplayed[numberGauge] = 2;
			}
		}
		else if(lowThresholdYellow_Idle>0&&Math.round(valueParameter)!=Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0])&&
				valueParameter<=lowThresholdYellow_Idle) {
			if(actualFaceDisplayed[numberGauge]!=1) {
				linearLayoutGauge.setBackgroundResource(R.drawable.ringed_gauge_face_with_yellow_alert);
				actualFaceDisplayed[numberGauge] = 1;
			}
		}
		// HIGH THRESHOLDS
		else if(highThresholdRed_Idle>0&&Math.round(valueParameter)!=Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0])&&
				valueParameter>=highThresholdRed_Idle) {
			if(actualFaceDisplayed[numberGauge]!=2) {
				linearLayoutGauge.setBackgroundResource(R.drawable.ringed_gauge_face_with_red_alert);
				actualFaceDisplayed[numberGauge] = 2;
			}
		}
		else if(highThresholdYellow_Idle>0&&Math.round(valueParameter)!=Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0])&&
				valueParameter>=highThresholdYellow_Idle) {
			if(actualFaceDisplayed[numberGauge]!=1) {
				linearLayoutGauge.setBackgroundResource(R.drawable.ringed_gauge_face_with_yellow_alert);
				actualFaceDisplayed[numberGauge] = 1;
			}
		}
		else {
			if(actualFaceDisplayed[numberGauge]!=0) {
				linearLayoutGauge.setBackgroundResource(R.drawable.gauge_background);
				actualFaceDisplayed[numberGauge] = 0;
			}
		}
	}

	public LinearLayout createLinearGauge(int numberGauge,LinearLayout linearLayout,ViewGroup.LayoutParams paramsLinearLayout) {
		LinearLayout linearGauge = new LinearLayout(Globals.context);
		LinearLayout.LayoutParams paramsLinearLayoutGauge = new LinearLayout.LayoutParams(paramsLinearLayout.width,
				paramsLinearLayout.height-20);
		paramsLinearLayoutGauge.topMargin = 10;
		linearGauge.setLayoutParams(paramsLinearLayoutGauge);

		String stringLinearGauge=null;
		SVGImageView svgImageView;
		// GAUGE
		// Read SVG file
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			if(numberGauge==0||numberGauge==1)
				docLinearGauge[numberGauge] = builder.parse(Globals.context.getResources().
						openRawResource(R.raw.gauge_linear_4_segments_1_background));
			else
					docLinearGauge[numberGauge] = builder.parse(Globals.context.getResources().
							openRawResource(R.raw.gauge_linear_4_segments_2_background));
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		// Prepare gauge

		// Add scale to gauge
		putScaleLinearGauge(numberGauge);

		try {
			DOMSource domSource = new DOMSource(docLinearGauge[numberGauge]);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			stringLinearGauge = writer.toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		// Create gauge
		try {
			SVG svg = SVG.getFromString(stringLinearGauge);
			svgImageView = new SVGImageView(Globals.context);
			svgImageView.setSVG(svg);
			LayoutParams paramsSVG=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			svgImageView.setLayoutParams(paramsSVG);
			linearGauge.addView(svgImageView);

		} catch (SVGParseException e){
			e.printStackTrace();
		}

		return linearGauge;
	}

	public void putScaleLinearGauge (int numberGauge) {
		NamedNodeMap attr;
		Node text;
		String numberScale;

		for (int i=0; i < docLinearGauge[numberGauge].getElementsByTagName("text").getLength(); i++) {
			text = docLinearGauge[numberGauge].getElementsByTagName("text").item(i);
			attr = text.getAttributes();
			numberScale = SCALES_LINEAR_GAUGES_SAE[numberGauge][i];
			text.setTextContent(numberScale);
		}
	}

	public static int getDrawable(Context context, String icon)
	{
		Assert.assertNotNull(context);
		Assert.assertNotNull(icon);

		return context.getResources().getIdentifier(icon,"drawable", context.getPackageName());
	}

	public void simulatorCircularGaugeDemoRPM() {
		float valueParameterCurrent=ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES_RPM[ConstantsGlobalsFuelScreen.
					counter_demo_data_rpm]/100;
		if(valueParameterCurrent==valueParameterCircularGaugePrevious[positionBigGauge]) {
			ConstantsGlobalsFuelScreen.counter_demo_data_rpm++;
			if(ConstantsGlobalsFuelScreen.counter_demo_data_rpm== ConstantsGlobalsFuelScreen.NUMBER_DEMO_DATA_RPM)
				ConstantsGlobalsFuelScreen.counter_demo_data_rpm = 0;
			return;
		}

		valueMinimum = 0;
		try 
		{
			valueMaximum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[positionBigGauge][7]);
		}
		catch (ParseException e) 
		{
			valueMaximum = 0;
		}
		valueRPMCurrent = ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES_RPM[ConstantsGlobalsFuelScreen.counter_demo_data_rpm];
		if(valueRPMCurrent/100<100)
			textViewValueGauge[positionBigGauge].setText(zeroDecForm.format(valueRPMCurrent));
		else
			textViewValueGauge[positionBigGauge].setText(oneDecForm.format(valueRPMCurrent));
		positionDegrees = Math.round((ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES_RPM[ConstantsGlobalsFuelScreen.
					counter_demo_data_rpm]/100 - valueMinimum) * (MAXIMUM_ANGLE_GAUGES[2] - (-90)) /
					(valueMaximum - valueMinimum) + (-90));
		linearLayoutNeedle[positionBigGauge].animate().rotation(positionDegrees).start();
		valueParameterCircularGaugePrevious[positionBigGauge] = valueParameterCurrent;

		updateFaceCircularGauge(positionBigGauge,linearLayoutRightColumnFirstSecondRowsBigGauge,
				paramsLinearLayoutRightColumnFirstSecondRowBigGauge,valueParameterCurrent*100);

		ConstantsGlobalsFuelScreen.counter_demo_data_rpm++;
		if(ConstantsGlobalsFuelScreen.counter_demo_data_rpm==ConstantsGlobalsFuelScreen.NUMBER_DEMO_DATA_RPM)
			ConstantsGlobalsFuelScreen.counter_demo_data_rpm = 0;
	}

	public void simulatorCircularGaugesDemo () {
		float valueParameterCurrent;
		RelativeLayout gauge;
		int indexSampleData;

		for(int i=0;i<NUMBER_CIRCULAR_GAUGES-1;i++) {
			indexSampleData = ConstantsGlobalsFuelScreen.counter_demo_data_rpm / ConstantsGlobalsFuelScreen.RELATION_SAMPLES_RPM_OTHERS;
			switch(i) {
				case 5:
					if(ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES[7][indexSampleData]>0.0f)
						valueParameterCurrent = (ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES[2][indexSampleData] +
								ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES[3][indexSampleData]) /
								(ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES[7][indexSampleData] * 1.15078f);
					else
						valueParameterCurrent = 0.0f;
					break;
				case 6:
					valueParameterCurrent = ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES[2][indexSampleData] +
							ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES[3][indexSampleData];
					break;
				default:
					valueParameterCurrent = ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES[i][indexSampleData];
			}

/*
			// Create gauge dynamic
			String commandSQL="SELECT * FROM AlarmParameters WHERE Parameter_AlarmParameters='"+parameter[i]+"'";
			cursor = dewLineDB.rawQuery(commandSQL,null);
			if(cursor.getCount()>0) {
				cursor.moveToFirst();
				if(cursor.getString(cursor.getColumnIndex("Dynamic_AlarmParameters")).equals("Yes")) {
					if(!(lowThresholdRed_Idle==valueThresholdsDynamicPrevious[positionThresholdsDynamicPrevious[i]][1]&&
								lowThresholdYellow_Idle==valueThresholdsDynamicPrevious[positionThresholdsDynamicPrevious[i]][2]&&
								highThresholdYellow_Idle==valueThresholdsDynamicPrevious[positionThresholdsDynamicPrevious[i]][3]&&
								highThresholdYellow_Idle==valueThresholdsDynamicPrevious[positionThresholdsDynamicPrevious[i]][4])) {
						valueThresholdsDynamicPrevious[positionThresholdsDynamicPrevious[i]][1] = lowThresholdRed_Idle;
						valueThresholdsDynamicPrevious[positionThresholdsDynamicPrevious[i]][2] = lowThresholdYellow_Idle;
						valueThresholdsDynamicPrevious[positionThresholdsDynamicPrevious[i]][3] = highThresholdYellow_Idle;
						valueThresholdsDynamicPrevious[positionThresholdsDynamicPrevious[i]][4] = highThresholdYellow_Idle;

						ViewGroup parent = (ViewGroup) svgImageView[i].getParent();
						if(parent!=null) {
							final int index = parent.indexOfChild(svgImageView[i]);
							parent.removeView(svgImageView[i]);
							createSVGCircularGaugeDynamic(i);
							parent.addView(svgImageView[i], index);
						}
					}
				}
			}
*/

			valueMinimum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[i][0]);
			try 
			{
				valueMaximum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[i][SEGMENTS_GAUGE[i]]);
			}
			catch (ParseException e) 
			{
				valueMaximum = 0;
			}
			if(valueParameterCurrent<100)
				textViewValueGauge[i].setText(oneDecForm.format(valueParameterCurrent));
			else
				textViewValueGauge[i].setText(zeroDecForm.format(valueParameterCurrent));

			int maximumAngleGauge=MAXIMUM_ANGLE_GAUGES[0];
			switch(SEGMENTS_GAUGE[i]) {
				case 5:
					maximumAngleGauge = MAXIMUM_ANGLE_GAUGES[0];
					break;
				case 6:
					maximumAngleGauge = MAXIMUM_ANGLE_GAUGES[1];
					break;
				case 7:
					maximumAngleGauge = MAXIMUM_ANGLE_GAUGES[2];
					break;
			}
			positionDegrees = Math.round((valueParameterCurrent - valueMinimum) * (maximumAngleGauge - (-90)) /
					(valueMaximum - valueMinimum) + (-90));
			linearLayoutNeedle[i].animate().rotation(positionDegrees).start();
//System.out.println(ConstantsGlobalsFuelScreen.DEMO_VALUES_CIRCULAR_GAUGES[i][ConstantsGlobalsFuelScreen.counter_demo_data[i]]);
			valueParameterCircularGaugePrevious[i] = valueParameterCurrent;

// CHANGE WITH NEW CIRCULAR GAUGES
			switch(i) {
				case 0:
					updateFaceCircularGauge(i,linearLayoutFirstRowFirstGauge,paramsLinearLayoutFirstRowFirstGauge,
							valueParameterCurrent);
					break;
				case 1:
					updateFaceCircularGauge(i,linearLayoutFirstRowSecondGauge,paramsLinearLayoutFirstRowSecondGauge,
							valueParameterCurrent);
					break;
				case 8:
					updateFaceCircularGauge(i,linearLayoutRightColumnFirstSecondRowsBigGauge,
							paramsLinearLayoutRightColumnFirstSecondRowBigGauge,valueParameterCurrent);
					break;
			}
		}
	}

	public void simulatorLinearGaugesDemo () {
		NamedNodeMap attr;
		Node rect;
		Node idAttr,yAttr;
		double yMainRect=0,yCeroMainRect=0,yCeroRectAnim=0;
		double heightMainRect=0,heightAnimRect=0;
		String styleString="";
		SVGImageView svgImageView;

		for(int j=0;j<NUMBER_LINEAR_GAUGES;j++) {
			String stringLinearGauge=null;
			if(j==0||j==1) {
				valueMinimum = 0;
				valueMaximum = 1;
			}
			else {
				valueMinimum = Float.parseFloat(SCALES_LINEAR_GAUGES_SAE[j][0]);
				valueMaximum = Float.parseFloat(SCALES_LINEAR_GAUGES_SAE[j][4]);
			}
			for (int i=0; i < docLinearGauge[j].getElementsByTagName("rect").getLength(); ++i) {
				rect = docLinearGauge[j].getElementsByTagName("rect").item(i);
				attr = rect.getAttributes();
				idAttr = attr.getNamedItem("id");
				if(idAttr.getNodeValue().equals("mainRect")) {
					yMainRect = Double.parseDouble(attr.getNamedItem("y").getNodeValue());
					heightMainRect = Double.parseDouble(attr.getNamedItem("height").getNodeValue());
					heightAnimRect = heightMainRect * (ConstantsGlobalsFuelScreen.DEMO_VALUES_LINEAR_GAUGES[j][ConstantsGlobalsFuelScreen.
							counterDemoDataLinearGauges[j]] - valueMinimum) / (valueMaximum - valueMinimum);
					yCeroMainRect = yMainRect + heightMainRect;
					yCeroRectAnim = yCeroMainRect - heightAnimRect;
				}
				else if(idAttr.getNodeValue().equals("animRect")) {
					yAttr = attr.getNamedItem("y");
					yAttr.setTextContent(Double.toString(yCeroRectAnim));
					attr.getNamedItem("height").setTextContent(Double.toString(heightAnimRect));
					if(ConstantsGlobalsFuelScreen.DEMO_VALUES_LINEAR_GAUGES[j][ConstantsGlobalsFuelScreen.counterDemoDataLinearGauges[j]]>=0)
						styleString = "opacity:1.0;fill:#000000";
					else
						styleString = "opacity:1.0;fill:#FF0000";
					attr.getNamedItem("style").setTextContent(styleString);
				}
			}

			try {
				DOMSource domSource = new DOMSource(docLinearGauge[j]);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, result);
				stringLinearGauge = writer.toString();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}

			try {
				SVG svg = SVG.getFromString(stringLinearGauge);
				svgImageView = new SVGImageView(Globals.context);
				svgImageView.setSVG(svg);
				LayoutParams paramsSVG=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
				svgImageView.setLayoutParams(paramsSVG);
				switch(j){
					case 0:
						simulatorLinearGaugesDemo_2(linearLayoutSecondRowFirstGaugeGauge,svgImageView);
						break;
					case 1:
						simulatorLinearGaugesDemo_2(linearLayoutSecondRowSecondGaugeGauge,svgImageView);
						break;
				}
			} catch (SVGParseException e){
				e.printStackTrace();
			}
			ConstantsGlobalsFuelScreen.counterDemoDataLinearGauges[j]++;
			if(ConstantsGlobalsFuelScreen.counterDemoDataLinearGauges[j]== ConstantsGlobalsFuelScreen.NUMBER_DEMO_DATA)
				ConstantsGlobalsFuelScreen.counterDemoDataLinearGauges[j] = 0;
		}
	}

	public void simulatorLinearGaugesDemo_2 (LinearLayout linearLayoutThirdRowGaugeGauge,SVGImageView svgImageView) {		// Only for Linear Layouts
		ViewGroup.LayoutParams paramsLinearLayoutThirdRowGaugeGauge = linearLayoutThirdRowGaugeGauge.getLayoutParams();
		LinearLayout linearGauge = new LinearLayout(Globals.context);
		LinearLayout.LayoutParams paramsLinearLayoutGauge = new LinearLayout.LayoutParams(paramsLinearLayoutThirdRowGaugeGauge.width,
				paramsLinearLayoutThirdRowGaugeGauge.height-20);
		paramsLinearLayoutGauge.topMargin = 10;
		linearGauge.setLayoutParams(paramsLinearLayoutGauge);
		linearGauge.addView(svgImageView);
		linearLayoutThirdRowGaugeGauge.removeAllViews();
		linearLayoutThirdRowGaugeGauge.addView(linearGauge);
	}
}
