package com.eeducationgo.tectik.features.account.view;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CompoundButton;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.databinding.ActivityAccountBinding;
import com.eeducationgo.tectik.features.dialog.edit.view.EditDialogFragment;
import com.eeducationgo.tectik.features.account.presenter.AccountPresenter;
import com.eeducationgo.tectik.util.BaseActivity;
import com.eeducationgo.tectik.util.Constance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountActivity extends BaseActivity implements AccountView {
    private ActivityAccountBinding binding;
    private AccountPresenter presenter;
    private FirebaseAuth auth;
    private DatabaseReference databaseReferenceDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_account);

        binding = DataBindingUtil.setContentView(AccountActivity.this, R.layout.activity_account);

        init();
        initData();
        initListener();

    }

    private void initData() {
        presenter.setViewData(databaseReferenceDriver , binding.tvEmailUser , binding.tvNameUser
                ,binding.tvPhoneUser );
    }

    private void initListener() {
        editBtn();
        gpsBtn();
    }

    private void gpsBtn() {
        binding.swBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });
    }

    private void editBtn() {
        binding.tvEdit.setOnClickListener(v -> {
            EditDialogFragment editDialogFragment = new EditDialogFragment();
            editDialogFragment.show(getSupportFragmentManager() , editDialogFragment.getTag());
        });
    }

    private void init() {
        presenter = new AccountPresenter(this , this);
        auth = FirebaseAuth.getInstance();
        databaseReferenceDriver = FirebaseDatabase.getInstance().getReference()
                .child(Constance.RootPassenger).child(auth.getCurrentUser().getUid());
        databaseReferenceDriver.keepSynced(true);
    }
}