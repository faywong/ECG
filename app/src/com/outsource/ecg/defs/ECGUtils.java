package com.outsource.ecg.defs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ECGUtils {
	private static final String JDBC_URL_PREFIX = "jdbc:sqldroid:";
	private static final String RECORD_TABLE_PREFIX = "_";
	public static final String ACTION_ECG_USER_MANAGE = "com.outsource.ecg.ECG_MANAGER_MANAGE";

	public static String getCurrentDataTime() {
		return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
		.format(new Date());
	}
	
	public static String getJdbcUrlPrefix() {
		return JDBC_URL_PREFIX;
	}
	
	/* 
	 * create a database connection from a local system url 
	 * indicating a sqlite3 database file location
	 */
	public static Connection getConnection(String localURL) throws SQLException {
		return DriverManager.getConnection(ECGUtils.getJdbcUrlPrefix() + localURL);
	}
	
	/**
	 * 
	 */
	public static String createRecordTableFromDate() {
		return RECORD_TABLE_PREFIX + getCurrentDataTime();
	}
	
	public static boolean validRecordTable(String table) {
		if (null == table) {
			return false;
		} else {
			return table.startsWith(RECORD_TABLE_PREFIX);
		}
	}
}

