package com.modelviewer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.modelviewer.xyz.XYZModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import c0m.WildCom.Project.R;
import geometrypack.Vector;
import utilities.Utilities;


@SuppressLint("NewApi")
public class MainActivity extends Activity {

	private String name="output";
    private ModelViewerApplication app;
    @Nullable private ModelSurfaceView modelView;
    private ViewGroup containerView;
  // private static List<String> SAMPLES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = ModelViewerApplication.getInstance();
        containerView = (ViewGroup) findViewById(R.id.container_view);
        final TextView W = (TextView)findViewById(R.id.WeightTxtView);
        final EditText ed = (EditText)findViewById(R.id.search);
        Button LoadBtn = (Button)findViewById(R.id.load);
        Button ComputeBtn = (Button)findViewById(R.id.compute);


       Intent i = getIntent();
       name = i.getStringExtra("name");
        LoadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
			    name = ed.getText().toString();
				loadSampleModel();
			}
		});

        ComputeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				List<Vector> cloud = new ArrayList<Vector>();
				File file = new File(Environment.getExternalStorageDirectory().getPath(),name+".xyz");
				InputStream stream = null;
				try {
					stream = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					cloud = Utilities.ReadXYZ(stream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				float weight;



				weight = Utilities.estimateWeight(cloud);



				W.setText("W1: "+String.valueOf(weight)+"kg");
			}
		});

    }

    @Override
    protected void onStart() {
        super.onStart();

        createNewModelView(app.getCurrentModel());
        if (app.getCurrentModel() != null) {
            setTitle(app.getCurrentModel().getTitle());
        }
    }

    @SuppressLint("NewApi")
	@Override
    protected void onPause() {
        super.onPause();
        if (modelView != null) {
            modelView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (modelView != null) {
            modelView.onResume();
        }
    }

    private void createNewModelView(@Nullable Model model) {
        if (modelView != null) {
            containerView.removeView(modelView);
        }
        ModelViewerApplication.getInstance().setCurrentModel(model);
        modelView = new ModelSurfaceView(this, model);
        containerView.addView(modelView, 0);
    }

    private void setCurrentModel(@NonNull Model model) {
        createNewModelView(model);
        setTitle(model.getTitle());
    }

    private void loadSampleModel() {
        try {
        	File f = new File (Environment.getExternalStorageDirectory().getPath(),"0011_Clean.xyz");

        	InputStream stream = new FileInputStream(f);
            setCurrentModel(new XYZModel(stream));
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 }
