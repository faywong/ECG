package com.outsource.ekg.defs;
public interface IEKGClient {
    public String getMetaData(int key);
	public void initialize();
	public void connect(IEKGSever server);
	public void disconnect(IEKGSever server);
	public void release();
}