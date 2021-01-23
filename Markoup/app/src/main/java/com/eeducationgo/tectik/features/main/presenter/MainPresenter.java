package com.eeducationgo.tectik.features.main.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.features.account.view.AccountActivity;
import com.eeducationgo.tectik.features.contact.view.ContactActivity;
import com.eeducationgo.tectik.features.main.view.MainView;
import com.eeducationgo.tectik.features.registration.model.User;
import com.eeducationgo.tectik.features.registration.view.Registration;
import com.eeducationgo.tectik.features.varifcation.view.VerificationActivity;
import com.eeducationgo.tectik.util.AppShareData;
import com.eeducationgo.tectik.util.Constance;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;


public class MainPresenter {
    private Activity activity;
    private MainView view;
    private static final String TAG = "MainPresenter";

    public MainPresenter(Activity activity, MainView view) {
        this.activity = activity;
        this.view = view;
    }

    public void drawerListener(NavigationView navigationView, DrawerLayout drawer, NavController navController) {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_my_account:
                    activity.startActivity(new Intent(activity, AccountActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_invite_friends:
                    view.showProgress();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "this is my uber app";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "uber demo");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    view.hideProgress();
                    drawer.closeDrawer(GravityCompat.START);
                    return true;

                case R.id.verification_state:
                    //
                    activity.startActivity(new Intent(activity, VerificationActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_customerService:
                    activity.startActivity(new Intent(activity, ContactActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    activity.startActivity(new Intent(activity, Registration.class));
                    activity.finish();
                    drawer.closeDrawer(GravityCompat.START);
                    return true;

            }
            NavigationUI.onNavDestinationSelected(menuItem, navController);
            //drawer.setVisibility(View.GONE);
            drawer.closeDrawer(GravityCompat.START);


            return true;
        });
    }

    public void setData(ImageView imageViewProfile, TextView textViewName, TextView textViewPhone, User user) {

        if (user == null) {
            return;
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_);
        Glide.with(activity).load(user.getImage()).apply(requestOptions).into(imageViewProfile);
        textViewName.setText(user.getName());
        String phone = TextUtils.isEmpty(user.getPhone()) ? user.getEmail() : user.getPhone();
        textViewPhone.setText(phone);
        AppShareData.setUserName(user.getName());
    }

    public void sendToken(DatabaseReference databaseReferencePassenger) {

        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        Map<String, Object> map = new HashMap<>();
                        map.put(Constance.ChildPassengerToken, token);
                        databaseReferencePassenger.updateChildren(map)
                                .addOnSuccessListener(aVoid -> {

                                })
                                .addOnFailureListener(e -> view.showErrorMessage(e.toString()));
                    });
        } catch (Exception e) {
            e.printStackTrace();
            view.showErrorMessage(e.toString());
        }
    }

    public void initProfiel(DatabaseReference databaseReferencePassenger, ImageView imageViewProfile, TextView textViewName, TextView textViewPhone) {
        try {
            databaseReferencePassenger.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.default_);
                    Glide.with(activity).load(user.getImage()).apply(requestOptions).into(imageViewProfile);
                    textViewName.setText(user.getName());
                    String phone = TextUtils.isEmpty(user.getPhone()) ? user.getEmail() : user.getPhone();
                    textViewPhone.setText(phone);
                    AppShareData.setUserName(user.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
