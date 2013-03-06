package com.outsource.ecg.app;

import java.util.ArrayList;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

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
		setChildGroupData();

		ECGUserAdapter mECGUserAdapter = new ECGUserAdapter(groupItem, childItem);
		mECGUserAdapter
				.setInflater(
						(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
						this);
		getExpandableListView().setAdapter(mECGUserAdapter);
		expandbleLis.setOnChildClickListener(this);
	}

	public void setGroupData() {
		try {
			ArrayList<ECGUser> users = ECGUserManager.Instance().getAvailableUsers();
			for (ECGUser user: users) {
				groupItem.add(user);
				ArrayList<String> child = new ArrayList<String>();
				child.add(user.getIDDesc());
				child.add(user.getGender());
				child.add(user.getHBRDesc());
				child.add(user.getECGDataPathDesc());
				childItem.add(child);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			
		}
	}

	ArrayList<ECGUser> groupItem = new ArrayList<ECGUser>();
	ArrayList<Object> childItem = new ArrayList<Object>();

	public void setChildGroupData() {/*
		*//**
		 * Add Data For TecthNology
		 *//*
		ArrayList<String> child = new ArrayList<String>();
		child.add("Java");
		child.add("Drupal");
		child.add(".Net Framework");
		child.add("PHP");
		childItem.add(child);

		*//**
		 * Add Data For Mobile
		 *//*
		child = new ArrayList<String>();
		child.add("Android");
		child.add("Window Mobile");
		child.add("iPHone");
		child.add("Blackberry");
		childItem.add(child);
		*//**
		 * Add Data For Manufacture
		 *//*
		child = new ArrayList<String>();
		child.add("HTC");
		child.add("Apple");
		child.add("Samsung");
		child.add("Nokia");
		childItem.add(child);
		*//**
		 * Add Data For Extras
		 *//*
		child = new ArrayList<String>();
		child.add("Contact Us");
		child.add("About Us");
		child.add("Location");
		child.add("Root Cause");
		childItem.add(child);
	*/}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Toast.makeText(ECGUserManageActivity.this, "Clicked On Child",
				Toast.LENGTH_SHORT).show();
		return true;
	}
}
