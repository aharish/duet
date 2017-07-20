package com.gadgetroid.duet.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gadgetroid.duet.AddTaskDialogFragment;
import com.gadgetroid.duet.EditProjectDialogFragment;
import com.gadgetroid.duet.R;
import com.gadgetroid.duet.TaskDetailsDialogFragment;
import com.gadgetroid.duet.adapter.TasksAdapter;
import com.gadgetroid.duet.model.Project;
import com.gadgetroid.duet.model.Task;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectActivity extends AppCompatActivity implements AddTaskDialogFragment.AddTaskDialogListener, EditProjectDialogFragment.EditProjectDialogListener {

    private Realm realm;
    private TextView projectTitle, projectDesc;
    private FloatingActionButton addTaskFab;
    private ListView tasksListView;
    private TasksAdapter adapter;
    private CheckBox taskDoneCheckbox;

    private int pId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Intent intent = getIntent();
        pId = intent.getIntExtra("id", 1);

        realm = Realm.getDefaultInstance();
        projectTitle = (TextView) findViewById(R.id.activity_project_title_textview);
        projectDesc = (TextView) findViewById(R.id.activity_project_desc_textview);
        addTaskFab = (FloatingActionButton) findViewById(R.id.activity_project_fab);
        tasksListView = (ListView) findViewById(R.id.activity_project_task_listview);
        taskDoneCheckbox = (CheckBox) findViewById(R.id.task_done_checkbox);

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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.project_action_edit) {
            showEditProjectDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
        task.setProject(project);
        realm.commitTransaction();
        Toast.makeText(this, "Added task successfully", Toast.LENGTH_SHORT).show();
    }

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

    private void showAddTaskDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddTaskDialogFragment addTaskDialogFragment = AddTaskDialogFragment.newInstance("New task");
        addTaskDialogFragment.show(fm, "fragment_add_task");
    }

    private void showEditProjectDialog() {
        FragmentManager fm = getSupportFragmentManager();
        Bundle args = new Bundle();
        Project project = realm.where(Project.class).equalTo("projectId", pId).findFirst();
        args.putString("name", project.getProjectName());
        args.putString("description", project.getProjectDescription());
        EditProjectDialogFragment fragment = EditProjectDialogFragment.newInstance("Edit Project");
        fragment.setArguments(args);
        fragment.show(fm, "fragment_edit_project");
    }

    private void showTaskDetailsDialog(int taskId) {
        FragmentManager fm = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putInt("taskId", taskId);
        TaskDetailsDialogFragment fragment = TaskDetailsDialogFragment.newInstance("Task details");
        fragment.setArguments(args);
        fragment.show(fm, "fragment_task_details");
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
        tasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO Show Task Details Dialog
                Task task = adapter.getItem(position);
                showTaskDetailsDialog(task.getTaskId());
            }
        });
    }
}
