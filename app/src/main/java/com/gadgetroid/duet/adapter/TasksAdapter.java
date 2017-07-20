package com.gadgetroid.duet.adapter;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.gadgetroid.duet.R;
import com.gadgetroid.duet.model.Task;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;

/**
 * Created by gadgetroid on 18/07/17.
 */

public class TasksAdapter extends RealmBaseAdapter<Task> implements ListAdapter {

    private static class ViewHolder {
        //TODO Set up views
        TextView taskTitleTextView;
        CheckBox taskCompleteCheckbox;
    }

    public TasksAdapter(@Nullable OrderedRealmCollection<Task> data) {
        super(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_row_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.taskCompleteCheckbox = (CheckBox) convertView.findViewById(R.id.task_done_checkbox);
            viewHolder.taskTitleTextView = (TextView) convertView.findViewById(R.id.task_title_textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            final Task item = adapterData.get(position);
            viewHolder.taskTitleTextView.setText(item.getTaskTitle());
            viewHolder.taskCompleteCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Realm realm = Realm.getDefaultInstance();
                    Task task = realm.where(Task.class).equalTo("taskId", item.getTaskId()).findFirst();
                    realm.beginTransaction();
                    task.setTaskComplete(viewHolder.taskCompleteCheckbox.isChecked() ? true : false);
                    realm.commitTransaction();
                }
            });
            if (item.isTaskComplete()) {
                viewHolder.taskCompleteCheckbox.setChecked(true);
            } else {
                viewHolder.taskCompleteCheckbox.setChecked(false);
            }
        }
        return convertView;
    }
}
