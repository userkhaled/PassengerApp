package com.eeducationgo.tectik.features.confirem_booking.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.fcm.json.SendNotificationThread;
import com.eeducationgo.tectik.features.cancel_booking.view.CancelBookingActivity;
import com.eeducationgo.tectik.features.confirem_booking.view.ConfiremBookingView;
import com.eeducationgo.tectik.features.find_driver.model.UserDriver;
import com.eeducationgo.tectik.util.AppShareData;
import com.eeducationgo.tectik.util.Constance;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ConfiremBookinPresenter {
    private Activity activity;
    private ConfiremBookingView view;
    private static final String TAG = "ConfiremBookinPresenter";
    private static final int REQUEST_CODE = 100;

    public ConfiremBookinPresenter(Activity activity, ConfiremBookingView view) {
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

    public void sendRequestToDriver(FirebaseAuth auth, DatabaseReference databaseReferenceRoot, UserDriver userDriver) {
        view.showProgress();
        String SUid = auth.getCurrentUser().getUid();
        String RUid = userDriver.getUid();
        try {
            databaseReferenceRoot.child(Constance.RootBooking).child(SUid).child(RUid)
                    .child(Constance.ChildBookingRequestType)
                    .setValue(Constance.ChildBookingRequestSend)
                    .addOnSuccessListener((task) -> {
                        databaseReferenceRoot.child(Constance.RootBooking).child(RUid).child(SUid)
                                .child(Constance.ChildBookingRequestType)
                                .setValue(Constance.ChildBookingRequestReceiver)
                                .addOnSuccessListener(aVoid -> {
                                    view.hideProgress();
                                    view.showSuccessMessage(activity.getString(R.string.success_process));
                                    SendNotificationThread.getInstance().sendNotificationTo(AppShareData.getUserName(),
                                            "Send to you booking request",
                                            userDriver.getToken(),
                                            "");
                                    activity.startActivity(new Intent(activity, CancelBookingActivity.class)
                                            .putExtra(Constance.driverKey, userDriver));
                                }).addOnFailureListener(e -> {
                            view.hideProgress();
                            view.showErrorMessage(e.toString());
                        });

                    }).addOnFailureListener(e -> {
                view.hideProgress();
                view.showErrorMessage(e.toString());
            });
        } catch (Exception e) {
            e.printStackTrace();
            view.hideProgress();
            view.showErrorMessage(e.toString());
        }

    }
}
