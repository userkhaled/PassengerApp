package com.eeducationgo.tectik.features.ui.home.view;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.databinding.FragmentHomeBinding;
import com.eeducationgo.tectik.features.ui.home.presenter.HomeFragmentPresenter;
import com.eeducationgo.tectik.util.BaseFragment;
import com.eeducationgo.tectik.util.Constance;
import com.eeducationgo.tectik.util.ProgressBarDialog;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends BaseFragment implements OnMapReadyCallback, HomeFragmentView {

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    private FragmentHomeBinding binding;
    private SimpleDateFormat mFormatterTime = new SimpleDateFormat("MMMM dd yyyy");
    private SimpleDateFormat mFormatterDate = new SimpleDateFormat("hh:mm aa");
    private String station, destination, tripTime, tripDate;
    private ProgressBarDialog dialog;
    private HomeFragmentPresenter presenter;
    private DatabaseReference databaseReferencePassanger;
    private FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 100;
    private static final String TAG = "HomeFragment";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);


        init();
        initData();
        initListener();
        return binding.getRoot();
    }

    private void initData() {
        presenter.setDate(binding.tvDate);
    }

    private void initListener() {
        btnFindDriver();
        btnShowPicker();
        spinnerStation();
        spinnerDestination();
    }

    private void spinnerDestination() {
        binding.spinnerDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destination = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void spinnerStation() {
        binding.spinnerStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                station = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void btnShowPicker() {
        binding.cardDate.setOnClickListener(v -> showPickDate());

    }

    private void btnFindDriver() {
        binding.buttonFindDriver.setOnClickListener(view -> {
            presenter.updateDestination(databaseReferencePassanger, destination, station, tripTime, tripDate);
        });
    }

    private void init() {
        dialog = new ProgressBarDialog(getContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);
        presenter = new HomeFragmentPresenter(this, this);
        auth = FirebaseAuth.getInstance();
        databaseReferencePassanger = FirebaseDatabase.getInstance().getReference()
                .child(Constance.RootPassenger).child(auth.getCurrentUser().getUid());
        databaseReferencePassanger.keepSynced(true);

    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            tripTime = mFormatterTime.format(date).toString();
            tripDate = mFormatterDate.format(date).toString();
        }

        @Override
        public void onDateTimeCancel() {

        }
    };

    public void showPickDate() {
        new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setIs24HourTime(false)
                .build()
                .show();
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
    public void showProgress() {
        super.showProgress();
        dialog.show();
    }

    @Override
    public void hideProgress() {
        super.hideProgress();
        dialog.dismiss();
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
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        presenter.updateLocation(databaseReferencePassanger ,location);
    }
}