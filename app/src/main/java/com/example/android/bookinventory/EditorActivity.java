package com.example.android.bookinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookinventory.data.BookContract;

public class EditorActivity extends AppCompatActivity {
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
        Uri currentBookUri = intent.getData();
        if(currentBookUri == null)
            setTitle( getString(R.string.editor_activity_title_add_book) );
        else
            setTitle( getString(R.string.editor_activity_title_edit_book) );
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
                insertPet();
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
    private void insertPet() {
        String bookName = mBookName.getText().toString().trim();
        int bookPrice = Integer.parseInt(mBookPrice.getText().toString().trim());
        int bookQuantity = Integer.parseInt(mBookQuantity.getText().toString().trim());
        String supplierName = mSupplierName.toString().trim();
        //REMEMBER: supplierPhoneNumber is a String because there's too many digits
        String supplierPhoneNumber = mSupplierPhoneNumber.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, bookName);
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
