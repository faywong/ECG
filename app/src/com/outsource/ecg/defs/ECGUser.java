package com.outsource.ecg.defs;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.integer;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

public class ECGUser implements Parcelable {
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
	public ECGUser(int id, String name, String gender, String birthday,
			double HBR, String enrollDate) {
		mID = id;
		mName = name;
		mGender = gender;
		mBirth = birthday;
		mHBR = HBR;
		mEnrollDate = enrollDate;
		buildDataPath();
	}

	public ECGUser(int id, String name, String gender, String birthday,
			double HBR) {
		mID = id;
		mName = name;
		mGender = gender;
		mBirth = birthday;
		mHBR = HBR;
		mEnrollDate = ECGUtils.getCurrentDataTime();
		buildDataPath();
	}

	// use this version when you want to add a new ECGUser to ECGUserManager
	public ECGUser(String name, String gender, String birthday, double HBR) {
		// will be set a reasonable value after inserted into SQLite database
		this(INVALID_ID, name, gender, birthday, HBR);
	}

	public ECGUser(Parcel in) {
		mID = in.readInt();
		mName = in.readString();
		mGender = in.readString();
		mBirth = in.readString();
		mHBR = in.readDouble();
		mEnrollDate = in.readString();
		buildDataPath();
	}
	
	private void buildDataPath() {
		mDataPath = mName.trim() + "_" + mID + ".sqlite";
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
	
	public String getGenderDesc() {
		return "Gender:" + mGender;
	}
	
	public String getBirthDesc() {
		return "Birth:" + mBirth;
	}

	public String getEnrollDateDesc() {
		return "Enroll Date:" + mEnrollDate;
	}

	public String getEnrollDate() {
		return mEnrollDate;
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

	static public String getUserInfoTableStructure(boolean simple) {
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

	static public String getHistoyRecordTableStructure(boolean simple) {
		// return
		// "(X REAL, SERIESE1 REAL, SERIESE2 REAL, SERIESE3 REAL, SERIESE4 REAL)";
		// now only support one series per-user
		if (simple) {
			return "(X, SERIES1)";

		} else {
			return "(X REAL, SERIES1 REAL)";
		}

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + "id:" + mID + " name:" + mName + " gender:" + mGender
				+ " birth:" + mBirth + " HBR:" + mHBR + " enrollDate:"
				+ mEnrollDate + " dataPath:" + mDataPath + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(mID);
		dest.writeString(mName);
		dest.writeString(mGender);
		dest.writeString(mBirth);
		dest.writeDouble(mHBR);
		dest.writeString(mEnrollDate);
		dest.writeString(mDataPath);
	}

	public static final Parcelable.Creator<ECGUser> CREATOR = new Parcelable.Creator<ECGUser>() {
		public ECGUser createFromParcel(Parcel in) {
			return new ECGUser(in);
		}

		@Override
		public ECGUser[] newArray(int size) {
			return new ECGUser[size];
		}
	};
}
