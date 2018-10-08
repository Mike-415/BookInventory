package com.example.android.bookinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.example.android.bookinventory.data.BookContract;
import com.example.android.bookinventory.data.BookContract.BookEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_BOOK_LOADER = 1;
    private Uri mCurrentBookUri;
    private EditText mBookName;
    private EditText mBookPrice ;
    private EditText mBookQuantity ;
    private EditText mSupplierName;
    private EditText mSupplierPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mBookName = (EditText) findViewById(R.id.bookName);
        mBookPrice = (EditText) findViewById(R.id.bookPrice);
        mBookQuantity = (EditText) findViewById(R.id.bookQuantity);
        mSupplierName = (EditText) findViewById(R.id.supplierName);
        mSupplierPhoneNumber = (EditText) findViewById(R.id.supplierPhoneNumber);
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if(mCurrentBookUri == null){
            setTitle( getString(R.string.editor_activity_title_add_book) );
        } else{
            setTitle( getString(R.string.editor_activity_title_edit_book) );
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, EditorActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                return true;
            case R.id.action_delete:
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
//            case android.R.id.home:
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void saveBook() {
        String bookName = mBookName.getText().toString().trim();
        int bookPrice = Integer.parseInt(mBookPrice.getText().toString().trim());
        int bookQuantity = Integer.parseInt(mBookQuantity.getText().toString().trim());
        String supplierName = mSupplierName.toString().trim();
        //TODO: IMPORTANT You need to create  a dataValidation method for all these values before insertion
        //REMEMBER: supplierPhoneNumber is a String because there's too many digits
        String supplierPhoneNumber = mSupplierPhoneNumber.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, bookName);
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        // Show a toast message depending on whether or not the insertion or update was successful
        toastUpdateOrInsertionResults(values);
    }

    private void toastUpdateOrInsertionResults(ContentValues values) {
        if(mCurrentBookUri == null){
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null)
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri,
                    values,
                    null,
                    null);
            if(rowsAffected == 0)
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        if(mCurrentBookUri == null)
            return null;
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount() < 1 )
            return;
        if(cursor.moveToFirst()){
            updateAllEditTextWithValues(cursor);
        }
    }

    private void updateAllEditTextWithValues(Cursor cursor) {
        //Get the column index for all columns
        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int bookQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneNumberIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        //Extract value from cursor using the column index
        String bookName = cursor.getString(bookNameColumnIndex);
        int bookPrice = cursor.getInt(bookPriceColumnIndex);
        int bookQuantity = cursor.getInt(bookQuantityColumnIndex);
        String supplierName = cursor.getString(supplierNameColumnIndex);
        String supplierPhoneNumber = cursor.getString(supplierPhoneNumberIndex);
        //Update the EditText with values
        mBookName.setText(bookName);
        mBookPrice.setText(Integer.toString(bookPrice));
        mBookQuantity.setText(Integer.toString(bookQuantity));
        mSupplierName.setText(supplierName);
        mSupplierPhoneNumber.setText(supplierPhoneNumber);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
