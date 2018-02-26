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

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        TextView dueDate;
        TextView dueMonth;
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
            viewHolder.dueDate = (TextView) convertView.findViewById(R.id.all_tasks_due_date_tv);
            viewHolder.dueMonth = (TextView) convertView.findViewById(R.id.all_tasks_due_month_tv);
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
            Realm realm = Realm.getDefaultInstance();
            Task task = realm.where(Task.class).equalTo("taskId", item.getTaskId()).findFirst();
            long taskDueOn = task.getTaskDueOn();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat, monthFormat;
            if (taskDueOn == 0) {
                c.setTimeInMillis(System.currentTimeMillis());
                dateFormat = new SimpleDateFormat("d");
                monthFormat = new SimpleDateFormat("MMM");
                viewHolder.dueDate.setText(dateFormat.format(c.getTime()));
                viewHolder.dueMonth.setText(monthFormat.format(c.getTime()));
            } else {
                c.setTimeInMillis(taskDueOn);
                dateFormat = new SimpleDateFormat("d");
                monthFormat = new SimpleDateFormat("MMM");
                viewHolder.dueDate.setText(dateFormat.format(c.getTime()));
                viewHolder.dueMonth.setText(monthFormat.format(c.getTime()));
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
