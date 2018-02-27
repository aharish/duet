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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;

public class TaskAddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Realm realm;
    private EditText titleEditText, descriptionEditText;
    private static TextView dueDateTextView;
    static long dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

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

        setupViews();

        setupClickListeners();
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
        titleEditText = (EditText) findViewById(R.id.activity_task_add_task_title_edittext);
        descriptionEditText = (EditText) findViewById(R.id.activity_task_add_task_desc_edittext);
        dueDateTextView = (TextView) findViewById(R.id.activity_task_add_task_due_textview);
    }

    private void setupClickListeners() {
        dueDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });
    }

    private void saveTask() {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        int nextId;
        if (realm.where(Task.class).count() == 0) {
            nextId = 1;
        } else {
            nextId = (realm.where(Task.class).findAll().max("taskId").intValue() + 1);
        }
        Task task = realm.createObject(Task.class, nextId);
        task.setTaskTitle(titleEditText.getText().toString());
        task.setTaskDescription(descriptionEditText.getText().toString());
        task.setTaskDueOn(dueDate);
        task.setTaskComplete(false);
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
            dueDateTextView.setText(getFormattedDate(dueDate));
        }

        private String getFormattedDate(long chosenTime) {
            String date;
            int thisYear, savedYear, today, savedDay;
            Calendar now, saved;
            SimpleDateFormat dayFormatter, yearFormatter, defaultFormatter;

            //Initialize Calendar with current date
            now = Calendar.getInstance();

            //Initialize Calendar with saved date
            saved = Calendar.getInstance();
            saved.setTimeInMillis(chosenTime);

            //Get today's day of month
            today = now.get(Calendar.DAY_OF_MONTH);

            //Get saved day of month
            savedDay = saved.get(Calendar.DAY_OF_MONTH);

            //Get the current year
            thisYear = now.get(Calendar.YEAR);

            // Get the saved year
            savedYear = saved.get(Calendar.YEAR);

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
    }
}
