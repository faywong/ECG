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
 * This is the main entry of whole ECGClient application it plays several roles:
 * 1) the main activity 2) start a ECGService based on several threads for
 * bluetooth communication, currently handle receiving data 3) monitor the
 * SDCard mount/unmount event, the normally executing of this application
 * depends on the usage of external storage of phone 4) inflate two content
 * view: 4.1) "ecg_client_db" layout for a sqlite db driven chart view 4.2)
 * "ecg_client_rt_ecg" layout for XY Serials(double value pairs, such as 0.1->1,
 * 0.2->1.2) driven chart view 5) launch other related
 * activities(ECGUserHistroyRecordActivity for reviewing the history record of a
 * user,) ECGUserManageActivity for selecting a user to be the current user or
 * showing the detail info of a specific user, deleting an existed user, adding
 * a new user by launching CreateNewUserActivity and handle their executing
 * results
 * 
 * @author faywong
 * 
 */
public class RTEcgChartActivity extends Activity {
	private static final String TAG = "EcgClient";
	static final boolean DEBUG = false;
	public static final boolean DEBUG_UI_TOAST = false;

	private static final boolean TEST_USER_RECORDS = false;

	private static String DB_FILE_NAME = "ecg.sqlite";
	private String mDBFilePath;
	private XYSeries mDefaultSeries;
	private double mLastX = 0.0;
	private BluetoothAdapter mBluetoothAdapter;
	// Name of the connected device
	private String mConnectedDeviceName = null;
	private String mLastConnectedDevice = null;

	// Intent request codes
	private static final int REQUEST_USER_MANAGE = 0;
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
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
	private TextView mConnectionText;
	private XYPlotView mPlotView;
	boolean mStarted = false;

	private Button mStartStopBtn;
	private Button mSaveBtn;

	private ArrayList<Double> mReceivedEcgDataSet = new ArrayList<Double>();
	// Member object for the ECG services
	private EcgService mEcgService = null;

	private View.OnClickListener mSaveButtonOnclickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!ECGUserManager.getCurrentUser().isValid()) {
				Toast.makeText(RTEcgChartActivity.this,
						getString(R.string.select_valid_user),
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (TEST_USER_RECORDS) {
				mReceivedEcgDataSet.add(4.10);
				mReceivedEcgDataSet.add(4.12);
				mReceivedEcgDataSet.add(4.16);
				mReceivedEcgDataSet.add(4.19);
				mReceivedEcgDataSet.add(4.20);
				mReceivedEcgDataSet.add(4.22);
				mReceivedEcgDataSet.add(4.29);
			}

			if (mReceivedEcgDataSet.isEmpty()) {
				Toast.makeText(RTEcgChartActivity.this,
						getString(R.string.not_recv_any_data),
						Toast.LENGTH_SHORT).show();
				return;
			}

			try {
				Connection connection = ECGUtils.getConnection(ECGUserManager
						.getCurrentUserDataPath());
				ECGUserManager.createUserHistroyRecord(connection,
						ECGUtils.createRecordTableFromDate(),
						mReceivedEcgDataSet, 1);
				/*
				 * DEBUG for (String recordID : ECGUserManager
				 * .getUserHistroyRecords(connection,
				 * ECGUserManager.getCurrentUser())) { Log.d(TAG, "Record: " +
				 * recordID); }
				 */
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// after storing to database, clear the received dataset
			mReceivedEcgDataSet.clear();
			if (RTEcgChartActivity.DEBUG_UI_TOAST)
				Toast.makeText(RTEcgChartActivity.this,
						getString(R.string.new_user_created_debug),
						Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(RTEcgChartActivity.this,
					EcgUserHistroyRecordActivity.class);
			startActivityForResult(intent, REQUEST_SELECT_HISTROY_ECG_RECORD);
		}
	};

	private View.OnClickListener mStartStopButtonOncliClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// first need select a valid ECG User

			if (!ECGUserManager.getCurrentUser().isValid()) {
				Toast.makeText(RTEcgChartActivity.this,
						getString(R.string.select_valid_user),
						Toast.LENGTH_SHORT).show();
				return;
			}

			// second check whether target device is connected
			if (EcgService.STATE_CONNECTED != RTEcgChartActivity.this.mEcgService
					.getState()) {
				Toast.makeText(RTEcgChartActivity.this,
						getString(R.string.target_device_not_connected),
						Toast.LENGTH_SHORT).show();
				return;
			}

			/*
			 * for (int i = 1; i <= 100; i++) { mDefaultSeries.add(i * 1.0,
			 * Math.random() * 7000 + 11000); }
			 */

			synchronized (RTEcgChartActivity.this) {
				mStarted = !mStarted;
				if (mStarted) {
					mStartStopBtn.setText(R.string.stop_label);
					// do the real start stuff
				} else {
					mStartStopBtn.setText(R.string.start_label);
					// do the real stop stuff
				}
			}
		}
	};

	// The Handler that gets information back from the EcgService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (DEBUG)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case EcgService.STATE_CONNECTED:
					// setStatus(getString(R.string.title_connected_to,
					// mConnectedDeviceName));
					// mConversationArrayAdapter.clear();
					Bundle bundle = msg.getData();
					mLastConnectedDevice = bundle.getString(RTEcgChartActivity.DEVICE_NAME);

				case EcgService.STATE_CONNECTING:
					// setStatus(R.string.title_connecting);
				case EcgService.STATE_LISTEN:
				case EcgService.STATE_NONE:
					// setStatus(R.string.title_not_connected);
					updateConnectionInfo();
				}
				break;
			case MESSAGE_WRITE:
				// byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				// String writeMessage = new String(writeBuf);
				// mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);

				if (DEBUG_UI_TOAST)
					Toast.makeText(RTEcgChartActivity.this,
							"got a message " + readMessage, Toast.LENGTH_LONG)
							.show();
				if (DEBUG)
					Log.d(TAG, "got a message " + readMessage
							+ " mDefaultSeries" + mDefaultSeries);
				// mConversationArrayAdapter.add(mConnectedDeviceName+":  " +
				// readMessage);
				double y = 0.0;
				try {
					y = Double.parseDouble(readMessage);
				} catch (NumberFormatException ex) {
					Log.e(TAG, "Msg " + readMessage + " isn't a double type "
							+ ex);
					return;
				}
				Log.d(TAG, "got a double data " + y + " mDefaultSeries:"
						+ mDefaultSeries);
				if (DEBUG_UI_TOAST)
					Toast.makeText(RTEcgChartActivity.this,
							"Received a msg with a double type data:" + y,
							Toast.LENGTH_LONG).show();

				if (null == mDefaultSeries) {
					Toast.makeText(
							RTEcgChartActivity.this,
							"Please press start to prepare for receiving income data!",
							Toast.LENGTH_LONG);
					return;
				}

				mDefaultSeries.add(mLastX, y);
				mLastX += 0.2;
				if (mStarted) {
					mReceivedEcgDataSet.add(y);
					if (DEBUG_UI_TOAST) {
						Toast.makeText(RTEcgChartActivity.this,
								"Have added the data " + y
										+ " to mReceivedEcgDataSet",
								Toast.LENGTH_LONG);
					}
				}

				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	// External storage state listener
	private final BroadcastReceiver mSdcardListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Log.d("TAG", "sdcard action:::::" + action);
			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)
					|| Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)
					|| Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
				// mounted
				try {
					ECGUserManager.Instance().loadUserInfo(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(context,
							"ECGUserManager.Instance() failed!",
							Toast.LENGTH_SHORT).show();
					finish();
				}
			} else if (Intent.ACTION_MEDIA_REMOVED.equals(action)
					|| Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
					|| Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {
				// fail to mounted, do nothing
				Toast.makeText(context, "External storage mount failed!",
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ecg_client_rt_ecg);
		mPlotView = (XYPlotView) findViewById(R.id.ecg_chart);
		mSaveBtn = (Button) findViewById(R.id.save_btn);
		mSaveBtn.setOnClickListener(mSaveButtonOnclickListener);

		mStartStopBtn = (Button) findViewById(R.id.start_stop_btn);
		mStartStopBtn.setOnClickListener(mStartStopButtonOncliClickListener);

		XYSeriesCollection series = mPlotView.getDataset();
		mDefaultSeries = (XYSeries) series.getSeries().get(
				XYPlotView.DEFAULT_SERIES_INDEX);
		mNameText = (TextView) findViewById(R.id.patient_name);
		mIDText = (TextView) findViewById(R.id.patient_id);
		mHBRText = (TextView) findViewById(R.id.patient_hbr);
		mConnectionText = (TextView) findViewById(R.id.connection_status);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		// register external storage state listener
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addDataScheme("file");
		registerReceiver(mSdcardListener, intentFilter);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		enableBluetoothFunction();
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
		// connect the device last connected
		updateConnectionInfo();
	}

	private void updateConnectionInfo() {
		// TODO Auto-generated method stub
		setUpEcgService();
		switch (mEcgService.getState()) {
		case EcgService.STATE_CONNECTED:
			mConnectionText.setText(R.string.connected);
			break;
		case EcgService.STATE_CONNECTING:
			mConnectionText.setText(R.string.connecting);
			break;
		case EcgService.STATE_LISTEN:
			mConnectionText.setText(R.string.listen);
			break;
		case EcgService.STATE_NONE:
		default:
			mConnectionText.setText(R.string.none);
			break;
		}
	}

	private void updateCurrentUserInfo() {
		OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				RTEcgChartActivity.this.startActivityForResult(new Intent(
						ECGUtils.ACTION_ECG_USER_MANAGE),
						RTEcgChartActivity.REQUEST_USER_MANAGE);
			}
		};

		ECGUser currentUser = ECGUserManager.getCurrentUser();
		Log.d(TAG,
				"currentUser:" + currentUser + " valid:"
						+ currentUser.isValid() + " name:"
						+ currentUser.getName());
		if (null != mNameText) {
			mNameText.setText(getString(R.string.name_label) + "\n"
					+ currentUser.getName());
			mNameText.setOnClickListener(clickListener);
		}
		if (null != mIDText) {
			mIDText.setText(getString(R.string.ID_label) + "\n"
					+ String.valueOf(currentUser.getID()));
			mIDText.setOnClickListener(clickListener);
		}
		if (null != mHBRText) {
			mHBRText.setText(getString(R.string.hbr_label) + "\n"
					+ String.valueOf(currentUser.getHBR()));
			mHBRText.setOnClickListener(clickListener);
		}
		if (!currentUser.isValid()) {
			Toast.makeText(this, getString(R.string.select_valid_user),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mSdcardListener);
	}

	private void setUpEcgService() {
		// Initialize the BluetoothChatService to perform bluetooth connections
		if (null == mEcgService) {
			mEcgService = new EcgService(this, mHandler);
		}
	}

	private void ensureDiscoverable() {
		if (DEBUG)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	private void enableBluetoothFunction() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			setUpEcgService();
		}

	}

	private void checkExternalStorageMounted() {
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.realtime_ecg_option, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.insecure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent,
					REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.d(TAG, "onActivityResult " + resultCode);

		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up ECG Service
				setUpEcgService();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
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
				if (RTEcgChartActivity.DEBUG_UI_TOAST)
					Toast.makeText(this,
							"UserManage activity returned user:" + user,
							Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
}