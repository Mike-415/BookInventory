package com.example.android.bookinventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Book Inventory app.
 */
public class BookContract {
    private BookContract(){}

    /**
     * The "Content authority" is the package name for the app, which is guaranteed
     * to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path for the entire table or individual row/s in a table
     */
    public static final String PATH_PETS = "books";

    public static final class BookEntry implements BaseColumns{


        /** Name of database table for books */
        public static final String TABLE_NAME = "books";

        /**
         * Unique ID number for each book.
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the book.
         *
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NAME = "productName";

        /**
         * Price of the book.
         *
         * Type: INTEGER (cast into a 'double' later d/t varying decimal numbers)
         */
        public static final String COLUMN_PRODUCT_PRICE = "productPrice";

        /**
         * The quantity of that particular book.
         *
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_QUANTITY = "productQuantity";

        /**
         * Name of the supplier.
         *
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplierName";

        /**
         * Phone number of the supplier.
         *
         * Type: TEXT (Too large to store in an INTEGER)
         */
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplierPhoneNumber";

    }
}
