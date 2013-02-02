package com.outsource.ekg.ui;

// Data class to explicitly indicate that these bytes are raw data
public class RawData {
	public byte[] bytes = null;

	public RawData(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public RawData() {
	}
}
