package com.outsource.ecg.impl;

import com.outsource.ecg.defs.IDataConnection;

public class BluetoothDataConnection implements IDataConnection {

	public BluetoothDataConnection() {
		
	}
	
	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendData(Byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean receiveData(int maxLength, Byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendData(String file) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean receiveData(String file) {
		// TODO Auto-generated method stub
		return false;
	}

}
