package com.outsource.ecg.app;

import java.util.ArrayList;

import com.outsource.ecg.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {
	private ListView mAllAppEntryListView;
	private AppEntryAdapter mAppEntryAdapter;
	private ArrayList<AppEntry> mAppEntries = new ArrayList<AppEntry>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_app_entry_list);
		mAllAppEntryListView = (ListView) findViewById(R.id.all_app_entry_list);
		mAppEntries.add(new RTEcgChartAppEntry());
		mAppEntries.add(new LoadHistoryAppEntry());
		mAppEntries.add(new UserManagerAppEntry());
		mAppEntryAdapter = new AppEntryAdapter(this, mAppEntries);
		mAllAppEntryListView.setAdapter(mAppEntryAdapter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			showDialog(this);
			return true;
		}
		return false;
	}

	private void showDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.about_app);
		builder.setMessage(R.string.app_info);
		builder.setPositiveButton("Thanks",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		builder.show();
	}

	public static class AppEntryAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;
		private ArrayList<AppEntry> mAppEntries;

		public AppEntryAdapter(Context context, ArrayList<AppEntry> entries) {
			mContext = context;
			mAppEntries = entries;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mAppEntries.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mAppEntries.get(arg0);
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
			ViewGroup app_entry = (ViewGroup) mInflater.inflate(
					R.layout.app_entry_item, null);
			Button appButton = (Button) app_entry.findViewById(R.id.app);
			final AppEntry entry = mAppEntries.get(arg0);
			appButton.setText(mContext.getString(entry.displayNameResID()));

			appButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					entry.launchForResult(mContext);
				}
			});
			return app_entry;
		}

	}

	public static interface AppEntry {
		public static final String kFeedbackInfo = "feedback";

		int displayNameResID();

		void launch(Context context);

		void launchForResult(Context context);

		int getRequestCode();
	}

	public static class LoadHistoryAppEntry implements AppEntry {

		private static final int requstCode = 20;

		private Intent createIntent(Context context, boolean needFeedback) {
			Intent intent = new Intent(context, EcgLoadHistoryActivity.class);
			intent.putExtra(AppEntry.kFeedbackInfo, needFeedback);
			return intent;
		}

		@Override
		public void launch(Context context) {
			// TODO Auto-generated method stub
			context.startActivity(createIntent(context, false));
		}

		@Override
		public void launchForResult(Context context) {
			// TODO Auto-generated method stub
			if (context instanceof Activity) {
				((Activity) context).startActivityForResult(
						createIntent(context, true), requstCode);
			}

		}

		@Override
		public int getRequestCode() {
			// TODO Auto-generated method stub
			return requstCode;
		}

		@Override
		public int displayNameResID() {
			// TODO Auto-generated method stub
			return R.string.load_history_entry;
		}
	}

	public static class RTEcgChartAppEntry implements AppEntry {

		private static final int requstCode = 21;

		private Intent createIntent(Context context, boolean needFeedback) {
			Intent intent = new Intent(context, RTEcgChartActivity.class);
			intent.putExtra(AppEntry.kFeedbackInfo, needFeedback);
			return intent;
		}

		@Override
		public void launch(Context context) {
			// TODO Auto-generated method stub
			context.startActivity(createIntent(context, false));
		}

		@Override
		public void launchForResult(Context context) {
			// TODO Auto-generated method stub
			if (context instanceof Activity) {
				((Activity) context).startActivityForResult(
						createIntent(context, true), requstCode);
			}

		}

		@Override
		public int getRequestCode() {
			// TODO Auto-generated method stub
			return requstCode;
		}

		@Override
		public int displayNameResID() {
			// TODO Auto-generated method stub
			return R.string.rt_ecg_chart;
		}
	}

	public static class UserManagerAppEntry implements AppEntry {

		private static final int requstCode = 21;

		private Intent createIntent(Context context, boolean needFeedback) {
			Intent intent = new Intent(context, EcgUserManageActivity.class);
			intent.putExtra(AppEntry.kFeedbackInfo, needFeedback);
			return intent;
		}

		@Override
		public void launch(Context context) {
			// TODO Auto-generated method stub
			context.startActivity(createIntent(context, false));
		}

		@Override
		public void launchForResult(Context context) {
			// TODO Auto-generated method stub
			if (context instanceof Activity) {
				((Activity) context).startActivityForResult(
						createIntent(context, true), requstCode);
			}

		}

		@Override
		public int getRequestCode() {
			// TODO Auto-generated method stub
			return requstCode;
		}

		@Override
		public int displayNameResID() {
			// TODO Auto-generated method stub
			return R.string.user_manager_title;
		}
	}
}
