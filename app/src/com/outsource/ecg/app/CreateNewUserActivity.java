package com.outsource.ecg.app;

import com.outsource.ecg.R;
import com.outsource.ecg.defs.ECGUser;
import com.outsource.ecg.defs.ECGUserManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class CreateNewUserActivity extends Activity {

	DatePicker mDatePicker;
	String mGender = "male";
	EditText mNameInput;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user);
		mNameInput = (EditText)findViewById(R.id.name);
		Button commitButton = (Button) findViewById(R.id.commit_input);
		commitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTitle(String.valueOf(mDatePicker.getYear()) + "年"
						+ String.valueOf(mDatePicker.getMonth() + 1) + "月"
						+ String.valueOf(mDatePicker.getDayOfMonth()) + "日");
				String birthday = String.valueOf(mDatePicker.getYear()) + "-"
						+ String.valueOf(mDatePicker.getMonth());
				String name = mNameInput.getText().toString();
				try {
					ECGUserManager.Instance().addUser(
							new ECGUser(name, mGender, birthday, 64.0));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mDatePicker = (DatePicker) findViewById(R.id.date_picker);
		mDatePicker.init(1987, 11, 24, null);
		RadioGroup rg = (RadioGroup) findViewById(R.id.gender_group);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.male_radio) {
					Toast.makeText(CreateNewUserActivity.this, "Male选中",
							Toast.LENGTH_LONG).show();
					CreateNewUserActivity.this.mGender = "male";
				}
				if (checkedId == R.id.female_radio) {
					Toast.makeText(CreateNewUserActivity.this, "Female选中",
							Toast.LENGTH_LONG).show();
					CreateNewUserActivity.this.mGender = "female";
				}
			}

		});
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

}
