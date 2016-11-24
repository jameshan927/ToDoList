package com.example.james.todolist.db;

/**
 * Created by james on 11/24/2016.
 */

import android.provider.BaseColumns;

public class ListContract {
    public static final String DB_NAME = "com.example.james.todolist.db.lists";
    public static final int DB_VERSION = 1;

    public class ListEntry implements BaseColumns {
        public static final String TABLE = "lists";

        public static final String COL_LIST_TITLE = "title";
    }
}
