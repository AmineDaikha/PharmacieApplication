package com.example.pharmacieapplication.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.CalendarView;

import androidx.annotation.NonNull;

import com.example.pharmacieapplication.Activities.AddOffreActivity;
import com.example.pharmacieapplication.R;

public class DialogDate extends Dialog {

    CalendarView calendarView;
    private AddOffreActivity activity;
    private int id;

    public DialogDate(@NonNull Context context, int id) {
        super(context);
        this.id = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_date);
        calendarView = findViewById(R.id.calander);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
//                System.out.println("year : " + i);
//                System.out.println("month : " + i1);
//                System.out.println("day : " + i2);
                i1 += 1;
                String month = String.valueOf(i1), day = String.valueOf(i2);
                if (String.valueOf(i1).length() == 1) {
                    month = "0" + i1;
                }
                if (String.valueOf(i2).length() == 1) {
                    day = "0" + i2;
                }
                String date = i + "-" + month + "-" + day;
                System.out.println("date is : " + date);
                if (id == 1)
                    //activity.setDateStr1(date);
                    activity.getDateBeginOffre().setText(date);
                else if (id == 2)
                    //activity.setDateStr2(date);
                    activity.getDateEndOffre().setText(date);
                dismiss();
            }
        });
    }

    public AddOffreActivity getActivity() {
        return activity;
    }

    public void setActivity(AddOffreActivity activity) {
        this.activity = activity;
    }
}
