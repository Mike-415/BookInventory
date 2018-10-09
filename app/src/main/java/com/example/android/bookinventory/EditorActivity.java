package com.example.android.bookinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.android.bookinventory.data.BookContract;
import com.example.android.bookinventory.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "EditorActivity";
    private static final int EXISTING_BOOK_LOADER = 1;
    private Uri mCurrentBookUri;
    private EditText mBookName;
    private EditText mBookPrice ;
    private EditText mBookQuantity ;
    private EditText mSupplierName;
    private EditText mSupplierPhoneNumber;

    private boolean mBookHasChanged = false;

    private String errorToastMessage = null;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if(mCurrentBookUri == null){
            setTitle( getString(R.string.editor_activity_title_add_book) );
        } else{
            setTitle( getString(R.string.editor_activity_title_edit_book) );
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, EditorActivity.this);
        }

        mBookName = findViewById(R.id.bookName);
        mBookPrice = findViewById(R.id.bookPrice);
        mBookQuantity = findViewById(R.id.bookQuantity);
        mSupplierName = findViewById(R.id.supplierName);
        mSupplierPhoneNumber = findViewById(R.id.supplierPhoneNumber);

        mBookName.setOnTouchListener(mTouchListener);
        mBookPrice.setOnTouchListener(mTouchListener);
        mBookQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumber.setOnTouchListener(mTouchListener);
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
                //errorToastMessage serves as a flag.  If null, no invalid user data input
                Log.d(TAG, "onOptionsItemSelected: errorToastMessage is null");
                if(TextUtils.isEmpty(errorToastMessage)){
                    finish();
                } else {
                    Log.d(TAG, "onOptionsItemSelected: errorToastMessage: "+errorToastMessage);
                    Toast.makeText(this, errorToastMessage, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_delete:
                return true;

                // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if(!mBookHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //User clicked "Discard" button, navigate to parent activity
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // If the book hasn't changed,
        // continue with handling back button press
        if( !mBookHasChanged ){
            super.onBackPressed();
            return;
        }

        /*
        Otherwise if there are unsaved changes,
        setup a dialog to warn the user. Create
        a click listener to handle the user confirming
        that changes should be discarded
         */
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User clicked "Discard" button,
                        //close the current activity
                        finish();
                    }
                };
        //Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Keep editing" button, so
                // dismiss the dialog and continue editing the book
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Get user input from editor and save new pet into database.
     */
    private void saveBook() {
        String bookNameString = mBookName.getText().toString().trim();
        String bookPriceString = mBookPrice.getText().toString().trim();
        String bookQuantityString = mBookQuantity.getText().toString().trim();
        String supplierNameString = mSupplierName.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumber.getText().toString().trim();

        /*
        Before the information is added to the table, it must be validated -
        In particular, empty product information is not accepted.

        If user inputs invalid product information (name, price, quantity, supplier name, supplier phone number),
        instead of erroring out, the app includes logic to validate that no null values are accepted.
        If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.
         */

        //If the user pressed the add button and didn't fill out any of the EditText fields, exit this method
        Log.i(TAG, "saveBook: Before calling 'allEditTextValuesNulll' method");
        if( mCurrentBookUri == null && allEditTextValuesNull(bookNameString, bookPriceString, bookQuantityString, supplierNameString, supplierPhoneNumberString)){
            return;
        }
        Log.d(TAG, "saveBook: Before calling 'checkValidityOfAllValues' ");
        errorToastMessage = checkValidityOfAllValues(bookNameString, bookPriceString, bookQuantityString, supplierNameString, supplierPhoneNumberString);
        Log.d(TAG, "saveBook: errorToastMessage"+"__"+errorToastMessage+"__");
        if ( ! TextUtils.isEmpty(errorToastMessage)) {
            Toast.makeText(getApplicationContext(), errorToastMessage, Toast.LENGTH_LONG).show();
        } else {
            int bookPrice = Integer.valueOf(bookPriceString);
            int bookQuantity = Integer.valueOf(bookQuantityString);
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, bookNameString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, bookPrice);       //Remember, INTEGER
            values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity); //Remember, INTEGER
            values.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
            values.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

            // Show a toast message depending on whether or not the insertion or update was successful
            toastUpdateOrInsertionResults(values);
        }
    }

    private String checkValidityOfAllValues(String bookNameString, String bookPriceString, String bookQuantityString, String supplierNameString, String supplierPhoneNumberString) {
        //TODO: Make sure you have all error type in the BookError enum
        //TODO: See if you can get an EditText without a decimal point to avoid that problem
        //TODO: Max value of quantity is 9999, maxLength = 4
        //TODO: Max value of price is 999.99, maxLength = 5

        //TODO: Change the phone number to number, not phone
        //Solution : android:inputType="number|none"
        Log.i(TAG, "checkValidityOfAllValues: bookNameString: "+bookNameString+", bookPriceString: "+bookPriceString+", bookQuantityString: "+bookQuantityString+", supplierNameString: _"+supplierNameString+"_ , supplierPhoneNumberString: "+supplierPhoneNumberString);
        StringBuilder builder = new StringBuilder();
        //First check ALL EditText values are not null or empty
        if(TextUtils.isEmpty(bookNameString)){
            builder.append(BookError.BOOK_NAME_REQUIRED.toString()+"\n\n");
        }

        if(TextUtils.isEmpty(bookPriceString)){
            builder.append(BookError.BOOK_PRICE_REQUIRED.toString()+"\n\n");
        }

        if(TextUtils.isEmpty(bookQuantityString)){
            builder.append(BookError.BOOK_QUANTITY_REQUIRED.toString()+"\n\n");
        }

        if(TextUtils.isEmpty(supplierNameString)){
            builder.append(BookError.SUPPLIER_NAME_REQUIRED.toString()+"\n\n");
        }

        if(TextUtils.isEmpty(supplierPhoneNumberString)){
            builder.append(BookError.SUPPLIER_PHONE_NUMBER_REQUIRED.toString()+"\n\n");
        } else {
            // Check phone number length
            if(supplierPhoneNumberString.length() != 10){
                builder.append(BookError.SUPPLIER_PHONE_NUMBER_NOT_TEN_DIGITS.toString()+"\n\n");
            }
        }

        // Check all numeric values are greater than 0
        if(!TextUtils.isEmpty(bookPriceString)){
            if(Integer.valueOf(bookPriceString) < 0){
                builder.append(BookError.BOOK_PRICE_MINIMUM_ZERO.toString()+"\n\n");
            }
        }
        if(! TextUtils.isEmpty(bookQuantityString)){
            if(Integer.valueOf(bookQuantityString) < 0){
                builder.append(BookError.BOOK_QUANTITY_MINIMUM_ZERO.toString()+"\n\n");
            }
        }
        if(! TextUtils.isEmpty(supplierPhoneNumberString)){
            if(Long.valueOf(supplierPhoneNumberString) < 0 ){
                builder.append(BookError.SUPPLIER_PHONE_NUMBER_NEGATIVE_VALUE.toString()+"\n\n");
            }
        }
        return builder.toString();
    }

    private boolean allEditTextValuesNull(String bookNameString, String bookPriceString, String bookQuantityString, String supplierNameString, String supplierPhoneNumberString) {
        return TextUtils.isEmpty(bookNameString) &&
                TextUtils.isEmpty(bookPriceString) &&
                TextUtils.isEmpty(bookQuantityString) &&
                TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneNumberString);
    }





    private void toastUpdateOrInsertionResults(ContentValues values) {
        if(mCurrentBookUri == null){
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null){
                String insertFailed = getString(R.string.editor_insert_book_failed);
                Log.i(TAG, "toastUpdateOrInsertionResults: book insertion failed. String: "+insertFailed);
                Toast.makeText(this, insertFailed,
                        Toast.LENGTH_SHORT).show();
            } else {
                String insertSuccessful = getString(R.string.editor_insert_book_successful);
                Log.i(TAG, "toastUpdateOrInsertionResults: book insertion successful String: "+insertSuccessful);
                Toast.makeText(this, insertSuccessful ,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri,
                    values,
                    null,
                    null);
            if(rowsAffected == 0){
                String updateFailed = getString(R.string.editor_update_book_failed);
                Log.i(TAG, "toastUpdateOrInsertionResults: book update failed.  String: "+updateFailed);
                Toast.makeText(this, updateFailed ,
                        Toast.LENGTH_SHORT).show();
            } else {
                String updateSuccessful = getString(R.string.editor_update_book_successful);
                Log.i(TAG, "toastUpdateOrInsertionResults: book update successful. String: "+updateSuccessful);
                Toast.makeText(this, updateSuccessful ,
                        Toast.LENGTH_SHORT).show();
            }

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
