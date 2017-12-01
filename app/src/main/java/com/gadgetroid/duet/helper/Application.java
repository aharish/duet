package com.gadgetroid.duet.helper;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by gadgetroid on 16/07/17.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .schemaVersion(2)
                .migration(new Migration())
                .build();

        Realm.setDefaultConfiguration(configuration);

        Realm.getDefaultConfiguration();
    }
}
