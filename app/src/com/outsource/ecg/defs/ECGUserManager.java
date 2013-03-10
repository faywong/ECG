package com.outsource.ecg.defs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.R.bool;
import android.os.Environment;
import android.util.Log;
import android.widget.SimpleAdapter;

public class ECGUserManager {
	private static final String TAG = "EKGUserManager";
	private static ECGUserManager sInstance = null;
	private static final boolean TEST = false;
	private static final String ECG_USER_TABLE = "ECG_USER";
	private DBHelper mDBHelper = null;
	private static final String ECG_RELATIVE_PATH = "/ecg";
	// the local file system location to store all the ECG related data
	private static final String DataPath = Environment
			.getExternalStorageDirectory() + ECG_RELATIVE_PATH;
	private static final String UserManagementDBPath = DataPath
			+ "/user.sqlite";

	private ArrayList<ECGUser> mUsers = new ArrayList<ECGUser>();

	private static final ECGUser INVALID_USER = new ECGUser("Invalid", "Invalid", "1900-12-30", 0.0);
	private static ECGUser mCurrentUser = INVALID_USER;
	
	public String getDataPath() {
		return DataPath;
	}

	public static ECGUserManager Instance() throws Exception {
		if (null == sInstance) {
			sInstance = new ECGUserManager();
		}
		return sInstance;
	}

	private ECGUserManager() throws Exception {
		File ecgDataDir = new File(DataPath);
		if (!ecgDataDir.exists()) {
			boolean success = ecgDataDir.mkdir();
			if (!success) {
				throw new Exception("The ECG data dir:" + ecgDataDir
						+ " can't be created!");
			}
		}
	}

	public void loadUserInfo(boolean force) {
		if (force) {
			loadUserInfoInternal(force);
		}
	}

	private void loadUserInfoInternal(boolean force) {
		// TODO Auto-generated method stub
		if (force) {
			getAvailableUsers();
		} else if (mUsers.isEmpty()) {
			getAvailableUsers();
		}
		if (TEST) {
			addUser(new ECGUser("faywong1", "male", "2012-12-23", 46.0));
			addUser(new ECGUser("faywong2", "female", "2012-12-24", 48.0));
			Log.d(TAG, "All the available users are:" + getAvailableUsers());
		}
	}
	
	public static ECGUser getCurrentUser() {
		// TODO Auto-generated constructor stub
		return mCurrentUser;
	}
	
	public static String getCurrentUserDataPath() {
		// TODO Auto-generated constructor stub
		return DataPath + "/" + mCurrentUser.getECGDataPath();
	}
	
	public static void setCurrentUser(ECGUser user) {
		// TODO Auto-generated constructor stub
		synchronized (ECGUserManager.class) {
			mCurrentUser = user;
		}
	}
	
	/**
	 * Initialize system global user info database.
	 * ensure there's at least one table to store user metadata
	 * 
	 * @param con
	 */
	private static void initUserInfoDataBase(Connection con) throws Exception {
		String sql = "CREATE TABLE if not exists " + ECG_USER_TABLE + " "
						+ ECGUser.getUserInfoTableStructure(false);
		
		boolean create = con.createStatement().execute(sql);
		Log.d(TAG, "create table ECG_USER sql:" + sql + " result:" + create);
	}
	
	public static void delUserHistoryRecords(Connection con, String table) {
		if (null == con || null == table) {
			return;
		}
		String sql = String.format("DROP TABLE IF EXISTS %s", table);
		boolean dropRes = false;
		try {
			dropRes = con.createStatement().execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d(TAG, "The result of dropping table:" + table + " is " + dropRes);
	}
	
	/**
	 * create system global per-user ecg history records table
	 * 
	 * Table name format: "_ + {current_system_time}"
	 * @param con
	 */
	public static void createUserHistroyRecord(Connection con, String table, Collection<Double> series, double avgXoffset) {
		if (null == con || null == table) {
			return;
		}
		String sql = String.format("CREATE TABLE if not exists %s %s", table, ECGUser.getHistoyRecordTableStructure(false));
		Log.d(TAG, "sql to create histroy table:" + sql);
		if (avgXoffset < 0.0) {
			avgXoffset = 1;
		}
		boolean createRes = false;
		try {
			createRes = con.createStatement().execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "create table ECG_USER sql:" + sql + " result:" + createRes);
		
		// TODO: insert the series data
		double x = 0.0;
		for (double y : series) {
			x += avgXoffset;
		    String insertSql = String.format("INSERT INTO %s %s VALUES (%f, %f)", table, ECGUser.getHistoyRecordTableStructure(true), x, y);
			boolean insertRes = false;
			try {
				insertRes = con.createStatement().execute(insertSql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "sql to insert series data:" + insertSql + " result:" + insertRes);
		}
	}
	
	/**
	 * Retrieve the history ECG records of user
	 * 
	 * @param con
	 */
	public static ArrayList<String> getUserHistroyRecords(Connection con, ECGUser user) {
		String sql = String.format("SELECT name FROM sqlite_master WHERE type = 'table'");
		Log.d(TAG, "sql to create histroy table:" + sql);
		ArrayList<String> records = new ArrayList<String>();
		ResultSet queryResultSet = null;
		try {
			queryResultSet = con.createStatement().executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while (queryResultSet.next()) {
				String recordName = queryResultSet.getString("name");
				Log.d(TAG, "record table name is " + recordName);
				if (ECGUtils.validRecordTable(recordName)) {
					records.add(recordName);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return records;
	}

	public boolean addUser(ECGUser user) {
		Log.d(TAG, "addUser() in");
		if (null == user) {
			Log.d(TAG, "user null when addUser()");
			return false;
		}

		String url = ECGUtils.getJdbcUrlPrefix() + UserManagementDBPath;
		try {
			Connection sqlConnection = ECGUtils.getConnection(UserManagementDBPath);
			Statement queryStatement = sqlConnection.createStatement();
			String sql = String.format("INSERT INTO %s %s VALUES %s;",
					ECG_USER_TABLE, ECGUser.getUserInfoTableStructure(true),
					user.getValues());
			Log.d(TAG, "insert sql:" + sql);
			boolean addUsersRes = queryStatement.execute(sql);
			if (!addUsersRes) {
				Log.d(TAG, "query all the users failed!");
			}
			sqlConnection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG, "exception when query ecg users info" + ex);
		}
		return true;
	}

	public boolean delUser(ECGUser user)  {
		if (null == user) {
			Log.e(TAG, "null user, return!");
			return false;
		}
		
		try {
			Connection sqlConnection = ECGUtils.getConnection(UserManagementDBPath);
			Statement delStatement = sqlConnection.createStatement();
			String sql = String.format("DELETE FROM %s WHERE %s = '%s'", ECG_USER_TABLE, ECGUser.COL_ID_NAME, user.getID());
			Log.d(TAG, "the sql statement used to delete user:" + sql);
			delStatement.execute(sql);
			// no exception arised, treat as true case
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	
	public ArrayList<ECGUser> getAvailableUsers() {
		mUsers.clear();
		try {
			Connection sqlConnection = ECGUtils.getConnection(UserManagementDBPath);
			initUserInfoDataBase(sqlConnection);
			Statement queryStatement = sqlConnection.createStatement();
			// Note:Rowid is a hidden field and is not included in "*"
			String sql = String.format("SELECT %s,* FROM %s", ECGUser.COL_ID_NAME, ECG_USER_TABLE);
			Log.d(TAG, "query sql:" + sql);
			//boolean queryAllUsersRes = queryStatement.execute(sql);
			ResultSet resultSet = queryStatement.executeQuery(sql);
			Log.d(TAG, "query user info resultSet: " + resultSet);
			if (null == resultSet) {
				return mUsers;
			}
			while (resultSet.next()) {
				int id = resultSet.getInt(ECGUser.COL_ID_NAME);
				Log.d(TAG, "id is " + id);
				String name = resultSet.getString(ECGUser.COL_NAME_NAME);
				String gender = resultSet.getString(ECGUser.COL_GENDER_NAME);
				Log.d(TAG, "name:" + name + " gender:" + gender);
				String birth = resultSet.getString(ECGUser.COL_BIRTH_NAME);
				double HBR = resultSet.getDouble(ECGUser.COL_HBR_NAME);
				String enrollDate = resultSet
						.getString(ECGUser.COL_ENROLL_DATE_NAME);
/*				String dataPath = resultSet
						.getString(ECGUser.COL_DATA_PATH_NAME);*/
				ECGUser user = new ECGUser(id, name, gender, birth, HBR,
						enrollDate);
				mUsers.add(user);
				Log.d(TAG, "a new ECGUser got: " + user);
			}
			sqlConnection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG, "exception when query ecg users info " + ex);
		}
		return mUsers;
	}

	public DBHelper getDBHelper() {
		if (null == mDBHelper) {
			mDBHelper = new MyDBHelper();
		}
		return mDBHelper;
	}


	public static String getUsermanagementDBPath() {
		return UserManagementDBPath;
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