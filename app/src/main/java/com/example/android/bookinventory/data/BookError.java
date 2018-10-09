package com.example.android.bookinventory.data;

public enum BookError {
    BOOK_NAME_REQUIRED("Book name is required."),
    BOOK_PRICE_REQUIRED("Book price is required"),
    BOOK_PRICE_MINIMUM_ZERO("Book price cannot be a negative value.  Minimum is 0."),
    BOOK_QUANTITY_REQUIRED("Book quantity is required"),
    BOOK_QUANTITY_MINIMUM_ZERO("Book quantity cannot be a negative value.  Minimum is 0."),
    SUPPLIER_NAME_REQUIRED("Supplier name is required."),
    SUPPLIER_PHONE_NUMBER_REQUIRED("Supplier phone number is required."),
    SUPPLIER_PHONE_NUMBER_NOT_TEN_DIGITS("Supplier phone number must be 10 digits long, which includes both the area code and phone number"),
    SUPPLIER_PHONE_NUMBER_NEGATIVE_VALUE("The supplier phone number must be a positive number");
    private String errorMessage;
    BookError(String errorMessage){
        this.errorMessage = errorMessage;
    }
    @Override
    public String toString() {
        return errorMessage;
    }
}