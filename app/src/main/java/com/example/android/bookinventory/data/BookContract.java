package com.example.android.bookinventory.data;

import android.provider.BaseColumns;

/**
 * API Contract for the Book Inventory app.
 */
public class BookContract {
    private BookContract(){}

    public static final class BookEntry implements BaseColumns{
        //COLUMNS _id, Product Name, Price, Quantity,
        //        Supplier Name, Supplier Phone Number

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
