package com.outsource.ecg.defs;
public interface IECGClient {
    public String getMetaData(int key);
	public void initialize();
	public void connect(IECGSever server);
	public void disconnect(IECGSever server);
	public void release();
}