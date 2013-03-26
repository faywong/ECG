package com.outsource.ecg.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.afree.data.xy.XYSeries;
import org.afree.data.xy.XYSeriesCollection;

import com.outsource.ecg.ui.ECGUserAdapter;
import com.outsource.ecg.ui.JDBCXYChartView;
import com.outsource.ecg.ui.XYPlotView;
import com.outsource.ecg.defs.ECGUser;
import com.outsource.ecg.defs.ECGUserManager;
import com.outsource.ecg.defs.ECGUtils;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.outsource.ecg.R;

/**
 * This is activiy used to guide user to load history record of current selected user
 * it plays several roles:
 * 1) guide user to select a specified record by launching "EcgUserHistroyRecordActivity"
 * 2) display the history record chart through a customized JDBCXYChartView
 * @author faywong
 *
 */
public class EcgLoadHistoryActivity extends Activity {
	private static final String TAG = "EcgClient";
	static final boolean DEBUG = false;
	public static final boolean DEBUG_UI_TOAST = false;

	private static String DB_FILE_NAME = "ecg.sqlite";
	private String mDBFilePath;
	private BluetoothAdapter mBluetoothAdapter;
	// Name of the connected device

	// Intent request codes
	private static final int REQUEST_USER_MANAGE = 0;
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_SELECT_HISTROY_ECG_RECORD = 4;

	// Message types sent from the EcgService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the EcgService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	private TextView mNameText;
	private TextView mIDText;
	private TextView mHBRText;
	private JDBCXYChartView mPlotView;
	boolean mStarted = false;
	// Member object for the ECG services
	private EcgService mEcgService = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ecg_client_history_records);
		mPlotView = (JDBCXYChartView) findViewById(R.id.ecg_chart);
		updatePlotView();
		mNameText = (TextView) findViewById(R.id.patient_name);
		mIDText = (TextView) findViewById(R.id.patient_id);
		mHBRText = (TextView) findViewById(R.id.patient_hbr);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		Button loadBtn = (Button) findViewById(R.id.load_btn);
		loadBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!ECGUserManager.getCurrentUser().isValid()) {
					Toast.makeText(EcgLoadHistoryActivity.this,
							"Please select a valid ecg user!",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					startActivityForResult(new Intent(EcgLoadHistoryActivity.this,
							EcgUserHistroyRecordActivity.class),
							REQUEST_SELECT_HISTROY_ECG_RECORD);
				}

			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		checkExternalStorageMounted();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume() in");
		if (null != mEcgService) {
			mEcgService.start();
		}
		updateCurrentUserInfo();
	}

	private void updatePlotView() {
		if (DEBUG) {
			mDBFilePath = Environment.getExternalStorageDirectory() + "/ecg/"
					+ DB_FILE_NAME;
			// JDBCXYChartView contentView = new JDBCXYChartView(this,
			// mDBFilePath);
		} else {
			mDBFilePath = ECGUserManager.getCurrentUserDataPath();
			Log.d(TAG, "current user's mDBFilePath:" + mDBFilePath);

		}
		Log.d(TAG, "current user's mDBFilePath:" + mDBFilePath);
		mPlotView.setDBPath(mDBFilePath, "XYData1"/* test table */);
	}

	private void updateCurrentUserInfo() {
		OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EcgLoadHistoryActivity.this.startActivityForResult(new Intent(
						ECGUtils.ACTION_ECG_USER_MANAGE),
						EcgLoadHistoryActivity.REQUEST_USER_MANAGE);
			}
		};

		ECGUser currentUser = ECGUserManager.getCurrentUser();
		Log.d(TAG,
				"currentUser:" + currentUser + " valid:"
						+ currentUser.isValid() + " name:"
						+ currentUser.getName());
		if (null != mNameText) {
			mNameText.setText(getString(R.string.name_label) + "\n" + currentUser.getName());
			mNameText.setOnClickListener(clickListener);
		}
		if (null != mIDText) {
			mIDText.setText(getString(R.string.ID_label) + "\n" + String.valueOf(currentUser.getID()));
			mIDText.setOnClickListener(clickListener);
		}
		if (null != mHBRText) {
			mHBRText.setText(getString(R.string.hbr_label) + "\n" + String.valueOf(currentUser.getHBR()));
			mHBRText.setOnClickListener(clickListener);
		}
		if (!currentUser.isValid()) {
			Toast.makeText(
					this,
					getString(R.string.select_valid_user),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void checkExternalStorageMounted() {
		Log.d(TAG,
				"Result: "
						+ (Environment.MEDIA_MOUNTED != Environment
								.getExternalStorageState()));
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this,
					getString(R.string.external_storage_unmounted_prompt),
					Toast.LENGTH_SHORT).show();
			finish();
		} else {
			// populate user infomation from database
			try {
				ECGUserManager.Instance().loadUserInfo(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "ECGUserManager.Instance() failed!",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		if (null != data && null != data.getExtras()) {
			String address = data.getExtras().getString(
					DeviceListActivity.EXTRA_DEVICE_ADDRESS);
			// Get the BluetoothDevice object
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
			// Attempt to connect to the device
			mEcgService.connect(device, secure);
		} else {
			Log.e(TAG,
					"FATAL ERROR! The selected device for connect has no address!");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.d(TAG, "onActivityResult " + resultCode);

		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_USER_MANAGE:
			if (resultCode == Activity.RESULT_OK) {
				ECGUser user = data
						.getParcelableExtra(ECGUserAdapter.CURRENT_USER_EXTRA);
				updateCurrentUserInfo();
				if (EcgLoadHistoryActivity.DEBUG_UI_TOAST)
				Toast.makeText(this,
						"UserManage activity returned user:" + user,
						Toast.LENGTH_SHORT).show();
			}
			break;

		case REQUEST_SELECT_HISTROY_ECG_RECORD:
			if (resultCode == Activity.RESULT_OK) {
				String recordID = data
						.getStringExtra(EcgUserHistroyRecordActivity.EXTRA_TARGET_RECORD_ID);
				Log.d(TAG, "The recordID selected is " + recordID);
				mPlotView.setDBPath(ECGUserManager.getCurrentUserDataPath(),
						recordID);
			}
			break;
		}

	}
}