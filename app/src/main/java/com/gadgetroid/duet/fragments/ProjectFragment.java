package com.gadgetroid.duet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gadgetroid.duet.BottomSheetDialogs.ProjectBottomSheetDialog;
import com.gadgetroid.duet.R;
import com.gadgetroid.duet.adapter.ProjectsAdapter;
import com.gadgetroid.duet.model.Project;
import com.gadgetroid.duet.views.ProjectActivity;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by gadgetroid on 23/07/17.
 */

public class ProjectFragment extends Fragment {

    private Realm realm;
    private ListView listView;
    private ProjectsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = Realm.getDefaultInstance();
        listView = (ListView) view.findViewById(R.id.projectsListView);
        setUpListView();
    }

    private void setUpListView() {
        RealmResults<Project> projects = realm.where(Project.class).findAll();
        adapter = new ProjectsAdapter(projects);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ProjectActivity.class);
                //TODO Send extras to identify and show description
                Project project = adapter.getItem(position);
                intent.putExtra("id", project.getProjectId());
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Project project = adapter.getItem(position);
                Bundle args = new Bundle();
                args.putInt("projectId", project.getProjectId());
                ProjectBottomSheetDialog bottomSheetDialog = ProjectBottomSheetDialog.getInstance();
                bottomSheetDialog.setArguments(args);
                bottomSheetDialog.show(getFragmentManager(), "project_bottom_sheet");
                return true;
            }
        });
    }
}
