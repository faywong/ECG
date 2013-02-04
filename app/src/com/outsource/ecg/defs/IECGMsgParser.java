package com.outsource.ecg.defs;

import java.io.InputStreamReader;

public interface IECGMsgParser {
	public IECGMsgSegment getSegment();

	public boolean setDataSource(IDataConnection connection);

	public boolean setDataSource(InputStreamReader reader);

	public boolean start();
	
	public boolean stop();
	
	// re-usable object
	public void recycle();

	boolean setOnDataCaptureListener(IECGMsgParser.OnDataCaptureListener listener);
	
	public static interface OnDataCaptureListener {
		/* Waveform data format type enumerate */
		public static final int FORMAT_INVALID = -1;
		public static final int FORMAT_RAW = 0;
		public static final int FORMAT_FFT = 1;
		// simplest notify method
		public void onWaveFormDataCaptured(float[] bytes, int format);
		
		// more complicated notify method
		public void onNewSegmentFound(IECGMsgSegment segment);
	}
}
