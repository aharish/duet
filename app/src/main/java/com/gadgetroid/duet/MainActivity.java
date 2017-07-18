package com.gadgetroid.duet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gadgetroid.duet.adapter.ProjectsAdapter;
import com.gadgetroid.duet.model.Project;
import com.gadgetroid.duet.views.ProjectActivity;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements AddProjectDialogFragment.AddProjectDialogListener {

    private Realm realm;
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
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        int nextId;
        if (realm.where(Project.class).count() == 0) {
            nextId = 1;
        } else {
            nextId = (realm.where(Project.class).findAll().max("projectId").intValue() + 1);
        }
        Project project = realm.createObject(Project.class, nextId);
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
        RealmResults<Project> projects = realm.where(Project.class).findAll();
        adapter = new ProjectsAdapter(projects);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ProjectActivity.class);
                //TODO Send extras to identify and show description
                Project project = adapter.getItem(position);
                intent.putExtra("id", project.getProjectId());
                startActivity(intent);
            }
        });
    }
}
