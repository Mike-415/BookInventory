package com.example.android.bookinventory.data;



import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bookinventory.BookError;
import com.example.android.bookinventory.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    /** Tag for the log messages */
    private static final String TAG = "BookProvider";

    private BooksDbHelper dbHelper;

    /** URI matcher code for the content URI for the books table */
    private static final int BOOKS = 1;

    /** URI matcher code for the content URI for a single book in the books table */
    private static final int BOOK_ID = 2;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS+"/#", BOOK_ID);
    }



    @Override
    public boolean onCreate() {
        dbHelper = new BooksDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set a notification URI on the Cursor in order to
        // update the cursor if the data at this URI changes
        cursor.setNotificationUri( getContext().getContentResolver(), uri );
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a Book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        //Check all values before insertion
        validateAllInsertValues(values);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify all listeners that the data has changed for the book content URI
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        validateAllUpdateValues(values);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated = database.update(
                BookEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        if(rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }



    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted =  database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    private void validateAllInsertValues(ContentValues values) {
        String bookName = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        Integer bookPrice = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
        Integer bookQuantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        validateString(bookName, BookError.BOOK_NAME_REQUIRED.toString());
        validateInteger(bookPrice, BookError.BOOK_PRICE_MINIMUM_ZERO.toString());
        validateInteger(bookQuantity, BookError.BOOK_QUANTITY_MINIMUM_ZERO.toString());
        validateString(supplierName, BookError.SUPPLIER_NAME_REQUIRED.toString());
        validatePhoneNumber(supplierPhoneNumber);
    }

    private void validateAllUpdateValues(ContentValues values){
        if(values.containsKey(BookEntry.COLUMN_BOOK_NAME)){
            String bookName = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
            validateString(bookName, BookError.BOOK_NAME_REQUIRED.toString());
        }

        if(values.containsKey(BookEntry.COLUMN_BOOK_PRICE)){
            Integer bookPrice = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
            validateInteger(bookPrice, BookError.BOOK_PRICE_MINIMUM_ZERO.toString());
        }

        if(values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)){
            Integer bookQuantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            validateInteger(bookQuantity, BookError.BOOK_QUANTITY_MINIMUM_ZERO.toString());
        }

        if(values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)){
            String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            validateString(supplierName, BookError.SUPPLIER_NAME_REQUIRED.toString());
        }
        if(values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)){
            String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            validatePhoneNumber(supplierPhoneNumber);
        }
    }


    private void validateString(String string, String errorMessage){
        if(string == null)
            throw new IllegalArgumentException(errorMessage);
    }

    private void validateInteger(Integer integer, String errorMessage){
        if(integer < 0 && integer != null)
            throw new IllegalArgumentException(errorMessage);
    }

    private void validatePhoneNumber(String supplierPhoneNumber) {
        validateString(supplierPhoneNumber, BookError.SUPPLIER_PHONE_NUMBER_REQUIRED.toString());
        if(supplierPhoneNumber.length() != 10)
            throw new IllegalArgumentException(BookError.SUPPLIER_PHONE_NUMBER_NOT_TEN_DIGITS.toString());
        if(Long.valueOf(supplierPhoneNumber) < 0)
            throw new IllegalArgumentException(BookError.SUPPLIER_PHONE_NUMBER_NEGATIVE_VALUE.toString());
    }
}
