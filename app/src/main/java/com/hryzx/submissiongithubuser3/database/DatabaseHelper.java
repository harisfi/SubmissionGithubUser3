package com.hryzx.submissiongithubuser3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hryzx.submissiongithubuser3.database.UserContract.UserColumns;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_USER = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s TEXT)",
            UserColumns.TABLE_NAME,
            UserColumns.ID,
            UserColumns.URL,
            UserColumns.NAME,
            UserColumns.USERNAME,
            UserColumns.DESCRIPTION,
            UserColumns.LOCATION,
            UserColumns.PHOTO,
            UserColumns.FOLLOWING,
            UserColumns.FOLLOWERS,
            UserColumns.FOLLOWERS_URL,
            UserColumns.FOLLOWING_URL,
            UserColumns.REPOS,
            UserColumns.COMPANY,
            UserColumns.BLOG,
            UserColumns.EMAIL
    );
    public static String DATABASE_NAME = "dbgithubusers";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserColumns.TABLE_NAME);
        onCreate(db);
    }
}
