package com.eeducationgo.tectik.util;

import androidx.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class Uper extends MultiDexApplication {
    public static Uper instance;
    private static Boolean isChatActivityOpen = false;
    public static Uper getInstance() {
        return instance;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static Boolean getIsChatActivityOpen() {
        return isChatActivityOpen;
    }

    public static void setIsChatActivityOpen(Boolean isChatActivityOpen) {
        Uper.isChatActivityOpen = isChatActivityOpen;
    }
}
