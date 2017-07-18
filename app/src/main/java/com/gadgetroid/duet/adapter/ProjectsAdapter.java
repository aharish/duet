package com.gadgetroid.duet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.gadgetroid.duet.R;
import com.gadgetroid.duet.model.Project;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by gadgetroid on 17/07/17.
 */

public class ProjectsAdapter extends RealmBaseAdapter<Project> implements ListAdapter {

    private static class ViewHolder {
        TextView projectName;
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            final Project item = adapterData.get(position);
            viewHolder.projectName.setText(item.getProjectName());
        }
        return convertView;
    }
}
