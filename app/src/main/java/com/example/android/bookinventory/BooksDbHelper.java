package com.example.android.bookinventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BooksDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "BooksDbHelper";
    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;


    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
