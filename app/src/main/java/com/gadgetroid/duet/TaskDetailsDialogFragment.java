package com.gadgetroid.duet;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.gadgetroid.duet.model.Task;

import io.realm.Realm;

/**
 * Created by gadgetroid on 19/07/17.
 */

public class TaskDetailsDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private CheckBox taskIsDoneCheckBox;
    private ViewSwitcher titleViewSwitcher, descriptionViewSwitcher;
    private TextView titleTextView, descriptionTextView;
    private EditText titleEditText, descriptionEditText;
    private Realm realm;

    public TaskDetailsDialogFragment() {
        //Empty constructor
    }

    public static TaskDetailsDialogFragment newInstance(String title) {
        TaskDetailsDialogFragment frag = new TaskDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Makes the DialogFragment full-width
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_detail_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = realm.getDefaultInstance();
        taskIsDoneCheckBox = (CheckBox) view.findViewById(R.id.task_detail_checkbox);
        titleViewSwitcher = (ViewSwitcher) view.findViewById(R.id.task_detail_title_view_switcher);
        descriptionViewSwitcher = (ViewSwitcher) view.findViewById(R.id.task_detail_desc_view_switcher);
        titleTextView = (TextView) view.findViewById(R.id.task_detail_title_text_view);
        descriptionTextView = (TextView) view.findViewById(R.id.task_detail_desc_text_view);
        titleEditText = (EditText) view.findViewById(R.id.task_detail_title_edit_text);
        descriptionEditText = (EditText) view.findViewById(R.id.task_detail_desc_edit_text);

        descriptionEditText.setOnEditorActionListener(this);
        titleEditText.setOnEditorActionListener(this);

        taskIsDoneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int taskId = getArguments().getInt("taskId");
                Task task = realm.where(Task.class).equalTo("taskId", taskId).findFirst();
                realm.beginTransaction();
                task.setTaskComplete(isChecked ? true : false);
                realm.commitTransaction();
            }
        });

        setupViewSwitchers();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        InputMethodManager iMM = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            if (v.getId() == R.id.task_detail_title_edit_text) {
                int taskId = getArguments().getInt("taskId");
                Task task = realm.where(Task.class).equalTo("taskId", taskId).findFirst();
                realm.beginTransaction();
                task.setTaskTitle(titleEditText.getText().toString());
                realm.commitTransaction();
                titleTextView.setText(task.getTaskTitle());
                titleViewSwitcher.showPrevious();
                iMM.toggleSoftInput(0, 0);
            } else if (v.getId() == R.id.task_detail_desc_edit_text) {
                int taskId = getArguments().getInt("taskId");
                Task task = realm.where(Task.class).equalTo("taskId", taskId).findFirst();
                realm.beginTransaction();
                task.setTaskDescription(descriptionEditText.getText().toString());
                realm.commitTransaction();
                descriptionTextView.setText(task.getTaskDescription());
                descriptionViewSwitcher.showPrevious();
                iMM.toggleSoftInput(0, 0);
            }
            return true;
        }
        return false;
    }

    private void setupViewSwitchers() {
        int taskid = getArguments().getInt("taskId");
        Task task = realm.where(Task.class).equalTo("taskId", taskid).findFirst();
        taskIsDoneCheckBox.setChecked(task.isTaskComplete() ? true : false);
        titleTextView.setText(task.getTaskTitle());
        descriptionTextView.setText(task.getTaskDescription());
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleEditText.setText(titleTextView.getText().toString());
                titleViewSwitcher.showNext();
                titleEditText.requestFocus();
                InputMethodManager iMM = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                iMM.showSoftInput(titleEditText, 1);
                titleEditText.setSelection(titleEditText.getText().length());
            }
        });
        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionEditText.setHorizontallyScrolling(false);
                descriptionEditText.setMaxLines(Integer.MAX_VALUE);
                descriptionEditText.setText(descriptionTextView.getText().toString());
                descriptionViewSwitcher.showNext();
                descriptionEditText.requestFocus();
                InputMethodManager iMM = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                iMM.showSoftInput(descriptionEditText, 1);
                descriptionEditText.setSelection(descriptionEditText.getText().length());
            }
        });
    }
}
