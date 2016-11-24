package com.example.james.todolist.db;

/**
 * Created by james on 11/24/2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ListDbHelper extends SQLiteOpenHelper {

    public ListDbHelper(Context context) {
        super(context, ListContract.DB_NAME, null, ListContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + ListContract.ListEntry.TABLE + " ( " +
                ListContract.ListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ListContract.ListEntry.COL_LIST_TITLE + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ListContract.ListEntry.TABLE);
        onCreate(db);
    }

}
