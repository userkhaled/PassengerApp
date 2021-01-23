package com.eeducationgo.tectik.features.ui.home.view;

import android.location.Location;

import com.eeducationgo.tectik.util.BaseView;

public interface HomeFragmentView extends BaseView {
    void setLocation(Location location);
}
