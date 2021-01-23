package com.eeducationgo.tectik.features.main.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.features.main.presenter.MainPresenter;
import com.eeducationgo.tectik.features.registration.model.User;
import com.eeducationgo.tectik.util.BaseActivity;
import com.eeducationgo.tectik.util.Constance;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends BaseActivity implements MainView {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavController navController;
    private ImageView imageViewProfile;
    private TextView textViewName, textViewPhone;
    private MainPresenter presenter;
    private Toolbar toolbar;
    private User user;
    private NavigationView navigationView;
    private View hView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReferencePassenger;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setUpBars();
        initData();
        sendToken();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void sendToken() {
        if (auth.getCurrentUser() != null){
            presenter.sendToken(databaseReferencePassenger);
        }else{

        }
    }

    private void initData() {

        if (user == null) {
            presenter.initProfiel(databaseReferencePassenger ,imageViewProfile, textViewName, textViewPhone);
        } else {
            presenter.setData(imageViewProfile, textViewName, textViewPhone, user);
        }
    }

    private void init() {
        user = getIntent().getParcelableExtra(Constance.userKey);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        databaseReferencePassenger = FirebaseDatabase.getInstance().getReference()
                .child(Constance.RootPassenger).child(auth.getCurrentUser().getUid());
        navigationView = findViewById(R.id.nav_view);
        presenter = new MainPresenter(this, this);
        hView = navigationView.getHeaderView(0);
        imageViewProfile = hView.findViewById(R.id.profile_image_home);
        textViewName = hView.findViewById(R.id.tv_name_home);
        textViewPhone = hView.findViewById(R.id.tv_phone_home);
        databaseReferencePassenger.keepSynced(true);
    }

    private void setUpBars() {

        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navListener(navigationView);

    }

    private void navListener(NavigationView navigationView) {
        presenter.drawerListener(navigationView, drawer, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}