package com.outsource.ekg.defs;

import java.util.Set;

public class EKGUserManager {
	private static final String TAG = "EKGUserManager";
	private static EKGUserManager sInstance = null;
	private DBHelper mDBHelper = null;

	public static EKGUserManager Instance() {
		if (null == sInstance) {
			sInstance = new EKGUserManager();
		}
		return sInstance;
	}

	private EKGUserManager() {

	}

	public boolean addUser(EKGUser user) {
		return true;
	}

	public boolean delUser(EKGUser user) {
		return true;
	}

	public Set<EKGUser> getAvailableUsers() {
		return null;
	}

	public EKGRecord getHistroyRecords() {
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
		public EKGUser queryUserByID() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static interface DBHelper {
		public boolean dumpToDB(String db);

		public boolean populateFromDB(String db);

		public EKGUser queryUserByID();
	}
}