package com.outsource.ecg.defs;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.integer;
import android.text.format.DateFormat;

public class ECGUser {
	// colume names & datatypes in user table
	 
	public static final String COL_NAME = "NAME NCHAR";
	public static final String COL_GENDER = "GENDER NCHAR";
	public static final String COL_BIRTH = "BIRTH NCHAR";
	public static final String COL_HBR = "HBR REAL";
	public static final String COL_ENROLL_DATE = "ENROLL_DATE TEXT";
	public static final String COL_DATA_PATH = "DATA_PATH NCHAR";

	public static final String COL_ID_NAME = "rowid";
	public static final String COL_NAME_NAME = "NAME";
	public static final String COL_GENDER_NAME = "GENDER";
	public static final String COL_BIRTH_NAME = "BIRTH";
	public static final String COL_HBR_NAME = "HBR";
	public static final String COL_ENROLL_DATE_NAME = "ENROLL_DATE";
	public static final String COL_DATA_PATH_NAME = "DATA_PATH";

	public static final int INVALID_ID = 99999;

	private int mID;
	private String mName;
	private String mGender;
	private String mBirth;
	private double mHBR;
	private String mEnrollDate;
	private String mDataPath;
	
	// used when create user from a sql query
	public ECGUser(int id, String name, String gender, String birthday, double HBR,
			String enrollDate) {
		mID = id;
		mName = name;
		mGender = gender;
		mBirth = birthday;
		mHBR = HBR;
		mEnrollDate = enrollDate;
		mDataPath = mName.trim() + "_" + mID;
	}

	public ECGUser(int id, String name, String gender, String birthday, double HBR) {
		mID = id;
		mName = name;
		mGender = gender;
		mBirth = birthday;
		mHBR = HBR;
		mEnrollDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
				.format(new Date());
		mDataPath = mName.trim() + "_" + mID;
	}

	// use this version when you want to add a new ECGUser to ECGUserManager
	public ECGUser(String name, String gender, String birthday, double HBR) {
		// will be set a reasonable value after inserted into SQLite database
		this(INVALID_ID, name, gender, birthday, HBR);
	}

	public int getID() {
		return mID;
	}

	public String getIDDesc() {
		return "ID:" + mID;
	}
	
	public String getName() {
		return mName;
	}

	public String getGender() {
		return mGender;
	}

	
	public String getEnrollDataDesc() {
		return "Enroll Date:" + mEnrollDate;
	}

	public String getAgeDesc() {
		return "Age:" + mBirth;
	}
	
	public String getBirth() {
		return mBirth;
	}

	public String getHBRDesc() {
		return "HBR:" + mHBR;
	}
	
	// get Heart beat rate
	public double getHBR() {
		return mHBR;
	}

	public String getECGDataPath() {
		return mDataPath;
	}
	
	public String getECGDataPathDesc() {
		return "ECGData:" + mDataPath;
	}
	
	public boolean isValid() {
		return (mID != INVALID_ID);
	}
	
	// for extension
	public boolean addProperty(String key, Object property) {
		return false;
	}

	public Object getProperty(String key) {
		return null;
	}

	public String getValues() {
		return "('" + mName.trim() + "', '" + mGender.trim() + "', '" + mBirth
				+ "', '" + mHBR + "', '" + mEnrollDate + "', '"
				+ mDataPath.trim() + "')";
	}

	static public String getTableStructure(boolean simple) {
		if (simple) {
			return "(" + COL_NAME_NAME + ", " + COL_GENDER_NAME + ", "
					+ COL_BIRTH_NAME + ", " + COL_HBR_NAME + ", "
					+ COL_ENROLL_DATE_NAME + ", " + COL_DATA_PATH_NAME + ")";
		} else {
			return "(" + COL_NAME + ", " + COL_GENDER + ", " + COL_BIRTH + ", "
					+ COL_HBR + ", " + COL_ENROLL_DATE + ", " + COL_DATA_PATH
					+ ")";
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + "id:" + mID + " name:" + mName + " gender:" + mGender + " age:" + mBirth
				+ " HBR:" + mHBR + " enrollDate:" + mEnrollDate + " dataPath:"
				+ mDataPath + "]";
	}
}
