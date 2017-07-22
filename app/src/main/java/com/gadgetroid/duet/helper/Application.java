package com.gadgetroid.duet.helper;

import io.realm.Realm;

/**
 * Created by gadgetroid on 16/07/17.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        Realm.getDefaultConfiguration();
    }
}
