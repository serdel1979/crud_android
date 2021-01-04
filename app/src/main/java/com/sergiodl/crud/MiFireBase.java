package com.sergiodl.crud;

import androidx.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;

public class MiFireBase extends android.app.Application {

    @Override
   public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
