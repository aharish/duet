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

public class EditProjectDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText nameEditText, descriptionEditText;

    public interface EditProjectDialogListener {
        void onFinishEditDialog(String name, String description);
    }

    public EditProjectDialogFragment() {
        //Empty constructor
    }

    public static EditProjectDialogFragment newInstance(String title) {
        EditProjectDialogFragment frag = new EditProjectDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_project_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameEditText = (EditText) view.findViewById(R.id.add_project_dialog_name_edittext);
        descriptionEditText = (EditText) view.findViewById(R.id.add_project_dialog_description_edittext);
        String title = getArguments().getString("title", "New Project");
        getDialog().setTitle(title);
        nameEditText.setText(getArguments().getString("name"));
        descriptionEditText.setText(getArguments().getString("description"));
        nameEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        descriptionEditText.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            EditProjectDialogListener listener = (EditProjectDialogListener) getActivity();
            listener.onFinishEditDialog(nameEditText.getText().toString(), descriptionEditText.getText().toString());
            dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Makes the DialogFragment full-width
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }
}
