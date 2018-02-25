package com.gadgetroid.duet.helper;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
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

        if (oldVersion == 2) {
            //Migration to add Subtasks model
            RealmObjectSchema taskSchema = schema.get("Task");
            schema.create("Subtask")
                    .addRealmObjectField("task", taskSchema)
                    .addField("subtaskId", int.class, FieldAttribute.PRIMARY_KEY)
                    .addField("taskTitle", String.class)
                    .addField("isComplete", boolean.class);
            oldVersion++;
        }

        if (oldVersion == 3) {
            //Migration to change data-type of dueOn in Task model
            schema.get("Task")
                    .addField("taskDueOn_tmp", long.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            String oldTaskDueOn = obj.getString("taskDueOn");
                            obj.setLong("taskDueOn_tmp", 0);
                        }
                    })
                    .removeField("taskDueOn")
                    .renameField("taskDueOn_tmp", "taskDueOn");
            oldVersion++;
        }

    }
}
