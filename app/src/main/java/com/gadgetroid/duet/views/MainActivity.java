package com.gadgetroid.duet.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.gadgetroid.duet.DialogFragments.AddProjectDialogFragment;
import com.gadgetroid.duet.DialogFragments.AddTaskDialogFragment;
import com.gadgetroid.duet.DialogFragments.EditProjectDialogFragment;
import com.gadgetroid.duet.R;
import com.gadgetroid.duet.fragments.ProjectDetailFragment;
import com.gadgetroid.duet.fragments.ProjectFragment;
import com.gadgetroid.duet.model.Project;
import com.gadgetroid.duet.model.Task;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements AddProjectDialogFragment.AddProjectDialogListener,
        ProjectFragment.ChangeProjectFABActionListener, ProjectDetailFragment.ChangeProjectDetailFABActionListener,
        AddTaskDialogFragment.AddTaskDialogListener, EditProjectDialogFragment.EditProjectDialogListener {

    private Realm realm;
    private static final String PROJECT_FRAGMENT = "project_fragment";
    private static final String PROJECT_FRAGMENT_EDIT = "project_fragment_edit";

    public interface projectMetadataListener {
        void updateTextViews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.project_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            ProjectFragment projectFragment = new ProjectFragment();
            Slide slideIn = new Slide();
            slideIn.setSlideEdge(Gravity.END);
            projectFragment.setEnterTransition(slideIn);
            Slide slideOut = new Slide();
            slideOut.setSlideEdge(Gravity.START);
            projectFragment.setExitTransition(slideOut);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.project_fragment_container, projectFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void projectFragmentChangeFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddProjectDialog();
            }
        });
    }

    @Override
    public void changeProjectDetailFAB(final int projectId) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog(projectId);
            }
        });
    }

    @Override
    public void onFinishEditDialog(String name, String description, int pId) {
        //TODO Save edits to Realm
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
//        project.setProjectName(name);
        project.setProjectDescription(description);
        realm.commitTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PROJECT_FRAGMENT_EDIT);
        ((ProjectDetailFragment) fragment).setMetadata();
    }

    @Override
    public void onFinishAddDialog(String name, String description) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        int nextId;
        if (realm.where(Project.class).count() == 0) {
            nextId = 1;
        } else {
            nextId = (realm.where(Project.class).findAll().max("projectId").intValue() + 1);
        }
        Project project = realm.createObject(Project.class, nextId);
        project.setProjectName(name);
        project.setProjectDescription(description);
        realm.commitTransaction();
        Toast.makeText(this, "Project added successfully!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFinishAddTask(String title, String description, boolean isComplete, int pId) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        int nextId;
        if (realm.where(Task.class).count() == 0) {
            nextId = 1;
        } else {
            nextId = (realm.where(Task.class).findAll().max("taskId").intValue() + 1);
        }
        Task task = realm.createObject(Task.class, nextId);
        task.setTaskTitle(title);
        task.setTaskDescription(description);
        task.setTaskComplete(isComplete);
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
        task.setProject(project);
        realm.commitTransaction();
        Toast.makeText(this, "Added task successfully", Toast.LENGTH_SHORT).show();
    }

    private void showAddProjectDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddProjectDialogFragment addProjectDialogFragment = AddProjectDialogFragment.newInstance("New project");
        addProjectDialogFragment.show(fm, "fragment_add_project");
    }

    private void showAddTaskDialog(int projectId) {
        FragmentManager fm = getSupportFragmentManager();
        AddTaskDialogFragment addTaskDialogFragment = AddTaskDialogFragment.newInstance("New task", projectId);
        addTaskDialogFragment.show(fm, "fragment_add_task");
    }
}
