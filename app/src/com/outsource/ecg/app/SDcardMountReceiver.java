package com.outsource.ecg.app;

import com.outsource.ecg.defs.ECGUserManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SDcardMountReceiver extends BroadcastReceiver {

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
			}
		} else if (Intent.ACTION_MEDIA_REMOVED.equals(action)
				|| Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
				|| Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {
			// fail to mounted, do nothing
			Toast.makeText(context, "External storage mount failed!",
					Toast.LENGTH_SHORT).show();
		}

	}

}
