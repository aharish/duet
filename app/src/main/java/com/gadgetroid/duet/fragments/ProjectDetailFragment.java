package com.gadgetroid.duet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.gadgetroid.duet.BottomSheetDialogs.TaskBottomSheetDialog;
import com.gadgetroid.duet.DialogFragments.EditProjectDialogFragment;
import com.gadgetroid.duet.DialogFragments.TaskDetailsDialogFragment;
import com.gadgetroid.duet.R;
import com.gadgetroid.duet.adapter.TasksAdapter;
import com.gadgetroid.duet.model.Project;
import com.gadgetroid.duet.model.Task;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by gadgetroid on 23/07/17.
 */

public class ProjectDetailFragment extends Fragment {

    private Realm realm;
    private TextView projectTitle, projectDesc;
    private FloatingActionButton addTaskFab;
    private ListView tasksListView;
    private TasksAdapter adapter;
    private CheckBox taskDoneCheckbox;

    private int pId;

    public interface ChangeProjectDetailFABActionListener {
        void changeProjectDetailFAB(int projectId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = Realm.getDefaultInstance();
        ChangeProjectDetailFABActionListener listener = (ChangeProjectDetailFABActionListener) getActivity();
        projectTitle = (TextView) view.findViewById(R.id.activity_project_title_textview);
        projectDesc = (TextView) view.findViewById(R.id.activity_project_desc_textview);
        tasksListView = (ListView) view.findViewById(R.id.activity_project_task_listview);
        taskDoneCheckbox = (CheckBox) view.findViewById(R.id.task_done_checkbox);

        pId = getArguments().getInt("projectId");

        listener.changeProjectDetailFAB(pId);

        setupListView();

        setMetadata();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.project_action_edit) {
            showEditProjectDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEditProjectDialog() {
        FragmentManager fm = getFragmentManager();
        Bundle args = new Bundle();
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
        args.putString("name", project.getProjectName());
        args.putString("description", project.getProjectDescription());
        args.putInt("id", project.getProjectId());
        EditProjectDialogFragment fragment = EditProjectDialogFragment.newInstance("Edit Project");
        fragment.setArguments(args);
        fragment.show(fm, "fragment_edit_project");
    }

    private void showTaskDetailsDialog(int taskId) {
        FragmentManager fm = getFragmentManager();
        Bundle args = new Bundle();
        args.putInt("taskId", taskId);
        TaskDetailsDialogFragment fragment = TaskDetailsDialogFragment.newInstance("Task details");
        fragment.setArguments(args);
        fragment.show(fm, "fragment_task_details");
    }

    public void setMetadata() {
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
        projectTitle.setText(project.getProjectName());
        projectDesc.setText(project.getProjectDescription());
    }

    private void setupListView() {
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
        RealmResults<Task> tasks = realm.where(Task.class).equalTo("project.projectId", pId).findAll().sort("isTaskComplete");
        adapter = new TasksAdapter(tasks);
        tasksListView.setAdapter(adapter);
        tasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO Show Task Details Dialog
                Task task = adapter.getItem(position);
                showTaskDetailsDialog(task.getTaskId());
            }
        });
        tasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                Task task = adapter.getItem(position);
                args.putInt("taskId", task.getTaskId());
                TaskBottomSheetDialog taskBottomSheetDialog = TaskBottomSheetDialog.newInstance();
                taskBottomSheetDialog.setArguments(args);
                taskBottomSheetDialog.show(getFragmentManager(), "task_bottom_sheet");
                return true;
            }
        });
    }
}
