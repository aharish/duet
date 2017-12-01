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
 * Created by gadgetroid on 18/07/17.
 */

public class AddTaskToProjectDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText titleEditText, descriptionEditText;

    private int pId;

    public interface AddTaskDialogListener {
        void onFinishAddTask(String title, String description, boolean isComplete, int projectId);
    }

    public AddTaskToProjectDialogFragment() {
        //Empty constructor
    }

    public static AddTaskToProjectDialogFragment newInstance(String title, int projectId) {
        AddTaskToProjectDialogFragment frag = new AddTaskToProjectDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("projectId", projectId);
        frag.setArguments(args);
        return frag;
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
            AddTaskDialogListener addTaskDialogListener = (AddTaskDialogListener) getActivity();
            addTaskDialogListener.onFinishAddTask(
                    titleEditText.getText().toString(),
                    descriptionEditText.getText().toString(),
                    false,
                    pId
            );
            dismiss();
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_task_to_project_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleEditText = (EditText) view.findViewById(R.id.add_task_dialog_title_edittext);
        descriptionEditText = (EditText) view.findViewById(R.id.add_task_dialog_description_edittext);
        String title = getArguments().getString("title", "New Task");
        getDialog().setTitle(title);
        pId = getArguments().getInt("projectId");
        titleEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        descriptionEditText.setOnEditorActionListener(this);
    }
}
