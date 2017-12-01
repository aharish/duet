package com.gadgetroid.duet.DialogFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gadgetroid.duet.R;

/**
 * Created by gadgetroid on 25/07/17.
 */

public class AddTaskDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText taskTitle, taskDescription;

    public interface AddGenericTaskDialogListener {
        void onFinishAddGenericTask(String title, String description, boolean isComplete);
    }

    public AddTaskDialogFragment() {
        //Empty constructor
    }

    public static AddTaskDialogFragment newInstance() {
        AddTaskDialogFragment fragment = new AddTaskDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Makes the DialogFragment full-width
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            AddGenericTaskDialogListener addGenericTaskDialogListener = (AddGenericTaskDialogListener) getActivity();
            addGenericTaskDialogListener.onFinishAddGenericTask(
                    taskTitle.getText().toString(),
                    taskDescription.getText().toString(),
                    false
            );
            dismiss();
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_task_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskTitle = (EditText) view.findViewById(R.id.add_all_task_dialog_title_edittext);
        taskDescription = (EditText) view.findViewById(R.id.add_all_task_dialog_description_edittext);
        taskTitle.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        taskDescription.setOnEditorActionListener(this);
    }
}
