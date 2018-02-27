package com.gadgetroid.duet.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gadgetroid.duet.DialogFragments.AddProjectDialogFragment;
import com.gadgetroid.duet.DialogFragments.AddTaskDialogFragment;
import com.gadgetroid.duet.DialogFragments.AddTaskToProjectDialogFragment;
import com.gadgetroid.duet.DialogFragments.EditProjectDialogFragment;
import com.gadgetroid.duet.R;
import com.gadgetroid.duet.fragments.ProjectDetailFragment;
import com.gadgetroid.duet.fragments.ProjectFragment;
import com.gadgetroid.duet.fragments.TaskFragment;
import com.gadgetroid.duet.model.Project;
import com.gadgetroid.duet.model.Task;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements AddProjectDialogFragment.AddProjectDialogListener,
        ProjectFragment.ChangeProjectFABActionListener, ProjectDetailFragment.ChangeProjectDetailFABActionListener,
        AddTaskToProjectDialogFragment.AddTaskDialogListener, AddTaskDialogFragment.AddGenericTaskDialogListener, EditProjectDialogFragment.EditProjectDialogListener,
        TaskFragment.AllTasksFABListener {

    private Realm realm;
    private BottomNavigationView bottomNavigationView;
    private static final String PROJECT_FRAGMENT = "project_fragment";
    private static final String PROJECT_FRAGMENT_EDIT = "project_fragment_edit";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        setSupportActionBar(toolbar);
        bottomNavigationView.setSelectedItemId(R.id.bottom_view_projects);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.bottom_view_tasks:
                        changeFragment(item.getItemId(), savedInstanceState);
                        break;

                    case R.id.bottom_view_projects:
                        changeFragment(item.getItemId(), savedInstanceState);
                        break;

                    case R.id.bottom_view_today:
                        Toast.makeText(MainActivity.this, "Today", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });

        if (findViewById(R.id.project_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            ProjectFragment projectFragment = new ProjectFragment();
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
    public void setFAB() {
        //TODO Open add task dialog
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGenericTaskDialog();
            }
        });
    }

    @Override
    public void onFinishEditDialog(String name, String description, int pId) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
        project.setProjectName(name);
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

    @Override
    public void onFinishAddGenericTask(String title, String description, boolean isComplete) {
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
        AddTaskToProjectDialogFragment addTaskToProjectDialogFragment = AddTaskToProjectDialogFragment.newInstance("New task", projectId);
        addTaskToProjectDialogFragment.show(fm, "fragment_add_task");
    }

    private void showAddGenericTaskDialog() {
        Intent intent = new Intent(this, TaskAddActivity.class);
        startActivity(intent);
    }

    private void changeFragment(int id, Bundle savedInstanceState) {
        switch (id) {
            case R.id.bottom_view_projects:
                if (findViewById(R.id.project_fragment_container) != null) {
                    if (savedInstanceState != null) {
                        return;
                    }

                    ProjectFragment projectFragment = new ProjectFragment();
                    android.transition.Fade fadeIn = new android.transition.Fade(Fade.IN);
                    android.transition.Fade fadeOut = new android.transition.Fade(Fade.OUT);
                    projectFragment.setEnterTransition(fadeIn);
                    projectFragment.setReenterTransition(fadeIn);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.project_fragment_container, projectFragment).commit();
                }

                break;
            case R.id.bottom_view_tasks:
                if (findViewById(R.id.project_fragment_container) != null) {
                    if (savedInstanceState != null) {
                        return;
                    }

                    TaskFragment taskFragment = new TaskFragment();
                    android.transition.Fade fadeIn = new android.transition.Fade(Fade.IN);
                    android.transition.Fade fadeOut = new android.transition.Fade(Fade.OUT);
                    taskFragment.setEnterTransition(fadeIn);
                    taskFragment.setReenterTransition(fadeIn);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.project_fragment_container, taskFragment).commit();
                }
        }
    }
}
