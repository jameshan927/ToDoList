package com.example.james.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.james.todolist.db.ListContract;
import com.example.james.todolist.db.ListDbHelper;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListDbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    private void updateUI() {
        ArrayList<String> listList = new ArrayList<>();

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(ListContract.ListEntry.TABLE,
                new String[]{ListContract.ListEntry._ID, ListContract.ListEntry.COL_LIST_TITLE},
                null, null, null, null, null);
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(ListContract.ListEntry.COL_LIST_TITLE);
            // Log.d("MainActivity", "List: " + cursor.getString(idx));
            listList.add(cursor.getString(idx));
        }

        if(mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.list_title,
                    listList);
            mTaskListView.setAdapter(mAdapter);
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(listList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    public void deleteList(View view) {
        View parent = (View) view.getParent();
        TextView listTextView = (TextView) parent.findViewById(R.id.list_title);
        String list = String.valueOf(listTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(ListContract.ListEntry.TABLE,
                ListContract.ListEntry.COL_LIST_TITLE + " = ?",
                new String[]{list});
        db.close();
        updateUI();

        SharedPreferences pref = getSharedPreferences(list, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence message = list + " has been removed";

        Toast delete = Toast.makeText(context, message, duration);
        delete.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHelper = new ListDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.list_todo);

        updateUI();

        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView listName = (TextView) view.findViewById(R.id.list_title);
                String name = listName.getText().toString();
                Log.d("Log", name);
                Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("listName", name);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
                final String formattedDate = date.format(calendar.getTime());

                final EditText listName = new EditText(NavigationActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(NavigationActivity.this)
                        .setTitle("Create a new ToDo List")
                        .setMessage("Name your list")
                        .setView(listName)
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = String.valueOf(listName.getText());
                                Log.d("Log", "List created " + name);

                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(ListContract.ListEntry.COL_LIST_TITLE, name);
                                db.insertWithOnConflict(ListContract.ListEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();

                                updateUI();

                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                                CharSequence message = name + " created for " + formattedDate;

                                Toast add = Toast.makeText(context, message, duration);
                                add.show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.alltasks) {
            Intent intent = new Intent(getApplicationContext(), AllTasksActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.starred) {
        } else if (id == R.id.overdue) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
