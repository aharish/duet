package com.gadgetroid.duet;

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

/**
 * Created by gadgetroid on 18/07/17.
 */

public class AddTaskDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText titleEditText, descriptionEditText;

    //TODO Add Interface to add to Realm
    public interface AddTaskDialogListener {
        void onFinishAddTask(String title, String description, boolean isComplete);
    }

    public AddTaskDialogFragment() {
        //Empty constructor
    }

    public static AddTaskDialogFragment newInstance(String title) {
        AddTaskDialogFragment frag = new AddTaskDialogFragment();
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //TODO Add to Realm
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            AddTaskDialogListener addTaskDialogListener = (AddTaskDialogListener) getActivity();
            addTaskDialogListener.onFinishAddTask(
                    titleEditText.getText().toString(),
                    descriptionEditText.getText().toString(),
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
        titleEditText = (EditText) view.findViewById(R.id.add_task_dialog_title_edittext);
        descriptionEditText = (EditText) view.findViewById(R.id.add_task_dialog_description_edittext);
        String title = getArguments().getString("title", "New Task");
        getDialog().setTitle(title);
        titleEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        descriptionEditText.setOnEditorActionListener(this);
    }
}
