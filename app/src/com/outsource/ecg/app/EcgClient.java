package com.outsource.ecg.app;

import java.io.InputStreamReader;
import com.outsource.ecg.defs.IDataConnection;
import com.outsource.ecg.defs.IECGMsgParser;
import com.outsource.ecg.defs.IECGMsgSegment;
import com.outsource.ecg.ui.LineRenderer;
import com.outsource.ecg.ui.WaveformView;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import com.outsource.ecg.R;

public class EcgClient extends Activity implements IECGMsgParser {
	private OnDataCaptureListener listener = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ekg);
		WaveformView waveform = (WaveformView)findViewById(R.id.waveform);
		setOnDataCaptureListener(waveform);
		Paint testPaint = new Paint();
		testPaint.setColor(Color.BLUE);
		testPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		testPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		waveform.setRenderer(new LineRenderer(testPaint));
		float[] testData = new float[] { 1.0f, 2.0f, 3.0f, 4.0f, 4.1f, 4.2f, 4.3f, 4.4f };
		listener.onWaveFormDataCaptured(testData, 1);
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