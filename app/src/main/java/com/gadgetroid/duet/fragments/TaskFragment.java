package com.gadgetroid.duet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gadgetroid.duet.BottomSheetDialogs.TaskBottomSheetDialog;
import com.gadgetroid.duet.R;
import com.gadgetroid.duet.adapter.AllTasksAdapter;
import com.gadgetroid.duet.model.Task;
import com.gadgetroid.duet.views.TaskDetailActivity;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by gadgetroid on 24/07/17.
 */

public class TaskFragment extends Fragment {

    private Realm realm;
    private ListView tasksListView;
    private AllTasksAdapter adapter;

    public interface AllTasksFABListener {
        void setFAB();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = Realm.getDefaultInstance();
        tasksListView = (ListView) view.findViewById(R.id.tasks_list_view);
        AllTasksFABListener listener = (AllTasksFABListener) getActivity();
        listener.setFAB();
        setupListView();
    }

    private void setupListView() {
        RealmResults<Task> tasks = realm.where(Task.class).equalTo("isTaskComplete", false).findAll().sort("taskDueOn");
        adapter = new AllTasksAdapter(tasks);
        tasksListView.setAdapter(adapter);
        tasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = adapter.getItem(position);
                Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                intent.putExtra("taskId", task.getTaskId());
                startActivity(intent);
            }
        });
        tasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                Task task = adapter.getItem(position);
                args.putInt("taskId", task.getTaskId());
                TaskBottomSheetDialog taskBottomSheetDialog = TaskBottomSheetDialog.newInstance();
                taskBottomSheetDialog.setArguments(args);
                taskBottomSheetDialog.show(getFragmentManager(), "task_bottom_sheet");
                return true;
            }
        });
    }
}
