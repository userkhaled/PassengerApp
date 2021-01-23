package com.eeducationgo.tectik.features.cancel_booking.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.features.cancel_booking.view.CancelBookingView;
import com.eeducationgo.tectik.features.find_driver.model.UserDriver;
import com.eeducationgo.tectik.util.Constance;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class CancelBookingPresenter {
    private Activity activity;
    private CancelBookingView view;
    private static final String TAG = "CancelBookingPresenter";
    private static final int REQUEST_CODE = 100;

    public CancelBookingPresenter(Activity activity, CancelBookingView view) {
        this.activity = activity;
        this.view = view;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

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

    public void initData(UserDriver userDriver, CircleImageView profileImage, TextView tvEnd, TextView tvFrom, TextView tvName, TextView tvTripTime, TextView tvVehichleType, TextView tvVehichleNumber) {
        view.showProgress();
        if (userDriver == null) {
            view.hideProgress();
            return;
        }
        Glide.with(activity).load(userDriver.getImage())
                .apply(new RequestOptions().placeholder(R.drawable.holder)).into(profileImage);
        tvEnd.setText(userDriver.getDestination());
        tvFrom.setText(userDriver.getStation());
        tvName.setText(userDriver.getName());
        tvTripTime.setText("Trip time :  " + userDriver.getTripTime() + " " + userDriver.getTripDate());
        tvVehichleNumber.setText("Vehicle Number :  " + userDriver.getVehicleNumber());
        tvVehichleType.setText("Vehicle Type :  " + userDriver.getVehicleType());
        view.hideProgress();
    }

    public void cancelBooking(DatabaseReference databaseReferenceRoot,
                              FirebaseAuth auth, UserDriver userDriver) {
        view.showProgress();
        try {
            databaseReferenceRoot.child(Constance.RootBooking).child(auth.getCurrentUser().getUid())
                    .child(userDriver.getUid())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        databaseReferenceRoot.child(Constance.RootBooking)
                                .child(userDriver.getUid())
                                .child(auth.getCurrentUser().getUid())
                                .removeValue()
                                .addOnSuccessListener(aVoid1 -> {
                                    view.hideProgress();
                                    view.showSuccessMessage(activity.getString(R.string.success_process));
                                    activity.finish();
                                })
                                .addOnFailureListener(e -> {
                                    view.hideProgress();
                                    view.showErrorMessage(e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        view.hideProgress();
                        view.showErrorMessage(e.getMessage());
                    });
        } catch (Exception e) {
            e.printStackTrace();
            view.hideProgress();
            view.showErrorMessage(e.getMessage());
        }
    }
}
