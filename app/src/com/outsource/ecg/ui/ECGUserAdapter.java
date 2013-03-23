package com.outsource.ecg.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.outsource.ecg.R;
import com.outsource.ecg.app.RTEcgChartActivity;
import com.outsource.ecg.defs.ECGUser;
import com.outsource.ecg.defs.ECGUserManager;

@SuppressWarnings("unchecked")
public class ECGUserAdapter extends BaseExpandableListAdapter {

	public static final String CURRENT_USER_EXTRA = "current_ecg_user";
	public ArrayList<ECGUser> groupItem;
	public ArrayList<String> tempChild;
	public ArrayList<Object> childitem = new ArrayList<Object>();
	public LayoutInflater minflater;
	public Context context;

	public ECGUserAdapter(ArrayList<ECGUser> grList, ArrayList<Object> childItem) {
		groupItem = grList;
		childitem = childItem;
	}

	public void setInflater(LayoutInflater mInflater, Context act) {
		this.minflater = mInflater;
		context = act;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		tempChild = (ArrayList<String>) childitem.get(groupPosition);
		final ECGUser user = groupItem.get(groupPosition);
		TextView text = null;
		if (convertView == null) {
			// last child is delete button
			if (childPosition == (getChildrenCount(groupPosition) - 1)) {
				convertView = minflater.inflate(R.layout.select_childrow, null);
				Button selectBtn = (Button) convertView.findViewById(R.id.select_user);
				selectBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ECGUserManager.setCurrentUser(user);
						if (RTEcgChartActivity.DEBUG_UI_TOAST)
						Toast.makeText(context, "user" + user + " selected!", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						  intent.putExtra(ECGUserAdapter.CURRENT_USER_EXTRA, user);
						  ((Activity)context).setResult(Activity.RESULT_OK, intent);
						  ((Activity)context).finish();
					}
				});
				Button delBtn = (Button) convertView.findViewById(R.id.delete_user);
				delBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						try {
							if (!ECGUserManager.Instance().delUser(user)) {
								Toast.makeText(context, "Delete user" + user + " failed!", Toast.LENGTH_LONG).show();
							} else {
								ECGUserAdapter.this.groupItem.remove(user);
								ECGUserAdapter.this.notifyDataSetChanged();
							}
						} catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(context, "Delete user" + user + " an exception arised!", Toast.LENGTH_LONG).show();							
						}
					}
				});

			} else {
				convertView = minflater.inflate(R.layout.childrow, null);
				text = (TextView) convertView.findViewById(R.id.textView1);
				text.setText(tempChild.get(childPosition));
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (RTEcgChartActivity.DEBUG_UI_TOAST)
						Toast.makeText(context, tempChild.get(childPosition),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) childitem.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return groupItem.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = minflater.inflate(R.layout.grouprow, null);
		}
		((CheckedTextView) convertView).setText(groupItem.get(groupPosition).getName());
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
