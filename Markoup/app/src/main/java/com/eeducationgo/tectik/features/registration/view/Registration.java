package com.eeducationgo.tectik.features.registration.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.databinding.ActivityRegistrationBinding;
import com.eeducationgo.tectik.features.main.view.MainActivity;
import com.eeducationgo.tectik.features.registration.model.User;
import com.eeducationgo.tectik.features.name.view.NameActivity;
import com.eeducationgo.tectik.features.phone.view.PhoneActivity;
import com.eeducationgo.tectik.features.registration.presenter.RegistrationPresenter;
import com.eeducationgo.tectik.util.BaseActivity;
import com.eeducationgo.tectik.util.Constance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.eeducationgo.tectik.util.Constance.userKey;

public class Registration extends BaseActivity implements RegistrationView {

    private ActivityRegistrationBinding binding;
    private RegistrationPresenter presenter;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReferenceUser;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(Registration.this, R.layout.activity_registration);

        //linkUp();

        init();
        initListener();
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        presenter = new RegistrationPresenter(this, this);

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(Constance.RootPassenger);
    }

    private void initListener() {
        loginBtn();
        newDriverBtn();
        companyBtn();
    }

    private void companyBtn() {
        binding.buttonRegisterNewUser.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), PhoneActivity.class)));
    }

    private void newDriverBtn() {
        binding.buttonRegisterCompany.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), PhoneActivity.class)));
    }

    private void loginBtn() {
        binding.buttonLogin.setOnClickListener(v -> {
            presenter.validationInput(binding.inputUsername, binding.inputPassword, auth);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) {
            return;
        } else {
            try {
                uid = user.getUid();
                databaseReferenceUser.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() ) {
                            User user = snapshot.getValue(User.class);
                            startActivity(new Intent(getBaseContext(), MainActivity.class).putExtra(userKey, user));
                            finish();
                        } else {
                            startActivity(new Intent(getBaseContext(), NameActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        snackErrorShow(binding.getRoot(), error.toString());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                snackErrorShow(binding.getRoot(), e.toString());
            }

        }
    }

    @Override
    public void showErrorMessage(String message) {
        super.showErrorMessage(message);
        snackErrorShow(binding.getRoot(), message);
    }

    @Override
    public void showSuccessMessage(String message) {
        snackSuccessShow(binding.getRoot(), message);
    }


}