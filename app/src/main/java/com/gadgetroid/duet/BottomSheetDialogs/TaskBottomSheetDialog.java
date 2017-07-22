package com.gadgetroid.duet.BottomSheetDialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gadgetroid.duet.R;
import com.gadgetroid.duet.model.Task;

import io.realm.Realm;

/**
 * Created by gadgetroid on 22/07/17.
 */

public class TaskBottomSheetDialog extends BottomSheetDialogFragment {

    private Realm realm;
    private LinearLayout taskDelete;

    public static TaskBottomSheetDialog newInstance() {
        return new TaskBottomSheetDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = Realm.getDefaultInstance();
        taskDelete = (LinearLayout) view.findViewById(R.id.task_delete_action);
        taskDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Delete task.
                int taskId = getArguments().getInt("taskId");
                realm.beginTransaction();
                Task task = realm.where(Task.class).equalTo("taskId", taskId).findFirst();
                task.deleteFromRealm();
                realm.commitTransaction();
                Snackbar.make(getActivity().findViewById(R.id.activity_project), "Task deleted", Snackbar.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
