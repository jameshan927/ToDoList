package com.example.james.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.james.todolist.db.ListContract;
import com.example.james.todolist.db.ListDbHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class AllTasksActivity extends AppCompatActivity {

    private ListDbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;
    ArrayList<String> listList;
    ArrayList<String> taskList;

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
        String key = "";

        for(int j = 0; j < listList.size(); j++) {
            SharedPreferences pref = getSharedPreferences(listList.get(j), MODE_PRIVATE);
            key = findKey(pref, task);
            if(key != null) {
                SharedPreferences.Editor editor = pref.edit();
                editor.remove(key);
                editor.commit();
                break;
            }
        }

        for(int j = 0; j < taskList.size(); j++) {
            if(taskList.get(j).compareTo(key) == 0) {
                taskList.remove(j);
            }
        }


        updateUI();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence message = task + " has been removed";

        Toast delete = Toast.makeText(context, message, duration);
        delete.show();
    }

    public void updateUI() {

        if(mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.task_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        }
        else {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.task_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        mHelper = new ListDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.list_todo);

        listList = new ArrayList<>();
        taskList = new ArrayList<>();

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(ListContract.ListEntry.TABLE,
                new String[]{ListContract.ListEntry._ID, ListContract.ListEntry.COL_LIST_TITLE},
                null, null, null, null, null);
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(ListContract.ListEntry.COL_LIST_TITLE);
            // Log.d("MainActivity", "List: " + cursor.getString(idx));
            listList.add(cursor.getString(idx));
        }

        for(int i = 0; i < listList.size(); i++) {
            SharedPreferences pref = getSharedPreferences(listList.get(i), MODE_PRIVATE);

            TreeMap<String, ?> keys = new TreeMap<String, Object>(pref.getAll());

            for (Map.Entry<String, ?> entry: keys.entrySet()) {
                String task = entry.getValue().toString();
                taskList.add(task);
            }
        }

        cursor.close();
        db.close();

        updateUI();

    }
}
