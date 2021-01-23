package com.eeducationgo.tectik.features.cancel_booking.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.databinding.ActivityCancelBookingBinding;
import com.eeducationgo.tectik.features.cancel_booking.presenter.CancelBookingPresenter;
import com.eeducationgo.tectik.features.find_driver.model.UserDriver;
import com.eeducationgo.tectik.util.BaseActivity;
import com.eeducationgo.tectik.util.Constance;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CancelBookingActivity extends BaseActivity implements OnMapReadyCallback, CancelBookingView {

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    private ActivityCancelBookingBinding binding;
    private UserDriver userDriver;
    private CancelBookingPresenter presenter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 100;
    private static final String TAG = "CancelBookingActivity";
    private FirebaseAuth auth;
    private DatabaseReference databaseReferenceRoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(CancelBookingActivity.this, R.layout.activity_cancel_booking);

        init();
        initData();
        initListener();
    }

    private void initData() {
        presenter.initData(userDriver, binding.profileImage, binding.tvEnd, binding.tvFrom,
                binding.tvName, binding.tvTripTime, binding.tvVehichleType, binding.tvVehichleNumber);
    }

    private void initListener() {
        callBtn();
        cancelBtn();
    }

    private void cancelBtn() {
        binding.buttonCancellBooking.setOnClickListener(v -> {
            presenter.cancelBooking(databaseReferenceRoot , auth ,userDriver);
        });
    }

    private void callBtn() {
        binding.btnBook.setOnClickListener(v -> {
            String phone = TextUtils.isEmpty(userDriver.getPhone()) ? "000" : userDriver.getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        });

    }

    private void init() {
        userDriver = getIntent().getParcelableExtra(Constance.driverKey);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        presenter = new CancelBookingPresenter(this, this);
        auth = FirebaseAuth.getInstance();
        databaseReferenceRoot = FirebaseDatabase.getInstance().getReference();
        databaseReferenceRoot.keepSynced(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        mGoogleMap.setIndoorEnabled(true);
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        if (userDriver != null) {
            Log.d(TAG, "onMapReady: " + userDriver.getLate());
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(userDriver.getLate(), userDriver.getLong())));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userDriver.getLate(), userDriver.getLong()), 15));
        }
    }

    @Override
    public void showErrorMessage(String message) {
        super.showErrorMessage(message);
        snackErrorShow(binding.getRoot(), message);
    }

    @Override
    public void showSuccessMessage(String message) {
        super.showSuccessMessage(message);
        snackSuccessShow(binding.getRoot(), message);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (checkLocationPermission()) {
            getLastKnownLocation();
        }
    }

    private boolean checkLocationPermission() {
        return presenter.checkLocationPermission();
    }//end

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO:DONE
                    getLastKnownLocation();
                }
            }
            return;
        }
    }//end

    private void getLastKnownLocation() {
        presenter.getLastKnownLocation(fusedLocationProviderClient);
    }//end

    @Override
    public void setLocation(Location location) {
        Log.d(TAG, "setLocation: " + location.getLongitude() + " " + location.getLatitude());
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
        if (userDriver == null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }
    }
}