package com.eeducationgo.tectik.features.confirem_booking.view;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.databinding.ActivityConfiremBookingBinding;
import com.eeducationgo.tectik.features.confirem_booking.presenter.ConfiremBookinPresenter;
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

public class ConfiremBookingActivity extends BaseActivity implements OnMapReadyCallback, ConfiremBookingView {
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    private ActivityConfiremBookingBinding binding;
    private ConfiremBookinPresenter presenter;
    private UserDriver userDriver;
    private FirebaseAuth auth;
    private DatabaseReference databaseReferenceRoot;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 100;
    private static final String TAG = "HomeFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ConfiremBookingActivity.this, R.layout.activity_confirem_booking);

        init();
        initListener();

    }

    private void initListener() {
        ConfiermBtn();
    }


    private void ConfiermBtn() {

        binding.buttonConfirmBooking.setOnClickListener(v -> presenter.sendRequestToDriver(auth, databaseReferenceRoot, userDriver));

    }

    private void init() {
        userDriver = getIntent().getParcelableExtra(Constance.driverKey);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        presenter = new ConfiremBookinPresenter(this, this);
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
            Log.d(TAG, "onMapReady: "+userDriver.getLate());
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
        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude())));
        if (userDriver == null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }

    }
}