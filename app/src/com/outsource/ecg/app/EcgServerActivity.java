package com.outsource.ecg.app;

import com.outsource.ecg.defs.ECGUser;
import com.outsource.ecg.defs.ECGUserManager;
import com.outsource.ecg.defs.ECGUtils;
import com.outsource.ecg.defs.IDataConnection;
import com.outsource.ecg.defs.IECGMsgSegment;
import com.outsource.ecg.defs.IECGMsgParser.OnDataCaptureListener;
import com.outsource.ecg.ui.ECGUserAdapter;
import com.outsource.ecg.ui.JDBCXYChartView;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.outsource.ecg.R;

public class EcgServerActivity extends Activity {
	private static final String TAG = "EcgClient";
	private static final boolean DEBUG = true;
	private BluetoothAdapter mBluetoothAdapter;
	// Name of the connected device
	private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    private ListView mHistoryDataList = null;
    private EditText mOutEditText;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

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
	private Button mSendBtn;

	// Member object for the ECG services
	private EcgService mEcgService = null;

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
					setStatus(getString(R.string.title_connected_to) + mConnectedDeviceName);
					mConversationArrayAdapter.clear();
				case EcgService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
				case EcgService.STATE_LISTEN:
				case EcgService.STATE_NONE:
					setStatus(R.string.title_not_connected);
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				//construct a string from the buffer
				String writeMessage = new String(writeBuf);
				mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
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

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(DEBUG) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ecg_server_main);
		mHistoryDataList = (ListView) findViewById(R.id.history_data_list);
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mHistoryDataList.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.out_data);
        mOutEditText.setOnEditorActionListener(mWriteListener);
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		mSendBtn = (Button)findViewById(R.id.send_button);
		mSendBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		
        // Initialize the send button with a listener that for click events
		mSendBtn = (Button) findViewById(R.id.send_button);
		mSendBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.out_data);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		enableBluetoothFunction();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume() in");
		if (null != mEcgService) {
			mEcgService.start();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void setUpEcgService() {
		// Initialize the EcgService to perform bluetooth connections
		if (null == mEcgService) {
			mEcgService = new EcgService(this, mHandler);
		}
		if (null == mOutStringBuffer) {
	        // Initialize the buffer for outgoing messages
	        mOutStringBuffer = new StringBuffer("");
		}
	}

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mEcgService.getState() != EcgService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the EcgService to write
            byte[] send = message.getBytes();
            mEcgService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
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
		inflater.inflate(R.menu.main_option_menu, menu);
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
		}

	}
	
    private final void setStatus(int resId) {
        setTitle(resId);
    }
    
    private final void setStatus(String title) {
        setTitle(title);
    }
}
