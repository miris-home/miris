package com.miris.ui.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.miris.R;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by fantastic on 2015-11-06.
 */
public class TakeCalendarActivity extends BaseActivity{
    @InjectView(R.id.tbFollowers)
    ToggleButton tbFollowers;
    @InjectView(R.id.tbDirect)
    ToggleButton tbDirect;
    @InjectView(R.id.setsend)
    Button setsend;
    @InjectView(R.id.setcancel)
    Button setcancel;
    @InjectView(R.id.setdate)
    Button setdate;
    @InjectView(R.id.edt_message)
    EditText edt_message;

    private boolean propagatingToggleState = false;
    int mYear, mMonth, mDay;
    ProgressDialog myLoadingDialog;
    String newWritingPublic = "Y";
    Date today;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_calendar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        today = cal.getTime();
        UpdateNow();
    }

    @OnCheckedChanged(R.id.tbFollowers)
    public void onFollowersCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbDirect.setChecked(!checked);
            newWritingPublic = "N";
            propagatingToggleState = false;
        }
    }

    @OnCheckedChanged(R.id.tbDirect)
    public void onDirectCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbFollowers.setChecked(!checked);
            newWritingPublic = "Y";
            propagatingToggleState = false;
        }
    }

    @OnClick(R.id.setsend)
    public void onsetsendClick(final View v) {
        if (edt_message.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.calendar_text_check), Toast.LENGTH_SHORT).show();
        } else {
            hideSoftInputWindow(v);
            new setCalendarDataTask().execute();
        }
    }

    @OnClick(R.id.setcancel)
    public void onsetcancelClick(final View v) {
        finish();
    }

    @OnClick(R.id.setdate)
    public void onsetdateClick(final View v) {
        new DatePickerDialog(TakeCalendarActivity.this, mDateSetListener, mYear,
                mMonth, mDay).show();

    }
    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    cal = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                    today = cal.getTime();
                    UpdateNow();
                }
            };

    void UpdateNow(){
        setdate.setText(String.format("%d년 %d월 %d일", mYear,
                mMonth + 1, mDay));
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(TakeCalendarActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    class setCalendarDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            showDialog();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ParseObject calendarObject = new ParseObject("miris_schedule");
            calendarObject.put("user_id", memberData.get(0).getuserId());
            calendarObject.put("user_name", memberData.get(0).getuser_name());
            calendarObject.put("user_text", edt_message.getText().toString());
            calendarObject.put("user_calendar", today);
            calendarObject.put("user_public", newWritingPublic);
            calendarObject.put("user_holiday", "N");

            calendarObject.saveInBackground();
            calendarObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.save_toast), Toast.LENGTH_SHORT).show();
                        bringCalendarActivityToTop();
                    }
                }
            });
            return null ;
        }
    }

    private void bringCalendarActivityToTop() {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(CalendarActivity.ACTION_SHOW_NEW_ITEM);
        startActivity(intent);
    }
}
