package com.gadgetroid.duet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
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

public class ProjectDetailFragment extends Fragment implements EditProjectDialogFragment.EditProjectDialogListener {

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

//    @Override
//    public void onFinishAddTask(String title, String description, boolean isComplete) {
//        realm = realm.getDefaultInstance();
//        realm.beginTransaction();
//        int nextId;
//        if (realm.where(Task.class).count() == 0) {
//            nextId = 1;
//        } else {
//            nextId = (realm.where(Task.class).findAll().max("taskId").intValue() + 1);
//        }
//        Task task = realm.createObject(Task.class, nextId);
//        task.setTaskTitle(title);
//        task.setTaskDescription(description);
//        task.setTaskComplete(isComplete);
//        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
//        task.setProject(project);
//        realm.commitTransaction();
//        Toast.makeText(getContext(), "Added task successfully", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onFinishEditDialog(String name, String description) {
        //TODO Save edits to Realm
        realm = realm.getDefaultInstance();
        realm.beginTransaction();
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
        project.setProjectName(name);
        project.setProjectDescription(description);
        realm.commitTransaction();
        projectTitle.setText(project.getProjectName());
        projectDesc.setText(project.getProjectDescription());
        realm.close();
    }

    private void showEditProjectDialog() {
        FragmentManager fm = getFragmentManager();
        Bundle args = new Bundle();
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
        args.putString("name", project.getProjectName());
        args.putString("description", project.getProjectDescription());
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

    private void setMetadata() {
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
