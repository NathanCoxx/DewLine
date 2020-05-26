package com.john.dewline;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import junit.framework.Assert;

public class FragmentMain extends Fragment {

	View rootView;

	public static File root,dewLineFolder;
	public static SQLiteDatabase dewLineDB;
	public static Cursor cursor = null;
	public static Timer tDemoRPM,tDemo;

	// ********** CIRCULAR GAUGES
	// CHANGE
	public static final int NUMBER_CIRCULAR_GAUGES=11;
	// CHANGE
	public static final int SEGMENTS_GAUGE[]={7,5,7,6,5,
											  5,7,7,7,7,
											  7};

	// CHANGE
	public static String SCALES_CIRCULAR_GAUGES_SAE[][]= {
			{"0","10","20","30","40","50","60","70"},							// Turbo Boost -SAE -PSI (Dynamic))
			{"0","15","30","45","60","75","-","-"},								// Fuel Pressure -SAE -PSI
			{"0","2","4","6","8","10","12","14"},								// Raw Water Pressure -SAE -PSI (Dynamic)
			{"0","50","100","150","200","250","300","-"},						// Transmission Oil Pressure -SAE -PSI
			{"0","15","30","45","60","75","-","-"},								// Engine Oil Pressure -SAE -PSI (Dynamic)
			{"200","400","600","800","1000","1200","-","-"},					// Exhaust Gas Temp -SAE -°F (Dynamic)
			{"120","140","160","180","200","220","240","260"},					// Turbo 1/2 Oil Temp -SAE -°F
			{"120","140","160","180","200","220","240","260"},					// Engine Oil Temp -SAE -°F
			{"100","110","120","130","140","150","160","170"},					// Transmission Temperature -SAE -°F
			{"100","120","140","160","180","200","220","240"},					// Engine Coolant Temp -SAE -°F
			{"0","5","10","15","20","25","30","35"}								// RPM
	};
	public static String parameter[]={"Turbo Boost",
			  "Fuel Pressure",
			  "Raw Water Pressure",
			  "Transmission Oil Pressure",
			  "Engine Oil Pressure",
			  "Exhaust Gas Temp 1",
			  "Turbo 1 Oil Temp",
			  "Engine Oil Temp",
			  "Transmission Oil Temp",
			  "Engine Coolant Temp",
			  "RPM"
	};
	public static String NAME_ICONS_CIRCULAR_GAUGES[]={"turbo_boost_pressure_background",
											  "fuel_pressure_background",
											  "raw_water_pressure_background",
											  "gear_oil_pressure_background",
											  "oil_pressure_background",
											  "egt_background",
											  "turbo_temperature_background",
											  "engine_oil_temp_background",
											  "gear_temp_background",
											  "engine_coolant_temp_background",
											  "engine_rpm_port_background"
	};
	public static String UNITS_CIRCULAR_GAUGES_SAE[]={"PSI",
										 "PSI",
										 "PSI",
										 "PSI",
										 "PSI",
										 "°F",
										 "°F",
										 "°F",
										 "°F",
										 "°F",
										 "RPM"
	};
	public static int MAXIMUM_ANGLE_GAUGES[]={135,125,120};						// 5, 6 and 7 segments
	public static final int DASHES_CIRCULAR_GAUGES[]={26,31,36};
	public static double SHIFT_X_SCALE_GAUGE_PER_NUMBER=3;
	public static final int ADJUST_POSITION_NEEDLE_X=2,ADJUST_POSITION_ICON_X=8,ADJUST_POSITION_VALUE_GAUGE_X=8;

	LinearLayout linearLayoutFirstRowFirstGauge,linearLayoutFirstRowSecondGauge,linearLayoutFirstRowThirdGauge,
						linearLayoutFirstRowFourthGauge,linearLayoutFirstRowFifthGauge,
				 linearLayoutSecondRowFirstGauge,linearLayoutSecondRowSecondGauge,linearLayoutSecondRowThirdGauge,
						linearLayoutSecondRowFourthGauge,linearLayoutSecondRowFifthGauge,
				 linearLayoutFirstSecondRowBigGauge;
	ViewGroup.LayoutParams paramsLinearLayoutFirstRowFirstGauge,paramsLinearLayoutFirstRowSecondGauge,paramsLinearLayoutFirstRowThirdGauge,
									paramsLinearLayoutFirstRowFourthGauge,paramsLinearLayoutFirstRowFifthGauge,
						   paramsLinearLayoutSecondRowFirstGauge,paramsLinearLayoutSecondRowSecondGauge,paramsLinearLayoutSecondRowThirdGauge,
						   			paramsLinearLayoutSecondRowFourthGauge,paramsLinearLayoutSecondRowFifthGauge,
						   paramsLinearLayoutFirstSecondRowBigGauge;
	SVGImageView []svgImageView = new SVGImageView[NUMBER_CIRCULAR_GAUGES];

	public static RelativeLayout gaugeFirstRowFirstGauge,gaugeFirstRowSecondGauge,gaugeFirstRowThirdGauge,gaugeFirstRowFourthGauge,
								 gaugeFirstRowFifthGauge,
								 gaugeSecondRowFirstGauge,gaugeSecondRowSecondGauge,gaugeSecondRowThirdGauge,gaugeSecondRowFourthGauge,
								 gaugeSecondRowFifthGauge,
								 gaugeFirstSecondRowsBigGauge;
	public static boolean FLAG_WITH_BACKGROUNDS= true;
	public static int MARGIN_RELATIVE_LAYOUT_GAUGE=10,marginNeedle=8;
	//Before backgrounds public static int MARGIN_RELATIVE_LAYOUT_GAUGE=5,marginNeedle=8;
	public static LinearLayout gaugeNeedle = new LinearLayout(Globals.context);
	LinearLayout[] linearLayoutNeedle = new LinearLayout[NUMBER_CIRCULAR_GAUGES];
	TextView[] textViewValueGauge = new TextView[NUMBER_CIRCULAR_GAUGES];
	TextView textViewThirdRowLEDsItem5Memo;
	public static DecimalFormat zeroDecForm = new DecimalFormat("#"),oneDecForm = new DecimalFormat("#.#");

	public static float valueMinimum=0.0f,valueMaximum,increment=2;
	public static int positionDegrees;
	public static String unitsSystem="SAE";
	public static Document docGauge=null;

	public static float actualValueCircularGauge[]={0.0f,0.0f,0.0f,0.0f,0.0f,
			200.0f,120.0f,120.0f,100.0f,100.0f,
			0.0f};
	public static float valueParameterCircularGaugePrevious[]={0.0f,0.0f,0.0f,0.0f,0.0f,
			0.0f,0.0f,0.0f,0.0f,0.0f,
			0.0f};
	public static int actualFaceDisplayed[]={0,0,0,0,0,
			0,0,0,0,0,
			0};
	public static float valueThresholdsDynamicPrevious[][]={{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f},
						{0.0f,0.0f,0.0f,0.0f,0.0f},
						{0.0f,0.0f,0.0f,0.0f,0.0f},
						{0.0f,0.0f,0.0f,0.0f,0.0f}};
	public static int positionThresholdsDynamicPrevious[]={0,-1,1,-1,2,3,-1,-1,-1,-1,-1};

	// Variable to calculate dynamic threshold
	//float RPM_Idle,RPM_WOT,currentEngineRPM,parameterThreshold_Idle,parameterThreshold_WOT;
	public static float RPM_Idle=0.0f,RPM_WOT=0.0f;
	public static float valueRPMCurrent = ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES_RPM[0];

	// ********** LINEAR GAUGES
	LinearLayout linearLayoutThirdRowFirstGaugeGauge,linearLayoutThirdRowSecondGaugeGauge,linearLayoutThirdRowThirdGaugeGauge,
				 linearLayoutThirdRowFourthGaugeGauge,linearLayoutThirdRowFifthGaugeGauge,linearLayoutThirdRowSixthGaugeGauge;
	public static final int NUMBER_LINEAR_GAUGES=6;
	public static final int SEGMENTS_LINEAR_GAUGE[]={4,4,4,4,4,4};
	public static String SCALES_LINEAR_GAUGES_SAE[][]= {
			{"Empty","1/4","1/2","3/4","Full","-","-","-","-"},					// Waste 1/2
			{"Empty","1/4","1/2","3/4","Full","-","-","-","-"},					// Fuel 1/2
			{"0","4","8","12","16","-","-","-","-"},							// House Volts
			{"0","4","8","12","16","-","-","-","-"},							// Engine Volts
			{"-40","-20","0","20","40","-","-","-","-"},						// Amps
			{"-10","0","10","20","30","-","-","-","-"}							// In H20
	};
	Document docLinearGauge[]=new Document[NUMBER_LINEAR_GAUGES];

	public static LinearLayout linearGaugeThirdRowFirstGauge,linearGaugeThirdRowSecondGauge,linearGaugeThirdRowThirdGauge,
				linearGaugeThirdRowFourthGauge,linearGaugeThirdRowFifthGauge,linearGaugeThirdRowSixthGauge;

	// ********** FUEL MANAGEMENT PANEL
	public static final int NUMBER_VARIABLES_PANEL_FUEL_MANAGEMENT=7;
	public static TextView textViewSecondColumnThirdRowItem1Value,textViewSecondColumnThirdRowItem2Value,
				textViewSecondColumnThirdRowItem3Value,textViewSecondColumnThirdRowItem4Value,
				textViewSecondColumnThirdRowItem5Value,textViewSecondColumnThirdRowItem6Value,
				textViewSecondColumnThirdRowItem7Value;
	//public static Button buttonSetup;

	// Thresholds variables
	float lowThresholdShutdown_Idle=-1,lowThresholdRed_Idle=-1,lowThresholdYellow_Idle=-1;
	float highThresholdYellow_Idle=-1,highThresholdRed_Idle=-1,highThresholdShutdown_Idle=-1;
	float lowThresholdShutdown_WOT=-1,lowThresholdRed_WOT=-1,lowThresholdYellow_WOT=-1;
	float highThresholdYellow_WOT=-1,highThresholdRed_WOT=-1,highThresholdShutdown_WOT=-1;

	ScrollView scrollViewThirdRowLEDsItem5Inside;
/*
	public static Timer tDemo = new Timer();
	TimerTask timerDemoCircularGauge = new TimerTask(){
		@Override
		public void run() {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					simulatorCircularGaugesDemo();
					simulatorLinearGaugesDemo();
					simulatorFuelmanagementPanelDemo();
				}
			});
		}

	};

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
*/

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_main, container,false);

		root = Environment.getExternalStorageDirectory();
		dewLineFolder = new File(root.getAbsolutePath()+"/DEWLine/");

		scrollViewThirdRowLEDsItem5Inside = (ScrollView) rootView.findViewById(R.id.scrollViewThirdRowLEDsItem5Inside);

		dewLineDB = Globals.context.openOrCreateDatabase(dewLineFolder+"/DEWLine.db",Globals.context.MODE_PRIVATE, null);
		createGUI();
		//dewLineDB.close();													// See timers

		//tDemo = new Timer();
		//tDemo.scheduleAtFixedRate(timerDemoCircularGauge, 0 , 1000);
		//tDemoRPM = new Timer();
		//tDemoRPM.scheduleAtFixedRate(timerDemoRPM, 0 , 500);

		//setInitialValues();

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

	@Override
	public void onPause(){
		super.onPause();

		tDemoRPM.cancel();
		tDemoRPM = null;
		tDemo.cancel();
		tDemo = null;
		//dewLineDB.close();													// The DB is closed, but activity don't stop so ...error
	}

	@Override
	public void onResume(){
		super.onResume();

/*
		//dewLineDB = Globals.context.openOrCreateDatabase(dewLineFolder+"/DEWLine.db",Globals.context.MODE_PRIVATE, null);
		if(tDemo==null) {
			tDemo = new Timer();
			tDemo.scheduleAtFixedRate(timerDemoCircularGauge, 0 , 1000);
		}
		if(tDemoRPM==null) {
		}
*/
		tDemoRPM = new Timer();
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
		tDemoRPM.scheduleAtFixedRate(timerDemoRPM, 0 , 500);

		tDemo = new Timer();
		TimerTask timerDemoCircularGauge = new TimerTask(){
			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						simulatorCircularGaugesDemo();
						simulatorLinearGaugesDemo();
						simulatorFuelmanagementPanelDemo();
					}
				});
			}

		};
		tDemo.scheduleAtFixedRate(timerDemoCircularGauge, 0 , 1000);
/*
*/
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

					linearLayoutFirstRowFirstGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowFirstGauge);
					paramsLinearLayoutFirstRowFirstGauge = linearLayoutFirstRowFirstGauge.getLayoutParams();
					paramsLinearLayoutFirstRowFirstGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowFirstGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowFirstGauge.setX(0);
					linearLayoutFirstRowFirstGauge.setY(0);
					linearLayoutFirstRowFirstGauge.setLayoutParams(paramsLinearLayoutFirstRowFirstGauge);
					gaugeFirstRowFirstGauge = createCircularGauge(0,linearLayoutFirstRowFirstGauge,paramsLinearLayoutFirstRowFirstGauge,"Turbo Boost");
					linearLayoutFirstRowFirstGauge.addView(gaugeFirstRowFirstGauge);

					linearLayoutFirstRowSecondGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowSecondGauge);
					paramsLinearLayoutFirstRowSecondGauge = linearLayoutFirstRowSecondGauge.getLayoutParams();
					paramsLinearLayoutFirstRowSecondGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowSecondGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowSecondGauge.setX(paramsLinearLayoutFirstRowSecondGauge.width*23/32);
					linearLayoutFirstRowSecondGauge.setY(0);
					linearLayoutFirstRowSecondGauge.setLayoutParams(paramsLinearLayoutFirstRowSecondGauge);
					gaugeFirstRowSecondGauge = createCircularGauge(1,linearLayoutFirstRowSecondGauge,paramsLinearLayoutFirstRowSecondGauge,"Fuel Pressure");
					linearLayoutFirstRowSecondGauge.addView(gaugeFirstRowSecondGauge);

					linearLayoutFirstRowThirdGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowThirdGauge);
					paramsLinearLayoutFirstRowThirdGauge = linearLayoutFirstRowThirdGauge.getLayoutParams();
					paramsLinearLayoutFirstRowThirdGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowThirdGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowThirdGauge.setX(paramsLinearLayoutFirstRowThirdGauge.width*46/32);
					linearLayoutFirstRowThirdGauge.setY(0);
					linearLayoutFirstRowThirdGauge.setLayoutParams(paramsLinearLayoutFirstRowThirdGauge);
					gaugeFirstRowThirdGauge = createCircularGauge(2,linearLayoutFirstRowThirdGauge,paramsLinearLayoutFirstRowThirdGauge,"Raw Water Pressure");
					linearLayoutFirstRowThirdGauge.addView(gaugeFirstRowThirdGauge);

					linearLayoutFirstRowFourthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowFourthGauge);
					paramsLinearLayoutFirstRowFourthGauge = linearLayoutFirstRowFourthGauge.getLayoutParams();
					paramsLinearLayoutFirstRowFourthGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowFourthGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowFourthGauge.setX(paramsLinearLayoutFirstRowFourthGauge.width*69/32);
					linearLayoutFirstRowFourthGauge.setY(0);
					linearLayoutFirstRowFourthGauge.setLayoutParams(paramsLinearLayoutFirstRowFourthGauge);
					gaugeFirstRowFourthGauge = createCircularGauge(3,linearLayoutFirstRowFourthGauge,paramsLinearLayoutFirstRowFourthGauge,"Transmission Oil Pressure");
					linearLayoutFirstRowFourthGauge.addView(gaugeFirstRowFourthGauge);

					linearLayoutFirstRowFifthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstRowFifthGauge);
					paramsLinearLayoutFirstRowFifthGauge = linearLayoutFirstRowFifthGauge.getLayoutParams();
					paramsLinearLayoutFirstRowFifthGauge.width = paramsLinearLayoutFirstRow.height;
					paramsLinearLayoutFirstRowFifthGauge.height = paramsLinearLayoutFirstRow.height;
					linearLayoutFirstRowFifthGauge.setX(paramsLinearLayoutFirstRowFifthGauge.width*92/32);
					linearLayoutFirstRowFifthGauge.setY(0);
					linearLayoutFirstRowFifthGauge.setLayoutParams(paramsLinearLayoutFirstRowFifthGauge);
					gaugeFirstRowFifthGauge = createCircularGauge(4,linearLayoutFirstRowFifthGauge,paramsLinearLayoutFirstRowFifthGauge,"Engine Oil Pressure");
					linearLayoutFirstRowFifthGauge.addView(gaugeFirstRowFifthGauge);
//-END FIRST ROW

//-SECOND ROW
				// RelativeLayout "RelativeLayoutSecondRow"
				RelativeLayout relativeLayoutSecondRow = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSecondRow);
				ViewGroup.LayoutParams paramsLinearLayoutSecondRow = relativeLayoutSecondRow.getLayoutParams();
				paramsLinearLayoutSecondRow.width = paramsLinearLayoutLefColumn.width;
				paramsLinearLayoutSecondRow.height = (int)(Globals.heightScreen *  0.28);
				relativeLayoutSecondRow.setLayoutParams(paramsLinearLayoutSecondRow);

					linearLayoutSecondRowFirstGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowFirstGauge);
					paramsLinearLayoutSecondRowFirstGauge = linearLayoutSecondRowFirstGauge.getLayoutParams();
					paramsLinearLayoutSecondRowFirstGauge.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowFirstGauge.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowFirstGauge.setX(0);
					linearLayoutSecondRowFirstGauge.setY(0);
					linearLayoutSecondRowFirstGauge.setLayoutParams(paramsLinearLayoutSecondRowFirstGauge);
					gaugeSecondRowFirstGauge = createCircularGauge(5,linearLayoutSecondRowFirstGauge,paramsLinearLayoutSecondRowFirstGauge,"Exhaust Gas Temp 1");
					linearLayoutSecondRowFirstGauge.addView(gaugeSecondRowFirstGauge);

					linearLayoutSecondRowSecondGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowSecondGauge);
					paramsLinearLayoutSecondRowSecondGauge = linearLayoutSecondRowSecondGauge.getLayoutParams();
					paramsLinearLayoutSecondRowSecondGauge.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowSecondGauge.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowSecondGauge.setX(paramsLinearLayoutSecondRowSecondGauge.width*23/32);
					linearLayoutSecondRowSecondGauge.setY(0);
					linearLayoutSecondRowSecondGauge.setLayoutParams(paramsLinearLayoutSecondRowSecondGauge);
					gaugeSecondRowSecondGauge = createCircularGauge(6,linearLayoutSecondRowSecondGauge,paramsLinearLayoutSecondRowSecondGauge,"Turbo 1 Oil Temp");
					linearLayoutSecondRowSecondGauge.addView(gaugeSecondRowSecondGauge);

					linearLayoutSecondRowThirdGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowThirdGauge);
					paramsLinearLayoutSecondRowThirdGauge = linearLayoutSecondRowThirdGauge.getLayoutParams();
					paramsLinearLayoutSecondRowThirdGauge.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowThirdGauge.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowThirdGauge.setX(paramsLinearLayoutSecondRowThirdGauge.width*46/32);
					linearLayoutSecondRowThirdGauge.setY(0);
					linearLayoutSecondRowThirdGauge.setLayoutParams(paramsLinearLayoutSecondRowThirdGauge);
					gaugeSecondRowThirdGauge = createCircularGauge(7,linearLayoutSecondRowThirdGauge,paramsLinearLayoutSecondRowThirdGauge,"Engine Oil Temp");
					linearLayoutSecondRowThirdGauge.addView(gaugeSecondRowThirdGauge);

					linearLayoutSecondRowFourthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowFourthGauge);
					paramsLinearLayoutSecondRowFourthGauge = linearLayoutSecondRowFourthGauge.getLayoutParams();
					paramsLinearLayoutSecondRowFourthGauge.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowFourthGauge.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowFourthGauge.setX(paramsLinearLayoutSecondRowFourthGauge.width*69/32);
					linearLayoutSecondRowFourthGauge.setY(0);
					linearLayoutSecondRowFourthGauge.setLayoutParams(paramsLinearLayoutSecondRowFourthGauge);
					gaugeSecondRowFourthGauge = createCircularGauge(8,linearLayoutSecondRowFourthGauge,paramsLinearLayoutSecondRowFourthGauge,"Transmission Oil Temp");
					linearLayoutSecondRowFourthGauge.addView(gaugeSecondRowFourthGauge);

					linearLayoutSecondRowFifthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondRowFifthGauge);
					paramsLinearLayoutSecondRowFifthGauge = linearLayoutSecondRowFifthGauge.getLayoutParams();
					paramsLinearLayoutSecondRowFifthGauge.width = paramsLinearLayoutSecondRow.height;
					paramsLinearLayoutSecondRowFifthGauge.height = paramsLinearLayoutSecondRow.height;
					linearLayoutSecondRowFifthGauge.setX(paramsLinearLayoutSecondRowFifthGauge.width*92/32);
					linearLayoutSecondRowFifthGauge.setY(0);
					linearLayoutSecondRowFifthGauge.setLayoutParams(paramsLinearLayoutSecondRowFifthGauge);
					gaugeSecondRowFifthGauge = createCircularGauge(9,linearLayoutSecondRowFifthGauge,paramsLinearLayoutSecondRowFifthGauge,"Engine Coolant Temp");
					linearLayoutSecondRowFifthGauge.addView(gaugeSecondRowFifthGauge);
//-END SECOND ROW

//-THIRD ROW
				// Linear layout "LinearLayoutThirdRow"
				LinearLayout linearLayoutThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRow);
				ViewGroup.LayoutParams paramsLinearLayoutThirdRow = linearLayoutThirdRow.getLayoutParams();
				paramsLinearLayoutThirdRow.width = paramsLinearLayoutLefColumn.width;
				paramsLinearLayoutThirdRow.height = (int)(Globals.heightScreen *  0.28 - 18);
				linearLayoutThirdRow.setLayoutParams(paramsLinearLayoutThirdRow);

					LinearLayout linearLayoutThirdRowFirstG = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFirstG);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowFirstG = linearLayoutThirdRowFirstG.getLayoutParams();
					paramsLinearLayoutThirdRowFirstG.width = paramsLinearLayoutThirdRow.width / 12;
					paramsLinearLayoutThirdRowFirstG.height = paramsLinearLayoutThirdRow.height;
					linearLayoutThirdRowFirstG.setLayoutParams(paramsLinearLayoutThirdRowFirstG);
						LinearLayout linearLayoutThirdRowFirstGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFirstGauge);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowFirstGauge = linearLayoutThirdRowFirstGauge.getLayoutParams();
						paramsLinearLayoutThirdRowFirstGauge.width = (int)(paramsLinearLayoutThirdRowFirstG.width * 0.8);
						paramsLinearLayoutThirdRowFirstGauge.height = paramsLinearLayoutThirdRow.height;
						linearLayoutThirdRowFirstGauge.setLayoutParams(paramsLinearLayoutThirdRowFirstGauge);
							LinearLayout linearLayoutThirdRowFirstGaugeDescription = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFirstGaugeDescription);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowFirstGaugeDescription = linearLayoutThirdRowFirstGaugeDescription.getLayoutParams();
							paramsLinearLayoutThirdRowFirstGaugeDescription.width = paramsLinearLayoutThirdRowFirstGauge.width;
							paramsLinearLayoutThirdRowFirstGaugeDescription.height = (int)(paramsLinearLayoutThirdRowFirstGauge.height*0.2);
							linearLayoutThirdRowFirstGaugeDescription.setLayoutParams(paramsLinearLayoutThirdRowFirstGaugeDescription);
								TextView textViewThirdRowFirstGaugeDescription = (TextView) rootView.findViewById(R.id.textViewThirdRowFirstGaugeDescription);
								//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 
							linearLayoutThirdRowFirstGaugeGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFirstGaugeGauge);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowFirstGaugeGauge = linearLayoutThirdRowFirstGaugeGauge.getLayoutParams();
							paramsLinearLayoutThirdRowFirstGaugeGauge.width = paramsLinearLayoutThirdRowFirstGauge.width;
							paramsLinearLayoutThirdRowFirstGaugeGauge.height = (int)(paramsLinearLayoutThirdRowFirstGauge.height*0.8);
							linearLayoutThirdRowFirstGaugeGauge.setLayoutParams(paramsLinearLayoutThirdRowFirstGaugeGauge);
								linearGaugeThirdRowFirstGauge = createLinearGauge(0,linearLayoutThirdRowFirstGaugeGauge,paramsLinearLayoutThirdRowFirstGaugeGauge);
								linearLayoutThirdRowFirstGaugeGauge.addView(linearGaugeThirdRowFirstGauge);

					LinearLayout linearLayoutThirdRowSecondG = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSecondG);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowSecondG = linearLayoutThirdRowSecondG.getLayoutParams();
					paramsLinearLayoutThirdRowSecondG.width = paramsLinearLayoutThirdRow.width / 12;
					paramsLinearLayoutThirdRowSecondG.height = paramsLinearLayoutThirdRow.height;
					linearLayoutThirdRowSecondG.setLayoutParams(paramsLinearLayoutThirdRowSecondG);
						LinearLayout linearLayoutThirdRowSecondGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSecondGauge);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowSecondGauge = linearLayoutThirdRowSecondGauge.getLayoutParams();
						paramsLinearLayoutThirdRowSecondGauge.width = (int)(paramsLinearLayoutThirdRowSecondG.width * 0.8);
						paramsLinearLayoutThirdRowSecondGauge.height = paramsLinearLayoutThirdRow.height;
						linearLayoutThirdRowSecondGauge.setLayoutParams(paramsLinearLayoutThirdRowSecondGauge);
							LinearLayout linearLayoutThirdRowSecondGaugeDescription = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSecondGaugeDescription);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowSecondGaugeDescription = linearLayoutThirdRowSecondGaugeDescription.getLayoutParams();
							paramsLinearLayoutThirdRowSecondGaugeDescription.width = paramsLinearLayoutThirdRowSecondGauge.width;
							paramsLinearLayoutThirdRowSecondGaugeDescription.height = (int)(paramsLinearLayoutThirdRowSecondGauge.height*0.2);
							linearLayoutThirdRowSecondGaugeDescription.setLayoutParams(paramsLinearLayoutThirdRowSecondGaugeDescription);
								TextView textViewThirdRowSecondGaugeDescription = (TextView) rootView.findViewById(R.id.textViewThirdRowSecondGaugeDescription);
								//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 
							linearLayoutThirdRowSecondGaugeGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSecondGaugeGauge);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowSecondGaugeGauge = linearLayoutThirdRowSecondGaugeGauge.getLayoutParams();
							paramsLinearLayoutThirdRowSecondGaugeGauge.width = paramsLinearLayoutThirdRowSecondGauge.width;
							paramsLinearLayoutThirdRowSecondGaugeGauge.height = (int)(paramsLinearLayoutThirdRowSecondGauge.height*0.8);
							linearLayoutThirdRowSecondGaugeGauge.setLayoutParams(paramsLinearLayoutThirdRowSecondGaugeGauge);
								linearGaugeThirdRowSecondGauge = createLinearGauge(1,linearLayoutThirdRowSecondGaugeGauge,paramsLinearLayoutThirdRowSecondGaugeGauge);
								linearLayoutThirdRowSecondGaugeGauge.addView(linearGaugeThirdRowSecondGauge);

					LinearLayout linearLayoutThirdRowThirdG = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowThirdG);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowThirdG = linearLayoutThirdRowThirdG.getLayoutParams();
					paramsLinearLayoutThirdRowThirdG.width = paramsLinearLayoutThirdRow.width / 12;
					paramsLinearLayoutThirdRowThirdG.height = paramsLinearLayoutThirdRow.height;
					linearLayoutThirdRowThirdG.setLayoutParams(paramsLinearLayoutThirdRowThirdG);
						LinearLayout linearLayoutThirdRowThirdGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowThirdGauge);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowThirdGauge = linearLayoutThirdRowThirdGauge.getLayoutParams();
						paramsLinearLayoutThirdRowThirdGauge.width = (int)(paramsLinearLayoutThirdRowThirdG.width * 0.8);
						paramsLinearLayoutThirdRowThirdGauge.height = paramsLinearLayoutThirdRow.height;
						linearLayoutThirdRowThirdGauge.setLayoutParams(paramsLinearLayoutThirdRowThirdGauge);
							LinearLayout linearLayoutThirdRowThirdGaugeDescription = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowThirdGaugeDescription);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowThirdGaugeDescription = linearLayoutThirdRowThirdGaugeDescription.getLayoutParams();
							paramsLinearLayoutThirdRowThirdGaugeDescription.width = paramsLinearLayoutThirdRowThirdGauge.width;
							paramsLinearLayoutThirdRowThirdGaugeDescription.height = (int)(paramsLinearLayoutThirdRowThirdGauge.height*0.2);
							linearLayoutThirdRowThirdGaugeDescription.setLayoutParams(paramsLinearLayoutThirdRowThirdGaugeDescription);
								TextView textViewThirdRowThirdGaugeDescription = (TextView) rootView.findViewById(R.id.textViewThirdRowThirdGaugeDescription);
								//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 
							linearLayoutThirdRowThirdGaugeGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowThirdGaugeGauge);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowThirdGaugeGauge = linearLayoutThirdRowThirdGaugeGauge.getLayoutParams();
							paramsLinearLayoutThirdRowThirdGaugeGauge.width = paramsLinearLayoutThirdRowThirdGauge.width;
							paramsLinearLayoutThirdRowThirdGaugeGauge.height = (int)(paramsLinearLayoutThirdRowThirdGauge.height*0.8);
							linearLayoutThirdRowThirdGaugeGauge.setLayoutParams(paramsLinearLayoutThirdRowThirdGaugeGauge);
								linearGaugeThirdRowThirdGauge = createLinearGauge(2,linearLayoutThirdRowThirdGaugeGauge,paramsLinearLayoutThirdRowThirdGaugeGauge);
								linearLayoutThirdRowThirdGaugeGauge.addView(linearGaugeThirdRowThirdGauge);

					LinearLayout linearLayoutThirdRowFourthG = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFourthG);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowFourthG = linearLayoutThirdRowFourthG.getLayoutParams();
					paramsLinearLayoutThirdRowFourthG.width = paramsLinearLayoutThirdRow.width / 12;
					paramsLinearLayoutThirdRowFourthG.height = paramsLinearLayoutThirdRow.height;
					linearLayoutThirdRowFourthG.setLayoutParams(paramsLinearLayoutThirdRowFourthG);
						LinearLayout linearLayoutThirdRowFourthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFourthGauge);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowFourthGauge = linearLayoutThirdRowFourthGauge.getLayoutParams();
						paramsLinearLayoutThirdRowFourthGauge.width = (int)(paramsLinearLayoutThirdRowFourthG.width * 0.8);
						paramsLinearLayoutThirdRowFourthGauge.height = paramsLinearLayoutThirdRow.height;
						linearLayoutThirdRowFourthGauge.setLayoutParams(paramsLinearLayoutThirdRowFourthGauge);
							LinearLayout linearLayoutThirdRowFourthGaugeDescription = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFourthGaugeDescription);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowFourthGaugeDescription = linearLayoutThirdRowFourthGaugeDescription.getLayoutParams();
							paramsLinearLayoutThirdRowFourthGaugeDescription.width = paramsLinearLayoutThirdRowFourthGauge.width;
							paramsLinearLayoutThirdRowFourthGaugeDescription.height = (int)(paramsLinearLayoutThirdRowFourthGauge.height*0.2);
							linearLayoutThirdRowFourthGaugeDescription.setLayoutParams(paramsLinearLayoutThirdRowFourthGaugeDescription);
								TextView textViewThirdRowFourthGaugeDescription = (TextView) rootView.findViewById(R.id.textViewThirdRowFourthGaugeDescription);
								//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 
							linearLayoutThirdRowFourthGaugeGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFourthGaugeGauge);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowFourthGaugeGauge = linearLayoutThirdRowFourthGaugeGauge.getLayoutParams();
							paramsLinearLayoutThirdRowFourthGaugeGauge.width = paramsLinearLayoutThirdRowFourthGauge.width;
							paramsLinearLayoutThirdRowFourthGaugeGauge.height = (int)(paramsLinearLayoutThirdRowFourthGauge.height*0.8);
							linearLayoutThirdRowFourthGaugeGauge.setLayoutParams(paramsLinearLayoutThirdRowFourthGaugeGauge);
								linearGaugeThirdRowFourthGauge = createLinearGauge(3,linearLayoutThirdRowFourthGaugeGauge,paramsLinearLayoutThirdRowFourthGaugeGauge);
								linearLayoutThirdRowFourthGaugeGauge.addView(linearGaugeThirdRowFourthGauge);

					LinearLayout linearLayoutThirdRowFifthG = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFifthG);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowFifthG = linearLayoutThirdRowFifthG.getLayoutParams();
					paramsLinearLayoutThirdRowFifthG.width = paramsLinearLayoutThirdRow.width / 12;
					paramsLinearLayoutThirdRowFifthG.height = paramsLinearLayoutThirdRow.height;
					linearLayoutThirdRowFifthG.setLayoutParams(paramsLinearLayoutThirdRowFifthG);
						LinearLayout linearLayoutThirdRowFifthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFifthGauge);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowFifthGauge = linearLayoutThirdRowFifthGauge.getLayoutParams();
						paramsLinearLayoutThirdRowFifthGauge.width = (int)(paramsLinearLayoutThirdRowFifthG.width * 0.8);
						paramsLinearLayoutThirdRowFifthGauge.height = paramsLinearLayoutThirdRow.height;
						linearLayoutThirdRowFifthGauge.setLayoutParams(paramsLinearLayoutThirdRowFifthGauge);
							LinearLayout linearLayoutThirdRowFifthGaugeDescription = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFifthGaugeDescription);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowFifthGaugeDescription = linearLayoutThirdRowFifthGaugeDescription.getLayoutParams();
							paramsLinearLayoutThirdRowFifthGaugeDescription.width = paramsLinearLayoutThirdRowFifthGauge.width;
							paramsLinearLayoutThirdRowFifthGaugeDescription.height = (int)(paramsLinearLayoutThirdRowFifthGauge.height*0.2);
							linearLayoutThirdRowFifthGaugeDescription.setLayoutParams(paramsLinearLayoutThirdRowFifthGaugeDescription);
								TextView textViewThirdRowFifthGaugeDescription = (TextView) rootView.findViewById(R.id.textViewThirdRowFifthGaugeDescription);
								//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 
							linearLayoutThirdRowFifthGaugeGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowFifthGaugeGauge);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowFifthGaugeGauge = linearLayoutThirdRowFifthGaugeGauge.getLayoutParams();
							paramsLinearLayoutThirdRowFifthGaugeGauge.width = paramsLinearLayoutThirdRowFifthGauge.width;
							paramsLinearLayoutThirdRowFifthGaugeGauge.height = (int)(paramsLinearLayoutThirdRowFifthGauge.height*0.8);
							linearLayoutThirdRowFifthGaugeGauge.setLayoutParams(paramsLinearLayoutThirdRowFifthGaugeGauge);
								linearGaugeThirdRowFifthGauge = createLinearGauge(4,linearLayoutThirdRowFifthGaugeGauge,paramsLinearLayoutThirdRowFifthGaugeGauge);
								linearLayoutThirdRowFifthGaugeGauge.addView(linearGaugeThirdRowFifthGauge);

					LinearLayout linearLayoutThirdRowSixthG = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSixthG);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowSixthG = linearLayoutThirdRowSixthG.getLayoutParams();
					paramsLinearLayoutThirdRowSixthG.width = paramsLinearLayoutThirdRow.width / 12;
					paramsLinearLayoutThirdRowSixthG.height = paramsLinearLayoutThirdRow.height;
					linearLayoutThirdRowSixthG.setLayoutParams(paramsLinearLayoutThirdRowSixthG);
						LinearLayout linearLayoutThirdRowSixthGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSixthGauge);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowSixthGauge = linearLayoutThirdRowSixthGauge.getLayoutParams();
						paramsLinearLayoutThirdRowSixthGauge.width = (int)(paramsLinearLayoutThirdRowSixthG.width * 0.8);
						paramsLinearLayoutThirdRowSixthGauge.height = paramsLinearLayoutThirdRow.height;
						linearLayoutThirdRowSixthGauge.setLayoutParams(paramsLinearLayoutThirdRowSixthGauge);
							LinearLayout linearLayoutThirdRowSixthGaugeIconDescription = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSixthGaugeIconDescription);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowSixthGaugeIconDescription = linearLayoutThirdRowSixthGaugeIconDescription.getLayoutParams();
							paramsLinearLayoutThirdRowSixthGaugeIconDescription.width = paramsLinearLayoutThirdRowSixthGauge.width;
							paramsLinearLayoutThirdRowSixthGaugeIconDescription.height = (int)(paramsLinearLayoutThirdRowSixthGauge.height*0.2);
							linearLayoutThirdRowSixthGaugeIconDescription.setLayoutParams(paramsLinearLayoutThirdRowSixthGaugeIconDescription);
								LinearLayout linearLayoutThirdRowSixthGaugeIcon = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSixthGaugeIcon);
								ViewGroup.LayoutParams paramsLinearLayoutThirdRowSixthGaugeIcon = linearLayoutThirdRowSixthGaugeIcon.getLayoutParams();
								paramsLinearLayoutThirdRowSixthGaugeIcon.width = paramsLinearLayoutThirdRowSixthGaugeIconDescription.width;
								paramsLinearLayoutThirdRowSixthGaugeIcon.height = (int)(paramsLinearLayoutThirdRowSixthGaugeIconDescription.height*0.65);
								linearLayoutThirdRowSixthGaugeIcon.setLayoutParams(paramsLinearLayoutThirdRowSixthGaugeIcon);

								LinearLayout linearLayoutThirdRowSixthGaugeDescription = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSixthGaugeDescription);
								ViewGroup.LayoutParams paramsLinearLayoutThirdRowSixthGaugeDescription = linearLayoutThirdRowSixthGaugeDescription.getLayoutParams();
								paramsLinearLayoutThirdRowSixthGaugeDescription.width = paramsLinearLayoutThirdRowSixthGaugeIconDescription.width;
								paramsLinearLayoutThirdRowSixthGaugeDescription.height = (int)(paramsLinearLayoutThirdRowSixthGaugeIconDescription.height*0.35);
								linearLayoutThirdRowSixthGaugeDescription.setLayoutParams(paramsLinearLayoutThirdRowSixthGaugeDescription);
									TextView textViewThirdRowSixthGaugeDescription = (TextView) rootView.findViewById(R.id.textViewThirdRowSixthGaugeDescription);
									textViewThirdRowSixthGaugeDescription.setText(Html.fromHtml("In H<small><small>2</small></small>O"));
									//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 

							linearLayoutThirdRowSixthGaugeGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowSixthGaugeGauge);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowSixthGaugeGauge = linearLayoutThirdRowSixthGaugeGauge.getLayoutParams();
							paramsLinearLayoutThirdRowSixthGaugeGauge.width = paramsLinearLayoutThirdRowSixthGauge.width;
							paramsLinearLayoutThirdRowSixthGaugeGauge.height = (int)(paramsLinearLayoutThirdRowSixthGauge.height*0.8);
							linearLayoutThirdRowSixthGaugeGauge.setLayoutParams(paramsLinearLayoutThirdRowSixthGaugeGauge);
								linearGaugeThirdRowSixthGauge = createLinearGauge(5,linearLayoutThirdRowSixthGaugeGauge,paramsLinearLayoutThirdRowSixthGaugeGauge);
								linearLayoutThirdRowSixthGaugeGauge.addView(linearGaugeThirdRowSixthGauge);

					LinearLayout linearLayoutThirdRowLEDs = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDs);
					ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDs = linearLayoutThirdRowLEDs.getLayoutParams();
					paramsLinearLayoutThirdRowLEDs.width = (int)(paramsLinearLayoutThirdRow.width*0.2);
					paramsLinearLayoutThirdRowLEDs.height = paramsLinearLayoutThirdRow.height;
					linearLayoutThirdRowLEDs.setLayoutParams(paramsLinearLayoutThirdRowLEDs);

						LinearLayout linearLayoutThirdRowLEDsItem1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem1);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem1 = linearLayoutThirdRowLEDsItem1.getLayoutParams();
						paramsLinearLayoutThirdRowLEDsItem1.width = paramsLinearLayoutThirdRowLEDs.width;
						paramsLinearLayoutThirdRowLEDsItem1.height = (int)(paramsLinearLayoutThirdRow.height*0.12);
						linearLayoutThirdRowLEDsItem1.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem1);
							LinearLayout linearLayoutThirdRowLEDsItem1Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem1Description);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem1Description = linearLayoutThirdRowLEDsItem1Description.getLayoutParams();
							paramsLinearLayoutThirdRowLEDsItem1Description.width = (int)(paramsLinearLayoutThirdRowLEDs.width*0.74);
							paramsLinearLayoutThirdRowLEDsItem1Description.height = paramsLinearLayoutThirdRowLEDsItem1.height;
							linearLayoutThirdRowLEDsItem1Description.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem1Description);

							LinearLayout linearLayoutThirdRowLEDsItem1Image = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem1Image);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem1Image = linearLayoutThirdRowLEDsItem1Image.getLayoutParams();
							paramsLinearLayoutThirdRowLEDsItem1Image.width = (int)(paramsLinearLayoutThirdRowLEDs.width*0.26);
							paramsLinearLayoutThirdRowLEDsItem1Image.height = paramsLinearLayoutThirdRowLEDsItem1.height;
							linearLayoutThirdRowLEDsItem1Image.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem1Image);

						LinearLayout linearLayoutThirdRowLEDsItem2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem2);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem2 = linearLayoutThirdRowLEDsItem2.getLayoutParams();
						paramsLinearLayoutThirdRowLEDsItem2.width = paramsLinearLayoutThirdRowLEDs.width;
						paramsLinearLayoutThirdRowLEDsItem2.height = (int)(paramsLinearLayoutThirdRow.height*0.12);
						linearLayoutThirdRowLEDsItem2.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem2);
							LinearLayout linearLayoutThirdRowLEDsItem2Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem2Description);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem2Description = linearLayoutThirdRowLEDsItem2Description.getLayoutParams();
							paramsLinearLayoutThirdRowLEDsItem2Description.width = (int)(paramsLinearLayoutThirdRowLEDs.width*0.74);
							paramsLinearLayoutThirdRowLEDsItem2Description.height = paramsLinearLayoutThirdRowLEDsItem2.height;
							linearLayoutThirdRowLEDsItem2Description.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem2Description);
								TextView textViewThirdRowLEDsItem2Description = (TextView) rootView.findViewById(R.id.textViewThirdRowLEDsItem2Description);

							LinearLayout linearLayoutThirdRowLEDsItem2Image = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem2Image);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem2Image = linearLayoutThirdRowLEDsItem2Image.getLayoutParams();
							paramsLinearLayoutThirdRowLEDsItem2Image.width = (int)(paramsLinearLayoutThirdRowLEDs.width*0.26);
							paramsLinearLayoutThirdRowLEDsItem2Image.height = paramsLinearLayoutThirdRowLEDsItem2.height;
							linearLayoutThirdRowLEDsItem2Image.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem2Image);

						LinearLayout linearLayoutThirdRowLEDsItem3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem3);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem3 = linearLayoutThirdRowLEDsItem3.getLayoutParams();
						paramsLinearLayoutThirdRowLEDsItem3.width = paramsLinearLayoutThirdRowLEDs.width;
						paramsLinearLayoutThirdRowLEDsItem3.height = (int)(paramsLinearLayoutThirdRow.height*0.12);
						linearLayoutThirdRowLEDsItem3.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem3);
							LinearLayout linearLayoutThirdRowLEDsItem3Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem3Description);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem3Description = linearLayoutThirdRowLEDsItem3Description.getLayoutParams();
							paramsLinearLayoutThirdRowLEDsItem3Description.width = (int)(paramsLinearLayoutThirdRowLEDs.width*0.74);
							paramsLinearLayoutThirdRowLEDsItem3Description.height = paramsLinearLayoutThirdRowLEDsItem3.height;
							linearLayoutThirdRowLEDsItem3Description.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem3Description);

							LinearLayout linearLayoutThirdRowLEDsItem3Image = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem3Image);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem3Image = linearLayoutThirdRowLEDsItem3Image.getLayoutParams();
							paramsLinearLayoutThirdRowLEDsItem3Image.width = (int)(paramsLinearLayoutThirdRowLEDs.width*0.26);
							paramsLinearLayoutThirdRowLEDsItem3Image.height = paramsLinearLayoutThirdRowLEDsItem3.height;
							linearLayoutThirdRowLEDsItem3Image.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem3Image);

						LinearLayout linearLayoutThirdRowLEDsItem4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem4);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem4 = linearLayoutThirdRowLEDsItem4.getLayoutParams();
						paramsLinearLayoutThirdRowLEDsItem4.width = paramsLinearLayoutThirdRowLEDs.width;
						paramsLinearLayoutThirdRowLEDsItem4.height = (int)(paramsLinearLayoutThirdRow.height*0.12);
						linearLayoutThirdRowLEDsItem4.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem4);
							LinearLayout linearLayoutThirdRowLEDsItem4Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem4Description);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem4Description = linearLayoutThirdRowLEDsItem4Description.getLayoutParams();
							paramsLinearLayoutThirdRowLEDsItem4Description.width = (int)(paramsLinearLayoutThirdRowLEDs.width*0.74);
							paramsLinearLayoutThirdRowLEDsItem4Description.height = paramsLinearLayoutThirdRowLEDsItem4.height;
							linearLayoutThirdRowLEDsItem4Description.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem4Description);

							LinearLayout linearLayoutThirdRowLEDsItem4Image = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem4Image);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem4Image = linearLayoutThirdRowLEDsItem4Image.getLayoutParams();
							paramsLinearLayoutThirdRowLEDsItem4Image.width = (int)(paramsLinearLayoutThirdRowLEDs.width*0.26);
							paramsLinearLayoutThirdRowLEDsItem4Image.height = paramsLinearLayoutThirdRowLEDsItem4.height;
							linearLayoutThirdRowLEDsItem4Image.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem4Image);

						LinearLayout linearLayoutThirdRowLEDsItem5 = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem5);
						ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem5 = linearLayoutThirdRowLEDsItem5.getLayoutParams();
						paramsLinearLayoutThirdRowLEDsItem5.width = paramsLinearLayoutThirdRowLEDs.width;
						paramsLinearLayoutThirdRowLEDsItem5.height = (int)(paramsLinearLayoutThirdRow.height*0.52);
						linearLayoutThirdRowLEDsItem5.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem5);
							LinearLayout linearLayoutThirdRowLEDsItem5Inside = (LinearLayout) rootView.findViewById(R.id.linearLayoutThirdRowLEDsItem5Inside);
							ViewGroup.LayoutParams paramsLinearLayoutThirdRowLEDsItem5Inside = linearLayoutThirdRowLEDsItem5Inside.getLayoutParams();
							paramsLinearLayoutThirdRowLEDsItem5Inside.width = (int)(paramsLinearLayoutThirdRowLEDsItem5.width * 0.9);
							paramsLinearLayoutThirdRowLEDsItem5Inside.height = (int)(paramsLinearLayoutThirdRowLEDsItem5.height * 0.9);
							linearLayoutThirdRowLEDsItem5Inside.setLayoutParams(paramsLinearLayoutThirdRowLEDsItem5Inside);
								ScrollView scrollViewLeftPanelRestRowsInside = (ScrollView) rootView.findViewById(R.id.scrollViewLeftPanelRestRowsInside);
									textViewThirdRowLEDsItem5Memo = (TextView) rootView.findViewById(R.id.textViewThirdRowLEDsItem5Memo);
//-END THIRD ROW

//-END LEFT COLUMN

//-RIGHT COLUMN
			// Linear layout "LinearLayoutRightColumn"
			LinearLayout linearLayoutRightColumn = (LinearLayout) rootView.findViewById(R.id.linearLayoutRightColumn);
			ViewGroup.LayoutParams paramsLinearLayoutRightColumn = linearLayoutRightColumn.getLayoutParams();
			paramsLinearLayoutRightColumn.width = (int)(paramsRelativeLayoutGauges.width * 0.26);
			paramsLinearLayoutRightColumn.height = paramsLinearLayoutLefColumn.height;
			linearLayoutRightColumn.setX(paramsLinearLayoutLefColumn.width-paramsLinearLayoutRightColumn.width);
			linearLayoutRightColumn.setLayoutParams(paramsLinearLayoutRightColumn);

				// Linear layout "LinearLayoutTitleScreen"
				LinearLayout linearLayoutTitleScreen = (LinearLayout) rootView.findViewById(R.id.linearLayoutTitleScreen);
				ViewGroup.LayoutParams paramsLinearLayoutTitleScreen = linearLayoutTitleScreen.getLayoutParams();
				paramsLinearLayoutTitleScreen.width = paramsLinearLayoutRightColumn.width;
				paramsLinearLayoutTitleScreen.height = (int)(paramsLinearLayoutRightColumn.height * 0.13);
				linearLayoutTitleScreen.setLayoutParams(paramsLinearLayoutTitleScreen);
					TextView textViewTitleScreen = (TextView) rootView.findViewById(R.id.textViewTitleScreen);
					Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 
					textViewTitleScreen.setTypeface(type);
					textViewTitleScreen.setTypeface(textViewTitleScreen.getTypeface(), Typeface.ITALIC);

				linearLayoutFirstSecondRowBigGauge = (LinearLayout) rootView.findViewById(R.id.linearLayoutFirstSecondRowBigGauge);
				paramsLinearLayoutFirstSecondRowBigGauge = linearLayoutFirstSecondRowBigGauge.getLayoutParams();
				paramsLinearLayoutFirstSecondRowBigGauge.width = paramsLinearLayoutRightColumn.width - MARGIN_RELATIVE_LAYOUT_GAUGE;
				paramsLinearLayoutFirstSecondRowBigGauge.height = paramsLinearLayoutRightColumn.width - MARGIN_RELATIVE_LAYOUT_GAUGE;
				linearLayoutFirstSecondRowBigGauge.setLayoutParams(paramsLinearLayoutFirstSecondRowBigGauge);
				gaugeFirstSecondRowsBigGauge = createCircularGauge(10,linearLayoutFirstSecondRowBigGauge,paramsLinearLayoutFirstSecondRowBigGauge,"RPM");
				linearLayoutFirstSecondRowBigGauge.addView(gaugeFirstSecondRowsBigGauge);
				String commandSQL="SELECT * FROM AlarmParameters WHERE Parameter_AlarmParameters='RPM'";
				cursor = dewLineDB.rawQuery(commandSQL,null);
				if(cursor.getCount()>0) {
					cursor.moveToFirst();
					RPM_Idle = cursor.getFloat(cursor.getColumnIndex("Yellow_LowThresholds_AtIdle_AlarmParameters"));
					RPM_WOT = cursor.getFloat(cursor.getColumnIndex("Red_HighThresholds_AtIdle_AlarmParameters"));
				}

				LinearLayout linearLayoutSecondColumnThirdRow = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRow);
				ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRow = linearLayoutSecondColumnThirdRow.getLayoutParams();
				paramsLinearLayoutSecondColumnThirdRow.width = paramsLinearLayoutRightColumn.width - 5;
				paramsLinearLayoutSecondColumnThirdRow.height = paramsLinearLayoutRightColumn.height -
						paramsLinearLayoutTitleScreen.height - paramsLinearLayoutFirstSecondRowBigGauge.height - 90;
				linearLayoutSecondColumnThirdRow.setLayoutParams(paramsLinearLayoutSecondColumnThirdRow);

					LinearLayout linearLayoutSecondColumnThirdRowItem1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem1);
					ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem1 = linearLayoutSecondColumnThirdRowItem1.getLayoutParams();
					paramsLinearLayoutSecondColumnThirdRowItem1.width = paramsLinearLayoutSecondColumnThirdRow.width;
					paramsLinearLayoutSecondColumnThirdRowItem1.height = (int)(paramsLinearLayoutSecondColumnThirdRow.height/7);
					linearLayoutSecondColumnThirdRowItem1.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem1);
						LinearLayout linearLayoutSecondColumnThirdRowItem1Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem1Description);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem1Description = linearLayoutSecondColumnThirdRowItem1Description.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem1Description.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem1.width*0.5);
						paramsLinearLayoutSecondColumnThirdRowItem1Description.height = paramsLinearLayoutSecondColumnThirdRowItem1.height;
						linearLayoutSecondColumnThirdRowItem1Description.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem1Description);
							TextView textViewSecondColumnThirdRowItem1Description = (TextView) rootView.findViewById(R.id.textViewSecondColumnThirdRowItem1Description);
							//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 

						LinearLayout linearLayoutSecondColumnThirdRowItem1Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem1Value);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem1Value = linearLayoutSecondColumnThirdRowItem1Value.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem1Value.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem1.width*0.31);
						paramsLinearLayoutSecondColumnThirdRowItem1Value.height = paramsLinearLayoutSecondColumnThirdRowItem1.height;
						linearLayoutSecondColumnThirdRowItem1Value.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem1Value);
							textViewSecondColumnThirdRowItem1Value = (TextView) rootView.findViewById(R.id.textViewSSecondColumnThirdRowItem1Value);

						LinearLayout linearLayoutSecondColumnThirdRowItem1Units = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem1Units);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem1Units = linearLayoutSecondColumnThirdRowItem1Units.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem1Units.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem1.width*0.19);
						paramsLinearLayoutSecondColumnThirdRowItem1Units.height = paramsLinearLayoutSecondColumnThirdRowItem1.height;
						linearLayoutSecondColumnThirdRowItem1Units.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem1Units);

					LinearLayout linearLayoutSecondColumnThirdRowItem2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem2);
					ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem2 = linearLayoutSecondColumnThirdRowItem2.getLayoutParams();
					paramsLinearLayoutSecondColumnThirdRowItem2.width = paramsLinearLayoutSecondColumnThirdRow.width;
					paramsLinearLayoutSecondColumnThirdRowItem2.height = (int)(paramsLinearLayoutSecondColumnThirdRow.height/7);
					linearLayoutSecondColumnThirdRowItem2.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem2);
						LinearLayout linearLayoutSecondColumnThirdRowItem2Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem2Description);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem2Description = linearLayoutSecondColumnThirdRowItem2Description.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem2Description.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem2.width*0.5);
						paramsLinearLayoutSecondColumnThirdRowItem2Description.height = paramsLinearLayoutSecondColumnThirdRowItem2.height;
						linearLayoutSecondColumnThirdRowItem2Description.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem2Description);
							TextView textViewSecondColumnThirdRowItem2Description = (TextView) rootView.findViewById(R.id.textViewSecondColumnThirdRowItem2Description);
							//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 

						LinearLayout linearLayoutSecondColumnThirdRowItem2Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem2Value);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem2Value = linearLayoutSecondColumnThirdRowItem2Value.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem2Value.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem2.width*0.31);
						paramsLinearLayoutSecondColumnThirdRowItem2Value.height = paramsLinearLayoutSecondColumnThirdRowItem2.height;
						linearLayoutSecondColumnThirdRowItem2Value.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem2Value);
							textViewSecondColumnThirdRowItem2Value = (TextView) rootView.findViewById(R.id.textViewSSecondColumnThirdRowItem2Value);

						LinearLayout linearLayoutSecondColumnThirdRowItem2Units = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem2Units);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem2Units = linearLayoutSecondColumnThirdRowItem2Units.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem2Units.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem2.width*0.19);
						paramsLinearLayoutSecondColumnThirdRowItem2Units.height = paramsLinearLayoutSecondColumnThirdRowItem2.height;
						linearLayoutSecondColumnThirdRowItem2Units.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem2Units);

					LinearLayout linearLayoutSecondColumnThirdRowItem3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem3);
					ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem3 = linearLayoutSecondColumnThirdRowItem3.getLayoutParams();
					paramsLinearLayoutSecondColumnThirdRowItem3.width = paramsLinearLayoutSecondColumnThirdRow.width;
					paramsLinearLayoutSecondColumnThirdRowItem3.height = (int)(paramsLinearLayoutSecondColumnThirdRow.height/7);
					linearLayoutSecondColumnThirdRowItem3.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem3);
						LinearLayout linearLayoutSecondColumnThirdRowItem3Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem3Description);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem3Description = linearLayoutSecondColumnThirdRowItem3Description.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem3Description.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem3.width*0.5);
						paramsLinearLayoutSecondColumnThirdRowItem3Description.height = paramsLinearLayoutSecondColumnThirdRowItem3.height;
						linearLayoutSecondColumnThirdRowItem3Description.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem3Description);
							TextView textViewSecondColumnThirdRowItem3Description = (TextView) rootView.findViewById(R.id.textViewSecondColumnThirdRowItem3Description);
							//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 

						LinearLayout linearLayoutSecondColumnThirdRowItem3Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem3Value);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem3Value = linearLayoutSecondColumnThirdRowItem3Value.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem3Value.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem3.width*0.31);
						paramsLinearLayoutSecondColumnThirdRowItem3Value.height = paramsLinearLayoutSecondColumnThirdRowItem3.height;
						linearLayoutSecondColumnThirdRowItem3Value.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem3Value);
							textViewSecondColumnThirdRowItem3Value = (TextView) rootView.findViewById(R.id.textViewSSecondColumnThirdRowItem3Value);

						LinearLayout linearLayoutSecondColumnThirdRowItem3Units = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem3Units);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem3Units = linearLayoutSecondColumnThirdRowItem3Units.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem3Units.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem3.width*0.19);
						paramsLinearLayoutSecondColumnThirdRowItem3Units.height = paramsLinearLayoutSecondColumnThirdRowItem3.height;
						linearLayoutSecondColumnThirdRowItem3Units.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem3Units);

					LinearLayout linearLayoutSecondColumnThirdRowItem4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem4);
					ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem4 = linearLayoutSecondColumnThirdRowItem4.getLayoutParams();
					paramsLinearLayoutSecondColumnThirdRowItem4.width = paramsLinearLayoutSecondColumnThirdRow.width;
					paramsLinearLayoutSecondColumnThirdRowItem4.height = (int)(paramsLinearLayoutSecondColumnThirdRow.height/7);
					linearLayoutSecondColumnThirdRowItem4.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem4);
						LinearLayout linearLayoutSecondColumnThirdRowItem4Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem4Description);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem4Description = linearLayoutSecondColumnThirdRowItem4Description.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem4Description.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem4.width*0.5);
						paramsLinearLayoutSecondColumnThirdRowItem4Description.height = paramsLinearLayoutSecondColumnThirdRowItem4.height;
						linearLayoutSecondColumnThirdRowItem4Description.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem4Description);
							TextView textViewSecondColumnThirdRowItem4Description = (TextView) rootView.findViewById(R.id.textViewSecondColumnThirdRowItem4Description);
							//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 

						LinearLayout linearLayoutSecondColumnThirdRowItem4Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem4Value);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem4Value = linearLayoutSecondColumnThirdRowItem4Value.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem4Value.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem4.width*0.31);
						paramsLinearLayoutSecondColumnThirdRowItem4Value.height = paramsLinearLayoutSecondColumnThirdRowItem4.height;
						linearLayoutSecondColumnThirdRowItem4Value.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem4Value);
							textViewSecondColumnThirdRowItem4Value = (TextView) rootView.findViewById(R.id.textViewSSecondColumnThirdRowItem4Value);

						LinearLayout linearLayoutSecondColumnThirdRowItem4Units = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem4Units);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem4Units = linearLayoutSecondColumnThirdRowItem4Units.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem4Units.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem4.width*0.19);
						paramsLinearLayoutSecondColumnThirdRowItem4Units.height = paramsLinearLayoutSecondColumnThirdRowItem4.height;
						linearLayoutSecondColumnThirdRowItem4Units.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem4Units);

					LinearLayout linearLayoutSecondColumnThirdRowItem5 = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem5);
					ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem5 = linearLayoutSecondColumnThirdRowItem5.getLayoutParams();
					paramsLinearLayoutSecondColumnThirdRowItem5.width = paramsLinearLayoutSecondColumnThirdRow.width;
					paramsLinearLayoutSecondColumnThirdRowItem5.height = (int)(paramsLinearLayoutSecondColumnThirdRow.height/7);
					linearLayoutSecondColumnThirdRowItem5.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem5);
						LinearLayout linearLayoutSecondColumnThirdRowItem5Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem5Description);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem5Description = linearLayoutSecondColumnThirdRowItem5Description.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem5Description.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem5.width*0.5);
						paramsLinearLayoutSecondColumnThirdRowItem5Description.height = paramsLinearLayoutSecondColumnThirdRowItem5.height;
						linearLayoutSecondColumnThirdRowItem5Description.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem5Description);
							TextView textViewSecondColumnThirdRowItem5Description = (TextView) rootView.findViewById(R.id.textViewSecondColumnThirdRowItem5Description);
							//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 

						LinearLayout linearLayoutSecondColumnThirdRowItem5Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem5Value);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem5Value = linearLayoutSecondColumnThirdRowItem5Value.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem5Value.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem5.width*0.31);
						paramsLinearLayoutSecondColumnThirdRowItem5Value.height = paramsLinearLayoutSecondColumnThirdRowItem5.height;
						linearLayoutSecondColumnThirdRowItem5Value.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem5Value);
							textViewSecondColumnThirdRowItem5Value = (TextView) rootView.findViewById(R.id.textViewSSecondColumnThirdRowItem5Value);

						LinearLayout linearLayoutSecondColumnThirdRowItem5Units = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem5Units);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem5Units = linearLayoutSecondColumnThirdRowItem5Units.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem5Units.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem5.width*0.19);
						paramsLinearLayoutSecondColumnThirdRowItem5Units.height = paramsLinearLayoutSecondColumnThirdRowItem5.height;
						linearLayoutSecondColumnThirdRowItem5Units.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem5Units);

					LinearLayout linearLayoutSecondColumnThirdRowItem6 = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem6);
					ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem6 = linearLayoutSecondColumnThirdRowItem6.getLayoutParams();
					paramsLinearLayoutSecondColumnThirdRowItem6.width = paramsLinearLayoutSecondColumnThirdRow.width;
					paramsLinearLayoutSecondColumnThirdRowItem6.height = (int)(paramsLinearLayoutSecondColumnThirdRow.height/7);
					linearLayoutSecondColumnThirdRowItem6.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem6);
						LinearLayout linearLayoutSecondColumnThirdRowItem6Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem6Description);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem6Description = linearLayoutSecondColumnThirdRowItem6Description.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem6Description.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem6.width*0.5);
						paramsLinearLayoutSecondColumnThirdRowItem6Description.height = paramsLinearLayoutSecondColumnThirdRowItem6.height;
						linearLayoutSecondColumnThirdRowItem6Description.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem6Description);
							TextView textViewSecondColumnThirdRowItem6Description = (TextView) rootView.findViewById(R.id.textViewSecondColumnThirdRowItem6Description);
							//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 

						LinearLayout linearLayoutSecondColumnThirdRowItem6Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem6Value);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem6Value = linearLayoutSecondColumnThirdRowItem6Value.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem6Value.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem6.width*0.31);
						paramsLinearLayoutSecondColumnThirdRowItem6Value.height = paramsLinearLayoutSecondColumnThirdRowItem6.height;
						linearLayoutSecondColumnThirdRowItem6Value.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem6Value);
							textViewSecondColumnThirdRowItem6Value = (TextView) rootView.findViewById(R.id.textViewSSecondColumnThirdRowItem6Value);

						LinearLayout linearLayoutSecondColumnThirdRowItem6Units = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem6Units);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem6Units = linearLayoutSecondColumnThirdRowItem6Units.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem6Units.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem6.width*0.19);
						paramsLinearLayoutSecondColumnThirdRowItem6Units.height = paramsLinearLayoutSecondColumnThirdRowItem6.height;

					LinearLayout linearLayoutSecondColumnThirdRowItem7 = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem7);
					ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem7 = linearLayoutSecondColumnThirdRowItem7.getLayoutParams();
					paramsLinearLayoutSecondColumnThirdRowItem7.width = paramsLinearLayoutSecondColumnThirdRow.width;
					paramsLinearLayoutSecondColumnThirdRowItem7.height = (int)(paramsLinearLayoutSecondColumnThirdRow.height/7);
					linearLayoutSecondColumnThirdRowItem7.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem7);
						LinearLayout linearLayoutSecondColumnThirdRowItem7Description = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem7Description);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem7Description = linearLayoutSecondColumnThirdRowItem7Description.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem7Description.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem7.width*0.5);
						paramsLinearLayoutSecondColumnThirdRowItem7Description.height = paramsLinearLayoutSecondColumnThirdRowItem7.height;
						linearLayoutSecondColumnThirdRowItem7Description.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem7Description);
							TextView textViewSecondColumnThirdRowItem7Description = (TextView) rootView.findViewById(R.id.textViewSecondColumnThirdRowItem7Description);
							//Typeface type = Typeface.createFromAsset(Globals.context.getAssets(),"fonts/coopbl.ttf"); 

						LinearLayout linearLayoutSecondColumnThirdRowItem7Value = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem7Value);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem7Value = linearLayoutSecondColumnThirdRowItem7Value.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem7Value.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem7.width*0.31);
						paramsLinearLayoutSecondColumnThirdRowItem7Value.height = paramsLinearLayoutSecondColumnThirdRowItem7.height;
						linearLayoutSecondColumnThirdRowItem7Value.setLayoutParams(paramsLinearLayoutSecondColumnThirdRowItem7Value);
							textViewSecondColumnThirdRowItem7Value = (TextView) rootView.findViewById(R.id.textViewSSecondColumnThirdRowItem7Value);

						LinearLayout linearLayoutSecondColumnThirdRowItem7Units = (LinearLayout) rootView.findViewById(R.id.linearLayoutSecondColumnThirdRowItem7Units);
						ViewGroup.LayoutParams paramsLinearLayoutSecondColumnThirdRowItem7Units = linearLayoutSecondColumnThirdRowItem7Units.getLayoutParams();
						paramsLinearLayoutSecondColumnThirdRowItem7Units.width = (int)(paramsLinearLayoutSecondColumnThirdRowItem7.width*0.19);
						paramsLinearLayoutSecondColumnThirdRowItem7Units.height = paramsLinearLayoutSecondColumnThirdRowItem7.height;
//-END RIGHT COLUMN

// -END  GAUGES

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
				buttonFuel.setLayoutParams(paramsButtonFuel);

			LinearLayout linearLayoutButton4 = (LinearLayout) rootView.findViewById(R.id.linearLayoutButton4);
			ViewGroup.LayoutParams paramsLinearLayoutButton4 = linearLayoutButton4.getLayoutParams();
			paramsLinearLayoutButton4.width = paramsLinearLayoutButtons.width / 6;
			linearLayoutButton4.setLayoutParams(paramsLinearLayoutButton4);
				Button button4 = (Button) rootView.findViewById(R.id.button4);
				ViewGroup.LayoutParams paramsButton4 = button4.getLayoutParams();
				paramsButton4.height = (int)(paramsLinearLayoutButtons.height * 0.65);

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

	public RelativeLayout createCircularGauge(int numberGauge,LinearLayout linearLayout,ViewGroup.LayoutParams paramsLinearLayout,
			String parameter) {
		RelativeLayout gauge = new RelativeLayout(Globals.context);
		LinearLayout.LayoutParams paramsRelativeLayoutGauge = new LinearLayout.LayoutParams(paramsLinearLayout.width-
				2*MARGIN_RELATIVE_LAYOUT_GAUGE,paramsLinearLayout.height-2*MARGIN_RELATIVE_LAYOUT_GAUGE);
		gauge.setX(MARGIN_RELATIVE_LAYOUT_GAUGE);
		gauge.setY(MARGIN_RELATIVE_LAYOUT_GAUGE);
//gauge.setBackgroundColor(Color.GREEN);
		gauge.setLayoutParams(paramsRelativeLayoutGauge);

		String stringGauge=null;
		// GAUGE
		// Read SVG file
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			if(SEGMENTS_GAUGE[numberGauge]==5) {
				if(FLAG_WITH_BACKGROUNDS)
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_5_segments_background));
				else
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_5_segments));
			}
			else if(SEGMENTS_GAUGE[numberGauge]==6) {
				if(FLAG_WITH_BACKGROUNDS)
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_6_segments_background));
				else
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_6_segments));
			}
			else
				if(FLAG_WITH_BACKGROUNDS)
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_7_segments_background));
				else
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_7_segments));
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		// Prepare gauge

		// Incorporate thresholds
		String commandSQL="SELECT * FROM AlarmParameters WHERE Parameter_AlarmParameters='"+parameter+"'";
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
			if(FLAG_WITH_BACKGROUNDS)
				linearLayoutTextValue.setBackgroundResource(R.drawable.border_with_background);
			else
				linearLayoutTextValue.setBackgroundResource(R.drawable.border);
			linearLayoutTextValue.setY(paramsLinearLayout.height/2+paramsLinearLayout.height/36);
			linearLayoutTextValue.setLayoutParams(paramsLinearLayoutValueGauge);
			textViewValueGauge[numberGauge] = new TextView(Globals.context);
			textViewValueGauge[numberGauge].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			textViewValueGauge[numberGauge].setGravity(Gravity.CENTER);
			textViewValueGauge[numberGauge].setTextSize(TypedValue.COMPLEX_UNIT_PX,20);
			if(FLAG_WITH_BACKGROUNDS)
				textViewValueGauge[numberGauge].setTextColor(Color.BLACK);
			else
				textViewValueGauge[numberGauge].setTextColor(Color.rgb(68,114,196));
			if(!FLAG_WITH_BACKGROUNDS)
				textViewValueGauge[numberGauge].setTypeface(textViewValueGauge[numberGauge].getTypeface(), Typeface.BOLD);
			textViewValueGauge[numberGauge].setText(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0]);
			linearLayoutTextValue.addView(textViewValueGauge[numberGauge]);
			gauge.addView(linearLayoutTextValue);

			// Add units label to gauge
			LinearLayout linearLayoutUnits = new LinearLayout(Globals.context);
			LinearLayout.LayoutParams paramsLinearLayoutUnits = new LinearLayout.LayoutParams(paramsLinearLayout.width/4,
					paramsLinearLayout.height/6);
			linearLayoutUnits.setGravity(Gravity.CENTER);
			if(FLAG_WITH_BACKGROUNDS) {
				linearLayoutUnits.setX((int)(paramsLinearLayout.width * 0.55));
				linearLayoutUnits.setY((int)(paramsLinearLayout.height *0.69));
			}
			else {
				linearLayoutUnits.setX((int)(paramsLinearLayout.width * 0.59));
				linearLayoutUnits.setY((int)(paramsLinearLayout.height *0.735));
			}
			linearLayoutUnits.setBackgroundColor(Color.parseColor("#00000000"));
			linearLayoutUnits.setLayoutParams(paramsLinearLayoutUnits);
			TextView textViewUnits = new TextView(Globals.context);
			textViewUnits.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			textViewUnits.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
			textViewUnits.setTextSize(TypedValue.COMPLEX_UNIT_PX,17);
			if(FLAG_WITH_BACKGROUNDS)
				textViewUnits.setTextColor(Color.BLACK);
			else
				textViewUnits.setTextColor(Color.rgb(68,114,196));
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
			if(FLAG_WITH_BACKGROUNDS)
				svg = SVG.getFromResource(Globals.context, R.raw.needle_background);
			else
				svg = SVG.getFromResource(Globals.context, R.raw.needle);
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

	public void createSVGCircularGaugeDynamic(int numberGauge) {

		String stringGauge=null;
		// GAUGE
		// Read SVG file
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			if(SEGMENTS_GAUGE[numberGauge]==5) {
				if(FLAG_WITH_BACKGROUNDS)
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_5_segments_background));
				else
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_5_segments));
			}
			else if(SEGMENTS_GAUGE[numberGauge]==6) {
				if(FLAG_WITH_BACKGROUNDS)
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_6_segments_background));
				else
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_6_segments));
			}
			else
				if(FLAG_WITH_BACKGROUNDS)
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_7_segments_background));
				else
					docGauge = builder.parse(Globals.context.getResources().openRawResource(R.raw.gauge_7_segments));
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
			assignValuesThresholdVariablesDynamic();
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
			svgImageView[numberGauge].setSVG(svg);
			LayoutParams paramsSVG=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			svgImageView[numberGauge].setLayoutParams(paramsSVG);
		} catch (SVGParseException e){
			e.printStackTrace();
		}

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

	public void assignValuesThresholdVariablesDynamic() {
		try {
			lowThresholdShutdown_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			lowThresholdShutdown_Idle = -1;
		}
		try {
			lowThresholdShutdown_WOT = Float.valueOf(cursor.getString(cursor.getColumnIndex("Shutdn_LowThresholds_AtWot_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			lowThresholdShutdown_WOT = -1;
		}
		if(lowThresholdShutdown_Idle>=0&&lowThresholdShutdown_WOT>=0)
			lowThresholdShutdown_Idle += (valueRPMCurrent - RPM_Idle) / (RPM_WOT - RPM_Idle) *
						(lowThresholdShutdown_WOT - lowThresholdShutdown_Idle);

		try {
			lowThresholdRed_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			lowThresholdRed_Idle = -1;
		}
		try {
			lowThresholdRed_WOT = Float.valueOf(cursor.getString(cursor.getColumnIndex("Red_LowThresholds_AtWot_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			lowThresholdRed_WOT = -1;
		}
//System.out.println("Static lowThresholdRed_Idle:   "+lowThresholdRed_Idle);
		if(lowThresholdRed_Idle>=0&&lowThresholdRed_WOT>=0)
			lowThresholdRed_Idle += (valueRPMCurrent - RPM_Idle) / (RPM_WOT - RPM_Idle) *
						(lowThresholdRed_WOT - lowThresholdRed_Idle);
//System.out.println("Dynamic lowThresholdRed_Idle:   "+lowThresholdRed_Idle);

		try {
			lowThresholdYellow_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			lowThresholdYellow_Idle = -1;
		}
		try {
			lowThresholdYellow_WOT = Float.valueOf(cursor.getString(cursor.getColumnIndex("Yellow_LowThresholds_AtWot_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			lowThresholdYellow_WOT = -1;
		}
//System.out.println("Static lowThresholdYellow_Idle:  "+lowThresholdYellow_Idle);
		if(lowThresholdYellow_Idle>=0&&lowThresholdYellow_WOT>=0)
			lowThresholdYellow_Idle += (valueRPMCurrent - RPM_Idle) / (RPM_WOT - RPM_Idle) *
						(lowThresholdYellow_WOT - lowThresholdYellow_Idle);
//System.out.println("Dynamic lowThresholdYellow_Idle:   "+lowThresholdYellow_Idle);

		try {
			highThresholdYellow_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			highThresholdYellow_Idle = -1;
		}
		try {
			highThresholdYellow_WOT = Float.valueOf(cursor.getString(cursor.getColumnIndex("Yellow_HighThresholds_AtWot_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			highThresholdYellow_WOT = -1;
		}
		if(highThresholdYellow_Idle>=0&&highThresholdYellow_WOT>=0)
			highThresholdYellow_Idle += (valueRPMCurrent - RPM_Idle) / (RPM_WOT - RPM_Idle) *
						(highThresholdYellow_WOT - highThresholdYellow_Idle);

		try {
			highThresholdRed_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			highThresholdRed_Idle = -1;
		}
		try {
			highThresholdRed_WOT = Float.valueOf(cursor.getString(cursor.getColumnIndex("Red_HighThresholds_AtWot_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			highThresholdRed_WOT = -1;
		}
		if(highThresholdRed_Idle>=0&&highThresholdRed_WOT>=0)
			highThresholdRed_Idle += (valueRPMCurrent - RPM_Idle) / (RPM_WOT - RPM_Idle) *
						(highThresholdRed_WOT - highThresholdRed_Idle);

		try {
			highThresholdShutdown_Idle = Float.valueOf(cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtIdle_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			highThresholdShutdown_Idle = -1;
		}
		try {
			highThresholdShutdown_WOT = Float.valueOf(cursor.getString(cursor.getColumnIndex("Shutdn_HighThresholds_AtWot_AlarmParameters")));
		}
		catch (NumberFormatException e) {
			highThresholdShutdown_WOT = -1;
		}
		if(highThresholdShutdown_Idle>=0&&highThresholdShutdown_WOT>=0)
			highThresholdShutdown_Idle += (valueRPMCurrent - RPM_Idle) / (RPM_WOT - RPM_Idle) *
						(highThresholdShutdown_WOT - highThresholdShutdown_Idle);
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
				if(numberGauge==10)
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
		if(textViewThirdRowLEDsItem5Memo.getLineCount()>1000)
			textViewThirdRowLEDsItem5Memo.setText("");
		// LOW THRESHOLDS
		if(lowThresholdRed_Idle>0&&Math.round(valueParameter)!=Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0])&&
				valueParameter<=lowThresholdRed_Idle) {
			if(actualFaceDisplayed[numberGauge]!=2) {
				textViewThirdRowLEDsItem5Memo.setText(textViewThirdRowLEDsItem5Memo.getText()+"\n"+parameter[numberGauge]+
						": Low Red");
				linearLayoutGauge.setBackgroundResource(R.drawable.ringed_gauge_face_with_red_alert);
				actualFaceDisplayed[numberGauge] = 2;
			}
		}
		else if(lowThresholdYellow_Idle>0&&Math.round(valueParameter)!=Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[numberGauge][0])&&
				valueParameter<=lowThresholdYellow_Idle) {
			if(actualFaceDisplayed[numberGauge]!=1) {
				textViewThirdRowLEDsItem5Memo.setText(textViewThirdRowLEDsItem5Memo.getText()+"\n"+parameter[numberGauge]+
						": Low Yellow");
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
		scrollViewThirdRowLEDsItem5Inside.setScrollY(View.FOCUS_DOWN);
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
			if(numberGauge==0||numberGauge==1) {
				if(FLAG_WITH_BACKGROUNDS)
					docLinearGauge[numberGauge] = builder.parse(Globals.context.getResources().
							openRawResource(R.raw.gauge_linear_4_segments_1_background));
				else
					docLinearGauge[numberGauge] = builder.parse(Globals.context.getResources().
							openRawResource(R.raw.gauge_linear_4_segments_1));
			}
			else {
				if(FLAG_WITH_BACKGROUNDS)
					docLinearGauge[numberGauge] = builder.parse(Globals.context.getResources().
							openRawResource(R.raw.gauge_linear_4_segments_2_background));
				else
					docLinearGauge[numberGauge] = builder.parse(Globals.context.getResources().
							openRawResource(R.raw.gauge_linear_4_segments_2));
			}
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

	public void simulatorCircularGaugesDemo () {
		float valueParameterCurrent;
		RelativeLayout gauge;
		for(int i=0;i<NUMBER_CIRCULAR_GAUGES-1;i++) {
			valueParameterCurrent = ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES[i][ConstantsGlobals.counter_demo_data_rpm/
						ConstantsGlobals.RELATION_SAMPLES_RPM_OTHERS];

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

			valueMinimum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[i][0]);
			try 
			{
				valueMaximum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[i][SEGMENTS_GAUGE[i]]);
			}
			catch (ParseException e) 
			{
				valueMaximum = 0;
			}
			if(ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES[i][ConstantsGlobals.counter_demo_data_rpm/
						ConstantsGlobals.RELATION_SAMPLES_RPM_OTHERS]<100)
				textViewValueGauge[i].setText(oneDecForm.format(ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES[i][ConstantsGlobals.
							counter_demo_data_rpm/ConstantsGlobals.RELATION_SAMPLES_RPM_OTHERS]));
			else
				textViewValueGauge[i].setText(zeroDecForm.format(ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES[i][ConstantsGlobals.
							counter_demo_data_rpm/ConstantsGlobals.RELATION_SAMPLES_RPM_OTHERS]));
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
			positionDegrees = Math.round((ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES[i][ConstantsGlobals.counter_demo_data_rpm/
						ConstantsGlobals.RELATION_SAMPLES_RPM_OTHERS] -
					valueMinimum) * (maximumAngleGauge - (-90)) / (valueMaximum - valueMinimum) + (-90));
			linearLayoutNeedle[i].animate().rotation(positionDegrees).start();
//System.out.println(ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES[i][ConstantsGlobals.counter_demo_data[i]]);
			valueParameterCircularGaugePrevious[i] = valueParameterCurrent;

			switch(i) {
				case 0:
					//updateFaceCircularGauge(i,linearLayoutFirstRowFirstGauge,paramsLinearLayoutFirstRowFirstGauge,
							//valueParameterCurrent);
					break;
				case 1:
					//updateFaceCircularGauge(i,linearLayoutFirstRowSecondGauge,paramsLinearLayoutFirstRowSecondGauge,
							//valueParameterCurrent);
					break;
				case 2:
					updateFaceCircularGauge(i,linearLayoutFirstRowThirdGauge,paramsLinearLayoutFirstRowThirdGauge,
							valueParameterCurrent);
					break;
				case 3:
					updateFaceCircularGauge(i,linearLayoutFirstRowFourthGauge,paramsLinearLayoutFirstRowFourthGauge,
							valueParameterCurrent);
					break;
				case 4:
					updateFaceCircularGauge(i,linearLayoutFirstRowFifthGauge,paramsLinearLayoutFirstRowFifthGauge,
							valueParameterCurrent);
					break;
				case 5:
					updateFaceCircularGauge(i,linearLayoutSecondRowFirstGauge,paramsLinearLayoutSecondRowFirstGauge,
							valueParameterCurrent);
					break;
				case 6:
					updateFaceCircularGauge(i,linearLayoutSecondRowSecondGauge,paramsLinearLayoutSecondRowSecondGauge,
							valueParameterCurrent);
					break;
				case 7:
					updateFaceCircularGauge(i,linearLayoutSecondRowThirdGauge,paramsLinearLayoutSecondRowThirdGauge,
							valueParameterCurrent);
					break;
				case 8:
					updateFaceCircularGauge(i,linearLayoutSecondRowFourthGauge,paramsLinearLayoutSecondRowFourthGauge,
							valueParameterCurrent);
					break;
				case 9:
					updateFaceCircularGauge(i,linearLayoutSecondRowFifthGauge,paramsLinearLayoutSecondRowFifthGauge,
							valueParameterCurrent);
					break;
				case 10:
					updateFaceCircularGauge(i,linearLayoutFirstSecondRowBigGauge,paramsLinearLayoutFirstSecondRowBigGauge,
							valueParameterCurrent);
					break;
			}
		}
	}

	public void simulatorCircularGaugeDemoRPM() {
		float valueParameterCurrent=ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES_RPM[ConstantsGlobals.counter_demo_data_rpm]/100;
		if(valueParameterCurrent==valueParameterCircularGaugePrevious[10]) {
			ConstantsGlobals.counter_demo_data_rpm++;
			if(ConstantsGlobals.counter_demo_data_rpm== ConstantsGlobals.NUMBER_DEMO_DATA_RPM)
				ConstantsGlobals.counter_demo_data_rpm = 0;
			return;
		}

		valueMinimum = 0;
		try 
		{
			valueMaximum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[10][7]);
		}
		catch (ParseException e) 
		{
			valueMaximum = 0;
		}
		valueRPMCurrent = ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES_RPM[ConstantsGlobals.counter_demo_data_rpm];
		if(valueRPMCurrent/100<100)
			textViewValueGauge[10].setText(zeroDecForm.format(valueRPMCurrent));
		else
			textViewValueGauge[10].setText(oneDecForm.format(valueRPMCurrent));
		positionDegrees = Math.round((ConstantsGlobals.DEMO_VALUES_CIRCULAR_GAUGES_RPM[ConstantsGlobals.counter_demo_data_rpm]/100 -
					valueMinimum) * (MAXIMUM_ANGLE_GAUGES[2] - (-90)) / (valueMaximum - valueMinimum) + (-90));
		linearLayoutNeedle[10].animate().rotation(positionDegrees).start();
		valueParameterCircularGaugePrevious[10] = valueParameterCurrent;

		updateFaceCircularGauge(10,linearLayoutFirstSecondRowBigGauge,paramsLinearLayoutFirstSecondRowBigGauge,
				valueParameterCurrent*100);

		ConstantsGlobals.counter_demo_data_rpm++;
		if(ConstantsGlobals.counter_demo_data_rpm==ConstantsGlobals.NUMBER_DEMO_DATA_RPM)
			ConstantsGlobals.counter_demo_data_rpm = 0;
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
					heightAnimRect = heightMainRect * (ConstantsGlobals.DEMO_VALUES_LINEAR_GAUGES[j][ConstantsGlobals.
							counterDemoDataLinearGauges[j]] - valueMinimum) / (valueMaximum - valueMinimum);
					yCeroMainRect = yMainRect + heightMainRect;
					yCeroRectAnim = yCeroMainRect - heightAnimRect;
				}
				else if(idAttr.getNodeValue().equals("animRect")) {
					yAttr = attr.getNamedItem("y");
					yAttr.setTextContent(Double.toString(yCeroRectAnim));
					attr.getNamedItem("height").setTextContent(Double.toString(heightAnimRect));
					if(ConstantsGlobals.DEMO_VALUES_LINEAR_GAUGES[j][ConstantsGlobals.counterDemoDataLinearGauges[j]]>=0)
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
						simulatorLinearGaugesDemo_2(linearLayoutThirdRowFirstGaugeGauge,svgImageView);
						break;
					case 1:
						simulatorLinearGaugesDemo_2(linearLayoutThirdRowSecondGaugeGauge,svgImageView);
						break;
					case 2:
						simulatorLinearGaugesDemo_2(linearLayoutThirdRowThirdGaugeGauge,svgImageView);
						break;
					case 3:
						simulatorLinearGaugesDemo_2(linearLayoutThirdRowFourthGaugeGauge,svgImageView);
						break;
					case 4:
						simulatorLinearGaugesDemo_2(linearLayoutThirdRowFifthGaugeGauge,svgImageView);
						break;
					case 5:
						simulatorLinearGaugesDemo_2(linearLayoutThirdRowSixthGaugeGauge,svgImageView);
						break;
				}
			} catch (SVGParseException e){
				e.printStackTrace();
			}
			ConstantsGlobals.counterDemoDataLinearGauges[j]++;
			if(ConstantsGlobals.counterDemoDataLinearGauges[j]== ConstantsGlobals.NUMBER_DEMO_DATA)
				ConstantsGlobals.counterDemoDataLinearGauges[j] = 0;
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

	public void simulatorFuelmanagementPanelDemo () {
		textViewSecondColumnThirdRowItem1Value.setText(Float.toString(ConstantsGlobals.DEMO_VALUES_FUEL_MANAGEMENT[0]
				[ConstantsGlobals.counterDemoDataFuelManagementPanel]));
		textViewSecondColumnThirdRowItem2Value.setText(Float.toString(ConstantsGlobals.DEMO_VALUES_FUEL_MANAGEMENT[1]
				[ConstantsGlobals.counterDemoDataFuelManagementPanel]));
		textViewSecondColumnThirdRowItem3Value.setText(Float.toString(ConstantsGlobals.DEMO_VALUES_FUEL_MANAGEMENT[2]
				[ConstantsGlobals.counterDemoDataFuelManagementPanel]));
		textViewSecondColumnThirdRowItem4Value.setText(Float.toString(ConstantsGlobals.DEMO_VALUES_FUEL_MANAGEMENT[3]
				[ConstantsGlobals.counterDemoDataFuelManagementPanel]));
		textViewSecondColumnThirdRowItem5Value.setText(Float.toString(ConstantsGlobals.DEMO_VALUES_FUEL_MANAGEMENT[4]
				[ConstantsGlobals.counterDemoDataFuelManagementPanel]));
		textViewSecondColumnThirdRowItem6Value.setText(Float.toString(ConstantsGlobals.DEMO_VALUES_FUEL_MANAGEMENT[5]
				[ConstantsGlobals.counterDemoDataFuelManagementPanel]));
		textViewSecondColumnThirdRowItem7Value.setText(Float.toString(ConstantsGlobals.DEMO_VALUES_FUEL_MANAGEMENT[6]
				[ConstantsGlobals.counterDemoDataFuelManagementPanel]));

		ConstantsGlobals.counterDemoDataFuelManagementPanel++;
		if(ConstantsGlobals.counterDemoDataFuelManagementPanel== ConstantsGlobals.NUMBER_DEMO_DATA)
			ConstantsGlobals.counterDemoDataFuelManagementPanel = 0;
	}
}

/*

	public void setInitialValues () {
		// CIRCULAR GAUGES
		float initialValuesCircularGauges[]={35,58,6.5f,197,32,650,155,170,123,185,26};
		int maximumAngleGauge=MAXIMUM_ANGLE_GAUGES[0];
		for(int i=0;i<NUMBER_CIRCULAR_GAUGES;i++) {
			valueMinimum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[i][0]);
			valueMaximum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[i][SEGMENTS_GAUGE[i]]);
			textViewValueGauge[i].setText(oneDecForm.format(initialValuesCircularGauges[i]));
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
			positionDegrees = Math.round((initialValuesCircularGauges[i] - valueMinimum) * (maximumAngleGauge - (-90)) /
					(valueMaximum - valueMinimum) + (-90));
			linearLayoutNeedle[i].animate().rotation(positionDegrees).start();
		}

		// LINEAR GAUGES
		float initialValuesLinearGauges[]={0.5f,0.6f,12,12.5f,15,25};
		NamedNodeMap attr;
		Node rect;
		Node idAttr,yAttr;
		double yMainRect=0,yCeroMainRect=0,yCeroRectAnim=0;
		double heightMainRect=0,heightAnimRect=0;
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
					heightAnimRect = heightMainRect * initialValuesLinearGauges[j] / (valueMaximum - valueMinimum);
					yCeroMainRect = yMainRect + heightMainRect;
					yCeroRectAnim = yCeroMainRect - heightAnimRect;
//System.out.println(j+":   "+initialValuesLinearGauges[j]+";   yMainRect: "+yMainRect+";   heightMainRect: "+heightMainRect+
//";   heightAnimRect: "+heightAnimRect+";   yCeroMainRect: "+yCeroMainRect+";   yCeroRectAnim: "+yCeroRectAnim);
				}
				else if(idAttr.getNodeValue().equals("animRect")) {
					yAttr = attr.getNamedItem("y");
					yAttr.setTextContent(Double.toString(yCeroRectAnim));
					attr.getNamedItem("height").setTextContent(Double.toString(heightAnimRect));
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
						setInitialValues2(linearLayoutThirdRowFirstGaugeGauge,svgImageView);
						break;
					case 1:
						setInitialValues2(linearLayoutThirdRowSecondGaugeGauge,svgImageView);
						break;
					case 2:
						setInitialValues2(linearLayoutThirdRowThirdGaugeGauge,svgImageView);
						break;
					case 3:
						setInitialValues2(linearLayoutThirdRowFourthGaugeGauge,svgImageView);
						break;
					case 4:
						setInitialValues2(linearLayoutThirdRowFifthGaugeGauge,svgImageView);
						break;
					case 5:
						setInitialValues2(linearLayoutThirdRowSixthGaugeGauge,svgImageView);
						break;
				}
			} catch (SVGParseException e){
				e.printStackTrace();
			}
//System.out.println("docLinearGauge[i].getElementsByTagName(rect).getLength(): "+docLinearGauge[j].getElementsByTagName("rect").getLength());
		}

		textViewSecondColumnThirdRowItem1Value.setText("87.2");
		textViewSecondColumnThirdRowItem2Value.setText("200");
		textViewSecondColumnThirdRowItem3Value.setText("5.2");
		textViewSecondColumnThirdRowItem4Value.setText("17.2");
		textViewSecondColumnThirdRowItem5Value.setText("7.2");
		textViewSecondColumnThirdRowItem6Value.setText("734");
		textViewSecondColumnThirdRowItem7Value.setText("95.6");
	}
	public void setInitialValues2 (LinearLayout linearLayoutThirdRowGaugeGauge,SVGImageView svgImageView) {		// Only for Linear Layouts
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

	public void simulator () {

		for(int i=0;i<NUMBER_CIRCULAR_GAUGES;i++) {
if(i==9)
	continue;
		valueMinimum = 0;

			switch(SEGMENTS_GAUGE[i]) {
				case 5:
					if(i==5)
						continue;
					try 
					{
						valueMaximum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[i][5]);
					}
					catch (ParseException e) 
					{
						valueMaximum = 0;
					}
//(x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
					if(actualValueCircularGauge[i]>valueMaximum)
						actualValueCircularGauge[i] = 0;
					if(actualValueCircularGauge[i]<100)
						textViewValueGauge[i].setText(zeroDecForm.format(actualValueCircularGauge[i]));
					else
						textViewValueGauge[i].setText(oneDecForm.format(actualValueCircularGauge[i]));
					positionDegrees = Math.round((actualValueCircularGauge[i] - valueMinimum) * (MAXIMUM_ANGLE_GAUGES[0] - (-90)) /
							(valueMaximum - valueMinimum) + (-90));
					linearLayoutNeedle[i].animate().rotation(positionDegrees).start();
//if(i==1)
	//System.out.println(MAXIMUM_ANGLE_GAUGES[0]+"   "+valueMinimum+"   "+valueMaximum+"   "+actualValue[i]+"   "+positionDegrees);
					actualValueCircularGauge[i] += 3.0f;
					break;
				case 6:
					try 
					{
						valueMaximum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[i][6]);
					}
					catch (ParseException e) 
					{
						valueMaximum = 0;
					}
					if(actualValueCircularGauge[i]<valueMinimum)
						actualValueCircularGauge[i] = valueMaximum;
					if(actualValueCircularGauge[i]<100)
						textViewValueGauge[i].setText(zeroDecForm.format(actualValueCircularGauge[i]));
					else
						textViewValueGauge[i].setText(oneDecForm.format(actualValueCircularGauge[i]));
					positionDegrees = Math.round((actualValueCircularGauge[i] - valueMinimum) * (MAXIMUM_ANGLE_GAUGES[1] - (-90)) /
							(valueMaximum - valueMinimum) + (-90));
					linearLayoutNeedle[i].animate().rotation(positionDegrees).start();
					actualValueCircularGauge[i] -= 10f;
					break;
				case 7:
					if(i!=0)
						continue;
					try 
					{
						valueMaximum = Float.parseFloat(SCALES_CIRCULAR_GAUGES_SAE[i][7]);
					}
					catch (ParseException e) 
					{
						valueMaximum = 0;
					}
					if(increment==2) {
						if(actualValueCircularGauge[i]>valueMaximum) {
							actualValueCircularGauge[i] = valueMaximum;
							increment = -2;
						}
					}
					else {
						if(actualValueCircularGauge[i]<0) {
							actualValueCircularGauge[i] = valueMinimum;
							increment = 2;
						}
					}
					if(actualValueCircularGauge[i]<100)
						textViewValueGauge[i].setText(zeroDecForm.format(actualValueCircularGauge[i]));
					else
						textViewValueGauge[i].setText(oneDecForm.format(actualValueCircularGauge[i]));
					positionDegrees = Math.round((actualValueCircularGauge[i] - valueMinimum) * (MAXIMUM_ANGLE_GAUGES[2] - (-90)) /
							(valueMaximum - valueMinimum) + (-90));
					linearLayoutNeedle[i].animate().rotation(positionDegrees).start();
//if(i==0)
//System.out.println(MAXIMUM_ANGLE_GAUGES[0]+"   "+valueMinimum+"   "+valueMaximum+"   "+actualValue[i]+"   "+positionDegrees);
					actualValueCircularGauge[i] += increment;
					break;
			}
		}
	}
*/