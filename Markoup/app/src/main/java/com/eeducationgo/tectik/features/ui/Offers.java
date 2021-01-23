package com.eeducationgo.tectik.features.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.eeducationgo.tectik.R;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.material.button.MaterialButton;

import java.util.Date;


public class Offers extends Fragment {
    Spinner spinnerFilter;
    CardView cardDate;
    MaterialButton one, two;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_offers, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Find Booking");

        linkUi(root);

        return root;
    }

    private void linkUi(View root) {
        spinnerFilter = root.findViewById(R.id.spinner_filter);
        String[] filter = getResources().getStringArray(R.array.voyage_filter);
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, filter);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(aa);

        cardDate = root.findViewById(R.id.card_date);
        cardDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickDate();
            }
        });

        one = root.findViewById(R.id.button_book_now_1);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_offers_to_bookingFragment);

            }
        });

        two = root.findViewById(R.id.button_book_now_2);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_offers_to_bookingFragment);

            }
        });

    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {

        }

        @Override
        public void onDateTimeCancel() {

        }
    };


    public void showPickDate() {
        new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .build()
                .show();
    }

}