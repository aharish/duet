package com.gadgetroid.duet.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gadgetroid.duet.DialogFragments.AddSubTaskDialogFragment;
import com.gadgetroid.duet.R;
import com.gadgetroid.duet.adapter.SubtasksAdapter;
import com.gadgetroid.duet.model.Subtask;
import com.gadgetroid.duet.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class TaskDetailActivity extends AppCompatActivity implements AddSubTaskDialogFragment.AddSubTaskDialogListener {

    private Realm realm;
    private TextView taskTitleTextView, taskDescTextView, taskDueTextView;
    private Button completionButton;
    private FloatingActionButton addSubTaskButton;
    private ListView subTaskListView;
    private LinearLayout subTaskLinearLayout, descriptionWrapperLinearLayout;
    private SubtasksAdapter subtasksAdapter;
    boolean isInvisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });

        realm = Realm.getDefaultInstance();
        Task task = realm.where(Task.class).equalTo("taskId", getIntent().getExtras().getInt("taskId")).findFirst();

        setupViews();

        setMetadata(task);

        setupListeners(task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.task_action_edit) {
            Intent editTaskIntent = new Intent(getApplicationContext(), TaskEditActivity.class);
            editTaskIntent.putExtra("taskId", getIntent().getExtras().getInt("taskId"));
            startActivity(editTaskIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Task task = realm.where(Task.class).equalTo("taskId", getIntent().getExtras().getInt("taskId")).findFirst();
        setMetadata(task);
    }

    private void setupViews() {
        taskTitleTextView = (TextView) findViewById(R.id.activity_task_detail_task_title_textview);
        taskDescTextView = (TextView) findViewById(R.id.activity_task_detail_task_desc_textview);
        taskDueTextView = (TextView) findViewById(R.id.activity_task_detail_task_due_textview);
        completionButton = (Button) findViewById(R.id.activity_task_detail_task_completion_button);
        addSubTaskButton = (FloatingActionButton) findViewById(R.id.fab);
        subTaskLinearLayout = (LinearLayout) findViewById(R.id.activity_task_detail_subtask_linear_layout);
        descriptionWrapperLinearLayout = (LinearLayout) findViewById(R.id.activity_task_detail_description_wrapper);
        subTaskListView = (ListView) findViewById(R.id.activity_task_detail_subtask_list_view);
    }

    private void setMetadata(Task task) {
        taskTitleTextView.setText(task.getTaskTitle());

        if (task.getTaskDescription().isEmpty()) {
            descriptionWrapperLinearLayout.setVisibility(View.GONE);
        } else {
            descriptionWrapperLinearLayout.setVisibility(View.VISIBLE);
            taskDescTextView.setText(task.getTaskDescription());
        }
        taskDueTextView.setText(getFormattedDate(task));

        if (task.isTaskComplete())
            completionButton.setText("Mark as not done");
        else
            completionButton.setText("Mark as done");

        RealmResults<Subtask> subtasks = realm.where(Subtask.class).equalTo("task.taskId", task.getTaskId()).findAll();
        if(subtasks.isEmpty()) {
            isInvisible = true;
            subTaskLinearLayout.setVisibility(View.INVISIBLE);
        } else {
            isInvisible = false;
            subTaskLinearLayout.setVisibility(View.VISIBLE);
            subtasksAdapter = new SubtasksAdapter(subtasks);
            subTaskListView.setAdapter(subtasksAdapter);
        }
    }

    private void setupListeners(final Task task) {
        completionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                if (task.isTaskComplete()) {
                    task.setTaskComplete(false);
                    completionButton.setText("Mark as done");
                } else {
                    task.setTaskComplete(true);
                    completionButton.setText("Mark as not done");
                }
                realm.commitTransaction();
            }
        });

        addSubTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Open DialogFragment to add new subtask
                FragmentManager fm = getSupportFragmentManager();
                Bundle args = new Bundle();
                args.putInt("taskId", getIntent().getExtras().getInt("taskId"));
                AddSubTaskDialogFragment fragment = AddSubTaskDialogFragment.newInstance("Add subtask");
                fragment.setArguments(args);
                fragment.show(fm, "add_subtask_dialog_fragment");
            }
        });
    }

    private String getFormattedDate(Task task) {
        String date;
        int thisYear, savedYear, today, savedDay;
        Calendar now, saved;
        SimpleDateFormat dayFormatter, yearFormatter, defaultFormatter;

        //Initialize Calendar with current date
        now = Calendar.getInstance();

        //Initialize Calendar with saved date
        saved = Calendar.getInstance();
        saved.setTimeInMillis(task.getTaskDueOn());

        //Get today's day of month
        today = now.get(Calendar.DAY_OF_MONTH);

        //Get saved day of month
        savedDay = saved.get(Calendar.DAY_OF_MONTH);

        //Get the current year
        thisYear = now.get(Calendar.YEAR);

        // Get the saved year
        savedYear = saved.get(Calendar.YEAR);

        if (task.getTaskDueOn() == 0) {
            return "Today";
        }

        if (saved != null && now != null) {

            /*
            * If the saved day is in the near future, then
            * display the date relatively.
            */

            if (savedDay == today + 1 || savedDay == today - 1 || savedDay == today) {

                if (savedDay == today + 1) {
                    return "Tomorrow";
                } else if (savedDay == today - 1) {
                    return "Yesterday";
                } else if (savedDay == today) {
                    return "Today";
                }

            } else {
                dayFormatter = new SimpleDateFormat("EEE, MMM d");
                date = dayFormatter.format(saved.getTime());

                /*
                * If the saved year is the same as the current
                * year, then don't show the year to the user
                */

                if ((thisYear - savedYear) == 0) {

                    return date;

                } else {

                    yearFormatter = new SimpleDateFormat(", yyyy");
                    date = date + yearFormatter.format(saved.getTime());
                    return date;

                }
            }

        }

        defaultFormatter = new SimpleDateFormat("EEE, MMM d, yyyy");
        return defaultFormatter.format(saved.getTime());
    }

    @Override
    public void onFinishAddSubTask(String title, boolean isComplete, int taskId) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        int nextId;
        if (realm.where(Subtask.class).count() == 0) {
            nextId = 1;
        } else {
            nextId = (realm.where(Subtask.class).findAll().max("subtaskId").intValue() + 1);
        }
        Task task = realm.where(Task.class).equalTo("taskId", getIntent().getExtras().getInt("taskId")).findFirst();
        Subtask subtask = realm.createObject(Subtask.class, nextId);
        subtask.setTaskTitle(title);
        subtask.setComplete(isComplete);
        subtask.setTask(task);
        realm.commitTransaction();
        Toast.makeText(this, "Subtask added", Toast.LENGTH_SHORT).show();
        if(isInvisible) {
            isInvisible = false;
            subTaskLinearLayout.setVisibility(View.VISIBLE);
        }
        setMetadata(task);
    }
}
