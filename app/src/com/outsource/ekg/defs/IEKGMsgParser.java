package com.outsource.ekg.defs;

import java.io.InputStreamReader;

public interface IEKGMsgParser {
	public IEKGMsgSegment getSegment();

	public boolean setDataSource(IDataConnection connection);

	public boolean setDataSource(InputStreamReader reader);

	public boolean start();
	
	public boolean stop();
	
	// re-usable object
	public void recycle();

	boolean setOnDataCaptureListener(IEKGMsgParser.OnDataCaptureListener listener);
	
	public static interface OnDataCaptureListener {
		// simplest notify method
		public void onWaveFormDataCaptured(byte[] bytes, int format);
		
		// more complicated notify method
		public void onNewSegmentFound(IEKGMsgSegment segment);
	}
}
