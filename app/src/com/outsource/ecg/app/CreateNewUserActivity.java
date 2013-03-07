package com.outsource.ecg.app;

import com.outsource.ecg.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class CreateNewUserActivity extends Activity {

	DatePicker mDatePicker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user);
		Button commitButton = (Button)findViewById(R.id.commit_input);
		commitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 setTitle(String.valueOf(mDatePicker.getYear())+"Äê"+String.valueOf(mDatePicker.getMonth()+1)+"ÔÂ"+String.valueOf(mDatePicker.getDayOfMonth())+"ÈÕ");
			}
		});
		mDatePicker =(DatePicker)findViewById(R.id.date_picker);
		mDatePicker.init(1987,11,24, null);

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
