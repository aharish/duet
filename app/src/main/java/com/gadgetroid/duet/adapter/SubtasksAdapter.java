package com.gadgetroid.duet.adapter;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.gadgetroid.duet.R;
import com.gadgetroid.duet.model.Subtask;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;

/**
 * Created by gadgetroid on 02/12/17.
 */

public class SubtasksAdapter extends RealmBaseAdapter<Subtask> implements ListAdapter {

    private static class ViewHolder {
        TextView taskTitleTextView;
        CheckBox taskCompleteCheckbox;
    }

    public SubtasksAdapter(@Nullable OrderedRealmCollection<Subtask> data) {
        super(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subtask_row_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.taskCompleteCheckbox = (CheckBox) convertView.findViewById(R.id.subtask_done_checkbox);
            viewHolder.taskTitleTextView = (TextView) convertView.findViewById(R.id.subtask_title_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            final Subtask item = adapterData.get(position);
            viewHolder.taskTitleTextView.setText(item.getTaskTitle());
            viewHolder.taskCompleteCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Realm realm = Realm.getDefaultInstance();
                    Subtask subtask = realm.where(Subtask.class).equalTo("subtaskId", item.getSubtaskId()).findFirst();
                    realm.beginTransaction();
                    subtask.setComplete(viewHolder.taskCompleteCheckbox.isChecked() ? true : false);
                    realm.commitTransaction();
                }
            });
            if (item.isComplete()) {
                viewHolder.taskCompleteCheckbox.setChecked(true);
            } else {
                viewHolder.taskCompleteCheckbox.setChecked(false);
            }
        }
        return convertView;
    }
}
