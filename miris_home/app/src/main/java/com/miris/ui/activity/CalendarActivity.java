package com.miris.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import android.support.v4.app.FragmentActivity;

import com.miris.R;

/**
 * Created by Miris on 09.02.15.
 */
public class CalendarActivity extends FragmentActivity {

    private static final String TAG = MConfig.TAG;
    private static final String NAME = "CalendarActivity";
    private final String CLASS = NAME + "@" + Integer.toHexString(hashCode());

    TextView thisMonthTv;
    int current, mSelectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        thisMonthTv = (TextView) findViewById(R.id.this_month_tv);

        MonthlyFragment mf = (MonthlyFragment) getSupportFragmentManager().findFragmentById(R.id.monthly);
        mf.setOnMonthChangeListener(new MonthlyFragment.OnMonthChangeListener() {

            @Override
            public void onChange(int year, int month) {
                HLog.d(TAG, CLASS, "onChange " + year + "." + month);
                thisMonthTv.setText(year + "." + (month + 1));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
