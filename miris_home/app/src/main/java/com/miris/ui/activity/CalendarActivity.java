package com.miris.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.miris.R;
import com.miris.net.CalendarListData;
import com.miris.ui.adapter.CalendarAdapter;
import com.miris.ui.comp.CalendarDayEvent;
import com.miris.ui.view.CompactCalendarView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Miris on 09.02.15.
 */
public class CalendarActivity extends BaseActivity {
    public static final String ACTION_SHOW_NEW_ITEM = "action_show_new_item";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy년 MMM", Locale.getDefault());

    @InjectView(R.id.rvCalendar)
    RecyclerView rvCalendar;
    @InjectView(R.id.toDate)
    TextView toDate;
    @InjectView(R.id.btnCreate)
    FloatingActionButton fabCreate;

    LinearLayoutManager linearLayoutManager;
    private CalendarAdapter calendarAdapter;
    ProgressDialog myLoadingDialog;
    List<ParseObject> ob;
    Date today;
    Calendar cal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setupFeed();
    }

    private void setupFeed() {
        linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvCalendar.setLayoutManager(linearLayoutManager);
        new loadDataTask().execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_NEW_ITEM.equals(intent.getAction())) {
            calendarData.clear();
            new loadDataTask().execute();
        }
    }

    class loadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            calendarData = new ArrayList<CalendarListData>();

            ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_schedule");
            offerQuery.orderByDescending("createdAt");

            try {
                ob = offerQuery.find();
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            for (ParseObject country : ob) {
                if (isCancelled()) {
                    return null;
                }
                if (country.get("user_public").toString().equals("N")) {
                    if (!country.get("user_id").toString().equals(memberData.get(0).getuserId())) {
                        continue;
                    }
                }
                calendarData.add(new CalendarListData(
                        country.get("user_id").toString(),
                        country.get("user_name").toString(),
                        country.getDate("user_calendar"),
                        country.get("user_text").toString(),
                        country.get("user_public").toString()));
            }
            return null ;
        }
        @Override
        protected void onPostExecute(Void result) {
            final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
            compactCalendarView.drawSmallIndicatorForEvents(true);
            addEvents(compactCalendarView);
            compactCalendarView.invalidate();
            final Button showPreviousMonthBut = (Button) findViewById(R.id.prev_button);
            final Button showNextMonthBut = (Button) findViewById(R.id.next_button);

            calendarAdapter = new CalendarAdapter(CalendarActivity.this, calendarData);
            rvCalendar.setAdapter(calendarAdapter);


            toDate.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

                @Override
                public void onDayClick(Date dateClicked) {
                    calendarAdapter.getFilter(dateClicked);
                }

                @Override
                public void onMonthScroll(Date firstDayOfNewMonth) {
                    toDate.setText(dateFormatForMonth.format(firstDayOfNewMonth));
                }
            });

            showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compactCalendarView.showPreviousMonth();
                }
            });

            showNextMonthBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compactCalendarView.showNextMonth();
                }
            });
            calendarAdapter.updateItems(true);
        }
    }

    private void addEvents(CompactCalendarView compactCalendarView) {
        for(int i = 0; i < calendarData.size(); i++){
            compactCalendarView.addEvent(new CalendarDayEvent( calendarData.get(i).getuser_calendar().getTime(), Color.argb(255, 239, 68, 65)), false);
        }
        cal = new GregorianCalendar();
        int[][] holidays = {
                {cal.get(Calendar.YEAR), 0, 1},
                {cal.get(Calendar.YEAR), 2, 1},
                {cal.get(Calendar.YEAR), 4, 5},
                {cal.get(Calendar.YEAR), 5, 6},
                {cal.get(Calendar.YEAR), 7, 15},
                {cal.get(Calendar.YEAR), 9, 3},
                {cal.get(Calendar.YEAR), 9, 9},
                {cal.get(Calendar.YEAR), 11, 25},
        };

        String[] holidaysName = {
                "신정",
                "삼일절",
                "어린이날",
                "현충일",
                "광복절",
                "개천절",
                "한글날",
                "성탄절",
        };
        for(int i = 0; i < holidays.length; i++){
            cal = new GregorianCalendar(holidays[i][0],holidays[i][1],holidays[i][2]);
            today = cal.getTime();
            calendarData.add(new CalendarListData(
                    "Defult",
                    "공휴일",
                    today,
                    holidaysName[i],
                    "Y"));
            compactCalendarView.addEvent(new CalendarDayEvent(today.getTime(), Color.argb(255, 239, 68, 65)), false);
        }
    }

    @OnClick(R.id.btnCreate)
    public void onTakePhotoClick() {
        Intent intent = new Intent(CalendarActivity.this, TakeCalendarActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
