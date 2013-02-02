package com.outsource.ekg.defs;

public abstract class EKGUser {
	public String getID() {
		return null;
	}

	public String getName() {
		return null;
	}

	public String getGender() {
		return null;
	}

	public int getAge() {
		return 0;
	}

	// get Heart beat rate
	public int getHBR() {
		return 0;
	}

	// for extension
	public boolean addProperty(String key, Object property) {
		return false;
	}

	public Object getProperty(String key) {
		return null;
	}
}
