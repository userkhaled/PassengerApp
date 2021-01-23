package com.eeducationgo.tectik.features.find_driver.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.databinding.ActivityFindDriverBinding;
import com.eeducationgo.tectik.features.confirem_booking.view.ConfiremBookingActivity;
import com.eeducationgo.tectik.features.find_driver.model.UserDriver;
import com.eeducationgo.tectik.features.find_driver.presenter.FindDriverPresenter;
import com.eeducationgo.tectik.util.BaseActivity;
import com.eeducationgo.tectik.util.Constance;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FindDriverActivity extends BaseActivity implements FindDriverView {

    private ActivityFindDriverBinding binding;
    private DatabaseReference databaseReferenceDriver, databaseReferenceRoot;
    private FindDriverPresenter presenter;
    private String RootDriver = "Driver";
    private static final String TAG = "FindDriverActivity";
    private SimpleDateFormat mFormatterTime = new SimpleDateFormat("MMMM dd yyyy");
    private SimpleDateFormat mFormatterDate = new SimpleDateFormat("hh:mm aa");
    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(FindDriverActivity.this, R.layout.activity_find_driver);

        init();
        initData();
        initListener();
    }

    private void initData() {
        presenter.setDate(binding.tvDate);
       // date = DateFormat.getDateInstance(DateFormat.DEFAULT).format(new Date());
        Log.d(TAG, "initData: "+date);
    }

    private void init() {
        presenter = new FindDriverPresenter(this, this);
        databaseReferenceDriver = FirebaseDatabase.getInstance().getReference().child(RootDriver);
        databaseReferenceRoot = FirebaseDatabase.getInstance().getReference().child(Constance.RootBookingList);
        databaseReferenceDriver.keepSynced(true);
        databaseReferenceRoot.keepSynced(true);
        binding.rvDriver.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        binding.rvDriver.setHasFixedSize(true);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UserDriver, OfferAdapter> adapter = new FirebaseRecyclerAdapter<UserDriver, OfferAdapter>(
                UserDriver.class,
                R.layout.item_offer,
                OfferAdapter.class,
                databaseReferenceDriver/*.child("tripDate").equalTo(date)*/
        ) {
            @Override
            protected void populateViewHolder(OfferAdapter holder, UserDriver user, int i) {
                boolean available = user.isAvailable();
                String key = getRef(i).getKey();
                String date = DateFormat.getDateTimeInstance().format(new Date());
                String type = TextUtils.isEmpty(user.getVehicleType()) ? "" : user.getVehicleType();
                Log.d(TAG, "populateViewHolder: o" + available + " " + user.isAvailable());

                holder.itemView.setVisibility(View.VISIBLE);
                holder.setTextViewName(user.getName());
                holder.setTextViewCarType("Viechle type : " + type);
                holder.setTextViewDate("trip date and time : " + date);
                holder.setImageViewUser(user.getImage());
                holder.setTextViewTime("" + user.getNumberSeats());
                holder.buttonBook.setOnClickListener(v -> {
                    startActivity(new Intent(getBaseContext(), ConfiremBookingActivity.class).putExtra(Constance.driverKey, user));
                });
                String s = TextUtils.isEmpty(user.getNumberSeats()) ? "0" : user.getNumberSeats();
                String numberSeats = s;
                long seats = Long.parseLong(numberSeats);

                databaseReferenceDriver.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean isAvailable = (Boolean) snapshot.child("IsAvailable").getValue();
                        Log.d(TAG, "onDataChange: " + isAvailable);
                        if (!isAvailable) {
                            holder.itemView.setVisibility(View.GONE);
                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                            params.height=0;
                            params.width= 0;
                            holder.itemView.setLayoutParams(params);
                        } else {
                            holder.itemView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                databaseReferenceRoot.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.d(TAG, "onDataChange: '" + user.getName() + "'" + snapshot.getChildrenCount());
                        long childrenCount = snapshot.getChildrenCount();


                        if (seats <= 0) {
                            holder.setTextViewTime("" + getString(R.string.not_Ava));
                        } else {
                            long result = seats - childrenCount;
                            if (result <= 0) {
                                holder.setTextViewTime("" + getString(R.string.not_Ava));
                            } else {
                                holder.setTextViewTime("" + result);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        };
        binding.rvDriver.setAdapter(adapter);
    }


    public static class OfferAdapter extends RecyclerView.ViewHolder {
        private TextView textViewName, textViewCarType, textViewDate, textViewSeat;
        private RatingBar ratingBar;
        private Button buttonBook;
        private ImageView imageViewUser;

        public OfferAdapter(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tv_dri_name);
            textViewCarType = itemView.findViewById(R.id.tv_Viechle_type);
            textViewDate = itemView.findViewById(R.id.tv_date);
            textViewSeat = itemView.findViewById(R.id.empty_set);
            ratingBar = itemView.findViewById(R.id.rb_dri_Rate);
            buttonBook = itemView.findViewById(R.id.btn_book);
            imageViewUser = itemView.findViewById(R.id.iv_dri_logo);
        }

        public void setTextViewName(String textViewName) {
            this.textViewName.setText(textViewName);
        }

        public void setTextViewCarType(String textViewCarType) {
            this.textViewCarType.setText(textViewCarType);
        }

        public void setTextViewDate(String textViewDate) {
            this.textViewDate.setText(textViewDate);
        }

        public void setTextViewTime(String textViewDate) {
            this.textViewSeat.setText(textViewDate + " Seats Available");
        }

        public void setRatingBar(float ratingBar) {
            this.ratingBar.setRating(ratingBar);
        }

        public void setImageViewUser(String imageViewUser) {
            Glide.with(this.imageViewUser.getContext()).load(imageViewUser)
                    .apply(new RequestOptions().placeholder(R.drawable.holder)).into(this.imageViewUser);
        }
    }

    private void initListener() {
        btnPicker();

    }

    private void btnPicker() {
        binding.cardDate.setOnClickListener(v -> showPickDate());
    }


    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date dates) {
            date = mFormatterTime.format(dates);
        }

        @Override
        public void onDateTimeCancel() {

        }
    };

    public void showPickDate() {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setIs24HourTime(false)
                .build()
                .show();
    }
}