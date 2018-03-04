package com.gadgetroid.duet.adapter;

import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.gadgetroid.duet.R;
import com.gadgetroid.duet.helper.PieProgressDrawable;
import com.gadgetroid.duet.model.Project;
import com.gadgetroid.duet.model.Task;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by gadgetroid on 17/07/17.
 */

public class ProjectsAdapter extends RealmBaseAdapter<Project> implements ListAdapter {

    PieProgressDrawable pieProgressDrawable;

    private static class ViewHolder {
        TextView projectName, projectTasksInfo;
        ImageView progressBar;
    }

    public ProjectsAdapter(OrderedRealmCollection<Project> data) {
        super(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.project_row_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.projectName = (TextView) convertView.findViewById(R.id.projectTitleTextView);
            viewHolder.projectTasksInfo = (TextView) convertView.findViewById(R.id.project_tasks_info_text_view);
            viewHolder.progressBar = (ImageView) convertView.findViewById(R.id.project_progress_iv);
            DisplayMetrics dm = parent.getResources().getDisplayMetrics();
            pieProgressDrawable = new PieProgressDrawable();
            pieProgressDrawable.setColor(ContextCompat.getColor(parent.getContext(), R.color.colorAccent));
            pieProgressDrawable.setBorderWidth(2f, dm);
            viewHolder.progressBar.setImageDrawable(pieProgressDrawable);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            final Project item = adapterData.get(position);
            viewHolder.projectName.setText(item.getProjectName());
            Realm  realm = Realm.getDefaultInstance();
            RealmResults<Task> totalTasks = realm.where(Task.class).equalTo("project.projectId", item.getProjectId()).findAll();
            RealmResults<Task> completeTasks = realm.where(Task.class).equalTo("project.projectId", item.getProjectId()).equalTo("isTaskComplete", true).findAll();
            float progressPercent = ((float) completeTasks.size() / (float) totalTasks.size())*100;
            pieProgressDrawable.setLevel((int) progressPercent);
            viewHolder.progressBar.invalidate();
            viewHolder.projectTasksInfo.setText("Completed " + completeTasks.size() + " out of " + totalTasks.size() + " tasks");
        }
        return convertView;
    }
}
