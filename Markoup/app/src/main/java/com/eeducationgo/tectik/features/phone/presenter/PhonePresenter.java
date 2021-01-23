package com.eeducationgo.tectik.features.phone.presenter;

import android.app.Activity;

import com.eeducationgo.tectik.features.phone.view.PhoneView;

public class PhonePresenter {
    private Activity activity;
    private PhoneView view;

    public PhonePresenter(Activity activity, PhoneView view) {
        this.activity = activity;
        this.view = view;
    }
}
