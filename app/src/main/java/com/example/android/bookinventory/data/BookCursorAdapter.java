package com.example.android.bookinventory.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.bookinventory.R;
import com.example.android.bookinventory.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {
    private Context mContext;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mContext = context;

        TextView bookNameTextView = (TextView) view.findViewById(R.id.bookName);
        TextView bookPriceTextView = (TextView) view.findViewById(R.id.bookPrice);
        final TextView bookQuantityTextView = (TextView) view.findViewById(R.id.bookQuantity);
        ImageButton saleButton = (ImageButton) view.findViewById(R.id.sale_button);

        final int bookId = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        String bookName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME));
        int bookPrice = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE));
        double bookPriceDouble = bookPrice * .01;
        final int bookQuantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY));

        bookNameTextView.setText(bookName);
        bookPriceTextView.setText("$ "+String.format("%.2f", bookPriceDouble));
        bookQuantityTextView.setText(Integer.toString(bookQuantity));

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer intValue = Integer.valueOf(bookQuantityTextView.getText().toString().trim());
                if(intValue > 0){
                    int listItemBookQuantity = bookQuantity;
                    listItemBookQuantity--;
                    Uri listItemBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, listItemBookQuantity);
                    mContext.getContentResolver().update(listItemBookUri, values, null, null);
                } else {
                    Toast.makeText(mContext,
                            BookError.BOOK_QUANTITY_MINIMUM_ZERO.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
