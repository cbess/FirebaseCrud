package com.example.cbess.firebasecrud;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.BuildConfig;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskAdapterListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<TaskItem> tasks = new ArrayList<>();
    private TaskAdapter adapter;
    // Tasks ref to firebase DB
    private DatabaseReference tasksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initConfig();
        initDatabase();

        // setup the recycler view
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(tasks, this);
        recyclerView.setAdapter(adapter);

        loadData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEmptyTask();
            }
        });
    }

    private void initConfig() {
        final FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
        // update the config to ping multiple times, don't use liberally
        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        config.setConfigSettings(settings);

        // set the defaults
        config.setDefaults(R.xml.firebase);
        // if there is no entry on the server for the config keys, then the client-side defaults are used
        config.fetch(15).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    config.activateFetched();
                }
            }
        });
    }

    private void initDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // synced data and writes will be persisted to disk across app 
        // restarts and our app should work seamlessly in offline situations
        //database.setPersistenceEnabled(true);

        // create a leaf on root
        tasksRef = database.getReference("tasks");
    }

    private void loadData() {
        tasks.clear();

        // fetch from firebase as remote data changes
        tasksRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TaskItem item = dataSnapshot.getValue(TaskItem.class);

                // make sure it is not already in the dataset
                if (!tasks.contains(item)) {
                    addTask(item);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                TaskItem remoteItem = dataSnapshot.getValue(TaskItem.class);

                // find the local item by uid
                int idx = tasks.indexOf(remoteItem);
                TaskItem localItem = tasks.get(idx);

                Log.d(TAG, String.format("Updating '%s' to '%s'", localItem.getName(), remoteItem.getName()));

                // update local from remote
                localItem.setName(remoteItem.getName());
                adapter.notifyItemChanged(idx);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                TaskItem remoteItem = dataSnapshot.getValue(TaskItem.class);
                int idx = tasks.indexOf(remoteItem);
                if (idx > -1) {
                    tasks.remove(idx);
                    adapter.notifyItemRemoved(idx);
                }
            }

            // region Unused Methods
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
            // endregion
        });
    }

    @Override
    public void onDeleteButtonClick(TaskAdapter adapter, int position) {
        TaskItem item = tasks.get(position);
        tasksRef.child(item.getUid()).removeValue();

        Toast.makeText(this, "Deleted: " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoneKeyClick(TaskAdapter adapter, int position) {
        final TaskItem item = tasks.get(position);

        String uid = item.getUid();
        if (uid == null) {
            // get unique ID from firebase db
            uid = tasksRef.push().getKey();
            item.setUid(uid);
        }

        // send data to firebase
        tasksRef.child(uid).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "Saved: " + item, Toast.LENGTH_SHORT).show();
                addEmptyTask();
            }
        });
    }

    private void addEmptyTask() {
        String newTaskDefault = getString(R.string.config_task_name);
        String hint = FirebaseRemoteConfig.getInstance().getString(newTaskDefault);
        adapter.setEditTextHint(hint);

        addTask(new TaskItem());
    }

    private void addTask(TaskItem taskItem) {
        // try to find an existing new task, then remove it
        int idx = 0;
        for (TaskItem item : tasks) {
            if (item.getUid() == null) {
                tasks.remove(idx);
                break;
            }
            ++idx;
        }

        tasks.add(taskItem);
        adapter.notifyDataSetChanged();
    }
}
