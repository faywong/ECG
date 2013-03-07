package com.outsource.ecg.app;

import java.util.ArrayList;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.outsource.ecg.R;
import com.outsource.ecg.ui.ECGUserAdapter;
import com.outsource.ecg.defs.ECGUserManager;
import com.outsource.ecg.defs.ECGUser;

public class ECGUserManageActivity extends ExpandableListActivity implements
		OnChildClickListener {
	private static final String TAG = "ECGUserManageActivity";
	public static final String ACTION_ECG_USER_MANAGE = "com.outsource.ecg.ECG_MANAGER_MANAGE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExpandableListView expandbleLis = getExpandableListView();
		expandbleLis.setDividerHeight(2);
		expandbleLis.setGroupIndicator(null);
		expandbleLis.setClickable(true);

		setGroupData();

		ECGUserAdapter mECGUserAdapter = new ECGUserAdapter(groupItem,
				childItem);
		mECGUserAdapter
				.setInflater(
						(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
						this);
		getExpandableListView().setAdapter(mECGUserAdapter);
		expandbleLis.setOnChildClickListener(this);
	}

	public void setGroupData() {
		try {
			ArrayList<ECGUser> users = ECGUserManager.Instance()
					.getAvailableUsers();
			for (ECGUser user : users) {
				groupItem.add(user);
				ArrayList<String> child = new ArrayList<String>();
				child.add(user.getIDDesc());
				child.add(user.getGender());
				child.add(user.getHBRDesc());
				child.add(user.getECGDataPathDesc());
				child.add(user.getEnrollDataDesc());
				childItem.add(child);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	ArrayList<ECGUser> groupItem = new ArrayList<ECGUser>();
	ArrayList<Object> childItem = new ArrayList<Object>();

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Toast.makeText(ECGUserManageActivity.this, "Clicked On Child",
				Toast.LENGTH_SHORT).show();
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.user_manager_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.add_user:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, CreateNewUserActivity.class);
			startActivityForResult(serverIntent, 0);
			return true;
		}
		return false;
	}
}
