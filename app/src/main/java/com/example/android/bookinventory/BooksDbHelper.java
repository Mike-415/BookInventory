package com.example.android.bookinventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.bookinventory.BookContract.BookEntry;

public class BooksDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "BooksDbHelper";
    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;


    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //COLUMNS: _id, Product Name, Price, Quantity,
        //         Supplier Name, Supplier Phone Number
        //TODO: Check if supplier name should be an optional or mandatory value
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE "+BookEntry.TABLE_NAME+ " ( "
                +BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                +BookEntry.COLUMN_PRODUCT_PRICE+ " INTEGER NOT NULL DEFAULT 0,"
                +BookEntry.COLUMN_PRODUCT_QUANTITY+" INTEGER NOT NULL DEFAULT 0,"
                +BookEntry.COLUMN_SUPPLIER_NAME+" TEXT,"
                +BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER+" TEXT NOT NULL); ";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
