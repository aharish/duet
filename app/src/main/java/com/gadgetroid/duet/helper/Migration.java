package com.gadgetroid.duet.helper;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by gadgetroid on 01/12/17.
 */

public class Migration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

//        if (oldVersion == 1) {
//            schema.get("Task")
//                    .addField("taskDueOn", String.class);
//            oldVersion++;
//        }

    }
}
