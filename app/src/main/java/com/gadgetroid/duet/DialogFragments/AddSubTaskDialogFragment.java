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
 * Created by gadgetroid on 24/02/18.
 */

public class AddSubTaskDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText titleEditText;

    public interface AddSubTaskDialogListener {
        void onFinishAddSubTask(String title, boolean isComplete, int taskId);
    }

    public AddSubTaskDialogFragment() {
        //Empty constructor
    }

    public static AddSubTaskDialogFragment newInstance(String title) {
        AddSubTaskDialogFragment frag = new AddSubTaskDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_sub_task_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleEditText = (EditText) view.findViewById(R.id.add_sub_task_dialog_title_edittext);
        titleEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        titleEditText.setOnEditorActionListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Makes the DialogFragment full-width
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            AddSubTaskDialogListener addSubTaskDialogListener = (AddSubTaskDialogListener) getActivity();
            addSubTaskDialogListener.onFinishAddSubTask(titleEditText.getText().toString(), false, getArguments().getInt("taskId"));
            dismiss();
        }
        return true;
    }
}
