package com.outsource.ecg.defs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import android.R.bool;
import android.os.Environment;
import android.util.Log;

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
	private static final String JDBC_URL_PREFIX = "jdbc:sqldroid:";

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
	
	public ECGUser getCurrentUser() {
		// TODO Auto-generated constructor stub
		return mCurrentUser;
	}
	
	/**
	 * Initialize table.
	 * 
	 * @param con
	 */
	private static void initDataBase(Connection con) throws Exception {
		String sql = "CREATE TABLE if not exists " + ECG_USER_TABLE + " "
						+ ECGUser.getTableStructure(false);
		
		boolean create = con.createStatement().execute(sql);
		Log.d(TAG, "create table ECG_USER sql:" + sql + " result:" + create);
	}

	public boolean addUser(ECGUser user) {
		Log.d(TAG, "addUser() in");
		if (null == user) {
			Log.d(TAG, "user null when addUser()");
			return false;
		}

		String url = getJdbcUrlPrefix() + UserManagementDBPath;
		try {
			Connection sqlConnection = getConnection(UserManagementDBPath);
			Statement queryStatement = sqlConnection.createStatement();
			String sql = String.format("INSERT INTO %s %s VALUES %s;",
					ECG_USER_TABLE, ECGUser.getTableStructure(true),
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
			Connection sqlConnection = getConnection(UserManagementDBPath);
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

	Connection getConnection(String jdbcURL) throws SQLException {
		return DriverManager.getConnection(getJdbcUrlPrefix() + jdbcURL);
	}
	
	public ArrayList<ECGUser> getAvailableUsers() {
		mUsers.clear();
		try {
			Connection sqlConnection = getConnection(UserManagementDBPath);
			initDataBase(sqlConnection);
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
				String dataPath = resultSet
						.getString(ECGUser.COL_DATA_PATH_NAME);
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

	public ECGRecord getHistroyRecords() {
		return null;
	}

	public DBHelper getDBHelper() {
		if (null == mDBHelper) {
			mDBHelper = new MyDBHelper();
		}
		return mDBHelper;
	}

	public static String getJdbcUrlPrefix() {
		return JDBC_URL_PREFIX;
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

	private static class MethodNotImplementedException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			return "This method is not implemted yet as faywong(philip584521@gmail.com) is so busy!";
		}

		@Override
		public void printStackTrace() {
			// TODO Auto-generated method stub
			super.printStackTrace();
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "This method is not implemted yet as faywong(philip584521@gmail.com) is so busy!";

		}

	}
}