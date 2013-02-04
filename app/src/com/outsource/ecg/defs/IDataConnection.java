package com.outsource.ecg.defs;

public interface IDataConnection {
    public boolean connect();
	public boolean disconnect();
	// for dynamic & in-continuous operation
	public boolean sendData(Byte[] data);
	public boolean receiveData(int maxLength, Byte[] data);
	// for store/send data to/from persistent storage
	public boolean sendData(String file);
	public boolean receiveData(String file);
}
