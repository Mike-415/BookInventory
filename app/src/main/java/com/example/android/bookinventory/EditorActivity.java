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
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.android.bookinventory.data.BookContract;
import com.example.android.bookinventory.data.BookContract.BookEntry;
import com.example.android.bookinventory.data.BookError;

import faranjit.currency.edittext.CurrencyEditText;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "EditorActivity";
    private static final int EXISTING_BOOK_LOADER = 1;
    private static final int MAX_QUANTITY = 9999;
    private static final int MIN_QUANTITY = 0;
    private Uri mCurrentBookUri;
    private EditText mBookName;
    private CurrencyEditText mBookPrice;
    private EditText mBookQuantity ;
    private EditText mSupplierName;
    private EditText mSupplierPhoneNumber;
    private ImageButton mPhoneButton;
    private ImageButton mDecreaseQuantityButton;
    private ImageButton mIncreaseQuantityButton;
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
            //This method makes the delete menu option disappear, since
            //Book never existed in the first place
            invalidateOptionsMenu();
        } else{
            setTitle( getString(R.string.editor_activity_title_edit_book) );
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, EditorActivity.this);
        }

        initializeAllEditText();
        initializeAllImageButtons();
    }

    /**
     * Binds all the ImageButtons to their respective UI element
     * and sets their onClickListeners
     */
    private void initializeAllImageButtons() {
        mPhoneButton = findViewById(R.id.phone_button);
        mIncreaseQuantityButton = findViewById(R.id.increase_quantity_button);
        mDecreaseQuantityButton = findViewById(R.id.decrease_quantity_button);
        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSupplierPhoneNumber.length() == 10){
                    String phoneNumber = mSupplierPhoneNumber.getText().toString().trim();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+phoneNumber));
                    startActivity(intent);
                } else {
                    Toast.makeText(EditorActivity.this,
                            BookError.SUPPLIER_PHONE_NUMBER_NOT_TEN_DIGITS.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mIncreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer value = Integer.valueOf(mBookQuantity.getText().toString().trim());
                if(value < MAX_QUANTITY){
                    String incrementedValue = String.valueOf(++value);
                    mBookQuantity.setText(incrementedValue);
                } else {
                    Toast.makeText(EditorActivity.this,
                            getString(R.string.editor_quantity_too_high), Toast.LENGTH_SHORT).show();
                    String maxValue = String.valueOf(MAX_QUANTITY);
                    mBookQuantity.setText(maxValue);
                }
            }
        });
        mDecreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer value = Integer.valueOf(mBookQuantity.getText().toString().trim());
                if(value > MIN_QUANTITY){
                    String decrementedValue = String.valueOf(--value);
                    mBookQuantity.setText(decrementedValue);
                } else {
                    Toast.makeText(EditorActivity.this,
                            getString(R.string.editor_quantity_too_low), Toast.LENGTH_SHORT).show();
                    String minValue = String.valueOf(MIN_QUANTITY);
                    mBookQuantity.setText(minValue);
                }
            }
        });
    }

    /**
     * Binds all the EditTexts to their respective UI element
     * and set their onTouchListeners
     */
    private void initializeAllEditText() {
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

        if(mCurrentBookUri == null){
            mBookQuantity.setText("1");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after calling 'invalidateOptionsMenu( )'
     * In order to make the save menu button visible
     * and the delete menu button invisible
     */

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //If we're inserting a new Book, hide the delete menu button
        if(mCurrentBookUri == null){
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            deleteMenuItem.setVisible(false);
        }
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
                showDeleteConfirmationDialog();
                return true;
                /** Respond to a click on the "Up" arrow button in the app bar */
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

    /**
     * Builds and displays a deletion confirmation alert dialog prior to the actual deletion
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Deletes the current book if it exist in the database
     */
    private void deleteBook() {
        // Only perform if the delete if this is an existing pet
        if(mCurrentBookUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if(rowsDeleted == 0){
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
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
        //Remove periods and comma's before dataValidation
        String bookPriceString = removeNonNumericChars(mBookPrice.getText().toString().trim());
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

    /**
     * Remove all non numeric characters
     */
    private String removeNonNumericChars(String bookPriceString) {
        return bookPriceString.replaceAll("[^0-9]", "");
    }

    /**
     * Checks the validity of all user's input prior to saving into the database
     */
    private String checkValidityOfAllValues(String bookNameString, String bookPriceString, String bookQuantityString, String supplierNameString, String supplierPhoneNumberString) {

        StringBuilder builder = new StringBuilder();
        //First, check ALL EditText values are not null or empty
        if(TextUtils.isEmpty(bookNameString)){
            builder.append("\n"+BookError.BOOK_NAME_REQUIRED.toString()+"\n");
        }

        if(TextUtils.isEmpty(bookPriceString)){
            builder.append("\n"+BookError.BOOK_PRICE_REQUIRED.toString()+"\n");
        }

        if(TextUtils.isEmpty(bookQuantityString)){
            builder.append("\n"+BookError.BOOK_QUANTITY_REQUIRED.toString()+"\n");
        }

        if(TextUtils.isEmpty(supplierNameString)){
            builder.append("\n"+BookError.SUPPLIER_NAME_REQUIRED.toString()+"\n");
        }

        if(TextUtils.isEmpty(supplierPhoneNumberString)){
            builder.append("\n"+BookError.SUPPLIER_PHONE_NUMBER_REQUIRED.toString()+"\n");
        } else {
            // Check phone number length
            if(supplierPhoneNumberString.length() != 10){
                builder.append("\n"+BookError.SUPPLIER_PHONE_NUMBER_NOT_TEN_DIGITS.toString()+"\n");
            }
        }

        // Check all numeric values are greater than 0
        if(!TextUtils.isEmpty(bookPriceString)){
            if(Integer.valueOf(bookPriceString) < 0){
                builder.append("\n"+BookError.BOOK_PRICE_MINIMUM_ZERO.toString()+"\n");
            }
        }
        if(! TextUtils.isEmpty(bookQuantityString)){
            if(Integer.valueOf(bookQuantityString) < 0){
                builder.append("\n"+BookError.BOOK_QUANTITY_MINIMUM_ZERO.toString()+"\n");
            }
        }
        if(! TextUtils.isEmpty(supplierPhoneNumberString)){
            if(Long.valueOf(supplierPhoneNumberString) < 0 ){
                builder.append("\n"+BookError.SUPPLIER_PHONE_NUMBER_NEGATIVE_VALUE.toString()+"\n");
            }
        }
        return builder.toString();
    }

    /**
     * Checks if all user's input is null
     */
    private boolean allEditTextValuesNull(String bookNameString, String bookPriceString, String bookQuantityString, String supplierNameString, String supplierPhoneNumberString) {
        return TextUtils.isEmpty(bookNameString) &&
                TextUtils.isEmpty(bookPriceString) &&
                TextUtils.isEmpty(bookQuantityString) &&
                TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneNumberString);
    }


    /**
     * Displays Toasts depending on whether or not the update or insertion
     * into the database was successful
     */
    private void toastUpdateOrInsertionResults(ContentValues values) {
        if(mCurrentBookUri == null){
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null){
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_book_successful) ,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri,
                    values,
                    null,
                    null);
            if(rowsAffected == 0){
                Toast.makeText(this, getString(R.string.editor_update_book_failed) ,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_book_successful) ,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        if(mCurrentBookUri == null){
            return null;
        }
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

    /**
     * Sets the text inside all the EditText for updating
     */
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
