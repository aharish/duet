package com.gadgetroid.duet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gadgetroid.duet.model.Project;

import io.realm.Realm;

/**
 * Created by gadgetroid on 22/07/17.
 */

public class ProjectBottomSheetDialog extends BottomSheetDialogFragment {

    private Realm realm;
    private LinearLayout projectDelete;
    private CoordinatorLayout coordinatorLayout;

    public static ProjectBottomSheetDialog getInstance() {
        return new ProjectBottomSheetDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.project_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = Realm.getDefaultInstance();
        projectDelete = (LinearLayout) view.findViewById(R.id.project_delete_action);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.activity_main_coordinator_layout);
        projectDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int projectId = getArguments().getInt("projectId");
                Project project = realm.where(Project.class).equalTo("projectId", projectId).findFirst();
                realm.beginTransaction();
                project.deleteFromRealm();
                realm.commitTransaction();
                Snackbar.make(getActivity().findViewById(R.id.activity_main_coordinator_layout), "Project deleted", Snackbar.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
