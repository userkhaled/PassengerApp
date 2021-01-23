package com.eeducationgo.tectik.features.contact.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.databinding.ActivityContactBinding;
import com.eeducationgo.tectik.features.contact_us.view.ContactUsActivity;

public class ContactActivity extends AppCompatActivity {
    private ActivityContactBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ContactActivity.this ,R.layout.activity_contact);
        binding.tvContact.setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext() , ContactUsActivity.class));
        });
    }
}