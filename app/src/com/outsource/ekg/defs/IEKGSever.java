package com.outsource.ekg.defs;
public interface IEKGSever {
	public String getMetaData(int key);
	public void initialize();
	public void start();
	public void stop();
	public void release();
}