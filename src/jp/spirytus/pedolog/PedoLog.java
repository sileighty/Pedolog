package jp.spirytus.pedolog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jp.spirytus.pedolog.dao.DatabaseHelper;
import jp.spirytus.pedolog.dao.Pedolog;
import jp.spirytus.pedolog.dao.PedologDao;

import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PedoLog extends Activity implements
	SensorListener {
	
	private static final float FILTERING_VALUE = 0.1f;
	
	private static final float SCALE = 0.2f;
	
	private static final String START_VALUE = "00000";
	
	private float lowX;
	private float lowY;
	private float lowZ;
	
	private SensorManagerSimulator mSensorManager;
	
//	protected TextView tv1;
//	protected TextView tv2;
//	protected TextView tv3;
	
	
	protected Integer counter = 0;
	
	protected PedologDao dao;
	
	protected List<TextView> logFieldList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

//    	LinearLayout linearLayout = new LinearLayout(this);
//    	linearLayout.setOrientation(LinearLayout.VERTICAL);
//    	
//    	tv1 = new TextView(this);
//    	tv2 = new TextView(this);
//    	tv3 = new TextView(this);
//    	linearLayout.addView(tv1);
//    	linearLayout.addView(tv2);
//    	linearLayout.addView(tv3);
    	
    	setContentView(R.layout.main);

    	Button registBtn = (Button) findViewById(R.id.Button02);
    	
    	registBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// 確認ダイアログの表示
				AlertDialog.Builder builder = new AlertDialog.Builder(PedoLog.this);
				// アイコン設定
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				
				// タイトル設定
				builder.setTitle("お疲れ様でした。");
				builder.setMessage("登録します。よろしいですか？");
				// OKボタン設定
				builder.setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	regist();
				    	reset();
				    	load();
				    }
				});
				// キャンセルボタン設定
				builder.setNegativeButton( android.R.string.cancel, null);
				// ダイアログの表示
				builder.show();
			}
    	});
    	
    	
    	Button resetBtn = (Button) findViewById(R.id.Button01);
    	
    	resetBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				// 確認ダイアログの表示
				AlertDialog.Builder builder = new AlertDialog.Builder(PedoLog.this);
				// アイコン設定
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				
				// タイトル設定
				builder.setTitle("リセット");
				builder.setMessage("リセットします。よろしいですか？");
				// OKボタン設定
				builder.setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	reset();
				    }
				});
				// キャンセルボタン設定
				builder.setNegativeButton( android.R.string.cancel, null);
				// ダイアログの表示
				builder.show();
			}
    	});
    	
        ////////////////////////////////////////////////////////////////
        // INSTRUCTIONS
        // ============

        // 1) Use the separate application SensorSimulatorSettings
        //    to enter the correct IP address of the SensorSimulator.
        //    This should work before you proceed, because the same
        //    settings are used for your custom sensor application.

        // 2) Include sensorsimulator-lib.jar in your project.
        //    Put that file into the 'lib' folder.
        //    In Eclipse, right-click on your project in the 
        //    Package Explorer, select
        //    Properties > Java Build Path > (tab) Libraries
        //    then click Add JARs to add this jar.

        // 3) You need the permission
        //    <uses-permission android:name="android.permission.INTERNET"/>
        //    in your Manifest file!

        // 4) Instead of calling the system service to obtain the Sensor manager,
        //    you should obtain it from the SensorManagerSimulator:

        //mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);

        // 5) Connect to the sensor simulator, using the settings
        //    that have been set previously with SensorSimulatorSettings
        mSensorManager.connectSimulator();

        // The rest of your application can stay unmodified.
        //////////////////////////////////////////////////////////////// 

        dao = new PedologDao(new DatabaseHelper(this).getWritableDatabase());


        logFieldList = Arrays.asList(new TextView[]{
        		(TextView) findViewById(R.id.TextView03),
    			(TextView) findViewById(R.id.TextView04),
    			(TextView) findViewById(R.id.TextView05),
    			(TextView) findViewById(R.id.TextView06),
    			(TextView) findViewById(R.id.TextView07),
    			}
        );
        
        
        load();
    }
    

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER
                | SensorManager.SENSOR_MAGNETIC_FIELD
                | SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }





	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void onSensorChanged(int sensor, float[] values) {

		switch(sensor) {
		
		case SensorManager.SENSOR_ACCELEROMETER:
		
			float x = values[SensorManager.DATA_X];
			float y = values[SensorManager.DATA_Y];
			float z = values[SensorManager.DATA_Z];

			lowX = x * FILTERING_VALUE + lowX * (1.0f - FILTERING_VALUE);
			lowY = y * FILTERING_VALUE + lowY * (1.0f - FILTERING_VALUE);
			lowZ = z * FILTERING_VALUE + lowZ * (1.0f - FILTERING_VALUE);
			
//			float highX = Math.abs(x - lowX);
//			float highY = Math.abs(y - lowY);
//			float highZ = Math.abs(z - lowZ);
			
			float highX = x - lowX;
			float highY = y - lowY;
			float highZ = z - lowZ;

			TextView tv = (TextView) findViewById(R.id.TextView02);
			DecimalFormat df = new DecimalFormat(START_VALUE);
			
			if(highX > SCALE) {
				tv.setText(df.format(counter++));
			}else if(highY > SCALE) {
				tv.setText(df.format(counter++));
			}else if(highZ > SCALE){
				tv.setText(df.format(counter++));
				
			}
			
			meter();
		}
	}
	
	protected void meter() {
		
		
    	ImageView iv = (ImageView) findViewById(R.id.ImageView01);
    	
    	
    	if(counter > 100) {
    		iv.setImageResource(R.drawable.meter_5);
    	}else if(counter > 80){
    		iv.setImageResource(R.drawable.meter_4);
    	}else if(counter > 60) {
    		iv.setImageResource(R.drawable.meter_3);
    	}else if(counter > 40) {
    		iv.setImageResource(R.drawable.meter_2);
    	}else if(counter > 20) {
    		iv.setImageResource(R.drawable.meter_1);
    	}else{
    		iv.setImageResource(R.drawable.meter_0);
    	}
	}
    
	protected void reset() {
		counter = 0;
		DecimalFormat df = new DecimalFormat("00000");
		TextView tv = (TextView)findViewById(R.id.TextView02);
		tv.setText(START_VALUE);
		meter();
	}
	
	protected void regist() {
		Pedolog pedolog = new Pedolog();
		pedolog.setDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		pedolog.setCount(counter);
		
		dao.insert(pedolog);
	}
	
	protected void load() {

		List<Pedolog> pedologList = dao.findAll();

		for(int i=0;  i<pedologList.size() && i<logFieldList.size(); i++) {
			
			Pedolog pedolog = pedologList.get(i);
			
			logFieldList.get(i).setText(pedolog.getDate() + " " + new DecimalFormat("00000").format(pedolog.getCount()) + "歩");
			

		}
	}
}