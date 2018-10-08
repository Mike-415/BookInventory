package com.example.android.bookinventory;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.bookinventory.data.BookContract.BookEntry;
import com.example.android.bookinventory.data.BookCursorAdapter;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "CatalogActivity";
    private static final int BOOK_LOADER_ID = 1;
    private BookCursorAdapter bookCursorAdapter;

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

        ListView bookListView = (ListView) findViewById(R.id.listView);
        View emptyView = findViewById(R.id.emptyView);
        bookListView.setEmptyView(emptyView);

        bookCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(bookCursorAdapter);

        getSupportLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Writes a book into the books database and logs the row id number
     */
    public void insertBook(){
        ContentValues values = new ContentValues();
        //COLUMNS: _id, Product Name, Price, Quantity,
        //         Supplier Name, Supplier Phone Number
        values.put(BookEntry.COLUMN_BOOK_NAME, "SomeBookTitle");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 1000);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY
        };

        return new CursorLoader(
                this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        bookCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bookCursorAdapter.swapCursor(null);
    }
}
