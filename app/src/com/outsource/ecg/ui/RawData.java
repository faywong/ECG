package com.outsource.ecg.ui;

// Data class to explicitly indicate that these bytes are raw data
public class RawData {
	public float[] data = null;

	public RawData(float[] bytes) {
		this.data = bytes;
	}
	
	public RawData() {
	}
}
