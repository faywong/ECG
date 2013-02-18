package com.outsource.ecg.app;

import java.io.InputStreamReader;

import com.outsource.ecg.ui.JDBCXYChartView;
import com.outsource.ecg.ui.XYPlotView;
import com.outsource.ecg.defs.IDataConnection;
import com.outsource.ecg.defs.IECGMsgParser;
import com.outsource.ecg.defs.IECGMsgSegment;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.outsource.ecg.R;

public class EcgClient extends Activity implements IECGMsgParser {
	private static final String TAG = "EcgClient";
	private static final boolean DEBUG = true;
    private static String DB_FILE_NAME = "main.sqlite";
    private String dbFilePath;
	private Object listener;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbFilePath = this.getApplicationInfo().dataDir + "/" + DB_FILE_NAME;
        Log.d(TAG, "application data dir:" + this.getApplicationInfo().dataDir);
        //JDBCXYChartView contentView = new JDBCXYChartView(this, dbFilePath);
        XYPlotView contentView = new XYPlotView(this);
        setContentView(contentView);
	}

	@Override
	public IECGMsgSegment getSegment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setDataSource(IDataConnection connection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setDataSource(InputStreamReader reader) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void recycle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean setOnDataCaptureListener(OnDataCaptureListener listener) {
		// TODO Auto-generated method stub
		if (this.listener == null && listener != null) {
			this.listener = listener;
			return true;
		} else {
			return false;
		}
	}
}