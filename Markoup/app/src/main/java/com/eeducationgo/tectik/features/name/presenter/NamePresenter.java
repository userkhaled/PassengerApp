package com.eeducationgo.tectik.features.name.presenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.features.id_user.view.IDActivity;
import com.eeducationgo.tectik.features.name.view.NameView;
import com.eeducationgo.tectik.util.AppShareData;
import com.eeducationgo.tectik.util.Constance;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class NamePresenter {
    private Activity activity;
    private NameView view;
    private static final String TAG = "NamePresenter";

    public NamePresenter(Activity activity, NameView view) {
        this.activity = activity;
        this.view = view;
    }

    public void createNameUser(TextInputEditText inputUserEmail, FirebaseAuth auth, DatabaseReference databaseReferenceName) {
        errorNull(inputUserEmail);
        if (AppShareData.checkIsEmptyEmailEdieText(inputUserEmail)) {
            AppShareData.errorInput(inputUserEmail, activity.getString(R.string.required_filed));
            return;
        }
        createUser(AppShareData.returnTextFromEditText(inputUserEmail), auth, databaseReferenceName);
    }

    private void createUser(String name, FirebaseAuth auth, DatabaseReference databaseReferenceName) {
        view.showProgress();
        String uid = auth.getCurrentUser().getUid();
        String email = auth.getCurrentUser().getEmail();
        String phone = auth.getCurrentUser().getPhoneNumber();

        if (AppShareData.checkIsEmptyString(email) && AppShareData.checkIsEmptyString(phone)) {
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(Constance.ChildPassengerUID, uid);
        map.put(Constance.ChildPassengerEmail, email);
        map.put(Constance.ChildPassengerPhone, phone == null ? "" : phone);
        map.put(Constance.ChildPassengerName, name);
        map.put(Constance.ChildPassengerType, Constance.RootPassenger);
        map.put(Constance.ChildPassengerImage, "");
        try {
            databaseReferenceName.setValue(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //todo this for passenger id for driver cart type
                    view.hideProgress();
                    view.showSuccessMessage(activity.getString(R.string.success_process));
                    activity.startActivity(new Intent(activity, IDActivity.class));
                    activity.finish();
                }
            }).addOnFailureListener(e -> {
                view.hideProgress();
                view.showErrorMessage(e.toString());
                Log.d(TAG, "createUser: " + e.toString());
            });
        } catch (Exception e) {
            e.printStackTrace();
            view.hideProgress();
            Log.d(TAG, "createUser: " + e.toString());
            view.showErrorMessage(e.toString());
        }


    }

    private void errorNull(TextInputEditText inputUserEmail) {
        inputUserEmail.setError(null);
    }
}
