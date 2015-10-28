package com.android.diplomado.postjob;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.diplomado.postjob.Data.JobPostDbContract.JobPost;

/**
 * Created by Internet on 13/10/2015.
 */
public class CursorAdapterMine extends CursorAdapter {

    public CursorAdapterMine(Context context, Cursor c, int flags){
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.employment_item, parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTextView;
        TextView dateTextView;

        titleTextView = (TextView)view.findViewById(R.id.titleTextView);
        dateTextView = (TextView)view.findViewById(R.id.dateTextView);

        String title = cursor.getString(cursor.getColumnIndex(JobPost.TITLE_COLUMN));
        String date = cursor.getString(cursor.getColumnIndex(JobPost.POSTED_DATE_COLUMN));
        titleTextView.setText(title);
        dateTextView.setText(date);
    }
}
