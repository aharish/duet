package com.gadgetroid.duet;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gadgetroid.duet.adapter.ProjectsAdapter;
import com.gadgetroid.duet.model.Project;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements AddProjectDialogFragment.AddProjectDialogListener {

    private Realm realm;
    private RecyclerView recyclerView;
    private ListView listView;
    private ProjectsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        listView = (ListView) findViewById(R.id.projectsListView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Launch DialogFagment
                showAddProjectDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishAddDialog(String name, String description) {
        // TODO Add to Realm database
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Project project = realm.createObject(Project.class);
        project.setProjectName(name);
        project.setProjectDescription(description);
        realm.commitTransaction();
        Toast.makeText(this, "Project added successfully!", Toast.LENGTH_LONG).show();
    }

    private void showAddProjectDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddProjectDialogFragment addProjectDialogFragment = AddProjectDialogFragment.newInstance("New project");
        addProjectDialogFragment.show(fm, "fragment_add_project");
    }

    private void setUpListView() {
        //TODO Set up ListView
        RealmResults<Project> projects = realm.where(Project.class).findAll();
        adapter = new ProjectsAdapter(projects);
        listView.setAdapter(adapter);
    }

    private void setUpRecyclerView() {
//        adapter = new ProjectsAdapter(realm.where(Project.class).findAll());
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
//        recyclerView.setHasFixedSize(true);
    }
}
