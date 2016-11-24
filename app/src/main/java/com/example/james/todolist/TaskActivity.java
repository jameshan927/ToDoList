package com.example.james.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.MenuItemHoverListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.james.todolist.db.ListContract;
import com.example.james.todolist.db.TaskContract;
import com.example.james.todolist.db.TaskDbHelper;
import com.example.james.todolist.MainActivity;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class TaskActivity extends AppCompatActivity {

    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    public int counter = 0;

    private void updateUI(String listName) {
        ArrayList<String> taskList = new ArrayList<>();

        if(counter != 0) {
            SharedPreferences editor = getSharedPreferences(listName, MODE_PRIVATE);
            for (int i = 1; i <= counter; i++) {
                String index = Integer.toString(i);
                String task = editor.getString(index, null);
                if (task != null) {
                    taskList.add(task);
                }
            }
        }

        if(mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.task_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

    }

    public String findKey(SharedPreferences sharedPreferences, String value) {
        TreeMap<String, ?> keys = new TreeMap<String, Object>(sharedPreferences.getAll());
        for (Map.Entry<String, ?> entry: keys.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null; // not found
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());


        Bundle bundle = getIntent().getExtras();
        String listName = bundle.getString("listName");

        SharedPreferences pref = getSharedPreferences(listName,MODE_PRIVATE);
        String key = findKey(pref, task);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();


        updateUI(listName);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence message = task + " has been removed";

        Toast delete = Toast.makeText(context, message, duration);
        delete.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.task_list);


        Bundle bundle = getIntent().getExtras();
        String listName = bundle.getString("listName");
        Log.d("listname", listName);

        updateUI(listName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_task:
                //Log.d("TaskActivity", "Add a new task");
                final EditText taskName = new EditText(TaskActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(TaskActivity.this)
                        .setTitle("Add a new task")
                        .setMessage("Describe your task")
                        .setView(taskName)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = String.valueOf(taskName.getText());
                                Log.d("MainActivity", "Task created " + name);


                                Bundle bundle = getIntent().getExtras();
                                String listName = bundle.getString("listName");

                                SharedPreferences.Editor editor = getSharedPreferences(listName,MODE_PRIVATE).edit();

                                String place = Integer.toString(++counter);
                                editor.putString(place, name);
                                editor.commit();


                                updateUI(listName);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
