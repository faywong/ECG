package com.outsource.ecg.defs;
public interface IECGSever {
	public String getMetaData(int key);
	public void initialize();
	public void start();
	public void stop();
	public void release();
}