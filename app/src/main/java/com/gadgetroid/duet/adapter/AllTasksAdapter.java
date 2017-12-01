package com.gadgetroid.duet.adapter;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.gadgetroid.duet.R;
import com.gadgetroid.duet.model.Project;
import com.gadgetroid.duet.model.Task;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;

/**
 * Created by gadgetroid on 24/07/17.
 */

public class AllTasksAdapter extends RealmBaseAdapter<Task> implements ListAdapter {

    private static class ViewHolder {
        CheckBox taskIsDone;
        TextView taskTitle;
        TextView taskProject;
    }

    public AllTasksAdapter(@Nullable OrderedRealmCollection<Task> data) {
        super(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.all_task_row_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.taskIsDone = (CheckBox) convertView.findViewById(R.id.all_tasks_checkbox);
            viewHolder.taskProject = (TextView) convertView.findViewById(R.id.all_tasks_project);
            viewHolder.taskTitle = (TextView) convertView.findViewById(R.id.all_tasks_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            final Task item = adapterData.get(position);
            Project project = item.getProject();
            viewHolder.taskTitle.setText(item.getTaskTitle());
            if (project == null) {
                viewHolder.taskProject.setText("No project");
            } else {
                viewHolder.taskProject.setText(project.getProjectName());
            }
            viewHolder.taskIsDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Realm realm = Realm.getDefaultInstance();
                    Task task = realm.where(Task.class).equalTo("taskId", item.getTaskId()).findFirst();
                    realm.beginTransaction();
                    task.setTaskComplete(viewHolder.taskIsDone.isChecked() ? true : false);
                    realm.commitTransaction();
                }
            });
            if (item.isTaskComplete()) {
                viewHolder.taskIsDone.setChecked(true);
            } else if (!item.isTaskComplete()) {
                viewHolder.taskIsDone.setChecked(false);
            }
        }
        return convertView;
    }
}
