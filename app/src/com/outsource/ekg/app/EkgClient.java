package com.outsource.ekg.app;

import java.io.InputStreamReader;

import com.outsource.ekg.R;
import com.outsource.ekg.defs.IDataConnection;
import com.outsource.ekg.defs.IEKGMsgParser;
import com.outsource.ekg.defs.IEKGMsgSegment;
import com.outsource.ekg.ui.WaveformView;
import com.outsource.ekg.ui.WaveformView.Label;

import android.app.Activity;
import android.os.Bundle;

public class EkgClient extends Activity implements IEKGMsgParser {
	private OnDataCaptureListener listener = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ekg);
		WaveformView waveform = (WaveformView)findViewById(R.id.waveform);
		setOnDataCaptureListener(waveform);
		listener.onWaveFormDataCaptured(null, 1);
	}

	@Override
	public IEKGMsgSegment getSegment() {
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