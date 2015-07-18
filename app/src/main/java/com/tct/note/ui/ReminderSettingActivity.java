/******************************************************************************/
/*                                                               Date:07/2013 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2013 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  Yan.Teng                                                        */
/*  Email  :  Yan.Teng@tcl-mobile.com                                         */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     :                                                                */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 07/05/2013|       Yan.Teng       |       CR484292       |the development   */
/*           |                      |                      |of note and note  */
/*           |                      |                      |widget            */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.note.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tct.note.util.TctLog;
import com.tct.note.R;

public class ReminderSettingActivity extends Activity {
    private static final String TAG = "ReminderSettingActivity";
    private Button mStartDateButton;
    private Button mStartTimeButton;
    private long currentMil = -1;
    private Time mStartTime;

    
    //PR894251.Date(Time)PickerDialog show error when switch screen orientation. Added by hz_nanbing.zou at 07/01/2015 begin
    private DatePickerDialog dpd;
    private TimePickerDialog tp;
    private boolean dateflag = false;
    private boolean timeflag = false;
    
	Handler mhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			TctLog.d("nb","handleMessage");
			if(dpd!=null){
				if(dpd.isShowing()){
					dpd.dismiss();
					dateflag = true;
					mStartDateButton.performClick();
				}
			}
			if(tp!=null){
				if(tp.isShowing()){
					tp.dismiss();
				timeflag = true;
				mStartTimeButton.performClick();
				}
			}
			super.handleMessage(msg);
		}
		
	};
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_setting);
        mStartDateButton = (Button) findViewById(R.id.start_date);
        mStartTimeButton = (Button) findViewById(R.id.start_time);
        mStartTime = new Time();
        final long mil = System.currentTimeMillis();
        mStartTime.setToNow();
        mStartDateButton.setOnClickListener(new DateClickListener(mStartTime));
        mStartTimeButton.setOnClickListener(new TimeClickListener(mStartTime));

        setDate(mStartDateButton, mil);
        setTime(mStartTimeButton, mil);
        currentMil = mil;
        ActionBar actionBar = getActionBar();

        View view = LayoutInflater.from(this).inflate(R.layout.reminder_actionbar, null);
        ImageButton saveItem = (ImageButton) view.findViewById(R.id.save_reminder_item);
        saveItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (currentMil < mil) {
                    Toast.makeText(ReminderSettingActivity.this, R.string.reminder_error,
                            Toast.LENGTH_LONG).show();
                } else {
                    com.tct.note.util.TctLog.e(TAG, "save click: " + currentMil);
                    Intent intent = new Intent();
                    intent.putExtra("reminder_time", currentMil);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        ImageButton cancleItem = (ImageButton) view.findViewById(R.id.cancle_reminder_item);
        cancleItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(view);
        actionBar.setDisplayShowHomeEnabled(false);
        
        actionBar.setIcon(R.drawable.ic_description_white);
        
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.note_action_bar_bg));

    }

    private class DateClickListener implements View.OnClickListener {
        private Time mTime;

        public DateClickListener(Time time) {
            mTime = time;
        }

        public void onClick(View v) {
        	if(dpd==null||!dateflag){
             dpd = new DatePickerDialog(ReminderSettingActivity.this,
                    new DateListener(v), mTime.year, mTime.month, mTime.monthDay);
        	}else{
        		dateflag = false;
        		dpd = new DatePickerDialog(ReminderSettingActivity.this,
                        new DateListener(v), dpd.getDatePicker().getYear(), dpd.getDatePicker().getMonth(),
                        dpd.getDatePicker().getDayOfMonth());
        	}
             
            //CalendarView cv = dpd.getDatePicker().getCalendarView();
            //cv.setShowWeekNumber(true);
            int startOfWeek = Calendar.getInstance().getFirstDayOfWeek();
            // Utils returns Time days while CalendarView wants Calendar days
            if (startOfWeek == Time.SATURDAY) {
                startOfWeek = Calendar.SATURDAY;
            } else if (startOfWeek == Time.SUNDAY) {
                startOfWeek = Calendar.SUNDAY;
            } else {
                startOfWeek = Calendar.MONDAY;
            }
            //cv.setFirstDayOfWeek(startOfWeek);
            dpd.setCanceledOnTouchOutside(true);

            //PR 826663 set reminder more than 2038 display wrong. Fixed by hz_nanbing_zou at 3/11/2014 begin
            Calendar c = Calendar.getInstance();
            c.set(2037, 11, 31, 0, 0, 0);
            dpd.getDatePicker().setMaxDate( c.getTimeInMillis());
            //PR 826663 set reminder more than 2038 display wrong. Fixed by hz_nanbing_zou at 3/11/2014 end

            dpd.show();
        }
    }

    private class DateListener implements OnDateSetListener {

        View mView;

        public DateListener(View view) {
            mView = view;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int monthDay) {
            TctLog.d(TAG, "onDateSet: " + year + " " + month + " " + monthDay);
            // Cache the member variables locally to avoid inner class overhead.
            Time startTime = mStartTime;

            // Cache the start and end millis so that we limit the number
            // of calls to normalize() and toMillis(), which are fairly
            // expensive.
            long startMillis;
            if (mView == mStartDateButton) {

                startTime.year = year;
                startTime.month = month;
                startTime.monthDay = monthDay;
                startMillis = startTime.normalize(true);

            } else {
                // The end date was changed.
                startMillis = startTime.toMillis(true);

            }

            setDate(mStartDateButton, startMillis);

            // reset
            // updateHomeTime();
        }
    }

    private void setDate(TextView view, long millis) {
        currentMil = millis;
        TctLog.e(TAG, "timeString: " + millis);
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH
                | DateUtils.FORMAT_ABBREV_WEEKDAY;

        // Unfortunately, DateUtils doesn't support a timezone other than the
        // default timezone provided by the system, so we have this ugly hack
        // here to trick it into formatting our time correctly. In order to
        // prevent all sorts of craziness, we synchronize on the TimeZone class
        // to prevent other threads from reading an incorrect timezone from
        // calls to TimeZone#getDefault()
        // TODO fix this if/when DateUtils allows for passing in a timezone
        String dateString;
        synchronized (TimeZone.class) {
            dateString = DateUtils.formatDateTime(ReminderSettingActivity.this, millis, flags);
            // setting the default back to null restores the correct behavior
        }
        TctLog.e(TAG, "dateString:  " + dateString);
        view.setText(dateString);
    }

    private class TimeClickListener implements View.OnClickListener {
        private Time mTime;

        public TimeClickListener(Time time) {
            mTime = time;
            TctLog.e(TAG, "mTime.hour: " + mTime.hour + " mTime.minute: " + mTime.minute);
        }

        @Override
        public void onClick(View v) {
        	if(tp == null || !timeflag){
            tp = new TimePickerDialog(ReminderSettingActivity.this,
                    new TimeListener(v), mTime.hour, mTime.minute,
                    DateFormat.is24HourFormat(ReminderSettingActivity.this));
        	}else{
                tp = new TimePickerDialog(ReminderSettingActivity.this,
                        new TimeListener(v), tp.onSaveInstanceState().getInt("hour", mTime.hour),
                        tp.onSaveInstanceState().getInt("minute", mTime.minute),
                        DateFormat.is24HourFormat(ReminderSettingActivity.this));

        	}
        	
            tp.setCanceledOnTouchOutside(true);
            tp.show();
        }
    }

    private class TimeListener implements OnTimeSetListener {
        private View mView;

        public TimeListener(View view) {
            mView = view;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Cache the member variables locally to avoid inner class overhead.
            Time startTime = mStartTime;

            // Cache the start and end millis so that we limit the number
            // of calls to normalize() and toMillis(), which are fairly
            // expensive.
            long startMillis;
            if (mView == mStartTimeButton) {

                startTime.hour = hourOfDay;
                startTime.minute = minute;
                startMillis = startTime.normalize(true);

            } else {
                // The end time was changed.
                startMillis = startTime.toMillis(true);

                // Call populateTimezone if we support end time zone as well
            }

            setTime(mStartTimeButton, startMillis);
            // setTime(mEndTimeButton, endMillis);
            // updateHomeTime();
        }
    }

    @SuppressWarnings("deprecation")
	private void setTime(TextView view, long millis) {
        currentMil = millis;
        TctLog.e(TAG, "timeString: " + millis);
        int flags = DateUtils.FORMAT_SHOW_TIME;
        if (DateFormat.is24HourFormat(ReminderSettingActivity.this)) {
            flags |= DateUtils.FORMAT_24HOUR;
        }

        // Unfortunately, DateUtils doesn't support a timezone other than the
        // default timezone provided by the system, so we have this ugly hack
        // here to trick it into formatting our time correctly. In order to
        // prevent all sorts of craziness, we synchronize on the TimeZone class
        // to prevent other threads from reading an incorrect timezone from
        // calls to TimeZone#getDefault()
        // TODO fix this if/when DateUtils allows for passing in a timezone
        String timeString;
        synchronized (TimeZone.class) {
            // TimeZone.setDefault(TimeZone.getTimeZone(mTimezone));
            timeString = DateUtils.formatDateTime(ReminderSettingActivity.this, millis, flags);
            TimeZone.setDefault(null);
        }
        TctLog.e(TAG, "timeString: " + timeString);
        view.setText(timeString);
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		mhandler.sendEmptyMessage(0);
	}
	//PR894251.Date(Time)PickerDialog show error when switch screen orientation. Added by hz_nanbing.zou at 07/01/2015 end

}
