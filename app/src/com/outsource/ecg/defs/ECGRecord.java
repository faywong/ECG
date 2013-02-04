package com.outsource.ecg.defs;

import java.util.Date;

public abstract class ECGRecord {
	private ECGRecord(String filename, ECGUser owner, Date date) {

	}

	public void setOwner(ECGUser owner) {

	}

	public int getOwnerID() {
		return -1;
	}

	public ECGUser getOwner() {
		return null;
	}

	public void setDate(Date date) {

	}
	
	public Date getDate() {
		return null;
	}
}