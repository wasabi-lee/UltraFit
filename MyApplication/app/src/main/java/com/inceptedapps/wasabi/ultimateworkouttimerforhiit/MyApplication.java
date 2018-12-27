package com.inceptedapps.wasabi.ultimateworkouttimerforhiit;

import android.app.Application;

import java.io.FileNotFoundException;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by Wasabi on 5/6/2016.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .schemaVersion(0)
                .build();
        try{
            Realm.migrateRealm(realmConfiguration, new RealmMigration() {
                @Override
                public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

                }
            });
        } catch (FileNotFoundException ignored){

        }
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
