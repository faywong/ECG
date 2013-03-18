package com.outsource.ecg.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.outsource.ecg.R;
import com.outsource.ecg.defs.ECGUserManager;
import com.outsource.ecg.defs.ECGUtils;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ECGUserHistroyRecordActivity extends ListActivity {
	private ECGRecordListAdapter mListAdapter;

	public static final String EXTRA_TARGET_RECORD_ID = "record_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setListAdapter(mListAdapter = new ECGRecordListAdapter(
				getHistoryRecordIDs(), this));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mListAdapter.setRecordIDs(getHistoryRecordIDs());
		mListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	ArrayList<String> getHistoryRecordIDs() {
		ArrayList<String> recordIDs = null;
		try {
			Connection connection = ECGUtils.getConnection(ECGUserManager
					.getCurrentUserDataPath());
			recordIDs = ECGUserManager.getUserHistroyRecords(connection,
					ECGUserManager.getCurrentUser());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recordIDs;
	}

	private static class ECGRecordListAdapter extends BaseAdapter {
		ArrayList<String> mRecords;
		Activity mContext;

		public ECGRecordListAdapter(ArrayList<String> recordIDs,
				Activity context) {
			mRecords = recordIDs;
			mContext = context;
		}

		public void setRecordIDs(ArrayList<String> recordIDs) {
			mRecords = recordIDs;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mRecords.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mRecords.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public int getItemViewType(int arg0) {
			// TODO Auto-generated method stub
			return IGNORE_ITEM_VIEW_TYPE;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			LayoutInflater flater = LayoutInflater.from(mContext);
			View row = flater.inflate(R.layout.select_recordrow, null);
			TextView recordIDView = (TextView) row.findViewById(R.id.record);
			final String currentRecordID = mRecords.get(arg0);
			recordIDView.setText(currentRecordID);
			Button selectRecordBtn = (Button) row
					.findViewById(R.id.select_record);
			selectRecordBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra(EXTRA_TARGET_RECORD_ID, currentRecordID);
					mContext.setResult(RESULT_OK, intent);
					mContext.finish();
				}
			});

			Button delRecordBtn = (Button) row.findViewById(R.id.delete_record);
			delRecordBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						Connection connection = ECGUtils
								.getConnection(ECGUserManager
										.getCurrentUserDataPath());
						ECGUserManager.delUserHistoryRecords(connection,
								currentRecordID);
						mContext.finish();
						if (EcgClientActivity.DEBUG_UI_TOAST)
						Toast.makeText(
								mContext,
								"The history record:" + currentRecordID
										+ " is deleted!", Toast.LENGTH_SHORT)
								.show();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			return row;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return true;
		}

	}
}
