
package com.modelviewer;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.OpenNI;
import org.openni.PixelFormat;
import org.openni.SensorType;
import org.openni.VideoFrameRef;
import org.openni.VideoMode;
import org.openni.VideoStream;
import org.openni.android.OpenNIHelper;
import org.openni.android.OpenNIView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import c0m.WildCom.Project.R;
import geometrypack.Vector;
import patterns.cluster.DoubleArray;
import smartscaledatabase.PigTable;
import smartscaledatabase.PigViewModel;
import utilities.Utilities;


public class StartActivity extends AppCompatActivity implements OpenNIHelper.DeviceOpenListener {

	private static final String TAG = "Viewer";
	private OpenNIHelper mOpenNIHelper;
	private boolean mDeviceOpenPending = false;
	private Thread mMainLoopThread;
	private boolean mShouldRun = true;
	private Device mDevice;
	private VideoStream mStream;
	private VideoStream mSecondStream;
	private OpenNIView mFrameView;
	private TextView mStatusLine;
	private TextView weightPreview;
	private int flag=0;
	private int count;
	private int priority;
	private int pigNumber;
	private String pigNum;
	private List<PigTable> pigTables = new ArrayList<>();
	private SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:SS", Locale.getDefault());
	private String currentDateAndTime = formatter.format(new Date());
	private PigTable pigTable = new PigTable(0, 0, 0, "");
	double f = 0.0;
	private PigViewModel pigViewModel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mOpenNIHelper = new OpenNIHelper(this);
		OpenNI.setLogAndroidOutput(true);
		OpenNI.setLogMinSeverity(0);
		OpenNI.initialize();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		mFrameView =  findViewById(R.id.frameView);
		mStatusLine =  findViewById(R.id.status_line);
		weightPreview = findViewById(R.id.textView_weight_preview);
		Button scan = findViewById(R.id.scanButton);
		Button displayBtn = findViewById(R.id.visualizeButton);

		pigViewModel = ViewModelProviders.of(StartActivity.this).get(PigViewModel.class);
		pigViewModel.getItemCuount().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(@Nullable Integer integer) {
				count = integer;
				Log.d(TAG, "On change count " + count);
			}
		});

		scan.setOnClickListener((View view) -> setFlag());

		displayBtn.setOnClickListener((View v) -> startSecondActivity());
		}

		private void setFlag(){
		flag = 1;
		priority = count;
			Log.d(TAG, "On Scan Clicked Priority Value " + priority);
			pigNumber = priority;
		}


	private void startSecondActivity(){
		Intent data = new Intent(StartActivity.this, SecondActivity.class);
		startActivity(data);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mOpenNIHelper.shutdown();
		OpenNI.shutdown();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");

		super.onResume();

		if (mDeviceOpenPending) {
			return;
		}
		String uri;
		
		List<DeviceInfo> devices = OpenNI.enumerateDevices();
		if (devices.isEmpty()) {
			showAlertAndExit("No OpenNI-compliant device found.");
			return;
		}
		
		uri = devices.get(0).getUri();

		mDeviceOpenPending = true;
		mOpenNIHelper.requestDeviceOpen(uri, this);
	}

	private void showAlertAndExit(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.show();
	}
 
	private void device_initialization(Device aDevice) {

		VideoMode vm = new VideoMode();
		vm.setResolution(320/2,240/2);
	    vm.setFps( 30 );
	    vm.setPixelFormat(PixelFormat.DEPTH_1_MM);
		mDeviceOpenPending = false;
		try {
			
			mDevice = aDevice;
			mStream = VideoStream.create(mDevice, SensorType.DEPTH);
			
			mStream.setVideoMode(vm);
			mStream.start();
			
		} catch (RuntimeException e) {
			showAlertAndExit("Failed to open stream: " + e.getMessage());

		}
	}
	
	@Override 
	public void onDeviceOpened(Device aDevice) {

		device_initialization(aDevice);
		mShouldRun = true;
		mMainLoopThread = new Thread() {
			
			@Override
			public void run() {
				List<Vector> Cloud= new ArrayList<Vector>();
				List<DoubleArray> clean = new ArrayList<>();
				double pigWeight;
				String weightValue;
				int count = pigTables.size();


				while (mShouldRun) {
					VideoFrameRef frame = null;
					try {
						frame = mStream.readFrame();
						mFrameView.setBaseColor(Color.GRAY);
						mFrameView.update(frame);
						mStream.setMirroringEnabled(false);
						if (flag==1) {
							Cloud = Utilities.frameTocloud(frame, mStream);
							Cloud = Utilities.sample(0.7, Cloud);
							clean = Utilities.outliersRemoval_opt(Cloud);
							clean = Utilities.align_opt(clean);
							Cloud = Utilities.cloudArrayToVector(clean);
							pigWeight = Utilities.estimateWeight(Cloud);
				            f = frame.getTimestamp() / 1e6;
				            weightValue = new DecimalFormat("##.#").format(pigWeight);
							updateLabel(String.format("Frame Index: %,d | Timestamp: %.6f seconds", frame.getFrameIndex(), frame.getTimestamp() / 1e6), (weightValue + " KG"));
							pigWeight = Math.round(pigWeight);
							pigViewModel.insert(new PigTable(pigNumber, pigWeight, priority, currentDateAndTime));
							flag = 0;
							}
						} catch (Exception e) {
						Log.e(TAG, "Failed reading frame: " + e);
					}
				}
			}
		};
		mMainLoopThread.start();
	}
	
	private void stop() {
		mShouldRun = false;
		
		while (mMainLoopThread != null) {
			try {
				mMainLoopThread.join();
				mMainLoopThread = null;
				break;
			} catch (InterruptedException e) {
			}
		}

		if (mStream != null) {
			mStream.stop();
		}
		
		if (mSecondStream != null) {
			mSecondStream.stop();
		}
		
		mStatusLine.setText(R.string.waiting_for_frames);
	}
	
	private void updateLabel(final String message, final String weightValue) {
		runOnUiThread(new Runnable() {
			public void run() {
				mStatusLine.setText(message);
				weightPreview.setText(weightValue);
			}
		});
	}


	@Override
	public void onDeviceOpenFailed(String uri) {
		Log.e(TAG, "Failed to open device " + uri);
		mDeviceOpenPending = false;
		showAlertAndExit("Failed to open device");
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");

		super.onPause();


		if (mDeviceOpenPending)
			return;

		stop();
		
		if (mStream != null) {
			mStream.destroy();
			mStream = null;
		}
		
		if (mSecondStream != null) {
			mSecondStream.destroy();
			mSecondStream = null;
		}
		
		if (mDevice != null) {
			mDevice.close();
			mDevice = null;
		}
	}

}
