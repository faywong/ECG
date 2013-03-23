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

/**
 * This class mainly displays detailed info of available users through extending ExpandableListActivity
 * , also support select one user to be the current user or remove it, or launch CreateNewUserActivity to create a new User
 * @author faywong
 *
 */
public class EcgUserManageActivity extends ExpandableListActivity implements
		OnChildClickListener {
	private static final String TAG = "ECGUserManageActivity";
	static final int CREATE_USER_REQUEST = 0;

	ArrayList<ECGUser> groupItem = new ArrayList<ECGUser>();
	ArrayList<Object> childItem = new ArrayList<Object>();
	ECGUserAdapter mECGUserAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExpandableListView expandbleLis = getExpandableListView();
		expandbleLis.setDividerHeight(2);
		expandbleLis.setGroupIndicator(null);
		expandbleLis.setClickable(true);

		setGroupData();

		mECGUserAdapter = new ECGUserAdapter(groupItem,
				childItem);
		mECGUserAdapter
				.setInflater(
						(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
						this);
		getExpandableListView().setAdapter(mECGUserAdapter);
		expandbleLis.setOnChildClickListener(this);
	}

	public void setGroupData() {
		groupItem.clear();
		childItem.clear();
		try {
			ArrayList<ECGUser> users = ECGUserManager.Instance()
					.getAvailableUsers();
			for (ECGUser user : users) {
				groupItem.add(user);
				ArrayList<String> child = new ArrayList<String>();
				child.add(getString(R.string.ID_label) + user.getID());
				child.add(getString(R.string.gender_label) + user.getGender());
				child.add(getString(R.string.birth_label) + user.getBirth());
				child.add(getString(R.string.hbr_label) + user.getHBR());
				child.add(getString(R.string.ecg_data_path_label) + user.getECGDataPath());
				child.add(getString(R.string.enroll_date_label) + user.getEnrollDate());
				// the last one child is fake object for place "delete" & "select" button
				child.add(String.valueOf(user.getID()));
				childItem.add(child);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if (RTEcgChartActivity.DEBUG_UI_TOAST)
		Toast.makeText(EcgUserManageActivity.this, "Clicked On Child",
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
			startActivityForResult(serverIntent, CREATE_USER_REQUEST);
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
        if (requestCode == CREATE_USER_REQUEST) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
        		if (RTEcgChartActivity.DEBUG_UI_TOAST)
                Toast.makeText(this, "A new user created success!", Toast.LENGTH_SHORT).show();
                // update the user info
                setGroupData();
                mECGUserAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "A new user created failed!", Toast.LENGTH_SHORT).show();
            }
        }

		
	}
	
}
