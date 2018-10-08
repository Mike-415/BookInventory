package com.example.android.bookinventory.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookinventory.R;
import com.example.android.bookinventory.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {


    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView bookNameTextView = (TextView) view.findViewById(R.id.bookName);
        TextView bookPriceTextView = (TextView) view.findViewById(R.id.bookPrice);
        TextView bookQuantityTextView = (TextView) view.findViewById(R.id.bookQuantity);

        String bookName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME));
        int bookPrice = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE));
        int bookQuantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY));

        bookNameTextView.setText(bookName);
        //TODO: Cast into a real number(Look at previous work)
        bookPriceTextView.setText("$ "+bookPrice);
        bookQuantityTextView.setText(bookQuantity);

    }
}
