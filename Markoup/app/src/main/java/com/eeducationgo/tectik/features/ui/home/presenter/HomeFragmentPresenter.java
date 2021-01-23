package com.eeducationgo.tectik.features.ui.home.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.features.find_driver.view.FindDriverActivity;
import com.eeducationgo.tectik.features.ui.home.view.HomeFragment;
import com.eeducationgo.tectik.features.ui.home.view.HomeFragmentView;
import com.eeducationgo.tectik.util.Constance;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragmentPresenter {
    private HomeFragment fragment;
    private HomeFragmentView view;
    private static final int REQUEST_CODE = 100;
    private static final String TAG = "HomeFragmentPresenter";

    public HomeFragmentPresenter(HomeFragment fragment, HomeFragmentView view) {
        this.fragment = fragment;
        this.view = view;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(fragment.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        } else {
            return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public void getLastKnownLocation(FusedLocationProviderClient fusedLocationProviderClient) {

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                //TODO work
                location.getLatitude();
                location.getLongitude();
                Log.d(TAG, "getLastKnownLocation: " + location.getLatitude() + " \n" + location.getLongitude());
                //showLocationInTextView(location);
                view.setLocation(location);
            }
        });
    }


    public void updateDestination(DatabaseReference databaseReferencePassanger, String destination, String station,
                                  String tripTime, String tripDate) {
        if (TextUtils.isEmpty(station)) {
            view.showErrorMessage(fragment.getString(R.string.choose_trip_time));
            return;
        }
        if (TextUtils.isEmpty(destination)) {
            view.showErrorMessage(fragment.getString(R.string.enter_destination));
            return;
        }
        if (TextUtils.isEmpty(tripTime)) {
            view.showErrorMessage(fragment.getString(R.string.choose_trip_time));
            return;
        }
        if (TextUtils.isEmpty(tripDate)) {
            view.showErrorMessage(fragment.getString(R.string.choose_trip_time));
            return;
        }

        update(databaseReferencePassanger, destination, station, tripTime, tripDate);
    }

    private void update(DatabaseReference databaseReferencePassanger, String destination, String station, String tripTime, String tripDate) {
        view.showProgress();
        Map<String, Object> map = new HashMap<>();
        map.put(Constance.ChildPassengerDestination, destination);
        map.put(Constance.ChildPassengerStation, station);
        map.put(Constance.ChildPassengerTripTime, tripTime);
        map.put(Constance.ChildPassengerTripDate, tripDate);

        try {
            databaseReferencePassanger.updateChildren(map)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            view.hideProgress();
                            view.showErrorMessage(task.toString());
                            return;
                        }
                        view.hideProgress();
                        view.showSuccessMessage(fragment.getString(R.string.success_process));
                        //todo intent
                        fragment.startActivity(new Intent(fragment.getContext(), FindDriverActivity.class));
                    }).
                    addOnFailureListener(e -> {
                        view.hideProgress();
                        view.showErrorMessage(e.toString());
                    });
        } catch (Exception e) {
            e.printStackTrace();
            view.hideProgress();
            view.showErrorMessage(e.toString());
        }
    }

    public void updateLocation(DatabaseReference databaseReferencePassanger, Location location) {
       // view.showProgress();
        Map<String, Object> map = new HashMap<>();
        map.put(Constance.ChildPassengerLate, location.getLatitude());
        map.put(Constance.ChildPassengerLong, location.getLongitude());
        try {
            databaseReferencePassanger
                    .updateChildren(map)
                    .addOnSuccessListener(aVoid -> {
                        view.hideProgress();
                    })
                    .addOnFailureListener(e -> {
                        view.hideProgress();
                        view.showErrorMessage(e.toString());
                    });
        } catch (Exception e) {
            e.printStackTrace();
            view.hideProgress();
            view.showErrorMessage(e.toString());
        }
    }

    public void setDate(TextView tvDate) {
        tvDate.setText(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(new Date()));
    }
}
