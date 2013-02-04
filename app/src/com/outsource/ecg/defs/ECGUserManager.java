package com.outsource.ecg.defs;

import java.util.Set;

public class ECGUserManager {
	private static final String TAG = "EKGUserManager";
	private static ECGUserManager sInstance = null;
	private DBHelper mDBHelper = null;

	public static ECGUserManager Instance() {
		if (null == sInstance) {
			sInstance = new ECGUserManager();
		}
		return sInstance;
	}

	private ECGUserManager() {

	}

	public boolean addUser(ECGUser user) {
		return true;
	}

	public boolean delUser(ECGUser user) {
		return true;
	}

	public Set<ECGUser> getAvailableUsers() {
		return null;
	}

	public ECGRecord getHistroyRecords() {
		return null;
	}

	public DBHelper getDBHelper() {
		if (null == mDBHelper) {
			mDBHelper = new MyDBHelper();
		}
		return mDBHelper;
	}

	private class MyDBHelper implements DBHelper {

		@Override
		public boolean dumpToDB(String db) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean populateFromDB(String db) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ECGUser queryUserByID() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static interface DBHelper {
		public boolean dumpToDB(String db);

		public boolean populateFromDB(String db);

		public ECGUser queryUserByID();
	}
}