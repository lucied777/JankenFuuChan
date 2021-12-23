package com.example.jankenfuuchan;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyJunkenApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().allowWritesOnUiThread(true).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
