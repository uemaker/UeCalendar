package com.uemaker.uecalendarsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.uemaker.uecalendar.UeCalendar;
import com.uemaker.uecalendar.util.CustomDate;
import com.uemaker.uecalendarspmple.R;

public class MainActivity extends AppCompatActivity implements UeCalendar.CalendarListener {

    private UeCalendar ueCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ueCalendar = findViewById(R.id.ue_calendar);

        ueCalendar.setCalendarListener(this);

    }

    @Override
    public void onClickDate(CustomDate date) {
        Log.e("app","date:"+date.toString());
    }

    @Override
    public void onChangeMonth(CustomDate date) {

    }
}
