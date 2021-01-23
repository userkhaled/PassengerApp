package com.eeducationgo.tectik.features.name.view;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.databinding.ActivityNameBinding;
import com.eeducationgo.tectik.features.name.presenter.NamePresenter;
import com.eeducationgo.tectik.util.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.eeducationgo.tectik.util.Constance.RootPassenger;

public class NameActivity extends BaseActivity implements NameView {

    private ActivityNameBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference databaseReferenceName;
    private NamePresenter presenter;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(NameActivity.this, R.layout.activity_name);

        init();
        initListeners();

    }

    private void initListeners() {
        nextBtn();
    }

    private void nextBtn() {

        binding.buttonNextEmailRegisteration.setOnClickListener(v -> {
            presenter.createNameUser(binding.inputUserEmail , auth , databaseReferenceName);
            //startActivity(new Intent(getBaseContext(), CarTypeActivity.class));
        });
    }

    private void init() {
        presenter = new NamePresenter(this, this);
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        databaseReferenceName = FirebaseDatabase.getInstance().getReference().child(RootPassenger).child(uid);
        databaseReferenceName.keepSynced(true);
    }

    @Override
    public void showSuccessMessage(String message) {
        super.showSuccessMessage(message);
        snackSuccessShow(binding.getRoot() , message);
    }

    @Override
    public void showErrorMessage(String message) {
        super.showErrorMessage(message);
        snackErrorShow(binding.getRoot() , message);
    }
}