package com.outsource.ekg.defs;

import java.util.Date;

public abstract class EKGRecord {
	private EKGRecord(String filename, EKGUser owner, Date date) {

	}

	public void setOwner(EKGUser owner) {

	}

	public int getOwnerID() {
		return -1;
	}

	public EKGUser getOwner() {
		return null;
	}

	public void setDate(Date date) {

	}
	
	public Date getDate() {
		return null;
	}
}