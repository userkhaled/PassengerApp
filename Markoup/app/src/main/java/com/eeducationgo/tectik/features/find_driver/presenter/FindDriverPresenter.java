package com.eeducationgo.tectik.features.find_driver.presenter;

import android.app.Activity;
import android.widget.TextView;

import com.eeducationgo.tectik.features.find_driver.view.FindDriverView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FindDriverPresenter {
    private Activity activity;
    private FindDriverView view;

    public FindDriverPresenter(Activity activity, FindDriverView view) {
        this.activity = activity;
        this.view = view;
    }

    public void setDate(TextView tvDate) {
        String format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(new Date());
        tvDate.setText(format);
    }
}
