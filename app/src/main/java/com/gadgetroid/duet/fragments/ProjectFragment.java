package com.gadgetroid.duet.fragments;

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

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by gadgetroid on 23/07/17.
 */

public class ProjectFragment extends Fragment {

    public interface ChangeProjectFABActionListener {
        void projectFragmentChangeFAB();
    }

    private static final String PROJECT_FRAGMENT_EDIT = "project_fragment_edit";

    private Realm realm;
    private ListView listView;
    private ProjectsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        ChangeProjectFABActionListener listener = (ChangeProjectFABActionListener) getActivity();
        listener.projectFragmentChangeFAB();
        setUpListView();
    }

    private void setUpListView() {
        RealmResults<Project> projects = realm.where(Project.class).findAll();
        adapter = new ProjectsAdapter(projects);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProjectDetailFragment projectDetailFragment = new ProjectDetailFragment();
                Bundle args = new Bundle();
                Project project = adapter.getItem(position);
                args.putInt("projectId", project.getProjectId());
                projectDetailFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.project_fragment_container, projectDetailFragment, PROJECT_FRAGMENT_EDIT)
                        .addToBackStack(null)
                        .commit();
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
