package com.example.android.bookinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.bookinventory.data.BookContract.BookEntry;

/** An API used to access the database through the BookProvider(ContentProvider)*/
public class BooksDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "BooksDbHelper";
    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;


    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE "+BookEntry.TABLE_NAME+ " ( "
                +BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                +BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL DEFAULT 0,"
                +BookEntry.COLUMN_BOOK_QUANTITY +" INTEGER NOT NULL DEFAULT 0,"
                +BookEntry.COLUMN_SUPPLIER_NAME+" TEXT NOT NULL,"
                +BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER+" TEXT NOT NULL); ";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
