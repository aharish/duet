package com.gadgetroid.duet.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gadgetroid.duet.AddTaskDialogFragment;
import com.gadgetroid.duet.R;
import com.gadgetroid.duet.adapter.TasksAdapter;
import com.gadgetroid.duet.model.Project;
import com.gadgetroid.duet.model.Task;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectActivity extends AppCompatActivity implements AddTaskDialogFragment.AddTaskDialogListener {

    private Realm realm;
    private TextView projectTitle, projectDesc;
    private FloatingActionButton addTaskFab;
    private ListView tasksListView;
    private TasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        realm = Realm.getDefaultInstance();
        projectTitle = (TextView) findViewById(R.id.activity_project_title_textview);
        projectDesc = (TextView) findViewById(R.id.activity_project_desc_textview);
        addTaskFab = (FloatingActionButton) findViewById(R.id.activity_project_fab);
        tasksListView = (ListView) findViewById(R.id.activity_project_task_listview);

        addTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });

        //TODO Register ListView & set adapter
        setupListView();

        setMetadata();
    }

    @Override
    public void onFinishAddTask(String title, String description, boolean isComplete) {
        realm = realm.getDefaultInstance();
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
        Project project = realm.where(Project.class).equalTo("projectId", getIntent().getIntExtra("id", 1)).findFirst();
        task.setProject(project);
        realm.commitTransaction();
        Toast.makeText(this, "Added task successfully", Toast.LENGTH_SHORT).show();
    }

    private void showAddTaskDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddTaskDialogFragment addTaskDialogFragment = AddTaskDialogFragment.newInstance("New task");
        addTaskDialogFragment.show(fm, "fragment_add_task");
    }

    private void setMetadata() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 1);
        Project project = realm.where(Project.class).equalTo("projectId", id).findFirst();
        projectTitle.setText(project.getProjectName());
        projectDesc.setText(project.getProjectDescription());
    }

    private void setupListView() {
        Project project = realm.where(Project.class).equalTo("projectId", getIntent().getIntExtra("id", 1)).findFirst();
        RealmResults<Task> tasks = realm.where(Task.class).equalTo("project.projectId", getIntent().getIntExtra("id", 1)).findAll();
        adapter = new TasksAdapter(tasks);
        tasksListView.setAdapter(adapter);
    }
}
