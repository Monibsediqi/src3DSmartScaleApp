
package com.modelviewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import c0m.WildCom.Project.R;
import geometrypack.Vector;
import patterns.cluster.DoubleArray;
import utilities.Utilities;

/* TODO - 1
1- Change the MainActivity (Second Activity) and design it
2- Run the app wit the design only
3- understand about android room
*/
public class StartActivity extends Activity implements OpenNIHelper.DeviceOpenListener {

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
	private int flag=0;
	private String name = "output";
	double f = 0.0;
	TextView exec_time;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mOpenNIHelper = new OpenNIHelper(this);
		OpenNI.setLogAndroidOutput(true);
		OpenNI.setLogMinSeverity(0);
		OpenNI.initialize();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_viewer);
		mFrameView = (OpenNIView) findViewById(R.id.frameView);
		mStatusLine = (TextView) findViewById(R.id.status_line);
		Button scan = (Button) findViewById(R.id.button1);
		final EditText mEdit = (EditText) findViewById(R.id.editText1);
		final Button displayBtn = (Button)findViewById(R.id.display);
		exec_time = (TextView) findViewById(R.id.Execution_time);

		scan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				flag = 1;
				if (mEdit.getText().toString()!=null)
					name = mEdit.getText().toString();
				else
					name = "output";
			}
			});


		}
		public void visualizePigInfo(View view){
		Intent secondActivity = new Intent(this, MainActivity.class);
		secondActivity.putExtra("name" , name);
		startActivity(secondActivity);
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

	@SuppressLint("NewApi")
	public void visualize_pointCloud(View view) throws IOException {


		Intent intent = new Intent(view.getContext(),MainActivity.class);
		intent.putExtra("name", name);

		startActivity(intent);
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
		vm.setResolution(320*4/2,240*4/2);
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
			return;
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
				while (mShouldRun) {
					VideoFrameRef frame = null;
					double ff = 0;
					DecimalFormat df = new DecimalFormat("0.00");
					try {
						frame = mStream.readFrame();
						// Request rendering of the current OpenNI frame
						mFrameView.setBaseColor(Color.GRAY);
						mFrameView.update(frame);
						mStream.setMirroringEnabled(false);
						if (flag==1) {
							
//							File file = new File(Environment.getExternalStorageDirectory().getPath(),"21.xyz");
//							InputStream stream = new FileInputStream(file);
//							Cloud = Utilities.ReadXYZ(stream);
							Cloud = Utilities.frameTocloud(frame, mStream);
							Cloud = Utilities.sample(0.7, Cloud);
							clean = Utilities.outliersRemoval_opt(Cloud);
							clean = Utilities.align_opt(clean);
//							Utilities.SaveXYZ(clean,Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"21_clean");
							Utilities.SaveXYZ(clean,Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+name);
//This is for test							
				            f = frame.getTimestamp() / 1e6;
							flag=-1;
							}
						if (flag==-1) {
							 ff = (double) (frame.getTimestamp() / 1e6);
//							 exec_time.setText(df.format(ff-f)+" seconds");
							 exec_time.setText(" seconds");
							 flag = 0;
							 }			
						updateLabel(String.format("Frame Index: %,d | Timestamp: %.6f seconds", frame.getFrameIndex(), frame.getTimestamp() / 1e6));
					} catch (Exception e) {
						Log.e(TAG, "Failed reading frame: " + e);
					}
				
				}
			};
			
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
	
	private void updateLabel(final String message) {
		runOnUiThread(new Runnable() {
			public void run() {
				mStatusLine.setText(message);								
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
