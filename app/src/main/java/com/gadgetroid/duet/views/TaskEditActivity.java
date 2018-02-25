package com.gadgetroid.duet.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.gadgetroid.duet.R;
import com.gadgetroid.duet.model.Task;

import java.util.Calendar;

import io.realm.Realm;

public class TaskEditActivity extends AppCompatActivity {

    private Realm realm;
    private Toolbar toolbar;
    static TextView datePickerTextView;
    private EditText titleEditText, descEditText;
    static long dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });

        realm = Realm.getDefaultInstance();
        Task task = realm.where(Task.class).equalTo("taskId", getIntent().getExtras().getInt("taskId")).findFirst();

        setupViews();

        setupClickListeners();

        setMetadata(task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.task_action_save) {
            saveTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        datePickerTextView = (TextView) findViewById(R.id.activity_task_edit_task_due_textview);
        titleEditText = (EditText) findViewById(R.id.activity_task_edit_task_title_edittext);
        descEditText = (EditText) findViewById(R.id.activity_task_edit_task_desc_edittext);
    }

    private void setupClickListeners() {
        datePickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });
    }

    private void setMetadata(Task task) {
        titleEditText.setText(task.getTaskTitle());
        descEditText.setText(task.getTaskDescription());
        datePickerTextView.setText(String.valueOf(task.getTaskDueOn()));
    }

    private void saveTask() {
        realm.beginTransaction();
        Task task = realm.where(Task.class).equalTo("taskId", getIntent().getExtras().getInt("taskId")).findFirst();
        task.setTaskTitle(titleEditText.getText().toString());
        task.setTaskDescription(descEditText.getText().toString());
        task.setTaskDueOn(dueDate);
        realm.commitTransaction();
        finishAfterTransition();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            final Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            setDueDate(c);
        }

        private void setDueDate(Calendar c) {
            dueDate = c.getTimeInMillis();
            datePickerTextView.setText(String.valueOf(c.getTimeInMillis()));
        }
    }
}
