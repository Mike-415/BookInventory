package com.example.android.bookinventory;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bookinventory.data.BookContract.BookEntry;
import com.example.android.bookinventory.data.BooksDbHelper;

public class CatalogActivity extends AppCompatActivity {
    private static final String TAG = "CatalogActivity";
    private BooksDbHelper booksDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        //TODO: Use ButterKnife when finished
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this , EditorActivity.class);
                startActivity(intent);
            }
        });
        booksDbHelper = new BooksDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        insertBook();
        insertBook();
        logAllBooks();
    }


    /**
     * Reads all the rows and columns in the books database and logs them
     */
    private void logAllBooks() {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = getContentResolver().query(BookEntry.CONTENT_URI, projection, null, null, null);

        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int productNameIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int productPriceIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneNumberIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        Log.i(TAG, "logAllBooks: \n\n\n");
        try
        {
            while (cursor.moveToNext()){
                int currentId = cursor.getInt(idColumnIndex);
                String productName = cursor.getString(productNameIndex);
                int productPrice = cursor.getInt(productPriceIndex);
                int productQuantity = cursor.getInt(productQuantityIndex);
                String supplierName = cursor.getString(supplierNameIndex);
                String supplierPhoneNumber = cursor.getString(supplierPhoneNumberIndex);
                Log.i(TAG, "_id: "+currentId+", productName: "+productName+", productPrice: "+productPrice+", productQuantity: "+productQuantity+", supplierName: "+supplierName+", supplierPhoneNumber: "+supplierPhoneNumber);
            }
        }
        finally
        {
            cursor.close();
        }
    }


    /**
     * Writes a book into the books database and logs the row id number
     */
    public void insertBook(){
        //SQLiteDatabase db = booksDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //COLUMNS: _id, Product Name, Price, Quantity,
        //         Supplier Name, Supplier Phone Number
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "SomeBookTitle");
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, 1000);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Penguin");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "4155551212");


        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
        Log.i(TAG, "insertBook: new book inserted.  row ID: "+newUri.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;

            case R.id.action_delete_all_entries:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
