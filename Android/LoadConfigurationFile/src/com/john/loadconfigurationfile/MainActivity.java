/*
  NOTES FOR EMULATOR

  In DDMS File Esplorer:
    Internal sdcard path : DDMS->File Explorer->mnt->sdcard
    External sdcard path : DDMS->File Explorer->storage->sdcard

  1) Create folder

     - Change to D:\AndroidSDK\platform-tools | adb shell | su |  mount -o rw,remount rootfs / | 	 |
       exit
     - Change DDMS perspective | Storage | SD Card | Create folder
     - Delete folder, so it can created with the App
 */

package com.john.loadconfigurationfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends Activity {

	public static File root,dewLineFolder,settingsFilesFolder;
	public static Spinner spinnerFilesConfiguration;
	public static SQLiteDatabase dewLineDB;
	public static Cursor cursor = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Check for Settings folder
		root = Environment.getExternalStorageDirectory();
		dewLineFolder = new File(root.getAbsolutePath()+"/DEWLine/");
		if(!dewLineFolder.exists()) {
			if(dewLineFolder.mkdir())
				Toast.makeText(getApplicationContext(),"DEWWLine folder created",Toast.LENGTH_LONG).show();
			settingsFilesFolder = new File(dewLineFolder.getAbsolutePath()+"/SettingsFilesFolder/");
			if(settingsFilesFolder.mkdir())
				Toast.makeText(getApplicationContext(),"Settings Files folder created",Toast.LENGTH_LONG).show();
		}

		// FILL SPINNER
		spinnerFilesConfiguration = (Spinner) findViewById(R.id.spinnerFilesConfiguration);

		List<String> temp = new ArrayList<String>();
		settingsFilesFolder = new File(dewLineFolder.getAbsolutePath()+"/SettingsFilesFolder/");
		if(dewLineFolder.exists()) {
			File[] files = settingsFilesFolder.listFiles();
System.out.println("Files number: "+ files.length);
			for (int i=0;i<files.length;i++)
				temp.add(files[i].getName());
		}
		String[] filesConfiguration = new String[temp.size()];
		temp.toArray(filesConfiguration);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.spinner_item,filesConfiguration);
		arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinnerFilesConfiguration.setAdapter(arrayAdapter);

		createDB();

		// LISTENERS
		Button buttonLoadSettings = (Button) findViewById(R.id.buttonLoadSettings);
		buttonLoadSettings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String itemSelected = spinnerFilesConfiguration.getSelectedItem().toString();
				String commandSQL;

				dewLineDB = openOrCreateDatabase(dewLineFolder+"/DEWLine.db", MODE_PRIVATE, null);

				Workbook workbook = null;
				try {
					workbook = Workbook.getWorkbook(new File(settingsFilesFolder+"/"+itemSelected));
					Sheet sheet = workbook.getSheet(0);
					// Save in BD
					Cell cell;
					for(int i=5;i<26;i++) {
						cell = sheet.getCell(1, i);
						commandSQL = "SELECT * FROM AlarmParameters WHERE Parameter_AlarmParameters='"+cell.getContents()+"'";
						cursor = dewLineDB.rawQuery(commandSQL,null);
						ContentValues values = new ContentValues();
						if(cursor.getCount()==0||cursor.getCount()==1) {
							values.put("Engine_AlarmParameters", spinnerFilesConfiguration.getSelectedItem().toString());
							values.put("Parameter_AlarmParameters", sheet.getCell(1, i).getContents().trim());
							values.put("Units_AlarmParameters", sheet.getCell(2, i).getContents().trim());
							values.put("Dynamic_AlarmParameters", sheet.getCell(3, i).getContents().trim());
							values.put("Shutdn_LowThresholds_AtIdle_AlarmParameters", sheet.getCell(4, i).getContents().trim());
							values.put("Red_LowThresholds_AtIdle_AlarmParameters", sheet.getCell(5, i).getContents().trim());
							values.put("Yellow_LowThresholds_AtIdle_AlarmParameters", sheet.getCell(6, i).getContents().trim());
							values.put("Yellow_HighThresholds_AtIdle_AlarmParameters", sheet.getCell(7, i).getContents().trim());
							values.put("Red_HighThresholds_AtIdle_AlarmParameters", sheet.getCell(8, i).getContents().trim());
							values.put("Shutdn_HighThresholds_AtIdle_AlarmParameters", sheet.getCell(9, i).getContents().trim());
							values.put("Shutdn_LowThresholds_AtWot_AlarmParameters", sheet.getCell(11, i).getContents().trim());
							values.put("Red_LowThresholds_AtWot_AlarmParameters", sheet.getCell(12, i).getContents().trim());
							values.put("Yellow_LowThresholds_AtWot_AlarmParameters", sheet.getCell(13, i).getContents().trim());
							values.put("Yellow_HighThresholds_AtWot_AlarmParameters", sheet.getCell(14, i).getContents().trim());
							values.put("Red_HighThresholds_AtWot_AlarmParameters", sheet.getCell(15, i).getContents().trim());
							values.put("Shutdn_HighThresholds_AtWot_AlarmParameters", sheet.getCell(16, i).getContents().trim());
							values.put("Delay_Alarm_AlarmParameters", sheet.getCell(18, i).getContents().trim());
							values.put("Delay_Shutdown_AlarmParameters", sheet.getCell(19, i).getContents().trim());
						}
//System.out.println(cell.getContents());
						if(cursor.getCount()==0)
							dewLineDB.insert("AlarmParameters", null, values);
						else if(cursor.getCount()==1)
							dewLineDB.update("AlarmParameters",values,"Parameter_AlarmParameters=?",
									new String[] {cell.getContents()});   
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (BiffException e) {
					e.printStackTrace();
				} finally {

					if (workbook != null) {
						workbook.close();
					}

				}
				dewLineDB.close();
			}
	});
	}
	public void createDB() {

		File fileDB = new File(dewLineFolder+"/DEWLine.db");
		if(fileDB.exists()) {
			return;
		}

//ystem.out.println(dewLineFolder+"/DEWLine.db");
		dewLineDB = openOrCreateDatabase(dewLineFolder+"/DEWLine.db", MODE_PRIVATE, null);

		dewLineDB.execSQL("CREATE TABLE IF NOT EXISTS AlarmParameters(" +
				"Engine_AlarmParameters TEXT(25), "+
				"Parameter_AlarmParameters TEXT(25), "+
				"Units_AlarmParameters TEXT(10), "+
				"Dynamic_AlarmParameters TEXT, "+
				"Shutdn_LowThresholds_AtIdle_AlarmParameters TEXT(10), "+
				"Red_LowThresholds_AtIdle_AlarmParameters TEXT(10), "+
				"Yellow_LowThresholds_AtIdle_AlarmParameters TEXT(10), "+
				"Yellow_HighThresholds_AtIdle_AlarmParameters TEXT(10), "+
				"Red_HighThresholds_AtIdle_AlarmParameters TEXT(10), "+
				"Shutdn_HighThresholds_AtIdle_AlarmParameters TEXT(10), "+
				"Shutdn_LowThresholds_AtWot_AlarmParameters TEXT(10), "+
				"Red_LowThresholds_AtWot_AlarmParameters TEXT(10), "+
				"Yellow_LowThresholds_AtWot_AlarmParameters TEXT(10), "+
				"Yellow_HighThresholds_AtWot_AlarmParameters TEXT(10), "+
				"Red_HighThresholds_AtWot_AlarmParameters TEXT(10), "+
				"Shutdn_HighThresholds_AtWot_AlarmParameters TEXT(10), "+
				"Delay_Alarm_AlarmParameters TEXT(2), "+
				"Delay_Shutdown_AlarmParameters TEXT(2)"+
		");");

		dewLineDB.close();
	}
}
